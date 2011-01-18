package suboptimalone.strategies;

import battlecode.common.*;
import suboptimalone.Behavior;
import suboptimalone.RobotPlayer;
import suboptimalone.behaviors.DefaultBehavior;
import suboptimalone.behaviors.NavtestBehavior;

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
