package costax3;

import battlecode.common.*;

import java.util.ArrayList;

public class JimmyBehavior extends Behavior {
	
	JimmyBuildOrder obj = JimmyBuildOrder.THIS_IS_JIMMY;
	
	public JimmyBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception {
		
		
		switch (obj) {
			case THIS_IS_JIMMY:
				myPlayer.myRC.setIndicatorString(1,"THIS_IS_JIMMY");
				myPlayer.myMessenger.sendNotice(MsgType.MSG_POWER_UP);
				obj = JimmyBuildOrder.WAITING;
				return;
				
			case WAITING:
				myPlayer.myRC.setIndicatorString(1,"WAITING");
				Utility.spin(myPlayer);
				return;
		}
	}
	
	
	
	public String toString() {
		return "JimmyBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		
	}
}
