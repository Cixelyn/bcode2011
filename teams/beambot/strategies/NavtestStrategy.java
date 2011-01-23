package beambot.strategies;

import battlecode.common.*;
import beambot.Behavior;
import beambot.RobotPlayer;
import beambot.behaviors.DefaultBehavior;
import beambot.behaviors.NavtestBehavior;

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
