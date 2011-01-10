package team068;

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
	boolean hasComm;
	boolean mineFound;
	boolean justTurned;
	
	Mine[] nearbyMines;
	MineInfo mInfo;
	
	boolean eeHanTiming = false;
	int rebroadcastCounter = 0;
	int spawn = -1;
	MapLocation hometown;
	MapLocation enemyLocation;
	
	int travelTime;
	
	public MuleBehavior(RobotPlayer player)
	{
		super(player);
	}

	
	public void run() throws Exception
	{
		
		if(spawn != -1 && myPlayer.myBroadcaster != null)
    	{
    		rebroadcastCounter++;
    		if (rebroadcastCounter >= Constants.REBROADCAST_FREQ)
    		{
    			rebroadcastCounter = 0;
    			myPlayer.myMessenger.sendIntDoubleLoc(MsgType.MSG_MOVE_OUT, spawn, hometown, enemyLocation);
    		}
    	}
		
    	switch (obj)
    	{
    		case EQUIPPING:
    			myPlayer.myRC.setIndicatorString(1, "EQUIPPING");
    			hasConstructor = false;
    			hasSensor = false;
    			for(ComponentController c:myPlayer.myRC.components())
    			{
    				if (c.type() == ComponentType.CONSTRUCTOR)
    					hasConstructor = true;
    				if (c.type() == Constants.SENSORTYPE)
    					hasSensor = true;
    				if (c.type() == Constants.COMMTYPE)
    					hasComm = true;
    			}
    			if (hasConstructor && hasSensor && hasComm)
    				obj = MuleBuildOrder.FIND_MINE;
    			return;
    			
    		case EXPAND:
    			myPlayer.myRC.setIndicatorString(1, "EXPAND");
    			if (eeHanTiming && Clock.getRoundNum() > Constants.MID_GAME && travelTime < Constants.TRAVEL_TIME)
        		{
        			travelTime++;
        			Utility.navStep(myPlayer, robotNavigation, enemyLocation);
        		}
        		else
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
    				obj = MuleBuildOrder.CAP_MINE;
    			}
    			else
    			{
    				tiredness++;
    				if (tiredness > Constants.SCV_SEARCH_FREQ)
    				{
    					tiredness = 0;
    					obj = MuleBuildOrder.FIND_MINE;
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
    				obj = MuleBuildOrder.EXPAND;
    			}
    			if(mineFound)
    			{
    				dizziness = 0;
    				obj = MuleBuildOrder.CAP_MINE;
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
            				obj = MuleBuildOrder.EXPAND;
            				tiredness = 0;
            			}
        			}
        			else
        			{
        				if (Utility.buildChassisInDir(myPlayer, myPlayer.myRC.getLocation().directionTo(destination), Chassis.BUILDING))
        					obj = MuleBuildOrder.ADDON_MINE;
        				else
        					obj = MuleBuildOrder.EXPAND;
        				tiredness = 0;
        				return;
        			}
    			}
    			else
    			{
    				obj = MuleBuildOrder.FIND_MINE;
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
    			obj = MuleBuildOrder.FIND_MINE;
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
		if (t == MsgType.MSG_MOVE_OUT)
		{
			myPlayer.myRC.setIndicatorString(2, "We spawned " + Utility.spawnString(spawn) + ".");
			eeHanTiming = true;
			spawn = msg.ints[Messenger.firstData];
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
		}
	}
}
