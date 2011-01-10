package costax4.behaviors;

import costax4.*;

import battlecode.common.*;

public class MarineBehavior extends Behavior
{
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	WeaponController gun;
	
	int guns;
	int staleness = 0;
	
	boolean hasSensor;
	boolean hasArmor;
	boolean justTurned;
    boolean killAllRobots=false;
    boolean shouldMove=true;
	boolean eeHanTiming = false;
	
	int rebroadcastCounter = 0;
	int spawn = -1;
	MapLocation hometown;
	MapLocation enemyLocation;
	MapLocation chasingEnemyLoc;
	
	int travelTime;
	static double damageDealt=0;
	static double minHealth=100; //some large amount of health not possible to attain in the game
	static double secondMinHealth=100; //some large amount of health not possible to attain in the game
	static RobotInfo rInfo;
	static RobotInfo minRobot;
	static RobotInfo secondMinRobot;
	
	private static boolean seeEnemyRobot;
	
	public MarineBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		
		switch (obj)
		{
			case EQUIPPING:
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
	            guns = 0;
	            hasSensor = false;
	            hasArmor = false;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if (c.type()==Constants.GUNTYPE)
					{
						guns++;
						if (!myPlayer.myWeapons.contains((WeaponController)c))
							myPlayer.myWeapons.add((WeaponController)c);
					}
					if (c.type()==Constants.SENSORTYPE)
					{
						hasSensor = true;
						myPlayer.mySensor = (SensorController)c;
					}
					if (c.type()==Constants.ARMORTYPE)
						hasArmor = true;
				}
				if (guns >= Constants.GUNS && hasSensor && hasArmor)
					obj = MarineBuildOrder.FIND_ENEMY;
				return;
			case FIND_ENEMY: //keep moving*///////////////////////////////////989  till we find an enemy
				
				robotNavigation.bounceNavNoLoops(myPlayer);
				priorityAttack();
				if (seeEnemyRobot && !killAllRobots) { //some robot is still alive!
					if (damageDealt>minHealth && secondMinRobot!=null) { //we killed the first priority, but not the second
						chasingEnemyLoc=secondMinRobot.location;
					}
					else { //we didn't even kill the first target
						chasingEnemyLoc=minRobot.location;
					}
					obj=MarineBuildOrder.CHASE_ENEMY;
				}
				else if (Clock.getRoundNum() > Constants.LATE_GAME + 9999 && Utility.senseDebris(myPlayer) != null) // remove 9999 to kill rocks
					return;
				else {
					if (eeHanTiming && Clock.getRoundNum() > Constants.MID_GAME && travelTime < Constants.TRAVEL_TIME) {
						travelTime++;
						Utility.navStep(myPlayer, robotNavigation, enemyLocation);
					}
					else {
						robotNavigation.bounceNavNoLoops(myPlayer);
					}
				}
				return;	
			case CHASE_ENEMY: //we've found an enemy and haven't killed it, lets go after it
				if (shouldMove) {
					if (!myPlayer.myMotor.isActive()) {
						if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
							myPlayer.myMotor.moveForward();
							shouldMove=false;
						}
					}
				}
				else {
					Direction enemyDirection = robotNavigation.bugTo(chasingEnemyLoc);
					if (!myPlayer.myMotor.isActive()) {
						myPlayer.myMotor.setDirection(enemyDirection);
						shouldMove=true;
					}
					
				}
				priorityAttack();
				if (killAllRobots) {
					obj=MarineBuildOrder.FIND_ENEMY;
				}
				else if (!seeEnemyRobot) { 
					staleness=staleness+1;
					if (staleness>Constants.OLDNEWS); {
						obj=MarineBuildOrder.FIND_ENEMY;
					}
				}
				else {
					if (damageDealt>minHealth && secondMinRobot!=null) { //we killed the first priority, but not the second
						chasingEnemyLoc=secondMinRobot.location;
					}
					else { //we didn't even kill the first target
						chasingEnemyLoc=minRobot.location;
					}
					obj=MarineBuildOrder.CHASE_ENEMY;
				}
		}
	}
	
	
	
	public String toString()
	{
		return "MarineBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		if (t == MsgType.MSG_MOVE_OUT)
		{
			myPlayer.myRC.setIndicatorString(2, "We spawned " + Utility.spawnString(spawn) + ".");
			eeHanTiming = true;
			spawn = msg.ints[Messenger.firstData];
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
		}
	}
	
	public void priorityAttack() {
		
		
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
		//find enemies by lowest hit point priority, but don't overkill
		//specifically made for a 2 gun robot (finds the two highest priority targets, if the first one is killable
		//by one shot, have our second weapon shoot the other target.
		
		minHealth=1000.0; //some large amount of health not possible to attain in the game
		secondMinHealth=1000.0; //some large amount of health not possible to attain in the game
		seeEnemyRobot=false;
		minRobot = null;
		secondMinRobot = null;
		for (Robot robot : nearbyRobots) {
			if (robot.getTeam().equals(myPlayer.myRC.getTeam().opponent())) {
				seeEnemyRobot=true;
				try {
					RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(robot);
					if (rInfo.hitpoints<minHealth) {
						secondMinHealth=minHealth;
						minHealth=rInfo.hitpoints;
						secondMinRobot=minRobot;
						minRobot=rInfo;
					}
					else if (rInfo.hitpoints<secondMinHealth) {
						secondMinHealth=rInfo.hitpoints;
						secondMinRobot=rInfo;
					}
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		}
		
		// attempt to not overkill targets by seeing how much damage we have done, if
		// we have killed all robots, then we just go on bouncing around, otherwise,
		// we chase after it.
		killAllRobots=false;
		damageDealt=0;
		if (seeEnemyRobot) {
			for (WeaponController weapon :myPlayer.myWeapons) {
				if(!weapon.isActive()) {
					if (damageDealt<minHealth) { //our top priority is still alive, better try and kill it
						damageDealt=damageDealt+weapon.type().attackPower;
						try {
							weapon.attackSquare(minRobot.location, minRobot.robot.getRobotLevel());
						} catch (GameActionException e) {
							e.printStackTrace();
						}
				}	
					else {
						if (secondMinRobot!=null) {
							damageDealt=damageDealt+weapon.type().attackPower;
							try {
								weapon.attackSquare(secondMinRobot.location, secondMinRobot.robot.getRobotLevel());
								if (secondMinHealth<weapon.type().attackPower) {
									killAllRobots=true;
								}
							} catch (GameActionException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
}
