package costax3;

import battlecode.common.ComponentController;
import battlecode.common.Message;

public class ScoutBehavior extends Behavior{

	public ScoutBehavior(RobotPlayer player) {
		super(player);
	}

	@Override
	public void newComponentCallback(ComponentController[] components) {
		
	}

	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		
	}

	@Override
	public void run() throws Exception {
		if(!myPlayer.myMotor.isActive()) {
			if(myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			} else{
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
			}
		}	
	}

	@Override
	public String toString() {
		return null;
	}

}
