package fibbyBot3;


import battlecode.common.*;
import java.util.ArrayList;

public class ImMarine
{
	
	private static final int GUNS = 2;
	private static final ComponentType GUNTYPE = ComponentType.BLASTER;
	private static final ComponentType SENSORTYPE = ComponentType.SIGHT;
	private static final ComponentType COMMTYPE = ComponentType.ANTENNA;
	private static final ComponentType ARMORTYPE = ComponentType.SHIELD; 
	private static final int MARINES = 2;
	
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{
		MovementController motor = (MovementController)motors.get(0);
		Navigation robotNavigation=new Navigation(player,myRC,motor);
		MapLocation destination = myRC.getLocation().add(Direction.SOUTH,500);
        int guns;
        WeaponController gun;
        GameObject[] nearbyRobots;
        RobotInfo rInfo;
        SensorController sensor = null;
        boolean hasSensor;
        boolean hasArmor;
        ArrayList<?>[] componentList;
        while (true) {
            try {
            	//myRC.setIndicatorString(0, Integer.toString(Clock.getRoundNum())); // DEBUG
            	componentList = Utilities.getComponents(myRC.components());
            	weapons = componentList[3];
                guns = 0;
                hasSensor = false;
                hasArmor = false;
				for(ComponentController c:myRC.components())
				{
					if (c.type()==GUNTYPE)
						guns = guns+1;
					if (c.type()==SENSORTYPE)
					{
						hasSensor = true;
						sensor = (SensorController)c;
					}
					if (c.type()==ARMORTYPE)
					{
						hasArmor = true;
					}
				}
				if (hasSensor && hasArmor)
					myRC.setIndicatorString(1, "Sensor and armor installed!");
				myRC.setIndicatorString(2, "I haz "+Integer.toString(guns)+" guns!");
				myRC.yield();
                if (guns >= GUNS && hasSensor && hasArmor)
                {
                	nearbyRobots = sensor.senseNearbyGameObjects(GameObject.class);
                	for(GameObject r:nearbyRobots)
                	{
    					for (Object c:weapons)
    					{
    						gun = (WeaponController) c;
    						if(!gun.isActive() && r.getTeam()==myRC.getTeam().opponent())
    						{
    							rInfo = sensor.senseRobotInfo((Robot)r);
    							myRC.setIndicatorString(0,"Enemy found!");
    							if(sensor.withinRange(rInfo.location))
    								rInfo = sensor.senseRobotInfo((Robot)r);
    							if(rInfo.hitpoints>0 && gun.withinRange(rInfo.location))
    							{
    								gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
    								myRC.setIndicatorString(0,"Pew pew pew!");
    							}
    						}
    					}
                	}
                	myRC.yield();
                	if (!motor.isActive())
                    {
    					/*if (motor.canMove(myRC.getDirection())) {
    	                   motor.moveForward();
    	                } else {
    	                    motor.setDirection(myRC.getDirection().rotateLeft());
    	                }*/
                		Direction direction = robotNavigation.bugTo(destination);
                		motor.setDirection(direction);
						myRC.yield();
						motor.moveForward();
                    }
                }

            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}
}
