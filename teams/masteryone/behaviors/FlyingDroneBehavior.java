package masteryone.behaviors;

import battlecode.common.*;
import masteryone.MsgType;
import masteryone.RobotPlayer;
import masteryone.Utility;

public class FlyingDroneBehavior extends Behavior {
	
	FlyingDroneActions obj = FlyingDroneActions.EQUIPPING;
	
	boolean hasSight = false;
	boolean hasConstructor = false;
	boolean foundID = false;
	boolean builtRefineryChassis = false;
	boolean setRunAwayDirection=false;
	int ID;
	Mine currentMine;
	double prevRoundHP;
	boolean seenOtherSide;
	boolean knowEverything;
	
	Direction initialDirection;
	Direction currentDirection;

	public FlyingDroneBehavior(RobotPlayer player) {
		super(player);
	}
	
	@Override
	public void run() throws Exception {
		
		if (hasSight && hasConstructor) { //finally we are good to go!
			obj=FlyingDroneActions.FLYING_DRONE_ID;
		}
		if (myPlayer.myRC.getHitpoints()<prevRoundHP) {
			prevRoundHP=myPlayer.myRC.getHitpoints();
			obj=FlyingDroneActions.RUN_AWAY;
		}
    	switch (obj) {
    	
    		case EQUIPPING: {
    			myPlayer.sleep();
    			return;
    		}
    		case FLYING_DRONE_ID: {
    			if (foundID) {
    				setDirectionID(ID);
    			}
    			return;
    		}
    		case EXPAND: {
    			if (!myPlayer.myMotor.isActive()) {
        			for (Mine mine : myPlayer.myScanner.detectedMines) { //look for mines, if we find one, lets go get it
        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)!=null) {
        					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(mine.getLocation()));
        					currentMine=mine;
        					obj=FlyingDroneActions.FOUND_MINE;
        					return;
        				}
        			}
        			if (ID==0) {
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
				if (myPlayer.mySensor.senseObjectAtLocation(currentMine.getLocation(), RobotLevel.ON_GROUND)!=null) { //someone is on our mine, gonna just look for other ones
					obj=FlyingDroneActions.EXPAND;
					return;
				}
    			if (currentMine.getLocation().equals(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()))) { //i'm right by the mine, build recycler!
    				if (myPlayer.myRC.getTeamResources()>(ComponentType.RECYCLER.cost+Chassis.BUILDING.cost)) {
    					Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
    					Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.MINE);
    					obj=FlyingDroneActions.EXPAND;
    				}
    			}
    			else {
    				if (!myPlayer.myMotor.isActive())  { //move closer to the mine
        				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
        					myPlayer.myMotor.moveForward();
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
    				Direction runAway=myPlayer.myRC.getLocation().directionTo(vector);
    				if (myPlayer.myMotor.canMove(runAway)) {
    					if (!setRunAwayDirection) {
        					if (!myPlayer.myMotor.isActive()) {
            					myPlayer.myMotor.setDirection(runAway);
            					setRunAwayDirection=true;
            					return;
        					}
    					}
    					else {
        					if (!myPlayer.myMotor.isActive()) {
                				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
                					myPlayer.myMotor.moveForward();
                					return;
                				}
        					}
    					}
    				}
    			}
    			else {
    				
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
		if (type.equals(MsgType.MSG_DRONE_ID)) {
			foundID=true;
			ID=msg.ints[0];
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
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
		else if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)){
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
				myPlayer.myMotor.setDirection(Direction.NORTH);
				if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)){
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
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
		else if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)){
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
				if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)){
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
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
		else if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)){
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
				if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)){
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

}
