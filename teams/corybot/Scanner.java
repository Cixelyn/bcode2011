package corybot;

import battlecode.common.*;

public class Scanner {
	
	private final RobotPlayer myPlayer;
	private boolean canScan;
	
	public GameObject[] nearbyGameObjects;
	
	Scanner(RobotPlayer player) {
		myPlayer = player;
		canScan = false;
	}
	
	
	public void InitialScan() {
		if(canScan)	nearbyGameObjects = myPlayer.mySensor.senseNearbyGameObjects(GameObject.class);
	
	}
	
	
	public void enableScanner() {
		canScan = true;
	}
	
	
	

}
