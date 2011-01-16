package suboptimal.behaviors;

import battlecode.common.*;
import suboptimal.*;

public class ArmoryBehavior extends Behavior
{
	
	
	private enum ArmoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		EQUIP_UNITS
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	MapLocation towerLoc;
	
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	
	boolean hasJump;
	boolean hasSatellite;
	
	int currFlyer = 0;
	double minFluxToBuild;
	
	boolean towerEquipped = false;
	
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
        				obj = ArmoryBuildOrder.EQUIP_UNITS;
    				}
    			}
    			return;
    			
    		case EQUIP_UNITS:
    			
    			Utility.setIndicator(myPlayer, 1, "BUILD_FLYERS");
    			
    			r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
				if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() )
				{
					rInfo = myPlayer.mySensor.senseRobotInfo(r);
					if ( rInfo.chassis == Chassis.HEAVY )
					{
						Utility.setIndicator(myPlayer, 2, "Equipping heavy.");
						hasJump = false;
						hasSatellite = false;
						for ( int j = rInfo.components.length - 1 ; j >= 0 ; j-- )
						{
							c = rInfo.components[j];
							if ( c == ComponentType.JUMP )
								hasJump = true;
							if ( c == ComponentType.SATELLITE )
								hasSatellite = true;
						}
						if ( !hasJump )
							Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND);
						else if ( !hasSatellite )
							Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SATELLITE, RobotLevel.ON_GROUND);
						return;
					}
				}
				else if ( currFlyer < Constants.MAX_FLYERS )
				{
					Utility.setIndicator(myPlayer, 2, "Building flyer " + Integer.toString(currFlyer) + ".");
					r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
					if ( currFlyer >= Constants.FLYERS_TO_BUILD_FAST )
						minFluxToBuild = Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + 2*Constants.RESERVE;
					else
						minFluxToBuild = Chassis.FLYING.cost + ComponentType.CONSTRUCTOR.cost + ComponentType.SIGHT.cost + Constants.RESERVE;
					if ( r == null && !myPlayer.myBuilder.isActive() && myPlayer.myRC.getTeamResources() > minFluxToBuild && myPlayer.myRC.getTeamResources() - myPlayer.myLastRes > Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep )
					{
						Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
		    			currFlyer++;
					}
				}
				else if ( !towerEquipped && towerLoc != null && myPlayer.mySensor.senseObjectAtLocation(towerLoc, RobotLevel.ON_GROUND) != null )
				{
					Utility.setIndicator(myPlayer, 2, "Equipping missile turret.");
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(towerLoc));
					myPlayer.sleep();
					if ( myPlayer.mySensor.senseObjectAtLocation(towerLoc, RobotLevel.ON_GROUND) != null && !myPlayer.myBuilder.isActive() )
						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SATELLITE, RobotLevel.ON_GROUND);
					towerEquipped = true;
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
				}
				else
					Utility.setIndicator(myPlayer, 2, "Idle.");
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
