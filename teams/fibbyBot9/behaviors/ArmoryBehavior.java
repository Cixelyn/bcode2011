package fibbyBot9.behaviors;

import battlecode.common.*;
import fibbyBot9.*;

public class ArmoryBehavior extends Behavior
{
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	Robot rFront;
	
	int currFlyer = 0;
	
	public ArmoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
    			
    		case WAIT_FOR_DOCK:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
    			if ( unitDock != null )
    			{
    				while ( myPlayer.myMotor.isActive() )
    					myPlayer.sleep();
    				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) <= ComponentType.ARMORY.range )
    				{
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    					myPlayer.sleep();
        				obj = ArmoryBuildOrder.BUILD_FLYERS;
    				}
    			}
    			return;
    			
    		case BUILD_FLYERS:
    			
    			Utility.setIndicator(myPlayer, 1, "BUILD_FLYERS");
    			Utility.setIndicator(myPlayer, 2, "Building flyer " + Integer.toString(currFlyer) + ".");
    			if ( currFlyer > Constants.MAX_FLYERS )
	    			obj = ArmoryBuildOrder.WAIT_FOR_HANBANG;
    			else
    			{
					rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
	    			while ( rFront != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE + 5 || myPlayer.myRC.getTeamResources() < myPlayer.myLastRes + Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep )
	    			{
	    				myPlayer.sleep();
	    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
	    			}
	    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
	    			currFlyer++;
    			}
    			return;
    			
    		case WAIT_FOR_HANBANG:
    			
    			Utility.setIndicator(myPlayer, 1, "WAITING");
    			Utility.setIndicator(myPlayer, 2, "Done building flyers.");
    			if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
    			{
    				obj = ArmoryBuildOrder.BUILD_WRAITHS;
    				currFlyer = 0;
    			}
    			return;
    			
    		case BUILD_WRAITHS:
    			
    			Utility.setIndicator(myPlayer, 1, "BUILD_WRAITHS");
    			Utility.setIndicator(myPlayer, 2, "Building wraith " + Integer.toString(currFlyer) + ".");
				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
    			while ( rFront != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE + 5 || myPlayer.myRC.getTeamResources() < myPlayer.myLastRes + Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep )
    			{
    				myPlayer.sleep();
    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
    			}
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
    			currFlyer++;
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
	}


	public void onWakeupCallback(int lastActiveRound) {}

}
