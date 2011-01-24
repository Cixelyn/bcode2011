package costax3;

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
	
	
	
	private static final int RESOURCE_AVERAGE_WINDOW = 8;
	private final double[] resourceDeltas = new double[RESOURCE_AVERAGE_WINDOW];
	private int currResourceDeltaPos = 0;
	private double lastResourceCount = 1;
	         
	/**
	 * Computes the average resource collection rate
	 * @return
	 */
	public double averageResourceRate() {
		
		double currResources = myPlayer.myRC.getTeamResources();
		currResourceDeltaPos = (currResourceDeltaPos + 1)%RESOURCE_AVERAGE_WINDOW;
		resourceDeltas[currResourceDeltaPos] = currResources - lastResourceCount;
		
		
		//Compute Sum
		double total=0;
		for(int i=0;i<RESOURCE_AVERAGE_WINDOW;i++){
			total += resourceDeltas[i];						
		}
		lastResourceCount = currResources;
		
		
		return total/RESOURCE_AVERAGE_WINDOW;
	}
	
	
	

}
