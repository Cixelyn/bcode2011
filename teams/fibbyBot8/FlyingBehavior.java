package fibbyBot8;

import battlecode.common.*;

public class FlyingBehavior extends Behavior {

	public FlyingBehavior(RobotPlayer player) {
		super(player);
	}

	public void run() {
		myPlayer.myRC.setIndicatorString(1, "WHO AM I???");
	}
	
	
	
	public String toString() {
		return "FlyingBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		
	}
}
