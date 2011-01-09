package costax3;

import battlecode.common.*;
import java.util.*;

public class MuleBehavior extends Behavior {
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation factoryLoc;
	MapLocation jimmyHome = myPlayer.myRC.getLocation();
	MapLocation enemyLocation; 
	MapLocation mainDestination;
	MapLocation tempDestination;
	MapLocation[] waypoints = new MapLocation[3];
	
	Direction waypointDir1;
	Direction waypointDir2;
	Direction[] waypointDirs;
	
	MuleBuildOrder obj = MuleBuildOrder.EQUIPPING;
	
	int westEdge = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int dizziness = 0;
	int tiredness = 0;
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
	
	int spawn;
	
	
	public MuleBehavior(RobotPlayer player) {
		super(player);
	}

	

	public void run() throws Exception {
    	switch (obj)
    	{
    		case EQUIPPING:
    			myPlayer.myRC.setIndicatorString(1, "EQUIPPING");
    			for(ComponentController c:myPlayer.myRC.components())
    			{
    				if (c.type() == ComponentType.CONSTRUCTOR)
    					myPlayer.myBuilder = (BuilderController)c;
    				if (c.type() == Constants.COMMTYPE)
    				{
    					myPlayer.myBroadcaster = (BroadcastController)c;
    					myPlayer.myMessenger.enableSender();
    				}
    				if (c.type() == Constants.SENSORTYPE)
    					myPlayer.mySensor = (SensorController)c;
    			}
    			if (myPlayer.myBuilder != null && myPlayer.myBroadcaster != null && myPlayer.mySensor != null)
    				obj = MuleBuildOrder.BUILD_FACTORY;
    			return;
    		
    		case BUILD_FACTORY:
    			myPlayer.myRC.setIndicatorString(1, "BUILD_FACTORY");
    			while(factoryLoc == null)
    			{
    				myPlayer.myMotor.setDirection(Direction.NORTH);
    				myPlayer.myRC.yield();
    				if(Utility.shouldBuild(myPlayer, Direction.NORTH_WEST, jimmyHome))
    					factoryLoc = myPlayer.myRC.getLocation().add(Direction.NORTH_WEST);
    				else if (Utility.shouldBuild(myPlayer, Direction.NORTH_EAST, jimmyHome))
    					factoryLoc = myPlayer.myRC.getLocation().add(Direction.NORTH_EAST);
    				else if (Utility.shouldBuild(myPlayer, Direction.NORTH, jimmyHome))
    					factoryLoc = myPlayer.myRC.getLocation().add(Direction.NORTH);
    				else
    				{
    					myPlayer.myMotor.setDirection(Direction.SOUTH);
        				myPlayer.myRC.yield();
        				if (Utility.shouldBuild(myPlayer, Direction.SOUTH_WEST, jimmyHome))
        					factoryLoc = myPlayer.myRC.getLocation().add(Direction.SOUTH_WEST);
        				else if (Utility.shouldBuild(myPlayer, Direction.SOUTH_EAST, jimmyHome))
        					factoryLoc = myPlayer.myRC.getLocation().add(Direction.SOUTH_EAST);
        				else if (Utility.shouldBuild(myPlayer, Direction.SOUTH, jimmyHome))
        					factoryLoc = myPlayer.myRC.getLocation().add(Direction.SOUTH);
    				}
    			}
    			myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(factoryLoc));
    			myPlayer.myRC.yield();
    			if(Utility.buildChassis(myPlayer, Chassis.BUILDING))
    				obj = MuleBuildOrder.ADDON_FACTORY;
    			else
    				factoryLoc = null;
    			return;
    			
    		case ADDON_FACTORY:
    			myPlayer.myRC.setIndicatorString(1, "ADDON_FACTORY");
    			Utility.equipFrontWithOneComponent(myPlayer, ComponentType.FACTORY);
    			myPlayer.myMessenger.sendLoc(MsgType.MSG_JIMMY_HOME, jimmyHome);
    			obj = MuleBuildOrder.VACATE_JIMMY_HOME;
    			return;
    			
    		case VACATE_JIMMY_HOME:
    			myPlayer.myRC.setIndicatorString(1, "VACATE_JIMMY_HOME");
    			while(!Utility.shouldBuild(myPlayer, myPlayer.myRC.getDirection(), jimmyHome))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
    			myPlayer.myMotor.moveForward();
    			myPlayer.myMessenger.sendLoc(MsgType.MSG_JIMMY_HOME, jimmyHome);
    			obj = MuleBuildOrder.WAITING;
    			return;
    			
    		case WAITING:
    			myPlayer.myRC.setIndicatorString(1, "WAITING");
    			Utility.spin(myPlayer);
    			if (eeHanTiming)
    				obj = MuleBuildOrder.EXPAND;
    			return;
    			
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
    				
    				if ( mainDestination != null && (myPlayer.myRC.getLocation().distanceSquaredTo(mainDestination) < Constants.HOME_PROXIMITY || currWaypointIdx != 0) )
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
    				
    				obj = MuleBuildOrder.EXPAND;
    			}
    			if(mineFound)
    			{
    				dizziness = 0;
    				obj = MuleBuildOrder.CAP_MINE;
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
        				if (Utility.buildChassis(myPlayer, Chassis.BUILDING))
        					obj = MuleBuildOrder.ADDON_MINE;
        				else
        					obj = MuleBuildOrder.FIND_MINE;
        				tiredness = 0;
        			}
    			}
    			else
    			{
    				obj = MuleBuildOrder.FIND_MINE;
    				tiredness = 0;
    			}
    			tiredness++;
    			if(tiredness > Constants.MINE_AFFINITY)
    			{
    				badMines.add(mInfo.mine.getID());
    				obj = MuleBuildOrder.FIND_MINE;
    				tiredness = 0;
    			}
    			return;
    			
    		case ADDON_MINE:
    			myPlayer.myRC.setIndicatorString(1, "ADDON_MINE");
    			Utility.equipFrontWithOneComponent(myPlayer, ComponentType.RECYCLER);
    			obj = MuleBuildOrder.FIND_MINE;
    			myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, jimmyHome, enemyLocation);
    			myPlayer.myMessenger.sendNotice(MsgType.MSG_POWER_UP);
    			return;
    			
    		case EXPAND:
    			myPlayer.myRC.setIndicatorString(1, "EXPAND");
    			Utility.navStep(myPlayer, robotNavigation, mainDestination);
				tiredness++;
				if (tiredness >= Constants.SCV_SEARCH_FREQ)
				{
					tiredness = 0;
					obj = MuleBuildOrder.FIND_MINE;
				}
    			return;
    	}
	}
	
	
	
	public String toString() {
		return "MuleBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
		}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_MOVE_OUT)
		{
			spawn = msg.ints[Messenger.firstData];
			waypointDirs = Utility.spawnAdjacent(spawn);
			waypointDir1 = waypointDirs[1]; // change for other scv
			waypointDir2 = waypointDirs[3]; // change for other scv
			waypoints[0] = jimmyHome;
			waypoints[1] = jimmyHome.add(waypointDir1, Utility.dirSize(waypointDir1));
			nextWaypointIdx = (currWaypointIdx + 1) % (waypoints.length);
			currWaypointIdx = nextWaypointIdx;
			mainDestination = waypoints[currWaypointIdx];
			eeHanTiming = true;
		}
	}
}
