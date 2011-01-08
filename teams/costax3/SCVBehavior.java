package costax3;

import battlecode.common.*;
import java.util.*;

public class SCVBehavior extends Behavior {
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation hometown = myPlayer.myRC.getLocation();
	MapLocation enemyLocation; 
	MapLocation mainDestination;
	MapLocation tempDestination;
	MapLocation[] waypoints = new MapLocation[3];
	
	Direction waypointDir1;
	Direction waypointDir2;
	Direction[] waypointDirs;
	
	SCVBuildOrder obj = SCVBuildOrder.FIND_MINE;
	
	int westEdge = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int dizziness = 0;
	int tiredness = 0;
	int minesCapped = 2;
	int currWaypointIdx = 0;
	int nextWaypointIdx;
	
	boolean eeHanTiming;
	boolean mineFound;
	
	MineInfo mInfo;
	Mine[] nearbyMines;
	RobotInfo rInfo;
	Robot[] nearbyRobots;
	
	ArrayList<Integer> badMines = new ArrayList<Integer>(); // mines SCV will not consider capping for whatever reason
	
	Message attackMsg;
	
	int spawn = -1; // 0 is west, increments clockwise; 8 means "i dont know"
	
	
	public SCVBehavior(RobotPlayer player) {
		super(player);
	}

	

