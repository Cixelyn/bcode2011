package suboptimalone.behaviors;


import suboptimalone.*;
import battlecode.common.*;;

public class MissileTurretBehavior extends Behavior
{
	
	private enum MissileTurretBuildOrder
	{
		
		EQUIPPING,
		DEFENSE

	}
	
	MissileTurretBuildOrder obj = MissileTurretBuildOrder.EQUIPPING;
	
	MapLocation enemyLoc;
	
	boolean hasSatellite;
	boolean hasRailgun;
	
	public MissileTurretBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				Utility.setIndicator(myPlayer, 1, "EQUIPPING");
				hasSatellite = false;
				hasRailgun = false;
				for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.SATELLITE )
						hasSatellite = true;
					if ( c.type() == ComponentType.RAILGUN )
						hasRailgun = true;
				}
				if ( hasSatellite && hasRailgun )
					obj = MissileTurretBuildOrder.DEFENSE;
				return;
			
			case DEFENSE:
				
				Utility.setIndicator(myPlayer, 1, "DEFENSE");
				//enemyLoc = Utility.attackEnemies(myPlayer);
				if ( enemyLoc == null )
				{
					Utility.setIndicator(myPlayer, 2, "No enemies nearby.");
					if (!myPlayer.myMotor.isActive())
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
				}
				else if ( myPlayer.myRC.getLocation().distanceSquaredTo(enemyLoc) > ComponentType.BLASTER.range )
				{
					Utility.setIndicator(myPlayer, 2, "Enemy detected.");
					if (!myPlayer.myMotor.isActive())
						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyLoc));
				}
				else
				{
					Utility.setIndicator(myPlayer, 2, "Enemy in range!");
					if (!myPlayer.myMotor.isActive())
						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyLoc));
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
		return "AncientProtector (from WC3)"; // m1$$1l3 7u|z|z37$ 4 LYF3
	}
}
