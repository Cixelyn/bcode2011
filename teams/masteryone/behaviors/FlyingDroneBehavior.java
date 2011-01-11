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
	
	Direction initialDirection;

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
    		}
    		return;
    		case FLYING_DRONE_ID: {
    			if (foundID) {
    				setDirectionID(ID);
    			}
    		}
    		return;
    		case EXPAND: {
    			if (!myPlayer.myMotor.isActive()) {
        			for (Mine mine : myPlayer.myScanner.detectedMines) {
        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)!=null) {
        					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(mine.getLocation()));
        					currentMine=mine;
        					obj=FlyingDroneActions.FOUND_MINE;
        					return;
        				}
        			}
        			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)){
        				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
        				return;
        			}
        			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)){
        				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
        			}
        			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),1)).equals(TerrainTile.OFF_MAP)){
        				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
        			}
    				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
    					myPlayer.myMotor.moveForward();
    				}
    			}
    			return;
    		}
    		case FOUND_MINE: {
				if (myPlayer.mySensor.senseObjectAtLocation(currentMine.getLocation(), RobotLevel.ON_GROUND)!=null) {
					obj=FlyingDroneActions.EXPAND;
					return;
				}
    			if (currentMine.getLocation().equals(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()))) {
    				if (myPlayer.myRC.getTeamResources()>(ComponentType.RECYCLER.cost+Chassis.BUILDING.cost)) {
    					Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
    					Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.MINE);
    				}
    			}
    			else {
    				if (!myPlayer.myMotor.isActive())  {
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
	
	public void setDirectionID(int ID) throws GameActionException {
		if (ID==0) {
			myPlayer.myMotor.setDirection(Direction.SOUTH_WEST);
		}
		if (ID==1) {
			myPlayer.myMotor.setDirection(Direction.SOUTH_EAST);
		}
		if (ID==2) {
			myPlayer.myMotor.setDirection(Direction.NORTH);
		}
	}

}
