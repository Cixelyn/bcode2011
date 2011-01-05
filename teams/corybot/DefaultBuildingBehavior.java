package corybot;

import battlecode.common.ComponentController;
import battlecode.common.ComponentType;
import battlecode.common.Message;

public class DefaultBuildingBehavior extends Behavior {

	public DefaultBuildingBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	
	public void newComponentCallback(ComponentController[] components) {
		
	
		//Switch to Recycler Behavior
		if(Utility.hasComponent(ComponentType.RECYCLER,components)) {
			myPlayer.swapBehavior(new RecyclerBehavior(myPlayer));
		}
		

	}




	public void run() throws Exception {
		// TODO Auto-generated method stub

	}

	public String toString() {
		return "DefaultBuildingBehavior";
	}

	
	public void newMessageCallback(Message msg) {
		
		
	}

}
