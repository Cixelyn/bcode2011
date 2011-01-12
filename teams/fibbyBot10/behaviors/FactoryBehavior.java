package fibbyBot10.behaviors;

import fibbyBot10.*;
import battlecode.common.*;

public class FactoryBehavior extends Behavior
{

	FactoryBuildOrder obj = FactoryBuildOrder.SLEEP;
	
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
			
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				myPlayer.myRC.turnOff();
				return;
				
			case WAIT_FOR_DOCK:
    			
    			Utility.setIndicator(myPlayer, 1, "WAIT_FOR_DOCK");
    			if ( unitDock != null )
    			{
    				while ( myPlayer.myMotor.isActive() )
    					myPlayer.sleep();
    				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) <= ComponentType.FACTORY.range )
    				{
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
    					myPlayer.sleep();
        				obj = FactoryBuildOrder.BUILD_TANKS;
    				}
    			}
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
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.REGEN, RobotLevel.ON_GROUND);
    			tanksBuilt++;
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
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		obj = FactoryBuildOrder.WAIT_FOR_DOCK;
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
}
