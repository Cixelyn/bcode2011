package suboptimal.behaviors;

import suboptimal.*;
import battlecode.common.*;

public class FactoryBehavior extends Behavior
{

	private enum FactoryBuildOrder 
	{
		WAIT_FOR_DOCK,
		EQUIP_TOWER,
		BUILD_HEAVY,
		SLEEP
	}
	
	
	FactoryBuildOrder obj = FactoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	MapLocation towerLoc;
	
	Robot r;
	RobotInfo rInfo;
	
	int currHeavy;
	
	double minFluxToBuild;
	
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
        				obj = FactoryBuildOrder.EQUIP_TOWER;
    				}
    				else
    				{
    					Utility.setIndicator(myPlayer, 2, "UNIT DOCK OUT OF RANGE!");
    					obj = FactoryBuildOrder.SLEEP;
    				}
    			}
    			return;
    			
			case EQUIP_TOWER:
				
				Utility.setIndicator(myPlayer, 1, "EQUIP_TOWER");
				if ( !towerEquipped && towerLoc != null )
				{
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
							Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
							Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
							towerEquipped = true;
							while ( myPlayer.myMotor.isActive() )
								myPlayer.sleep();
							myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(unitDock));
							obj = FactoryBuildOrder.BUILD_HEAVY;
							return;
						}
					}
				}
				Utility.setIndicator(myPlayer, 2, "Waiting for turret.");
				return;
    			
			case BUILD_HEAVY:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_TANKS");
    			Utility.setIndicator(myPlayer, 2, "Building heavy " + Integer.toString(currHeavy) + ".");
    			
				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				if ( currHeavy % 3 == 0 )
					minFluxToBuild = Chassis.HEAVY.cost + ComponentType.RADAR.cost + ComponentType.JUMP.cost + ComponentType.RAILGUN.cost + 5*ComponentType.SHIELD.cost + 2*ComponentType.SMG.cost + Constants.RESERVE;
				else if ( currHeavy % 3 == 1 )
					minFluxToBuild = Chassis.HEAVY.cost + ComponentType.RADAR.cost + ComponentType.JUMP.cost + ComponentType.RAILGUN.cost + 2*ComponentType.PLASMA.cost + ComponentType.SMG.cost + Constants.RESERVE;
				else if ( currHeavy % 3 == 2 )
					minFluxToBuild = Chassis.HEAVY.cost + ComponentType.RADAR.cost + ComponentType.JUMP.cost + ComponentType.RAILGUN.cost + ComponentType.HARDENED.cost + ComponentType.SHIELD.cost + ComponentType.BLASTER.cost + ComponentType.SMG.cost + Constants.RESERVE;
    			while ( r != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < minFluxToBuild || myPlayer.myRC.getTeamResources() - myPlayer.myLastRes < Chassis.BUILDING.upkeep + Chassis.HEAVY.upkeep * (currHeavy + 1) )
    			{
    				myPlayer.sleep();
    				r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			}
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.HEAVY);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RAILGUN, RobotLevel.ON_GROUND);
    			if ( currHeavy % 3 == 2 )
    				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.HARDENED, RobotLevel.ON_GROUND);
    			currHeavy++;
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
