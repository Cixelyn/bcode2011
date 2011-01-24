package beambot.behaviors;

import battlecode.common.*;
import beambot.*;

public class ArmoryBehavior extends Behavior
{
	
	
	private enum ArmoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		EQUIP_UNIT,
		MAKE_WRAITH,
		MAKE_DRONE,
		EQUIP_HEAVY,
		EQUIP_ARMOR,
		EQUIP_ARBITER,
		SLEEP,
		REBUILT
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	int currWraith;
	int currDrone;
	int currHeavy;
	int currUnit;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	RobotInfo refineryInfo;
	RobotInfo factoryInfo;
	Robot r;
	ComponentType c;
	
	int babyHeavy;
	
	boolean rHasSatellite;
	int rNumJumps;
	int rNumPlasma;
	int rNumBeams;
	
	double minFluxToBuild;
	
	boolean armorEquipped = false;
	boolean nearFactory;
	boolean nearRefinery;
	boolean arbiterEquipped = false;
	
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
    			else if ( Clock.getRoundNum() > 1000 )
    				obj = ArmoryBuildOrder.REBUILT;
    			return;
    			
    		case EQUIP_UNIT:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_UNIT");
    			Utility.setIndicator(myPlayer, 2, "Idle.");
    			
    			// check what unit should be made
    			currUnit = currWraith + currDrone + currHeavy;
    			
    			if ( !armorEquipped && currUnit == 2 )
    				obj = ArmoryBuildOrder.EQUIP_ARMOR;
    			// uncomment to make ONLY ONE arbiter
    			/*else if ( !arbiterEquipped && currUnit == Constants.ARBITER_TIME )
    			{
    				// does not count towards currUnit
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    				if ( r != null && r.getID() != babyHeavy )
    				{
    					Utility.setIndicator(myPlayer, 2, "Equipping arbiter.");
    					babyHeavy = r.getID();
    					obj = ArmoryBuildOrder.EQUIP_ARBITER;
    				}
    			}*/
    			// uncomment to make a wraith
    			/*if ( currUnit == 1 )
    			{
    				Utility.setIndicator(myPlayer, 2, "Making wraith.");
    				obj = ArmoryBuildOrder.MAKE_WRAITH;
    			}
    			else if ( currUnit % 3 == 2 )*/
    			else if ( currUnit == 1 || currUnit % 3 == 2 )
    			{
    				if ( currDrone < Constants.MAX_DRONES )
    				{
	    				Utility.setIndicator(myPlayer, 2, "Making drone.");
	    				obj = ArmoryBuildOrder.MAKE_DRONE;
    				}
    				else
    				{
    					r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
        				if ( r != null && r.getID() != babyHeavy )
        				{
        					Utility.setIndicator(myPlayer, 2, "Equipping arbiter.");
        					babyHeavy = r.getID();
        					obj = ArmoryBuildOrder.EQUIP_ARBITER;
        				}
    				}
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
    			if ( currHeavy % 3 == 0 )
    			{
					rNumJumps = 0;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.JUMP )
							rNumJumps++;
					}
					if ( rNumJumps < 1 )
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
    				rNumPlasma = 0;
    				rNumBeams = 0;
    				rNumJumps = 0;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.PLASMA )
							rNumPlasma++;
						if ( c == ComponentType.BEAM )
							rNumBeams++;
						if ( c == ComponentType.JUMP )
							rNumJumps++;
					}
					if ( rNumPlasma < 2 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.PLASMA, RobotLevel.ON_GROUND);
					else if ( rNumBeams < 2 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BEAM, RobotLevel.ON_GROUND);
					else if ( rNumJumps < 1 )
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
					rNumJumps = 0;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.PLASMA )
							rNumPlasma++;
						if ( c == ComponentType.JUMP )
							rNumJumps++;
					}
					if ( rNumPlasma < 2 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.PLASMA, RobotLevel.ON_GROUND );
					else if ( rNumJumps < 1 )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND) )
						{
							currHeavy++;
							obj = ArmoryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			
    			return;
    			
    		case EQUIP_ARMOR:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_ARMOR");
    			Utility.setIndicator(myPlayer, 2, "");
    			armorEquipped = true;
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for ( int i = nearbyRobots.length ; --i >= 0 ; )
    			{
    				r = nearbyRobots[i];
    				if ( r.getTeam() == myPlayer.myRC.getTeam() && r.getRobotLevel() == RobotLevel.ON_GROUND )
    				{
    					rInfo = myPlayer.mySensor.senseRobotInfo(r);
    					if ( rInfo.on && rInfo.chassis == Chassis.BUILDING )
    					{
    						for ( int j = rInfo.components.length ; --j >= 0 ; )
    						{
    							c = rInfo.components[j];
    							if ( c == ComponentType.RECYCLER )
	    							refineryInfo = rInfo;
    							if ( c == ComponentType.FACTORY )
    								factoryInfo = rInfo;
    						}
    					}
    				}
    			}
    			
    			if ( factoryInfo != null )
    			{
    				// give factory plasmas
    				Utility.setIndicator(myPlayer, 2, "Giving plasmas to factory.");
    				while ( myPlayer.myRC.getTeamResources() < 2*ComponentType.PLASMA.cost + Constants.RESERVE )
    					myPlayer.sleep();
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryInfo.location), ComponentType.PLASMA, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryInfo.location), ComponentType.PLASMA, RobotLevel.ON_GROUND);
    			}
    			if ( refineryInfo != null )
    			{
    				// give refinery plasmas
    				Utility.setIndicator(myPlayer, 2, "Giving plasmas to refinery.");
    				while ( myPlayer.myRC.getTeamResources() < 2*ComponentType.PLASMA.cost + Constants.RESERVE )
    					myPlayer.sleep();
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(refineryInfo.location), ComponentType.PLASMA, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(refineryInfo.location), ComponentType.PLASMA, RobotLevel.ON_GROUND);
    			}
				// give self plasmas
    			Utility.setIndicator(myPlayer, 2, "Giving plasmas to myself.");
    			while ( myPlayer.myRC.getTeamResources() < 2*ComponentType.PLASMA.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.PLASMA, RobotLevel.ON_GROUND);
				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.PLASMA, RobotLevel.ON_GROUND);
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
				obj = ArmoryBuildOrder.EQUIP_UNIT;
    			return;
    			
    		case EQUIP_ARBITER:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_ARBITER");
				Utility.setIndicator(myPlayer, 2, "Equipping arbiter.");
    			
				arbiterEquipped = true;
				r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r == null || r.getID() != babyHeavy )
    			{
    				obj = ArmoryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
				rNumJumps = 0;
				rHasSatellite = false;
				for ( int j = rInfo.components.length ; --j >= 0 ; )
				{
					c = rInfo.components[j];
					if ( c == ComponentType.JUMP )
						rNumJumps++;
					if ( c == ComponentType.SATELLITE )
						rHasSatellite = true;
				}
				if ( rNumJumps < 2 )
					Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND);
				else if ( !rHasSatellite )
				{
					if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SATELLITE, RobotLevel.ON_GROUND) )
					{
						currDrone++;
						obj = ArmoryBuildOrder.EQUIP_UNIT;
					}
				}
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
    							c = rInfo.components[j];
    							if ( c == ComponentType.CONSTRUCTOR )
    							{
    								Utility.setIndicator(myPlayer, 2, "Arbiter found.");
    								unitDock = rInfo.location;
    								obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
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
