package fibbyBot11.behaviors;

import fibbyBot11.*;
import battlecode.common.*;

public class FactoryBehavior extends Behavior
{

	FactoryBuildOrder obj = FactoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	Robot rFront;
	
	int tanksBuilt = 0;
	
	public FactoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch ( obj )
		{
			
			case WAIT_FOR_DOCK:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
    			if ( unitDock != null )
    			{
    				while ( myPlayer.myMotor.isActive() )
    					myPlayer.sleep();
    				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) <= ComponentType.FACTORY.range )
    				{
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
        				obj = FactoryBuildOrder.WAIT_FOR_HANBANG;
    				}
    			}
    			return;
    			
			case WAIT_FOR_HANBANG:
				
				Utility.setIndicator(myPlayer, 1, "WAIT_FOR_HANBANG");
    			if ( Clock.getRoundNum() > Constants.HANBANG_TIME )
    				obj = FactoryBuildOrder.BUILD_TANKS;
				return;
				
			case BUILD_TANKS:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_TANKS");
    			Utility.setIndicator(myPlayer, 2, "Building tank " + Integer.toString(tanksBuilt) + ".");
				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			while ( rFront != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE + 5 || myPlayer.myRC.getTeamResources() < myPlayer.myLastRes + Chassis.MEDIUM.upkeep + Chassis.BUILDING.upkeep )
    			{
    				myPlayer.sleep();
    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			}
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.MEDIUM);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.MEDIC, RobotLevel.ON_GROUND);
    			tanksBuilt++;
    			return;
			
			case PAUSE_TANKS:
				
				Utility.setIndicator(myPlayer, 1, "PAUSE_TANKS");
    			Utility.setIndicator(myPlayer, 2, "Pausing tank " + Integer.toString(tanksBuilt) + ".");
    			return;
		}
	}

	public String toString()
	{
		return "FactoryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_DOCK )
			unitDock = msg.locations[Messenger.firstData];
		if ( t == MsgType.MSG_STOP_TANKS )
			obj = FactoryBuildOrder.PAUSE_TANKS;
		if ( t == MsgType.MSG_START_TANKS )
			obj = FactoryBuildOrder.BUILD_TANKS;
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		obj = FactoryBuildOrder.BUILD_TANKS;
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
}
