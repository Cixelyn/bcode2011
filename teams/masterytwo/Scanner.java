package masterytwo;

import java.util.*;
import battlecode.common.*;

public class Scanner {
	
	private final RobotPlayer myPlayer;
	private boolean canScan;
	private Class<? extends GameObject> detectionType;
	
	
	public GameObject[] nearbyGameObjects;
	
	public final ArrayDeque<Robot> detectedRobots;
	public final ArrayDeque<Mine> detectedMines;
	
	public final ArrayDeque<RobotInfo> scannedRobotInfos;
	public final ArrayDeque<MineInfo> scannedMineInfos;
	
	
	
	
	public Scanner(RobotPlayer player) {
		myPlayer = player;
		canScan = false;
		
		detectedRobots = new ArrayDeque<Robot>(144);
		detectedMines = new ArrayDeque<Mine>(144);
		
		scannedRobotInfos = new ArrayDeque<RobotInfo>(144);
		scannedMineInfos = new ArrayDeque<MineInfo>(144);
		
		//default detection type, can be changed by the others
		detectionType = GameObject.class;

		
	}
	
	
	/**
	 * Run this to fill the detectedRobots and the detectedMines list.
	 * Also fills in scannedRobotInfos and scannedMineInfos
	 */
	public void InitialScan() throws Exception
	{
		if(canScan)
		{
			nearbyGameObjects = myPlayer.mySensor.senseNearbyGameObjects(detectionType);
			
			detectedRobots.clear();
			detectedMines.clear();
			scannedRobotInfos.clear();
			scannedMineInfos.clear();
			
			//Populate the list
			for(GameObject obj:nearbyGameObjects)
			{
				
				if(obj instanceof Robot)
				{
					detectedRobots.add((Robot)obj);
					scannedRobotInfos.add(myPlayer.mySensor.senseRobotInfo((Robot)obj));
				}
				
				if(obj instanceof Mine)
				{
					detectedMines.add((Mine)obj);
					scannedMineInfos.add(myPlayer.mySensor.senseMineInfo((Mine)obj));
				}	
			}
		}
	}
	
	
	public void detectRobots() {
		
		
	}
	
	
	public void enableScanner() {
		canScan = true;
	}
	
	public void setDetectionMode(Class<? extends GameObject> type) {
		detectionType = type;		
	}
	
	
	
	

}
