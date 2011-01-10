package team068;

import battlecode.common.*;

public class MediumBehavior extends Behavior {

	public MediumBehavior(RobotPlayer player) {
		super(player);
	}

	public void run() {
		myPlayer.myRC.setIndicatorString(1, "WHO AM I???");
	}
	
	
	
	public String toString() {
		return "MediumBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
		for(ComponentController c:components)
		{
			if (c.type() == ComponentType.DISH || c.type() == ComponentType.DUMMY || c.type() == ComponentType.RADAR)
			{
				myPlayer.swapBehavior(new JimmyBehavior(myPlayer));
				myPlayer.myMessenger.toggleReceive(true);
				return;
			}
		}
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		
	}
}
