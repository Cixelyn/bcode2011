package costax3.behaviors;

import costax3.Constants;
import costax3.Encoder;
import costax3.RobotPlayer;
import costax3.Scanner;
import battlecode.common.*;

public class JimmyBattleStrategy extends BattleStrategy{
	private Scanner myScanner;


	public JimmyBattleStrategy(RobotPlayer player) {
		super(player);
		this.myScanner=new Scanner(player);
	}

	
	public void runBehaviors() {
		myScanner.InitialScan();
		Robot[] nearbyRobots = player.mySensor.senseNearbyGameObjects(Robot.class);
		int[] priority = new int[4];
		MapLocation[] robotLocations=new MapLocation[4];
		int index=0;
		RobotInfo rInfo;
		for (Robot robot : nearbyRobots) {
			if (!robot.getTeam().equals(player.myRC.getTeam())) {
				try {
					rInfo=player.mySensor.senseRobotInfo(robot);
					priority[index]=Encoder.encodeRobotInfo(1,robot,rInfo);
					robotLocations[index]=rInfo.location;
					index=index+1;
					if (index==Constants.ENEMIES_COUNT) {
						break;
					}
				} catch (GameActionException e1) {
					e1.printStackTrace();
				}
			}
		}
		Message message=new Message();
		message.ints=priority;
		message.locations=robotLocations;
		try {
			player.myBroadcaster.broadcast(message);
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		
	}
}
