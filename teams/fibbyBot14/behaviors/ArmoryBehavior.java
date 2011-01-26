package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;

public class ArmoryBehavior extends Behavior
{
	
	
	MapLocation towerLoc;
	
	int wakeTime = 0;
	
	private enum ArmoryBuildOrder 
	{
		FIND_TOWER,
		EQUIP_TOWER,
		MAKE_HERO_WRAITH_1,
		MAKE_MEDIVAC_1A,
		MAKE_MEDIVAC_1B,
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
				
				for ( int i = Direction.values().length; --i >= 0 ; )
				{
					Direction d = Direction.values()[i];
					Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(d), RobotLevel.ON_GROUND);
					if ( r != null )
					{
						RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(r);
						if ( rInfo.chassis == Chassis.BUILDING && Utility.totalWeight(rInfo.components) == 0 )
						{
							Utility.setIndicator(myPlayer, 1, "Tower found.");
							towerLoc = myPlayer.myLoc.add(d);
							obj = ArmoryBuildOrder.EQUIP_TOWER;
							return;
						}
					}
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
				
			case MAKE_HERO_WRAITH_1:
				
				Utility.setIndicator(myPlayer, 0, "MAKE_HERO_WRAITH_1");
				Utility.setIndicator(myPlayer, 1, "It's NOT a void ray.");
				Utility.buildChassis(myPlayer, Direction.SOUTH_EAST, Chassis.FLYING);
				Utility.buildComponent(myPlayer, Direction.SOUTH_EAST, ComponentType.BEAM, RobotLevel.IN_AIR);
				obj = ArmoryBuildOrder.MAKE_MEDIVAC_1A;
				return;
				
			case MAKE_MEDIVAC_1A:
				
				Utility.setIndicator(myPlayer, 0, "MAKE_MEDIVAC_1A");
				Utility.setIndicator(myPlayer, 1, "");
				while ( myPlayer.myRC.getTeamResources() < Chassis.FLYING.cost + ComponentType.MEDIC.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.OMNI, Chassis.FLYING);
				while ( Clock.getRoundNum() < Constants.SECOND_MEDIVAC )
					myPlayer.sleep();
				obj = ArmoryBuildOrder.MAKE_MEDIVAC_1B;
				return;
				
			case MAKE_MEDIVAC_1B:
				
				Utility.setIndicator(myPlayer, 0, "MAKE_MEDIVAC_1B");
				Utility.setIndicator(myPlayer, 1, "");
				while ( myPlayer.myRC.getTeamResources() < Chassis.FLYING.cost + ComponentType.MEDIC.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.WEST, Chassis.FLYING);
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
		wakeTime++;
		switch ( wakeTime )
		{
			
			case 1:
				obj = ArmoryBuildOrder.MAKE_HERO_WRAITH_1;
				return;
		}
	}
	
}
