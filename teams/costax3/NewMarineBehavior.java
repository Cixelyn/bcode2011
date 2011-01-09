package costax3;

import battlecode.common.*;

import java.util.ArrayList;

public class NewMarineBehavior extends Behavior {
	
	WeaponController gun;
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation hometown;
	MapLocation enemyLocation;
	MapLocation currDestination;
	MapLocation newDestination;
	MapLocation mainDestination;
	
	NewMarineBuildOrder obj = NewMarineBuildOrder.EQUIPPING;
	
	Direction direction;
	
	int staleness = 0;
	int guns;
	int dizziness = 0;
	double damageDealt=0;
	
	//double lastHP = myPlayer.myRC.get;
	
	boolean hasSensor;
    boolean hasArmor;
	boolean eeHanTiming = false;
    boolean moveOut = false;
    boolean enemyFound;
    boolean killAllRobots=false;
    boolean shouldMove=true;
    
	double minHealth=100; //some large amount of health not possible to attain in the game
	double secondMinHealth=100; //some large amount of health not possible to attain in the game
	RobotInfo rInfo;
	RobotInfo minRobot;
	RobotInfo secondMinRobot;
    
    Robot[] nearbyRobots;
    
    ArrayList<?>[] componentList;
    
    Message[] msgs;
    
    MapLocation chasingEnemyLoc;
    
    String spawn;

	private boolean seeEnemyRobot;
	
	public NewMarineBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception {
		
		
		switch (obj) {
			case EQUIPPING:
				// if I have all the equipment, it's time to go go go.
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
				Utility.spin(myPlayer);
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
					{
						hasArmor = true;
					}
				}
				if (guns >= Constants.GUNS && hasSensor && hasArmor)
					obj = NewMarineBuildOrder.FIND_ENEMY;
				return;
				
			case FIND_ENEMY:
				
				//bounce movement
				if (!myPlayer.myMotor.isActive()) {
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
						myPlayer.myMotor.moveForward();
					}
					else {
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					}
				}
				priorityAttack();
				if (!killAllRobots) { //some robot is still alive!
					if (damageDealt>minHealth) { //we killed the first priority, but not the second
						chasingEnemyLoc=secondMinRobot.location;
					}
					else { //we didn't even kill the first target
						chasingEnemyLoc=minRobot.location;
					}
					obj=NewMarineBuildOrder.CHASE_ENEMY;
				}
			case CHASE_ENEMY:
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
					myPlayer.myMotor.setDirection(enemyDirection);
					shouldMove=true;
					
				}
				priorityAttack();
				if (killAllRobots) {
					obj=NewMarineBuildOrder.FIND_ENEMY;
				}
				else if (!seeEnemyRobot) { 
					staleness=staleness+1;
					if (staleness>Constants.OLDNEWS); {
						obj=NewMarineBuildOrder.FIND_ENEMY;
					}
				}
				else {
					if (damageDealt>minHealth) { //we killed the first priority, but not the second
						chasingEnemyLoc=secondMinRobot.location;
					}
					else { //we didn't even kill the first target
						chasingEnemyLoc=minRobot.location;
					}
					obj=NewMarineBuildOrder.CHASE_ENEMY;
				}
		}
	}
	
	
	
	public String toString() {
		return "NewMarineBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_MOVE_OUT)
		{
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
			currDestination = enemyLocation;
			mainDestination = enemyLocation;
			eeHanTiming = true;
		}
		
	}
	
	public void priorityAttack() {
		
		
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
		//find enemies by lowest hit point priority, but don't overkill
		//specifically made for a 2 gun robot (finds the two highest priority targets, if the first one is killable
		//by one shot, have our second weapon shoot the other target.
		
		minHealth=100; //some large amount of health not possible to attain in the game
		secondMinHealth=100; //some large amount of health not possible to attain in the game
		seeEnemyRobot=false;
		RobotInfo rInfo =null;
		RobotInfo minRobot = null;
		RobotInfo secondMinRobot = null;
		for (Robot robot : nearbyRobots) {
			if (robot.getTeam()!=myPlayer.myRC.getTeam()) {
				seeEnemyRobot=true;
				try {
					rInfo = myPlayer.mySensor.senseRobotInfo(robot);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
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
							if (secondMinHealth<weapon.type().attackPower) {
								killAllRobots=true;
							}
							try {
								weapon.attackSquare(secondMinRobot.location, secondMinRobot.robot.getRobotLevel());
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