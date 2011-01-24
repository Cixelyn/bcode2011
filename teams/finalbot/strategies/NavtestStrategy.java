package finalbot.strategies;

import battlecode.common.*;
import finalbot.Behavior;
import finalbot.RobotPlayer;
import finalbot.behaviors.DefaultBehavior;
import finalbot.behaviors.NavtestBehavior;

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
