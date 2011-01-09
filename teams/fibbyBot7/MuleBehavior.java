package fibbyBot7;

import battlecode.common.*;
import java.util.*;

public class MuleBehavior extends Behavior
{

	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MuleBuildOrder obj = MuleBuildOrder.EQUIPPING;
	
	MapLocation destination;
	
	int dizziness;
	int tiredness;
	ArrayList<Integer> badMines = new ArrayList<Integer>(100);
	
	boolean hasConstructor;
	boolean hasSensor;
	boolean mineFound;
	boolean justTurned;
	
	Mine[] nearbyMines;
	MineInfo mInfo;
	
	public MuleBehavior(RobotPlayer player)
	{
		super(player);
	}

	
	public void run() throws Exception
	{
    	switch (obj)
    	{
    		case EQUIPPING:
    			myPlayer.myRC.setIndicatorString(1, "EQUIPPING");
    			hasConstructor = false;
    			hasSensor = false;
    			for(ComponentController c:myPlayer.myRC.components())
    			{
    				if (c.type() == ComponentType.CONSTRUCTOR)
    				{
    					myPlayer.myBuilder = (BuilderController)c;
    					hasConstructor = true;
    				}
    				if (c.type() == Constants.SENSORTYPE)
    				{
    					myPlayer.mySensor = (SensorController)c;
    					hasSensor = true;
    				}
    			}
    			if (hasConstructor && hasSensor)
    				obj = MuleBuildOrder.EXPAND;
    			return;
    			
    		case EXPAND:
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
    				obj = MuleBuildOrder.CAP_MINE;
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
            				obj = MuleBuildOrder.EXPAND;
            				tiredness = 0;
            			}
        			}
        			else
        			{
        				while(myPlayer.myMotor.isActive())
							myPlayer.myRC.yield();
        				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(destination));
        				myPlayer.myRC.yield(); // must yield before building since turning occurs at end of turn!
        				if (Utility.buildChassis(myPlayer, Chassis.BUILDING))
        					obj = MuleBuildOrder.ADDON_MINE;
        				else
        					obj = MuleBuildOrder.EXPAND;
        				tiredness = 0;
        				return;
        			}
    			}
    			else
    			{
    				obj = MuleBuildOrder.EXPAND;
    				tiredness = 0;
    			}
    			return;
    			
    		case ADDON_MINE:
    			Utility.equipFrontWithOneComponent(myPlayer, ComponentType.RECYCLER);
    			obj = MuleBuildOrder.EXPAND;
    			return;
    	}
	}
	
	
	
	public String toString()
	{
		return "MuleBehavior";
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
