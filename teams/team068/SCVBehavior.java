package team068;

import battlecode.common.*;

import java.util.*;

public class SCVBehavior extends Behavior
{
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.FIND_MINE;
	
	MapLocation destination;
	
	int dizziness;
	int tiredness;
	ArrayList<Integer> badMines = new ArrayList<Integer>(100);
	
	boolean mineFound;
	boolean justTurned;
	
	Mine[] nearbyMines;
	MineInfo mInfo;
	
	public SCVBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
    	switch (obj)
    	{
	    	case EXPAND:
				myPlayer.myRC.setIndicatorString(1, "EXPAND");
				Utility.bounceNav(myPlayer);
				mineFound = false;
				nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
				for (Mine m:nearbyMines)
				{
					if(!mineFound)
	    			{
	    				mInfo = myPlayer.mySensor.senseMineInfo(m);
	    				if(myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
	    				{
	        				mineFound = true;
	        				destination = mInfo.mine.getLocation();
	    				}
	    			}
				}
				if (mineFound)
				{
					tiredness = 0;
					obj = SCVBuildOrder.CAP_MINE;
				}
				else
				{
					tiredness++;
					if (tiredness > Constants.SCV_SEARCH_FREQ)
					{
						tiredness = 0;
						obj = SCVBuildOrder.FIND_MINE;
					}
				}
				return;
				
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
    				while (myPlayer.myMotor.isActive())
    					myPlayer.myRC.yield();
    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    				dizziness++;
    			}
    			if(!mineFound && dizziness >= 4)
    			{
    				dizziness = 0;
    				obj = SCVBuildOrder.EXPAND;
    			}
    			if(mineFound)
    			{
    				dizziness = 0;
    				obj = SCVBuildOrder.CAP_MINE;
    			}
    			return;
				
    		case CAP_MINE:
    			myPlayer.myRC.setIndicatorString(1, "CAP_MINE");
    			if(!myPlayer.mySensor.withinRange(mInfo.mine.getLocation()) || myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(destination) > myPlayer.myBuilder.type().range)
        			{
        				Utility.navStep(myPlayer, robotNavigation, destination);
        				tiredness++;
            			if(tiredness > Constants.MINE_AFFINITY)
            			{
            				badMines.add(mInfo.mine.getID());
            				obj = SCVBuildOrder.FIND_MINE;
            				tiredness = 0;
            			}
        			}
        			else
        			{
        				if (Utility.buildChassisInDir(myPlayer, myPlayer.myRC.getLocation().directionTo(destination), Chassis.BUILDING))
        					obj = SCVBuildOrder.ADDON_MINE;
        				else
        					obj = SCVBuildOrder.EXPAND;
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
    			myPlayer.myRC.setIndicatorString(1, "ADDON_MINE");
    			while(myPlayer.myMotor.isActive())
    				myPlayer.myRC.yield();
    			myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(destination));
    			myPlayer.myRC.yield();
    			Utility.equipFrontWithOneComponent(myPlayer, ComponentType.RECYCLER);
    			obj = SCVBuildOrder.FIND_MINE;
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
