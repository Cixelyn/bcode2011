package masteryone.behaviors;

import battlecode.common.*;
import masteryone.MsgType;
import masteryone.RobotPlayer;



/**
 * Will write a simple NavTest to try to get it to work.
 * @author Cory
 *
 */
public class NavtestBehavior extends Behavior {

	public NavtestBehavior(RobotPlayer player) {
		super(player);
	}


	public void newComponentCallback(ComponentController[] components) {
	}


	public void newMessageCallback(MsgType type, Message msg) {
	}

	
	public void run() throws Exception {
		Direction currDir = myPlayer.myRC.getDirection();
		MapLocation myLoc = myPlayer.myRC.getLocation();
		
		if(!myPlayer.myMotor.isActive()) {
			
			
			
		}
		
		
		
	}

	public String toString() {
		return "Navtest Behavior";
	}
	
	
	public Direction bugNavTo() {
		Direction currDir = myPlayer.myRC.getDirection();
		MapLocation myLoc = myPlayer.myRC.getLocation();
		
		
		if(myPlayer.myMotor.canMove(currDir)) {
			return currDir;
		} else {
			return currDir.rotateLeft();
		}
		
		
	}
	

}
