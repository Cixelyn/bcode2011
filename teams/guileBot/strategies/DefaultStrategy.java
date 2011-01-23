package guileBot.strategies;


import battlecode.common.Chassis;
import guileBot.*;
import guileBot.behaviors.*;


/**
 * This is just an example
 * @author Cory
 *
 */
public class DefaultStrategy extends Strategy {

	public Behavior selectBehavior(RobotPlayer player, int currTime) {
		
		//testing framework code for custom maps.
		if(currTime<10) {
			Chassis ctype = player.myRC.getChassis();
			
			
			if(ctype!=Chassis.BUILDING || ctype!=Chassis.LIGHT) { //If we are not the original starting units
				
				//Build testing code here.
				
				if(ctype==Chassis.HEAVY) {
					return new ArbiterBehavior(player);
				}
				
				
				
			}
		}
		
		
		/////////////////////////////////////////////////////////////////
		//Our main instantiation code		
		return new DefaultBehavior(player);
	}
	
	
	public String toString() {
		return "DefaultStrategy";
	}

}
