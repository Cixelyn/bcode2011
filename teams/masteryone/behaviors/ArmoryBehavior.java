package masteryone.behaviors;

import battlecode.common.*;
import masteryone.*;

public class ArmoryBehavior extends Behavior
{
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	Robot rFront;
	
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
    			for ( int i = 0 ; i <= 2 ; i++ )
    			{
    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
	    			while ( rFront != null || myPlayer.myRC.getTeamResources() < Chassis.FLYING.cost + ComponentType.CONSTRUCTOR.cost + ComponentType.SIGHT.cost )
	    			{
	    				myPlayer.sleep();
	    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
	    			}
	    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
	    			myPlayer.sleep();
    			}
    			
    			obj = ArmoryBuildOrder.WAITING;
    			return;
    			
    		case WAITING:
    			
    			Utility.setIndicator(myPlayer, 1, "WAITING");
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

}
