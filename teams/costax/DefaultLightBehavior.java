package costax;

import battlecode.common.ComponentClass;
import battlecode.common.ComponentController;
import battlecode.common.ComponentType;
import battlecode.common.Message;

public class DefaultLightBehavior extends Behavior {

	public DefaultLightBehavior(RobotPlayer player) {
		super(player);
	}

	
	
	/**
	 * This robot doesn't do anything for the time being.
	 */
	public void run() {
		// TODO Auto-generated method stub
	}
	
	
	
	public String toString() {
		return "LightBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
		if(Utility.hasComponent(ComponentType.SMG,components)) {
			myPlayer.swapBehavior(new ScoutBehavior(myPlayer));
		}
	}





	@Override
	public void newMessageCallback(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
