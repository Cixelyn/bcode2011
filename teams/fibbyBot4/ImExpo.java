package fibbyBot4;


import battlecode.common.*;

import java.util.ArrayList;

public class ImExpo
{
	
	private static final int GUNS = 2;
	private static final ComponentType GUNTYPE = ComponentType.BLASTER;
	private static final ComponentType SENSORTYPE = ComponentType.SIGHT;
	private static final ComponentType ARMORTYPE = ComponentType.SHIELD; 
	private static final int MARINES = 2;
	private static final int RESERVE = 5;
	
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> broadcasters, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{		
		BuilderController builder = null;
		MovementController motor = (MovementController) motors.get(0);
		SensorController sensor = null;
		
		RefineryBuildOrder obj = RefineryBuildOrder.WAIT_FOR_SIGNAL;
		
		int rGuns;
		int rID;
		boolean rSensor;
		boolean rArmor;
		RobotInfo rInfo = null;
		GameObject[] nearbyRobots;
		boolean built = false;
		ArrayList<?>[] componentList;
		
		int marinesMade = 0;
		ArrayList<Integer> myRobots = new ArrayList<Integer>();
		Robot babyRobot;
		
		while (true)
		{
            try
            {
            	switch(obj)
            	{
            		case WAIT_FOR_SIGNAL:
            			myRC.setIndicatorString(2, "WAIT_FOR_SIGNAL");
            			if(myRC.getAllMessages().length>0)
            			{
            				myRC.setIndicatorString(1,"Message received!");
            				componentList = Utilities.getComponents(myRC.components());
            				builder = (BuilderController)componentList[1].get(0);
            				obj = RefineryBuildOrder.GET_RADAR;
            			}
            			myRC.yield();
            			break;
            			
	            	case GET_RADAR:
	        			myRC.setIndicatorString(2, "GET_RADAR");
	        			while(builder.isActive() || myRC.getTeamResources()<ComponentType.RADAR.cost + RESERVE)
	        				myRC.yield();
	        			builder.build(ComponentType.RADAR,myRC.getLocation(),RobotLevel.ON_GROUND);
	        			myRC.setIndicatorString(1, "Radar installed!");
	        			for(ComponentController c:myRC.components())
						{
							if (c.type()==ComponentType.RADAR)
							{
								sensor = (SensorController)c;
							}
						}
	        			obj = RefineryBuildOrder.MAKE_MARINE;
	        			myRC.setIndicatorString(1,"Message received!");
        				obj = RefineryBuildOrder.MAKE_MARINE;
	        			myRC.yield();
	        			break;
	        	
            		case MAKE_MARINE:
            			myRC.setIndicatorString(2, "MAKE_MARINE");
            			if(!motor.canMove(myRC.getDirection()))
    					{
    						motor.setDirection(myRC.getDirection().rotateRight());
    					}
    					else if(marinesMade < MARINES && myRC.getTeamResources()>=2*Chassis.LIGHT.cost)
    					{
    						builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()));
    						marinesMade++;
    						babyRobot = (Robot)sensor.senseObjectAtLocation(myRC.getLocation().add(myRC.getDirection()), RobotLevel.ON_GROUND);
    						myRobots.add(babyRobot.getID());
    						obj = RefineryBuildOrder.EQUIP_MARINE;
    					}
            			myRC.yield();
            			break;
            			
            		case EQUIP_MARINE:
            			myRC.setIndicatorString(2, "EQUIP_MARINE");
            			nearbyRobots = sensor.senseNearbyGameObjects(GameObject.class);
    					for (GameObject r:nearbyRobots)
    					{
    						if(sensor.canSenseObject(r) && r.getTeam()==myRC.getTeam())
    						{
    							rID = r.getID();
    							if(sensor.canSenseObject(r))
    								rInfo = sensor.senseRobotInfo((Robot)r);
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
    									while(motor.isActive())
    	    								myRC.yield();
    	    							motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    							myRC.yield();
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
    										{
    											while(motor.isActive())
    	    	    								myRC.yield();
    	    	    							motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    	    							myRC.yield();
    	    	    							rInfo = sensor.senseRobotInfo((Robot)r);
    	    									motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    									myRC.yield();
    										}
    										myRC.yield();
    									}
    									if (!built)
    										myRC.setIndicatorString(2, "Target moved away.");
    									else
    										myRC.setIndicatorString(2, "Placed sensor on "+Integer.toString(rID)+".");
    								}
    								else if (myRobots.contains(rID) && rGuns<GUNS)
    								{
    									while(motor.isActive())
    	    								myRC.yield();
    	    							motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    							myRC.yield();
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
    										{
    											while(motor.isActive())
    	    	    								myRC.yield();
    	    	    							motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    	    							myRC.yield();
    	    	    							rInfo = sensor.senseRobotInfo((Robot)r);
    	    									motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    									myRC.yield();
    										}
    										myRC.yield();
    									}
    									if (!built)
    										myRC.setIndicatorString(2, "Target moved away.");
    									else
    										myRC.setIndicatorString(2, "Placed gun on "+Integer.toString(rID)+".");
    								}
    								else if (myRobots.contains(rID) && !rArmor)
    								{
    									while(motor.isActive())
    	    								myRC.yield();
    	    							motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    							myRC.yield();
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
    										{
    											while(motor.isActive())
    	    	    								myRC.yield();
    	    	    							motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    	    							myRC.yield();
    	    	    							rInfo = sensor.senseRobotInfo((Robot)r);
    	    									motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
    	    									myRC.yield();
    										}
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
    						myRC.yield();
    					}
    					if (!built)
    					{
    						if (marinesMade == MARINES)
    							obj = RefineryBuildOrder.SLEEP;
    						else
    							obj = RefineryBuildOrder.MAKE_MARINE;
    					}
            			break;
            			
            		case SLEEP:
            			myRC.yield();
            			break;
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
