package team068.behaviors;

import battlecode.common.*;
import team068.*;

public class ArmoryBehavior extends Behavior
{
	
	
	private enum ArmoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		EQUIP_UNIT,
		MAKE_WRAITH,
		MAKE_DRONE,
		EQUIP_HEAVY,
		SLEEP
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	int currWraith;
	int currDrone;
	int currHeavy;
	int currUnit;
	
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	
	int babyHeavy;
	
	boolean rHasJump;
	int rNumPlasma;
	
	double minFluxToBuild;
	
	public ArmoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
    			
    		case WAIT_FOR_DOCK:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
    			Utility.setIndicator(myPlayer, 2, "");
    			if ( unitDock != null )
    			{
    				while ( myPlayer.myMotor.isActive() )
    					myPlayer.sleep();
    				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) <= ComponentType.ARMORY.range )
    				{
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
        				obj = ArmoryBuildOrder.EQUIP_UNIT;
    				}
    				else
    				{
    					Utility.setIndicator(myPlayer, 2, "UNIT DOCK OUT OF RANGE!");
    					obj = ArmoryBuildOrder.SLEEP;
    				}
    			}
    			return;
    			
    		case EQUIP_UNIT:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_UNIT");
    			Utility.setIndicator(myPlayer, 2, "Idle.");
    			
    			// check what unit should be made
    			currUnit = currWraith + currDrone + currHeavy;
    			if ( currUnit == 1 )
    			{
    				Utility.setIndicator(myPlayer, 2, "Making wraith.");
    				obj = ArmoryBuildOrder.MAKE_WRAITH;
    			}
    			else if ( currUnit % 3 == 2 )
    			{
    				Utility.setIndicator(myPlayer, 2, "Making drone.");
    				obj = ArmoryBuildOrder.MAKE_DRONE;
    			}
    			else
    			{
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    				if ( r != null && r.getID() != babyHeavy )
    				{
    					Utility.setIndicator(myPlayer, 2, "Equipping heavy.");
    					babyHeavy = r.getID();
    					obj = ArmoryBuildOrder.EQUIP_HEAVY;
    				}
    			}
    			return;
    			
    		case MAKE_WRAITH:
    			
    			Utility.setIndicator(myPlayer, 1, "MAKE_WRAITH");
    			Utility.setIndicator(myPlayer, 2, "Building wraith " + Integer.toString(currWraith) + ".");
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r != null )
    			{
    				obj = ArmoryBuildOrder.EQUIP_UNIT;
    				return;
    			}
    			
    			minFluxToBuild = Chassis.FLYING.cost + ComponentType.BLASTER.cost + ComponentType.RADAR.cost + Constants.RESERVE;
				if ( !myPlayer.myBuilder.isActive() && myPlayer.myRC.getTeamResources() > minFluxToBuild && myPlayer.myRC.getTeamResources() - myPlayer.myLastRes > Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep )
				{
					Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
	    			currWraith++;
	    			obj = ArmoryBuildOrder.EQUIP_UNIT;
				}
				return;
    			
    		case MAKE_DRONE:
    			
    			Utility.setIndicator(myPlayer, 1, "MAKE_DRONE");
    			Utility.setIndicator(myPlayer, 2, "Building drone " + Integer.toString(currDrone) + ".");
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r != null )
    			{
    				obj = ArmoryBuildOrder.EQUIP_UNIT;
    				return;
    			}
    			
    			minFluxToBuild = Chassis.FLYING.cost + ComponentType.CONSTRUCTOR.cost + ComponentType.RADAR.cost + Constants.RESERVE;
				if ( !myPlayer.myBuilder.isActive() && myPlayer.myRC.getTeamResources() > minFluxToBuild && myPlayer.myRC.getTeamResources() - myPlayer.myLastRes > Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep )
				{
					Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
	    			currDrone++;
	    			obj = ArmoryBuildOrder.EQUIP_UNIT;
				}
				return;
				
    		case EQUIP_HEAVY:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_HEAVY");
				Utility.setIndicator(myPlayer, 2, "Equipping heavy " + Integer.toString(currHeavy) + ".");
    			
				r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r == null || r.getID() != babyHeavy )
    			{
    				obj = ArmoryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
    			if ( currHeavy % 3 == 0 ) // currHeavy == 0 case is identical
    			{
					rHasJump = false;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.JUMP )
							rHasJump = true;
					}
					if ( !rHasJump )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND) )
						{
							currHeavy++;
							obj = ArmoryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			else if ( currHeavy % 3 == 1 )
    			{
    				rHasJump = false;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.JUMP )
							rHasJump = true;
					}
					if ( !rHasJump )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND) )
						{
							currHeavy++;
							obj = ArmoryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			else if ( currHeavy % 3 == 2 )
    			{
    				rNumPlasma = 0;
    				rHasJump = false;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.PLASMA )
							rNumPlasma++;
						if ( c == ComponentType.JUMP )
							rHasJump = true;
					}
					if ( rNumPlasma < 2 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.PLASMA, RobotLevel.ON_GROUND);
					else if ( !rHasJump )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND) )
						{
							currHeavy++;
							obj = ArmoryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			return;
    			
    		case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				Utility.setIndicator(myPlayer, 2, "zzzzzzz");
				myPlayer.myRC.turnOff();
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
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_DOCK )
			unitDock = msg.locations[Messenger.firstData];
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)	
	{
		
	}
	
}
