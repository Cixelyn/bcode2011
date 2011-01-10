package fibbyBot8c;

import battlecode.common.*;

public class MarineBehavior extends Behavior
{
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	WeaponController gun;
	
	int guns;
	
	boolean hasSensor;
	boolean hasArmor;
	boolean justTurned;
	
	boolean eeHanTiming = false;
	int rebroadcastCounter = 0;
	int spawn = -1;
	MapLocation hometown;
	MapLocation enemyLocation;
	
	int travelTime;
	
	public MarineBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		if(eeHanTiming && myPlayer.myBroadcaster != null)
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
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
	            guns = 0;
	            hasSensor = false;
	            hasArmor = false;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if (c.type()==Constants.GUNTYPE)
					{
						guns++;
						if (!myPlayer.myWeapons.contains((WeaponController)c))
							myPlayer.myWeapons.add((WeaponController)c);
					}
					if (c.type()==Constants.SENSORTYPE)
					{
						hasSensor = true;
						myPlayer.mySensor = (SensorController)c;
					}
					if (c.type()==Constants.ARMORTYPE)
						hasArmor = true;
				}
				if (guns >= Constants.GUNS && hasSensor && hasArmor)
					obj = MarineBuildOrder.MOVE_OUT;
				return;
	        	
			case MOVE_OUT:
	        	myPlayer.myRC.setIndicatorString(1,"MOVE_OUT");
	        	if(Utility.senseEnemies(myPlayer) != null)
	        		return;
	        	else if (Clock.getRoundNum() > Constants.LATE_GAME + 9999 && Utility.senseDebris(myPlayer) != null) // remove 9999 to kill rocks
	        		return;
	        	else
	        	{
	        		if (eeHanTiming && Clock.getRoundNum() > Constants.MID_GAME && travelTime < Constants.TRAVEL_TIME)
	        		{
	        			travelTime++;
	        			Utility.navStep(myPlayer, robotNavigation, enemyLocation);
	        		}
	        		else
	        			Utility.bounceNav(myPlayer);
	        	}
	        	return;
		}
	}
	
	
	
	public String toString()
	{
		return "MarineBehavior";
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
