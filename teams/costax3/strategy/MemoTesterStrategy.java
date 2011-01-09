package costax3.strategy;

import battlecode.common.*;
import costax3.*;


public class MemoTesterStrategy extends Strategy {

	public Behavior selectBehavior(RobotPlayer player, int currTime) {
		
		switch(player.myRC.getChassis()) {
			case BUILDING:
				return new SightConstructorAntennaeBehavior(player);
			case LIGHT:
				return new LightBehavior(player);
			default:
				return null;
		}
	}

}
