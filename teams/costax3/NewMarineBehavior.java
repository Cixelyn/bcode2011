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
	
	//double lastHP = myPlayer.myRC.get;
	
	boolean hasSensor;
    boolean hasArmor;
	boolean eeHanTiming = false;
    boolean moveOut = false;
    boolean enemyFound;
    
    Robot[] nearbyRobots;
    RobotInfo rInfo;
    
    ArrayList<?>[] componentList;
    
    Message[] msgs;
    
    String spawn;
	
	public NewMarineBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception {
		
		
		switch (obj) {
			case EQUIPPING:
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
				Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
				
				//find enemies by lowest hit point priority, but don't overkill
				//specifically made for a 2 gun robot (finds the two highest priority targets, if the first one is killable
				//by one shot, have our other weapon shoot the other target.
				
				double minHealth=100; //some large amount of health not possible to attain in the game
				double secondMinHealth=100; //some large amount of health not possible to attain in the game
				RobotInfo rInfo;
				RobotInfo minRobot = null;
				RobotInfo secondMinRobot = null;
				for (Robot robot : nearbyRobots) {
					if (robot.getTeam()!=myPlayer.myRC.getTeam()) {
						rInfo = myPlayer.mySensor.senseRobotInfo(robot);
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
				
				//attempt to not overkill targets by seeing how much damage we have done
				double damageDealt=0;
				if (minRobot!=null) {
					for (WeaponController weapon :myPlayer.myWeapons) {
						if(!weapon.isActive()) {
							if (damageDealt<minHealth) { //our top priority is still alive, better try and kill it
								damageDealt=damageDealt+weapon.type().attackPower;
								weapon.attackSquare(minRobot.location, minRobot.robot.getRobotLevel());
						}	
							else {
								if (secondMinRobot!=null) {
									damageDealt=damageDealt+weapon.type().attackPower;
									weapon.attackSquare(secondMinRobot.location, secondMinRobot.robot.getRobotLevel());
								}
							}
						}
					}
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
}