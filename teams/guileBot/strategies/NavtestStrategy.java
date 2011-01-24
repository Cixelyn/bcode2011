package guileBot.strategies;

import battlecode.common.*;
import guileBot.Behavior;
import guileBot.RobotPlayer;
import guileBot.behaviors.DefaultBehavior;
import guileBot.behaviors.NavtestBehavior;

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
