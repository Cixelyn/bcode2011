package costax3;

import battlecode.common.*;

public class FactoryBehavior extends Behavior {
	
	FactoryBuildOrder obj = FactoryBuildOrder.SPIN;
	
	public FactoryBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception
	{

		switch(obj)
    	{
    		case SPIN:
    			myPlayer.myRC.setIndicatorString(1, "SPIN");
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
	}

}
