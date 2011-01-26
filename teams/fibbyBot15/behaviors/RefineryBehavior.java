package fibbyBot15.behaviors;

import battlecode.common.*;
import fibbyBot15.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	Message myLocMsg;
	int wakeTime = 0;
	
	private enum RefineryBuildOrder 
	{
		DETERMINE_LEADER,
		BROADCAST_LOC,
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
				if ( Clock.getRoundNum() > Constants.LEADER_TIME )
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
				if ( Clock.getRoundNum() % 250 == 0 )
					myPlayer.myBroadcaster.broadcastTurnOnAll();
				else
					myPlayer.myBroadcaster.broadcast(myLocMsg);
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
