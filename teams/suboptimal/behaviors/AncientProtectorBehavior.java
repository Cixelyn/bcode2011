package suboptimal.behaviors;


import suboptimal.*;
import battlecode.common.*;;

public class AncientProtectorBehavior extends Behavior {
	
	private enum APActions {
		
		ROOTED,
		THROWING_ROCKS

	}
	APActions obj = APActions.ROOTED;
	MapLocation enemyLoc;
	public AncientProtectorBehavior(RobotPlayer player) {
		super(player);
	}
	
	public void run() throws Exception {
		switch (obj) {
			case ROOTED:
				enemyLoc = Utility.attackEnemies(myPlayer);
				if (enemyLoc!=null) {
					obj = APActions.THROWING_ROCKS;
					return;
				}
				if (!myPlayer.myMotor.isActive())
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
				return;
				
			case THROWING_ROCKS:
				enemyLoc = Utility.attackEnemies(myPlayer);
				if (enemyLoc==null) {
					obj = APActions.ROOTED;
				}
				return;
		}
	}

	@Override
	public void newComponentCallback(ComponentController[] components) {
		
	}

	@Override
	public void newMessageCallback(MsgType type, Message msg) {
		
	}

	@Override
	public void onDamageCallback(double damageTaken) {
		
	}

	@Override
	public void onWakeupCallback(int lastActiveRound) {
		 
	}

	@Override
	public String toString() {
		return "AncientProtector (from WC3)";
	}
}
