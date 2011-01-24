package team068.behaviors;

import team068.*;
import battlecode.common.*;

public class FactoryBehavior extends Behavior
{

	private enum FactoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		EQUIP_UNIT,
		MAKE_HEAVY,
		EQUIP_ARMOR,
		MAKE_ARBITER,
		SLEEP,
		REBUILT
	}
	
	
	FactoryBuildOrder obj = FactoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	Robot[] nearbyRobots;
	Robot r;
	RobotInfo rInfo;
	RobotInfo armoryInfo;
	RobotInfo refineryInfo;
	
	int currWraith;
	int currDrone;
	int currHeavy;
	int currUnit;
	
	int babyWraith;
	int babyDrone;
	
	double minFluxToBuild;
	
	boolean towerEquipped = false;
	boolean armorEquipped = false;
	boolean arbiterMade = false;
	
	public FactoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch ( obj )
		{
			
			case WAIT_FOR_DOCK:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
    			if ( unitDock != null )
    			{
    				while ( myPlayer.myMotor.isActive() )
    					myPlayer.sleep();
    				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) <= ComponentType.FACTORY.range )
    				{
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
        				obj = FactoryBuildOrder.EQUIP_UNIT;
    				}
    				else
    				{
    					Utility.setIndicator(myPlayer, 2, "UNIT DOCK OUT OF RANGE!");
    					obj = FactoryBuildOrder.SLEEP;
    				}
    			}
    			else if ( Clock.getRoundNum() > Constants.REBUILD_TIME )
    				obj = FactoryBuildOrder.REBUILT;
    			return;
    			
			case EQUIP_UNIT:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_UNIT");
    			Utility.setIndicator(myPlayer, 2, "Idle.");
    			
    			// check what unit should be made
    			currUnit = currWraith + currDrone + currHeavy;
    			
    			if ( !armorEquipped && currUnit == 2 )
    				obj = FactoryBuildOrder.EQUIP_ARMOR;
    			// uncomment me to make ONLY ONE arbiter
    			/*else if ( !arbiterMade && currUnit == Constants.ARBITER_TIME )
    			{
    				// does not count towards currUnit
    				Utility.setIndicator(myPlayer, 2, "Making arbiter.");
    				obj = FactoryBuildOrder.MAKE_ARBITER;
    			}*/
    			// uncomment me to make a wraith
    			/*if ( currUnit == 1 )
    			{
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    				if ( r != null && r.getID() != babyWraith )
    				{
    					Utility.setIndicator(myPlayer, 2, "Halting for wraith.");
    					babyWraith = r.getID();
    					currWraith++;
    				}
    			}
    			else if ( currUnit % 3 == 2 )*/
				else if ( currUnit == 1 || currUnit % 3 == 2 )
    			{
    				if ( currDrone < Constants.MAX_DRONES )
    				{
    					r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
        				if ( r != null && r.getID() != babyDrone )
        				{
        					Utility.setIndicator(myPlayer, 2, "Halting for drone.");
        					babyDrone = r.getID();
        					currDrone++;
        				}
    				}
    				else
    				{
    					Utility.setIndicator(myPlayer, 2, "Making arbiter.");
    					obj = FactoryBuildOrder.MAKE_ARBITER;
    				}
    			}
    			else
    			{
    				Utility.setIndicator(myPlayer, 2, "Making heavy.");
    				obj = FactoryBuildOrder.MAKE_HEAVY;
    			}
    			return;
    			
			case MAKE_HEAVY:
				
				Utility.setIndicator(myPlayer, 1, "MAKE_HEAVY");
    			Utility.setIndicator(myPlayer, 2, "Building heavy " + Integer.toString(currHeavy) + ".");
    			
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				if ( currHeavy == 0 )
					minFluxToBuild = Chassis.HEAVY.cost + ComponentType.RADAR.cost + ComponentType.JUMP.cost + 2*ComponentType.RAILGUN.cost + ComponentType.SHIELD.cost + ComponentType.SMG.cost + Constants.RESERVE;
				else if ( currHeavy % 3 == 0 )
				{
					//minFluxToBuild = Utility.totalCost(Constants.heavyLoadout1) + Constants.RESERVE;
					minFluxToBuild = Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + 2*Constants.RESERVE; // heavy cheaper than refinery, prioritize refinery
				}
				else if ( currHeavy % 3 == 1 )
				{
					//minFluxToBuild = Utility.totalCost(Constants.heavyLoadout2) + Constants.RESERVE;
					minFluxToBuild = Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + 2*Constants.RESERVE; // heavy cheaper than refinery, prioritize refinery
				}
				else if ( currHeavy % 3 == 2 )
				{
					//minFluxToBuild = Utility.totalCost(Constants.heavyLoadout3) + Constants.RESERVE;
					minFluxToBuild = Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + 2*Constants.RESERVE; // heavy cheaper than refinery, prioritize refinery
				}
    			while ( r != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < minFluxToBuild || myPlayer.myRC.getTeamResources() - myPlayer.myLastRes < Chassis.BUILDING.upkeep + Chassis.HEAVY.upkeep * (currHeavy + 1) )
    			{
    				myPlayer.sleep();
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			}
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.HEAVY);
    			if ( currHeavy % 3 == 0 )
    			{
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
    			}
    			else if ( currHeavy % 3 == 1 )
    			{
    				
    			}
    			else if ( currHeavy % 3 == 2 )
    			{
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
    			}
    			currHeavy++;
    			obj = FactoryBuildOrder.EQUIP_UNIT;
    			return;
    			
			case EQUIP_ARMOR:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_ARMOR");
    			Utility.setIndicator(myPlayer, 2, "Waiting for plasmas or shields.");
    			armorEquipped = true;
    			// get plasmas or shields
    			while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    			obj = FactoryBuildOrder.EQUIP_UNIT;
    			return;
    			
			case MAKE_ARBITER:
				
				Utility.setIndicator(myPlayer, 1, "MAKE_ARBITER");
    			Utility.setIndicator(myPlayer, 2, "Building arbiter.");
    			
    			arbiterMade = true;
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				minFluxToBuild = Chassis.HEAVY.cost + Utility.totalCost(Constants.arbiterLoadout) + Constants.RESERVE;
    			while ( r != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < minFluxToBuild )
    			{
    				myPlayer.sleep();
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			}
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.HEAVY);
    			currDrone++;
    			obj = FactoryBuildOrder.EQUIP_UNIT;
    			return;
    			
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				Utility.setIndicator(myPlayer, 2, "zzzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
			case REBUILT:
    			
    			Utility.setIndicator(myPlayer, 1, "REBUILT");
    			Utility.setIndicator(myPlayer, 2, "Proxy!");
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for ( int i = nearbyRobots.length ; --i >= 0 ; )
    			{
    				r = nearbyRobots[i];
    				if ( r.getTeam() == myPlayer.myRC.getTeam() && r.getRobotLevel() == RobotLevel.ON_GROUND )
    				{
    					rInfo = myPlayer.mySensor.senseRobotInfo(r);
    					if ( rInfo.chassis == Chassis.HEAVY && rInfo.on )
    					{
    						for ( int j = rInfo.components.length ; --j >= 0 ; )
    						{
    							ComponentType c = rInfo.components[j];
    							if ( c == ComponentType.CONSTRUCTOR )
    							{
    								Utility.setIndicator(myPlayer, 2, "Arbiter found.");
    								unitDock = rInfo.location;
    								obj = FactoryBuildOrder.WAIT_FOR_DOCK;
    								return;
    							}
    						}
    					}
    				}
    			}
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
