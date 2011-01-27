package fibbyBot16.behaviors;

import battlecode.common.*;
import fibbyBot16.*;


/**
 * 
 * @author FiBsTeR
 *
 */



public class ScienceFacilityBehavior extends Behavior
{
	
	MapLocation bunkerLoc;
	
	int wakeTime = 0;
	
	boolean allEquipped;
	
	private enum ScienceFacilityBuildOrder 
	{
		FIND_BUNKER,
		EQUIP_BUNKER,
		SLEEP,
		SUICIDE
	}
	
	
	ScienceFacilityBuildOrder obj = ScienceFacilityBuildOrder.FIND_BUNKER;

	public ScienceFacilityBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
    			
    		case FIND_BUNKER:
				
				Utility.setIndicator(myPlayer, 0, "FIND_BUNKER");
				Utility.setIndicator(myPlayer, 1, "Looking for bunker...");
				
				if ( (Clock.getRoundNum() % 500) == 250 )
				{
					obj = ScienceFacilityBuildOrder.SLEEP;
					return;
				}
				
				allEquipped = true;
				for ( int i = Direction.values().length; --i >= 0 ; )
				{
					Direction d = Direction.values()[i];
					
					Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(d), RobotLevel.ON_GROUND);
					if ( r != null )
					{
						RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(r);
						if ( rInfo.chassis == Chassis.BUILDING )
						{
							if ( Utility.totalWeight(rInfo.components) == 0 )
							{
								Utility.setIndicator(myPlayer, 1, "Bunker found.");
								bunkerLoc = myPlayer.myLoc.add(d);
								obj = ScienceFacilityBuildOrder.EQUIP_BUNKER;
								return;
							}
						}
						else
						{
							// non building next to me
							allEquipped = false;
						}
					}
					else
					{
						// blank square next to me
						allEquipped = false;
					}
				}
				
				if ( allEquipped )
					obj = ScienceFacilityBuildOrder.SLEEP;
				
				return;
			
			case EQUIP_BUNKER:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_BUNKER");
				Utility.setIndicator(myPlayer, 1, "Equipping bunker.");
				// 10 plasma
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(bunkerLoc), ComponentType.PLASMA, RobotLevel.ON_GROUND);
				obj = ScienceFacilityBuildOrder.FIND_BUNKER;
				return;
				
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
		return "ScienceFacilityBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}

	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
}
