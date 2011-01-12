package masteryone.behaviors;

import masteryone.*;
import battlecode.common.*;

public class MarineBehavior extends Behavior
{
	
	MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	WeaponController gun;
	
	int guns;
	
	boolean hasBlaster;
	boolean hasRadar;
	boolean hasShield;
	boolean engagedInCombat;
	
	
	private final OldNavigation myNav;
	
	public MarineBehavior(RobotPlayer player)
	{
		super(player);
		overrideScanner = true;	//disable the scanner subsystem
		
		myNav = new OldNavigation(player); //Instantiate an old navigation system for now.
	}

	
	
	
	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			//Fully Equip the Marine
			case EQUIPPING:
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
	            hasBlaster = false;
	            hasRadar = false;
	            hasShield = false;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if ( c.type() == ComponentType.BLASTER )
						hasBlaster = true;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
					if ( c.type() == ComponentType.SHIELD )
						hasShield = true;
				}
				if ( hasBlaster && hasRadar && hasShield )
					obj = MarineBuildOrder.MOVE_OUT;
				return;
	        	
				
			//Running the main loop
			case MOVE_OUT:	
				
				
				
				//Get mylocation
				MapLocation currLoc = myPlayer.myRC.getLocation();
				
				
				//Scan to find closest enemy robot
				Robot closestRobot = null;
				RobotInfo closestRobotInfo = null;
				Direction closestRobotDirection = null;
				int closestRobotDistance = 999;
				
				Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
				for(Robot r:nearbyRobots) {
					if(r.getTeam()==myPlayer.myRC.getTeam().opponent()) {
						RobotInfo rinfo = myPlayer.mySensor.senseRobotInfo(r);
						int robotDistance = currLoc.distanceSquaredTo(rinfo.location);
						if(robotDistance<closestRobotDistance) {
							closestRobot = r;
							closestRobotInfo = rinfo;
							closestRobotDistance = robotDistance;
							closestRobotDirection = currLoc.directionTo(closestRobotInfo.location);
						}
						
					}
				}
				
				if(closestRobot!=null) {	//I AM ENGAGED IN BLOODY COMBAT
					
					Utility.setIndicator(myPlayer, 1, "Attack!");
					
					//Now that i have the closet, shoot at it.
					if(closestRobotDistance <16) {
							for(WeaponController w:myPlayer.myWeapons) {
								if(!w.isActive() && w.withinRange(closestRobotInfo.location)) {	//FIXME: Overkill if using more than one weapon
									w.attackSquare(closestRobotInfo.location, closestRobot.getRobotLevel());
								}
							}
					}
					
					
					//if I'm too closet to enemy units, move back
					if(closestRobotDistance==16) {
						return;  //I'm good					
					} else if(closestRobotDistance<16) {					//I'm too close!
						myPlayer.myActions.backUpInDir(closestRobotDirection.opposite());
					} else { //I'm too far
						myPlayer.myActions.moveInDir(myNav.bugTo(closestRobotInfo.location));
					}
					return;
					
				} else{														//I am not engaged in bloody combat!
						Utility.setIndicator(myPlayer, 1, "Bounce!");
		        		Utility.bounceNav(myPlayer);
		        	return;
				}
	        	
		}
	}

	Direction lastHeading;
	public boolean moveLikeMJ(Direction faceDir, Direction moveDir) {
		try {
			RobotController tMyRC = myPlayer.myRC;	//fast access variable
			MovementController tMyMot = myPlayer.myMotor;
			if (!tMyMot.isActive() && moveDir.ordinal()<8) {	//can we move

				//first compute whether moveDir is "behind" faceDir
				Direction oppDir = faceDir.opposite();			//TODO inline these variables
				Direction moveDirR = moveDir.rotateRight();
				Direction moveDirL = moveDir.rotateLeft();

				if(oppDir==moveDir || oppDir==moveDirR || oppDir==moveDirL) { 
					if (tMyRC.getDirection().opposite().equals(moveDir)) { //backwards movement code
						if (tMyMot.canMove(moveDir)) {
							tMyMot.moveBackward();
							return true;
						}
					} else {
						tMyMot.setDirection(moveDir.opposite());
						lastHeading = moveDir;
					}					
				} else {//moveDir is in front of faceDir
					if (tMyRC.getDirection().equals(moveDir)) {
						if (tMyMot.canMove(moveDir)) {
							tMyMot.moveForward();
							return true;
						}
					} else {
						tMyMot.setDirection(moveDir);
						lastHeading = moveDir;
					}	
				}
			}
		} catch(GameActionException e) {
//			System.out.println("Action Exception: moveLikeMJ");
			e.printStackTrace();
		}
		return false;			
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
		
	}
	public void onWakeupCallback(int lastActiveRound) {}
	public void onDamageCallback(double damageTaken) {}
}
