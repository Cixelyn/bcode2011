package masterytwo.behaviors;

import battlecode.common.*;
import masterytwo.*;

public class ArmoryBehavior extends Behavior
{
	
	
	private enum ArmoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		BUILD_FLYERS,
		SLEEP
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	Robot rFront;
	
	int flyersBuilt = 0;
	boolean flyerRemake = false;
	
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
        				obj = ArmoryBuildOrder.BUILD_FLYERS;
    				}
    			}
    			return;
    			
    		case BUILD_FLYERS:
    			
    			Utility.setIndicator(myPlayer, 1, "BUILD_FLYERS");
    			if ( flyersBuilt > Constants.MAX_FLYERS )
    			{
    				Utility.setIndicator(myPlayer, 2, "Pausing flyer production.");
    				if ( Clock.getRoundNum() > Constants.REMAKE_FLYER_TIME )
    				{
    					if ( !flyerRemake )
    					{
    						flyerRemake = true;
    						flyersBuilt = 0;
    					}
    					else
    						obj = ArmoryBuildOrder.SLEEP;
    				}
    			}
    			else
    			{
    				Utility.setIndicator(myPlayer, 2, "Building flyer " + Integer.toString(flyersBuilt) + ".");
					rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
					if ( flyerRemake || flyersBuilt >= Constants.FLYERS_TO_BUILD_FAST )
					{
		    			while ( rFront != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE + 5 || myPlayer.myRC.getTeamResources() < myPlayer.myLastRes + Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep )
		    			{
		    				myPlayer.sleep();
		    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
		    			}
					}
					else
					{
						while ( rFront != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.FLYING.cost + ComponentType.CONSTRUCTOR.cost + ComponentType.SIGHT.cost + Constants.RESERVE )
		    			{
		    				myPlayer.sleep();
		    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
		    			}
					}
	    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
	    			flyersBuilt++;
    			}
    			return;
    			
    		case SLEEP:
    			
    			Utility.setIndicator(myPlayer, 1, "SLEEP");
    			Utility.setIndicator(myPlayer, 2, "Done building flyers.");
    			myPlayer.myRC.turnOff();
    			return;
    	}
		
	}

	public String toString()
	{
		return "ArmoryBehavior";
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
		
	}
	
	public void onDamageCallback(double damageTaken)	
	{
		
	}
	
}
