package fibbyBot2;

import battlecode.common.*;

import java.util.*;

import fibbyBot2.Navigation;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;
	private final int SMGS = 3;
	private final int MARINES = 1;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

	public void run() {
		//ComponentController[] components = myRC.newComponents();
		//System.out.println(myRC.components().length);
		//System.out.println(java.util.Arrays.toString(components));
		//System.out.flush();
		ArrayList<?>[] componentList = getComponents(myRC.components());
		ArrayList<?> builders = componentList[0];
		ArrayList<?> motors = componentList[1];
		ArrayList<?> sensors = componentList[2];
		ArrayList<?> weapons = componentList[3];
		if(myRC.getChassis()==Chassis.BUILDING)
		{
			myRC.setIndicatorString(0, "(refinery)");
			imRefinery(builders, motors, sensors, weapons);
		}
		else if (builders.size()>0)
		{
			myRC.setIndicatorString(0, "In the rear with the gear!");
			imSCV(builders, motors, sensors, weapons);
		}
		else
		{
			myRC.setIndicatorString(0, "Go, go, go!");
			imMarine(builders, motors, sensors, weapons);
		}
	}
	
	public ArrayList<?>[] getComponents(ComponentController[] components)
	{
		ArrayList<BuilderController> builders = new ArrayList<BuilderController>();
		ArrayList<MovementController> motors = new ArrayList<MovementController>();
		ArrayList<SensorController> sensors = new ArrayList<SensorController>();
		ArrayList<WeaponController> weapons = new ArrayList<WeaponController>();
		for(ComponentController c:components)
		{
			switch (c.componentClass())
			{
				case ARMOR: 
					break;
				case BUILDER: builders.add((BuilderController)c);
					break;
				case COMM:
					break;
				case MISC:
					break;
				case MOTOR: motors.add((MovementController)c);
					break;
				case SENSOR: sensors.add((SensorController)c);
					break;
				case WEAPON: weapons.add((WeaponController)c);
					break;
			}
		}
		ArrayList<?>[] componentList = {builders,motors,sensors,weapons};
		return componentList;
	}
	
	public void imRefinery(ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons) {
		SensorController sensor = (SensorController)sensors.get(0);
		MovementController motor = (MovementController)motors.get(0);
		BuilderController builder = (BuilderController)builders.get(0);
		boolean hasRadar = false;
		int rGuns;
		int rID;
		boolean rRadar;
		RobotInfo rInfo;
		GameObject[] nearbyRobots;
		boolean built;
		boolean isSCV;
		int marinesMade = 0;
		while (true) {
            try {
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
						if(r.getTeam()!=Team.NEUTRAL && myRC.getTeamResources()>=2*ComponentType.SMG.cost)
						{
							rID = r.getID();
							rInfo = sensor.senseRobotInfo((Robot)r);
							//System.out.println("Robot"+Integer.toString(rID)+" : "+rInfo.components);
							if(rInfo.chassis == Chassis.LIGHT && myRC.getLocation().distanceSquaredTo(rInfo.location)<4)
							{
								rGuns = 0;
								rRadar = false;
								isSCV = false;
								for(ComponentType c:rInfo.components)
								{
									if (c==ComponentType.SMG)
										rGuns = rGuns+1;
									if (c==ComponentType.RADAR)
										rRadar = true;
									if (c==ComponentType.CONSTRUCTOR)
										isSCV = true;
								}
								if (!isSCV && !rRadar)
								{
									rInfo = sensor.senseRobotInfo((Robot)r);
									motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
									myRC.yield();
									built = false;
									while (myRC.getDirection()==myRC.getLocation().directionTo(rInfo.location) && myRC.getLocation().distanceSquaredTo(rInfo.location)<4 && !built)
									{
										myRC.setIndicatorString(2, "Building radar...");
										if (myRC.getTeamResources()>=2*ComponentType.RADAR.cost)
										{
											built = true;
											builder.build(ComponentType.RADAR,rInfo.location,RobotLevel.ON_GROUND);
										}
										else
											rInfo = sensor.senseRobotInfo((Robot)r);
										myRC.yield();
									}
									if (!built)
										myRC.setIndicatorString(2, "Target moved away.");
									else
										myRC.setIndicatorString(2, "Placed radar on "+Integer.toString(rID)+".");
								}
								else if (!isSCV && rGuns<SMGS)
								{
									rInfo = sensor.senseRobotInfo((Robot)r);
									motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
									myRC.yield();
									built = false;
									while (myRC.getDirection()==myRC.getLocation().directionTo(rInfo.location) && myRC.getLocation().distanceSquaredTo(rInfo.location)<4 && !built)
									{
										myRC.setIndicatorString(2, "Building gun...");
										if (myRC.getTeamResources()>=2*ComponentType.SMG.cost)
										{
											built = true;
											builder.build(ComponentType.SMG,rInfo.location,RobotLevel.ON_GROUND);
											myRC.setIndicatorString(2, "Placed gun on "+Integer.toString(rID)+".");
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
						myRC.yield();
					}
            	}
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}

    public void imSCV(ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons) {
		//SensorController sensor = (SensorController)sensors.get(0);
		//MovementController motor = (MovementController)motors.get(0);
		//BuilderController builder = (BuilderController)builders.get(0);
		//Navigation robotNavigation=new Navigation(this,myRC,motor);
        while (true) {
            try {
                /*** beginning of main loop ***/
				/*else if (!motor.isActive())
                {
					if (motor.canMove(myRC.getDirection())) {
	                   motor.moveForward();
	                } else {
	                    motor.setDirection(myRC.getDirection().rotateLeft());
	                }
                }*/
                myRC.yield();

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
    
    public void imMarine(ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons) {
		MovementController motor = (MovementController)motors.get(0);
		Navigation robotNavigation=new Navigation(this,myRC,motor);
		MapLocation destination = myRC.getLocation().add(Direction.SOUTH,500);
        int guns;
        WeaponController smg;
        GameObject[] nearbyRobots;
        RobotInfo rInfo;
        SensorController sensor = null;
        boolean hasSensor;
        ArrayList<?>[] componentList;
        while (true) {
            try {
                /*** beginning of main loop ***/
            	componentList = getComponents(myRC.components());
            	weapons = componentList[3];
                guns = 0;
                hasSensor = false;
				for(ComponentController c:myRC.components())
				{
					if (c.type()==ComponentType.SMG)
						guns = guns+1;
					if (c.type()==ComponentType.RADAR)
					{
						hasSensor = true;
						myRC.setIndicatorString(1, "Radar installed!");
						sensor = (SensorController)c;
					}
				}
				myRC.setIndicatorString(2, "I haz "+Integer.toString(guns)+" guns!");
                if (guns >= SMGS && hasSensor)
                {
                	nearbyRobots = sensor.senseNearbyGameObjects(GameObject.class);
                	for(Object c:weapons)
                	{
                		smg = (WeaponController) c;
    					for (GameObject r:nearbyRobots)
    					{
    						if(!smg.isActive() && r.getTeam()==myRC.getTeam().opponent())
    						{
    							rInfo = sensor.senseRobotInfo((Robot)r);
    							myRC.setIndicatorString(0,"Enemy found!");
    							if(sensor.withinRange(rInfo.location))
    								rInfo = sensor.senseRobotInfo((Robot)r);
    							if(rInfo.hitpoints>0 && smg.withinRange(rInfo.location))
    							{
    								smg.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
    								myRC.setIndicatorString(0,"Pew pew pew!");
    							}
    						}
    						myRC.yield();
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
                myRC.yield();

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
