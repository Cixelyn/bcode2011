package team068;
import battlecode.common.*;

public class SentryBattleStrategy extends BattleStrategy {
	
	private Scanner myScanner;

	public SentryBattleStrategy(RobotPlayer player) {
		super(player);
		this.myScanner=new Scanner(player);
	}

	@Override
	public void runBehaviors() {
		boolean foundEnemy=false;
		Message[] messages = player.myRC.getAllMessages();
		myScanner.detectRobots();
		if (messages!=null) {
			for (Message message: messages) { //see all incoming broadcasts
				if (message.ints.length==Constants.ENEMIES_COUNT+2) { //we have an EnemyFound message (will probably change later with header?)
					for (int i=0;i<Constants.ENEMIES_COUNT;i++) { 
						for (int j=0;j<Constants.ENEMIES_COUNT;j++) {
							 if (message.ints[j]==i) {
								 for (WeaponController weapon : player.myWeapons) {
									 if (!weapon.isActive() && weapon.withinRange(message.locations[i])) {
										 try {
											weapon.attackSquare(message.locations[i], RobotLevel.ON_GROUND);
										} catch (GameActionException e) {
											e.printStackTrace();
										}
									 }
								 }
								 foundEnemy=true;
								 break;
							 }
						}
						if (foundEnemy) {
							break;
						}
					}
				}
				if (foundEnemy) {
					break;
				}
			}
		}
		else {
			try {
				Utility.senseEnemies(player);
			} catch (Exception e) {
			}
		}
	}
}