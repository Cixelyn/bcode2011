package team068;

import battlecode.common.*;

public class BuildingBehavior extends Behavior {

	public BuildingBehavior(RobotPlayer player) {
		super(player);
	}

	
	public void newComponentCallback(ComponentController[] components) {
	}




	public void run() throws Exception {
		if (Clock.getRoundNum() <= 2)
			myPlayer.swapBehavior(new MainRefineryBehavior(myPlayer));
		else
			myPlayer.swapBehavior(new ExpoRefineryBehavior(myPlayer));
	}

	public String toString() {
		return "DefaultBuildingBehavior";
	}

	
	public void newMessageCallback(MsgType t, Message msg) {
		
		
	}

}
