package costax3;

import battlecode.common.*;

public class JimmyBattleStrategy extends BattleStrategy{
	private Scanner myScanner;


	public JimmyBattleStrategy(RobotPlayer player) {
		super(player);
		this.myScanner=new Scanner(player);
	}

	
	public void runBehaviors() {
		myScanner.InitialScan();
		RobotInfo rInfo;
		Robot[] nearbyRobots = player.mySensor.senseNearbyGameObjects(Robot.class);
		for (Robot robot : nearbyRobots) {
			if (robot.getTeam().equals(player.myRC.getTeam())) {
				try {
					rInfo = player.mySensor.senseRobotInfo(robot);
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
