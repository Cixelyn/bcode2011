package costax3;

import battlecode.common.*;

import java.util.ArrayList;

public class MainRefineryBehavior extends Behavior {
	
	RefineryBuildOrder obj = RefineryBuildOrder.INITIALIZE;
	
	MapLocation hometown;
	MapLocation enemyLocation;
	
	int rGuns;
	int marinesMade = 0;
	int sheep = 0; // counts while sleeping!
	ArrayList<Integer> myRobots = new ArrayList<Integer>();
	
	boolean rBuilder;
	boolean rComm;
	boolean rSensor;
	boolean rArmor;
	boolean isLeader = false;
	boolean scouting = false;
	boolean powered = false;
	boolean eeHanTiming = false;
	
	RobotInfo rInfo;
	GameObject oFront;
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
    		case INITIALIZE:
    			myPlayer.myRC.setIndicatorString(1, "INITIALIZE");
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
						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(rInfo.location));
						myPlayer.myRC.yield();
					}
    			}
    			if (isLeader)
    			{
    				Utility.buildComponent(myPlayer, Constants.COMMTYPE);
    				obj = RefineryBuildOrder.WAIT_FOR_SCOUTING;
    			}
    			else
    				obj = RefineryBuildOrder.WAIT_FOR_POWER;
    			return;
    			
    		case WAIT_FOR_SCOUTING:
    			myPlayer.myRC.setIndicatorString(1, "WAIT_FOR_SCOUTING");
    			Utility.spin(myPlayer);
    			if(scouting)
        			obj = RefineryBuildOrder.MAKE_MULE;
    			return;
    			
    		case MAKE_MULE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MULE");
    			while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) || myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.MINE) != null)
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				Utility.buildChassis(myPlayer, Chassis.LIGHT);
				babyMule = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = RefineryBuildOrder.EQUIP_MULE;
    			return;
    			
    		case EQUIP_MULE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MULE");
    			oFront = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			if(oFront != null && ((Robot)oFront).getID() == babyMule.getID())
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo((Robot)oFront);
    				rBuilder = false;
    				rComm = false;
    				rSensor = false;
    				if(rInfo.components != null)
    				{
    					for (ComponentType c:rInfo.components)
    					{
    						if (c == ComponentType.CONSTRUCTOR)
    							rBuilder = true;
    						if (c == Constants.COMMTYPE)
    							rComm = true;
    						if (c == Constants.SENSORTYPE)
    							rSensor = true;
    					}
    				}
    				if (!rBuilder)
    					Utility.buildComponent(myPlayer, ComponentType.CONSTRUCTOR);
    				else if (!rComm)
    					Utility.buildComponent(myPlayer, Constants.COMMTYPE);
    				else if (!rSensor)
    					Utility.buildComponent(myPlayer, Constants.SENSORTYPE);
    				else
    					obj = RefineryBuildOrder.WAIT_FOR_POWER;
    			}
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
    			while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) || myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.MINE) != null)
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
    			if(marinesMade < Constants.MARINES - 1) // tweak to make main refineries build diff number of marines than expos
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
    			oFront = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			if(oFront != null && ((Robot)oFront).getID() == babyMarine.getID())
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo((Robot)oFront);
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
    						myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    				}
    			}
    			else
    			{
    				obj = RefineryBuildOrder.MAKE_MARINE;
    			}
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
		if(t == MsgType.MSG_SCOUTING)
			scouting = true;
		if(t == MsgType.MSG_POWER_UP)
			powered = true;
		if(t == MsgType.MSG_MOVE_OUT)
		{
			spawn = msg.ints[Messenger.firstData];
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
			eeHanTiming = true;
		}
	}

}
