package team068;

import battlecode.common.*;




/**
 * The cartographer class charts map exploration and provides feedback on places to explore, etc.
 * 
 * @author Cory
 *
 */
public class Cartographer {
	
	//Define constants here for fast access
	public final RobotPlayer myPlayer;
	public static ComponentController mySensor;
	
	boolean seenNorth;
	boolean seenSouth;
	boolean seenEast;
	boolean seenWest;
	
	int coordNorth;
	int coordSouth;
	int coordWest;
	int coordEast;
	
	
	
	/**
	 * Instantiates a new Cartographer system.
	 * @param player
	 */
	public Cartographer(RobotPlayer player) {
		
		//Initialize the main components
		myPlayer = player;
		mySensor = null;
		
		//Initialize the coordinates and whether things have been seen
		seenNorth = seenSouth = seenEast = seenWest = false;
		coordNorth = coordSouth = coordWest = coordEast = 0;
		
	}
	
	
	
	/**
	 * This function sets the current sensor type.
	 * The private flag sensorType is set depending on the controller type.
	 * @param c
	 */
	public void setSensor(ComponentController c) {
		mySensor = c;	
	}
	

	
	/**
	 * Sense the terrain and enter the information into the mapping engine.
	 */
	public void runSensor() {
		MapLocation myLoc = myPlayer.myRC.getLocation();
		
		switch(mySensor.type()) {
		
		//Cases for the radar
		case RADAR:
			if(!seenNorth) {}
			if(!seenSouth) {}
			if(!seenEast) {}
			if(!seenWest) {}
			
			
			
			break;
			
		default: 
			Utility.printMsg(myPlayer,"Cartographer Error: Wrong Sensor Type");
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
