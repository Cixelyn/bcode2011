package finalbot.strategies;

import finalbot.Behavior;
import finalbot.RobotPlayer;

/**
 * A strategy is a collection of behaviors that are instantiated upon robot default.
 * Strategies should only change _BETWEEN_ games.  Not during games
 * @author Cory
 *
 */
public abstract class Strategy {
	public abstract Behavior selectBehavior(RobotPlayer player, int currTime);
	public abstract String toString();
}
