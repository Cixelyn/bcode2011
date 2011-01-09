package fibbyBot7;

import battlecode.common.*;

import java.util.*;

public class SCVBehavior extends Behavior
{
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.EXPAND;
	
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
				obj = SCVBuildOrder.CAP_MINE;
			return;
    			
    		case CAP_MINE:
    			if(!myPlayer.mySensor.withinRange(mInfo.mine.getLocation()) || myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(destination) > myPlayer.myBuilder.type().range)
        			{
        				Utility.navStep(myPlayer, robotNavigation, destination);
        				tiredness++;
            			if(tiredness > Constants.MINE_AFFINITY)
            			{
            				badMines.add(mInfo.mine.getID());
            				obj = SCVBuildOrder.EXPAND;
            				tiredness = 0;
            			}
        			}
        			else
        			{
        				while(myPlayer.myMotor.isActive())
							myPlayer.myRC.yield();
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
    				obj = SCVBuildOrder.EXPAND;
    				tiredness = 0;
    			}
    			return;
    			
    		case ADDON_MINE:
    			Utility.equipFrontWithOneComponent(myPlayer, ComponentType.RECYCLER);
    			obj = SCVBuildOrder.EXPAND;
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
