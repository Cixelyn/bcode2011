package costax3;

import battlecode.common.*;

public class FactoryBehavior extends Behavior {
	
	FactoryBuildOrder obj = FactoryBuildOrder.WAIT_FOR_JIMMY_HOME;
	
	MapLocation jimmyHome;
	
	boolean rComm;
	
	Robot rFront;
	Robot babyJimmy;
	RobotInfo rInfo;
	
	public FactoryBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception
	{

		switch(obj)
    	{
    		case WAIT_FOR_JIMMY_HOME:
    			myPlayer.myRC.setIndicatorString(1, "WAIT_FOR_JIMMY_HOME");
    			Utility.spin(myPlayer);
    			if (jimmyHome != null)
    				obj = FactoryBuildOrder.MAKE_JIMMY;
    			return;
    			
    		case MAKE_JIMMY:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_JIMMY");
    			while(!Utility.shouldBuildJimmy(myPlayer, myPlayer.myRC.getDirection(), jimmyHome))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				Utility.buildChassis(myPlayer, Chassis.LIGHT);
				babyJimmy = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = FactoryBuildOrder.EQUIP_JIMMY;
    			return;
    			
    		case EQUIP_JIMMY:
    			rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
    			if(rFront != null && (rFront).getID() == babyJimmy.getID())
    			{
    				rInfo = myPlayer.mySensor.senseRobotInfo(rFront);
    				rComm = false;
    				if(rInfo.components != null)
    				{
    					for (ComponentType c:rInfo.components)
    					{
    						if (c == ComponentType.DISH)
    							rComm = true;
    					}
    				}
    				if (!rComm)
    					Utility.buildComponent(myPlayer, ComponentType.DISH);
    				else
    					obj = FactoryBuildOrder.SLEEP;
    			}
    			return;
    			
    		case SLEEP:
    			myPlayer.myRC.setIndicatorString(1, "SLEEP");
    			Utility.spin(myPlayer);
    			return;
    			
    	}
		
	}

	public String toString() {
		return "FactoryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components) {
			
	
		}
	
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_JIMMY_HOME)
			jimmyHome = msg.locations[Messenger.firstData];
	}

}
