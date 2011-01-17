package fibbyBot12b.behaviors;

import java.util.ArrayDeque;
import battlecode.common.*;
import fibbyBot12b.*;

public class SCVBehavior extends Behavior
{
	
	private enum SCVBuildOrder 
	{
		GET_INITIAL_IDS,
		FIND_MINE,
		GET_OFF_MINE,
		WAIT_FOR_ANTENNA,
		BUILD_REFINERY,
		BUILD_ARMORY,
		VACATE_HOME,
		VACATE_FACTORY,
		WAIT_FOR_FACTORY,
		BUILD_FACTORY,
		COMPUTE_TOWER,
		BUILD_TOWER,
		SLEEP,
		WEIRD_SPAWN
	}
	
	final OldNavigation nav = new OldNavigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.GET_INITIAL_IDS;
	
	MapLocation hometown = myPlayer.myRC.getLocation();
	MapLocation unitDock;
	MapLocation[] mineLocs = {null, null, null, null};
	MapLocation armoryLoc;
	MapLocation factoryLoc;
	MapLocation towerLoc;
	MapLocation loc;
	Direction dir;
	
	Robot[] nearbyRobots;
	Mine[] nearbyMines;
	Robot r;
	Mine currMine;
	RobotInfo rInfo;
	
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
	
	int[] mainRefineries = {-1,-1};
	
	public SCVBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
		
			case GET_INITIAL_IDS:
				
				Utility.setIndicator(myPlayer, 1, "GET_INITIAL_IDS");
				Utility.setIndicator(myPlayer, 2, "[" + Integer.toString(mainRefineries[0]) + ", " + Integer.toString(mainRefineries[1]) + "]");
				nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
				for ( int i = nearbyRobots.length ; --i >= 0 ; )
				{
					r = nearbyRobots[i];
					rInfo = myPlayer.mySensor.senseRobotInfo(r);
					if ( mainRefineries[0] == -1 )
					{
						mainRefineries[0] = r.getID();
						mineLocs[0] = rInfo.location;
					}
					else if ( r.getID() != mainRefineries[0] && mainRefineries[1] == -1 )
					{
						mainRefineries[1] = r.getID();
						mineLocs[1] = rInfo.location;
					}
				}
				if ( mainRefineries[0] != -1 && mainRefineries[1] != -1 )
					obj = SCVBuildOrder.FIND_MINE;
				else
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
				}
				return;
			
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
        				{
        					obj = SCVBuildOrder.BUILD_ARMORY;
        					mineLocs[3] = loc;
        				}
        				else
        				{
        					obj = SCVBuildOrder.FIND_MINE;
        					mineLocs[2] = loc;
        				}
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
						armoryLoc = myPlayer.myRC.getLocation().add(d);
						while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.ARMORY.cost )
							myPlayer.sleep();
						Utility.buildChassis(myPlayer, d, Chassis.BUILDING);
						Utility.buildComponent(myPlayer, d, ComponentType.ARMORY, RobotLevel.ON_GROUND);
						myPlayer.sleep();
						myPlayer.myBroadcaster.broadcastTurnOn(mainRefineries);
						myPlayer.sleep();
						myPlayer.myMessenger.sendLoc(MsgType.MSG_SEND_DOCK, unitDock);
						myPlayer.sleep();   // comment me for fac
						myPlayer.myRC.suicide(); // comment me for fac
						//obj = SCVBuildOrder.VACATE_HOME; // uncomment me for fac
						return;
					}
					dizziness++;
					if ( dizziness >= 8 )
					{
						myPlayer.myRC.suicide(); // SCV is trapped... but then how did you get there?
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
				factoryLoc = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.FACTORY.cost )
					myPlayer.sleep();
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.FACTORY, RobotLevel.ON_GROUND);
    			myPlayer.sleep();
    			myPlayer.myMessenger.sendLoc(MsgType.MSG_SEND_DOCK, unitDock);
    			//myPlayer.sleep();
    			//myPlayer.myRC.suicide(); // uncomment me and above line to scout
    			obj = SCVBuildOrder.COMPUTE_TOWER;
    			return;
				
			case COMPUTE_TOWER:
				
				Utility.setIndicator(myPlayer, 1, "COMPUTE_TOWER");
				for ( int i = Math.min(armoryLoc.x, factoryLoc.x) - 1 ; i <= Math.max(armoryLoc.x, factoryLoc.x) + 1 ; i++ )
					for ( int j = Math.min(armoryLoc.y, factoryLoc.y) - 1 ; j <= Math.max(armoryLoc.y, factoryLoc.y) + 1 ; j++)
					{
						towerLoc = new MapLocation(i,j);
						if ( myPlayer.myRC.senseTerrainTile(towerLoc) != TerrainTile.LAND )
							continue;
						if ( towerLoc.distanceSquaredTo(armoryLoc) > ComponentType.ARMORY.range )
							continue;
						if ( towerLoc.distanceSquaredTo(factoryLoc) > ComponentType.FACTORY.range )
							continue;
						if ( towerLoc.equals(unitDock) )
							continue;
						if ( towerLoc.equals(mineLocs[0]) )
							continue;
						if ( towerLoc.equals(mineLocs[1]) )
							continue;
						if ( mineLocs[2] != null && towerLoc.equals(mineLocs[2]) )
							continue;
						if ( mineLocs[3] != null && towerLoc.equals(mineLocs[3]) )
							continue;
						obj = SCVBuildOrder.BUILD_TOWER;
						tiredness = 0;
						return;
					}
				myPlayer.myRC.suicide(); // no suitable location for tower
				return;
				
			case BUILD_TOWER:
			
				Utility.setIndicator(myPlayer, 1, "BUILD_TOWER");
				Utility.setIndicator(myPlayer, 2, "Giving up in " + Integer.toString(Constants.MINE_AFFINITY - tiredness) + "...");
				if ( tiredness < Constants.MINE_AFFINITY )
	    		{
					if (myPlayer.myRC.getLocation().distanceSquaredTo(towerLoc) > myPlayer.myBuilder.type().range)
	    			{
	    				Utility.navStep(myPlayer, nav, towerLoc);
	    				tiredness++;
	    			}
	    			else
	    			{
	    				Utility.setIndicator(myPlayer, 2, "Building!");
	    				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + Constants.RESERVE )
							myPlayer.sleep();
	    				Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(towerLoc), Chassis.BUILDING);
	    				myPlayer.sleep();
	    				myPlayer.myMessenger.sendLoc(MsgType.MSG_SEND_TOWER, towerLoc);
	    				myPlayer.sleep();
	    				myPlayer.myRC.suicide();
	    			}
	    		}
				else
					myPlayer.myRC.suicide();
				return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				Utility.setIndicator(myPlayer, 1, "zzzzzzz");
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
