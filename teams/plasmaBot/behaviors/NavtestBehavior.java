package plasmaBot.behaviors;

import battlecode.common.*;
import plasmaBot.*;



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
		moveInDirection(bugNavTo(new MapLocation(0,0)));
	}
	
	
	
	
	
	int[][] lastTracingDirectionMap = new int[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
	boolean isTracing = false;
	int tracingDirection = 0;

	
	public Direction bugNavTo(MapLocation dest) {
		
		Utility.setIndicator(myPlayer, 0, "Tracing: "+Boolean.toString(isTracing)+"  Dir:"+tracingDirection);
		Utility.setIndicator(myPlayer, 1, myPlayer.myRC.getLocation().toString());
		
	
		MapLocation myLoc = myPlayer.myRC.getLocation();
		Direction currDir = myPlayer.myRC.getDirection();
		Direction destDir = myLoc.directionTo(dest);
		int xmod=myLoc.x%GameConstants.MAP_MAX_WIDTH; 
		int ymod=myLoc.y%GameConstants.MAP_MAX_HEIGHT;				
		
		if(!myPlayer.myMotor.isActive()) {											//make sure we can actually move.
			if(isTracing) {
				if(currDir==destDir) { 												//simple clear of obstacle
					isTracing = false;												//stop tracing
					return currDir;
				}else {											 					//we're still tracing
					if(tracingDirection == RIGHT) {
						return firstOpenDir(currDir.rotateRight(),tracingDirection*-1);
					} else { //if tracingDirection == LEFT
						return firstOpenDir(currDir.rotateLeft(),tracingDirection*-1);
					}
				}
			} else {
				if(myPlayer.myMotor.canMove(destDir)) {
					return destDir;
				} else{																			//we've hit obsticle
					isTracing = true;															//start tracing
	
					if(lastTracingDirectionMap[xmod][ymod]==0) {								//pick a direction to trace		
						tracingDirection = closerAngle(currDir,destDir);
						lastTracingDirectionMap[xmod][ymod] = tracingDirection;					//and remember it
					}else{
						tracingDirection *= -1;													//pick opposite direction if we've seen it.
					} 
					
					 
					return firstOpenDir(currDir,tracingDirection);
				}
			}
		} else{
			return currDir;
		}
		
		
	}
	
	
	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	
	
	
	
	
	public int closerAngle(Direction currDir, Direction destDir) {
		int diff = destDir.ordinal() - currDir.ordinal();
		
		if(diff>0) {
			if(diff<4) {
				return RIGHT;
			} else {
				return LEFT;
			}
		} else {
			if(diff<-4) {
				return RIGHT;
			} else {
				return LEFT;
			}	
		}
	}
	
	
	public Direction firstOpenDir(Direction currDir, int rotation) {
		Direction startDir = currDir;
		
		for(int i=0; i<8; i++) {
			if(myPlayer.myMotor.canMove(currDir)) break;
			
			
			if(rotation==RIGHT) currDir=currDir.rotateRight();
			else currDir=currDir.rotateLeft();
			
		} 
		
		
		return currDir;
		
	}
	
	
	
	
	
	
	
	
	
	
	public boolean moveInDirection(Direction dir) throws GameActionException {
		if(myPlayer.myMotor.isActive()) {
			return false;
		}
		
		if(myPlayer.myRC.getDirection()!=dir) {
			myPlayer.myMotor.setDirection(dir);
			return false;
		}
		
		if(!myPlayer.myMotor.canMove(dir)) {
			return false;
		}
		
		myPlayer.myMotor.moveForward();
		return true;
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
	

	
	public String toString() {
		return "Navtest Behavior";
	}
	
	
	public void onWakeupCallback(int lastActiveRound) {}
	
	
	
	
	
}
