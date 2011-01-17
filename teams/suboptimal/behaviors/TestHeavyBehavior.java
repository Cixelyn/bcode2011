package suboptimal.behaviors;

import suboptimal.*;
import battlecode.common.*;


public class TestHeavyBehavior extends Behavior
{
	
	
	private enum TestHeavyBuildOrder
	{
		EQUIPPING,
		MOVE_OUT
	}
	
	private TestHeavyBuildOrder obj = TestHeavyBuildOrder.EQUIPPING;
	
	int num;	
	boolean hasJump;
	boolean hasSatellite;
	boolean hasRegen;
	int numBlasters;
	private final OldNavigation myNav;
	
	boolean[] hasSeenRobot = new boolean[1024];
	
	
	
	
	private int[] componentLoadOut;
	
	
	public TestHeavyBehavior(RobotPlayer player)
	{
		super(player);
		
		
		//lets initialize our map navigation variables.
		mapLeftEdge = myPlayer.myBirthplace.x;
		mapRightEdge = myPlayer.myBirthplace.x;
		mapBottomEdge = myPlayer.myBirthplace.y;
		mapTopEdge = myPlayer.myBirthplace.y;
		
		
		//initialize our old navigation engine
		myNav = new OldNavigation(player);
		
		
		componentLoadOut = Utility.countComponents(new ComponentType[]    
		                               {ComponentType.JUMP,
										ComponentType.SATELLITE,
										ComponentType.REGEN,
										ComponentType.BLASTER,
										ComponentType.BLASTER});
	}

	
	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				if(Utility.compareComponents(myPlayer, componentLoadOut)) {
					obj = TestHeavyBuildOrder.MOVE_OUT;
				}
				return;
	        	
			
				
				
				
			/*
			 * Our main movement & attack code.
			 */
			case MOVE_OUT:				
	        	satelliteScanMapEdge();
	        	
	        	
	        	
	        	//RUN SUPER SPECIAL CUSTOM SENSOR CODE
	        	Robot[] robots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
	        	
	        	
	        	//QUICK CODE FOR JUST FINDING THE NEAREST ROBOT
	        	//FIXME: I'll eventually get around to adding some extra thing;
	        	//		TODO:add prioritization based on components, etc.	        	
	        	Robot 		nearestEnemyRobot			= null;
	        	RobotInfo	nearestEnemyRobotInfo		= null;
	        	int			nearestEnemyRobotDistance	= 999;
	        	Direction	nearestEnemyRobotDirection  = null;
	        	
	        	MapLocation myLoc = myPlayer.myRC.getLocation();
	        	
