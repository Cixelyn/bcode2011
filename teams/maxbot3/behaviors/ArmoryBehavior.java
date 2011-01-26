package maxbot3.behaviors;

import battlecode.common.*;
import maxbot3.*;

public class ArmoryBehavior extends Behavior
{
	
	MapLocation towerLoc;
	
	int wakeTime = 0;
	
	boolean sleepTime;
	
	private enum ArmoryBuildOrder 
	{
		FIND_TOWER,
		EQUIP_TOWER,
		SLEEP,
		BUILD_FLYER,
		SUICIDE
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.FIND_TOWER;

	public ArmoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
    			
			case FIND_TOWER:
				
				Utility.setIndicator(myPlayer, 0, "FIND_TOWER");
				Utility.setIndicator(myPlayer, 1, "Looking for tower...");
				
				sleepTime = true;
				for ( int i = Direction.values().length; --i >= 0 ; )
				{
					Direction d = Direction.values()[i];
					
					Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(d), RobotLevel.ON_GROUND);
					if ( r != null )
					{
						RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(r);
						if ( rInfo.chassis == Chassis.BUILDING && Utility.totalWeight(rInfo.components) == 0 )
						{
							Utility.setIndicator(myPlayer, 1, "Tower found.");
							towerLoc = myPlayer.myLoc.add(d);
							obj = ArmoryBuildOrder.EQUIP_TOWER;
							return;
						}
						else
							sleepTime = false;
					}
					else
						sleepTime = false;
				}
				
				if ( sleepTime )
					obj = ArmoryBuildOrder.SUICIDE;
				
				return;
			
			case EQUIP_TOWER:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_TOWER");
				Utility.setIndicator(myPlayer, 1, "Equipping tower.");
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				obj = ArmoryBuildOrder.SLEEP;
				return;
				
			case BUILD_FLYER:
				Utility.setIndicator(myPlayer, 0, "BUILD_FLYER");
				Utility.setIndicator(myPlayer, 0, Clock.getRoundNum()+"");
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.FLYING);
				myPlayer.sleep();
				myPlayer.myRC.suicide();
    		case SLEEP:
				
				Utility.setIndicator(myPlayer, 0, "SLEEP");
				Utility.setIndicator(myPlayer, 1, "zzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
			case SUICIDE:
				
				Utility.setIndicator(myPlayer, 0, "SUICIDE");
				Utility.setIndicator(myPlayer, 1, ":(");
				myPlayer.sleep();
				myPlayer.myRC.suicide();
				return;
				
    	}
		
	}

	public String toString()
	{
		return "ArmoryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}

	public void onWakeupCallback(int lastActiveRound)
	{
		obj=ArmoryBuildOrder.BUILD_FLYER;
		try {
			myPlayer.myRC.turnOn(new MapLocation(myPlayer.myLoc.x+1,myPlayer.myLoc.y+1), RobotLevel.ON_GROUND);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
	}
	
}

