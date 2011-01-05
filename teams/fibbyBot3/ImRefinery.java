package fibbyBot3;


import battlecode.common.*;
import java.util.ArrayList;

public class ImRefinery
{
	
	private static final int GUNS = 2;
	private static final ComponentType GUNTYPE = ComponentType.BLASTER;
	private static final ComponentType SENSORTYPE = ComponentType.SIGHT;
	private static final ComponentType COMMTYPE = ComponentType.ANTENNA;
	private static final ComponentType ARMORTYPE = ComponentType.SHIELD; 
	private static final int MARINES = 2;
	
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{
		SensorController sensor = (SensorController)sensors.get(0);
		MovementController motor = (MovementController)motors.get(0);
		BuilderController builder = (BuilderController)builders.get(0);
		boolean hasRadar = false;
		int rGuns;
		int rID;
		boolean rSensor;
		boolean rArmor;
		RobotInfo rInfo;
		GameObject[] nearbyRobots;
		boolean built;
		int marinesMade = 0;
		ArrayList<Integer> myRobots = new ArrayList<Integer>();
		Robot babyRobot;
		
		while (true)
		{
            try
            {
            	//myRC.setIndicatorString(0, Integer.toString(Clock.getRoundNum())); // DEBUG
				myRC.yield();
            	if (!hasRadar) // testing without radar! change to !hasRadar otherwise
            	{
            		while(myRC.getTeamResources()<2*ComponentType.RADAR.cost)
            		{
            			myRC.yield();
            		}
            		hasRadar = true;
            		builder.build(ComponentType.RADAR,myRC.getLocation(),RobotLevel.ON_GROUND);
            		myRC.setIndicatorString(1, "Radar installed!");
					for(ComponentController c:myRC.components())
					{
						if (c.type()==ComponentType.RADAR)
						{
							sensor = (SensorController)c;
						}
					}
            	}
            	else
            	{
					nearbyRobots = sensor.senseNearbyGameObjects(GameObject.class);
					//System.out.println("nearbyRobots : "+Integer.toString(nearbyRobots.length));
					for (GameObject r:nearbyRobots)
					{
						if(r.getTeam()!=Team.NEUTRAL)
						{
							rID = r.getID();
							rInfo = sensor.senseRobotInfo((Robot)r);
							//System.out.println("Robot"+Integer.toString(rID)+" : "+rInfo.components);
							if(rInfo.chassis == Chassis.LIGHT && myRC.getLocation().distanceSquaredTo(rInfo.location)<=2)
							{
								rGuns = 0;
								rSensor = false;
								rArmor = false;
								if (rInfo.components!=null)
								{
									for(ComponentType c:rInfo.components)
									{
										if (c==GUNTYPE)
											rGuns = rGuns+1;
										if (c==SENSORTYPE)
											rSensor = true;
										if (c==ARMORTYPE)
											rArmor = true;
									}
								}
								if (myRobots.contains(rID) && !rSensor)
								{
									rInfo = sensor.senseRobotInfo((Robot)r);
									motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
									myRC.yield();
									built = false;
									while (builder.withinRange(rInfo.location) && !built)
									{
										myRC.setIndicatorString(2, "Building sensor...");
										if (myRC.getTeamResources()>=2*SENSORTYPE.cost)
										{
											built = true;
											builder.build(SENSORTYPE,rInfo.location,RobotLevel.ON_GROUND);
										}
										else
											rInfo = sensor.senseRobotInfo((Robot)r);
										myRC.yield();
									}
									if (!built)
										myRC.setIndicatorString(2, "Target moved away.");
									else
										myRC.setIndicatorString(2, "Placed sensor on "+Integer.toString(rID)+".");
								}
								else if (myRobots.contains(rID) && rGuns<GUNS)
								{
									rInfo = sensor.senseRobotInfo((Robot)r);
									motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
									myRC.yield();
									built = false;
									while (builder.withinRange(rInfo.location) && !built)
									{
										myRC.setIndicatorString(2, "Building gun...");
										if (myRC.getTeamResources()>=2*GUNTYPE.cost)
										{
											built = true;
											builder.build(GUNTYPE,rInfo.location,RobotLevel.ON_GROUND);
										}
										else
											rInfo = sensor.senseRobotInfo((Robot)r);
										myRC.yield();
									}
									if (!built)
										myRC.setIndicatorString(2, "Target moved away.");
									else
										myRC.setIndicatorString(2, "Placed gun on "+Integer.toString(rID)+".");
								}
								else if (myRobots.contains(rID) && !rArmor)
								{
									rInfo = sensor.senseRobotInfo((Robot)r);
									motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
									myRC.yield();
									built = false;
									while (builder.withinRange(rInfo.location) && !built)
									{
										myRC.setIndicatorString(2, "Building armor...");
										if (myRC.getTeamResources()>=2*ARMORTYPE.cost)
										{
											built = true;
											builder.build(ARMORTYPE,rInfo.location,RobotLevel.ON_GROUND);
										}
										else
											rInfo = sensor.senseRobotInfo((Robot)r);
										myRC.yield();
									}
									if (!built)
										myRC.setIndicatorString(2, "Target moved away.");
									else
										myRC.setIndicatorString(2, "Placed armor on "+Integer.toString(rID)+".");
								}
								break;
							}
						}
					}
            	
					if(!motor.canMove(myRC.getDirection()))
					{
						motor.setDirection(myRC.getDirection().rotateRight());
						myRC.yield();
					}
					else if(marinesMade < MARINES && myRC.getTeamResources()>=2*Chassis.LIGHT.cost)
					{
						builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()));
						marinesMade++;
						babyRobot = (Robot)sensor.senseObjectAtLocation(myRC.getLocation().add(myRC.getDirection()), RobotLevel.ON_GROUND);
						myRobots.add(babyRobot.getID());
						myRC.yield();
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
