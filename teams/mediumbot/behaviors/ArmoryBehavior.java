package mediumbot.behaviors;

import battlecode.common.*;
import mediumbot.*;

public class ArmoryBehavior extends Behavior
{
	
	
	private enum ArmoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		EQUIP_UNIT,
		MAKE_FLYER,
		SLEEP
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	int currFlyer = 0;
	int currMedium = 0;
	boolean remakeFlyers = false;
	
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	
	int babyHeavy;
	
	boolean rHasJump;
	int rNumPlasma;
	
	double minFluxToBuild;
	
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
        				obj = ArmoryBuildOrder.EQUIP_UNIT;
    				}
    			}
    			return;
    			
    		case EQUIP_UNIT:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_UNIT");
    			
    			if ( !remakeFlyers && Clock.getRoundNum() > Constants.REMAKE_FLYER_TIME )
    			{
    				remakeFlyers = true;
    				currFlyer = 0;
    			}
    			
    			if ( (!remakeFlyers && currFlyer < Constants.MAX_WRAITHS + Constants.MAX_DRONES) || (remakeFlyers && currFlyer < Constants.MAX_DRONES) )
    			{
	    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
	    			if ( r == null )
		    			obj = ArmoryBuildOrder.MAKE_FLYER;
	    			return;
    			}
    			
    			Utility.setIndicator(myPlayer, 2, "No more flyers to make.");
    			return;
    			
    		case MAKE_FLYER:
    			
    			Utility.setIndicator(myPlayer, 1, "MAKE_FLYER");
    			Utility.setIndicator(myPlayer, 2, "Building flyer " + Integer.toString(currFlyer) + ".");
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.IN_AIR);
    			if ( r != null )
    			{
    				obj = ArmoryBuildOrder.EQUIP_UNIT;
    				return;
    			}
    			
				if ( currFlyer < Constants.MAX_WRAITHS )
					minFluxToBuild = Chassis.FLYING.cost + ComponentType.BLASTER.cost + ComponentType.RADAR.cost + Constants.RESERVE;
				else if ( currFlyer < Constants.MAX_WRAITHS + 2 )
					minFluxToBuild = Chassis.FLYING.cost + ComponentType.CONSTRUCTOR.cost + ComponentType.SIGHT.cost + Constants.RESERVE;
				else
					minFluxToBuild = Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + 2*Constants.RESERVE;
				if ( !myPlayer.myBuilder.isActive() && myPlayer.myRC.getTeamResources() > minFluxToBuild && myPlayer.myRC.getTeamResources() - myPlayer.myLastRes > Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep )
				{
					Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
	    			currFlyer++;
				}
				return;
				
    		case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				Utility.setIndicator(myPlayer, 2, "zzzzzzz");
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
