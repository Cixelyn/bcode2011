package masterytwo.behaviors;

import battlecode.common.*;
import masterytwo.*;

public class ExpoRefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	MapLocation unitDock;

	Robot rFront;
	
	int currFlyer;
	int currTank;
	int tanksToMake;
	double lastIncome;
	
	boolean rHasConstructor;
	boolean rHasSight;
	
	int rNumProcessors;
	int rNumBlasters;
	boolean rHasSMG;
	boolean rHasRadar;
	boolean rHasAntenna;
	
	MapLocation enemyLocation;
	int spawn = -1;
	boolean tanksStopped = false;
	boolean flyersRemade = false;
	
	public ExpoRefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
		
    		case EQUIPPING:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIPPING");
    			Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.ANTENNA, RobotLevel.ON_GROUND);
    			obj = RefineryBuildOrder.WAIT_FOR_DOCK;
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
    					obj = RefineryBuildOrder.SLEEP;
    			}
    			else if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
	    			obj = RefineryBuildOrder.SLEEP;
    			return;
    			
    		case EQUIP_FLYERS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_FLYERS");
    			Utility.setIndicator(myPlayer, 2, "Equipping flyer " + Integer.toString(currFlyer) + " out of " + Integer.toString(Constants.MAX_FLYERS) + ".");
    			if ( currFlyer > Constants.MAX_FLYERS )
    			{
    				if ( !flyersRemade )
    				{
    					obj = RefineryBuildOrder.WAIT_FOR_HANBANG;
    					flyersRemade = true;
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP;
    			}
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
    			{
    				currTank = 0;
    				obj = RefineryBuildOrder.EQUIP_TANKS;
    			}
    			lastIncome = myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot());
    			tanksToMake = Constants.TANKS_PER_EXPO * (int)Math.floor(lastIncome - 5);
    			return;
    			
    		case EQUIP_TANKS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_TANKS");
    			Utility.setIndicator(myPlayer, 2, "Equipping tank " + Integer.toString(currTank) + " out of " + Integer.toString(tanksToMake) + ".");
    			
    			if ( currTank == tanksToMake - 1 )
					myPlayer.myMessenger.sendNotice(MsgType.MSG_STOP_TANKS);
    			
    			if ( currTank >= tanksToMake )
    			{
    				if ( !tanksStopped )
    				{
    					tanksStopped = true;
    					currFlyer = 0;
    					obj = RefineryBuildOrder.EQUIP_FLYERS;
    				}
    				else
    					obj = RefineryBuildOrder.SLEEP;
    			}
    			else
    			{	
					if ( myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot()) > lastIncome )
	    				tanksToMake += Constants.TANKS_PER_EXPO;
	    			for ( RobotInfo rInfo : myPlayer.myScanner.scannedRobotInfos )
	    			{
	    				if ( rInfo.chassis == Chassis.MEDIUM && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() && rInfo.location.equals(unitDock) )
	    				{
	    					rNumProcessors = 0;
	    					rNumBlasters = 0;
	    					rHasSMG = false;
	    					rHasRadar = false;
	    					rHasAntenna = false;
	    					for ( ComponentType c : rInfo.components )
	    					{
	    						if ( c == ComponentType.PROCESSOR )
	    							rNumProcessors++;
	    						if ( c == ComponentType.BLASTER )
	    							rNumBlasters++;
	    						if ( c == ComponentType.RADAR )
	    							rHasRadar = true;
	    						if ( c == ComponentType.ANTENNA )
	    							rHasAntenna = true;
	    					}
	    					if ( rNumProcessors < 0 )
	    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.PROCESSOR, RobotLevel.ON_GROUND);
	    					else if ( rNumBlasters < 2 )
	    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND);
	    					else if ( !rHasRadar )
	    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND);
	    					else if ( !rHasAntenna )
	    					{
	    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.ANTENNA, RobotLevel.ON_GROUND);
	    						myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_SEND_NUM, spawn, currTank, enemyLocation);
	    						currTank++;
	    					}
							myPlayer.sleep();
							lastIncome = myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot());
	    					return;
	    				}
	    			}
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
		return "ExpoRefineryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_DOCK )
			unitDock = msg.locations[Messenger.firstData];
		if ( t == MsgType.MSG_SEND_NUM )
		{
			currFlyer++;
			currTank++;
		}
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
