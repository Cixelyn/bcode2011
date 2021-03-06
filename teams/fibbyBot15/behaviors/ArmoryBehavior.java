package fibbyBot15.behaviors;

import battlecode.common.*;
import fibbyBot15.*;


/**
 * 
 * @author FiBsTeR
 *
 */



public class ArmoryBehavior extends Behavior
{
	
	MapLocation towerLoc;
	
	int wakeTime = 0;
	
	boolean allEquipped;
	
	private enum ArmoryBuildOrder 
	{
		FIND_TOWER,
		EQUIP_TOWER,
		SLEEP,
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
				
				if ( (Clock.getRoundNum() % 500) == 250 )
				{
					obj = ArmoryBuildOrder.SLEEP;
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
								Utility.setIndicator(myPlayer, 1, "Tower found.");
								towerLoc = myPlayer.myLoc.add(d);
								obj = ArmoryBuildOrder.EQUIP_TOWER;
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
					obj = ArmoryBuildOrder.SUICIDE;
				
				return;
			
			case EQUIP_TOWER:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_TOWER");
				Utility.setIndicator(myPlayer, 1, "Equipping tower.");
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				obj = ArmoryBuildOrder.FIND_TOWER;
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
		return "ArmoryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}

	public void onWakeupCallback(int lastActiveRound)
	{
		if ( Clock.getRoundNum() % 500 < 250 )
			obj = ArmoryBuildOrder.FIND_TOWER;
	}
	
}
