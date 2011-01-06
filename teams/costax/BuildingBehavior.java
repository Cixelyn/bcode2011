package costax;

import battlecode.common.*;

public class BuildingBehavior extends Behavior {

	public BuildingBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	
	public void newComponentCallback(ComponentController[] components) {
		
	
		//Switch to Refinery Behavior
		if(Utility.hasComponent(ComponentType.RECYCLER,components)) {
			if (runtime == 0) // Building started with recycler
				myPlayer.swapBehavior(new MainRefineryBehavior(myPlayer));
			else
				myPlayer.swapBehavior(new ExpoRefineryBehavior(myPlayer));		
		}
		

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

	
	public void newMessageCallback(Message msg) {
		
		
	}

}
