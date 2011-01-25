package fibbyBot14.behaviors;


import fibbyBot14.*;
import battlecode.common.*;;


/**
 * 
 * 
 * <pre>
 * 
 *                                                                                 
 *     Since WC3 is essentially a dead game, there exists no community from which
 *     a wire-frame picture of an "Ancient Protector" (whatever that is) can be used
 *     for ASCII art. We have therefore taken the liberty of replacing said "tower"
 *     with art of the awesomer and more WELL KNOWN photon cannon.
 *     
 *     
 *     How can WC3 be a dead game with a new patch that was just released, a vibrant
 *     Chinese pro scene, and Battle.net where it is still insanely easy to find games?
 *     Contrast this to ICCUP, where there is probably about 6 games going on at any
 *     given time, half of which you will be cheesed on, and the other half you will
 *     get owned by a Korean smurf.
 *     
 *     
 *     
 *     Why would you bring up the "Chinese pro scene" and compare it to ICCUP, while completely
 *     ignoring the Korean pro scene, which makes the "WC3 ""pro"" scene" (yes nested quotes on
 *     """pro""") look like a bunch of ppl in the middle of nowhere that just happen to be playing
 *     some old game at the same time. And ICCUP is not the only server to find games.
 *     The original BNET servers are still active (probably more so than WC3), not to mention
 *     other ladders like brain clan, etc.. 
 *     
 *                                                                       
 *                                                                               
 *                                          ..M  .                               
 *                       . MM              ,M ,. MM,,                            
 *                     MN.....MM.M.        MM      . MM                          
 *                 .MM        .M    .......    MM.  .M.                          
 *                M   .M     .MMM   M.MMMM .      .M. MM.          ..            
 *                 M   .M MM . .MM   M... M    .MM. MM .. M..      ..            
 *                   M.  N   ...MNN MM   .NMMM.  .MMN .     MM   M,  M.          
 *                .M..N..M..MNM . .  M , .N .M.MM.   ,MM   MM     M.  .M.        
 *        .MMM    M    .MMM..    MMM. ..  . M..MM      . ..M.M     MMM .,N       
 *      M  M     M ..M.     , M,   .NMNMMM..M  .   MM .  ..M. .MM.     MM.N      
 *     M  M       MM     MMM    ..M M.  .M.   MM.     M.   M             M       
 *    .,.MNMM.   MMM  M M.   .MM.   M.   . M   ..MN     M   .M         MM.       
 *    MM      MMM M .M   MNMM     . M.     M.MM. . MM    N..  .M. .  MM.         
 *   .M         M M M    .M      .M M      M.MM  N...M   .M.  .M.  .MN  ..       
 *   ..MM       M. M     ..      M.  M    M.  .      M.  .    N..  ...MMM        
 *        .M  ..MMMM.     .                    M     M   .M...M.     M     .     
 *           MM.  .MM.   .M.     MN .        MM    .,M   .M.. N. MMMM     .M     
 *        .M .       M     M.       MDMMMNM..       M.  .M. ..M     .M     M     
 *       NM M..  ..MM...     M ..                .M   . M     .M      M   .M     
 *    .,M   .M .MM    MM      ,MM.       ..    .MM . MMM        M  .....M.M:     
 *    .M      M..    .M..M     NMM..MMM.... MMM   MN ,  .M.    .MNM.....MM.      
 *    .M     M..      N.  .MM.M. .   MN  .      ..M      .M. .M.. . ... ..       
 *       M  M.        M.    .M        MM...... MMM. M .MMM MM.                   
 *       .MM  DMNMMM ..MM.. MMMM.   .N  M.....  .M   M..    M.                   
 *        . .          .  MM  . ..MMM...M       .M   M       M                   
 *                         N        M  .M, .  . M,    M,      .                  
 *                       .M        M  ..M ......  N.         .M                  
 *                       .MM       M..M..            M M .MMM..                  
 *                           M   ..MM                                            
 *                              ...                                              
 *                                                                           
 * 
 * </pre>
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
				
				//Utility.setIndicator(myPlayer, 1, "EQUIPPING");
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
				
				//Utility.setIndicator(myPlayer, 1, "DEFENSE");
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
