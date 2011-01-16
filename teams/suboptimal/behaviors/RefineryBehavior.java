package suboptimal.behaviors;

import battlecode.common.*;
import suboptimal.*;
import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	
	private enum RefineryBuildOrder 
	{
		WAIT_FOR_RALLY,
		EQUIPPING,
		GIVE_ANTENNA,
		DETERMINE_LEADER,
		WAIT_FOR_DOCK,
		EQUIP_UNITS,
		SLEEP
	}
	
	
	RefineryBuildOrder obj = RefineryBuildOrder.WAIT_FOR_RALLY;
	
	MapLocation unitDock;
	
	int isLeader = -1; // -1 means unknown, 0 means no, 1 means yes
	int currFlyer;
	int currHeavy;
	double lastIncome;
	
	boolean rHasConstructor;
	boolean rHasSight;
	
	int rNumBlasters;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	
	MapLocation enemyLocation;
	int spawn = -1; // -1 means unknown
	int realSpawn = -1; // -1 means unknown
	
	final Random random = new Random();
	
	public RefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		Utility.setIndicator(myPlayer, 2, "Current direction: " + myPlayer.myRC.getDirection().toString());
		
		switch(obj)
    	{
		
			case WAIT_FOR_RALLY:
				
				Utility.setIndicator(myPlayer, 1, "WAIT_FOR_RALLY");
				
				nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
				if ( spawn == -1 && Clock.getRoundNum() - myPlayer.myBirthday < Constants.RALLY_WAIT )
				{
					for ( int i = nearbyRobots.length - 1 ; i >= 0 ; i-- )
					{
						r = nearbyRobots[i];
						rInfo = myPlayer.mySensor.senseRobotInfo(r);
						if ( rInfo.chassis == Chassis.LIGHT )
						{
							for ( int j = rInfo.components.length - 1 ; j >= 0 ; j-- )
							{
								c = rInfo.components[j];
								if ( c == ComponentType.CONSTRUCTOR ) // initial SCV found, I should not wait for rally
								{
									spawn = 0;
									Utility.setIndicator(myPlayer, 0, "I am one of the first four refineries.");
									obj = RefineryBuildOrder.EQUIPPING;
									return;
								}
							}
						}
						if ( rInfo.chassis == Chassis.FLYING && rInfo.direction == rInfo.location.directionTo(myPlayer.myRC.getLocation()) )
						{
							if ( spawn != -1 ) // this is bad, two flyers facing me have been found, wait longer plz
							{
								spawn = -1;
								enemyLocation = null;
								return;
							}
							spawn = myPlayer.myRC.getLocation().directionTo(rInfo.location).ordinal(); // opposite the rally
							enemyLocation = Utility.spawnOpposite(myPlayer.myRC.getLocation(), spawn);
						}
					}
				}
				else // rally has been determined or timeout
				{
					if ( spawn != -1 )
					{
						while ( myPlayer.myMotor.isActive() )
							myPlayer.sleep();
						myPlayer.myMotor.setDirection(Direction.values()[spawn]);
						Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ".");
						obj = RefineryBuildOrder.EQUIPPING;
					}
					else
					{
						//spawn = random.nextInt();
						//Utility.setIndicator(myPlayer, 0, "I think we spawned center, arbitrarily choosing: " + Direction.values()[spawn].toString() + ".");
						Utility.setIndicator(myPlayer, 0, "Spawn could not be determined. Shutting down.");
						obj = RefineryBuildOrder.SLEEP;
					}
				}
				return;
			
    		case EQUIPPING:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIPPING");
    			Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.ANTENNA, RobotLevel.ON_GROUND);
    			if ( Clock.getRoundNum() < 5 ) // I'm one of the first two refineries
    				obj = RefineryBuildOrder.DETERMINE_LEADER;
    			else
    				obj = RefineryBuildOrder.WAIT_FOR_DOCK;
    			return;
    			
    		case DETERMINE_LEADER:
    			
    			Utility.setIndicator(myPlayer, 1, "DETERMINE_LEADER");
    			myPlayer.myMessenger.sendInt(MsgType.MSG_SEND_ID, myPlayer.myRC.getRobot().getID());
    			if ( isLeader == 1 )
	    			obj = RefineryBuildOrder.GIVE_ANTENNA;
    			if ( isLeader == 0 )
    				obj = RefineryBuildOrder.WAIT_FOR_DOCK;
    			return;
    			
    		case GIVE_ANTENNA:
    			
    			Utility.setIndicator(myPlayer, 1, "GIVE_ANTENNA");
    			
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
    			for ( int i = nearbyRobots.length - 1 ; i >= 0 ; i-- )
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
    			if ( unitDock != null )
    			{
    				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) <= ComponentType.CONSTRUCTOR.range )
    				{
    					while ( myPlayer.myMotor.isActive() )
        					myPlayer.sleep();
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    					currFlyer = 0;
    					obj = RefineryBuildOrder.EQUIP_UNITS;
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP; // I am one of the first four but not near armory
    			}
    			else if ( Clock.getRoundNum() > Constants.FACTORY_TIME )
	    			obj = RefineryBuildOrder.SLEEP; // TODO switch to tower equipping state
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
						rNumBlasters = 0;
						for ( int j = rInfo.components.length - 1 ; j >= 0 ; j-- )
						{
							c = rInfo.components[j];
							if ( c == ComponentType.BLASTER )
								rNumBlasters++;
						}
						if ( rNumBlasters == 0 )
							Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND);
						else if ( rNumBlasters == 1 )
						{
							if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND) )
							{
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
					for ( int j = rInfo.components.length - 1 ; j >= 0 ; j-- )
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
		if ( t == MsgType.MSG_SEND_ID )
		{
			if ( msg.ints[Messenger.firstData] < myPlayer.myRC.getRobot().getID() )
				isLeader = 0;
			else
				isLeader = 1;
		}
		if ( t == MsgType.MSG_SEND_DOCK )
			unitDock = msg.locations[Messenger.firstData];
		if ( t == MsgType.MSG_SEND_NUM_FLYER )
			currFlyer++;
		if ( t == MsgType.MSG_SEND_NUM_HEAVY )
			currHeavy++;
	}
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}

}
