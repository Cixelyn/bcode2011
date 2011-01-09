package costax3;

import battlecode.common.ComponentController;
import battlecode.common.Message;
import battlecode.common.*;

public class TestBugBehavior extends Behavior {

	public TestBugBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public void newComponentCallback(ComponentController[] components) {
		
	}

	@Override
	public void newMessageCallback(MsgType type, Message msg) {
		
	}

	@Override
	public void run() throws Exception {
		while (true) {
			Direction direction = myPlayer.myNavigation.bugTo(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),4));
			myPlayer.myMotor.setDirection(direction);
			myPlayer.myRC.yield();
			myPlayer.myMotor.moveForward();
			myPlayer.myRC.yield();
			myPlayer.myRC.yield();
			myPlayer.myRC.yield();
		}
	}

	@Override
	public String toString() {
		return null;
	}

}
