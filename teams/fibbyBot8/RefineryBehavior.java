package fibbyBot8;

import battlecode.common.*;

import java.util.*;

public class RefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	Robot[] nearbyRobots;
	RobotInfo rInfo;
	Robot babySCV;
	Robot babyMule;
	Robot babyMarine;
	
	boolean isLeader;
	
	double lastRes;
	Random random = new Random();
	double p;
	
	boolean eeHanTiming = false;
	int rebroadcastCounter = 0;
	int spawn = -1;
	MapLocation hometown;
	MapLocation enemyLocation;
	
	public RefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{

		if(eeHanTiming && myPlayer.myBroadcaster != null)
    	{
    		rebroadcastCounter++;
    		if (rebroadcastCounter >= Constants.REBROADCAST_FREQ)
    		{
    			rebroadcastCounter = 0;
    			myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    		}
    	}
		
		switch(obj)
    	{
    		case EQUIPPING:
    			myPlayer.myRC.setIndicatorString(1, "EQUIPPING");
    			for(ComponentController c:myPlayer.myRC.components())
				{
					if (c.type()==ComponentType.RECYCLER)
					{
						Utility.buildComponentOnSelf(myPlayer, Constants.COMMTYPE);
						obj = RefineryBuildOrder.GIVE_ANTENNA;
					}
				}
    			lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case GIVE_ANTENNA:
    			myPlayer.myRC.setIndicatorString(1, "GIVE_ANTENNA");
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for (Robot r:nearbyRobots)
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if (rInfo.chassis == Chassis.BUILDING && myPlayer.myRC.getRobot().getID() < rInfo.robot.getID())
    					isLeader = true;
    				if (rInfo.chassis == Chassis.LIGHT && myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location)<=2 && !myPlayer.myMotor.isActive() && !myPlayer.myBuilder.isActive())
					{
    					babySCV = rInfo.robot;
						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(rInfo.location));
						myPlayer.myRC.yield();
					}
    			}
    			if (isLeader)
    				Utility.equipFrontWithOneComponent(myPlayer, babySCV, Constants.COMMTYPE);
    			obj = RefineryBuildOrder.WAITING;
    			return;
    			
    		case WAITING:
    			myPlayer.myRC.setIndicatorString(1, "WAITING");
    			if(Clock.getRoundNum() >= Constants.MULE_TIME && Clock.getRoundNum() < Constants.EXPAND_TIME && myPlayer.myRC.getTeamResources() > 90 && lastRes < myPlayer.myRC.getTeamResources() + Chassis.LIGHT.upkeep)
    				obj = RefineryBuildOrder.MAKE_MULE;
    			if(Clock.getRoundNum() >= Constants.MARINE_TIME && Clock.getRoundNum() < Constants.MID_GAME && myPlayer.myRC.getTeamResources() > 90 && lastRes < myPlayer.myRC.getTeamResources() + Chassis.LIGHT.upkeep)
        			obj = RefineryBuildOrder.MAKE_MARINE;
    			if(Clock.getRoundNum() >= Constants.LATE_GAME && myPlayer.myRC.getTeamResources() > 100 && lastRes < myPlayer.myRC.getTeamResources() + Chassis.LIGHT.upkeep)
    			{
    				p = random.nextDouble();
    				if (p < Constants.MARINE_MULE_RATIO)
    					obj = RefineryBuildOrder.MAKE_MARINE;
    				else
    					obj = RefineryBuildOrder.MAKE_MULE;
    			}
    			lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case MAKE_MULE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MULE");
    			while(!Utility.shouldBuild(myPlayer, myPlayer.myRC.getDirection()))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				Utility.buildChassisInDir(myPlayer, myPlayer.myRC.getDirection(), Chassis.LIGHT);
				babyMule = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = RefineryBuildOrder.EQUIP_MULE;
				lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case EQUIP_MULE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MULE");
    			Utility.equipFrontWithThreeComponents(myPlayer, babyMule, ComponentType.CONSTRUCTOR, Constants.SENSORTYPE, Constants.COMMTYPE);
    			if (eeHanTiming)
    				myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    			obj = RefineryBuildOrder.WAITING;
    			lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case MAKE_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MARINE");
    			while(!Utility.shouldBuild(myPlayer, myPlayer.myRC.getDirection()))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				Utility.buildChassisInDir(myPlayer, myPlayer.myRC.getDirection(), Chassis.LIGHT);
				babyMarine = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = RefineryBuildOrder.EQUIP_MARINE;
				lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case EQUIP_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MARINE");
    			Utility.equipFrontWithSameComponents(myPlayer, babyMarine, Constants.GUNTYPE, Constants.GUNS);
    			Utility.equipFrontWithTwoComponents(myPlayer, babyMarine, Constants.ARMORTYPE, Constants.SENSORTYPE);
    			if (eeHanTiming)
    				myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    			obj = RefineryBuildOrder.WAITING;
    			lastRes = myPlayer.myRC.getTeamResources();
    			return;
    	}
		
	}

	public String toString()
	{
		return "RefineryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		if (t == MsgType.MSG_MOVE_OUT)
		{
			myPlayer.myRC.setIndicatorString(2, "We spawned " + Utility.spawnString(spawn));
			eeHanTiming = true;
			spawn = msg.ints[Messenger.firstData];
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
		}
	}

}
