package costax;

import battlecode.common.*;
import java.util.ArrayList;

public class ExpoRefineryBehavior extends Behavior {

	RefineryBuildOrder obj = RefineryBuildOrder.WAIT_FOR_SIGNAL;
	
	int rGuns;
	int marinesMade = 0;
	ArrayList<Integer> myRobots = new ArrayList<Integer>();
	
	boolean rSensor;
	boolean rArmor;
	boolean eeHanTiming = false;
	
	RobotInfo rInfo;
	Robot babyRobot;
	Robot rFront;
	
	ArrayList<?>[] componentList;
	
	Message[] msgs;
	Message attackMsg;
	
	public ExpoRefineryBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	
	public void newComponentCallback(ComponentController[] components) {
		

	}




	public void run() throws Exception {

		switch(obj)
    	{
    		case WAIT_FOR_SIGNAL:
    			myPlayer.myRC.setIndicatorString(2, "WAIT_FOR_SIGNAL");
    			msgs = myPlayer.myRC.getAllMessages();
    			for (Message m:msgs)
    			{
    				if(m.ints != null && m.ints[0] == 9090)
    				{
    					myPlayer.myRC.setIndicatorString(1,"Message received!");
        				obj = RefineryBuildOrder.INITIALIZE;
        				componentList = Utility.getComponents(myPlayer.myRC.components());
        				myPlayer.myBuilder = (BuilderController)componentList[1].get(0);
    				}
    				if(m.ints != null && m.ints[0] == 4774 && m.strings != null && m.strings[0] != "idk")
    				{
						myPlayer.myRC.setIndicatorString(0,"(refinery) | knows spawn");
						attackMsg = m;
    					eeHanTiming = true;
    				}
    			}
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
    					msgs = myPlayer.myRC.getAllMessages();
    					for(Message m:msgs)
    					{
    						if(m.ints != null && m.ints[0] == 4774 && m.strings != null)
            				{
    							myPlayer.myRC.setIndicatorString(0,"(expo) | knows spawn");
    							attackMsg = m;
            					eeHanTiming = true;
                				obj = RefineryBuildOrder.MAKE_MARINE;
            				}
    					}
    					if (eeHanTiming)
    					{
    						myPlayer.myBroadcaster.broadcast(attackMsg);
    					}
    				}
    			}
    			else
    			{
    				obj = RefineryBuildOrder.MAKE_MARINE;
    			}
    			break;
    			
    		case SLEEP:
    			myPlayer.myRC.yield();
    			break;
    	}
		
	}

	public String toString() {
		return "ExpoRefineryBehavior";
	}

	
	public void newMessageCallback(Message msg) {
		
		
	}

}
