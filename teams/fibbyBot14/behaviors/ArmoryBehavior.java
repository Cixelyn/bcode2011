package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;

public class ArmoryBehavior extends Behavior
{
	
	
	MapLocation towerLoc;
	
	private enum ArmoryBuildOrder 
	{
		FIND_TOWER,
		EQUIP_TOWER,
		SLEEP,
		SUICIDE
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.FIND_TOWER;

	public ArmoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
    			
			case FIND_TOWER:
				
				Utility.setIndicator(myPlayer, 0, "FIND_TOWER");
				Utility.setIndicator(myPlayer, 1, "Looking for tower...");
				
				Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.EAST), RobotLevel.ON_GROUND);
				
				if ( r != null && r.getID() > myPlayer.myRC.getRobot().getID() )
				{
					Utility.setIndicator(myPlayer, 1, "Tower found.");
					towerLoc = myPlayer.myLoc.add(Direction.EAST);
					obj = ArmoryBuildOrder.EQUIP_TOWER;
				}
				
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(Direction.NORTH_WEST), RobotLevel.ON_GROUND);
				
				if ( r != null && r.getID() > myPlayer.myRC.getRobot().getID() )
				{
					Utility.setIndicator(myPlayer, 1, "Tower found.");
					towerLoc = myPlayer.myLoc.add(Direction.NORTH_WEST);
					obj = ArmoryBuildOrder.EQUIP_TOWER;
				}
				return;
			
			case EQUIP_TOWER:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_TOWER");
				Utility.setIndicator(myPlayer, 1, "Equipping tower.");
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(towerLoc), ComponentType.BEAM, RobotLevel.ON_GROUND);
				obj = ArmoryBuildOrder.SLEEP;
				return;
				
    		case SLEEP:
				
				Utility.setIndicator(myPlayer, 0, "SLEEP");
				Utility.setIndicator(myPlayer, 1, "zzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
			case SUICIDE:
				
				Utility.setIndicator(myPlayer, 0, "SUICIDE");
				Utility.setIndicator(myPlayer, 1, ":(");
				myPlayer.sleep();
				myPlayer.myRC.suicide();
				return;
				
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
