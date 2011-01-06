package fibbyBot5;


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
	private static final int OLDNEWS = 5;
	private static final int RESERVE = 5;
	
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> broadcasters, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{
		MovementController motor = (MovementController)motors.get(0);
		
		Navigation robotNavigation=new Navigation(player,myRC,motor);
		MapLocation destination = null;
		MapLocation prevDestination = destination;
		Direction direction;
		int staleness = 0;
		
        int guns;
        WeaponController gun;
        GameObject[] nearbyRobots;
        RobotInfo rInfo;
        SensorController sensor = null;
        boolean hasSensor;
        boolean hasArmor;
        ArrayList<?>[] componentList;
        
        Message[] msgs;
        String spawn;
        Direction enemyDirection;
        boolean eeHanTiming = false;
        
        while (true)
        {
            try {
	            	componentList = Utilities.getComponents(myRC.components());
	            	weapons = componentList[4];
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
					myRC.setIndicatorString(1,"I haz "+Integer.toString(guns)+" guns.");
					myRC.yield();
					msgs = myRC.getAllMessages();
					for(Message m:msgs)
					{
						if (m.ints != null && m.ints[0] == 4774 && m.strings != null && m.strings[0] != "idk")
						{
							spawn = m.strings[0];
							myRC.setIndicatorString(0,"(marine) | knows spawn");
							enemyDirection = Utilities.spawnOpposite(spawn);
							destination = myRC.getLocation().add(enemyDirection, 500);
							eeHanTiming = true;
						}
					}
	                if (eeHanTiming && guns >= GUNS && hasSensor && hasArmor)
	                {
	                	myRC.setIndicatorString(1,"EE HAN TIMING!");
	                	nearbyRobots = sensor.senseNearbyGameObjects(GameObject.class);
	                	for(GameObject r:nearbyRobots)
	                	{
	    					for (Object c:weapons)
	    					{
	    						gun = (WeaponController) c;
	    						if(!gun.isActive() && r.getTeam()==myRC.getTeam().opponent())
	    						{
	    							rInfo = sensor.senseRobotInfo((Robot)r);
	    							myRC.setIndicatorString(1,"Enemy found!");
	    							destination = rInfo.location;
	    							staleness = 0;
	    							if(rInfo.hitpoints>0 && gun.withinRange(rInfo.location))
	    							{
	    								gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
	    								myRC.setIndicatorString(1,"Pew pew pew!");
	    							}
	    						}
	    					}
	                	}
	                	myRC.yield();
	                	if (!motor.isActive())
	                    {
	                		direction = robotNavigation.bugTo(destination);
	                		staleness++;
	                		if (staleness >= OLDNEWS)
	                		{
	                			destination = prevDestination;
	                		}
	                		if (direction != Direction.OMNI && direction != Direction.NONE)
	                		{
		                		motor.setDirection(direction);
								myRC.yield();
								while(!motor.canMove(myRC.getDirection()))
								{
									myRC.yield();
								}
								motor.moveForward();
	                		}
	                    }
	                }

            }
            catch (Exception e)
            {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}
}
