package masterytwo.behaviors;

import java.util.ArrayList;

import masterytwo.behaviors.FlyingDroneActions;
import masterytwo.*;
import battlecode.common.*;

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
	MapLocation minePlacement;
	MapLocation spawnLocation;
	Mine currentMine;
	MapLocation currentBroadcastedMine;
	double prevRoundHP=10.0;
	boolean seenOtherSide;
	static boolean knowEverything;
	boolean returnedHome;
	int timeTrying=0;
	int timeout=0;
	ArrayList<MapLocation> broadcastedMines= new ArrayList<MapLocation>();
	MapLocation currentLoc;
	Direction initialDirection;
	Direction currentDirection;
	Direction oppositeOfSpawn;

	int runAwayTime=0;

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
    			Utility.setIndicator(myPlayer, 0, "equipping");
    			if (spawnLocation==null) {
    				spawnLocation=myPlayer.myRC.getLocation();
    			}
    			if (hasSight && hasConstructor) { //finally we are good to go!
    				obj=FlyingDroneActions.FLYING_DRONE_ID;
    			}
    			return;
    		}
    		case FLYING_DRONE_ID: {
    			Utility.setIndicator(myPlayer, 0, "waiting for id");
    			if (foundID) {
    				if (!myPlayer.myMotor.isActive()) {
    					setDirectionID(ID);
    					obj=FlyingDroneActions.EXPAND;
    				}
    			}
    			return;
    		}
    		case EXPAND: {
    			if (initialDirection==null) {
    				initialDirection=myPlayer.myRC.getDirection();
    			}
    			Utility.setIndicator(myPlayer, 0, "expanding" + initialDirection);
/*    			if (!returnedHome && Clock.getRoundNum()>Constants.FACTORY_TIME+500) {
    				int steps=0;
    				boolean firstTurn=false;
    				boolean secondTurn=false;
    				while (!returnedHome) {
    					if (!myPlayer.myMotor.isActive()) {
    						if (!firstTurn) {
    							myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    							firstTurn=true;
    						}
    						else if (steps<Constants.STEPS) {
    							if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
    								myPlayer.myMotor.moveForward();
    								steps=steps+1;
    							}
    							else {
        							myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(spawnLocation));
        							returnedHome=true;
    							}
    						}
    						else if (!secondTurn) {
    							myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(spawnLocation));
    							returnedHome=true;
    						}
    					}
    				}
    			}*/
    			if (!myPlayer.myMotor.isActive()) {
    				Mine[] detectedMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
        			for (Mine mine : detectedMines) { //look for mines, if we find one, lets go get it
        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)==null) {
        					if (myPlayer.myRC.getLocation().equals(mine.getLocation())) {
            					currentMine=mine;
            					oppositeOfSpawn=myPlayer.myRC.getLocation().directionTo(spawnLocation).opposite();
            					minePlacement=currentMine.getLocation().add(oppositeOfSpawn);
            					obj=FlyingDroneActions.FOUND_MINE;
            					currentLoc=myPlayer.myRC.getLocation();
            					return;
        					}
        					else {
            					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(mine.getLocation()));
            					currentMine=mine;
            					oppositeOfSpawn=myPlayer.myRC.getLocation().directionTo(spawnLocation).opposite();
            					minePlacement=currentMine.getLocation().add(oppositeOfSpawn);
            					obj=FlyingDroneActions.FOUND_MINE;
            					currentLoc=myPlayer.myRC.getLocation();
            					return;
        					}
        				}
        			}
        			droneGeneralNav(ID);
    			}
    			return;
    		}
    		case FOUND_MINE: {
    			Utility.setIndicator(myPlayer, 0, "found mine");
    			if (myPlayer.mySensor.withinRange(minePlacement) && myPlayer.myRC.senseTerrainTile(minePlacement).equals(TerrainTile.OFF_MAP)) {
    				minePlacement=currentMine.getLocation().add(myPlayer.myRC.getLocation().directionTo(currentLoc));
    			}
    			else if (myPlayer.myRC.getLocation().equals(minePlacement) && !myPlayer.myRC.getDirection().equals(myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()))) {
					if (!myPlayer.myMotor.isActive()) {
						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()));
					}
    			}
				else if (minePlacement.equals(myPlayer.myRC.getLocation())) { //i'm right by the mine, build recycler!
						if (myPlayer.mySensor.senseObjectAtLocation(currentMine.getLocation(), RobotLevel.ON_GROUND)!=null) { //someone is on our mine, gonna just look for other ones
							if (!myPlayer.myMotor.isActive()) {
								myPlayer.myMotor.setDirection(initialDirection);
								obj=FlyingDroneActions.EXPAND;
								return;
							}
						}
						else if ( myPlayer.myRC.getTeamResources() > Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE && !myPlayer.myMotor.isActive()){
    						Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()), Chassis.BUILDING);
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
    						obj =  FlyingDroneActions.WAIT_FOR_ACK;
    					}
    			}
    			else {
    				if (!myPlayer.myMotor.isActive())  { //move closer to the mine
    					if (myPlayer.myRC.getDirection().equals(myPlayer.myRC.getLocation().directionTo(minePlacement))) {
            				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
            					myPlayer.myMotor.moveForward();
            				}
    					}
    					else {
    						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(minePlacement));
    					}
    				}
    				return;
    			}
    			return;
    		}
    		case RUN_AWAY: {
    			Utility.setIndicator(myPlayer, 0, "run away!");
    			int totalX=0;
    			int totalY=0;
    			int totalEnemyRobots=0;
    			boolean enemyInFront=false;
    			if (runAwayTime>Constants.RUN_AWAY_TIME) {
    				runAwayTime=0;
    				setRunAwayDirection=false;
    				foundVoids=false;
    				obj=FlyingDroneActions.EXPAND;
    				return;
    			}
    			Robot[] detectedRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for (Robot robot : detectedRobots) {
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
    				if (runAway.equals(Direction.OMNI) || runAway.equals(Direction.NONE)) {
    					runAway=myPlayer.myRC.getDirection().opposite();
    				}
    				if (myPlayer.myMotor.canMove(runAway)) {
    					if (!setRunAwayDirection) {
        					if (!myPlayer.myMotor.isActive()) {
            					myPlayer.myMotor.setDirection(runAway);
            					setRunAwayDirection=true;
        					}
    					}
    					else {
        					if (!myPlayer.myMotor.isActive()) {
                				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
                				}
        					}
    					}
    					runAwayTime=runAwayTime+1;
    					return;
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
        					runAwayTime=runAwayTime+1;
        					return;
        				}
					}
					runAwayTime=runAwayTime+1;
    			}
    			return;
    		}
    		
    		case FIND_BROADCASTED: {
    			int index=0;
    			int min=-1;
    			MapLocation closestMine=null;
    			for (int i=0;i<broadcastedMines.size();i++) {
    				MapLocation mineLocation=broadcastedMines.get(i);
    				if (mineLocation!=null) {
    					if (myPlayer.myRC.getLocation().distanceSquaredTo(mineLocation)<min || min==-1) {
    						min=myPlayer.myRC.getLocation().distanceSquaredTo(mineLocation);
    						closestMine=mineLocation;
    						index=i;
    					}
    				}
    			}
    			if (closestMine==null) {
    				obj=FlyingDroneActions.EXPAND;
    			}
    			else {
    				broadcastedMines.set(index, null);
    				currentBroadcastedMine=closestMine;
    				obj=FlyingDroneActions.FIND_BROADCASTED_MINE;
    				
    			}
    			return;
    		}
    		
    		case FIND_BROADCASTED_MINE: {
    			Utility.setIndicator(myPlayer, 0, "found mine");
				if (myPlayer.mySensor.senseObjectAtLocation(currentBroadcastedMine, RobotLevel.ON_GROUND)!=null) { //someone is on our mine, gonna just look for other ones
					obj=FlyingDroneActions.FIND_BROADCASTED;
					return;
				}
				else if (currentBroadcastedMine.equals(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection())) || (myPlayer.myRC.getLocation().equals(currentBroadcastedMine))) { //i'm right by the mine, build recycler!
    					if ( myPlayer.myRC.getTeamResources() > Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE && !myPlayer.myMotor.isActive()){
    						Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(currentBroadcastedMine), Chassis.BUILDING);
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(currentBroadcastedMine), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
    						myPlayer.myMotor.setDirection(initialDirection);
    						obj =  FlyingDroneActions.EXPAND;
    					}
    			}
    			else {
    				if (!myPlayer.myMotor.isActive())  { //move closer to the mine
    					if (myPlayer.myRC.getDirection().equals(myPlayer.myRC.getLocation().directionTo(currentBroadcastedMine))) {
            				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
            					myPlayer.myMotor.moveForward();
            				}
    					}
    					else {
    						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(currentBroadcastedMine));
    					}
    				}
    			}
				return;
    		}
    		
    		case WAIT_FOR_ACK: {
    			Utility.setIndicator(myPlayer, 0, myPlayer.myRC.getLocation().toString());
				Robot[] detectedRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
				Mine[] detectedMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
    			if (timeout>Constants.TIMEOUT) {
        			for (Mine mine : detectedMines) { //look for mines, if we find one, lets go get it
        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)==null) {
        					if (myPlayer.myRC.getLocation().equals(mine.getLocation())) {
            					currentMine=mine;
            					oppositeOfSpawn=myPlayer.myRC.getLocation().directionTo(spawnLocation).opposite();
            					minePlacement=currentMine.getLocation().add(oppositeOfSpawn);
            					obj=FlyingDroneActions.FOUND_MINE;
            					currentLoc=myPlayer.myRC.getLocation();
            					return;
        					}
        					else {
            					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(mine.getLocation()));
            					currentMine=mine;
            					oppositeOfSpawn=myPlayer.myRC.getLocation().directionTo(spawnLocation).opposite();
            					minePlacement=currentMine.getLocation().add(oppositeOfSpawn);
            					obj=FlyingDroneActions.FOUND_MINE;
            					currentLoc=myPlayer.myRC.getLocation();
            					return;
        					}
        				}
        			}
					myPlayer.myMotor.setDirection(initialDirection);
					obj =  FlyingDroneActions.EXPAND;
					return;
    			}
    			else {
        			for (Robot robot : detectedRobots) {
        				RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(robot);
        				if (robot.getTeam().equals(myPlayer.myRC.getTeam())) {
        					Utility.setIndicator(myPlayer, 1, robot.toString());
        					Utility.setIndicator(myPlayer, 2, rInfo.location.toString() + "," + rInfo.direction.toString());
        				}
        				if (rInfo.location.equals(currentMine.getLocation()) && rInfo.location.add(rInfo.direction).equals(myPlayer.myRC.getLocation())) {
                			for (Mine mine : detectedMines) { //look for mines, if we find one, lets go get it
                				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)==null) {
                					if (myPlayer.myRC.getLocation().equals(mine.getLocation())) {
                    					currentMine=mine;
                    					oppositeOfSpawn=myPlayer.myRC.getLocation().directionTo(spawnLocation).opposite();
                    					minePlacement=currentMine.getLocation().add(oppositeOfSpawn);
                    					obj=FlyingDroneActions.FOUND_MINE;
                    					currentLoc=myPlayer.myRC.getLocation();
                    					timeout=0;
                    					return;
                					}
                					else {
                    					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(mine.getLocation()));
                    					currentMine=mine;
                    					oppositeOfSpawn=myPlayer.myRC.getLocation().directionTo(spawnLocation).opposite();
                    					minePlacement=currentMine.getLocation().add(oppositeOfSpawn);
                    					obj=FlyingDroneActions.FOUND_MINE;
                    					currentLoc=myPlayer.myRC.getLocation();
                    					timeout=0;
                    					return;
                					}
                				}
                			}
                			timeout=0;
        					myPlayer.myMotor.setDirection(initialDirection);
        					obj =  FlyingDroneActions.EXPAND;
        					return;
        				}
        				timeout=timeout+1;
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
			ID=msg.ints[Messenger.firstData+1];
			foundID=true;
		}		
		if (type.equals(MsgType.MSG_MINES)) {
			for (MapLocation mineLocation : msg.locations) {
				if (!broadcastedMines.contains(mineLocation)) {
					broadcastedMines.add(mineLocation);
				}
			}
			for (int j=0;j<broadcastedMines.size();j++) {
				broadcastedMines.remove(null);
			}
			obj=FlyingDroneActions.FIND_BROADCASTED;
			
		}
	}

	@Override
	public String toString() {
		return "FlyingDroneBehavior";
	}
	
	
	
	/////////////////////////////
	//////////NAVIGATION/////////
	/////////////////////////////
	
	public void droneGeneralNav(int ID) throws GameActionException {
		boolean foundEdge=false;
		Utility.setIndicator(myPlayer, 2, initialDirection.toString());
		if (myPlayer.myRC.getDirection().isDiagonal()) {
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)) {
				float probability = myPlayer.myDice.nextFloat();
				if (probability<.5) {
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
				}
				else {
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
				}
				foundEdge=true;
			}
		}
		else if (!myPlayer.myRC.getDirection().isDiagonal()) {
			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)) {
				float probability = myPlayer.myDice.nextFloat();
				if (probability<.5) {
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
				}
				else {
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
				}
				foundEdge=true;
			}
		}
		if (!foundEdge) {
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
			else {
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
			}
		}
	}
	
	
	public void setDirectionID(int ID) throws GameActionException {
		myPlayer.myMotor.setDirection(Direction.values()[(ID*3)%8]);
	}
	
	public void droneNav() throws GameActionException {
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
			droneKnowEverything();
		}
		else {
			if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
				myPlayer.myMotor.moveForward();
			}
		}
	}
	
	public void droneKnowEverything() {
		
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

	public void onDamageCallback(double damageTaken) {}

	@Override
	public void onWakeupCallback(int lastActiveRound) {
		// TODO Auto-generated method stub
		
	}

}