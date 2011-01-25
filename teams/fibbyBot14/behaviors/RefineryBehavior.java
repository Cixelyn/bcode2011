package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	
	private enum RefineryBuildOrder 
	{
		INITIALIZE,
		GIVE_ANTENNA,
		DETERMINE_LEADER,
		WAIT_FOR_DOCK,
		EQUIP_UNIT,
		EQUIP_HEAVY,
		EQUIP_DRONE,
		EQUIP_WRAITH,
		EQUIP_ARMOR,
		EQUIP_ARBITER,
		SLEEP,
		REBUILT
	}
	
	RefineryBuildOrder obj = RefineryBuildOrder.INITIALIZE;
	
	MapLocation unitDock;
	
	int currWraith;
	int currDrone;
	int currHeavy;
	int currUnit;
	double lastIncome;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	RobotInfo armoryInfo;
	RobotInfo factoryInfo;
	Robot r;
	ComponentType c;
	int babyUnit;

	boolean rHasConstructor;
	boolean rHasSight;
	
	int rNumSMGs;
	int rNumShields;
	int rNumBlasters;
	int rNumHammers;
	boolean rHasRadar;
	
	boolean hasSlept = false;
	boolean armorEquipped = false;
	boolean arbiterEquipped = false;
	
	public RefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
			
    		case INITIALIZE:
    			
    			//Utility.setIndicator(myPlayer, 1, "INITIALIZE");
    			//Utility.setIndicator(myPlayer, 2, "");
    			if ( Clock.getRoundNum() < 200 ) // I'm one of the first four refineries (change to 10 for first two, 80 for first 3)
    				obj = RefineryBuildOrder.DETERMINE_LEADER;
    			else
    				obj = RefineryBuildOrder.SLEEP;
    			return;
    			
    		case DETERMINE_LEADER:
    			
    			//Utility.setIndicator(myPlayer, 1, "DETERMINE_LEADER");
    			//Utility.setIndicator(myPlayer, 2, "");
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for ( int i = nearbyRobots.length ; --i >= 0 ; )
    			{
    				r = nearbyRobots[i];
    				if ( r.getTeam() == myPlayer.myRC.getTeam() && r.getID() < myPlayer.myRC.getRobot().getID() )
    				{
    					rInfo = myPlayer.mySensor.senseRobotInfo(r);
    					if ( rInfo.chassis == Chassis.BUILDING )
    					{
    						obj = RefineryBuildOrder.SLEEP; // I'm not the leader
    						return;
    					}
    				}
    			}
    			obj = RefineryBuildOrder.GIVE_ANTENNA; // I'm the leader
    			return;
    			
    		case GIVE_ANTENNA:
    			
    			//Utility.setIndicator(myPlayer, 1, "GIVE_ANTENNA");
    			//Utility.setIndicator(myPlayer, 2, "");
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
    			for ( int i = nearbyRobots.length ; --i >= 0 ; )
    			{
    				r = nearbyRobots[i];
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if ( rInfo.chassis == Chassis.LIGHT && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() )
    				{
    					Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(rInfo.location), ComponentType.ANTENNA, RobotLevel.ON_GROUND);
    					obj = RefineryBuildOrder.SLEEP;
    					return;
    				}
    			}
    			return;
    			
    		case WAIT_FOR_DOCK:
    			
    			//Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
    			//Utility.setIndicator(myPlayer, 2, "");
    			if ( unitDock != null )
    			{
    				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) <= ComponentType.CONSTRUCTOR.range )
    				{
    					nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    	    			for ( int i = nearbyRobots.length ; --i >= 0 ; )
    	    			{
    	    				r = nearbyRobots[i];
    	    				if ( r.getTeam() == myPlayer.myRC.getTeam() && r.getID() < myPlayer.myRC.getRobot().getID() )
    	    				{
    	    					rInfo = myPlayer.mySensor.senseRobotInfo(r);
    	    					if ( rInfo.on && rInfo.location.distanceSquaredTo(unitDock) <= ComponentType.CONSTRUCTOR.range && rInfo.chassis == Chassis.BUILDING )
    	    					{
    	    						obj = RefineryBuildOrder.SLEEP; // I am one of the first two capped but not with least ID
    	    						return;
    	    					}
    	    				}
    	    			}
    	    			Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.ANTENNA, RobotLevel.ON_GROUND);
    	    			while ( myPlayer.myMotor.isActive() )
        					myPlayer.sleep();
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    	    			obj = RefineryBuildOrder.EQUIP_UNIT;        // I am one of the first two capped and with least ID
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP;             // I am one of the first two capped but not near armory
    			}
    			return;
    			
    		case EQUIP_UNIT:
    			
    			//Utility.setIndicator(myPlayer, 1, "EQUIP_UNIT");
    			//Utility.setIndicator(myPlayer, 2, "Idle.");
    			
    			// check what unit should be made
    			currUnit = currWraith + currDrone + currHeavy;
    			
    			if ( !armorEquipped && currUnit == 2 )
    				obj = RefineryBuildOrder.EQUIP_ARMOR;
    			// uncomment to make ONLY ONE arbiter
    			/*else if ( !arbiterEquipped && currUnit == Constants.ARBITER_TIME )
    			{
    				// does not count towards currUnit
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    				if ( r != null && r.getID() != babyUnit )
    				{
    					Utility.setIndicator(myPlayer, 2, "Equipping arbiter.");
    					babyUnit = r.getID();
    					obj = RefineryBuildOrder.EQUIP_ARBITER;
    				}
    			}*/
    			// uncomment to make wraith
    			/*if ( currUnit == 1 )
    			{
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    				if ( r != null && r.getID() != babyUnit )
    				{
    					Utility.setIndicator(myPlayer, 2, "Equipping wraith.");
    					babyUnit = r.getID();
    					obj = RefineryBuildOrder.EQUIP_WRAITH;
    				}
    			}
    			else if ( currUnit % 3 == 2 )*/
    			else if ( currUnit == 1 || currUnit % 3 == 2 )
    			{
    				if ( currDrone < Constants.MAX_DRONES )
    				{
	    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
	    				if ( r != null && r.getID() != babyUnit && r.getTeam() == myPlayer.myRC.getTeam() && r.getID() > myPlayer.myRC.getRobot().getID() )
	    				{
	    					//Utility.setIndicator(myPlayer, 2, "Equipping drone.");
	    					babyUnit = r.getID();
	    					obj = RefineryBuildOrder.EQUIP_DRONE;
	    				}
    				}
    				else
    				{
    					r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
        				if ( r != null && r.getID() != babyUnit && r.getTeam() == myPlayer.myRC.getTeam() && r.getID() > myPlayer.myRC.getRobot().getID() )
        				{
        					//Utility.setIndicator(myPlayer, 2, "Equipping arbiter.");
        					babyUnit = r.getID();
        					obj = RefineryBuildOrder.EQUIP_ARBITER;
        				}
    				}
    			}
    			else
    			{
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    				if ( r != null && r.getID() != babyUnit && r.getTeam() == myPlayer.myRC.getTeam() && r.getID() > myPlayer.myRC.getRobot().getID() )
    				{
    					//Utility.setIndicator(myPlayer, 2, "Equipping heavy.");
    					babyUnit = r.getID();
    					obj = RefineryBuildOrder.EQUIP_HEAVY;
    				}
    			}
    			return;
    			
    		case EQUIP_HEAVY:
    			
    			//Utility.setIndicator(myPlayer, 1, "EQUIP_HEAVY");
				//Utility.setIndicator(myPlayer, 2, "Equipping heavy " + Integer.toString(currHeavy) + ".");
    			
				r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r == null || r.getID() != babyUnit || r.getTeam() != myPlayer.myRC.getTeam() )
    			{
    				obj = RefineryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
    			if ( currHeavy % 3 == 0 )
    			{
					rHasRadar = false;
					rNumSMGs = 0;
					rNumShields = 0;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.RADAR )
							rHasRadar = true;
						if ( c == ComponentType.SMG )
							rNumSMGs++;
						if ( c == ComponentType.SHIELD )
							rNumShields++;
					}
					if ( rNumSMGs < 1 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SMG, RobotLevel.ON_GROUND);
					else if ( rNumShields < 1 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SHIELD, RobotLevel.ON_GROUND);
					else if ( !rHasRadar )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND) )
						{
							myPlayer.sleep(); // NECESSARY TO GIVE HEAVY TIME TO REALIZE WHO HE IS
							myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM, -1, currHeavy, null);
							currHeavy++;
							obj = RefineryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			else if ( currHeavy % 3 == 1 )
    			{
    				rHasRadar = false;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.RADAR )
							rHasRadar = true;
					}
					if ( !rHasRadar )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND) )
						{
							myPlayer.sleep(); // NECESSARY TO GIVE HEAVY TIME TO REALIZE WHO HE IS
							myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM, -1, currHeavy, null);
							currHeavy++;
							obj = RefineryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			else if ( currHeavy % 3 == 2 )
    			{
					rHasRadar = false;
					rNumShields = 0;
					rNumSMGs = 0;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.RADAR )
							rHasRadar = true;
						if ( c == ComponentType.SHIELD )
							rNumShields++;
						if ( c == ComponentType.SMG )
							rNumSMGs++;
					}
					if ( rNumSMGs < 3 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SMG, RobotLevel.ON_GROUND);
					else if ( rNumShields < 1 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SHIELD, RobotLevel.ON_GROUND);
					else if ( !rHasRadar )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND) )
						{
							myPlayer.sleep(); // NECESSARY TO GIVE HEAVY TIME TO REALIZE WHO HE IS
							myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM, -1, currHeavy, null);
							currHeavy++;
							obj = RefineryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
				return;
    			
    		case EQUIP_WRAITH:
    			
    			//Utility.setIndicator(myPlayer, 1, "EQUIP_WRAITHS");
    			//Utility.setIndicator(myPlayer, 2, "Equipping wraith " + Integer.toString(currWraith) + ".");
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r == null || r.getID() != babyUnit || r.getTeam() != myPlayer.myRC.getTeam() )
    			{
    				obj = RefineryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
				rNumBlasters = 0;
				rHasRadar = false;
				for ( int j = rInfo.components.length ; --j >= 0 ; )
				{
					c = rInfo.components[j];
					if ( c == ComponentType.BLASTER )
						rNumBlasters++;
					if ( c == ComponentType.RADAR )
						rHasRadar = true;
				}
				if ( rNumBlasters < 1 )
					Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.IN_AIR);
				else if ( !rHasRadar )
				{
					if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.IN_AIR) )
					{
						myPlayer.sleep(); // NECESSARY TO GIVE FLYER TIME TO REALIZE WHO HE IS
						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM, -1, currWraith, null);
						currWraith++;
						obj = RefineryBuildOrder.EQUIP_UNIT;
					}
				}
				return;
    		
    		case EQUIP_DRONE:
    			
    			//Utility.setIndicator(myPlayer, 1, "EQUIP_DRONE");
    			//Utility.setIndicator(myPlayer, 2, "Equipping drone " + Integer.toString(currDrone) + ".");
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r == null || r.getID() != babyUnit || r.getTeam() != myPlayer.myRC.getTeam() )
    			{
    				obj = RefineryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
				rHasConstructor = false;
				rHasSight = false;
				for ( int j = rInfo.components.length ; --j >= 0 ; )
				{
					c = rInfo.components[j];
					if ( c == ComponentType.CONSTRUCTOR )
						rHasConstructor = true;
					if ( c == ComponentType.SIGHT )
						rHasSight = true;
				}
				if ( !rHasConstructor )
					Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.CONSTRUCTOR, RobotLevel.IN_AIR);
				else if ( !rHasSight )
				{
					if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SIGHT, RobotLevel.IN_AIR) )
					{
						myPlayer.sleep(); // NECESSARY TO GIVE FLYER TIME TO REALIZE WHO HE IS
						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM, -1, currDrone, null);
						currDrone++;
						obj = RefineryBuildOrder.EQUIP_UNIT;
					}
				}
				return;
				
    		case EQUIP_ARMOR:
    			
    			//Utility.setIndicator(myPlayer, 1, "EQUIP_ARMOR");
    			//Utility.setIndicator(myPlayer, 2, "");
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
    							if ( c == ComponentType.ARMORY )
	    							armoryInfo = rInfo;
    							if ( c == ComponentType.FACTORY )
    								factoryInfo = rInfo;
    						}
    					}
    				}
    			}
    			
    			if ( armoryInfo != null )
    			{
    				// Armory gives plasma, I don't do anything
    				//Utility.setIndicator(myPlayer, 2, "Armory is giving me plasmas.");
    			}
    			else if ( factoryInfo != null )
    			{
    				// I give myself shields, check if factory has plasmas // FIXME is there a better way to see if fac next to armory? broadcast?
    				//Utility.setIndicator(myPlayer, 2, "Giving shields to myself.");
    				while ( myPlayer.myRC.getTeamResources() < 4*ComponentType.SHIELD.cost + Constants.RESERVE )
    					myPlayer.sleep();
    				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				for ( int i = factoryInfo.components.length ; --i >= 0 ; )
    				{
    					c = factoryInfo.components[i];
    					if ( c == ComponentType.PLASMA )
    					{
    						obj = RefineryBuildOrder.EQUIP_UNIT;
    						return;
    					}
    				}
    				//Utility.setIndicator(myPlayer, 2, "Giving shields to factory and myself.");
    				while ( myPlayer.myRC.getTeamResources() < 5*ComponentType.SHIELD.cost + Constants.RESERVE )
    					myPlayer.sleep();
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryInfo.location), ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryInfo.location), ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryInfo.location), ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryInfo.location), ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryInfo.location), ComponentType.SHIELD, RobotLevel.ON_GROUND);
    			}
    			else
    			{
    				// I give myself shields
    				//Utility.setIndicator(myPlayer, 2, "Giving shields to myself.");
    				while ( myPlayer.myRC.getTeamResources() < 4*ComponentType.SHIELD.cost + Constants.RESERVE )
    					myPlayer.sleep();
    				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.SHIELD, RobotLevel.ON_GROUND);
    				Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.SHIELD, RobotLevel.ON_GROUND);
    			}
    			obj = RefineryBuildOrder.EQUIP_UNIT;
    			while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    			return;
				
    		case EQUIP_ARBITER:
    			
    			//Utility.setIndicator(myPlayer, 1, "EQUIP_ARBITER");
				//Utility.setIndicator(myPlayer, 2, "Equipping arbiter.");
    			
				arbiterEquipped = true;
				r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r == null || r.getID() != babyUnit || r.getTeam() != myPlayer.myRC.getTeam() )
    			{
    				obj = RefineryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
				rNumHammers = 0;
				rHasConstructor = false;
				for ( int j = rInfo.components.length ; --j >= 0 ; )
				{
					c = rInfo.components[j];
					if ( c == ComponentType.HAMMER )
						rNumHammers++;
					if ( c == ComponentType.SATELLITE )
						rHasConstructor = true;
				}
				if ( rNumHammers < 1 )
					Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.HAMMER, RobotLevel.ON_GROUND);
				else if ( !rHasConstructor )
				{
					if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.CONSTRUCTOR, RobotLevel.ON_GROUND) )
					{
						myPlayer.sleep(); // NECESSARY TO GIVE ARBITER TIME TO REALIZE WHO HE IS
						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM, myPlayer.myRC.getRobot().getID(), currDrone, myPlayer.myLoc);
						currDrone++;
						obj = RefineryBuildOrder.EQUIP_UNIT;
					}
				}
    			return;
    			
    		case SLEEP:
				
				//Utility.setIndicator(myPlayer, 1, "SLEEP");
				//Utility.setIndicator(myPlayer, 2, "zzzzzzz");
				myPlayer.shutdown();
				return;
    			
    		case REBUILT:
    			
    			//Utility.setIndicator(myPlayer, 1, "REBUILT");
    			//Utility.setIndicator(myPlayer, 2, "Proxy!");
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
    								currDrone = 5; // TODO change me
    								currHeavy = 10;
    								obj = RefineryBuildOrder.WAIT_FOR_DOCK;
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
		return "RefineryBehavior";
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
		hasSlept = true;
		if ( Clock.getRoundNum() < Constants.REBUILD_TIME )
			obj = RefineryBuildOrder.WAIT_FOR_DOCK; // I'm one of the first four refineries
		else
			obj = RefineryBuildOrder.REBUILT;
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}

}
