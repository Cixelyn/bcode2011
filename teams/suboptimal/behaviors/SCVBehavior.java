package suboptimal.behaviors;

import java.util.ArrayDeque;
import battlecode.common.*;
import suboptimal.*;

public class SCVBehavior extends Behavior
{
	
	private enum SCVBuildOrder 
	{
		GET_INITIAL_IDS,
		WAIT_FOR_ANTENNA,
		NORMAL_SPAWN,
		WEIRD_SPAWN,
		WEIRD_VACATE,
		WEIRD_REFINERY,
		BUILD_ARMORY,
		VACATE_HOME,
		VACATE_FACTORY,
		WAIT_FOR_FACTORY,
		BUILD_FACTORY,
		COMPUTE_TOWER,
		BUILD_TOWER,
		SLEEP,
		GIVE_UP
	}
	
	final OldNavigation nav = new OldNavigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.WAIT_FOR_ANTENNA;
	
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
	Mine m;
	Mine currMine;
	RobotInfo rInfo;
	
	int dizziness = 0;
	int tiredness = 0;
	int minesCapped = 0;
	
	boolean hasAntenna = false;
	boolean mineFound;
	
	boolean steppedOff = false;
	boolean triedOtherSide = false;
	
	ArrayDeque<MapLocation> breadcrumbs = new ArrayDeque<MapLocation>();
	int westEdge = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int realSpawn = -1;
	MapLocation realEnemyLocation;
	boolean spawnReceived;
	
	Robot rFront;
	Robot rLeft;
	Robot rRight;
	int frontRefinery;
	int leftRefinery;
	int rightRefinery;
	
	public SCVBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case WAIT_FOR_ANTENNA:
				
				Utility.setIndicator(myPlayer, 1, "WAIT_FOR_ANTENNA");
				if ( hasAntenna )
					obj = SCVBuildOrder.NORMAL_SPAWN;
				return;
				
			case NORMAL_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "NORMAL_SPAWN");
				Utility.setIndicator(myPlayer, 2, "");
				
				frontRefinery = 0;
				leftRefinery = 0;
				rightRefinery = 0;
				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				rLeft = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft()), RobotLevel.ON_GROUND);
				rRight = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight()), RobotLevel.ON_GROUND);
				if ( rFront != null && rFront.getTeam() == myPlayer.myRC.getTeam() )
					frontRefinery = 1;
				if ( rLeft != null && rLeft.getTeam() == myPlayer.myRC.getTeam() )
					leftRefinery = 1;
				if ( rRight != null && rRight.getTeam() == myPlayer.myRC.getTeam() )
					rightRefinery = 1;
				
				// we are facing our 2 starting refineries, the 2 untaken mines are behind them
				if ( !myPlayer.myRC.getDirection().isDiagonal() && frontRefinery + leftRefinery + rightRefinery >= 2 )
				{
					// mine found 2 spaces away
					if ( myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(), 2), RobotLevel.MINE) != null )
					{
						// path found to the left
						if ( rightRefinery == 1 && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()) )
						{
							while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveForward();
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveForward();
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		    				myPlayer.sleep();
		    				Utility.setIndicator(myPlayer, 2, "Building!");
		    				mineLocs[2] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
	        				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
								myPlayer.sleep();
	        				Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
	        				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
	        				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveBackward();
		    				mineLocs[3] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
		    				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
								myPlayer.sleep();
	        				Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
	        				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
	        				obj = SCVBuildOrder.BUILD_ARMORY;
						}
						// path found to the right
						else if ( leftRefinery == 1 && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()) )
						{
							while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveForward();
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveForward();
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
		    				myPlayer.sleep();
		    				Utility.setIndicator(myPlayer, 2, "Building!");
		    				mineLocs[2] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
	        				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
								myPlayer.sleep();
	        				Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
	        				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
	        				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveBackward();
		    				mineLocs[3] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
		    				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
								myPlayer.sleep();
	        				Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
	        				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
	        				obj = SCVBuildOrder.BUILD_ARMORY;
						}
						else if ( !triedOtherSide )
						{
							triedOtherSide = true;
							if ( rightRefinery == 1 )
							{
								while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
			    				while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.moveForward();
			    				while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
							}
							else if ( leftRefinery == 1 )
							{
								while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
			    				while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.moveForward();
			    				while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
							}
						}
						// both sides failed, bug naving
						else
							obj = SCVBuildOrder.WEIRD_SPAWN;
					}
					// no mine found
					else
						obj = SCVBuildOrder.WEIRD_SPAWN;
				}
				// we are not facing the 2 starting refineries -> turn
				else
				{
					dizziness++;
    				while (myPlayer.myMotor.isActive())
    					myPlayer.sleep();
    				if ( myPlayer.myRC.getDirection().isDiagonal() )
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
    				else
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    				if ( dizziness >= 8 )
    					obj = SCVBuildOrder.WEIRD_SPAWN;
				}
				return;
				
			case WEIRD_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "WEIRD_SPAWN");
				Utility.setIndicator(myPlayer, 2, "");
				
				mineFound = false;
				nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class); 
				for ( int i = nearbyMines.length ; --i >= 0 ; )
				{
					m = nearbyMines[i];
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
    					obj = SCVBuildOrder.WEIRD_VACATE;
    				else
    					obj = SCVBuildOrder.GIVE_UP;
    			}
    			if ( mineFound )
    			{
    				dizziness = 0;
    				obj = SCVBuildOrder.WEIRD_REFINERY;
    			}
    			return;
				
			case WEIRD_VACATE:
				
				Utility.setIndicator(myPlayer, 1, "WEIRD_VACATE");
				for ( Direction d : Direction.values() )
				{
					if ( d != Direction.OMNI && d != Direction.NONE && myPlayer.myMotor.canMove(d) )
					{
						while ( myPlayer.myMotor.isActive() )
							myPlayer.sleep();
						myPlayer.myMotor.setDirection(d.opposite());
						myPlayer.sleep();
						myPlayer.myMotor.moveBackward();
						obj = SCVBuildOrder.WEIRD_SPAWN;
						return;
					}
				}
				return;
    			
			case WEIRD_REFINERY:
    			
				Utility.setIndicator(myPlayer, 1, "WEIRD_REFINERY");
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
        					obj = SCVBuildOrder.WEIRD_SPAWN;
        					mineLocs[2] = loc;
        				}
        				tiredness = 0;
        				return;
        			}
    			}
    			else
    			{
    				obj = SCVBuildOrder.GIVE_UP;
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
						myPlayer.myMessenger.sendLoc(MsgType.MSG_SEND_DOCK, unitDock);
						//myPlayer.sleep();   // comment me for fac
						//myPlayer.myRC.suicide(); // comment me for fac
						obj = SCVBuildOrder.VACATE_HOME; // uncomment me for fac
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
				
			case GIVE_UP:
				
				Utility.setIndicator(myPlayer, 1, "GIVE_UP");
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
