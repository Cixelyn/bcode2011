package fibbyBot9.strategies;

import fibbyBot9.RobotPlayer;
import fibbyBot9.behaviors.*;

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
