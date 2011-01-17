package fibbyBot12.strategies;

import battlecode.common.*;
import fibbyBot12.Behavior;
import fibbyBot12.RobotPlayer;
import fibbyBot12.behaviors.DefaultBehavior;
import fibbyBot12.behaviors.NavtestBehavior;

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
