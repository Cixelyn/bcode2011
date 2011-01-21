package team068.behaviors;

import battlecode.common.*;
import team068.*;
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
	
	int currWraith;
	int currDrone;
	int currHeavy;
	int currUnit;
	double lastIncome;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	int babyWraith;
	int babyDrone;
	int babyHeavy;

	boolean rHasConstructor;
	boolean rHasSight;
	
	int rNumSMGs;
	int rNumShields;
	int rNumBlasters;
	boolean rHasRadar;
	
	boolean hasSlept = false;
	
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
    			Utility.setIndicator(myPlayer, 2, "");
    			if ( Clock.getRoundNum() < 200 ) // I'm one of the first four refineries (change to 10 for first two, 80 for first 3)
    				obj = RefineryBuildOrder.DETERMINE_LEADER;
    			else
    				obj = RefineryBuildOrder.SLEEP;
    			return;
    			
    		case DETERMINE_LEADER:
    			
    			Utility.setIndicator(myPlayer, 1, "DETERMINE_LEADER");
    			Utility.setIndicator(myPlayer, 2, "");
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
    			Utility.setIndicator(myPlayer, 2, "");
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
    			Utility.setIndicator(myPlayer, 2, "");
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
    			return;
    			
    		case EQUIP_UNIT:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_UNIT");
    			Utility.setIndicator(myPlayer, 2, "Idle.");
    			
    			// check what unit should be made
    			currUnit = currWraith + currDrone + currHeavy;
    			if ( currUnit == 1 )
    			{
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    				if ( r != null && r.getID() != babyWraith )
    				{
    					Utility.setIndicator(myPlayer, 2, "Equipping wraith.");
    					babyWraith = r.getID();
    					obj = RefineryBuildOrder.EQUIP_WRAITH;
    				}
    			}
    			else if ( currUnit % 3 == 2 )
    			{
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    				if ( r != null && r.getID() != babyDrone )
    				{
    					Utility.setIndicator(myPlayer, 2, "Equipping drone.");
    					babyDrone = r.getID();
    					obj = RefineryBuildOrder.EQUIP_DRONE;
    				}
    			}
    			else
    			{
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    				if ( r != null && r.getID() != babyHeavy )
    				{
    					Utility.setIndicator(myPlayer, 2, "Equipping heavy.");
    					babyHeavy = r.getID();
    					obj = RefineryBuildOrder.EQUIP_HEAVY;
    				}
    			}
    			return;
    			
    		case EQUIP_HEAVY:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_HEAVY");
				Utility.setIndicator(myPlayer, 2, "Equipping heavy " + Integer.toString(currHeavy) + ".");
    			
				r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r == null || r.getID() != babyHeavy )
    			{
    				obj = RefineryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
    			if ( currHeavy == 0 )
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
							myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM_HEAVY, -1, currHeavy, null);
							currHeavy++;
							obj = RefineryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			else if ( currHeavy % 3 == 0 )
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
					if ( rNumSMGs < 3 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SMG, RobotLevel.ON_GROUND);
					else if ( rNumShields < 1 )
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
    			}
    			else if ( currHeavy % 3 == 1 )
    			{
    				rHasRadar = false;
					rNumShields = 0;
					rNumBlasters = 0;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.RADAR )
							rHasRadar = true;
						if ( c == ComponentType.SHIELD )
							rNumShields++;
						if ( c == ComponentType.BLASTER )
							rNumBlasters++;
					}
					if ( rNumBlasters < 1 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND);
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
    			}
    			else if ( currHeavy % 3 == 2 )
    			{
    				rHasRadar = false;
    				rNumBlasters = 0;
					rNumSMGs = 0;
					rNumShields = 0;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.RADAR )
							rHasRadar = true;
						if ( c == ComponentType.BLASTER )
							rNumBlasters++;
						if ( c == ComponentType.SMG )
							rNumSMGs++;
						if ( c == ComponentType.SHIELD )
							rNumShields++;
					}
					if ( rNumBlasters < 3 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND);
					else if ( rNumSMGs < 1 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SMG, RobotLevel.ON_GROUND);
					else if ( rNumShields < 1 )
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
		obj = RefineryBuildOrder.WAIT_FOR_DOCK;
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}

}
