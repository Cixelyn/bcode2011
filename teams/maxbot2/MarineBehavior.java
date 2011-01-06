package maxbot2;

import battlecode.common.*;
import java.util.ArrayList;

public class MarineBehavior extends Behavior {
	
	WeaponController gun;
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation destination;
	MapLocation prevDestination = destination;
	
	Direction direction;
	Direction enemyDirection;
	
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

		if(!moveOut)
		{
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
			//myPlayer.myRC.setIndicatorString(1,"I haz "+Integer.toString(guns)+" guns.");
			myPlayer.myRC.yield();
			msgs = myPlayer.myRC.getAllMessages();
			for(Message m:msgs)
			{
				if (m.ints != null && m.ints[0] == 4774 && m.strings != null && m.strings[0] != "idk")
				{
					spawn = m.strings[0];
					//myPlayer.myRC.setIndicatorString(0,"(marine) | knows spawn");
					enemyDirection = Utility.spawnOpposite(spawn);
					destination = myPlayer.myRC.getLocation().add(enemyDirection, 500);
					prevDestination = destination;
					eeHanTiming = true;
					//myPlayer.myRC.setIndicatorString(1, "Going to the enemy.");
				}
			}
			moveOut = eeHanTiming && guns >= Constants.GUNS && hasSensor && hasArmor;
		}
		else
        {
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
						//myPlayer.myRC.setIndicatorString(1,"Enemy found!");
						destination = rInfo.location;
						staleness = 0;
						if(rInfo.hitpoints>0 && gun.withinRange(rInfo.location))
						{
							gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
							//myPlayer.myRC.setIndicatorString(1,"Pew pew pew!");
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
        			//myPlayer.myRC.setIndicatorString(1, "Going to the enemy.");
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
	public void newMessageCallback(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
