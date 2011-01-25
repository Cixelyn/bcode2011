package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;

public class FactoryBehavior extends Behavior
{
	
	
	MapLocation towerLoc;
	
	int wakeTime = 0;
	
	private enum FactoryBuildOrder 
	{
		SLEEP
	}
	
	
	FactoryBuildOrder obj = FactoryBuildOrder.SLEEP;

	public FactoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
				
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
