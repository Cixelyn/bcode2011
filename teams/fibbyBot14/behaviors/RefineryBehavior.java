package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	
	private enum RefineryBuildOrder 
	{
		TURN_ON_SCV
	}
	
	RefineryBuildOrder obj = RefineryBuildOrder.TURN_ON_SCV;
	
	public RefineryBehavior(RobotPlayer player)
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
		return "RefineryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{

	}
	

}
