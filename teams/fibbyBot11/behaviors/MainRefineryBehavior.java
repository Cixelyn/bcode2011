package fibbyBot11.behaviors;

import battlecode.common.*;
import fibbyBot11.*;

public class MainRefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	MapLocation unitDock;
	
	Robot rFront;
	
	int isLeader = -1; // -1 means unknown, 0 means no, 1 means yes
	int currFlyer;
	int currTank;
	int tanksToMake;
	double lastIncome;
	
	boolean hasAntenna = false;
	
	boolean rHasConstructor;
	boolean rHasSight;
	
	int rNumProcessors;
	int rNumBlasters;
	boolean rHasSMG;
	boolean rHasRadar;
	boolean rHasAntenna;
	
	MapLocation enemyLocation;
	int spawn = -1;
	boolean eeHanTiming = false;
	
	public MainRefineryBehavior(RobotPlayer player)
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
    			obj = RefineryBuildOrder.DETERMINE_LEADER;
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
    					obj = RefineryBuildOrder.SLEEP;
    			}
    			if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
	    			obj = RefineryBuildOrder.SLEEP;
    			return;
    			
    		case EQUIP_FLYERS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_FLYERS");
    			Utility.setIndicator(myPlayer, 2, "Equipping tank " + Integer.toString(currTank) + " out of " + Integer.toString(tanksToMake) + ".");
    			if ( currFlyer > Constants.MAX_FLYERS )
    			{
    				obj = RefineryBuildOrder.SLEEP;
    				return;
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
    						myPlayer.myMessenger.sendInt(MsgType.MSG_SEND_NUM, currFlyer);
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
    			
    			Utility.setIndicator(myPlayer, 0, "Last: " + Double.toString(lastIncome) + " , Current: " + Double.toString(myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot())));
    			Utility.setIndicator(myPlayer, 1, "EQUIP_TANKS");
    			Utility.setIndicator(myPlayer, 2, "Equipping tank " + Integer.toString(currTank) + " out of " + Integer.toString(tanksToMake) + ".");
    			
    			if ( myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot()) > lastIncome )
    				tanksToMake += Constants.TANKS_PER_EXPO;
    			
    			if ( currTank < tanksToMake )
	    			myPlayer.myMessenger.sendNotice(MsgType.MSG_START_TANKS);
    			else
    				myPlayer.myMessenger.sendNotice(MsgType.MSG_STOP_TANKS);
    			
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
    						myPlayer.myMessenger.sendInt(MsgType.MSG_SEND_NUM, currTank);
    						if ( eeHanTiming )
    							myPlayer.myMessenger.sendIntLoc(MsgType.MSG_ENEMY_LOC, spawn, enemyLocation);
    						currTank++;
    					}
						myPlayer.sleep();
						lastIncome = myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot());
    					return;
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
		{
			currFlyer++;
			currTank++;
		}
		if (t == MsgType.MSG_ENEMY_LOC)
		{
			spawn = msg.ints[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData];
			Utility.setIndicator(myPlayer, 0, "We spawned " + Utility.spawnString(spawn) + ".");
			if ( !eeHanTiming )
				myPlayer.myMessenger.sendIntLoc(MsgType.MSG_ENEMY_LOC, spawn, enemyLocation);
			eeHanTiming = true;
		}
	}
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}

}
