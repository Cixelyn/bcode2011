package fibbyBot14.behaviors;

import java.util.*;


import fibbyBot14.*;
import battlecode.common.*;


public class FlyingDroneBehavior extends Behavior {
	/**
	 * <pre>
	 * 
	 * 
	 *                                           
	 *                                           
	 *                                           
	 *                                           
	 *                                           .                                     
                                                                                
                                                                                
                                     ..                                         
                                 .MMM .MN                                       
                      .  MMMM ..    ,..,.M.                      ......         
                    .MMM .   M M.N..DMM,  MM                    MM.M..M.        
                    .M.MNM.   MN  .M  MMMM  N                  M.   M  M        
                    .MM...    .M    M   ..   M                M..   .M  M       
                    .M M .NMN.  MN.. MN  .....M             M        M  ,M      
                    .M .M.     ..MM.. . M......M.          M          M .M.     
                    .M   M   MM . N.M  ...   ..M..       .M.           M..N.    
                    .M   .MD..  ..M .MM.N.    .M. MM     . .           M  M.    
                    .M   ..M. . .NM..MMM.  MM..M    .MMN.M           .M  M      
     .              .M  .MMM. M...M..     ,,.M..M       .MMMMM.      MM.M.      
  .MM.M..           .M.M . MM.   . MM...MMM  MM  M.          ,M.    .N .M.      
   M. .MMM          .M,      M.  .   M.      MMMNMDMM         M.   .M, M,       
   M   M  M         M          M  MM M... , NMNM ,    MMM.   M    .M. M         
    M, .M  .N    .M.             M.MMMMMMM     . M        NMM.    M   M         
   ..   .M   N...M               M..  .. M.       .M.       MM .  M  M          
     M...NM.  .M .             M.         M         M         ..MM  M           
      MN.. M   M              MM.         .M.       M        .M..M  M.          
       M.. .M,.,M            N.M           .M.      .M:       M..M.M            
        M.  .M   N          MM. ,          ..M       .M      M   .N.M.          
         M.   M   M.      D..M.,M            MM        M .NM     .M.M.          
        . M  . M   M     M.   M. M.           M        .M .      .M.M.          
           M    M   M  .M...M  M...MMM..  ...MNN.       .M       .D.M,          
           .M    M    .M   N    .MM,....  ... .M.        .MM  ..,MMM            
             M    M      M.NM      NM           M       .MMNM...M.M:.           
              M, ..M.     MN.MM    .M           MM  MMM. .. . ..N. M            
               N.   M    ..M . MMMNMM    ..   MMM ..          ..N. M.           
               ,M    M ..NMM  .M.  .M M MMM.M.                ..N.  N           
                 M.. .M..   .M.M. M M...     M                ..M..M            
                  MM, M      .MMNM  .,M.      M               ..MMM M.          
                    N         .M .M  ...M      M     .. .  MMMM.. .M            
                              .M    MMMMMM     M  ..MMMMM     .....N M          
                                MM .M....M     .MNM.        MM..  M.M.          
                                . MM ...MMMM,  .N.   MM.M  M,    .MMM.          
                                    M   N.. M  M.MM,..  .M.M.M MM M.M.          
                                     .MMM    ,MMMMM....MMM.M M    M.M.          
                                      ..MN.  M.  M DM.., M.M M    M M.          
                                          M.M.  .M MM    N.M M    MM..          
                                            MM. .M MM    M.M M    M             
                                               ,MN MN .  MMN.M.MMM              
                                                ..MMM. .MM  ..                  
     *                                              . . .    ..                                           
	 *                                                                               
	 * 
	 * </pre>
	 * Basic flying constructor, attempts to take mines and run away from enemies (and yes, I'm aware that the above picture is actually a Valkyrie). 
	 * @author Max
	 *  
	 **/
	
	private enum FlyingDroneActions
	{
		
		EQUIPPING,
		FLYING_DRONE_ID,
		FOUND_MINE,
		RUN_AWAY,
		BUILD_TOWER,
		SCRAMBLE,
		EXPAND;

	}
	

	
	static FlyingDroneActions obj = FlyingDroneActions.EQUIPPING;
	
	static boolean hasSight = false;
	static boolean hasConstructor = false;
	static boolean foundID = false;
	boolean builtRefineryChassis = false;
	boolean setRunAwayDirection=false;
	boolean foundVoids=false;
	boolean hasBeenScrambled=false;
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
	int zigzagCounter=0;
	int zigzag=1;
	Direction initialDirection;
	Direction currentDirection;
	Direction towerDirection;

