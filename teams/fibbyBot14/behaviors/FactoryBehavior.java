package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;

public class FactoryBehavior extends Behavior
{
	
	
	MapLocation towerLoc;
	
	int wakeTime = 0;
	
	private enum FactoryBuildOrder 
	{
		EQUIP_MEDIVAC_1,
		EQUIP_MEDIVAC_2,
		SLEEP
	}
	
	
	FactoryBuildOrder obj = FactoryBuildOrder.EQUIP_MEDIVAC_1;

	public FactoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
				
			case EQUIP_MEDIVAC_1:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_MEDIVAC_1");
				Utility.setIndicator(myPlayer, 1, "Waiting for flyer...");
				Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.NORTH), RobotLevel.IN_AIR);
				if ( r != null )
				{
					Utility.setIndicator(myPlayer, 1, "Flyer found.");
					Utility.buildComponent(myPlayer, Direction.NORTH, ComponentType.MEDIC, RobotLevel.IN_AIR);
					obj = FactoryBuildOrder.EQUIP_MEDIVAC_2;
				}
				return;
				
			case EQUIP_MEDIVAC_2:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_MEDIVAC_2");
				Utility.setIndicator(myPlayer, 1, "Waiting for flyer...");
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.NORTH_WEST), RobotLevel.IN_AIR);
				if ( r != null )
				{
					Utility.setIndicator(myPlayer, 1, "Flyer found.");
					Utility.buildComponent(myPlayer, Direction.NORTH_WEST, ComponentType.MEDIC, RobotLevel.IN_AIR);
					obj = FactoryBuildOrder.SLEEP;
				}
				return;
			
    		case SLEEP:
				
				Utility.setIndicator(myPlayer, 0, "SLEEP");
				Utility.setIndicator(myPlayer, 1, "zzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
    	}
		
	}

	public String toString()
	{
		return "FactoryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}

	public void onWakeupCallback(int lastActiveRound)
	{
		wakeTime++;
	}
	
}
