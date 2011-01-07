package costax3;

import battlecode.common.*;
import java.util.*;

public class SCVBuildFactory extends Behavior {
	
	public SCVBuildFactory(RobotPlayer player) {
		super(player);
	}





	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation hometown = myPlayer.myRC.getLocation();
	MapLocation enemyLocation; 
	MapLocation destination;
	
	Direction direction;
	
	SCVBuildOrder obj = SCVBuildOrder.FIND_MINE;
	
	int westEdge = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int dizziness = 0;
	int tiredness = 0;
	int minesCapped = 2;
	
	boolean mineFound;
	
	MineInfo mInfo;
	Mine[] nearbyMines;
	RobotInfo rInfo;
	Robot[] nearbyRobots;
	
	ArrayList<Integer> badMines = new ArrayList<Integer>(); // mines SCV will not consider capping for whatever reason
	
	Message attackMsg;
	
	String spawn;

	

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
            				destination = mInfo.mine.getLocation();
        				}
        			}
    			}
    			if(!mineFound && dizziness < 4)
    			{
    				while (!myPlayer.myMotor.isActive())
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    				dizziness++;
    			}
    			if(!mineFound && dizziness == 4)
    			{
    				dizziness = 0;
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
    			break;
    			
    		case WAIT_FOR_ANTENNA:
    			myPlayer.myRC.setIndicatorString(1, "WAIT_FOR_ANTENNA");
    			for(ComponentController c:myPlayer.myRC.components())
    			{
    				if (c.type()==ComponentType.ANTENNA)
    				{
    					myPlayer.myBroadcaster = (BroadcastController)c;
    					myPlayer.myMessenger.enableSender();
    					obj = SCVBuildOrder.CAP_MINE;
    				}
    			}
    			break;
    			
    		case CAP_MINE:
    			myPlayer.myRC.setIndicatorString(1, "CAP_MINE");
    			if(!myPlayer.mySensor.withinRange(mInfo.mine.getLocation()) || myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
    			{
        			direction = robotNavigation.bugTo(destination);
        			if (!myPlayer.myMotor.isActive() && myPlayer.myRC.getLocation().distanceSquaredTo(destination)>myPlayer.myBuilder.type().range && direction != Direction.OMNI && direction != Direction.NONE)
            		{
                		myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
						{
							myPlayer.myRC.yield();
						}
						myPlayer.myMotor.moveForward();
            		}
        			if (!myPlayer.myMotor.isActive() && myPlayer.myRC.getLocation().distanceSquaredTo(destination)<=myPlayer.myBuilder.type().range)
        			{
        				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(destination));
        				myPlayer.myRC.yield();
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
    			break;
    			
    		case ADDON_MINE:
    			myPlayer.myRC.setIndicatorString(1, "ADDON_MINE");
    			Utility.buildComponent(myPlayer, ComponentType.RECYCLER);
    			if(minesCapped>=4)
    			{
    				if (minesCapped == 4)
    					obj = SCVBuildOrder.SCOUT_WEST;
    				else
    				{
    					obj = SCVBuildOrder.FIND_MINE;
    					myPlayer.myMessenger.sendDoubleLoc(MsgType.MSG_MOVE_OUT, hometown, enemyLocation);
    				}
    				myPlayer.myMessenger.sendNotice(MsgType.MSG_POWER_UP);
    			}
    			else
    				obj = SCVBuildOrder.FIND_MINE;
    			break;
    			
    		case SCOUT_WEST:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_WEST");
    			westEdge = 0;
    			if(!myPlayer.myMotor.isActive())
    			{
    				myPlayer.myMotor.setDirection(Direction.WEST);
    				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.WEST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
    				{
    					westEdge = 1;
    					obj = SCVBuildOrder.RETURN_HOME;
    				}
    				myPlayer.myRC.yield();
        			destination = myPlayer.myRC.getLocation().add(Direction.WEST, GameConstants.MAP_MAX_WIDTH);
        			direction = robotNavigation.bugTo(destination);
        			if(direction != Direction.OMNI && direction != Direction.NONE)
        			{
            			myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
						{
							myPlayer.myRC.yield();
						}
						myPlayer.myMotor.moveForward();
        			}
					if (Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE)
					{
						obj = SCVBuildOrder.RETURN_HOME;
					}
    			}
    			break;
    			
    		case SCOUT_NORTH:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_NORTH");
    			northEdge = 0;
    			if(!myPlayer.myMotor.isActive())
    			{
    				myPlayer.myMotor.setDirection(Direction.NORTH);
    				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.NORTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
    				{
    					northEdge = 1;
    					obj = SCVBuildOrder.RETURN_HOME;
    				}
    				myPlayer.myRC.yield();
        			destination = myPlayer.myRC.getLocation().add(Direction.NORTH, GameConstants.MAP_MAX_HEIGHT);
        			direction = robotNavigation.bugTo(destination);
        			if(direction != Direction.OMNI && direction != Direction.NONE)
        			{
            			myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
						{
							myPlayer.myRC.yield();
						}
						myPlayer.myMotor.moveForward();
        			}
					if (Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE)
					{
						obj = SCVBuildOrder.RETURN_HOME;
					}
    			}
    			break;
    			
    		case SCOUT_EAST:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_EAST");
    			eastEdge = 0;
    			if(!myPlayer.myMotor.isActive())
    			{
    				myPlayer.myMotor.setDirection(Direction.EAST);
    				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.EAST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
    				{
    					eastEdge = 1;
    					obj = SCVBuildOrder.RETURN_HOME;
    				}
    				myPlayer.myRC.yield();
        			destination = myPlayer.myRC.getLocation().add(Direction.EAST, GameConstants.MAP_MAX_WIDTH);
        			direction = robotNavigation.bugTo(destination);
        			if(direction != Direction.OMNI && direction != Direction.NONE)
        			{
            			myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
						{
							myPlayer.myRC.yield();
						}
						myPlayer.myMotor.moveForward();
        			}
					if (Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE)
					{
						obj = SCVBuildOrder.RETURN_HOME;
					}
    			}
    			break;
    			
    		case SCOUT_SOUTH:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_SOUTH");
    			southEdge = 0;
    			if(!myPlayer.myMotor.isActive())
    			{
    				myPlayer.myMotor.setDirection(Direction.SOUTH);
    				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.SOUTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
    				{
    					southEdge = 1;
    					obj = SCVBuildOrder.RETURN_HOME;
    				}
    				myPlayer.myRC.yield();
        			destination = myPlayer.myRC.getLocation().add(Direction.SOUTH, GameConstants.MAP_MAX_HEIGHT);
        			direction = robotNavigation.bugTo(destination);
        			if(direction != Direction.OMNI && direction != Direction.NONE)
        			{
            			myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
						{
							myPlayer.myRC.yield();
						}
						myPlayer.myMotor.moveForward();
        			}
					if (Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE)
					{
						obj = SCVBuildOrder.RETURN_HOME;
					}
    			}
    			break;
    			
    		case RETURN_HOME:
    			myPlayer.myRC.setIndicatorString(1,"RETURN_HOME");
    			if(!myPlayer.myMotor.isActive())
    			{
        			destination = hometown;
        			direction = robotNavigation.bugTo(destination);
        			if(direction != Direction.OMNI && direction != Direction.NONE)
        			{
            			myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
						{
							myPlayer.myRC.yield();
						}
						myPlayer.myMotor.moveForward();
        			}
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
								obj = SCVBuildOrder.BUILD_FACTORY;
							}
							else
								obj = SCVBuildOrder.SCOUT_EAST;
						}
						else if(southEdge == -1)
						{
							if (northEdge == 1)
							{
								southEdge = 0;
								obj = SCVBuildOrder.BUILD_FACTORY;
							}
							else
								obj = SCVBuildOrder.SCOUT_SOUTH;
						}
						else
							obj = SCVBuildOrder.BUILD_FACTORY;
						if (westEdge != -1 && northEdge != -1 && eastEdge != -1 && southEdge != -1)
						{
							spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
							enemyLocation = Utility.spawnOpposite(hometown, spawn);
							destination = enemyLocation;
						}
					}
    			}
    			break;
    		case BUILD_FACTORY:
    			myPlayer.myRC.setIndicatorString(1,"BUILD_FACTORY");
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for (int i=0;i<8;i++) {
    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
    				myPlayer.myRC.yield();
    				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
    					break;
    				}
    			}
    			
        		Utility.buildChassis(myPlayer, Chassis.BUILDING);
        		Utility.buildComponent(myPlayer, ComponentType.FACTORY);
        		obj = SCVBuildOrder.BROADCAST_SPAWN;
        		break;
    			
    		case BROADCAST_SPAWN:
    			myPlayer.myRC.setIndicatorString(1,"BROADCAST_SPAWN");
    			myPlayer.myMessenger.sendDoubleLoc(MsgType.MSG_MOVE_OUT, hometown, enemyLocation);
    			nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
    			for (Robot r: nearbyRobots)
    			{
    				if (r.getTeam() == myPlayer.myRC.getTeam())
    				{
    					rInfo = myPlayer.mySensor.senseRobotInfo(r);
    					if (myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location) < Constants.COMMTYPE.range && rInfo.chassis == Chassis.BUILDING)
    					{
    						obj = SCVBuildOrder.EXPAND;
    					}
    				}
    			}
    			if(!myPlayer.myMotor.isActive())
    			{
        			direction = robotNavigation.bugTo(destination);
        			if(direction != Direction.OMNI && direction != Direction.NONE)
        			{
            			myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
						{
							myPlayer.myRC.yield();
						}
						myPlayer.myMotor.moveForward();
        			}
    			}
    			break;
    			
    		case EXPAND:
    			myPlayer.myRC.setIndicatorString(1, "EXPAND");
    			if(!myPlayer.myMotor.isActive())
    			{
        			direction = robotNavigation.bugTo(destination);
        			if(direction != Direction.OMNI && direction != Direction.NONE)
        			{
            			myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
						{
							myPlayer.myRC.yield();
						}
						myPlayer.myMotor.moveForward();
        			}
					tiredness++;
					if (tiredness >= 4)
					{
						tiredness = 0;
						obj = SCVBuildOrder.FIND_MINE;
					}
    			}
    			break;
    	}
        return;
	}
	
	
	
	public String toString() {
		return "SCVBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
		}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		
	}
}