	int runAwayTime;

	public FlyingDroneBehavior(RobotPlayer player) {
		super(player);
		runAwayTime=0;
	}
	
	@Override
	public void run() throws Exception {

    	switch (obj) {
    	
    		case EQUIPPING: { //initial state, will get the sight and constructor that is needed to cap mines!
    			if (spawnLocation==null) {
    				spawnLocation=myPlayer.myRC.getLocation();
    			}
    			if (hasSight && hasConstructor) { //finally we are good to go, but we first need to get our ID
    				obj=FlyingDroneActions.FLYING_DRONE_ID;
    			}
    			return;
    		}
    		case FLYING_DRONE_ID: {  //receive ID from refinery, the ID will decide the drones initial direction so that we can split up as much as possible
    			if (foundID) {
    				if (!myPlayer.myMotor.isActive()) {
    					setDirectionID(ID);
    					obj=FlyingDroneActions.EXPAND; //Now its time to go expanding!
    				}
    			}
    			return;
    		}
    		case EXPAND: { //Main state where we are searching for mines, if we find a mine, we go to "FOUND_MINE" state
    			if (Clock.getRoundNum()-myPlayer.myBirthday>Constants.SCRAMBLE_TIME && !hasBeenScrambled) { //after a certain time, the drones will migrate to the center of the map along with the collosus, hopefully taking more mines along the way
    				//Utility.setIndicator(myPlayer, 0, "BEEN SCRAMBLED!");
    				hasBeenScrambled=true;
    				while (myPlayer.myMotor.isActive()) {
    					myPlayer.sleep();
    				}
    				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(myPlayer.myCartographer.getMapCenter()));
    			}
    			if (initialDirection==null) { //initial check our initial direction in the direction that was determined by our ID
    				initialDirection=myPlayer.myRC.getDirection();
    			}
    			if (!myPlayer.myMotor.isActive()) {
    				Mine[] detectedMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
        			for (Mine mine : detectedMines) { //look for mines, if we find one, lets go get it
        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)==null) {
            					currentMine=mine;
            					towerDirection=currentMine.getLocation().directionTo(spawnLocation).opposite(); //old code when we were actually making towers at expansions
            					towerPlacement=currentMine.getLocation().add(towerDirection);  //old code when we were actually making towers at expansions
            					currentDirection=myPlayer.myRC.getDirection(); //store the drection we were going so we can continue going in that direction after we take the mine
            					obj=FlyingDroneActions.FOUND_MINE;
            					return;
        				}
        			}
        			droneGeneralNav(ID); //if we don't find a mine, just look around as normal with some zig zagging.
    			}
    			return;
    		}
    		
    		case FOUND_MINE: {
    			//Utility.setIndicator(myPlayer, 0, "found mine");
				if (myPlayer.mySensor.senseObjectAtLocation(currentMine.getLocation(), RobotLevel.ON_GROUND)!=null) { //someone is on our mine, gonna just look for other ones
					obj=FlyingDroneActions.EXPAND;
					return;
				}
				Robot[] nearbyRobots= myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
				RobotInfo rInfo;
				for (Robot robot : nearbyRobots) {  //check to make sure one of our own flyers isn't already taking the mine i'm taking
					rInfo=myPlayer.mySensor.senseRobotInfo(robot);
					if (robot.getTeam().equals(myPlayer.myRC.getTeam()) && rInfo.chassis.equals(Chassis.FLYING)) {
						currentDirection=Direction.NORTH;
						for (int i=0;i<8;i++) {
							if (rInfo.location.equals(currentMine.getLocation().add(currentDirection))) {
	        					while (myPlayer.myMotor.isActive()) {
	        						myPlayer.sleep();
	        					}
	        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
	        					obj=FlyingDroneActions.EXPAND;
	        					return;
							}
							currentDirection=currentDirection.rotateRight();
						}
					}
				}
				if (currentMine.getLocation().equals(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection())) || (myPlayer.myRC.getLocation().equals(currentMine.getLocation()))) { //i'm right by the mine, build recycler!
    					if ( myPlayer.myRC.getTeamResources() > Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE && !myPlayer.myMotor.isActive()){
    						Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()), Chassis.BUILDING); //build building chassis 
    						Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()), ComponentType.RECYCLER, RobotLevel.ON_GROUND); //build recycler component
    	        			
    	        			
        						Mine[] nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
        	        			for (Mine mine : nearbyMines) { //look for mines while we are here, if we find one, lets go get it
        	        				if (myPlayer.mySensor.senseObjectAtLocation(mine.getLocation(), RobotLevel.ON_GROUND)==null) {
        	        					while (myPlayer.myMotor.isActive()) {
        	        						myPlayer.sleep();
        	        					}
        	        					
        	        					
        	        					if (mine.getLocation().equals(myPlayer.myRC.getLocation())) { //I'm right on top of a mine !
        	        						while (myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE && !myPlayer.myMotor.isActive()) {
                        						myPlayer.sleep();
                        					}
        	        						Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()), Chassis.BUILDING); //build building chassis 
        	        						Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(currentMine.getLocation()), ComponentType.RECYCLER, RobotLevel.ON_GROUND);  //build recycler component
        	        						obj=FlyingDroneActions.FOUND_MINE;
        	        						return;
        	        					}
        	        					
        	        					
        	        					
        	            				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(mine.getLocation()));
                    					currentMine=mine;
                    					obj=FlyingDroneActions.FOUND_MINE;
                    					return;
        	        				}
        	        			}
        					
        					while (myPlayer.myMotor.isActive()) {
        						myPlayer.sleep();
        					}
        					myPlayer.myMotor.setDirection(currentDirection); //resume our direction that we were going before we found the mine
    					/*	if (steps>Constants.STEPS) {
    							obj =  FlyingDroneActions.BUILD_TOWER;  //old code when we built towers
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
            					steps=steps+1;  //for zigzagging
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
    		case BUILD_TOWER: //OLD CODE, we no longer build towers
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
    		
    		case RUN_AWAY: { //We've been hit, lets do our best to run away from the enemy!
    			//Utility.setIndicator(myPlayer, 0, "run away counter : " + runAwayTime);
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
    			for (Robot robot : detectedRobots) {  //get average map location of all the enemies in vision
    				rInfo=myPlayer.mySensor.senseRobotInfo(robot);
    				if (robot.getTeam().equals(myPlayer.myRC.getTeam().opponent()) && rInfo.on) {
    					totalX=totalX+rInfo.location.x;
    					totalY=totalY+rInfo.location.y;
    					totalEnemyRobots=totalEnemyRobots+1;
    					enemyInFront=true;
    				}
    			}
    			if (enemyInFront) { //we see an enemy, lets turn around in the correct direction!
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
    			else { //we don't see any enemies, lets just fly over the zone with the most voids
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
		if (type.equals(MsgType.MSG_SEND_NUM) && ID==-1) { ///received ID, lets set our ID and never receive another message ever again
			ID=msg.ints[Messenger.firstData+1]%8;
			foundID=true;
			myPlayer.myMessenger.toggleReceive(false);
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
		try {
			if (zigzagCounter==Constants.ZIGZAG_STEPS) { // We are ready to zig or zag!
				zigzagCounter=0;
				if (zigzag==1) { //ZIG
					Utility.bounceNavForFlyers(myPlayer,zigzag);
					zigzag=2;
				}
				else if (zigzag==2) { //ZAG
					Utility.bounceNavForFlyers(myPlayer,zigzag);
					zigzag=1;
				}
			}
			else { //otherwise just bounce smartly
				int bounce = Utility.bounceNavForFlyers(myPlayer, 0); 
				if (bounce!=0) {
					zigzagCounter=0;
					zigzag=bounce;
					Utility.setIndicator(myPlayer, 1, zigzag+"");
				}
				else {
					zigzagCounter=zigzagCounter+1;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	public void setDirectionID(int ID) throws GameActionException {  //set initial direction from our given ID so we can spread out
		if (myPlayer.myRC.getTeam().equals(Team.A)) {
			myPlayer.myMotor.setDirection(Direction.values()[((ID*3)+4)%8]);
		}
		else {
			myPlayer.myMotor.setDirection(Direction.values()[(ID*3)%8]);
		}
	}
	
	
	
	public Direction getMostVoidsDirection() {  //function for finding the most voids in our direction.
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

	public void onDamageCallback(double damageTaken) { //will be called when we take damage
		if (!obj.equals(FlyingDroneActions.EQUIPPING) && !obj.equals(FlyingDroneActions.FLYING_DRONE_ID)) {
			obj=FlyingDroneActions.RUN_AWAY;
		}
	}
	@Override
	public void onWakeupCallback(int lastActiveRound) {		
	}

}