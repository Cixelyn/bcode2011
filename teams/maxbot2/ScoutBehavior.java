package maxbot2;

import battlecode.common.ComponentController;
import battlecode.common.Message;

public class ScoutBehavior extends Behavior{

	public ScoutBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void newComponentCallback(ComponentController[] components) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newMessageCallback(Message msg) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
	}

}
