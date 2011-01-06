package maxbot2;

import battlecode.common.*;
import java.util.ArrayList;

public class ExpoRefineryBehavior extends Behavior {

	RefineryBuildOrder obj = RefineryBuildOrder.WAIT_FOR_SIGNAL;
	
	int rGuns;
	int marinesMade = 0;
	int sheep = 0; // counts while sleeping!
	ArrayList<Integer> myRobots = new ArrayList<Integer>();
	
	boolean rSensor;
	boolean rArmor;
	boolean powered = false;
	boolean eeHanTiming = false;
	
	RobotInfo rInfo;
	Robot babyRobot;
	Robot rFront;
	
	ArrayList<?>[] componentList;
	
	Message[] msgs;
	Message attackMsg;
	
	public ExpoRefineryBehavior(RobotPlayer player) {
		super(player);
	}

	
	public void newComponentCallback(ComponentController[] components) {
		

	}




	public void run() throws Exception {

		switch(obj)
    	{
    		case WAIT_FOR_SIGNAL:
    			myPlayer.myRC.setIndicatorString(2, "WAIT_FOR_SIGNAL");
    			if(powered)
        			obj = RefineryBuildOrder.INITIALIZE;
    			myPlayer.myRC.yield();
    			break;
    			
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
    			else if(marinesMade >= Constants.MARINES)
    			{
    				obj = RefineryBuildOrder.SLEEP;
    			}
    			myPlayer.myRC.yield();
    			break;
    			
    		case EQUIP_MARINE:
    			myPlayer.myRC.setIndicatorString(2, "EQUIP_MARINE");
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
    					myPlayer.myRC.setIndicatorString(1, "Equipped a marine!");
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
		return "ExpoRefineryBehavior";
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
