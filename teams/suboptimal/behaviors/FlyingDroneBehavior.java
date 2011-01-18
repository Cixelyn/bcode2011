package suboptimal.behaviors;

import java.util.*;


import suboptimal.*;
import battlecode.common.*;


public class FlyingDroneBehavior extends Behavior {
	
	
	private enum FlyingDroneActions
	{
		
		EQUIPPING,
		FLYING_DRONE_ID,
		FOUND_MINE,
		RUN_AWAY,
		BUILD_TOWER,
		EXPAND;

	}
	
	
	//TODO remember past mines, perhaps fix if we run into someone as we are running away
	
	static FlyingDroneActions obj = FlyingDroneActions.EQUIPPING;
	
	static boolean hasSight = false;
	static boolean hasConstructor = false;
	static boolean foundID = false;
	boolean builtRefineryChassis = false;
	boolean setRunAwayDirection=false;
	boolean foundVoids=false;
	int ID=-1;
	MapLocation towerPlacement;
	MapLocation spawnLocation;
	Mine currentMine;
	MapLocation currentBroadcastedMine;
	double prevRoundHP=10.0;
	boolean seenOtherSide;
	static boolean knowEverything;
	boolean returnedHome;
	int timeTrying=0;
	int timeout=0;
	int steps=0;
	int waiting=0;
	ArrayList<MapLocation> broadcastedMines= new ArrayList<MapLocation>();
	int triedDirections=0;
	
	Direction initialDirection;
	Direction currentDirection;
	Direction towerDirection;

	int runAwayTime=0;

	public FlyingDroneBehavior(RobotPlayer player) {
		super(player);
	}
	
	@Override
	public void run() throws Exception {

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
    			if (!myPlayer.myMotor.isActive()) {
    				Mine[] detectedMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
        			for (Mine mine : detectedMines) { //look for mines, if we find one, lets go get it
        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)==null) {
            					currentMine=mine;
            					towerDirection=currentMine.getLocation().directionTo(spawnLocation).opposite();
            					towerPlacement=currentMine.getLocation().add(towerDirection);
            					currentDirection=myPlayer.myRC.getDirection();
            					obj=FlyingDroneActions.FOUND_MINE;
            					return;
        				}
        			}
        			droneGeneralNav(ID);
    			}
    			return;
    		}
    		
    		case FOUND_MINE: {
    			Utility.setIndicator(myPlayer, 0, "found mine");
				if (myPlayer.mySensor.senseObjectAtLocation(currentMine.getLocation(), RobotLevel.ON_GROUND)!=null) { //someone is on our mine, gonna just look for other ones
					if (!myPlayer.myMotor.isActive()) {
						myPlayer.myMotor.setDirection(initialDirection);
						obj=FlyingDroneActions.EXPAND;
						return;
					}
				}
				else if (currentMine.getLocation().equals(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection())) || (myPlayer.myRC.getLocation().equals(currentMine.getLocation()))) { //i'm right by the mine, build recycler!
    					if ( myPlayer.myRC.getTeamResources() > Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE && !myPlayer.myMotor.isActive()){
    						Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()), Chassis.BUILDING);
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
    	        			
    	        			
        					for (int i=0;i<3;i++) { //spin and check all our other directions
        						Mine[] nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
        	        			for (Mine mine : nearbyMines) { //look for mines, if we find one, lets go get it
        	        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)==null) {
        	        					while (myPlayer.myMotor.isActive()) {
        	        						myPlayer.sleep();
        	        					}
        	            				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(mine.getLocation()));
                    					currentMine=mine;
                    					towerDirection=currentMine.getLocation().directionTo(spawnLocation).opposite();
                    					towerPlacement=currentMine.getLocation().add(towerDirection);
                    					currentDirection=myPlayer.myRC.getDirection();
                    					obj=FlyingDroneActions.FOUND_MINE;
                    					return;
        	        				}
        	        			}
	        					while (myPlayer.myMotor.isActive()) {
	        						myPlayer.sleep();
	        					}
	        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
        					}
        					
        					while (myPlayer.myMotor.isActive()) {
        						myPlayer.sleep();
        					}
    						myPlayer.myMotor.setDirection(initialDirection);
    					/*	if (steps>Constants.STEPS) {
    							obj =  FlyingDroneActions.BUILD_TOWER;
    						}*/
/*    						else {*/
    							obj = FlyingDroneActions.EXPAND;
