package fibbyBot9.behaviors;

import battlecode.common.*;
import fibbyBot9.*;
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
    				if ( !mineFound && m.getTeam() == Team.NEUTRAL )
        			{
        				if ( myPlayer.mySensor.senseObjectAtLocation(m.getLocation(), RobotLevel.ON_GROUND) == null && !badMines.contains(m.getID()) )
        				{
            				mineFound = true;
            				currMine = m;
            				loc = m.getLocation();
        				}
        			}
    			}
    			if ( !mineFound && dizziness < 4 )
    			{
    				dizziness++;
    				while (myPlayer.myMotor.isActive())
    					myPlayer.sleep();
    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    			}
    			if ( !mineFound && dizziness >= 4 )
    			{
    				dizziness = 0;
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
					if ( myPlayer.myMotor.canMove(d) )
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
						if ( d != Direction.OMNI && d != Direction.NONE && myPlayer.myRC.getLocation().add(d) != hometown && myPlayer.myMotor.canMove(d) )
						{
							while ( myPlayer.myMotor.isActive() )
								myPlayer.sleep();
							myPlayer.myMotor.setDirection(d.opposite());
							myPlayer.sleep();
							myPlayer.myMotor.moveBackward();
							obj = SCVBuildOrder.WAITING;
							// obj = SCVBuildOrder.BUILD_FACTORY; // switch to me if you want immediate factory!
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
    			
			case WAITING:
				
				Utility.setIndicator(myPlayer, 1, "WAITING");
				if ( Clock.getRoundNum() > 500 )
					obj = SCVBuildOrder.BUILD_FACTORY;
				return;
				
			case BUILD_FACTORY:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_FACTORY");
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.FACTORY.cost )
					myPlayer.sleep();
    			Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
    			Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.FACTORY, RobotLevel.ON_GROUND);
    			obj = SCVBuildOrder.SLEEP;
    			return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
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
		
	}
	
	public void onWakeupCallback(int lastActiveRound) {}
}
