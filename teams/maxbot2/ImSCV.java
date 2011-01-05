package maxbot2;


import battlecode.common.*;

import java.util.ArrayList;

public class ImSCV
{
	
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> broadcasters, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{
		MovementController motor = (MovementController)motors.get(0);

		
		Navigation robotNavigation=new Navigation(player,myRC,motor);
		boolean north=false;
		boolean east=false;
		boolean west=false;
		boolean south=false;
		MapLocation destination;
		
        while (true)
        {
            try
            {
            	motor.setDirection(Direction.NORTH);
            	myRC.yield();
            	motor.setDirection(Direction.EAST);
            	myRC.yield();
            	motor.setDirection(Direction.WEST);
            	myRC.yield();
            	motor.setDirection(Direction.SOUTH);
            	myRC.yield();
            	
            	
            	
            	if (myRC.senseTerrainTile(new MapLocation(myRC.getLocation().x,myRC.getLocation().y+3)).equals(TerrainTile.OFF_MAP)) {
            		north=true;
            	}
            	if (myRC.senseTerrainTile(new MapLocation(myRC.getLocation().x+3,myRC.getLocation().y)).equals(TerrainTile.OFF_MAP)) {
            		east=true;
            	}
            	if (myRC.senseTerrainTile(new MapLocation(myRC.getLocation().x-3,myRC.getLocation().y)).equals(TerrainTile.OFF_MAP)) {
            		west=true;
            	}
            	if (myRC.senseTerrainTile(new MapLocation(myRC.getLocation().x,myRC.getLocation().y-3)).equals(TerrainTile.OFF_MAP)) {
            		south=true;
            	}
            	
            	
            	
            	if (north && east && !south && !west) {
            		destination=new MapLocation(-1000,-1000);
            	}
            	else if (north && !east && !south && !west) {
            		destination=new MapLocation(0,-1000);
            	}
            	else if (north && !east && !south && west) {
            		destination=new MapLocation(1000,-1000);
            	}
            	else if (!north && !east && !south && west) {
            		destination=new MapLocation(1000,0);
            	}
            	else if (!north && !east && south && west) {
            		destination=new MapLocation(1000,1000);
            	}
            	else if (!north && !east && south && !west) {
            		destination=new MapLocation(0,1000);
            	}
            	else if (!north && east && south && !west) {
            		destination=new MapLocation(-1000,1000);
            	}
            	else {
            		destination=new MapLocation(-1000,0);
            	}
            	myRC.setIndicatorString(0, destination.toString());
            	MapLocation finalDestination=new MapLocation(myRC.getLocation().x+destination.x,myRC.getLocation().y+destination.y);
                while (true) {
                	myRC.setIndicatorString(1, "bug nav!");
                	myRC.yield();
                	motor.setDirection(robotNavigation.bugTo(finalDestination));
                	myRC.yield();
                	motor.moveForward();
                	myRC.yield();
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
