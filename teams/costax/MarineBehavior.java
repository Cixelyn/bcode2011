package costax;

import battlecode.common.*;

import java.util.ArrayList;

public class MarineBehavior extends Behavior {
	
	WeaponController gun;
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation hometown;
	MapLocation enemyLocation;
	MapLocation currDestination;
	MapLocation newDestination;
	MapLocation mainDestination;
	
	MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	Direction direction;
	
	int staleness = 0;
	int guns;
	int dizziness = 0;
	
	//double lastHP = myPlayer.myRC.get;
	
	boolean hasSensor;
    boolean hasArmor;
	boolean eeHanTiming = false;
    boolean moveOut = false;
    boolean enemyFound;
    
    Robot[] nearbyRobots;
    RobotInfo rInfo;
    
    ArrayList<?>[] componentList;
    
    Message[] msgs;
    
    String spawn;
	
	public MarineBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception {
		
		
		switch (obj) {
			case EQUIPPING:
	            guns = 0;
	            hasSensor = false;
	            hasArmor = false;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if (c.type()==Constants.GUNTYPE)
					{
						guns = guns+1;
						if (!myPlayer.myWeapons.contains((WeaponController)c))
							myPlayer.myWeapons.add((WeaponController)c);
					}
					if (c.type()==Constants.SENSORTYPE)
					{
						hasSensor = true;
						myPlayer.mySensor = (SensorController)c;
					}
					if (c.type()==Constants.ARMORTYPE)
					{
						hasArmor = true;
					}
				}
				if (guns >= Constants.GUNS && hasSensor && hasArmor)
					obj = MarineBuildOrder.WAITING;
				break;
			case WAITING:
				Utility.senseEnemies(myPlayer);
	        	if (eeHanTiming)
	        		obj = MarineBuildOrder.MOVE_OUT;
	        	break;
			case MOVE_OUT:
	        	myPlayer.myRC.setIndicatorString(1,"EE HAN TIMING!");
	        	newDestination = Utility.senseEnemies(myPlayer);
	        	if (newDestination != null) // enemy found
	        	{
	        		staleness = 0;
	        		currDestination = newDestination;
	        	}
	        	if (!myPlayer.myMotor.isActive())
	            {
	        		direction = robotNavigation.bugTo(currDestination);
	        		staleness++;
	        		if (staleness > Constants.OLDNEWS)
	        			currDestination = mainDestination;
	        		if (direction != Direction.OMNI && direction != Direction.NONE)
	        		{
	            		myPlayer.myMotor.setDirection(direction);
						if (staleness > Constants.OLDNEWS || myPlayer.myRC.getLocation().distanceSquaredTo(currDestination) >= Constants.GUNTYPE.range)
						{
							while(myPlayer.myMotor.isActive())
								myPlayer.myRC.yield();
							if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
								myPlayer.myMotor.moveForward();
	        			}
	        		}
	            }
	        	if (staleness > Constants.OLDNEWS && (Constants.OLDNEWS - staleness) % Constants.MARINE_SEARCH_FREQ == 0)
	        		obj = MarineBuildOrder.SEARCH_FOR_ENEMY;
	        	break;
	        	
			case SEARCH_FOR_ENEMY:
				myPlayer.myRC.setIndicatorString(1, "FIND_MINE");
    			enemyFound = false;
    			newDestination = Utility.senseEnemies(myPlayer);
    			if (newDestination != null)
    				enemyFound = true;
    			else
    			{
    				dizziness = 0;
    				obj = MarineBuildOrder.MOVE_OUT;
    			}
    			if(!enemyFound && dizziness < 4)
    			{
    				while (!myPlayer.myMotor.isActive())
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    				dizziness++;
    			}
    			if(!enemyFound && dizziness == 4)
    			{
    				dizziness = 0;
    				obj = MarineBuildOrder.MOVE_OUT;
    			}
    			break;
		}
	}
	
	
	
	public String toString() {
		return "MarineBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_MOVE_OUT)
		{
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
			currDestination = enemyLocation;
			mainDestination = enemyLocation;
			eeHanTiming = true;
		}
		
	}
}
