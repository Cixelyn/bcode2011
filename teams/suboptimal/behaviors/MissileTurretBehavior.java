package suboptimal.behaviors;


import suboptimal.*;
import battlecode.common.*;;

public class MissileTurretBehavior extends Behavior
{
	
	private enum MissileTurretBuildOrder
	{
		
		EQUIPPING,
		DEFENSE

	}
	
	MissileTurretBuildOrder obj = MissileTurretBuildOrder.EQUIPPING;
	
	RobotInfo enemyInfo;
	
	int towerType = -1;
	
	boolean hasSensor;
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
				hasSensor = false;
				hasRailgun = false;
				for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.RADAR )
					{
						hasSensor = true;
						towerType = 1;
					}
					if ( c.type() == ComponentType.SATELLITE )
					{
						hasSensor = true;
						towerType = 2;
					}
					if ( c.type() == ComponentType.RAILGUN )
						hasRailgun = true;
				}
				if ( hasSensor && hasRailgun )
				{
					Utility.setIndicator(myPlayer, 2, "I am tower type " + Integer.toString(towerType) + "!");
					obj = MissileTurretBuildOrder.DEFENSE;
				}
				return;
			
			case DEFENSE:
				
				Utility.setIndicator(myPlayer, 1, "DEFENSE");
				enemyInfo = Utility.attackEnemies(myPlayer);
				if ( enemyInfo == null )
				{
					Utility.setIndicator(myPlayer, 2, "No enemies nearby.");
					if ( !myPlayer.myMotor.isActive() && towerType == 1 )
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if ( myPlayer.myRC.getLocation().distanceSquaredTo(enemyInfo.location) > ComponentType.SMG.range )
				{
					Utility.setIndicator(myPlayer, 2, "Enemy detected.");
					if ( !myPlayer.myMotor.isActive() )
						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyInfo.location));
				}
				else
				{
					Utility.setIndicator(myPlayer, 2, "Enemy in range!");
					if ( !myPlayer.myMotor.isActive() )
						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyInfo.location));
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
