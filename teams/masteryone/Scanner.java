package masteryone;

import java.util.LinkedList;

import battlecode.common.*;

public class Scanner {
	
	private final RobotPlayer myPlayer;
	private boolean canScan;
	private Class<GameObject> detectionType;
	
	
	public GameObject[] nearbyGameObjects;
	
	public final LinkedList<Robot> detectedRobots;
	public final LinkedList<Mine> detectedMines;
	
	public final LinkedList<RobotInfo> scannedRobotInfos;
	public final LinkedList<MineInfo> scannedMineInfos;
	
	
	
	
	public Scanner(RobotPlayer player) {
		myPlayer = player;
		canScan = false;
		
		detectedRobots = new LinkedList<Robot>();
		detectedMines = new LinkedList<Mine>();
		
		scannedRobotInfos = new LinkedList<RobotInfo>();
		scannedMineInfos = new LinkedList<MineInfo>();
		
		//default detection type, can be changed by the others
		detectionType = GameObject.class;

		
	}
	
	
	/**
	 * Run this to fill the detectedRobots and the detectedMines list.
	 */
	public void InitialScan() {
		if(canScan) {
			nearbyGameObjects = myPlayer.mySensor.senseNearbyGameObjects(detectionType);
			
			detectedRobots.clear();
			detectedMines.clear();
			
			
			//Populate the list
			for(GameObject obj:nearbyGameObjects) {
				
				if(obj instanceof Robot) {
					detectedRobots.add((Robot)obj);
				}
				
				if(obj instanceof Mine) {
					detectedMines.add((Mine)obj);
				}	
			}
		}
	}
	
	
	public void detectRobots() {
		
		
	}
	
	
	public void enableScanner() {
		canScan = true;
	}
	
	public void setDetectionMode(Class<GameObject> type) {
		detectionType = type;		
	}
	
	
	
	

}
