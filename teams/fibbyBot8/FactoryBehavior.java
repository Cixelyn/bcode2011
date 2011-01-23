package fibbyBot8;

import battlecode.common.*;

public class FactoryBehavior extends Behavior {
	
	FactoryBuildOrder obj = FactoryBuildOrder.WAIT_FOR_JIMMY_HOME;
	
	MapLocation jimmyHome;
	
	boolean rComm;
	boolean rDummy;
	
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
				Utility.buildChassisInDir(myPlayer, myPlayer.myRC.getDirection(), Chassis.MEDIUM);
				babyJimmy = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = FactoryBuildOrder.EQUIP_JIMMY;
    			return;
    			
    		case EQUIP_JIMMY:
    			Utility.equipFrontWithTwoComponents(myPlayer, babyJimmy, ComponentType.DISH, ComponentType.DUMMY);
    			obj = FactoryBuildOrder.SLEEP;
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
