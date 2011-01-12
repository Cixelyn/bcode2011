package plasmabot2.behaviors;

import battlecode.common.*;
import plasmabot2.*;

public class ArmoryBehavior extends Behavior
{
	
	ArmoryBuildOrder obj = ArmoryBuildOrder.WAIT_FOR_DOCK;
	
	MapLocation unitDock;
	
	Robot rFront;
	
	int plasmas;
	
	int flyersBuilt = 0;
	
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
    			Utility.setIndicator(myPlayer, 2, "Building flyer " + Integer.toString(flyersBuilt) + ".");
    			if ( flyersBuilt > Constants.MAX_FLYERS )
	    			obj = ArmoryBuildOrder.BUILD_DRAGOON;
    			else
    			{
					rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
	    			while ( rFront != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE + 5 || myPlayer.myRC.getTeamResources() < myPlayer.myLastRes + Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep )
	    			{
	    				myPlayer.sleep();
	    				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.IN_AIR);
	    			}
	    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.FLYING);
	    			flyersBuilt++;
    			}
    			return;
    			
    		case BUILD_DRAGOON:
    			Utility.setIndicator(myPlayer, 1, "BUILD_DRAGOON");
				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
	    		while ( rFront != null || myPlayer.myBuilder.isActive() || myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE + 5 || myPlayer.myRC.getTeamResources() < myPlayer.myLastRes + Chassis.FLYING.upkeep + Chassis.BUILDING.upkeep ){
	    			myPlayer.sleep();
	    			rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
	    		}
	    		Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.MEDIUM);
	    		obj=ArmoryBuildOrder.EQUIP_DRAGOON;
    			return;
    		
    			
    		case EQUIP_DRAGOON:
    			Utility.setIndicator(myPlayer, 1, "EQUIP_DRAGOON");
    			for ( RobotInfo rInfo : myPlayer.myScanner.scannedRobotInfos )
    			{
    				if ( rInfo.chassis == Chassis.MEDIUM && rInfo.robot.getTeam() == myPlayer.myRC.getTeam() && rInfo.location.equals(unitDock) )
    				{
    					plasmas=0;
    					for ( ComponentType c : rInfo.components )
    					{
    						if ( c == ComponentType.PLASMA) {
    							plasmas=plasmas+1;
    						}
    					}
    					if (plasmas!=2 && plasmas<3) {
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.PLASMA, RobotLevel.ON_GROUND);
    					}
    					else {
    						obj=ArmoryBuildOrder.BUILD_DRAGOON;
    					}
    				}
    			}
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
	public void onDamageCallback(double damageTaken) {}

}
