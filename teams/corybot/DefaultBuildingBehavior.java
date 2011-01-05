package corybot;

import battlecode.common.ComponentController;
import battlecode.common.ComponentType;

public class DefaultBuildingBehavior extends Behavior {

	public DefaultBuildingBehavior(RobotPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}

	
	public void newComponentCallback(ComponentController[] components) {
		
		
		
		
		//Switch to Recycler Behavior
		if(Utility.hasComponent(ComponentType.RECYCLER,components)) {
			myPlayer.myBehavior = new RecyclerBehavior(myPlayer);
		}
		
		
		
		

	}


	public void newMessageCallback(PacketHeader msg) {
		// TODO Auto-generated method stub

	}


	public void run() throws Exception {
		// TODO Auto-generated method stub

	}

	public String toString() {
		return "DefaultBuildingBehavior";
	}


	@Override
	public void newMessageCallback(Packet packet) {
		// TODO Auto-generated method stub
		
	}

}
