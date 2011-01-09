package team068;

import battlecode.common.*;

import java.util.ArrayList;

public class ExpoRefineryBehavior extends Behavior {

	RefineryBuildOrder obj = RefineryBuildOrder.WAIT_FOR_SIGNAL;
	
	MapLocation hometown;
	MapLocation enemyLocation;
	
	int rGuns;
	int marinesMade = 0;
	int sheep = 0; // counts while sleeping!
	ArrayList<Integer> myRobots = new ArrayList<Integer>(1024); //high initial capacity
	
	boolean rSensor;
	boolean rArmor;
	boolean powered = false;
	boolean eeHanTiming = false;
	
	RobotInfo rInfo;
	Robot babyRobot;
	Robot rFront;
	
	ArrayList<?>[] componentList;
	
	public ExpoRefineryBehavior(RobotPlayer player) {
		super(player);
	}

	
	public void newComponentCallback(ComponentController[] components) {
		

	}




	public void run() throws Exception {

		switch(obj)
    	{
    		case WAIT_FOR_SIGNAL:
    			myPlayer.myRC.setIndicatorString(1, "WAIT_FOR_SIGNAL");
    			if(powered)
        			obj = RefineryBuildOrder.INITIALIZE;
    			return;
    			
    		case INITIALIZE:
    			myPlayer.myRC.setIndicatorString(1, "INITIALIZE");
    			if(myPlayer.myBuilder == null)
    			{
    				obj = RefineryBuildOrder.BROKEN;
    			}
    			else
    			{
	    			while(myPlayer.myRC.getTeamResources() < Constants.COMMTYPE.cost + Constants.RESERVE || myPlayer.myBuilder.isActive())
	    				myPlayer.myRC.yield();
	    			myPlayer.myBuilder.build(Constants.COMMTYPE,myPlayer.myRC.getLocation(),RobotLevel.ON_GROUND);
	    			for(ComponentController c:myPlayer.myRC.components())
					{
						if (c.type()==Constants.COMMTYPE)
						{
							myPlayer.myBroadcaster = (BroadcastController)c;
							myPlayer.myMessenger.enableSender();
						}
					}
	    			obj = RefineryBuildOrder.MAKE_MARINE;
    			}
    			return;
    	
    		case MAKE_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MARINE");
    			if(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) || myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.MINE) != null)
				{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
				}
    			else if(marinesMade < Constants.MARINES && myPlayer.myRC.getTeamResources() >= Chassis.LIGHT.cost + Constants.RESERVE)
				{
					myPlayer.myBuilder.build(Chassis.LIGHT,myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()));
					babyRobot = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
					myRobots.add(babyRobot.getID());
					obj = RefineryBuildOrder.EQUIP_MARINE;
				}
    			else if(marinesMade >= Constants.MARINES)
    			{
    				obj = RefineryBuildOrder.SLEEP;
    			}
    			return;
    			
    		case EQUIP_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MARINE");
    			rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			if(rFront != null && rFront.getID() == babyRobot.getID())
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(rFront);
    				rGuns = 0;
    				rSensor = false;
    				rArmor = false;
    				if(rInfo.components != null)
    				{
    					for (ComponentType c:rInfo.components)
    					{
    						if (c == Constants.GUNTYPE)
    							rGuns++;
    						if (c == Constants.SENSORTYPE)
    							rSensor = true;
    						if (c == Constants.ARMORTYPE)
    							rArmor = true;
    					}
    				}
    				if (rGuns < Constants.GUNS)
    					Utility.buildComponent(myPlayer, Constants.GUNTYPE);
    				else if (!rSensor)
    					Utility.buildComponent(myPlayer, Constants.SENSORTYPE);
    				else if (!rArmor)
    					Utility.buildComponent(myPlayer, Constants.ARMORTYPE);
    				else
    				{
    					marinesMade++;
    					obj = RefineryBuildOrder.MAKE_MARINE;
    					if (eeHanTiming)
    						myPlayer.myMessenger.sendDoubleLoc(MsgType.MSG_MOVE_OUT, hometown, enemyLocation);
    				}
    			}
    			else
    			{
    				obj = RefineryBuildOrder.MAKE_MARINE;
    			}
    			return;
    			
    		case SLEEP:
    			myPlayer.myRC.setIndicatorString(1, "SLEEP");
    			sheep++;
    			if (sheep >= Constants.MAX_SHEEP && eeHanTiming)
    			{
    				sheep = 0;
    				myPlayer.myMessenger.sendDoubleLoc(MsgType.MSG_MOVE_OUT, hometown, enemyLocation);
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

	
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_POWER_UP)
		{
			powered = true;
		}
		if(t == MsgType.MSG_MOVE_OUT)
		{
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
			eeHanTiming = true;
		}
	}

}
