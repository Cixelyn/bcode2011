package fibbyBot13.behaviors;

import battlecode.common.*;
import fibbyBot13.*;
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
		EQUIP_DRONE,
		MAKE_REFINERY,
		SLEEP
	}
	
	RefineryBuildOrder obj = RefineryBuildOrder.INITIALIZE;
	
	MapLocation unitDock;
	Direction d;
	
	int currDrone;
	double lastIncome;
	
	boolean rHasConstructor;
	boolean rHasSight;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	int babyDrone;
	
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
    					Utility.buildComponent(myPlayer, myPlayer.myRecycler, myPlayer.myRC.getLocation().directionTo(rInfo.location), ComponentType.ANTENNA, RobotLevel.ON_GROUND);
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
    	    			Utility.buildComponent(myPlayer, myPlayer.myRecycler, Direction.OMNI, ComponentType.ANTENNA, RobotLevel.ON_GROUND);
    	    			while ( myPlayer.myMotor.isActive() )
        					myPlayer.sleep();
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    					currDrone = 0;
    	    			obj = RefineryBuildOrder.EQUIP_UNIT;        // I am one of the first two capped and with least ID
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP;             // I am one of the first two capped but not near armory
    			}
    			else if ( Clock.getRoundNum() > 200 )
	    			obj = RefineryBuildOrder.MAKE_REFINERY;                 // I am not one of the first two capped
    			return;
    			
    		case EQUIP_UNIT:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_UNIT");
    			
    			if ( currDrone >= Constants.MAX_DRONES )
    				obj = RefineryBuildOrder.MAKE_REFINERY;
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() && r.getID() != babyDrone )
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
					Utility.setIndicator(myPlayer, 2, "Drone found.");
					babyDrone = r.getID();
					obj = RefineryBuildOrder.EQUIP_DRONE;
    				return;
    			}
    			
    			Utility.setIndicator(myPlayer, 2, "No units to equip.");
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
					Utility.tryBuildComponent(myPlayer, myPlayer.myRecycler, myPlayer.myRC.getDirection(), ComponentType.CONSTRUCTOR, RobotLevel.IN_AIR);
				else if ( !rHasSight )
				{
					if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRecycler, myPlayer.myRC.getDirection(), ComponentType.SIGHT, RobotLevel.IN_AIR) )
					{
						myPlayer.sleep(); // NECESSARY TO GIVE FLYER TIME TO REALIZE WHO HE IS
						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_FLYER, -1, currDrone, null);
						currDrone++;
						obj = RefineryBuildOrder.EQUIP_UNIT;
					}
				}
				return;
				
    		case MAKE_REFINERY:
    			
    			Utility.setIndicator(myPlayer, 1, "MAKE_REFINERY");
    			Utility.setIndicator(myPlayer, 2, "");
    			while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.CONSTRUCTOR.cost + ComponentType.RECYCLER.cost + 2*Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildComponent(myPlayer, myPlayer.myRecycler, Direction.OMNI, ComponentType.CONSTRUCTOR, RobotLevel.ON_GROUND);
				while ( myPlayer.myConstructor == null || myPlayer.myConstructor.isActive() )
					myPlayer.sleep();
    			for ( int i = Direction.values().length ; --i >= 0 ; )
				{
					d = Direction.values()[i];
					if ( d != Direction.OMNI && d != Direction.NONE && myPlayer.myMotor.canMove(d) )
					{
						Utility.setIndicator(myPlayer, 2, "Building!");
        				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.CONSTRUCTOR.cost + ComponentType.RECYCLER.cost + 3*Constants.RESERVE )
							myPlayer.sleep();
        				Utility.buildChassis(myPlayer, myPlayer.myConstructor, d, Chassis.BUILDING);
        				Utility.buildComponent(myPlayer, myPlayer.myConstructor, d, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
        				Utility.buildComponent(myPlayer, myPlayer.myRecycler, d, ComponentType.CONSTRUCTOR, RobotLevel.ON_GROUND);
						return;
					}
				}
    			obj = RefineryBuildOrder.SLEEP;
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
