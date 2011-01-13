package masterytwo.strategies;


import masterytwo.Behavior;
import masterytwo.RobotPlayer;
import masterytwo.behaviors.DefaultBehavior;


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
