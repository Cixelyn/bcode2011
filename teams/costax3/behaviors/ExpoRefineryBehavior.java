package costax3.behaviors;

import battlecode.common.*;

import java.util.ArrayList;

import costax3.Constants;
import costax3.Messenger;
import costax3.MsgType;
import costax3.RobotPlayer;
import costax3.Utility;

public class ExpoRefineryBehavior extends Behavior {

	RefineryBuildOrder obj = RefineryBuildOrder.WAIT_FOR_POWER;
	
	MapLocation hometown;
	MapLocation enemyLocation;
	MapLocation jimmyHome;
	
	int rGuns;
	int marinesMade = 0;
	int sheep = 0; // counts while sleeping!
	ArrayList<Integer> myRobots = new ArrayList<Integer>(1024); //high initial capacity
	
	boolean rSensor;
	boolean rArmor;
	boolean powered = false;
	boolean eeHanTiming = false;
	
	RobotInfo rInfo;
	Robot babyMarine;
	Robot rFront;
	
	ArrayList<?>[] componentList;
	
	int spawn;
	
	public ExpoRefineryBehavior(RobotPlayer player) {
		super(player);
	}

	
	public void run() throws Exception {

		switch(obj)
    	{
    		case WAIT_FOR_POWER:
    			myPlayer.myRC.setIndicatorString(1, "WAIT_FOR_POWER");
    			Utility.spin(myPlayer);
    			if(powered)
        			obj = RefineryBuildOrder.EQUIPPING;
    			return;
    			
    		case EQUIPPING:
    			myPlayer.myRC.setIndicatorString(1, "EQUIPPING");
    			if(myPlayer.myBuilder == null)
    				obj = RefineryBuildOrder.BROKEN;
    			else
    			{
	    			Utility.buildComponentOnSelf(myPlayer, Constants.COMMTYPE);
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
    			if(marinesMade < Constants.MARINES)
				{
    				marinesMade++;
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
    			
    		case BROKEN:
    			myPlayer.myRC.setIndicatorString(1, "BROKEN");
    			return;
    	}
		
	}

	public String toString() {
		return "ExpoRefineryBehavior";
	}


	public void newComponentCallback(ComponentController[] components) {
		

	}
	
	
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_JIMMY_HOME)
			jimmyHome = msg.locations[Messenger.firstData];
		if(t == MsgType.MSG_POWER_UP)
			powered = true;
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
