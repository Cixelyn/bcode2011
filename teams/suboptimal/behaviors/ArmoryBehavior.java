package suboptimal.behaviors;

import battlecode.common.*;
import suboptimal.*;

public class ArmoryBehavior extends Behavior
{
	
	
	private enum ArmoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		EQUIP_UNIT,
		MAKE_FLYER,
		EQUIP_HEAVY,
		EQUIP_TOWER,
		SLEEP
	}
	
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	MapLocation towerLoc;
	
	int currFlyer = 0;
	int currHeavy = 0;
	int towerType = -1;
	boolean remakeFlyers = false;
	
	RobotInfo rInfo;
	Robot r;
	ComponentType c;
	
	int babyHeavy;
	
	boolean rHasJump;
	int rNumPlasma;
	
	double minFluxToBuild;
	
	boolean towerEquipped;
	
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
	    			{
	    				Utility.setIndicator(myPlayer, 2, "Making flyer.");
		    			obj = ArmoryBuildOrder.MAKE_FLYER;
	    			}
	    			return;
    			}
    			
    			if ( !towerEquipped && currFlyer == Constants.MAX_WRAITHS + Constants.MAX_DRONES )
    			{
    				obj = ArmoryBuildOrder.EQUIP_TOWER;
    				return;
    			}
    			
    			r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() && r.getID() != babyHeavy )
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if ( rInfo.chassis == Chassis.HEAVY )
    				{
    					Utility.setIndicator(myPlayer, 2, "Heavy found.");
    					babyHeavy = r.getID();
	    				obj = ArmoryBuildOrder.EQUIP_HEAVY;
    				}
	    			return;
    			}
    			
    			Utility.setIndicator(myPlayer, 2, "No heavy to equip, no more flyers to make.");
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
    			
    		case EQUIP_HEAVY:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_HEAVY");
				Utility.setIndicator(myPlayer, 2, "Equipping heavy " + Integer.toString(currHeavy) + ".");
    			
				r = (Robot) myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r == null )
    			{
    				obj = ArmoryBuildOrder.EQUIP_UNIT;
    				return;
    			}
				
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
    			if ( currHeavy % 3 == 0 )
    			{
					rHasJump = false;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.JUMP )
							rHasJump = true;
					}
					if ( !rHasJump )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND) )
						{
							currHeavy++;
							obj = ArmoryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			else if ( currHeavy % 3 == 1 )
    			{
    				rHasJump = false;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.JUMP )
							rHasJump = true;
					}
					if ( !rHasJump )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND) )
						{
							currHeavy++;
							obj = ArmoryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			else if ( currHeavy % 3 == 2 )
    			{
    				rNumPlasma = 0;
    				rHasJump = false;
					for ( int j = rInfo.components.length ; --j >= 0 ; )
					{
						c = rInfo.components[j];
						if ( c == ComponentType.PLASMA )
							rNumPlasma++;
						if ( c == ComponentType.JUMP )
							rHasJump = true;
					}
					if ( rNumPlasma < 2 )
						Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.PLASMA, RobotLevel.ON_GROUND);
					else if ( !rHasJump )
					{
						if ( Utility.tryBuildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.JUMP, RobotLevel.ON_GROUND) )
						{
							currHeavy++;
							obj = ArmoryBuildOrder.EQUIP_UNIT;
						}
					}
    			}
    			return;
				
    		case EQUIP_TOWER:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIP_TOWER");
    			towerEquipped = true;
    			
    			r = (Robot)myPlayer.mySensor.senseObjectAtLocation(unitDock, RobotLevel.ON_GROUND);
    			if ( r != null )
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if ( rInfo.chassis == Chassis.HEAVY )
					{
						Utility.setIndicator(myPlayer, 2, "Heavy found before turret constructed, abandoning turret.");
						obj = ArmoryBuildOrder.EQUIP_UNIT;
						return;
					}
    			}
    			
				if ( towerLoc != null )
				{
					if ( myPlayer.myRC.getLocation().distanceSquaredTo(towerLoc) > ComponentType.ARMORY.range || towerType == 1 )
					{
						Utility.setIndicator(myPlayer, 2, "I am not responsible for turret type " + Integer.toString(towerType) + ".");
						obj = ArmoryBuildOrder.EQUIP_UNIT;
						return;
					}
					r = (Robot)myPlayer.mySensor.senseObjectAtLocation(towerLoc, RobotLevel.ON_GROUND);
					if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() )
					{
						rInfo = myPlayer.mySensor.senseRobotInfo(r);
						if ( rInfo.chassis == Chassis.BUILDING )
						{
							Utility.setIndicator(myPlayer, 2, "Equipping turret.");
							while ( myPlayer.myMotor.isActive() )
								myPlayer.sleep();
							myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(towerLoc));
							myPlayer.sleep();
							Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.SATELLITE, RobotLevel.ON_GROUND);
							Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.PLASMA, RobotLevel.ON_GROUND);
							Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.PLASMA, RobotLevel.ON_GROUND);
							while ( myPlayer.myMotor.isActive() )
								myPlayer.sleep();
							myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
							obj = ArmoryBuildOrder.EQUIP_UNIT;
							return;
						}
					}
				}
				Utility.setIndicator(myPlayer, 2, "Waiting for turret.");
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
		if ( t == MsgType.MSG_SEND_TOWER )
		{
			towerType = msg.ints[Messenger.firstData];
			towerLoc = msg.locations[Messenger.firstData];
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)	
	{
		
	}
	
}
