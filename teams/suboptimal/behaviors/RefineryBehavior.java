package suboptimal.behaviors;

import battlecode.common.*;
import suboptimal.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	
	private enum RefineryBuildOrder 
	{
		INITIALIZE,
		GIVE_ANTENNA,
		DETERMINE_LEADER,
		WAIT_FOR_DOCK,
		CLAIM_TOWERS,
		EQUIP_TOWERS,
		EQUIP_UNITS,
		SLEEP
	}
	
	
	RefineryBuildOrder obj = RefineryBuildOrder.INITIALIZE;
	
	MapLocation unitDock;
	
	int currFlyer;
	int currHeavy;
	double lastIncome;
	
	boolean rHasConstructor;
	boolean rHasSight;
	
	boolean keepWaiting;
	boolean rHasRadar;
	int rNumBlasters;
	int rNumSMGs;
	int rNumShields;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	
	MapLocation enemyLocation;
	int spawn = -1; // -1 means unknown
	int realSpawn = -1; // -1 means unknown
	
	final Random random = new Random();
	
	boolean sleepy = true;
	
	public RefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
			
    		case INITIALIZE:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIPPING");
    			if ( Clock.getRoundNum() < 10 ) // I'm one of the first two refineries
    				obj = RefineryBuildOrder.DETERMINE_LEADER;
    			else
    				obj = RefineryBuildOrder.WAIT_FOR_DOCK;
    			return;
    			
    		case DETERMINE_LEADER:
    			
    			Utility.setIndicator(myPlayer, 1, "DETERMINE_LEADER");
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for ( int i = nearbyRobots.length ; --i >= 0 ; )
    			{
    				r = nearbyRobots[i];
    				if ( r.getTeam() == myPlayer.myRC.getTeam() && r.getID() < myPlayer.myRC.getRobot().getID() )
    				{
    					rInfo = myPlayer.mySensor.senseRobotInfo(r);
    					if ( rInfo.chassis == Chassis.BUILDING )
    					{
    						obj = RefineryBuildOrder.WAIT_FOR_DOCK;
    						return;
    					}
    				}
    			}
    			obj = RefineryBuildOrder.GIVE_ANTENNA;
    			return;
    			
    		case GIVE_ANTENNA:
    			
    			Utility.setIndicator(myPlayer, 1, "GIVE_ANTENNA");
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
    			for ( int i = nearbyRobots.length ; --i >= 0 ; )
    			{
    				r = nearbyRobots[i];
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if ( rInfo.chassis == Chassis.LIGHT && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() )
    				{
    					Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(rInfo.location), ComponentType.ANTENNA, RobotLevel.ON_GROUND);
    					obj = RefineryBuildOrder.WAIT_FOR_DOCK;
    					return;
    				}
    			}
    			return;
    			
    		case WAIT_FOR_DOCK:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
    			if ( Clock.getRoundNum() < 20 )
    			{
    				myPlayer.myRC.turnOff();
    				sleepy = false;
    				return;
    			}
    			else if ( unitDock != null )
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
    	    					if ( rInfo.location.distanceSquaredTo(unitDock) <= ComponentType.CONSTRUCTOR.range && rInfo.chassis == Chassis.BUILDING )
    	    					{
    	    						obj = RefineryBuildOrder.SLEEP; // I am one of the first four near armory but not with least ID
    	    						return;
    	    					}
    	    				}
    	    			}
    	    			Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.ANTENNA, RobotLevel.ON_GROUND);
    	    			while ( myPlayer.myMotor.isActive() )
        					myPlayer.sleep();
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    					currFlyer = 0;
    	    			obj = RefineryBuildOrder.EQUIP_UNITS;       // I am one of the first four near armory and with least ID
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP;             // I am one of the first four but not near armory
    			}
    			else if ( Clock.getRoundNum() > Constants.FACTORY_TIME )
	    			obj = RefineryBuildOrder.CLAIM_TOWERS;
    			return;
    		
    		case CLAIM_TOWERS:
    			
    			Utility.setIndicator(myPlayer, 1, "CLAIM_TOWERS");
    			keepWaiting = false;
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for ( int i = nearbyRobots.length ; --i >= 0 ; )
    			{
    				r = nearbyRobots[i];
    				if ( r.getRobotLevel() == RobotLevel.IN_AIR && r.getTeam() == myPlayer.myRC.getTeam() )
    					keepWaiting = true;
    				if ( r.getRobotLevel() == RobotLevel.ON_GROUND && r.getTeam() == myPlayer.myRC.getTeam() )
    				{
    					rInfo = myPlayer.mySensor.senseRobotInfo(r);
    					if ( myPlayer.myBuilder.withinRange(rInfo.location) && rInfo.chassis == Chassis.BUILDING && myPlayer.mySensor.senseObjectAtLocation(rInfo.location, RobotLevel.MINE) == null && Utility.totalWeight(rInfo.components) == 0 )
    					{
    						if ( myPlayer.myRC.getDirection() != myPlayer.myRC.getLocation().directionTo(rInfo.location) )
    						{
    							while ( myPlayer.myMotor.isActive() )
    								myPlayer.sleep();
    							myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(rInfo.location));
    						}
    						obj = RefineryBuildOrder.EQUIP_TOWERS;
    						return;
    					}
    				}
    			}
    			if ( !keepWaiting )
    				obj = RefineryBuildOrder.SLEEP; // there are no unequipped towers nearby and there is no flyer nearby to build any
    			return;
    			
    		case EQUIP_TOWERS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_TOWERS");
    			Utility.setIndicator(myPlayer, 2, "Equipping tower with ID " + Integer.toString(r.getID()) + ".");
    			if ( myPlayer.mySensor.canSenseObject(r) )
    			{
	    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
					rNumBlasters = 0;
					rNumShields = 0;
					rHasRadar = false;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.BLASTER )
							rNumBlasters++;
						if ( c == ComponentType.SHIELD )
							rNumShields++;
						if ( c == ComponentType.RADAR )
							rHasRadar = true;
					}
					if ( rNumBlasters < Constants.BLASTERS_PER_TOWER )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND);
					else if ( rNumShields < Constants.SHIELDS_PER_TOWER )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SHIELD, RobotLevel.ON_GROUND);
					else if ( !rHasRadar )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND) )
							obj = RefineryBuildOrder.CLAIM_TOWERS;
					}
					else
						obj = RefineryBuildOrder.CLAIM_TOWERS;
	    			return;
    			}
    			else
    				obj = RefineryBuildOrder.CLAIM_TOWERS;
    			return;
    			
    		case EQUIP_UNITS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_UNITS");
    			r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
				if ( r != null )
				{
					rInfo = myPlayer.mySensor.senseRobotInfo(r);
					if ( rInfo.chassis == Chassis.HEAVY && r.getTeam() == myPlayer.myRC.getTeam() )
					{
						Utility.setIndicator(myPlayer, 2, "Equipping heavy " + Integer.toString(currHeavy) + ".");
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
						if ( rNumSMGs < 2 )
							Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SMG, RobotLevel.ON_GROUND);
						else if ( rNumShields < 5 )
							Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SHIELD, RobotLevel.ON_GROUND);
						else if ( !rHasRadar )
						{
							if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND) )
							{
								myPlayer.sleep(); // NECESSARY TO GIVE HEAVY TIME TO REALIZE WHO HE IS
								myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_HEAVY, spawn, currHeavy, enemyLocation);
								currHeavy++;
							}
						}
						return;
					}
				}
    			
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
				if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() )
				{
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
					Utility.setIndicator(myPlayer, 2, "Equipping flyer " + Integer.toString(currFlyer) + ".");
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
							myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_FLYER, spawn, currFlyer, enemyLocation);
							currFlyer++;
						}
					}
					return;
				}
				
				Utility.setIndicator(myPlayer, 2, "Idle.");
				return;
    			
    		case SLEEP:
    			
    			Utility.setIndicator(myPlayer, 1, "SLEEP");
    			Utility.setIndicator(myPlayer, 2, "");
    			myPlayer.myRC.turnOff();
    			return;
    			
    	}
		
	}

	public String toString()
	{
		return "MainRefineryBehavior";
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
