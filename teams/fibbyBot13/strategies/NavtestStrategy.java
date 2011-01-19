package fibbyBot13.strategies;

import battlecode.common.*;
import fibbyBot13.Behavior;
import fibbyBot13.RobotPlayer;
import fibbyBot13.behaviors.DefaultBehavior;
import fibbyBot13.behaviors.NavtestBehavior;

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
