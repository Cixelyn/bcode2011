package fibbyBot11.behaviors;

import battlecode.common.*;
import fibbyBot11.*;
import java.util.*;

public class SCVBehavior extends Behavior
{
	
	final OldNavigation nav = new OldNavigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.FIND_MINE;
	
	MapLocation hometown = myPlayer.myRC.getLocation();
	MapLocation unitDock;
	MapLocation loc;
	Direction dir;
	
	Mine currMine;
	
	int dizziness = 0;
	int tiredness = 0;
	int minesCapped = 0;
	ArrayList<Integer> badMines = new ArrayList<Integer>(GameConstants.MINES_MAX);
	
	boolean hasAntenna = false;
	boolean mineFound;
	
	MapLocation destination;
	MapLocation enemyLocation;
	ArrayDeque<MapLocation> breadcrumbs = new ArrayDeque<MapLocation>();
	int westEdge = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int spawn = -1;
	boolean spawnReceived;
	
	public SCVBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
		
			case FIND_MINE:
				
				Utility.setIndicator(myPlayer, 1, "FIND_MINE");
				mineFound = false;
				for ( Mine m : myPlayer.myScanner.detectedMines )
    			{
    				if ( !mineFound && m.getTeam() == Team.NEUTRAL && myPlayer.mySensor.senseObjectAtLocation(m.getLocation(), RobotLevel.ON_GROUND) == null && !badMines.contains(m.getID()) )
    				{
        				mineFound = true;
        				currMine = m;
        				loc = m.getLocation();
    				}
    			}
    			if ( !mineFound && dizziness < 5 )
    			{
    				dizziness++;
    				while (myPlayer.myMotor.isActive())
    					myPlayer.sleep();
    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    			}
    			if ( !mineFound && dizziness >= 5 )
    			{
    				dizziness = 0;
    				obj = SCVBuildOrder.GET_OFF_MINE;
    			}
    			if ( mineFound )
    			{
    				dizziness = 0;
    				if ( minesCapped == 0 )
    					obj = SCVBuildOrder.WAIT_FOR_ANTENNA;
    				else
    					obj = SCVBuildOrder.BUILD_REFINERY;
    			}
    			return;
    			
			case GET_OFF_MINE:
				
				Utility.setIndicator(myPlayer, 1, "GET_OFF_MINE");
				for ( Direction d : Direction.values() )
				{
					if ( d != Direction.OMNI && d != Direction.NONE && myPlayer.myMotor.canMove(d) )
					{
						while ( myPlayer.myMotor.isActive() )
							myPlayer.sleep();
						myPlayer.myMotor.setDirection(d.opposite());
						myPlayer.sleep();
						myPlayer.myMotor.moveBackward();
						obj = SCVBuildOrder.FIND_MINE;
						return;
					}
				}
				return;
    			
			case WAIT_FOR_ANTENNA:
				
				Utility.setIndicator(myPlayer, 1, "WAIT_FOR_ANTENNA");
				if ( hasAntenna )
					obj = SCVBuildOrder.BUILD_REFINERY;
				return;
    			
			case BUILD_REFINERY:
    			
