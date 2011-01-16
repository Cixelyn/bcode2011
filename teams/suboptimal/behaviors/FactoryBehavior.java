package suboptimal.behaviors;

import suboptimal.*;
import battlecode.common.*;

public class FactoryBehavior extends Behavior
{

	private enum FactoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		BUILD_HEAVY,
		SLEEP
	}
	
	
	FactoryBuildOrder obj = FactoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	MapLocation towerLoc;
	
	Robot r;
	
	int currHeavy;
	
	boolean towerEquipped = false;
	
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
        				obj = FactoryBuildOrder.BUILD_HEAVY;
    				}
    				else
    				{
    					Utility.setIndicator(myPlayer, 2, "UNIT DOCK OUT OF RANGE!");
    					obj = FactoryBuildOrder.SLEEP;
    				}
    			}
    			return;
    			
			case BUILD_HEAVY:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_TANKS");
				if ( !towerEquipped && towerLoc != null && myPlayer.mySensor.senseObjectAtLocation(towerLoc, RobotLevel.ON_GROUND) != null )
				{
					Utility.setIndicator(myPlayer, 2, "Equipping missile turret.");
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(towerLoc));
					myPlayer.sleep();
					if ( r == null && !myPlayer.myBuilder.isActive() && myPlayer.mySensor.senseObjectAtLocation(towerLoc, RobotLevel.ON_GROUND) != null )
					{
						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
					}
					towerEquipped = true;
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
				}
				else
				{
	    			Utility.setIndicator(myPlayer, 2, "Building heavy " + Integer.toString(currHeavy) + ".");
					r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
	    			while ( r != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + 3*Constants.RESERVE || myPlayer.myRC.getTeamResources() - myPlayer.myLastRes < Chassis.BUILDING.upkeep + Chassis.HEAVY.upkeep * (currHeavy + 1) )
	    			{
	    				myPlayer.sleep();
	    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
	    			}
	    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.HEAVY);
	    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.REGEN, RobotLevel.ON_GROUND);
	    			currHeavy++;
				}
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
		return "FactoryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_DOCK )
			unitDock = msg.locations[Messenger.firstData];
		if ( t == MsgType.MSG_SEND_TOWER )
			towerLoc = msg.locations[Messenger.firstData];
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
}