	        	for(int i=robots.length; --i>=0;) {
	        		if(robots[i].getTeam()==myPlayer.myOpponent) {
	        			RobotInfo rinfo = myPlayer.mySensor.senseRobotInfo(robots[i]);
	        			
	        			int robotDistance = myLoc.distanceSquaredTo(rinfo.location);
	        			if(robotDistance < nearestEnemyRobotDistance) {
	        				nearestEnemyRobot = robots[i];
	        				nearestEnemyRobotInfo = rinfo;
	        				nearestEnemyRobotDistance = robotDistance;
	        				nearestEnemyRobotDirection = myLoc.directionTo(nearestEnemyRobotInfo.location);
	        			}
	        		}
	        	}
	        	
	        	
	        	//I AM ENGAGED IN BLOODY COMBAT	
	        	if(nearestEnemyRobot!=null) {	
	        		
	        		
	
	        		
	        		//HOW FAR AWAY IS THE ENEMY
	        		if(nearestEnemyRobotDistance >64 ) { 		//checks range: (64,inf)
	        			myPlayer.myActions.moveInDir(myNav.bugTo(nearestEnemyRobotInfo.location));
	        		}else if(nearestEnemyRobotDistance >16) { 	// checks range: (16,64]
	        			
	        			if(strikeReady()) { //if i can jump in, attack, and move
	        				
	        				
	        				//Jump
	        				//Fire
	        				//Step back
	        				
	        				
	        				return;
	        			} else {	//we sleep
	        				return;
	        			}
	        			
	        			
	        			
	        		}
	        		else if(nearestEnemyRobotDistance<=16) {	// checks range: [0,16]
							for(WeaponController w:myPlayer.myWeapons) { 
								if(!w.isActive() && w.withinRange(nearestEnemyRobotInfo.location)) {	//FIXME: Overkill if using more than one weapon
									w.attackSquare(nearestEnemyRobotInfo.location, nearestEnemyRobot.getRobotLevel());
								}
							}
					}
					
					
					//if I'm too closet to enemy units, move back
					if(nearestEnemyRobotDistance<=16 && nearestEnemyRobotDistance > 9) {
						return;  //I'm good					
					} else if(nearestEnemyRobotDistance<=9) {					//I'm too close!
						myPlayer.myActions.backUpInDir(nearestEnemyRobotDirection.opposite());
					} else { //I'm too far
						myPlayer.myActions.moveInDir(myNav.bugTo(nearestEnemyRobotInfo.location));
					}
					return;
					
				} else{														//I am not engaged in bloody combat!
						Utility.setIndicator(myPlayer, 1, "Bounce!");
		        		Utility.bounceNav(myPlayer);
		        	return;
				}
		}
	}
	
	
	
	public boolean strikeReady() {
		if(myPlayer.myMotor.isActive()) return false;
		if(myPlayer.myWeapons[0].isActive()) return false;  //FIXME: fix hardcodededness of this eventually
		if(myPlayer.myWeapons[1].isActive()) return false;
		return true;
	}
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	///////////// MAP POSITION ESTIMATION CODE /////////////////////////////////////////
	
	
	//Booleans whether edges are ofund
	boolean foundTopEdge = false;
	boolean foundRightEdge = false;
	boolean foundBottomEdge = false;
	boolean foundLeftEdge = false;
	
	//Edges of the map (or farthest location if edges haven't been found)
	int mapLeftEdge;
	int mapRightEdge;
	int mapBottomEdge;
	int mapTopEdge;
	
	
	//Quick constant here for some faster access
	private static final int SATRANGE = ComponentType.SATELLITE.range;
	
	
	
	/**
	 * 
	 * We only need to scan for edges that we have yet to detect.
	 * This code is optimized for and only works w/ satellites
	 * 
	 * Map edges are scanned, and results are stored into the
	 * mapXXX,foundXXX variables.
	 *  
	 */
	public void satelliteScanMapEdge() {
		
		MapLocation toScan;
		MapLocation currLoc = myPlayer.myRC.getLocation();
		int myX = currLoc.x;
		int myY = currLoc.y;
		
		if(!foundTopEdge) {
			toScan = new MapLocation(myX,myY-SATRANGE);
			if(myPlayer.myRC.senseTerrainTile(toScan)==TerrainTile.OFF_MAP) {
				foundTopEdge = true;
				mapTopEdge = scanForLand(toScan,0,-1).y;
			} else {
				if(myY<mapTopEdge) mapTopEdge=myY;
			}
		}
		if(!foundRightEdge) {
			toScan = new MapLocation(myX+SATRANGE,myY);
			if(myPlayer.myRC.senseTerrainTile(toScan)==TerrainTile.OFF_MAP) {
				foundRightEdge = true;
				mapRightEdge= scanForLand(toScan,+1,0).x;
			} else{
				if(myX>mapRightEdge) mapRightEdge = myX;
			}
		}
		if(!foundBottomEdge) {
			toScan = new MapLocation(myX,myY+SATRANGE);
			if(myPlayer.myRC.senseTerrainTile(toScan)==TerrainTile.OFF_MAP) {
				foundBottomEdge = true;
				mapBottomEdge = scanForLand(toScan,0,+1).y;				
			} else{
				if(myY>mapBottomEdge) mapBottomEdge = myY;
			}
		}
		if(!foundLeftEdge) {
			toScan = new MapLocation(myX-SATRANGE,myY);
			if(myPlayer.myRC.senseTerrainTile(toScan)==TerrainTile.OFF_MAP) {
				foundLeftEdge = true;
				mapLeftEdge = scanForLand(toScan,-1,0).x;
			} else{
				if(myX<mapLeftEdge) mapLeftEdge = myX;
			}
		}
	}
	
	
	
	
	/**
	 * This function returns the first maplocation that is on-tile and not a void.
	 * Used in conjunction with satelliteScanMapEdge in order to find the true map edge.
	 * @param startLoc
	 * @param dX
	 * @param dY
	 * @return MapLocation that is on tile.
	 */
	public MapLocation scanForLand(MapLocation startLoc, int dX, int dY) {
		do {
			startLoc = new MapLocation(startLoc.x + dX, startLoc.y + dY);
		}while(myPlayer.myRC.senseTerrainTile(startLoc)==TerrainTile.OFF_MAP);
		return startLoc;
	}
	
	
	/**
	 * This function uses collected sensor data to estimate where the center of the map is.
	 * @return
	 */
	public MapLocation estimateCenter(){
		return null;
	};
	
	
	

	
	/////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////
	///////////// CALLBACKS /////////////////// /////////////////////////////////////////
	
	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM_HEAVY )
		{
			if ( num == -1 )
			{
				num = msg.ints[Messenger.firstData+1];
				Utility.setIndicator(myPlayer, 2, "I'm heavy " + Integer.toString(num) + "!");
			}
		}
	}
	
	public void onWakeupCallback(int lastActiveRound) {}
	
	public void onDamageCallback(double damageTaken)  {}

	
	
	
	
	
	
	

	
	public String toString()
	{
		return "TestHeavyBehavior";
	}


	
}
