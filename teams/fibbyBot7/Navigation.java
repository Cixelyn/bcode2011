package fibbyBot7;
import java.util.Random;

import battlecode.common.*;

public class Navigation {
	private final RobotPlayer player;
	private final RobotController myRC;
	private final MovementController motor;
	MapLocation[][] memory;
	public static int[][] seenLocations;
	static final Random rand = new Random();
	


	public Navigation(RobotPlayer player) {
		this.player = player;
		myRC = player.myRC;
		motor=player.myMotor;
		memory = new MapLocation[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
		seenLocations=new int[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////BUGNAV/////////////////////////////////////////////////////////
	
	private boolean isTracing;
	private boolean tracingRight;
	private int roundsTracing = 0;
	public Direction bugTo(MapLocation destLoc) {
		
		
		MapLocation currLoc = myRC.getLocation();
		Direction currDir=myRC.getDirection();
		Direction destDir = currLoc.directionTo(destLoc);
	
		player.myRC.setIndicatorString(2, "My loc: " +currLoc + "Dest: " + destLoc + " " + isTracing + " " + roundsTracing + destDir);
		
		
		if(currLoc.equals(destLoc)) {
			isTracing=false;
			return Direction.OMNI;
		}
		
		if(isTracing) {
			
			//if we can move, go in that direction, stop tracing
			if((motor.canMove(currDir) && currDir==destDir) || (roundsTracing > 50 && motor.canMove(destDir))) {
				isTracing = false;
				return destDir;
			}

			else { //we need to trace
				
				roundsTracing++;
				Boolean rotateLeft = false;
				Boolean isBlocked = false;
				Direction traceDir = currDir;
				
				//These statements can be replaced with binary manipulations
				if(tracingRight) {  //rotate as far left as possible. If not, rotate outwards.
					if(motor.canMove(currDir)) {
						rotateLeft = true;
						isBlocked = false;
					} else {
						rotateLeft = false;
						isBlocked = true;
					}					
				}
				
				else { //we're tracing left.  Rotate as far right, then rotate left.
					if(motor.canMove(currDir)) {
						rotateLeft = false;
						isBlocked = false;
					} else {
						rotateLeft = true;
						isBlocked = true;
					}
				}
				
				Direction oldDir=traceDir;
				
				

				for(int i=0; i<8; i++) {

					oldDir = traceDir;

					if(rotateLeft) traceDir = traceDir.rotateLeft();
					else traceDir = traceDir.rotateRight();
					
					if(isBlocked){ //We want to rotate to the first available space
						if(motor.canMove(traceDir)) 
							return traceDir;
					} 
								
					else { //We want to rotate until we reach the wall again
						
						if(traceDir==destDir && motor.canMove(destDir)) { //but break early if we can get on target
							return traceDir;
						}				
						if(!motor.canMove(traceDir)) 
							return oldDir;
					}
				}
			
				//We are at the destination
				return Direction.OMNI;
			}

		}
		else { //not tracing
			
			if(motor.canMove(destDir)) {
				return destDir;
			} 
				
			else {//we hit a wall, need to trace
				
				isTracing = true;
			
				//Figure out whether left or right is better.
				Direction leftDir=currDir;
				Direction rightDir=currDir;
			
			
				//Left Check
				for(int i=0; i<8; i++) {
					leftDir = leftDir.rotateLeft();	
					if(motor.canMove(leftDir)) {
						break;
					}
				}
				//Dir 
				for(int i=0; i<8; i++) {
					rightDir = rightDir.rotateRight();	
					if(motor.canMove(rightDir)) {
						break;
					}
				}
			
			
				//Check which distance is shorter.
				MapLocation leftLoc = currLoc.add(leftDir);
				MapLocation rightLoc = currLoc.add(rightDir);
				roundsTracing = 0;
				/*if (trapped==1) {
					trapped=0;
					return Direction.NONE;
				}*/
				if(destLoc.distanceSquaredTo(leftLoc)<destLoc.distanceSquaredTo(rightLoc)) {
					tracingRight = false;
					//System.out.println("Tracing Left");
					
					
					//I've never been to this spot while I'm looking for this destination, gonna go to the side with the shorter distance
					if (memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]==null) {
						memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]=destLoc;
						tracingRight = false;
						return leftDir;
					}
					//Uh oh, I've been to this spot since I've started looking for this location, better go the other way.
					if (memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT].equals(destLoc)) {
						tracingRight=true;
						return rightDir;
					}
					//I've already been to this spot, but now I'm going to a different destination, better just go to the side with the shorter distance.
					memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]=destLoc;
					tracingRight=false;
					return leftDir;
				} else {
					//System.out.println("Tracing Right");
					
					//I've never been to this spot while I'm looking for this destination, gonna go to the side with the shorter distance
					if (memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]==null) { 
						memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]=destLoc;
						tracingRight = true;
						return rightDir;
					}
					//Uh oh, I've been to this spot since I've started looking for this location, better go the other way.
					if (memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT].equals(destLoc)) {
						tracingRight=false;
						return leftDir;
					}
					//I've already been to this spot, but now I'm going to a different destination, better just go to the side with the shorter distance.
					memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]=destLoc;
					tracingRight=true;
					return rightDir;
				}				
			}		
		}
	}
	public void bounceNavNoLoops(RobotPlayer myPlayer) throws Exception
	{
		int random = rand.nextInt(10);
		if (!myPlayer.myMotor.isActive())
		{
			if(myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
				seenLocations[myPlayer.myRC.getLocation().x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().y%GameConstants.MAP_MAX_HEIGHT]=1; //remember where i've been
			}
			else
			{	
				//all directions around me
				Direction direction1=myPlayer.myRC.getDirection().rotateRight().rotateRight();
				Direction direction2=myPlayer.myRC.getDirection().rotateLeft().rotateLeft();
				Direction direction3=myPlayer.myRC.getDirection().rotateRight();
				Direction direction4=myPlayer.myRC.getDirection().rotateLeft();
				Direction direction5=myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight();
				Direction direction6=myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft();
				Direction direction7=myPlayer.myRC.getDirection().opposite();
				if (random == 0 || random == 1 || random == 2)
				{
					
					if (myPlayer.myMotor.canMove(direction1) && seenLocations[myPlayer.myRC.getLocation().add(direction1).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction1).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction2) && seenLocations[myPlayer.myRC.getLocation().add(direction2).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction2).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction3) && seenLocations[myPlayer.myRC.getLocation().add(direction3).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction3).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction4) && seenLocations[myPlayer.myRC.getLocation().add(direction4).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction4).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction5) && seenLocations[myPlayer.myRC.getLocation().add(direction5).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction5).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction6) && seenLocations[myPlayer.myRC.getLocation().add(direction6).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction6).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction7) && seenLocations[myPlayer.myRC.getLocation().add(direction7).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction7).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction7);
					
					//have seen everything, have to go to something i've already seen
					
					else if (myPlayer.myMotor.canMove(direction1))
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction2))
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction3))
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction4))
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction5))
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction6))
						myPlayer.myMotor.setDirection(direction6);
					else
						myPlayer.myMotor.setDirection(direction7);
					
					
				}
				else if (random == 3)
				{
					if (myPlayer.myMotor.canMove(direction3) && seenLocations[myPlayer.myRC.getLocation().add(direction3).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction3).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction4) && seenLocations[myPlayer.myRC.getLocation().add(direction4).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction4).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction1) && seenLocations[myPlayer.myRC.getLocation().add(direction1).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction1).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction2) && seenLocations[myPlayer.myRC.getLocation().add(direction2).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction2).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction5) && seenLocations[myPlayer.myRC.getLocation().add(direction5).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction5).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction6) && seenLocations[myPlayer.myRC.getLocation().add(direction6).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction6).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction7) && seenLocations[myPlayer.myRC.getLocation().add(direction7).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction7).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction7);
					
					//have seen everything, have to go to something i've already seen
					
					else if (myPlayer.myMotor.canMove(direction3))
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction4))
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction1))
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction2))
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction5))
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction6))
						myPlayer.myMotor.setDirection(direction6);
					else
						myPlayer.myMotor.setDirection(direction7);
				}
				else if (random == 4)
				{
					if (myPlayer.myMotor.canMove(direction5) && seenLocations[myPlayer.myRC.getLocation().add(direction5).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction5).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction6) && seenLocations[myPlayer.myRC.getLocation().add(direction6).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction6).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction3) && seenLocations[myPlayer.myRC.getLocation().add(direction3).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction3).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction4) && seenLocations[myPlayer.myRC.getLocation().add(direction4).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction4).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction1) && seenLocations[myPlayer.myRC.getLocation().add(direction1).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction1).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction2) && seenLocations[myPlayer.myRC.getLocation().add(direction2).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction2).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction7) && seenLocations[myPlayer.myRC.getLocation().add(direction7).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction7).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction7);
					
					//have seen everything, have to go to something i've already seen
					
					else if (myPlayer.myMotor.canMove(direction5))
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction6))
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction3))
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction4))
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction1))
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction2))
						myPlayer.myMotor.setDirection(direction2);
					else
						myPlayer.myMotor.setDirection(direction7);
				}
				else if (random == 5 || random == 6 || random == 7)
				{
					if (myPlayer.myMotor.canMove(direction2) && seenLocations[myPlayer.myRC.getLocation().add(direction2).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction2).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction1) && seenLocations[myPlayer.myRC.getLocation().add(direction1).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction1).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction4) && seenLocations[myPlayer.myRC.getLocation().add(direction4).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction4).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction3) && seenLocations[myPlayer.myRC.getLocation().add(direction3).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction3).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction6) && seenLocations[myPlayer.myRC.getLocation().add(direction6).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction6).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction5) && seenLocations[myPlayer.myRC.getLocation().add(direction5).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction5).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction7) && seenLocations[myPlayer.myRC.getLocation().add(direction7).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction7).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction7);
					
					//have seen everything, have to go to something i've already seen
					
					else if (myPlayer.myMotor.canMove(direction2))
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction1))
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction4))
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction3))
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction6))
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction5))
						myPlayer.myMotor.setDirection(direction5);
					else
						myPlayer.myMotor.setDirection(direction7);
				}
				else if (random == 8)
				{
					if (myPlayer.myMotor.canMove(direction4) && seenLocations[myPlayer.myRC.getLocation().add(direction4).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction4).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction3) && seenLocations[myPlayer.myRC.getLocation().add(direction3).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction3).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction2) && seenLocations[myPlayer.myRC.getLocation().add(direction2).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction2).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction1) && seenLocations[myPlayer.myRC.getLocation().add(direction1).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction1).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction6) && seenLocations[myPlayer.myRC.getLocation().add(direction6).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction6).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction5) && seenLocations[myPlayer.myRC.getLocation().add(direction5).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction5).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction7) && seenLocations[myPlayer.myRC.getLocation().add(direction7).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction7).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction7);
					
					//have seen everything, have to go to something i've already seen
					
					else if (myPlayer.myMotor.canMove(direction4))
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction3))
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction2))
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction1))
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction6)) 
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction5))
						myPlayer.myMotor.setDirection(direction5);
					else
						myPlayer.myMotor.setDirection(direction7);
				}
				else if (random == 9)
				{
					if (myPlayer.myMotor.canMove(direction6) && seenLocations[myPlayer.myRC.getLocation().add(direction6).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction6).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction5) && seenLocations[myPlayer.myRC.getLocation().add(direction5).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction5).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction4) && seenLocations[myPlayer.myRC.getLocation().add(direction4).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction4).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction3) && seenLocations[myPlayer.myRC.getLocation().add(direction3).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction3).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction2) && seenLocations[myPlayer.myRC.getLocation().add(direction2).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction2).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction1) && seenLocations[myPlayer.myRC.getLocation().add(direction1).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction1).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction1);
					else if (myPlayer.myMotor.canMove(direction7) && seenLocations[myPlayer.myRC.getLocation().add(direction7).x%GameConstants.MAP_MAX_WIDTH][myPlayer.myRC.getLocation().add(direction7).y%GameConstants.MAP_MAX_HEIGHT]==0)
						myPlayer.myMotor.setDirection(direction7);
					
					//have seen everything, have to go to something i've already seen
					
					else if (myPlayer.myMotor.canMove(direction6))
						myPlayer.myMotor.setDirection(direction6);
					else if (myPlayer.myMotor.canMove(direction5))
						myPlayer.myMotor.setDirection(direction5);
					else if (myPlayer.myMotor.canMove(direction4))
						myPlayer.myMotor.setDirection(direction4);
					else if (myPlayer.myMotor.canMove(direction3))
						myPlayer.myMotor.setDirection(direction3);
					else if (myPlayer.myMotor.canMove(direction2))
						myPlayer.myMotor.setDirection(direction2);
					else if (myPlayer.myMotor.canMove(direction1))
						myPlayer.myMotor.setDirection(direction1);
					else
						myPlayer.myMotor.setDirection(direction7);
				}
			}
		}
	}
	
}