package maxbot3.behaviors;

import battlecode.common.*;
import maxbot3.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	
	int wakeTime = 0;
	
	private enum RefineryBuildOrder 
	{
		DETERMINE_LEADER,
		TOWER_TIME,
		EQUIP_HAMMER_BROS,
		SLEEP
	}
	
	RefineryBuildOrder obj = RefineryBuildOrder.DETERMINE_LEADER;
	int numHammerBros=0;
	
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
					obj = RefineryBuildOrder.EQUIP_HAMMER_BROS;
				}
				return;
				
			case EQUIP_HAMMER_BROS:
				
				Utility.setIndicator(myPlayer, 0, "HAMMER_BROTHERS");
				Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.NORTH_EAST), RobotLevel.ON_GROUND);
				if ( r != null)
				{
					RobotInfo rInfo=myPlayer.mySensor.senseRobotInfo(r);
					if (rInfo.chassis.equals(Chassis.HEAVY)) {
						Utility.setIndicator(myPlayer, 1, "Heavy Found");
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, Direction.NORTH_EAST, ComponentType.HAMMER, RobotLevel.ON_GROUND);
						numHammerBros++;
						if (numHammerBros==4) {
							obj = RefineryBuildOrder.SLEEP;
						}
					}
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
	}
	

}
