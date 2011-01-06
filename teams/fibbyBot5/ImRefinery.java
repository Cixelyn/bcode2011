package fibbyBot5;


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
	private static final int OLDNEWS = 15;
	private static final int RESERVE = 5;
	
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> broadcasters, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{
		SensorController sensor = (SensorController)sensors.get(0);
		MovementController motor = (MovementController)motors.get(0);
		BuilderController builder = (BuilderController)builders.get(0);
		BroadcastController broadcaster = null;
		
		RefineryBuildOrder obj = RefineryBuildOrder.INITIALIZE;
		
		int rGuns;
		boolean rSensor;
		boolean rArmor;
		RobotInfo rInfo = null;
		Robot[] nearbyRobots;
		boolean isLeader = false;
		Message[] msgs;
		boolean eeHanTiming = false;
		String spawn;
		Message attackMsg = null;
		
		int marinesMade = 0;
		ArrayList<Integer> myRobots = new ArrayList<Integer>();
		Robot babyRobot = null;
		GameObject rFront = null;
		
		while (true)
		{
            try
            {
            	switch(obj)
            	{
            		case INITIALIZE:
            			myRC.setIndicatorString(2, "INITIALIZE");
            			while(myRC.getTeamResources() < COMMTYPE.cost + RESERVE || builder.isActive())
            				myRC.yield();
            			builder.build(COMMTYPE,myRC.getLocation(),RobotLevel.ON_GROUND);
            			myRC.setIndicatorString(1, "Antenna installed!");
            			for(ComponentController c:myRC.components())
    					{
    						if (c.type()==COMMTYPE)
    						{
    							broadcaster = (BroadcastController)c;
    						}
    					}
            			obj = RefineryBuildOrder.GIVE_ANTENNA;
            			myRC.yield();
            			break;
            			
            		case GIVE_ANTENNA:
            			myRC.setIndicatorString(2, "GIVE_ANTENNA");
            			nearbyRobots = sensor.senseNearbyGameObjects(Robot.class);
            			for (Robot r:nearbyRobots)
            			{
            				rInfo = sensor.senseRobotInfo(r);
            				if (rInfo.chassis == Chassis.BUILDING && myRC.getRobot().getID() < rInfo.robot.getID())
            					isLeader = true;
            			}
            			if (isLeader)
            			{
            				nearbyRobots = sensor.senseNearbyGameObjects(Robot.class);
            				for (Robot r:nearbyRobots)
            				{
            					rInfo = sensor.senseRobotInfo(r);
		    					if (rInfo.chassis == Chassis.LIGHT && myRC.getLocation().distanceSquaredTo(rInfo.location)<=2 && !motor.isActive() && !builder.isActive())
		    					{
		    						myRC.setIndicatorString(1,"I'm the leader!");
		    						motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
		    						myRC.yield();
		    						while (myRC.getTeamResources() < COMMTYPE.cost + RESERVE)
		    							myRC.yield();
		    						builder.build(COMMTYPE, rInfo.location, RobotLevel.ON_GROUND);
		    						obj = RefineryBuildOrder.WAIT_FOR_SIGNAL;
		    					}
            				}
            			}
            			else
            				obj = RefineryBuildOrder.WAIT_FOR_SIGNAL;
            			myRC.yield();
            			break;
            			
            		case WAIT_FOR_SIGNAL:
            			myRC.setIndicatorString(2, "WAIT_FOR_SIGNAL");
            			msgs = myRC.getAllMessages();
            			for (Message m:msgs)
            			{
            				if(m.ints != null && m.ints[0] == 9090)
            				{
            					myRC.setIndicatorString(1,"Message received!");
                				obj = RefineryBuildOrder.MAKE_MARINE;
            				}
            				if(m.ints != null && m.ints[0] == 4774 && m.strings != null && m.strings[0] != "idk")
            				{
    							myRC.setIndicatorString(0,"(refinery) | knows spawn");
    							attackMsg = m;
            					eeHanTiming = true;
            				}
            			}
            			myRC.yield();
            			break;
            			
            		case MAKE_MARINE:
            			myRC.setIndicatorString(2, "MAKE_MARINE");
            			if(!motor.canMove(myRC.getDirection()) || sensor.senseObjectAtLocation(myRC.getLocation().add(myRC.getDirection()), RobotLevel.MINE) != null)
    					{
    						motor.setDirection(myRC.getDirection().rotateRight());
    					}
            			else if(marinesMade < MARINES && myRC.getTeamResources() >= Chassis.LIGHT.cost + RESERVE)
    					{
    						builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()));
    						babyRobot = (Robot)sensor.senseObjectAtLocation(myRC.getLocation().add(myRC.getDirection()), RobotLevel.ON_GROUND);
    						myRobots.add(babyRobot.getID());
    						obj = RefineryBuildOrder.EQUIP_MARINE;
    					}
            			else if(marinesMade >= MARINES + 1)
            			{
            				obj = RefineryBuildOrder.SLEEP;
            			}
            			myRC.yield();
            			break;
            			
            		case EQUIP_MARINE:
            			myRC.setIndicatorString(2, "EQUIP_MARINE");
            			rFront = sensor.senseObjectAtLocation(myRC.getLocation().add(myRC.getDirection()), RobotLevel.ON_GROUND);
            			if(rFront != null && ((Robot)rFront).getID() == babyRobot.getID())
            			{
            				rInfo = sensor.senseRobotInfo((Robot)rFront);
            				rGuns = 0;
            				rSensor = false;
            				rArmor = false;
            				if(rInfo.components != null)
            				{
            					for (ComponentType c:rInfo.components)
            					{
            						if (c == GUNTYPE)
            							rGuns++;
            						if (c == SENSORTYPE)
            							rSensor = true;
            						if (c == ARMORTYPE)
            							rArmor = true;
            					}
            				}
            				if (rGuns < GUNS)
            					Utilities.buildComponent(myRC, builder, GUNTYPE, "blaster", ((Robot)rFront).getID());
            				else if (!rSensor)
            					Utilities.buildComponent(myRC, builder, SENSORTYPE, "sight", ((Robot)rFront).getID());
            				else if (!rArmor)
            					Utilities.buildComponent(myRC, builder, ARMORTYPE, "shield", ((Robot)rFront).getID());
            				else
            				{
            					marinesMade++;
            					obj = RefineryBuildOrder.MAKE_MARINE;
            					msgs = myRC.getAllMessages();
            					for(Message m:msgs)
            					{
            						if(m.ints != null && m.ints[0] == 4774 && m.strings != null && m.strings[0] != "idk")
                    				{
            							myRC.setIndicatorString(0,"(refinery) | knows spawn");
            							attackMsg = m;
                    					eeHanTiming = true;
                        				obj = RefineryBuildOrder.MAKE_MARINE;
                    				}
            					}
            					if (eeHanTiming)
            					{
            						while(broadcaster.isActive())
            							myRC.yield();
            						broadcaster.broadcast(attackMsg);
            					}
            				}
            			}
            			else
            			{
            				obj = RefineryBuildOrder.MAKE_MARINE;
            			}
            			myRC.yield();
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
