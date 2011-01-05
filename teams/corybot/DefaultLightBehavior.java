package corybot;

import battlecode.common.ComponentClass;
import battlecode.common.ComponentController;

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
		
	}



	@Override
	public void newMessageCallback(MsgType msg) {
		// TODO Auto-generated method stub
		
	}
}
