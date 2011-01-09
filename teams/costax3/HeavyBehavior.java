package costax3;

import battlecode.common.*;

public class HeavyBehavior extends Behavior {

	public HeavyBehavior(RobotPlayer player) {
		super(player);
	}

	public void run() {
		myPlayer.myRC.setIndicatorString(1, "WHO AM I???");
	}
	
	
	
	public String toString() {
		return "HeavyBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		
	}
}
