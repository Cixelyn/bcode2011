package masteryone.behaviors;

import battlecode.common.*;
import masteryone.MsgType;
import masteryone.RobotPlayer;
import masteryone.Utility;
import masteryone.Constants;

public class FlyingDroneBehavior extends Behavior {
	
	//TODO remember past mines, perhaps fix if we run into someone as we are running away
	
	static FlyingDroneActions obj = FlyingDroneActions.EQUIPPING;
	
	static boolean hasSight = false;
	static boolean hasConstructor = false;
	static boolean foundID = false;
	boolean builtRefineryChassis = false;
	boolean setRunAwayDirection=false;
	boolean foundVoids=false;
	int ID=-1;
	Mine currentMine;
	double prevRoundHP;
	boolean seenOtherSide;
	static boolean knowEverything;
	
	Direction initialDirection;
	Direction currentDirection;

	int runAwayTime;

	public FlyingDroneBehavior(RobotPlayer player) {
		super(player);
	}
	
	@Override
	public void run() throws Exception {
		if (myPlayer.myRC.getHitpoints()<prevRoundHP) {
			prevRoundHP=myPlayer.myRC.getHitpoints();
			runAwayTime=0;
			obj=FlyingDroneActions.RUN_AWAY;
		}
    	switch (obj) {
    	
    		case EQUIPPING: {
    			myPlayer.myRC.setIndicatorString(0, "equipping");
    			if (hasSight && hasConstructor) { //finally we are good to go!
    				obj=FlyingDroneActions.FLYING_DRONE_ID;
    			}
    			myPlayer.sleep();
    			return;
    		}
    		case FLYING_DRONE_ID: {
    			myPlayer.myRC.setIndicatorString(0, "waiting for id");
    			if (foundID) {
    				setDirectionID(ID);
    				obj=FlyingDroneActions.EXPAND;
    			}
    			myPlayer.sleep();
    			return;
    		}
    		case EXPAND: {
    			myPlayer.myRC.setIndicatorString(0, "expanding");                                                                                  
    			if (!myPlayer.myMotor.isActive()) {
        			for (Mine mine : myPlayer.myScanner.detectedMines) { //look for mines, if we find one, lets go get it
        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)==null) {
        					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(mine.getLocation()));
        					currentMine=mine;
        					obj=FlyingDroneActions.FOUND_MINE;
        					return;
        				}
        			}
        			if (ID==0) {
        				//Utility.println("going to drone0nav!"); // commented out and changed to Utility.println by JVen
        				Drone0Nav();
        			}
        			if (ID==1) {
        				Drone1Nav();
        			}
        			if (ID==2) {
        				Drone2Nav();
        			}
    			}
    			return;
    		}
    		case FOUND_MINE: {
    			myPlayer.myRC.setIndicatorString(0, "found mine"); 
				if (myPlayer.mySensor.senseObjectAtLocation(currentMine.getLocation(), RobotLevel.ON_GROUND)!=null) { //someone is on our mine, gonna just look for other ones
					obj=FlyingDroneActions.EXPAND;
					return;
				}
    			if (currentMine.getLocation().equals(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()))) { //i'm right by the mine, build recycler!
    				if (myPlayer.myRC.getTeamResources()>((ComponentType.RECYCLER.cost+Chassis.BUILDING.cost))) {
    					Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
    					Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
    					currentMine=null;
    					obj=FlyingDroneActions.EXPAND;
    				}
    			}
    			else {
    				if (!myPlayer.myMotor.isActive())  { //move closer to the mine
    					if (myPlayer.myRC.getDirection().equals(myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()))) {
            				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
            					myPlayer.myMotor.moveForward();
            				}
    					}
    					else {
    						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()));
    					}
    				}
    			}
    			return;
    		}
    		case RUN_AWAY: {
    			int totalX=0;
    			int totalY=0;
    			int totalEnemyRobots=0;
    			boolean enemyInFront=false;
    			if (runAwayTime>Constants.RUN_AWAY_TIME) {
    				setRunAwayDirection=false;
    				foundVoids=false;
    				obj=FlyingDroneActions.EXPAND;
    				return;
    			}
    			for (Robot robot : myPlayer.myScanner.detectedRobots) {
    				if (robot.getTeam().equals(myPlayer.myRC.getTeam().opponent())) {
    					RobotInfo rInfo= myPlayer.mySensor.senseRobotInfo(robot);
    					totalX=totalX+rInfo.location.x;
    					totalY=totalY+rInfo.location.y;
    					totalEnemyRobots=totalEnemyRobots+1;
    					enemyInFront=true;
    				}
    			}
    			if (enemyInFront) {
    				MapLocation vector=new MapLocation(totalX/totalEnemyRobots,totalY/totalEnemyRobots);
    				Direction runAway=myPlayer.myRC.getLocation().directionTo(vector).opposite();
    				if (myPlayer.myMotor.canMove(runAway)) {
    					if (!setRunAwayDirection) {
        					if (!myPlayer.myMotor.isActive()) {
            					myPlayer.myMotor.setDirection(runAway);
            					setRunAwayDirection=true;
            					runAwayTime=runAwayTime+1;
            					return;
        					}
    					}
    					else {
        					if (!myPlayer.myMotor.isActive()) {
                				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
                					myPlayer.myMotor.moveForward();
                					runAwayTime=runAwayTime+1;
                					return;
                				}
        					}
    					}
    				}
    			}
    			else {
    				if (!foundVoids) {
    					if (!myPlayer.myMotor.isActive()) {
    						Direction voidDirection=getMostVoidsDirection();
            				myPlayer.myMotor.setDirection(voidDirection);
            				foundVoids=true;
    					}
    					
    				}
					if (!myPlayer.myMotor.isActive()) {
        				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
        					myPlayer.myMotor.moveForward();
        					return;
        				}
					}
    			}
    			return;
    		}
    	}
		
	}
	@Override
	public void newComponentCallback(ComponentController[] components) {
		for (ComponentController component: components) {
			if (component.type().equals(ComponentType.SIGHT)) {
				hasSight=true;
			}
			if (component.type().equals(ComponentType.CONSTRUCTOR)) {
				hasConstructor=true;
			}
		}
	}

	@Override
	public void newMessageCallback(MsgType type, Message msg) {
		if (type.equals(MsgType.MSG_SEND_NUM) && ID==-1) {
			foundID=true;
			ID=msg.ints[2];
		}
	}

	@Override
	public String toString() {
		return "FlyingDroneBehavior";
	}
	
	
	
	/////////////////////////////
	//////////NAVIGATION/////////
	/////////////////////////////
	
	
	public void setDirectionID(int ID) throws GameActionException {
		// below loop added by JVen
		while ( myPlayer.myMotor.isActive() )
			myPlayer.sleep();
		if (ID==0) {
			myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
		}
		if (ID==1) {
			myPlayer.myMotor.setDirection(Direction.SOUTH_WEST);
		}
		if (ID==2) {
			myPlayer.myMotor.setDirection(Direction.SOUTH_EAST);
		}
		initialDirection=myPlayer.myRC.getDirection();
	}
	
	public void Drone0Nav() throws GameActionException {
		if (knowEverything) {
			myPlayer.myRC.setIndicatorString(0, "know everything");
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
			}
			else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
		else if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
			Drone0KnowEverything();
		}
		else {
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
	}
	
	public void Drone0KnowEverything() throws GameActionException {
		while (!knowEverything) {
			if (!myPlayer.myMotor.isActive()) {
				myPlayer.myRC.setIndicatorString(0, "trying to lean everything");
				myPlayer.myMotor.setDirection(Direction.NORTH);
				if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
					currentDirection=Direction.SOUTH_WEST;
				}
				else {
					currentDirection=Direction.NORTH_EAST;
				}
				myPlayer.sleep();
				myPlayer.myMotor.setDirection(currentDirection);
				knowEverything=true;
			}
		}
		
	}
	
	public void Drone1Nav() throws GameActionException {
		if (knowEverything) {
			myPlayer.myRC.setIndicatorString(0, "know everything");
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
			}
			else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
		else if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
			Drone1KnowEverything();
		}
		else {
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
	}
	
	public void Drone1KnowEverything() throws GameActionException {
		while (!knowEverything) {
			if (!myPlayer.myMotor.isActive()) {
				myPlayer.myMotor.setDirection(Direction.SOUTH);
				if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
					currentDirection=Direction.NORTH_WEST;
				}
				else {
					currentDirection=Direction.SOUTH_EAST;
				}
				myPlayer.sleep();
				myPlayer.myMotor.setDirection(currentDirection);
				knowEverything=true;
			}
		}
		
	}
	public void Drone2Nav() throws GameActionException {
		if (knowEverything) {
			myPlayer.myRC.setIndicatorString(0, "know everything");
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
			}
			else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
		else if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
			Drone2KnowEverything();
		}
		else {
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
	}
	
	public void Drone2KnowEverything() throws GameActionException {
		while (!knowEverything) {
			if (!myPlayer.myMotor.isActive()) {
				myPlayer.myMotor.setDirection(Direction.SOUTH);
				if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
					currentDirection=Direction.NORTH_EAST;
				}
				else {
					currentDirection=Direction.SOUTH_WEST;
				}
				myPlayer.sleep();
				myPlayer.myMotor.setDirection(currentDirection);
				knowEverything=true;
			}
		}
		
	}
	
	public Direction getMostVoidsDirection() {
		int leftCount=0;
		int frontCount=0;
		int rightCount=0;
		if (myPlayer.myRC.getDirection().isDiagonal()) {
			
			//get number of voids to the left of me
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft(),1)).equals(TerrainTile.VOID)) {
				leftCount=leftCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft(),2)).equals(TerrainTile.VOID)) {
				leftCount=leftCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft(),3)).equals(TerrainTile.VOID)) {
				leftCount=leftCount+1;
			}
			
			//get number of voids in front of me
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),1)).equals(TerrainTile.VOID)) {
				frontCount=frontCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.VOID)) {
				frontCount=frontCount+1;
			}
			//get number of voids to the right of me
			
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight(),1)).equals(TerrainTile.VOID)) {
				rightCount=rightCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight(),2)).equals(TerrainTile.VOID)) {
				rightCount=rightCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight(),3)).equals(TerrainTile.VOID)) {
				rightCount=rightCount+1;
			}
			int maximum= Math.max(Math.max(leftCount, frontCount), rightCount);
			if (maximum==leftCount) {
				return myPlayer.myRC.getDirection().rotateLeft();
			}
			if (maximum==frontCount) {
				return myPlayer.myRC.getDirection();
			}
			else {
				return myPlayer.myRC.getDirection().rotateRight();
			}
		}
		else {
			//get number of voids to the left of me
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft(),1)).equals(TerrainTile.VOID)) {
				leftCount=leftCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft(),2)).equals(TerrainTile.VOID)) {
				leftCount=leftCount+1;
			}
			
			//get number of voids in front of me
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),1)).equals(TerrainTile.VOID)) {
				frontCount=frontCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.VOID)) {
				frontCount=frontCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.VOID)) {
				frontCount=frontCount+1;
			}
			//get number of voids to the right of me
			
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight(),1)).equals(TerrainTile.VOID)) {
				rightCount=rightCount+1;
			}
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight(),2)).equals(TerrainTile.VOID)) {
				rightCount=rightCount+1;
			}
			int maximum= Math.max(Math.max(leftCount, frontCount), rightCount);
			if (maximum==leftCount) {
				return myPlayer.myRC.getDirection().rotateLeft();
			}
			if (maximum==frontCount) {
				return myPlayer.myRC.getDirection();
			}
			else {
				return myPlayer.myRC.getDirection().rotateRight();
			}
		}
	}
	
	public void onWakeupCallback(int lastActiveRound) {}

}