				Utility.setIndicator(myPlayer, 1, "BUILD_REFINERY");
    			if ( !myPlayer.mySensor.withinRange(loc) || myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) == null || tiredness > Constants.MINE_AFFINITY )
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(loc) > myPlayer.myBuilder.type().range)
        			{
        				Utility.navStep(myPlayer, nav, loc);
        				tiredness++;
        			}
        			else
        			{
        				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
							myPlayer.sleep();
        				Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(loc), Chassis.BUILDING);
        				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(loc), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
        				minesCapped++;
        				if (minesCapped == 2)
        					obj = SCVBuildOrder.BUILD_ARMORY;
        				else
        					obj = SCVBuildOrder.FIND_MINE;
        				tiredness = 0;
        				return;
        			}
    			}
    			else
    			{
    				minesCapped++; // in order to ignore bad initial mines
    				badMines.add(currMine.getID());
    				obj = SCVBuildOrder.FIND_MINE;
    				tiredness = 0;
    			}
    			return;
    		
			case BUILD_ARMORY:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_ARMORY");
				unitDock = myPlayer.myRC.getLocation();
				dizziness = 0;
				for ( Direction d : Direction.values() )
				{
					if ( d != Direction.OMNI && d != Direction.NONE && myPlayer.myMotor.canMove(d) )
					{
						while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.ARMORY.cost )
							myPlayer.sleep();
						Utility.buildChassis(myPlayer, d, Chassis.BUILDING);
						Utility.buildComponent(myPlayer, d, ComponentType.ARMORY, RobotLevel.ON_GROUND);
						myPlayer.sleep();
						myPlayer.myMessenger.sendLoc(MsgType.MSG_SEND_DOCK, unitDock);
						obj = SCVBuildOrder.VACATE_HOME;
						return;
					}
					dizziness++;
					if ( dizziness >= 8 )
					{
						obj = SCVBuildOrder.WEIRD_SPAWN;
						return;
					}
				}
				return;
				
			case VACATE_HOME:
				
				Utility.setIndicator(myPlayer, 1, "VACATE_HOME");
				dizziness = 0;
				for ( Direction d : Direction.values() )
				{
					if ( d != Direction.OMNI && d != Direction.NONE && myPlayer.myMotor.canMove(d) )
					{
						while ( myPlayer.myMotor.isActive() )
							myPlayer.sleep();
						myPlayer.myMotor.setDirection(d);
						myPlayer.sleep();
						myPlayer.myMotor.moveForward();
						obj = SCVBuildOrder.VACATE_FACTORY;
						return;
					}
					dizziness++;
					if ( dizziness >= 8 )
					{
						obj = SCVBuildOrder.WEIRD_SPAWN;
						return;
					}
				}
				return;
				
			case VACATE_FACTORY:
				 
				Utility.setIndicator(myPlayer, 1, "VACATE_FACTORY");
				dizziness = 0;
				 for ( Direction d : Direction.values() )
					{
						if ( d != Direction.OMNI && d != Direction.NONE && !myPlayer.myRC.getLocation().add(d).equals(unitDock) && myPlayer.myMotor.canMove(d) )
						{
							while ( myPlayer.myMotor.isActive() )
								myPlayer.sleep();
							myPlayer.myMotor.setDirection(d.opposite());
							myPlayer.sleep();
							myPlayer.myMotor.moveBackward();
							obj = SCVBuildOrder.WAIT_FOR_FACTORY;
							return;
						}
						dizziness++;
						if ( dizziness >= 8 )
						{
							obj = SCVBuildOrder.WEIRD_SPAWN;
							return;
						}
					}
				 return;
    			
			case WAIT_FOR_FACTORY:
				
				Utility.setIndicator(myPlayer, 1, "WAITING");
				if ( Clock.getRoundNum() > Constants.FACTORY_TIME )
					obj = SCVBuildOrder.BUILD_FACTORY;
				return;
				
			case BUILD_FACTORY:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_FACTORY");
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.FACTORY.cost )
					myPlayer.sleep();
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.FACTORY, RobotLevel.ON_GROUND);
    			myPlayer.sleep();
    			myPlayer.myMessenger.sendLoc(MsgType.MSG_SEND_DOCK, unitDock);
    			obj = SCVBuildOrder.SCOUT_WEST;
    			return;
				
			case SCOUT_WEST:
				
    			Utility.setIndicator(myPlayer, 1, "SCOUT_WEST");
    			if (tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE)
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			westEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.WEST);
				myPlayer.sleep();
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.WEST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					westEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			destination = myPlayer.myRC.getLocation().add(Direction.WEST, GameConstants.MAP_MAX_WIDTH);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, nav, destination);
				if (Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_NORTH:
    			
    			Utility.setIndicator(myPlayer, 1, "SCOUT_NORTH");
    			if (tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE)
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			northEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.NORTH);
				myPlayer.sleep();
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.NORTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					northEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			destination = myPlayer.myRC.getLocation().add(Direction.NORTH, GameConstants.MAP_MAX_HEIGHT);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, nav, destination);
				if (Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_EAST:
    			
    			Utility.setIndicator(myPlayer, 1, "SCOUT_EAST");
    			if (tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE)
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			eastEdge = 0;
    			while(myPlayer.myMotor.isActive())
    				myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.EAST);
				myPlayer.sleep();
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.EAST,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					eastEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			destination = myPlayer.myRC.getLocation().add(Direction.EAST, GameConstants.MAP_MAX_WIDTH);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, nav, destination);
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
    				myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.SOUTH);
				myPlayer.sleep();
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.SOUTH,3)) == TerrainTile.OFF_MAP) // sqrt(SENSORTYPE.range) = 3
				{
					southEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			destination = myPlayer.myRC.getLocation().add(Direction.SOUTH, GameConstants.MAP_MAX_HEIGHT);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, nav, destination);
				if (Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE)
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case RETURN_HOME:
    			
    			Utility.setIndicator(myPlayer, 1, "RETURN_HOME");
    			tiredness = 0;
    			if(!breadcrumbs.isEmpty())
    			{
    				destination = breadcrumbs.pollLast();
    				Direction direction = myPlayer.myRC.getLocation().directionTo(destination);
    				if(direction != Direction.OMNI && direction != Direction.NONE)
    				{
    					while(myPlayer.myMotor.isActive())
    						myPlayer.sleep();
    					myPlayer.myMotor.setDirection(direction);
    					while(myPlayer.myMotor.isActive() || !myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
    						myPlayer.sleep();
    					myPlayer.myMotor.moveForward();
    				}
    			}
				if (myPlayer.myRC.getLocation().distanceSquaredTo(hometown) <= Constants.HOME_PROXIMITY)
				{
					breadcrumbs.clear();
					if(westEdge == -1)
					{
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
    			
    			Utility.setIndicator(myPlayer, 1, "BROADCAST_SPAWN");
    			myPlayer.myMessenger.sendIntLoc(MsgType.MSG_ENEMY_LOC, spawn, enemyLocation);
    			if ( spawnReceived )
    				obj = SCVBuildOrder.VACATE_SPAWN;
    			else
    				Utility.navStep(myPlayer, nav, destination);
    			return;
				
			case VACATE_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "VACATE_SPAWN");
				if ( myPlayer.myRC.getLocation().distanceSquaredTo(enemyLocation) < Constants.HOME_PROXIMITY )
					Utility.navStep(myPlayer, nav, enemyLocation);
				else
					obj = SCVBuildOrder.SLEEP;
				return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				myPlayer.myRC.turnOff();
				return;
				
			case WEIRD_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "WEIRD_SPAWN");
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
		for ( ComponentController c : components )
		{
			if ( c.type() == ComponentType.ANTENNA )
				hasAntenna = true;
		}
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		if (t == MsgType.MSG_ENEMY_LOC)
		{
			Utility.setIndicator(myPlayer, 0, "We spawned " + Utility.spawnString(spawn) + ".");
			spawnReceived = true;
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
}
