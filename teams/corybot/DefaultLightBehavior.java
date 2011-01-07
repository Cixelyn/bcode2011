package corybot;

import battlecode.common.*;

public class DefaultLightBehavior extends Behavior {

	public DefaultLightBehavior(RobotPlayer player) {
		super(player);
	}

	
	
	/**
	 * This robot doesn't do anything for the time being.
	 */
	public void run() {
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
		
	}
}
