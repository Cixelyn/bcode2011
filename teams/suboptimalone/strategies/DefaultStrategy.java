package suboptimalone.strategies;


import suboptimalone.*;
import suboptimalone.behaviors.*;


/**
 * This is just an example
 * @author Cory
 *
 */
public class DefaultStrategy extends Strategy {

	public Behavior selectBehavior(RobotPlayer player, int currTime) {
		return new DefaultBehavior(player);
	}
	
	
	public String toString() {
		return "DefaultStrategy";
	}

}
