package masterytwo.strategies;

import battlecode.common.*;
import masterytwo.Behavior;
import masterytwo.RobotPlayer;
import masterytwo.behaviors.DefaultBehavior;
import masterytwo.behaviors.NavtestBehavior;

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
