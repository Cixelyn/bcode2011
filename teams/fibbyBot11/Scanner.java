package fibbyBot11;

import java.util.*;
import battlecode.common.*;

public class Scanner {
	
	private final RobotPlayer myPlayer;
	private boolean canScan;
	private Class<? extends GameObject> detectionType;
	
	
	public GameObject[] nearbyGameObjects;
	
	public final ArrayList<Robot> detectedRobots;
	public final ArrayList<Mine> detectedMines;
	
	public final ArrayList<RobotInfo> scannedRobotInfos;
	public final ArrayList<MineInfo> scannedMineInfos;
	
	
	
	
	public Scanner(RobotPlayer player) {
		myPlayer = player;
		canScan = false;
		
		detectedRobots = new ArrayList<Robot>(144);
		detectedMines = new ArrayList<Mine>(144);
		
		scannedRobotInfos = new ArrayList<RobotInfo>(144);
		scannedMineInfos = new ArrayList<MineInfo>(144);
		
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
