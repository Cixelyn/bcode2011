package masterytwo.behaviors;

import java.util.ArrayDeque;
import battlecode.common.*;
import masterytwo.*;

public class SCVBehavior extends Behavior
{
	
	private enum SCVBuildOrder 
	{
		FIND_MINE,
		GET_OFF_MINE,
		WAIT_FOR_ANTENNA,
		BUILD_REFINERY,
		BUILD_ARMORY,
		VACATE_HOME,
		VACATE_FACTORY,
		WAIT_FOR_FACTORY,
		BUILD_FACTORY,
		SCOUT_WEST,
		SCOUT_NORTH,
		SCOUT_EAST,
		SCOUT_SOUTH,
		RETURN_HOME,
		BROADCAST_SPAWN,
		WAIT_FOR_HANBANG,
		VACATE_SPAWN,
		SLEEP,
		WEIRD_SPAWN
	}
	
	final OldNavigation nav = new OldNavigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.FIND_MINE;
	
	MapLocation hometown = myPlayer.myRC.getLocation();
	MapLocation unitDock;
	MapLocation loc;
	Direction dir;
	
	Mine[] nearbyMines;
	Mine currMine;
	
	int dizziness = 0;
	int tiredness = 0;
	int minesCapped = 0;
	
	boolean hasAntenna = false;
	boolean mineFound;
	
	boolean steppedOff = false;
	
