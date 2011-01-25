package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	
	int wakeTime = 0;
	
	private enum RefineryBuildOrder 
	{
		DETERMINE_LEADER,
		TOWER_TIME,
		SHOW_TIME,
		EQUIP_HERO_WRAITH_1,
		EQUIP_HERO_WRAITH_2,
		SLEEP
	}
	
	RefineryBuildOrder obj = RefineryBuildOrder.DETERMINE_LEADER;
	
	public RefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
			case DETERMINE_LEADER:
				
				Utility.setIndicator(myPlayer, 0, "DETERMINE_LEADER");
				if ( Clock.getRoundNum() > Constants.LEADER_TIME && Clock.getRoundNum() < Constants.TOWER_TIME )
				{
					// I'm the 4th refinery
					Utility.setIndicator(myPlayer, 1, "I'm the leader!");
					obj = RefineryBuildOrder.TOWER_TIME;
				}
				else
				{
					Utility.setIndicator(myPlayer, 1, "I'm not the leader.");
					obj = RefineryBuildOrder.SLEEP;
				}
				return;
				
			case TOWER_TIME:
				
				Utility.setIndicator(myPlayer, 0, "TOWER_TIME");
				Utility.setIndicator(myPlayer, 1, "Waiting...");
				if ( Clock.getRoundNum() >= Constants.TOWER_TIME )
				{
					Utility.setIndicator(myPlayer, 1, "It's tower time!");
					myPlayer.myRC.turnOn(myPlayer.myLoc.add(Direction.NORTH), RobotLevel.ON_GROUND);
					obj = RefineryBuildOrder.SLEEP;
				}
				return;
				
			case SHOW_TIME:
				
				Utility.setIndicator(myPlayer, 0, "SHOW_TIME");
				Utility.setIndicator(myPlayer, 1, "Waiting...");
				if ( Clock.getRoundNum() >= Constants.SHOW_TIME )
				{
					Utility.setIndicator(myPlayer, 1, "It's show time!");
					myPlayer.myRC.turnOn(myPlayer.myLoc.add(Direction.NORTH_EAST), RobotLevel.ON_GROUND);
					obj = RefineryBuildOrder.EQUIP_HERO_WRAITH_1;
				}
				return;
				
			case EQUIP_HERO_WRAITH_1:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_HERO_WRAITH_1");
				Utility.setIndicator(myPlayer, 1, "Waiting for flyer...");
				Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.NORTH_EAST), RobotLevel.IN_AIR);
				if ( r != null )
				{
					Utility.setIndicator(myPlayer, 1, "Flyer found.");
					Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.SHIELD, RobotLevel.IN_AIR);
					obj = RefineryBuildOrder.EQUIP_HERO_WRAITH_2;
				}
				return;
				
			case EQUIP_HERO_WRAITH_2:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_HERO_WRAITH_2");
				Utility.setIndicator(myPlayer, 1, "Waiting for flyer...");
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.NORTH_WEST), RobotLevel.IN_AIR);
				if ( r != null )
				{
					Utility.setIndicator(myPlayer, 1, "Flyer found.");
					Utility.buildComponent(myPlayer, Direction.NORTH_WEST, ComponentType.SHIELD, RobotLevel.IN_AIR);
					obj = RefineryBuildOrder.SLEEP;
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
		return "RefineryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		wakeTime++;
		switch ( wakeTime )
		{
			case 1:
				obj = RefineryBuildOrder.SHOW_TIME;
		}
	}
	

}
