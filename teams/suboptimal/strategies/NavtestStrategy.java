package suboptimal.strategies;

import battlecode.common.*;
import suboptimal.Behavior;
import suboptimal.RobotPlayer;
import suboptimal.behaviors.DefaultBehavior;
import suboptimal.behaviors.NavtestBehavior;

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
