package guileBot;

import battlecode.common.*;


/**
 * Action system that wraps commonly used movement functionality
 * @author Cory
 *
 */
public class Actions {
	
	private final RobotPlayer myPlayer;
	private final RobotController myRC;
	
	
	
	
	public Actions(RobotPlayer player) {
		myPlayer = player;
		myRC = player.myRC;
	}
	
	
	
	/**
	 * This allows a robot to backup while facing a particular direction 
	 * @param dir - the direction to move in 
	 * @return whether move was successful or not
	 */
	public boolean backUpInDir(Direction dir) {
		try {
			if (!myPlayer.myMotor.isActive() && dir.ordinal()<8) {
				if (myRC.getDirection().opposite().equals(dir)) {
					if (myPlayer.myMotor.canMove((dir))) {
						myPlayer.myMotor.moveBackward();
						return true;
					}
				} else {
					myPlayer.myMotor.setDirection(dir.opposite());
				}
			}
		} catch (GameActionException e) {
			System.out.println("Action Exception: backUpInDir");
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	/**
	 * Attempts to retreat in the first open direction that 
	 * allows the robot to continue facing in the forward direction
	 * <pre>
	 * Valid:
	 *   . . .
	 *   . O .
	 *   x x x
	 * </pre>
	 * @param dir
	 * @return whether retreat was successful.
	 */
	public boolean retreatBackwardsInDir(Direction dir) {
		//TODO: Implement
		return false;
	}

	
	
	
	
	
	
	
	public boolean moveInDir(Direction dir) {
		try {
			if (!myPlayer.myMotor.isActive() && dir.ordinal()<8) {
				if (myRC.getDirection().equals(dir)) {
					if (myPlayer.myMotor.canMove((dir))) {
						myPlayer.myMotor.moveForward();
						return true;
					}
				} else {
					myPlayer.myMotor.setDirection(dir);
				}
			}
		} catch (GameActionException e) {
			Utility.printMsg(myPlayer,"Action Exception: backUpInDir");
			e.printStackTrace();
		}
		return false;
	}
	

	
}

