package maxbot3.behaviors;

import battlecode.common.*;
import maxbot3.*;

public class FactoryBehavior extends Behavior
{
	
	
	MapLocation towerLoc;
	
	int wakeTime = 0;
	int numHeavies=0;
	
	private enum FactoryBuildOrder 
	{
		MAKE_HAMMER_BROS,
		SLEEP
	}
	
	
	FactoryBuildOrder obj = FactoryBuildOrder.MAKE_HAMMER_BROS;

	public FactoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
				
			case MAKE_HAMMER_BROS:
				
				Utility.setIndicator(myPlayer, 0, "MAKE_HAMMER_BROS");
				Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.NORTH), RobotLevel.ON_GROUND);
				if (r!=null) {
					myPlayer.sleep();
				}
				else {
					Utility.buildChassis(myPlayer, Direction.NORTH, Chassis.HEAVY);
					numHeavies++;
					if (numHeavies==4) {
						obj=FactoryBuildOrder.SLEEP;
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
