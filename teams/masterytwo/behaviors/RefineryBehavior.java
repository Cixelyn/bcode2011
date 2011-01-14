package masterytwo.behaviors;

import battlecode.common.*;
import masterytwo.*;

public class RefineryBehavior extends Behavior
{
	
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
	
	Robot babyMarine;
	Robot rFront;
	RobotInfo rInfo;
	
	MapLocation enemyLocation;
	int spawn = -1; // -1 means unknown
	
	int parentID = -1; // -1 means unknown
	int rally = -1; // -1 means unknown
	
	public RefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
		
			case WAIT_FOR_RALLY:
				
				Utility.setIndicator(myPlayer, 1, "WAIT_FOR_RALLY");
				if ( rally == -1 && Clock.getRoundNum() - myPlayer.myBirthday < Constants.RALLY_WAIT )
				{
					for ( RobotInfo rInfo : myPlayer.myScanner.scannedRobotInfos )
					{
						if ( rInfo.chassis == Chassis.LIGHT )
						{
							for ( ComponentType c : rInfo.components )
							{
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
							if ( rally != -1 ) // this is bad, two flyers facing me have been found, wait longer plz
							{
								rally = -1;
								parentID = -1;
								return;
							}
							rally = myPlayer.myRC.getLocation().directionTo(rInfo.location).ordinal();
							parentID = rInfo.robot.getID();
						}
					}
				}
				else // rally has been determined or timeout
				{
					if ( rally != -1 )
					{
						while ( myPlayer.myMotor.isActive() )
							myPlayer.sleep();
						myPlayer.myMotor.setDirection(Direction.values()[rally]);
						Utility.setIndicator(myPlayer, 0, "I am an expo refinery, rally set: " + Direction.values()[rally].toString());
					}
					else
						Utility.setIndicator(myPlayer, 0, "I am an expo refinery, rally not known.");
					obj = RefineryBuildOrder.EQUIPPING;
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
    			for ( RobotInfo rInfo : myPlayer.myScanner.scannedRobotInfos )
    			{
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
    				while ( myPlayer.myMotor.isActive() )
    					myPlayer.sleep();
    				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) <= ComponentType.CONSTRUCTOR.range )
    				{
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    					currFlyer = 0;
    					obj = RefineryBuildOrder.EQUIP_FLYERS;
    				}
    				else
    					obj = RefineryBuildOrder.WAIT_FOR_HANBANG;
    			}
    			else if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
	    			obj = RefineryBuildOrder.WAIT_FOR_HANBANG;
    			return;
    			
    		case EQUIP_FLYERS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_FLYERS");
    			Utility.setIndicator(myPlayer, 2, "Equipping flyer " + Integer.toString(currFlyer) + " out of " + Integer.toString(Constants.MAX_FLYERS) + ".");
    			if ( currFlyer > Constants.MAX_FLYERS )
					obj = RefineryBuildOrder.WAIT_FOR_HANBANG;
    			for ( RobotInfo rInfo : myPlayer.myScanner.scannedRobotInfos )
    			{
    				if ( rInfo.chassis == Chassis.FLYING && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() && rInfo.location.equals(unitDock) )
    				{
    					rHasConstructor = false;
    					rHasSight = false;
    					for ( ComponentType c : rInfo.components )
    					{
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
    				for ( ComponentType c : rInfo.components )
    				{
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
    						myPlayer.myMessenger.sendIntLoc(MsgType.MSG_ENEMY_LOC, spawn, enemyLocation);
    					obj = RefineryBuildOrder.MAKE_MARINE;
    				}
    			}
	    		else
	    			obj = RefineryBuildOrder.MAKE_MARINE;
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
				Utility.setIndicator(myPlayer, 0, "We spawned " + Utility.spawnString(spawn) + ".");
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
