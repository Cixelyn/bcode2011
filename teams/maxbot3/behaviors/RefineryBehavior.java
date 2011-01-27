package maxbot3.behaviors;

import battlecode.common.*;
import maxbot3.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	Message myLocMsg;
	int wakeTime = 0;
	int numHammerBros=0;
	
	private enum RefineryBuildOrder 
	{
		DETERMINE_LEADER,
		BROADCAST_LOC,
		EQUIP_FLYER,
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
				if ( Clock.getRoundNum() > Constants.LEADER_TIME && Clock.getRoundNum() < Constants.LAST_LEADER )
				{
					// I'm the 4th refinery
					Utility.setIndicator(myPlayer, 1, "I'm the leader!");
					Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.ANTENNA, RobotLevel.ON_GROUND);
					myLocMsg = new Message();
					myLocMsg.locations = new MapLocation[] {myPlayer.myLoc};
					obj = RefineryBuildOrder.BROADCAST_LOC;
				}
				else
				{
					Utility.setIndicator(myPlayer, 1, "I'm not the leader.");
					obj = RefineryBuildOrder.SLEEP;
				}
				return;
				
			case BROADCAST_LOC:
				
				Utility.setIndicator(myPlayer, 0, "BROADCAST_LOC");
				Utility.setIndicator(myPlayer, 1, "");
				Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.NORTH_EAST), RobotLevel.ON_GROUND);
				if ( r != null)
				{
					RobotInfo rInfo=myPlayer.mySensor.senseRobotInfo(r);
					if (rInfo.chassis.equals(Chassis.HEAVY))
					{
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
						if (numHammerBros==5) {
							obj = RefineryBuildOrder.SLEEP;
						}
					}
				}
				if ( !myPlayer.myBroadcaster.isActive() )
				{
					if ( Clock.getRoundNum() % 500 < 20 && Clock.getRoundNum() / 500 >= 3 )
						myPlayer.myBroadcaster.broadcastTurnOnAll();
					else
						myPlayer.myBroadcaster.broadcast(myLocMsg);
				}
				return;
				
			case EQUIP_FLYER:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_FLYER");
				Utility.setIndicator(myPlayer, 1, "");
				
				Robot robot = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.WEST), RobotLevel.IN_AIR);
				if ( robot != null) {
					Utility.buildComponent(myPlayer, Direction.WEST, ComponentType.CONSTRUCTOR, RobotLevel.IN_AIR);
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
		obj = RefineryBuildOrder.EQUIP_FLYER;
	}
	

}

