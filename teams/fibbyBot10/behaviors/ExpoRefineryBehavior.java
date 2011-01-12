package fibbyBot10.behaviors;

import battlecode.common.*;
import fibbyBot10.*;

public class ExpoRefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	MapLocation unitDock;

	Robot rFront;
	
	int isLeader = -1;
	int currFlyer;
	int currTank;
	int tanksToMake;
	double lastIncome;
	
	boolean rHasConstructor;
	boolean rHasSight;
	boolean rHasRadar;
	
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
    			if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
	    			obj = RefineryBuildOrder.SLEEP;
    			return;
    			
    		case EQUIP_FLYERS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_FLYERS");
    			Utility.setIndicator(myPlayer, 2, "Equipping flyer " + Integer.toString(currFlyer) + ".");
    			if ( currFlyer > Constants.MAX_FLYERS )
    			{
    				obj = RefineryBuildOrder.WAIT_FOR_HANBANG;
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
    			Utility.setIndicator(myPlayer, 2, "Equipping tank " + Integer.toString(currTank) + " out of " + Integer.toString(tanksToMake));
    			tanksToMake += Math.max(Constants.TANKS_PER_EXPO * (int)Math.floor(myPlayer.mySensor.senseIncome(myPlayer.myRC.getRobot()) - lastIncome), 0);
    			if ( currTank < tanksToMake )
	    			myPlayer.myMessenger.sendNotice(MsgType.MSG_START_TANKS);
    			else
    				myPlayer.myMessenger.sendNotice(MsgType.MSG_STOP_TANKS);
    			for ( RobotInfo rInfo : myPlayer.myScanner.scannedRobotInfos )
    			{
    				if ( rInfo.chassis == Chassis.MEDIUM && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() && rInfo.location.equals(unitDock) )
    				{
    					rHasRadar = false;
    					for ( ComponentType c : rInfo.components )
    					{
    						if ( c == ComponentType.RADAR )
    							rHasRadar = true;
    					}
    					if ( !rHasRadar )
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND);
						myPlayer.sleep();
						myPlayer.myMessenger.sendInt(MsgType.MSG_SEND_NUM, currTank);
						currTank++;
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
		return "ExpoRefineryBehavior";
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
	}
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}

}