/*    						}*/
    					}
    			}
    			else {
    				if (!myPlayer.myMotor.isActive())  { //move closer to the mine
    					if (myPlayer.myRC.getDirection().equals(myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()))) {
            				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
            					myPlayer.myMotor.moveForward();
            					steps=steps+1;
            				}
    					}
    					else {
    						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()));
    					}
    				}
    				return;
    			}
    			return;
    		}
    		case BUILD_TOWER:
    			Utility.setIndicator(myPlayer, 0, "building AP");
    			Utility.setIndicator(myPlayer, 1, "tower Location:" + towerPlacement);
    			Utility.setIndicator(myPlayer, 2, "my Location:" + myPlayer.myRC.getLocation());
    			if (triedDirections==7) {
    				obj= FlyingDroneActions.EXPAND;
    			}
    			else if (myPlayer.mySensor.withinRange(towerPlacement)) {
    				if (myPlayer.myRC.senseTerrainTile(towerPlacement).equals(TerrainTile.LAND) && myPlayer.mySensor.senseObjectAtLocation(towerPlacement, RobotLevel.ON_GROUND)==null) {
    					if (myPlayer.myBuilder.withinRange(towerPlacement)) {
    						if (myPlayer.myRC.getTeamResources() >Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + 2 * Constants.RESERVE) {
        						triedDirections=0;
        						Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(towerPlacement), Chassis.BUILDING);
        						triedDirections=0;
        						obj =  FlyingDroneActions.EXPAND;
    						}
    					}
    					else {
    						if (!myPlayer.myMotor.isActive()) {
    							if (myPlayer.myRC.getDirection().equals(myPlayer.myRC.getLocation().directionTo(towerPlacement))) {
    								if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
    									myPlayer.myMotor.moveForward();
    									steps=steps+1;
    								}
    							}
    							else {
    								myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(towerPlacement));
    							}
    						}
    					}
    				}
    				else {
    					towerDirection=towerDirection.rotateRight();
    					towerPlacement=currentMine.getLocation().add(towerDirection);
    					triedDirections=triedDirections+1;
    				}
    			}
    			else {
					if (!myPlayer.myMotor.isActive()) {
						if (myPlayer.myRC.getDirection().equals(myPlayer.myRC.getLocation().directionTo(towerPlacement))) {
							if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
								myPlayer.myMotor.moveForward();
								steps=steps+1;
							}
						}
						else {
							myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(towerPlacement));
						}
					}
    			}
    			return;
    		
    		case RUN_AWAY: {
    			Utility.setIndicator(myPlayer, 0, "run away counter : " + runAwayTime);
    			int totalX=0;
    			int totalY=0;
    			int totalEnemyRobots=0;
    			boolean enemyInFront=false;
    			RobotInfo rInfo;
    			runAwayTime=runAwayTime+1;
    			if (runAwayTime>Constants.RUN_AWAY_TIME) {
    				runAwayTime=0;
    				setRunAwayDirection=false;
    				foundVoids=false;
    				obj=FlyingDroneActions.EXPAND;
    				return;
    			}
    			Robot[] detectedRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for (Robot robot : detectedRobots) {
    				rInfo=myPlayer.mySensor.senseRobotInfo(robot);
    				if (robot.getTeam().equals(myPlayer.myRC.getTeam().opponent()) && rInfo.on) {
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
        					steps=steps +1;
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
		if (type.equals(MsgType.MSG_SEND_NUM_FLYER) && ID==-1) {
			ID=msg.ints[Messenger.firstData+1]%8;
			foundID=true;
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
		Utility.setIndicator(myPlayer, 2, initialDirection.toString());
		try {
			Utility.bounceNavForFlyers(myPlayer);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	public void setDirectionID(int ID) throws GameActionException {
		if (myPlayer.myRC.getTeam().equals(Team.A)) {
			myPlayer.myMotor.setDirection(Direction.values()[((ID*3)+4)%8]);
		}
		else {
			myPlayer.myMotor.setDirection(Direction.values()[(ID*3)%8]);
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

	public void onDamageCallback(double damageTaken) {
		if (!obj.equals(FlyingDroneActions.EQUIPPING) && !obj.equals(FlyingDroneActions.FLYING_DRONE_ID)) {
			obj=FlyingDroneActions.RUN_AWAY;
		}
	}
	@Override
	public void onWakeupCallback(int lastActiveRound) {
		// TODO Auto-generated method stub
		
	}

}