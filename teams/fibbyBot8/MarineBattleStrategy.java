package fibbyBot8;
import java.util.Arrays;


import battlecode.common.*;

public class MarineBattleStrategy extends BattleStrategy {
	
	private Scanner myScanner;
	private int index;
	

	public MarineBattleStrategy(RobotPlayer player) {
		super(player);
		this.myScanner=new Scanner(player);
	}

	@Override
	public void runBehaviors() {
		boolean foundEnemy=false;
		Message[] messages = player.myRC.getAllMessages();
		myScanner.detectRobots();
		try {
			Utility.senseEnemies(player);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (messages!=null) {
			for (Message message: messages) { //see all incoming broadcasts
				if (message.ints.length==Constants.ENEMIES_COUNT+2) { //we have an EnemyFound message (will probably change later with header?)
					int[] messageCopy=message.ints.clone();
					Arrays.sort(messageCopy,2,message.ints.length); //sort our priorities :the lowest number has the highest priority
					for (int i=0;i<Constants.ENEMIES_COUNT;i++) {
						index= findPriorityIndex(messageCopy[Messenger.firstData+i], message.ints);
						 for (WeaponController weapon : player.myWeapons) {
							 if (!weapon.isActive() && weapon.withinRange(message.locations[index])) {
								 try {
									if (Encoder.decodeRobotChassis(messageCopy[Messenger.firstData+i]).equals(Chassis.FLYING)) {
										weapon.attackSquare(message.locations[i], RobotLevel.IN_AIR);
									}
									else {
										weapon.attackSquare(message.locations[i], RobotLevel.IN_AIR);
									}
								} catch (GameActionException e) {
									e.printStackTrace();
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
	}
	
	/*
	 * Finds the index of our next priority target.
	 */
	private int findPriorityIndex(int priority, int[] ints) {
		for (int j=0;j<Constants.ENEMIES_COUNT;j++) {
			if (ints[j]==priority) {
				return j;
			}
		}
		return 0;
	}
}
