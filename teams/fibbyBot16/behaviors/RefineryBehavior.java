package fibbyBot16.behaviors;

import battlecode.common.*;
import fibbyBot16.*;
import java.util.*;


/**
 * 
 * @author FiBsTeR
 *
 */


public class RefineryBehavior extends Behavior
{
	
	Message myLocMsg;
	
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
				while ( myPlayer.myBroadcaster.isActive() )
					myPlayer.sleep();
				if ( Clock.getRoundNum() % 500 == 0 || Clock.getRoundNum() % 500 == 250 + timingu() )
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

	public int timingu()
	{
		// this is where the magic happens, baby
		// depending on what round it is, the spawned units reach us slower
		// turn on the towers timingu rounds after the night cycle started
		switch ( (Clock.getRoundNum() / 500) )
		{
			case 0:
				return 30;
			case 1:
				return 30;
			case 2:
				return 30;
			case 3:
				return 130;
			case 4:
				return 130;
			case 5:
				return 70;
			default:
				return 0;
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
		
	}
	

}
