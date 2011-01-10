package masteryone.behaviors;

import battlecode.common.*;
import masteryone.*;
import java.util.*;

public class SCVBehavior extends Behavior
{
	final OldNavigation nav = new OldNavigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.FIND_MINE;
	
	MapLocation loc;
	Direction dir;
	Mine currMine;
	
	int dizziness = 0;
	int tiredness = 0;
	int minesCapped = 0;
	ArrayList<Integer> badMines = new ArrayList<Integer>(GameConstants.MINES_MAX);
	
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
    				if( !mineFound && m.getTeam() == Team.NEUTRAL )
        			{
        				if( myPlayer.mySensor.senseObjectAtLocation(m.getLocation(), RobotLevel.ON_GROUND) == null && !badMines.contains(m.getID()) )
        				{
            				mineFound = true;
            				currMine = m;
            				loc = m.getLocation();
        				}
        			}
    			}
    			if( !mineFound && dizziness < 4 )
    			{
    				dizziness++;
    				while (myPlayer.myMotor.isActive())
    					myPlayer.sleep();
    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    			}
    			if( !mineFound && dizziness >= 4 )
    			{
    				dizziness = 0;
    				obj = SCVBuildOrder.WEIRD_SPAWN;
    			}
    			if( mineFound )
    			{
    				dizziness = 0;
    				obj = SCVBuildOrder.CAP_MINE;
    			}
    			return;
    			
			case CAP_MINE:
    			
				Utility.setIndicator(myPlayer, 1, "CAP_MINE");
    			if( !myPlayer.mySensor.withinRange(loc) || myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) == null)
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(loc) > myPlayer.myBuilder.type().range)
        			{
        				Utility.navStep(myPlayer, nav, loc);
        				tiredness++;
            			if(tiredness > Constants.MINE_AFFINITY)
            			{
            				minesCapped++;
            				badMines.add(currMine.getID());
            				obj = SCVBuildOrder.FIND_MINE;
            				tiredness = 0;
            			}
        			}
        			else
        			{
        				if ( Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(loc), Chassis.BUILDING) )
        					obj = SCVBuildOrder.ADDON_MINE;
        				else
        					obj = SCVBuildOrder.FIND_MINE;
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
				
    			Utility.setIndicator(myPlayer, 1, "ADDON_MINE");
    			if( Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(loc), ComponentType.RECYCLER, RobotLevel.ON_GROUND) )
    			{
    				minesCapped++;
    				if (minesCapped == 2)
    				{
    					obj = SCVBuildOrder.BUILD_ARMORY;
    					return;
    				}
    			}
    			obj = SCVBuildOrder.FIND_MINE;
    			return;
    		
			case BUILD_ARMORY:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_ARMORY");
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
		
	}
}
