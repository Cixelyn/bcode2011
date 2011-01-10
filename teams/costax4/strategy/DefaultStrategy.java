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
			b = new MediumBehavior(player);
			break;
		case HEAVY:
			b = new HeavyBehavior(player);
			break;
		case FLYING:
			b = new FlyingBehavior(player);
			break;
		default:
			System.out.println("Error");
		}
		
		return b;
	}

}
