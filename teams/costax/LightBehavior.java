package costax;

import battlecode.common.*;

public class LightBehavior extends Behavior {

	public LightBehavior(RobotPlayer player) {
		super(player);
	}

	
	
	/**
	 * This robot doesn't do anything for the time being.
	 */
	public void run() {
		// TODO Auto-generated method stub
	}
	
	
	
	public String toString() {
		return "LightBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
		if(Utility.hasComponent(ComponentType.CONSTRUCTOR,components))
			myPlayer.swapBehavior(new SCVBehavior(myPlayer));
		if(Utility.hasComponent(Constants.GUNTYPE,components)) 
			myPlayer.swapBehavior(new MarineBehavior(myPlayer));
	}





	@Override
	public void newMessageCallback(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
