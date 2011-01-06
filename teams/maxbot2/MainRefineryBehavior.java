package maxbot2;

import battlecode.common.*;
import java.util.ArrayList;

public class MainRefineryBehavior extends Behavior {
	
	RefineryBuildOrder obj = RefineryBuildOrder.INITIALIZE;
	
	int rGuns;
	int marinesMade = 0;
	int sheep = 0; // counts while sleeping!
	ArrayList<Integer> myRobots = new ArrayList<Integer>();
	
	boolean rSensor;
	boolean rArmor;
	boolean isLeader = false;
	boolean powered = false;
	boolean eeHanTiming = false;
	
	RobotInfo rInfo;
	GameObject oFront;
	Robot babyRobot;
	Robot[] nearbyRobots;
	
	Message[] msgs;
	Message attackMsg;
	
	public MainRefineryBehavior(RobotPlayer player) {
		super(player);
	}

	
	public void newComponentCallback(ComponentController[] components) {
		

	}




	public void run() throws Exception {

		switch(obj)
    	{
    		case INITIALIZE:
    			myPlayer.myRC.setIndicatorString(2, "INITIALIZE");
    			while(myPlayer.myRC.getTeamResources() < Constants.COMMTYPE.cost + Constants.RESERVE || myPlayer.myBuilder.isActive())
    				myPlayer.myRC.yield();
    			myPlayer.myBuilder.build(Constants.COMMTYPE,myPlayer.myRC.getLocation(),RobotLevel.ON_GROUND);
    			myPlayer.myRC.setIndicatorString(1, "Antenna installed!");
    			for(ComponentController c:myPlayer.myRC.components())
				{
					if (c.type()==Constants.COMMTYPE)
					{
						myPlayer.myBroadcaster = (BroadcastController)c;
						myPlayer.myMessenger.enableSender();
					}
				}
    			obj = RefineryBuildOrder.GIVE_ANTENNA;
    			myPlayer.myRC.yield();
    			break;
    			
    		case GIVE_ANTENNA:
    			myPlayer.myRC.setIndicatorString(2, "GIVE_ANTENNA");
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for (Robot r:nearbyRobots)
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(r);
    				if (rInfo.chassis == Chassis.BUILDING && myPlayer.myRC.getRobot().getID() < rInfo.robot.getID())
    					isLeader = true;
    			}
    			if (isLeader)
    			{
    				nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    				for (Robot r:nearbyRobots)
    				{
    					rInfo = myPlayer.mySensor.senseRobotInfo(r);
    					if (rInfo.chassis == Chassis.LIGHT && myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location)<=2 && !myPlayer.myMotor.isActive() && !myPlayer.myBuilder.isActive())
    					{
    						myPlayer.myRC.setIndicatorString(1,"I'm the leader!");
    						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(rInfo.location));
    						myPlayer.myRC.yield();
    						while (myPlayer.myRC.getTeamResources() < Constants.COMMTYPE.cost + Constants.RESERVE)
    							myPlayer.myRC.yield();
    						myPlayer.myBuilder.build(Constants.COMMTYPE, rInfo.location, RobotLevel.ON_GROUND);
    						obj = RefineryBuildOrder.WAIT_FOR_SIGNAL;
    					}
    				}
    			}
    			else
    				obj = RefineryBuildOrder.WAIT_FOR_SIGNAL;
    			myPlayer.myRC.yield();
    			break;
    			
    		case WAIT_FOR_SIGNAL:
    			myPlayer.myRC.setIndicatorString(2, "WAIT_FOR_SIGNAL");
    			if(powered)
        			obj = RefineryBuildOrder.MAKE_MARINE;
    			myPlayer.myRC.yield();
    			break;
    			
    		case MAKE_MARINE:
    			myPlayer.myRC.setIndicatorString(2, "MAKE_MARINE");
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
    			else if(marinesMade >= Constants.MARINES )
    			{
    				obj = RefineryBuildOrder.SLEEP;
    			}
    			myPlayer.myRC.yield();
    			break;
    			
    		case EQUIP_MARINE:
    			myPlayer.myRC.setIndicatorString(2, "EQUIP_MARINE");
    			oFront = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			if(oFront != null && ((Robot)oFront).getID() == babyRobot.getID())
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
    						myPlayer.myMessenger.sendMsg(attackMsg);
    				}
    			}
    			else
    			{
    				obj = RefineryBuildOrder.MAKE_MARINE;
    			}
    			myPlayer.myRC.yield();
    			break;
    			
    		case SLEEP:
    			myPlayer.myRC.setIndicatorString(2, "SLEEP");
    			sheep++;
    			if (sheep >= Constants.MAX_SHEEP && eeHanTiming)
    			{
    				sheep = 0;
    				myPlayer.myMessenger.sendMsg(attackMsg);
    			}
    			myPlayer.myRC.yield();
    			break;
    	}
		
	}

	public String toString() {
		return "MainRefineryBehavior";
	}

	
	public void newMessageCallback(Message msg) {
		if(msg.ints != null && msg.ints[0] == Constants.POWER_ON[0])
		{
			myPlayer.myRC.setIndicatorString(1,"Message received!");
			powered = true;
		}
		else if(msg.ints != null && msg.ints[0] == Constants.ATTACK[0] && msg.strings != null && msg.strings[0] != "idk")
		{
			myPlayer.myRC.setIndicatorString(0,"(refinery) | knows spawn");
			attackMsg = msg;
			eeHanTiming = true;
		}
		
	}

}
