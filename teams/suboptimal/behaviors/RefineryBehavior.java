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
		EQUIP_UNIT,
		EQUIP_HEAVY,
		EQUIP_DRONE,
		EQUIP_WRAITH,
		SLEEP
	}
	
	RefineryBuildOrder obj = RefineryBuildOrder.INITIALIZE;
	
	MapLocation unitDock;
	
	int currDrone;
	int currWraith;
	int currHeavy;
	double lastIncome;
	boolean remakeFlyers = false;
	
	boolean rHasConstructor;
	boolean rHasSight;
	
	boolean rHasBlaster;
	boolean rHasRadar;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	Robot r;
	ComponentType c;

	int rNumSMGs;
	int rNumShields;
	
	public RefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
			
    		case INITIALIZE:
    			
    			Utility.setIndicator(myPlayer, 1, "INITIALIZE");
    			if ( Clock.getRoundNum() < 80 ) // I'm one of the first three refineries (change to 10 for first two)
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
    						obj = RefineryBuildOrder.SLEEP; // I'm not the leader
    						return;
    					}
    				}
    			}
    			obj = RefineryBuildOrder.GIVE_ANTENNA; // I'm the leader
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
    					obj = RefineryBuildOrder.SLEEP;
    					return;
    				}
    			}
    			return;
    			
    		case WAIT_FOR_DOCK:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
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
    					currWraith = 0;
    					currDrone = 0;
    	    			obj = RefineryBuildOrder.EQUIP_UNIT;        // I am one of the first two capped and with least ID
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP;             // I am one of the first two capped but not near armory
    			}
    			else if ( Clock.getRoundNum() > Constants.FACTORY_TIME )
	    			obj = RefineryBuildOrder.SLEEP;                 // I am not one of the first two capped
    			return;
    			
    		case EQUIP_UNIT:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_UNIT");
    			
    			if ( !remakeFlyers && Clock.getRoundNum() > Constants.REMAKE_FLYER_TIME )
    			{
    				remakeFlyers = true;
    				currDrone = 0;
    			}
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() )
    			{
    				Utility.setIndicator(myPlayer, 2, "Flyer found.");
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if ( currWraith < Constants.MAX_WRAITHS )
    					obj = RefineryBuildOrder.EQUIP_WRAITH;
    				else if ( currDrone < Constants.MAX_DRONES )
    					obj = RefineryBuildOrder.EQUIP_DRONE;
    				return;
    			}
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() )
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if ( rInfo.chassis == Chassis.HEAVY )
    				{
    					Utility.setIndicator(myPlayer, 2, "Heavy found.");
	    				obj = RefineryBuildOrder.EQUIP_HEAVY;
    				}
	    			return;
    			}
    			
    			Utility.setIndicator(myPlayer, 2, "No units to equip.");
    			return;
    			
    		case EQUIP_HEAVY:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_HEAVY");
				Utility.setIndicator(myPlayer, 2, "Equipping heavy " + Integer.toString(currHeavy) + ".");
    			
				r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r == null )
    			{
    				obj = RefineryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
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
						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_HEAVY, -1, currHeavy, null);
						currHeavy++;
						obj = RefineryBuildOrder.EQUIP_UNIT;
					}
				}
				return;
    			
    		case EQUIP_WRAITH:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_WRAITHS");
    			Utility.setIndicator(myPlayer, 2, "Equipping wraith " + Integer.toString(currWraith) + ".");
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r == null )
    			{
    				obj = RefineryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
				rHasBlaster = false;
				rHasRadar = false;
				for ( int j = rInfo.components.length ; --j >= 0 ; )
				{
					c = rInfo.components[j];
					if ( c == ComponentType.BLASTER )
						rHasBlaster = true;
					if ( c == ComponentType.RADAR )
						rHasRadar = true;
				}
				if ( !rHasBlaster )
					Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.IN_AIR);
				else if ( !rHasRadar )
				{
					if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.IN_AIR) )
					{
						myPlayer.sleep(); // NECESSARY TO GIVE FLYER TIME TO REALIZE WHO HE IS
						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_FLYER, -1, currWraith, null);
						currWraith++;
						obj = RefineryBuildOrder.EQUIP_UNIT;
					}
				}
				return;
    		
    		case EQUIP_DRONE:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_DRONE");
    			Utility.setIndicator(myPlayer, 2, "Equipping drone " + Integer.toString(currDrone) + ".");
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r == null )
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
						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_FLYER, -1, currDrone, null);
						currDrone++;
						obj = RefineryBuildOrder.EQUIP_UNIT;
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