	public void run() throws Exception {
    	switch (obj)
    	{
    		case FIND_MINE:
    			myPlayer.myRC.setIndicatorString(1, "FIND_MINE");
    			mineFound = false;
    			nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
    			for (Mine m:nearbyMines)
    			{
    				if(!mineFound && m.getTeam()==Team.NEUTRAL)
        			{
        				mInfo = myPlayer.mySensor.senseMineInfo(m);
        				if(myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null && !badMines.contains(mInfo.mine.getID()))
        				{
            				mineFound = true;
            				tempDestination = mInfo.mine.getLocation();
        				}
        			}
    			}
    			if(!mineFound && dizziness < 4)
    			{
    				while (myPlayer.myMotor.isActive())
    					myPlayer.myRC.yield();
    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    				dizziness++;
    			}
    			if(!mineFound && dizziness >= 4)
    			{
    				dizziness = 0;
    				
    				// the below checks if either we've arrived at the destination
    				// OR we're not going home and the proper off_map edge is found:
    				// the result is we set destination to the next waypoint, defining waypoint if need be
    				// of course, we only run this after spawn is known
    				// -jven
    				
    				if ( spawn != -1 && (myPlayer.myRC.getLocation().distanceSquaredTo(mainDestination) < Constants.HOME_PROXIMITY || currWaypointIdx != 0) )
    				{
    					if(currWaypointIdx == 0) // we do not reset hometown
    					{
    						if (waypoints[1] == null)
    							waypoints[1] = waypoints[0].add(waypointDir1, Utility.dirSize(waypointDir1));
    						nextWaypointIdx = (currWaypointIdx + 1) % (waypoints.length);
        					currWaypointIdx = nextWaypointIdx;
        					mainDestination = waypoints[currWaypointIdx];
    					}
    					if(currWaypointIdx == 1 && myPlayer.myRC.senseTerrainTile( myPlayer.myRC.getLocation().add(waypointDir1, 3) ) == TerrainTile.OFF_MAP)
    					{
    						waypoints[1] = myPlayer.myRC.getLocation();
    						if (waypoints[2] == null)
    							waypoints[2] = waypoints[1].add(waypointDir2, Utility.dirSize(waypointDir2));
    						nextWaypointIdx = (currWaypointIdx + 1) % (waypoints.length);
        					currWaypointIdx = nextWaypointIdx;
        					mainDestination = waypoints[currWaypointIdx];
    					}
    					if(currWaypointIdx == 2 && myPlayer.myRC.senseTerrainTile( myPlayer.myRC.getLocation().add(waypointDir2, 3) ) == TerrainTile.OFF_MAP) // waypoints[0] should already be set to hometown
    					{
    						waypoints[2] = myPlayer.myRC.getLocation();
    						nextWaypointIdx = (currWaypointIdx + 1) % (waypoints.length);
        					currWaypointIdx = nextWaypointIdx;
        					mainDestination = waypoints[currWaypointIdx];
    					}
    				}
    				
    				obj = SCVBuildOrder.EXPAND;
    			}
    			if(mineFound)
    			{
    				dizziness = 0;
    				if (minesCapped == 2)
    					obj = SCVBuildOrder.WAIT_FOR_ANTENNA;
    				else
    					obj = SCVBuildOrder.CAP_MINE;
    			}
    			return;
    			
    		case WAIT_FOR_ANTENNA:
    			myPlayer.myRC.setIndicatorString(1, "WAIT_FOR_ANTENNA");
    			for(ComponentController c:myPlayer.myRC.components())
    			{
    				if (c.type() == Constants.COMMTYPE)
    				{
    					myPlayer.myBroadcaster = (BroadcastController)c;
    					myPlayer.myMessenger.enableSender();
    					obj = SCVBuildOrder.CAP_MINE;
    				}
    			}
    			return;
    			
    		case CAP_MINE:
    			myPlayer.myRC.setIndicatorString(1, "CAP_MINE");
    			if(!myPlayer.mySensor.withinRange(mInfo.mine.getLocation()) || myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(tempDestination)>myPlayer.myBuilder.type().range)
        				Utility.navStep(myPlayer, robotNavigation, tempDestination);
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(tempDestination)<=myPlayer.myBuilder.type().range)
        			{
        				while(myPlayer.myMotor.isActive())
							myPlayer.myRC.yield();
        				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(tempDestination));
        				myPlayer.myRC.yield(); // must yield before building since turning occurs at end of turn!
        				Utility.buildChassis(myPlayer, Chassis.BUILDING);
        				minesCapped++;
        				obj = SCVBuildOrder.ADDON_MINE;
        				tiredness = 0;
        			}
    			}
    			else
    			{
    				obj = SCVBuildOrder.FIND_MINE;
    				tiredness = 0;
    			}
    			tiredness++;
    			if(tiredness > Constants.MINE_AFFINITY)
    			{
    				minesCapped++; // in case one or more of initial mines are bad
    				badMines.add(mInfo.mine.getID());
    				if(minesCapped == 4)
    					obj = SCVBuildOrder.RETURN_HOME;
    				else
    					obj = SCVBuildOrder.FIND_MINE;
    				tiredness = 0;
    			}
    			return;
    			
    		case ADDON_MINE:
    			myPlayer.myRC.setIndicatorString(1, "ADDON_MINE");
    			Utility.buildComponent(myPlayer, ComponentType.RECYCLER);
    			if(minesCapped>=4)
    			{
    				if (minesCapped == 4)
    				{
    					obj = SCVBuildOrder.SCOUT_WEST;
    					myPlayer.myMessenger.sendNotice(MsgType.MSG_SCOUTING);
    				}
    				else
    				{
    					obj = SCVBuildOrder.FIND_MINE;
    					myPlayer.myMessenger.sendNotice(MsgType.MSG_POWER_UP);
    					myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    				}
    			}
    			else
    				obj = SCVBuildOrder.FIND_MINE;
    			return;
    			
    		case SCOUT_WEST:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_WEST");
    			westEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(Direction.WEST);
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.WEST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					westEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			mainDestination = myPlayer.myRC.getLocation().add(Direction.WEST, GameConstants.MAP_MAX_WIDTH);
    			Utility.navStep(myPlayer, robotNavigation, mainDestination);
				if (Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_NORTH:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_NORTH");
    			northEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(Direction.NORTH);
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.NORTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					northEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			mainDestination = myPlayer.myRC.getLocation().add(Direction.NORTH, GameConstants.MAP_MAX_HEIGHT);
    			Utility.navStep(myPlayer, robotNavigation, mainDestination);
				if (Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_EAST:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_EAST");
    			eastEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(Direction.EAST);
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.EAST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					eastEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			mainDestination = myPlayer.myRC.getLocation().add(Direction.EAST, GameConstants.MAP_MAX_WIDTH);
    			Utility.navStep(myPlayer, robotNavigation, mainDestination);
				if (Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_SOUTH:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_SOUTH");
    			southEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(Direction.SOUTH);
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.SOUTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					southEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			mainDestination = myPlayer.myRC.getLocation().add(Direction.SOUTH, GameConstants.MAP_MAX_HEIGHT);
    			Utility.navStep(myPlayer, robotNavigation, mainDestination);
				if (Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case RETURN_HOME:
    			myPlayer.myRC.setIndicatorString(1,"RETURN_HOME");
    			mainDestination = hometown;
    			Utility.navStep(myPlayer, robotNavigation, mainDestination);
				if (myPlayer.myRC.getLocation().distanceSquaredTo(hometown) <= Constants.HOME_PROXIMITY)
				{
					if(westEdge == -1)
					{
						myPlayer.myMessenger.sendNotice(MsgType.MSG_POWER_UP);
						obj = SCVBuildOrder.SCOUT_WEST;
					}
					else if(northEdge == -1)
						obj = SCVBuildOrder.SCOUT_NORTH;
					else if(eastEdge == -1)
					{
						if (westEdge == 1 && northEdge == 1)
						{
							eastEdge = 0;
							southEdge = 0;
							obj = SCVBuildOrder.BROADCAST_SPAWN;
						}
						else
							obj = SCVBuildOrder.SCOUT_EAST;
					}
					else if(southEdge == -1)
					{
						if (northEdge == 1)
						{
							southEdge = 0;
							obj = SCVBuildOrder.BROADCAST_SPAWN;
						}
						else
							obj = SCVBuildOrder.SCOUT_SOUTH;
					}
					else
						obj = SCVBuildOrder.BROADCAST_SPAWN;
					if (westEdge != -1 && northEdge != -1 && eastEdge != -1 && southEdge != -1)
					{
						spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
						enemyLocation = Utility.spawnOpposite(hometown, spawn);
						waypointDirs = Utility.spawnAdjacent(spawn);
						waypointDir1 = waypointDirs[0]; // change for other scv
						waypointDir2 = waypointDirs[2]; // change for other scv
						waypoints[0] = hometown;
						waypoints[1] = hometown.add(waypointDir1, Utility.dirSize(waypointDir1));
						nextWaypointIdx = (currWaypointIdx + 1) % (waypoints.length);
						currWaypointIdx = nextWaypointIdx;
						mainDestination = waypoints[currWaypointIdx];
					}
				}
    			return;
    			
    		case BROADCAST_SPAWN:
    			myPlayer.myRC.setIndicatorString(1,"BROADCAST_SPAWN");
    			myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    			if (eeHanTiming)
    				obj = SCVBuildOrder.EXPAND;
    			else
    				Utility.navStep(myPlayer, robotNavigation, mainDestination);
    			return;
    			
    		case EXPAND:
    			myPlayer.myRC.setIndicatorString(1, "EXPAND");
    			Utility.navStep(myPlayer, robotNavigation, mainDestination);
				tiredness++;
				if (tiredness >= Constants.SCV_SEARCH_FREQ)
				{
					tiredness = 0;
					obj = SCVBuildOrder.FIND_MINE;
				}
    			return;
    	}
	}
	
	
	
	public String toString() {
		return "SCVBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
		}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_MOVE_OUT)
			eeHanTiming = true;
	}
}
