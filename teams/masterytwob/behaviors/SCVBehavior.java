package team068b.behaviors;

import battlecode.common.*;
import team068b.*;

public class SCVBehavior extends Behavior
{
	
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
						myPlayer.sleep();
						myPlayer.myRC.suicide();
						return;
					}
					dizziness++;
					if ( dizziness >= 8 )
					{
						myPlayer.myRC.suicide();
						return;
					}
				}
				return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				myPlayer.sleep();
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

	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
}
