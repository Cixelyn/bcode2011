package costax4.strategy;

import costax4.behaviors.*;
import costax4.*;

public class DefaultStrategy extends Strategy {

	public Behavior selectBehavior(RobotPlayer player, int currTime) {
		Behavior b = null;
		switch(player.myRC.getChassis()) {
		case BUILDING:
			b = new BuildingBehavior(player);
			break;
		case LIGHT:
			b = new LightBehavior(player);
			break;
		case MEDIUM:
			break;
		case HEAVY:
			break;
		case FLYING:
			break;
		default:
			System.out.println("Error");
		}
		
		return b;
	}

}
