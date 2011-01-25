package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	
	MapLocation refineryLoc;
	MapLocation spawnLoc;
	
	
	private enum RefineryBuildOrder 
	{
		DETERMINE_LEADER,
		TURN_ON_SCV,
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
				refineryLoc = myPlayer.myLoc;
				spawnLoc = refineryLoc.add(Direction.SOUTH_EAST);
				if ( myPlayer.mySensor.senseObjectAtLocation(refineryLoc.add(Direction.SOUTH), RobotLevel.ON_GROUND) != null )
				{
					Utility.setIndicator(myPlayer, 1, "I'm the leader!");
					obj = RefineryBuildOrder.TURN_ON_SCV;
				}
				else
				{
					Utility.setIndicator(myPlayer, 1, "I'm not the leader.");
					obj = RefineryBuildOrder.SLEEP;
				}
				return;
				
			case TURN_ON_SCV:
				
				Utility.setIndicator(myPlayer, 0, "TURN_ON_SCV");
				if ( Clock.getRoundNum() >= Constants.SHOW_TIME )
				{
					Utility.setIndicator(myPlayer, 1, "It's show time!");
					myPlayer.myRC.turnOn(spawnLoc, RobotLevel.ON_GROUND);
					myPlayer.sleep();
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

	}
	

}
