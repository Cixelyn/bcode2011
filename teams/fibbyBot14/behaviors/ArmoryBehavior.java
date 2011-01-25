package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;

public class ArmoryBehavior extends Behavior
{
	
	
	private enum ArmoryBuildOrder 
	{
		SLEEP
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.SLEEP;

	public ArmoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
    			
				
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
		
	}
	
}
