package guileBot;
import battlecode.common.*;

import java.util.*;

public class GuileNavigation {
	
	RobotPlayer myPlayer;
	boolean tracing=false;
	int roundsTracing=0;
	double originalDistanceSquared=0;
	boolean tracingRight;
	boolean tracingLeft;
	int[][] memory = new int[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
	
	
	public GuileNavigation(RobotPlayer player) {
		myPlayer=player;
	}
	
	public Direction bugTo(MapLocation destination) {
		
		
		if (myPlayer.myRC.getLocation().equals(destination)) {
			return Direction.OMNI; // made it to our destination
		}
		
		
		if (tracing) { //TRACING!
			Direction tracingDirection = myPlayer.myRC.getDirection();
			roundsTracing=roundsTracing+1;
			if (myPlayer.myRC.getDirection().equals(myPlayer.myRC.getLocation().directionTo(destination)) //rotated back to target
				&& myPlayer.myRC.getLocation().distanceSquaredTo(destination)<originalDistanceSquared) { //closer than we were before
				roundsTracing=0;
				tracing=false;
			}
			else if (roundsTracing>Constants.TRACING_THRESHOLD) {
				roundsTracing=0;
				tracing=false;
			}
			else {
				if (tracingRight) {
					for (int i=0;i<8;i++) {
						if (myPlayer.myMotor.canMove(tracingDirection.rotateLeft())) {
							tracingDirection=tracingDirection.rotateLeft();
							if (tracingDirection.equals(myPlayer.myRC.getLocation().directionTo(destination))) {
								return tracingDirection;
							}
						}
						else {
							return tracingDirection;
						}
					}
				}
				else if (tracingLeft) {
					for (int i=0;i<8;i++) {
						if (myPlayer.myMotor.canMove(tracingDirection.rotateRight())) {
							tracingDirection=tracingDirection.rotateRight();
							if (tracingDirection.equals(myPlayer.myRC.getLocation().directionTo(destination))) {
								return tracingDirection;
							}
						}
						else {
							return tracingDirection;
						}
					}
				}
			}
			return tracingDirection;
		}
		else { //not tracing
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getLocation().directionTo(destination))) {
				return myPlayer.myRC.getLocation().directionTo(destination);
			}
			else {
				tracing=true;
				Direction tracingDirection = myPlayer.myRC.getDirection();
				if (memory[myPlayer.myRC.getLocation().x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().y%GameConstants.MAP_MAX_HEIGHT]==0) {
					memory[myPlayer.myRC.getLocation().x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().y%GameConstants.MAP_MAX_HEIGHT]=1;
					for (int i=0;i<8;i++) {
						if (myPlayer.myMotor.canMove(tracingDirection.rotateLeft())) {
							tracingLeft=true;
							tracingRight=false;
							Utility.printMsg(myPlayer, tracingDirection.rotateLeft().toString());
							return tracingDirection.rotateLeft();
						}
						tracingDirection=tracingDirection.rotateLeft();
					}
				}
				else {
					for (int i=0;i<8;i++) {
						if (myPlayer.myMotor.canMove(tracingDirection.rotateRight())) {
							tracingLeft=false;
							tracingRight=true;
							return tracingDirection.rotateRight();
						}
						tracingDirection=tracingDirection.rotateRight();
					}
				}
			}
		}
		return Direction.NONE;
		
	}
}
