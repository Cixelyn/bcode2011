package costax3.behaviors;

import costax3.MsgType;
import costax3.Navigation;
import costax3.RobotPlayer;
import battlecode.common.*;

public class TestBugBehavior extends Behavior {
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
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
			myPlayer.myRC.setIndicatorString(0, "test bug!");
			if (!myPlayer.myMotor.isActive()) {
				MapLocation mapLocation=myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),4);
				Direction direction =robotNavigation.bugTo(mapLocation);
				myPlayer.myMotor.setDirection(direction);
				myPlayer.myRC.yield();
				myPlayer.myMotor.moveForward();
			}
			myPlayer.myRC.yield();
		}
	}

	@Override
	public String toString() {
		return null;
	}

}
