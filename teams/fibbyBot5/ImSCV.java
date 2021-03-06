package fibbyBot5;


import battlecode.common.*;
import java.util.ArrayList;

public class ImSCV
{
	
	//private static final int OLDNEWS = 15;
	private static final int RESERVE = 5;
	private static final int SCOUTING_DISTANCE = 7;
	
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> broadcasters, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{
		SensorController sensor = (SensorController)sensors.get(0);
		MovementController motor = (MovementController)motors.get(0);
		BuilderController builder = (BuilderController)builders.get(0);
		BroadcastController broadcaster = null;
		
		Navigation robotNavigation = new Navigation(player,myRC,motor);
		MapLocation hometown = myRC.getLocation();
		MapLocation destination = myRC.getLocation().add(Direction.NORTH,500);
		Direction direction;
		
		SCVBuildOrder obj = SCVBuildOrder.FIND_MINE;
		
		Mine[] nearbyMines;
		MineInfo mInfo = null;
		boolean mineFound;
		int minesCapped = 2;
		int dizziness = 0;
		int tiredness = 0;
		Message msg = null;
		Message attackMsg = null;
		int[] gogogo = {9090};
		int[] attack = {4774};
		
		int westEdge = -1;
		int northEdge = -1;
		int eastEdge = -1;
		int southEdge = -1;
		String spawn;
		Direction enemyDirection = null;
		
        while (true)
        {
            try
            {
            	myRC.setIndicatorString(1,"Mines capped: "+Integer.toString(minesCapped));
            	switch (obj)
            	{
            		case FIND_MINE:
            			myRC.setIndicatorString(2, "FIND_MINE");
            			mineFound = false;
            			nearbyMines = sensor.senseNearbyGameObjects(Mine.class);
            			for (Mine m:nearbyMines)
            			{
            				if(!mineFound && m.getTeam()==Team.NEUTRAL)
	            			{
	            				mInfo = sensor.senseMineInfo(m);
	            				if(sensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
	            				{
		            				mineFound = true;
		            				destination = mInfo.mine.getLocation();
	            				}
	            			}
            			}
            			if(!mineFound && dizziness < 4)
            			{
            				while (!motor.isActive())
            					motor.setDirection(myRC.getDirection().rotateRight().rotateRight());
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
            			myRC.yield();
            			break;
            			
            		case WAIT_FOR_ANTENNA:
            			myRC.setIndicatorString(2, "WAIT_FOR_ANTENNA");
            			for(ComponentController c:myRC.components())
            			{
            				if (c.type()==ComponentType.ANTENNA)
            				{
            					broadcaster = (BroadcastController)c;
            					obj = SCVBuildOrder.CAP_MINE;
            				}
            			}
            			myRC.yield();
            			break;
            			
            		case CAP_MINE:
            			myRC.setIndicatorString(2, "CAP_MINE");
            			if(!sensor.withinRange(mInfo.mine.getLocation()) || sensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
            			{
	            			direction = robotNavigation.bugTo(destination);
	            			if (!motor.isActive() && myRC.getLocation().distanceSquaredTo(destination)>builder.type().range && direction != Direction.OMNI && direction != Direction.NONE)
	                		{
		                		motor.setDirection(direction);
								myRC.yield();
								while(!motor.canMove(myRC.getDirection()))
								{
									myRC.yield();
								}
								motor.moveForward();
	                		}
	            			if (!motor.isActive() && myRC.getLocation().distanceSquaredTo(destination)<=builder.type().range)
	            			{
	            				motor.setDirection(myRC.getLocation().directionTo(destination));
	            				myRC.yield();
	            				while(myRC.getTeamResources() < Chassis.BUILDING.cost + RESERVE || builder.isActive())
	            					myRC.yield();
	            				builder.build(Chassis.BUILDING, destination);
	            				obj = SCVBuildOrder.ADDON_MINE;
	            			}
            			}
            			else
            				obj = SCVBuildOrder.FIND_MINE;
            			myRC.yield();
            			break;
            			
            		case ADDON_MINE:
            			myRC.setIndicatorString(2, "ADDON_MINE");
            			while(builder.isActive() || myRC.getTeamResources() < ComponentType.RECYCLER.cost + RESERVE)
            				myRC.yield();
            			builder.build(ComponentType.RECYCLER, destination, RobotLevel.ON_GROUND);
            			minesCapped++;
            			if(minesCapped>=4)
            			{
            				if (minesCapped == 4)
            					obj = SCVBuildOrder.SCOUT_WEST;
            				else
            				{
            					obj = SCVBuildOrder.FIND_MINE;
            					broadcaster.broadcast(attackMsg);
            				}
            				msg = new Message();
            				msg.ints = gogogo;
            				while(broadcaster.isActive())
            					myRC.yield();
	        				broadcaster.broadcast(msg);
            			}
            			else
            				obj = SCVBuildOrder.FIND_MINE;
            			break;
            			
            		case SCOUT_WEST:
            			myRC.setIndicatorString(2, "SCOUT_WEST");
            			westEdge = 0;
            			if(!motor.isActive())
            			{
            				motor.setDirection(Direction.WEST);
            				if(myRC.senseTerrainTile(myRC.getLocation().add(Direction.WEST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
            				{
            					westEdge = 1;
            					obj = SCVBuildOrder.RETURN_HOME;
            				}
            				myRC.yield();
	            			destination = myRC.getLocation().add(Direction.WEST,500);
	            			direction = robotNavigation.bugTo(destination);
	            			if(direction != Direction.OMNI && direction != Direction.NONE)
	            			{
		            			motor.setDirection(direction);
								myRC.yield();
								while(!motor.canMove(myRC.getDirection()))
								{
									myRC.yield();
								}
								motor.moveForward();
	            			}
							if (Math.abs(myRC.getLocation().x - hometown.x) > SCOUTING_DISTANCE)
							{
								obj = SCVBuildOrder.RETURN_HOME;
							}
            			}
            			myRC.yield();
            			break;
            			
            		case SCOUT_NORTH:
            			myRC.setIndicatorString(2, "SCOUT_NORTH");
            			northEdge = 0;
            			if(!motor.isActive())
            			{
            				motor.setDirection(Direction.NORTH);
            				if(myRC.senseTerrainTile(myRC.getLocation().add(Direction.NORTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
            				{
            					northEdge = 1;
            					obj = SCVBuildOrder.RETURN_HOME;
            				}
            				myRC.yield();
	            			destination = myRC.getLocation().add(Direction.NORTH,500);
	            			direction = robotNavigation.bugTo(destination);
	            			if(direction != Direction.OMNI && direction != Direction.NONE)
	            			{
		            			motor.setDirection(direction);
								myRC.yield();
								while(!motor.canMove(myRC.getDirection()))
								{
									myRC.yield();
								}
								motor.moveForward();
	            			}
							if (Math.abs(myRC.getLocation().y - hometown.y) > SCOUTING_DISTANCE)
							{
								obj = SCVBuildOrder.RETURN_HOME;
							}
            			}
            			myRC.yield();
            			break;
            			
            		case SCOUT_EAST:
            			myRC.setIndicatorString(2, "SCOUT_EAST");
            			eastEdge = 0;
            			if(!motor.isActive())
            			{
            				motor.setDirection(Direction.EAST);
            				if(myRC.senseTerrainTile(myRC.getLocation().add(Direction.EAST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
            				{
            					eastEdge = 1;
            					obj = SCVBuildOrder.RETURN_HOME;
            				}
            				myRC.yield();
	            			destination = myRC.getLocation().add(Direction.EAST,500);
	            			direction = robotNavigation.bugTo(destination);
	            			if(direction != Direction.OMNI && direction != Direction.NONE)
	            			{
		            			motor.setDirection(direction);
								myRC.yield();
								while(!motor.canMove(myRC.getDirection()))
								{
									myRC.yield();
								}
								motor.moveForward();
	            			}
							if (Math.abs(myRC.getLocation().x - hometown.x) > SCOUTING_DISTANCE)
							{
								obj = SCVBuildOrder.RETURN_HOME;
							}
            			}
            			myRC.yield();
            			break;
            			
            		case SCOUT_SOUTH:
            			myRC.setIndicatorString(2, "SCOUT_SOUTH");
            			southEdge = 0;
            			if(!motor.isActive())
            			{
            				motor.setDirection(Direction.SOUTH);
            				if(myRC.senseTerrainTile(myRC.getLocation().add(Direction.SOUTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
            				{
            					southEdge = 1;
            					obj = SCVBuildOrder.RETURN_HOME;
            				}
            				myRC.yield();
	            			destination = myRC.getLocation().add(Direction.SOUTH,500);
	            			direction = robotNavigation.bugTo(destination);
	            			if(direction != Direction.OMNI && direction != Direction.NONE)
	            			{
		            			motor.setDirection(direction);
								myRC.yield();
								while(!motor.canMove(myRC.getDirection()))
								{
									myRC.yield();
								}
								motor.moveForward();
	            			}
							if (Math.abs(myRC.getLocation().y - hometown.y) > SCOUTING_DISTANCE)
							{
								obj = SCVBuildOrder.RETURN_HOME;
							}
            			}
            			myRC.yield();
            			break;
            			
            		case RETURN_HOME:
            			myRC.setIndicatorString(2,"RETURN_HOME");
            			if(!motor.isActive())
            			{
	            			destination = hometown;
	            			direction = robotNavigation.bugTo(destination);
	            			if(direction != Direction.OMNI && direction != Direction.NONE)
	            			{
		            			motor.setDirection(direction);
								myRC.yield();
								while(!motor.canMove(myRC.getDirection()))
								{
									myRC.yield();
								}
								motor.moveForward();
	            			}
							if (myRC.getLocation().distanceSquaredTo(hometown) <= 9)
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
										spawn = Utilities.getSpawn(westEdge, northEdge, eastEdge, southEdge);
										enemyDirection = Utilities.spawnOpposite(spawn);
										myRC.setIndicatorString(0,"(SCV) | knows spawn");
										attackMsg = new Message();
										attackMsg.ints = attack;
										String[] spawnMsg = {spawn};
										attackMsg.strings = spawnMsg;
										broadcaster.broadcast(attackMsg);
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
										spawn = Utilities.getSpawn(westEdge, northEdge, eastEdge, southEdge);
										enemyDirection = Utilities.spawnOpposite(spawn);
										myRC.setIndicatorString(0,"(SCV) | knows spawn");
										attackMsg = new Message();
										attackMsg.ints = attack;
										String[] spawnMsg = {spawn};
										attackMsg.strings = spawnMsg;
										broadcaster.broadcast(attackMsg);
										obj = SCVBuildOrder.EXPAND;
									}
									else
										obj = SCVBuildOrder.SCOUT_SOUTH;
								}
								else
								{
									spawn = Utilities.getSpawn(westEdge, northEdge, eastEdge, southEdge);
									enemyDirection = Utilities.spawnOpposite(spawn);
									myRC.setIndicatorString(0,"(SCV) | knows spawn");
									attackMsg = new Message();
									attackMsg.ints = attack;
									String[] spawnMsg = {spawn};
									attackMsg.strings = spawnMsg;
									broadcaster.broadcast(attackMsg);
									obj = SCVBuildOrder.EXPAND;
								}
							}
            			}
            			myRC.yield();
            			break;
            			
            		case EXPAND:
            			myRC.setIndicatorString(2, "EXPAND");
            			if(!motor.isActive())
            			{
	            			destination = myRC.getLocation().add(enemyDirection,500);
	            			direction = robotNavigation.bugTo(destination);
	            			if(direction != Direction.OMNI && direction != Direction.NONE)
	            			{
		            			motor.setDirection(direction);
								myRC.yield();
								while(!motor.canMove(myRC.getDirection()))
								{
									myRC.yield();
								}
								motor.moveForward();
	            			}
							tiredness++;
							if (tiredness >= 4)
							{
								tiredness = 0;
								obj = SCVBuildOrder.FIND_MINE;
							}
            			}
            			myRC.yield();
            			break;
            	}
                myRC.yield();
            }
            catch (Exception e)
            {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}
}
