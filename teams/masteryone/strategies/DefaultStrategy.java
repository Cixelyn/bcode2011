package masteryone.strategies;


import masteryone.Behavior;
import masteryone.RobotPlayer;
import masteryone.behaviors.DefaultBehavior;


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
