package costax3;
import battlecode.common.*;

public class MarineBattleStrategy extends BattleStrategy {
	
	private Scanner myScanner;

	public MarineBattleStrategy(RobotPlayer player) {
		super(player);
		this.myScanner=new Scanner(player);
	}

	@Override
	public void runBehaviors() {
		boolean foundEnemy=false;
		Message[] messages = player.myRC.getAllMessages();
		myScanner.detectRobots();
		if (messages!=null) {
			for (Message message: messages) {
				if (message.ints.length==10) {
					for (int i=0;i<10;i++) {
						for (int j=0;j<10;j++) {
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
