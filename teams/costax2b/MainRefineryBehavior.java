package costax2b;

import battlecode.common.*;

import java.util.ArrayList;

public class MainRefineryBehavior extends Behavior {
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	MapLocation hometown;
	MapLocation jimmyHome;
	MapLocation enemyLocation;
	
	int rGuns;
	int marinesMade = 0;
	int sheep = 0; // counts while sleeping!
	ArrayList<Integer> myRobots = new ArrayList<Integer>();
	
	boolean rBuilder;
	boolean rComm;
	boolean rSensor;
	boolean rArmor;
	boolean rDummy;
	boolean isLeader = false;
	boolean powered = false;
	boolean eeHanTiming = false;
	
	RobotInfo rInfo;
	Robot rFront;
	Robot babySCV;
	Robot babyMule;
	Robot babyMarine;
	Robot[] nearbyRobots;
	
	int spawn = -1;
	
	public MainRefineryBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception {

		switch(obj)
    	{
    		case EQUIPPING:
    			myPlayer.myRC.setIndicatorString(1, "EQUIPPING");
    			Utility.buildComponentOnSelf(myPlayer, Constants.COMMTYPE);
    			for(ComponentController c:myPlayer.myRC.components())
				{
					if (c.type()==Constants.COMMTYPE)
					{
						myPlayer.myBroadcaster = (BroadcastController)c;
						myPlayer.myMessenger.enableSender();
					}
				}
    			obj = RefineryBuildOrder.GIVE_ANTENNA;
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
    			obj = RefineryBuildOrder.WAIT_FOR_POWER;
    			return;
    			
    		case WAIT_FOR_POWER:
    			myPlayer.myRC.setIndicatorString(1, "WAIT_FOR_POWER");
    			Utility.spin(myPlayer);
    			if(powered)
    			{
    				myPlayer.myMessenger.sendNotice(MsgType.MSG_POWER_UP);
        			obj = RefineryBuildOrder.MAKE_MARINE;
    			}
    			return;
    			
    		case MAKE_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MARINE");
    			while(!Utility.shouldBuild(myPlayer, myPlayer.myRC.getDirection(), jimmyHome))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
    			if(marinesMade < Constants.MARINES) // tweak to make main refineries build diff number of marines than expos
				{
					Utility.buildChassis(myPlayer, Chassis.LIGHT);
					babyMarine = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
					myRobots.add(babyMarine.getID());
					obj = RefineryBuildOrder.EQUIP_MARINE;
				}
    			else
    				obj = RefineryBuildOrder.SLEEP;
    			return;
    			
    		case EQUIP_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MARINE");
    			Utility.equipFrontWithSameComponents(myPlayer, babyMarine, Constants.GUNTYPE, Constants.GUNS);
    			Utility.equipFrontWithTwoComponents(myPlayer, babyMarine, Constants.ARMORTYPE, Constants.SENSORTYPE);
    			marinesMade++;
				obj = RefineryBuildOrder.MAKE_MARINE;
				if (eeHanTiming)
					myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    			return;
    			
    		case SLEEP:
    			myPlayer.myRC.setIndicatorString(1, "SLEEP");
    			Utility.spin(myPlayer);
    			sheep++;
    			if (sheep >= Constants.MAX_SHEEP && eeHanTiming)
    			{
    				sheep = 0;
    				myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    			}
    			return;
    	}
		
	}

	public String toString() {
		return "MainRefineryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components) {
			
	
		}
	
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_POWER_UP)
		{
			powered = true;
			myPlayer.myMessenger.sendNotice(MsgType.MSG_POWER_UP);
		}
		if(t == MsgType.MSG_MOVE_OUT)
		{
			spawn = msg.ints[Messenger.firstData];
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
			powered = true;
			eeHanTiming = true;
		}
	}

}
