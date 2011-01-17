package fibbyBot12.behaviors;

import battlecode.common.*;
import fibbyBot12.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	
	private enum RefineryBuildOrder 
	{
		INITIALIZE,
		GIVE_ANTENNA,
		DETERMINE_LEADER,
		WAIT_FOR_DOCK,
		EQUIP_WRAITHS,
		EQUIP_DRONES,
		SLEEP
	}
	
	
	RefineryBuildOrder obj = RefineryBuildOrder.INITIALIZE;
	
	MapLocation unitDock;
	
	int currWraith;
	int currDrone;
	double lastIncome;
	
	boolean rHasConstructor;
	boolean rHasSight;
	
	boolean rHasBlaster;
	boolean rHasRadar;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	
	MapLocation enemyLocation;
	int spawn = -1; // -1 means unknown
	int realSpawn = -1; // -1 means unknown
	
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
    			
    			Utility.setIndicator(myPlayer, 1, "INITIALIZE");
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
    						obj = RefineryBuildOrder.WAIT_FOR_DOCK; // I'm not the leader
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
    					obj = RefineryBuildOrder.WAIT_FOR_DOCK;
    					return;
    				}
    			}
    			return;
    			
    		case WAIT_FOR_DOCK:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
    			if ( Clock.getRoundNum() < 20 )
    			{
    				myPlayer.myRC.turnOff(); // I'm one of the first two refineries
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
    					currWraith = 0;
    					currDrone = 0;
    	    			obj = RefineryBuildOrder.EQUIP_WRAITHS;     // I am one of the first four near armory and with least ID
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP;             // I am one of the first four but not near armory
    			}
    			else if ( Clock.getRoundNum() > Constants.FACTORY_TIME )
	    			obj = RefineryBuildOrder.SLEEP;
    			return;
    			
    		case EQUIP_WRAITHS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_WRAITHS");
    			Utility.setIndicator(myPlayer, 2, "Equipping wraith " + Integer.toString(currWraith) + ".");
    			
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
				if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() )
				{
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
							myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_FLYER, spawn, currWraith, enemyLocation);
							currWraith++;
						}
					}
					return;
				}
				
				/*if ( currWraith % 5 == 4 )
					obj = RefineryBuildOrder.EQUIP_DRONES;*/
				return;
    		
    		case EQUIP_DRONES:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_DRONES");
    			Utility.setIndicator(myPlayer, 2, "Equipping drone " + Integer.toString(currDrone) + ".");
    			
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
				if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() )
				{
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
							myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_FLYER, spawn, currDrone, enemyLocation);
							currDrone++;
							obj = RefineryBuildOrder.EQUIP_WRAITHS;
						}
					}
					return;
				}
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
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}

}
