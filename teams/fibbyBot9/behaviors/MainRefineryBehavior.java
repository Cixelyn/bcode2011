package fibbyBot9.behaviors;

import battlecode.common.*;
import fibbyBot9.*;

public class MainRefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	MapLocation unitDock;
	
	Robot rFront;
	
	int isLeader = -1; // -1 means unknown, 0 means no, 1 means yes
	int currFlyer = 0;
	
	int rNumBlasters;
	boolean rHasConstructor;
	boolean rHasSight;
	
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
    				obj = RefineryBuildOrder.EQUIP_WRAITHS;
    				currFlyer = 0;
    			}
    			return;
    			
    		case EQUIP_WRAITHS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_WRAITHS");
    			Utility.setIndicator(myPlayer, 2, "Equipping wraith " + Integer.toString(currFlyer) + ".");

    			for ( RobotInfo rInfo : myPlayer.myScanner.scannedRobotInfos )
    			{
    				if ( rInfo.chassis == Chassis.FLYING && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() && rInfo.location.equals(unitDock) )
    				{
    					rNumBlasters = 0;
    					rHasSight = false;
    					for ( ComponentType c : rInfo.components )
    					{
    						if ( c == ComponentType.BLASTER )
    							rNumBlasters++;
    						if ( c == ComponentType.SIGHT )
    							rHasSight = true;
    					}
    					if ( rNumBlasters < 2 )
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.IN_AIR);
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
    			
    		case SLEEP:
    			
    			Utility.setIndicator(myPlayer, 1, "SLEEP");
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
	}
	public void onWakeupCallback(int lastActiveRound) {}

}
