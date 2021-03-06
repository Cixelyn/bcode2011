package fibbyBot5;
import battlecode.common.*;

public class Navigation {
	private final RobotController myRC;
	private final MovementController motor;
	Integer[][] memory;
	


	public Navigation(RobotPlayer player, RobotController RC, MovementController motorController) {
		myRC = RC;
		motor=motorController;
		memory = new Integer[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////BUGNAV/////////////////////////////////////////////////////////
	
	private boolean isTracing;
	private boolean tracingRight;
	private int roundsTracing = 0;
	private int trapped=0;
	public Direction bugTo(MapLocation destLoc) {
		
		
		MapLocation currLoc = myRC.getLocation();
		Direction currDir=myRC.getDirection();
		Direction destDir = currLoc.directionTo(destLoc);
		
//=		player.myRC.setIndicatorString(1, "Dest: "+destDir);
//		player.myRC.setIndicatorString(2, ""+isTracing);
		
		
		if(currLoc.equals(destLoc)) {
			isTracing=false;
			return Direction.OMNI;
		}
		//myRC.setIndicatorString(1, roundsTracing+"");
		//myRC.setIndicatorString(2, new Boolean(isTracing).toString());
		//System.out.println("first: (" + currLoc.x + "," + currLoc.y + ")");
		//MapLocation inFront = myRC.getLocation().add(currDir);
		//System.out.println("second: (" + inFront.x + "," + inFront.y + ")");
		
		if(isTracing) {
			
			//if we can move, go in that direction, stop tracing
			if((motor.canMove(currDir) && currDir==destDir) || (roundsTracing > 50 && motor.canMove(destDir))) {
				//System.out.println("mapLocation: (" + currLoc.x + "," + currLoc.y + ")");
				//System.out.flush();
				//memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]=0; 
				isTracing = false;
				//myRC.setIndicatorString(2, new Boolean(isTracing).toString());
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
				//myRC.setIndicatorString(2, new Boolean(isTracing).toString());
				if (memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]!=null) {
					//System.out.println("Seen location: (" + currLoc.x + "," + currLoc.y + ")");
				}
			
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
				for(int i=0; i<8; i++) {
					rightDir = rightDir.rotateRight();	
					if(motor.canMove(rightDir)) {
						break;
					}
				}
			
			
				//Check which distance is shorter.
				MapLocation leftLoc = currLoc.add(leftDir).add(leftDir);
				MapLocation rightLoc = currLoc.add(rightDir).add(rightDir);
				roundsTracing = 0;
				if (trapped==1) {
					trapped=0;
					return Direction.NONE;
				}
				if(destLoc.distanceSquaredTo(leftLoc)<destLoc.distanceSquaredTo(rightLoc)) {
					tracingRight = false;
					//System.out.println("Tracing Left");
					if (memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]==null) {
						memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]=0; 
						return leftDir;
					}
					trapped=1;
					//System.out.println("changed directions");
					tracingRight=true;
					return rightDir;
				} else {
					tracingRight = true;
					//System.out.println("Tracing Right");
					if (memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]==null) { 
						memory[currLoc.x%GameConstants.MAP_MAX_WIDTH][currLoc.y%GameConstants.MAP_MAX_HEIGHT]=0;
						return rightDir;
					}
					trapped=1;
					//System.out.println("changed directions");
					tracingRight=false;
					return leftDir;
				}				
			}		
		}
	}
}