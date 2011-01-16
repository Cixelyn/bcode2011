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
	
	boolean[] hasSeenRobot = new boolean[1024];
	
	
	
	public TestHeavyBehavior(RobotPlayer player)
	{
		super(player);
		
		
		//lets initialize our map navigation variables.
		mapLeftEdge = myPlayer.myBirthplace.x;
		mapRightEdge = myPlayer.myBirthplace.x;
		mapBottomEdge = myPlayer.myBirthplace.y;
		mapTopEdge = myPlayer.myBirthplace.y;
		
			
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
				hasJump = false;
				hasSatellite = false;
				hasRegen = false;
				int numBlasters = 0;
				for ( int i = myPlayer.myRC.components().length; --i>= 0;)
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.JUMP )
						hasJump = true;
					if ( c.type() == ComponentType.SATELLITE )
						hasSatellite = true;
					if ( c.type() == ComponentType.REGEN )
						hasRegen = true;
					if ( c.type() == ComponentType.BLASTER )
						numBlasters++;
				}
				if ( hasJump && hasSatellite && hasRegen && numBlasters >= 2 )
					obj = TestHeavyBuildOrder.MOVE_OUT;
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
	        	

	        	if(nearestEnemyRobot!=null) {
	        		
	        	}
	        	
	        	
	        	
	        	Utility.bounceNav(myPlayer);
	        	return;
	        	
		}
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
