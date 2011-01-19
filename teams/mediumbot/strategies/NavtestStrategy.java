package mediumbot.strategies;

import battlecode.common.*;
import mediumbot.Behavior;
import mediumbot.RobotPlayer;
import mediumbot.behaviors.DefaultBehavior;
import mediumbot.behaviors.NavtestBehavior;

public class NavtestStrategy extends Strategy {

	public Behavior selectBehavior(RobotPlayer player, int currTime) {
		
		if(player.myRC.getChassis()==Chassis.BUILDING) {
			return new DefaultBehavior(player);
		} else {
			return new NavtestBehavior(player);
		}
	}

	public String toString() {
		return "Navtest Strategy";
	}
	

}
