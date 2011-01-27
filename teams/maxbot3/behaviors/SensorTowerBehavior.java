package maxbot3.behaviors;

import battlecode.common.*;
import maxbot3.*;
import java.util.*;

public class SensorTowerBehavior extends Behavior
{
	
	private enum SensorTowerBuildOrder 
	{
		TURN_ON_FLYER,
		SLEEP
	}
	
	SensorTowerBuildOrder obj = SensorTowerBuildOrder.TURN_ON_FLYER;
	
	public SensorTowerBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
			
			case TURN_ON_FLYER:
				
				Utility.setIndicator(myPlayer, 0, "TURN_ON_FLYER");
				
				if ( Clock.getRoundNum() % 500 < 5 )
					myPlayer.myRC.turnOn(myPlayer.myLoc.add(Direction.NORTH), RobotLevel.IN_AIR);
				if ( Clock.getRoundNum() % 500 == 5 )
					obj = SensorTowerBuildOrder.SLEEP;
				return;
				
			case SLEEP:
				if (Clock.getRoundNum()>=8000) {
					myPlayer.sleep();
				}
				else {
					Utility.setIndicator(myPlayer, 0, "SLEEP");
					Utility.setIndicator(myPlayer, 1, "zzzzzz");
					myPlayer.myRC.turnOff();
				}
				return;
				
    	}
		
	}

	public String toString()
	{
		return "SensorTowerBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		obj = SensorTowerBuildOrder.TURN_ON_FLYER;
	}
	

}

