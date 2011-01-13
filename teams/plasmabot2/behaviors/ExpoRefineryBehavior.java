package plasmabot2.behaviors;

import battlecode.common.*;
import plasmabot2.*;

public class ExpoRefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	MapLocation unitDock;

	Robot rFront;
	
	int isLeader = -1;
	int currFlyer = 0;
	
	boolean hasAntenna = false;
	int rBlasters;
	boolean rHasConstructor;
	boolean rHasShield;
	boolean rHasSight;
	boolean rHasRadar;
	int rSMGS;
	
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
    					obj = RefineryBuildOrder.EQUIP_FLYERS;
    				}
    				else
    					obj = RefineryBuildOrder.WAIT_FOR_HANBANG;
    			}
    			if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
	    			obj = RefineryBuildOrder.EQUIP_DRAGOON;
    			return;
    			
    		case EQUIP_FLYERS:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_FLYERS");
    			Utility.setIndicator(myPlayer, 2, "Equipping flyer " + Integer.toString(currFlyer) + ".");
    			if ( currFlyer > Constants.MAX_FLYERS )
    			{
    				obj = RefineryBuildOrder.EQUIP_DRAGOON;
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
    				obj = RefineryBuildOrder.EQUIP_DRAGOON;
    			return;
    			
    		case BUILD_MARINE:
    			
    			Utility.setIndicator(myPlayer, 1, "BUILD_MARINE");
    			rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			while ( rFront != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE + 5 || myPlayer.myRC.getTeamResources() < myPlayer.myLastRes + Chassis.LIGHT.upkeep + Chassis.BUILDING.upkeep )
    			{
    				if ( !myPlayer.myMotor.isActive() )
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
    				myPlayer.sleep();
    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			}
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.LIGHT);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SHIELD, RobotLevel.ON_GROUND);
    			return;
    			
    		case EQUIP_DRAGOON:
    			Utility.setIndicator(myPlayer, 1, "EQUIP_DRAGOON");
    			for ( RobotInfo rInfo : myPlayer.myScanner.scannedRobotInfos )
    			{
    				if ( rInfo.chassis == Chassis.MEDIUM && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() && rInfo.location.equals(unitDock) )
    				{
    					rHasRadar = false;
    					rSMGS=0;
    					rBlasters=0;
    					for ( ComponentType c : rInfo.components )
    					{
    						if ( c == ComponentType.BLASTER) {
    							rBlasters=rBlasters+1;
    						}
    						if ( c == ComponentType.SMG )
    							rSMGS=rSMGS+1;
    						if ( c == ComponentType.RADAR )
    							rHasRadar = true;
    					}
    					if (rSMGS<1) { 
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SMG, RobotLevel.ON_GROUND);
    					}
    					else if (rBlasters<1) { 
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.BLASTER, RobotLevel.ON_GROUND);
    					}
    					else if ( !rHasRadar )
    					{
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RADAR, RobotLevel.ON_GROUND);
    						myPlayer.sleep();
    						myPlayer.myMessenger.sendInt(MsgType.MSG_SEND_NUM, currFlyer);
    						currFlyer++;
    					}
    					return;
    				}
    			}
    			
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
			currFlyer++;
	}
	public void onWakeupCallback(int lastActiveRound) {}
	public void onDamageCallback(double damageTaken) {}

}
