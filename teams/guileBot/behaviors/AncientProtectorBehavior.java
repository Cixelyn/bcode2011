package guileBot.behaviors;


import guileBot.*;
import battlecode.common.*;;


/**
 * 
 * 
 * 
 * WC3 is the best, how's thats for ASCII art?
 * 
 *                                           .                                    
 *
 */



public class AncientProtectorBehavior extends Behavior
{
	
	private enum APActions
	{
		
		EQUIPPING,
		DEFENSE

	}
	
	APActions obj = APActions.EQUIPPING;
	
	RobotInfo enemyInfo;
	
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
					obj = APActions.DEFENSE;
				return;
			
			case DEFENSE:
				
				Utility.setIndicator(myPlayer, 1, "DEFENSE");
				enemyInfo = Utility.attackEnemies(myPlayer);
				if ( enemyInfo == null || myPlayer.myRC.getLocation().distanceSquaredTo(enemyInfo.location) > ComponentType.BLASTER.range )
				{
					Utility.setIndicator(myPlayer, 2, "No enemies nearby.");
					if (!myPlayer.myMotor.isActive())
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else
				{
					Utility.setIndicator(myPlayer, 2, "Enemy detected!");
					if (!myPlayer.myMotor.isActive())
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
