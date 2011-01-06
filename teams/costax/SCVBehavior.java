package costax;

import battlecode.common.*;

public class SCVBehavior extends Behavior {
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation hometown = myPlayer.myRC.getLocation();
	MapLocation destination = myPlayer.myRC.getLocation().add(Direction.NORTH,500);
	
	Direction direction;
	Direction enemyDirection;
	
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
	
	Message msg;
	Message attackMsg;
	
	String spawn;
	
	
	public SCVBehavior(RobotPlayer player) {
		super(player);
	}

	

	public void run() throws Exception {
		
		myPlayer.myRC.setIndicatorString(1,"Mines capped: "+Integer.toString(minesCapped));
    	switch (obj)
    	{
    		case FIND_MINE:
    			myPlayer.myRC.setIndicatorString(2, "FIND_MINE");
    			mineFound = false;
    			nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
    			for (Mine m:nearbyMines)
    			{
    				if(!mineFound && m.getTeam()==Team.NEUTRAL)
        			{
        				mInfo = myPlayer.mySensor.senseMineInfo(m);
        				if(myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
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
    			myPlayer.myRC.setIndicatorString(2, "WAIT_FOR_ANTENNA");
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
    			myPlayer.myRC.setIndicatorString(2, "CAP_MINE");
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
        				obj = SCVBuildOrder.ADDON_MINE;
        			}
    			}
    			else
    				obj = SCVBuildOrder.FIND_MINE;
    			break;
    			
    		case ADDON_MINE:
    			myPlayer.myRC.setIndicatorString(2, "ADDON_MINE");
    			Utility.buildComponent(myPlayer, ComponentType.RECYCLER);
    			minesCapped++;
    			if(minesCapped>=4)
    			{
    				if (minesCapped == 4)
    					obj = SCVBuildOrder.SCOUT_WEST;
    				else
    				{
    					obj = SCVBuildOrder.FIND_MINE;
    					myPlayer.myMessenger.sendMsg(attackMsg);
    				}
    				msg = new Message();
    				msg.ints = Constants.POWER_ON;
    				myPlayer.myMessenger.sendMsg(msg);
    			}
    			else
    				obj = SCVBuildOrder.FIND_MINE;
    			break;
    			
    		case SCOUT_WEST:
    			myPlayer.myRC.setIndicatorString(2, "SCOUT_WEST");
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
        			destination = myPlayer.myRC.getLocation().add(Direction.WEST,500);
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
    			myPlayer.myRC.setIndicatorString(2, "SCOUT_NORTH");
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
        			destination = myPlayer.myRC.getLocation().add(Direction.NORTH,500);
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
    			myPlayer.myRC.setIndicatorString(2, "SCOUT_EAST");
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
        			destination = myPlayer.myRC.getLocation().add(Direction.EAST,500);
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
    			myPlayer.myRC.setIndicatorString(2, "SCOUT_SOUTH");
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
        			destination = myPlayer.myRC.getLocation().add(Direction.SOUTH,500);
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
    			myPlayer.myRC.setIndicatorString(2,"RETURN_HOME");
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
							return; // FAILURE
						else if(northEdge == -1)
							obj = SCVBuildOrder.SCOUT_NORTH;
						else if(eastEdge == -1)
						{
							if (westEdge == 1 && northEdge == 1)
							{
								eastEdge = 0;
								southEdge = 0;
								spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
								enemyDirection = Utility.spawnOpposite(spawn);
								myPlayer.myRC.setIndicatorString(0,"(SCV) | knows spawn");
								attackMsg = new Message();
								attackMsg.ints = Constants.ATTACK;
								String[] spawnMsg = {spawn};
								attackMsg.strings = spawnMsg;
								myPlayer.myMessenger.sendMsg(attackMsg);
								obj = SCVBuildOrder.EXPAND;
							}
							else
								obj = SCVBuildOrder.SCOUT_EAST;
						}
						else if(southEdge == -1)
						{
							if (northEdge == 1)
							{
								southEdge = 0;
								spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
								enemyDirection = Utility.spawnOpposite(spawn);
								myPlayer.myRC.setIndicatorString(0,"(SCV) | knows spawn");
								attackMsg = new Message();
								attackMsg.ints = Constants.ATTACK;
								String[] spawnMsg = {spawn};
								attackMsg.strings = spawnMsg;
								myPlayer.myMessenger.sendMsg(attackMsg);
								obj = SCVBuildOrder.EXPAND;
							}
							else
								obj = SCVBuildOrder.SCOUT_SOUTH;
						}
						else
						{
							spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
							enemyDirection = Utility.spawnOpposite(spawn);
							myPlayer.myRC.setIndicatorString(0,"(SCV) | knows spawn");
							attackMsg = new Message();
							attackMsg.ints = Constants.ATTACK;
							String[] spawnMsg = {spawn};
							attackMsg.strings = spawnMsg;
							myPlayer.myMessenger.sendMsg(attackMsg);
							obj = SCVBuildOrder.EXPAND;
						}
					}
    			}
    			break;
    			
    		case EXPAND:
    			myPlayer.myRC.setIndicatorString(2, "EXPAND");
    			if(!myPlayer.myMotor.isActive())
    			{
        			destination = myPlayer.myRC.getLocation().add(enemyDirection,Constants.MAP_MAX_SIZE);
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
	public void newMessageCallback(Message msg) {
		
	}
}
