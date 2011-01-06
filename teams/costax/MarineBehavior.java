package costax;

import battlecode.common.*;
import java.util.ArrayList;

public class MarineBehavior extends Behavior {
	
	WeaponController gun;
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation hometown;
	MapLocation enemyLocation;
	MapLocation destination;
	MapLocation prevDestination = destination;
	
	MarineBuildOrder obj = MarineBuildOrder.CONSTRUCTING;
	
	Direction direction;
	
	int staleness = 0;
	int guns;
	
	boolean hasSensor;
    boolean hasArmor;
	boolean eeHanTiming = false;
    boolean moveOut = false;
    
    GameObject[] nearbyRobots;
    RobotInfo rInfo;
    
    ArrayList<?>[] componentList;
    
    Message[] msgs;
    
    String spawn;
	
	public MarineBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception {
		
		
		switch (obj) {
			case CONSTRUCTING:
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
				myPlayer.myRC.yield();
				if (guns >= Constants.GUNS && hasSensor && hasArmor) {
					obj=MarineBuildOrder.WAITING;
				}
			case WAITING:
	        	for(GameObject r:nearbyRobots)
	        	{
					for (Object c:myPlayer.myWeapons)
					{
						gun = (WeaponController) c;
						if(!gun.isActive() && r.getTeam()==myPlayer.myRC.getTeam().opponent())
						{
							rInfo = myPlayer.mySensor.senseRobotInfo((Robot)r);
						 	destination = rInfo.location;
							staleness = 0;
							if(rInfo.hitpoints>0 && gun.withinRange(rInfo.location))
							{
								gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
							}
						}
					}
	        	}
	        	if (eeHanTiming) {
	        		obj=MarineBuildOrder.FIND_ENEMY;
	        	}
			case FIND_ENEMY:
	        	//myPlayer.myRC.setIndicatorString(2,"EE HAN TIMING!");
	        	nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(GameObject.class);
	        	for(GameObject r:nearbyRobots)
	        	{
					for (Object c:myPlayer.myWeapons)
					{
						gun = (WeaponController) c;
						if(!gun.isActive() && r.getTeam()==myPlayer.myRC.getTeam().opponent())
						{
							rInfo = myPlayer.mySensor.senseRobotInfo((Robot)r);
							myPlayer.myRC.setIndicatorString(1,"Enemy found!");
						 	destination = rInfo.location;
							staleness = 0;
							if(rInfo.hitpoints>0 && gun.withinRange(rInfo.location))
							{
								gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
								myPlayer.myRC.setIndicatorString(1,"Pew pew pew!");
							}
						}
					}
	        	}
	        	myPlayer.myRC.yield();
	        	if (!myPlayer.myMotor.isActive())
	            {
	        		direction = robotNavigation.bugTo(destination);
	        		staleness++;
	        		if (staleness >= Constants.OLDNEWS)
	        		{
	        			myPlayer.myRC.setIndicatorString(1, "Going to the enemy.");
	        			destination = prevDestination;
	        		}
	        		if (direction != Direction.OMNI && direction != Direction.NONE)
	        		{
	            		myPlayer.myMotor.setDirection(direction);
						myPlayer.myRC.yield();
						if (staleness >= Constants.OLDNEWS || myPlayer.myRC.getLocation().distanceSquaredTo(destination) >= Constants.GUNTYPE.range)
						{
							while(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
							{
								myPlayer.myRC.yield();
							}
							myPlayer.myMotor.moveForward();
	        			}
	        		}
	            }
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
			destination = enemyLocation;
			prevDestination = destination;
			eeHanTiming = true;
		}
		
	}
}
