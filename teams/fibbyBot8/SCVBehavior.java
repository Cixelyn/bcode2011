package fibbyBot8;

import battlecode.common.*;

import java.util.*;

public class SCVBehavior extends Behavior
{
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.FIND_MINE;
	
	MapLocation destination;
	
	int minesCapped = 0;
	int dizziness;
	int tiredness;
	ArrayList<Integer> badMines = new ArrayList<Integer>(100);
	
	boolean mineFound;
	boolean justTurned;
	
	Mine[] nearbyMines;
	MineInfo mInfo;
	
	int rebroadcastCounter = 0;
	int spawn = -1;
	MapLocation hometown;
	MapLocation enemyLocation;
	
	boolean eeHanTiming = false;
	int westEdge = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	
	final LinkedList<MapLocation> breadcrumbs = new LinkedList<MapLocation>();
	
	public SCVBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		if(eeHanTiming && myPlayer.myBroadcaster != null)
    	{
    		rebroadcastCounter++;
    		if (rebroadcastCounter >= Constants.REBROADCAST_FREQ)
    		{
    			rebroadcastCounter = 0;
    			myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    		}
    	}
		
    	switch (obj)
    	{
	    	case EXPAND:
				myPlayer.myRC.setIndicatorString(1, "EXPAND");
				if (eeHanTiming && Clock.getRoundNum() > Constants.MID_GAME)
        			Utility.navStep(myPlayer, robotNavigation, enemyLocation);
        		else
        			Utility.bounceNav(myPlayer);
				mineFound = false;
				nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
				for (Mine m:nearbyMines)
				{
					if(!mineFound)
	    			{
	    				mInfo = myPlayer.mySensor.senseMineInfo(m);
	    				if(myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
	    				{
	        				mineFound = true;
	        				destination = mInfo.mine.getLocation();
	    				}
	    			}
				}
				if (mineFound)
				{
					tiredness = 0;
					obj = SCVBuildOrder.CAP_MINE;
				}
				else
				{
					tiredness++;
					if (tiredness > Constants.SCV_SEARCH_FREQ)
					{
						tiredness = 0;
						obj = SCVBuildOrder.FIND_MINE;
					}
				}
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
            				destination = mInfo.mine.getLocation();
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
    				obj = SCVBuildOrder.EXPAND;
    			}
    			if(mineFound)
    			{
    				dizziness = 0;
    				if (minesCapped == 0)
    					obj = SCVBuildOrder.WAIT_FOR_ANTENNA;
    				else
    					obj = SCVBuildOrder.CAP_MINE;
    			}
    			return;
				
	    	case WAIT_FOR_ANTENNA:
    			myPlayer.myRC.setIndicatorString(1, "WAIT_FOR_ANTENNA");
    			hometown = myPlayer.myRC.getLocation();
    			for(ComponentController c:myPlayer.myRC.components())
    			{
    				if (c.type() == Constants.COMMTYPE)
    					obj = SCVBuildOrder.CAP_MINE;
    			}
    			return;
    			
    		case CAP_MINE:
    			myPlayer.myRC.setIndicatorString(1, "CAP_MINE");
    			if(!myPlayer.mySensor.withinRange(mInfo.mine.getLocation()) || myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(destination) > myPlayer.myBuilder.type().range)
        			{
        				Utility.navStep(myPlayer, robotNavigation, destination);
        				tiredness++;
            			if(tiredness > Constants.MINE_AFFINITY)
            			{
            				minesCapped++;
            				badMines.add(mInfo.mine.getID());
            				obj = SCVBuildOrder.FIND_MINE;
            				tiredness = 0;
            			}
        			}
        			else
        			{
        				if (Utility.buildChassisInDir(myPlayer, myPlayer.myRC.getLocation().directionTo(destination), Chassis.BUILDING))
        					obj = SCVBuildOrder.ADDON_MINE;
        				else
        					obj = SCVBuildOrder.EXPAND;
        				tiredness = 0;
        				return;
        			}
    			}
    			else
    			{
    				obj = SCVBuildOrder.FIND_MINE;
    				tiredness = 0;
    			}
    			return;
    			
    		case ADDON_MINE:
    			myPlayer.myRC.setIndicatorString(1, "ADDON_MINE");
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
    			myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(destination));
    			myPlayer.myRC.yield();
    			Utility.equipFrontWithOneComponent(myPlayer, ComponentType.RECYCLER);
    			{
    				minesCapped++;
    				if (minesCapped == 2)
    				{
    					obj = SCVBuildOrder.SCOUT_WEST;
    					return;
    				}
    			}
    			obj = SCVBuildOrder.FIND_MINE;
    			return;
    			
    		case SCOUT_WEST:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_WEST");
    			if (tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE)
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			westEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(Direction.WEST);
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.WEST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					westEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			destination = myPlayer.myRC.getLocation().add(Direction.WEST, GameConstants.MAP_MAX_WIDTH);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, robotNavigation, destination);
				if (Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_NORTH:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_NORTH");
    			if (tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE)
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			northEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(Direction.NORTH);
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.NORTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					northEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			destination = myPlayer.myRC.getLocation().add(Direction.NORTH, GameConstants.MAP_MAX_HEIGHT);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, robotNavigation, destination);
				if (Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_EAST:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_EAST");
    			if (tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE)
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			eastEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(Direction.EAST);
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.EAST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					eastEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			destination = myPlayer.myRC.getLocation().add(Direction.EAST, GameConstants.MAP_MAX_WIDTH);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, robotNavigation, destination);
				if (Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_SOUTH:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_SOUTH");
    			if (tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE)
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			southEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(Direction.SOUTH);
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.SOUTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					southEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			destination = myPlayer.myRC.getLocation().add(Direction.SOUTH, GameConstants.MAP_MAX_HEIGHT);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, robotNavigation, destination);
				if (Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case RETURN_HOME:
    			myPlayer.myRC.setIndicatorString(1,"RETURN_HOME");
    			tiredness = 0;
    			if(!breadcrumbs.isEmpty())
    			{
    				destination = breadcrumbs.pollLast();
    				Direction direction = myPlayer.myRC.getLocation().directionTo(destination);
    				if(direction != Direction.OMNI && direction != Direction.NONE)
    				{
    					while(myPlayer.myMotor.isActive())
    						myPlayer.myRC.yield();
    					myPlayer.myMotor.setDirection(direction);
    					while(myPlayer.myMotor.isActive() || !myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
    						myPlayer.myRC.yield();
    					myPlayer.myMotor.moveForward();
    				}
    			}
				if (myPlayer.myRC.getLocation().distanceSquaredTo(hometown) <= Constants.HOME_PROXIMITY)
				{
					breadcrumbs.clear();
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
					}
				}
    			return;
    			
    		case BROADCAST_SPAWN:
    			myPlayer.myRC.setIndicatorString(1,"BROADCAST_SPAWN");
    			myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    			if (eeHanTiming)
    				obj = SCVBuildOrder.EXPAND;
    			else
    				Utility.navStep(myPlayer, robotNavigation, destination);
    			return;
    	}
	}
	
	
	
	public String toString()
	{
		return "SCVBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		if (t == MsgType.MSG_MOVE_OUT)
		{
			myPlayer.myRC.setIndicatorString(2, "We spawned " + Utility.spawnString(spawn));
			eeHanTiming = true;
		}
	}
}
