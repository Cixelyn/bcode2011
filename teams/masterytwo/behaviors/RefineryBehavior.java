package masterytwo.behaviors;

import battlecode.common.*;
import masterytwo.*;
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
		EQUIP_FLYERS,
		WAIT_FOR_HANBANG,
		MAKE_MARINE,
		EQUIP_MARINE,
		SLEEP
	}
	
	
	RefineryBuildOrder obj = RefineryBuildOrder.WAIT_FOR_RALLY;
	
	MapLocation unitDock;
	
	int isLeader = -1; // -1 means unknown, 0 means no, 1 means yes
	int currFlyer;
	double lastIncome;
	
	boolean rHasConstructor;
	boolean rHasSight;
	
	boolean rHasBlaster;
	boolean rHasRadar;
	boolean rHasAntenna;
	
	Robot[] nearbyRobots;
	Robot babyMarine;
	Robot rFront;
	RobotInfo rInfo;
	
	MapLocation enemyLocation;
	int spawn = -1; // -1 means unknown
	
	final Random random = new Random();
	
	boolean flyerRemake = false;
	
	Robot r;
	ComponentType c;
	
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
							spawn = (myPlayer.myRC.getLocation().directionTo(rInfo.location).ordinal() + 4) % 8; // opposite the rally
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
						myPlayer.myMotor.setDirection(Direction.values()[(spawn + 4) % 8]);
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
    			if ( Clock.getRoundNum() < 5 ) // I'm a main refinery
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
    					obj = RefineryBuildOrder.EQUIP_FLYERS;
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP; // I am one of the first four but not near armory
    			}
    			else if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
	    			obj = RefineryBuildOrder.WAIT_FOR_HANBANG;
    			return;
    			
    		case EQUIP_FLYERS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_FLYERS");
    			Utility.setIndicator(myPlayer, 2, "Equipping flyer " + Integer.toString(currFlyer) + " out of " + Integer.toString(Constants.MAX_FLYERS) + ".");
    			if ( currFlyer > Constants.MAX_FLYERS )
    			{
    				if ( !flyerRemake )
    				{
    					flyerRemake = true;
    					obj = RefineryBuildOrder.WAIT_FOR_HANBANG;
    				}
    				else
    					obj = RefineryBuildOrder.MAKE_MARINE;
    			}
    			
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
    			for ( int i = nearbyRobots.length - 1 ; i >= 0 ; i-- )
    			{
    				r = nearbyRobots[i];
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if ( rInfo.chassis == Chassis.FLYING && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() && rInfo.location.equals(unitDock) )
    				{
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
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.CONSTRUCTOR, RobotLevel.IN_AIR);
    					else if ( !rHasSight )
    					{
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SIGHT, RobotLevel.IN_AIR);
    						myPlayer.sleep();
    						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM, spawn, currFlyer, enemyLocation);
    						currFlyer++;
    					}
    					return;
    				}
    			}
    			return;
    			
    		case WAIT_FOR_HANBANG:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_HANBANG");
    			Utility.setIndicator(myPlayer, 2, "");
    			if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
    				obj = RefineryBuildOrder.MAKE_MARINE;
    			lastIncome = myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot());
    			return;
    			
    		case MAKE_MARINE:
    			
    			Utility.setIndicator(myPlayer, 1, "MAKE_MARINE");
    			if ( unitDock != null && Clock.getRoundNum() > Constants.REMAKE_FLYER_TIME )
    			{
    				while ( myPlayer.myMotor.isActive() )
    					myPlayer.sleep();
					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    				currFlyer = 0;
    				obj = RefineryBuildOrder.EQUIP_FLYERS;
    				return;
    			}
    			if ( myPlayer.myMotor.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE + 5 || myPlayer.myRC.getTeamResources() - myPlayer.myLastRes < Chassis.BUILDING.upkeep + Chassis.LIGHT.upkeep * Math.max((int)Math.floor(lastIncome) - 6, 0) )
    				myPlayer.sleep();
    			else if ( !myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) || myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.MINE) != null )
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.sleep();
    			}
    			else
    			{
					Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.LIGHT);
					babyMarine = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
					obj = RefineryBuildOrder.EQUIP_MARINE;
    			}
    			lastIncome = myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot());
    			return;
    			
    		case EQUIP_MARINE:
    			
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MARINE");
    			rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			if ( rFront == babyMarine )
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(rFront);
    				rHasBlaster = false;
    				rHasRadar = false;
    				rHasAntenna = false;
    				for ( int i = rInfo.components.length - 1 ; i >= 0 ; i-- )
    				{
    					c = rInfo.components[i];
    					if ( c == ComponentType.BLASTER )
    						rHasBlaster = true;
    					if ( c == ComponentType.RADAR )
    						rHasRadar = true;
    					if ( c == ComponentType.ANTENNA )
    						rHasAntenna = true;
    				}
    				if ( !rHasBlaster )
    					Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND);
    				else if ( !rHasRadar )
    					Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND);
    				else if ( !rHasAntenna )
    					Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.ANTENNA, RobotLevel.ON_GROUND);
    				else
    				{
    					if ( spawn != -1 )
    					{
    						myPlayer.myMessenger.sendIntLoc(MsgType.MSG_ENEMY_LOC, spawn, enemyLocation);
    						if ( myPlayer.myRC.getDirection() != Direction.values()[spawn] )
    						{
		    					while ( myPlayer.myMotor.isActive() )
									myPlayer.sleep();
		    					myPlayer.myMotor.setDirection(Direction.values()[(spawn + 4) % 8]);
    						}
    					}
    					obj = RefineryBuildOrder.MAKE_MARINE;
    				}
    			}
	    		else
	    		{
	    			if ( spawn != -1 && myPlayer.myRC.getDirection() != Direction.values()[spawn] )
	    			{
		    			while ( myPlayer.myMotor.isActive() )
							myPlayer.sleep();
		    			myPlayer.myMotor.setDirection(Direction.values()[(spawn + 4) % 8]);
	    			}
	    			obj = RefineryBuildOrder.MAKE_MARINE;
	    		}
    			lastIncome = myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot());
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
		if ( t == MsgType.MSG_SEND_NUM )
			currFlyer++;
		if (t == MsgType.MSG_ENEMY_LOC)
		{
			if ( spawn == -1 )
			{
				spawn = msg.ints[Messenger.firstData];
				enemyLocation = msg.locations[Messenger.firstData];
				if ( spawn != -1 )
					Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ".");
				else
					Utility.setIndicator(myPlayer, 0, "I think we spawned center.");
					
				myPlayer.myMessenger.sendIntLoc(MsgType.MSG_ENEMY_LOC, spawn, enemyLocation);
			}
		}
	}
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}

}
