package maxbot2;

import battlecode.common.*;

public class LightBehavior extends Behavior {

	public LightBehavior(RobotPlayer player) {
		super(player);
	}

	public void run() {
		if (Clock.getRoundNum() <= 2)
			myPlayer.swapBehavior(new SCVBehavior(myPlayer));
		else
			myPlayer.swapBehavior(new MarineBehavior(myPlayer));
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
		
	}
}