	ArrayDeque<MapLocation> breadcrumbs = new ArrayDeque<MapLocation>();
	int westEdge = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int realSpawn = -1;
	MapLocation realEnemyLocation;
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
				Utility.setIndicator(myPlayer, 2, "");
				mineFound = false;
				nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class); 
				for ( Mine m : nearbyMines )
    			{
    				if ( !mineFound && m.getTeam() == Team.NEUTRAL && myPlayer.mySensor.senseObjectAtLocation(m.getLocation(), RobotLevel.ON_GROUND) == null )
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
    				if ( !steppedOff )
    					obj = SCVBuildOrder.GET_OFF_MINE;
    				else
    					obj = SCVBuildOrder.WEIRD_SPAWN;
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
				Utility.setIndicator(myPlayer, 2, "Giving up in " + Integer.toString(Constants.MINE_AFFINITY - tiredness) + "...");
    			if ( tiredness < Constants.MINE_AFFINITY && (!myPlayer.mySensor.withinRange(loc) || myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) == null) )
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(loc) > myPlayer.myBuilder.type().range)
        			{
        				Utility.navStep(myPlayer, nav, loc);
        				tiredness++;
        			}
        			else
        			{
        				Utility.setIndicator(myPlayer, 2, "Building!");
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
    				obj = SCVBuildOrder.WEIRD_SPAWN;
    				tiredness = 0;
    			}
    			return;
    		
			case BUILD_ARMORY:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_ARMORY");
				Utility.setIndicator(myPlayer, 2, "");
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
						obj = SCVBuildOrder.SCOUT_WEST;
						//myPlayer.sleep();
						//myPlayer.myRC.suicide();
						return;
					}
					dizziness++;
					if ( dizziness >= 8 )
					{
						myPlayer.myRC.turnOff(); // SCV is trapped... but then how did you get there?
						return;
					}
				}
				return;
				
			case SCOUT_WEST:
				
    			Utility.setIndicator(myPlayer, 1, "SCOUT_WEST");
    			if ( tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE )
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
    			loc = myPlayer.myRC.getLocation().add(Direction.WEST, GameConstants.MAP_MAX_WIDTH);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, nav, loc);
				if ( Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE )
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_NORTH:
    			
    			Utility.setIndicator(myPlayer, 1, "SCOUT_NORTH");
    			if ( tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE )
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
				if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.NORTH,3)) == TerrainTile.OFF_MAP ) // sqrt(SENSORTYPE.range) = 3
				{
					northEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			loc = myPlayer.myRC.getLocation().add(Direction.NORTH, GameConstants.MAP_MAX_HEIGHT);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, nav, loc);
				if ( Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE )
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_EAST:
    			
    			Utility.setIndicator(myPlayer, 1, "SCOUT_EAST");
    			if ( tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE )
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			eastEdge = 0;
    			while ( myPlayer.myMotor.isActive() )
    				myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.EAST);
				myPlayer.sleep();
				if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.EAST,3)) == TerrainTile.OFF_MAP ) // sqrt(SENSORTYPE.range) = 3
				{
					eastEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			loc = myPlayer.myRC.getLocation().add(Direction.EAST, GameConstants.MAP_MAX_WIDTH);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, nav, loc);
				if ( Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > 2*Constants.SCOUTING_DISTANCE )
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case SCOUT_SOUTH:
    			myPlayer.myRC.setIndicatorString(1, "SCOUT_SOUTH");
    			if ( tiredness > Constants.SCOUTING_DISTANCE * Constants.SCOUTING_DISTANCE )
    			{
    				tiredness++;
    				obj = SCVBuildOrder.RETURN_HOME;
    				return;
    			}	
    			southEdge = 0;
    			while ( myPlayer.myMotor.isActive() )
    				myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.SOUTH);
				myPlayer.sleep();
				if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.SOUTH,3)) == TerrainTile.OFF_MAP ) // sqrt(SENSORTYPE.range) = 3
				{
					southEdge = 1;
					obj = SCVBuildOrder.RETURN_HOME;
				}
    			loc = myPlayer.myRC.getLocation().add(Direction.SOUTH, GameConstants.MAP_MAX_HEIGHT);
    			breadcrumbs.add(myPlayer.myRC.getLocation());
    			Utility.navStep(myPlayer, nav, loc);
				if ( Math.abs(myPlayer.myRC.getLocation().y - hometown.y) > Constants.SCOUTING_DISTANCE || Math.abs(myPlayer.myRC.getLocation().x - hometown.x) > 2*Constants.SCOUTING_DISTANCE )
					obj = SCVBuildOrder.RETURN_HOME;
    			return;
    			
    		case RETURN_HOME:
    			
    			Utility.setIndicator(myPlayer, 1, "RETURN_HOME");
    			tiredness = 0;
    			if ( !breadcrumbs.isEmpty() )
    			{
    				loc = breadcrumbs.pollLast();
    				Direction direction = myPlayer.myRC.getLocation().directionTo(loc);
    				if ( direction != Direction.OMNI && direction != Direction.NONE )
    				{
    					while(myPlayer.myMotor.isActive())
    						myPlayer.sleep();
    					myPlayer.myMotor.setDirection(direction);
    					while(myPlayer.myMotor.isActive() || !myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
    						myPlayer.sleep();
    					myPlayer.myMotor.moveForward();
    				}
    			}
				if ( myPlayer.myRC.getLocation().distanceSquaredTo(hometown) <= Constants.HOME_PROXIMITY )
				{
					breadcrumbs.clear();
					if ( westEdge == -1 )
					{
						obj = SCVBuildOrder.SCOUT_WEST;
					}
					else if ( northEdge == -1 )
						obj = SCVBuildOrder.SCOUT_NORTH;
					else if ( eastEdge == -1 )
					{
						if ( westEdge == 1 && northEdge == 1 )
						{
							eastEdge = 0;
							southEdge = 0;
							obj = SCVBuildOrder.BROADCAST_SPAWN;
						}
						else
							obj = SCVBuildOrder.SCOUT_EAST;
					}
					else if ( southEdge == -1 )
					{
						if ( northEdge == 1 )
						{
							southEdge = 0;
							obj = SCVBuildOrder.BROADCAST_SPAWN;
						}
						else
							obj = SCVBuildOrder.SCOUT_SOUTH;
					}
					else
						obj = SCVBuildOrder.BROADCAST_SPAWN;
					if ( westEdge != -1 && northEdge != -1 && eastEdge != -1 && southEdge != -1 )
					{
						realSpawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
						realEnemyLocation = Utility.spawnOpposite(hometown, realSpawn);
					}
				}
    			return;
    			
    		case BROADCAST_SPAWN:
    			
    			Utility.setIndicator(myPlayer, 1, "BROADCAST_SPAWN");
    			if ( realSpawn != -1 )
    				myPlayer.myMessenger.sendIntLoc(MsgType.MSG_REAL_ENEMY_LOC, realSpawn, realEnemyLocation);
    			if ( spawnReceived )
    				obj = SCVBuildOrder.VACATE_SPAWN;
    			else
    				Utility.navStep(myPlayer, nav, hometown);
    			return;
				
			case VACATE_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "VACATE_SPAWN");
				if ( myPlayer.myRC.getLocation().distanceSquaredTo(unitDock) < Constants.HOME_PROXIMITY )
					Utility.navStep(myPlayer, nav, realEnemyLocation);
				else
				{
					myPlayer.myRC.suicide(); // he gets in the way >:[
					//obj = SCVBuildOrder.SLEEP;
				}
				return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				myPlayer.myRC.turnOff();
				return;
				
			case WEIRD_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "WEIRD_SPAWN");
				Utility.setIndicator(myPlayer, 2, "This is crazy!! Going back to my supply depot home.");
				if (myPlayer.myRC.getLocation().distanceSquaredTo(hometown) > 0)
    				Utility.navStep(myPlayer, nav, hometown);
				else
					obj = SCVBuildOrder.BUILD_ARMORY;
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
		if ( t == MsgType.MSG_REAL_ENEMY_LOC )
			spawnReceived = true;
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
}
