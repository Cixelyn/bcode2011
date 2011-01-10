package masteryone.behaviors;

import battlecode.common.ComponentController;
import battlecode.common.Message;
import masteryone.MsgType;
import masteryone.RobotPlayer;

public class DefaultBehavior extends Behavior {

	public DefaultBehavior(RobotPlayer player) {
		super(player);
	}


	public void newComponentCallback(ComponentController[] components) {
		
	}


	public void newMessageCallback(MsgType type, Message msg) {

		
	}

	
	public void run() throws Exception {
		myPlayer.sleep();
	}


	public String toString() {
		return null;
	}
	

}
