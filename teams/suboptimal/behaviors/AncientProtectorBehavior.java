package suboptimal.behaviors;


import suboptimal.*;
import battlecode.common.*;;

public class AncientProtectorBehavior extends Behavior
{
	
	private enum APActions
	{
		
		EQUIPPING,
		ROOTED,
		THROWING_ROCKS

	}
	
	APActions obj = APActions.ROOTED;
	
	MapLocation enemyLoc;
	
	int numBlasters;
	int numShields;
	boolean hasRadar;
	
	public AncientProtectorBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				Utility.setIndicator(myPlayer, 1, "EQUIPPING");
				numBlasters = 0;
				numShields = 0;
				hasRadar = false;
				for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.BLASTER )
						numBlasters++;
					if ( c.type() == ComponentType.SHIELD )
						numShields++;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
				}
				if ( numBlasters >= Constants.BLASTERS_PER_TOWER && numShields >= Constants.SHIELDS_PER_TOWER && hasRadar )
					obj = APActions.ROOTED;
				return;
			
			case ROOTED:
				
				Utility.setIndicator(myPlayer, 1, "ROOTED");
				enemyLoc = Utility.attackEnemies(myPlayer);
				if (enemyLoc!=null) {
					obj = APActions.THROWING_ROCKS;
					return;
				}
				if (!myPlayer.myMotor.isActive())
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
				return;
				
			case THROWING_ROCKS:
				
				Utility.setIndicator(myPlayer, 1, "THROWING_ROCKS");
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
		return "AncientProtector (from WC3)"; // m1$$1l3 7u|z|z37$ 4 LYF3
	}
}
