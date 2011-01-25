package fibbyBot14.behaviors;


import fibbyBot14.*;
import battlecode.common.*;;

/**
 * 
 * <pre>
 * 
 * 
 *                                                        .                        
 *                                                 ..  .MMNMM .                  
 *                                             . .MMMM       MM .                
 *                                          .  M ..M.   . MN. .N                 
 *                                          MM,,   MM. .M.,,MM MM.               
 *                            .. ...   ...MMMMMMM.  MN ,M   .M  M                
 *                     ..    ,MNM.NMM ...M...N, ..N. M  MM.MM.  .N               
 *                    MMM . .MM, MM.   .. . MM     M. M.         M               
 *                 .M.         MM..    . .    M.   .M.M           M,             
 *              ..MM.     NMM  8M    MM .M . . M   , M.M    .  . .M.             
 *            MM, MM    .MN .MM M    M.  .M    N .   M  M   MM. M.MM             
 *       .MMM .   .M     M   ,M.M . MM . MM    .M     M M   M  . M M             
 *       M        , M   .ND..MM. MM. ,.MM .M   NM    .N. N..MM  MM M             
 *      .M..        M    .MMMM...MM   ..MMMMN .      .M..MM  . .,..M.            
 *      ,M..        .M        ...MMMMMM  .            NM  M:      ,.M            
 *      .M.          M           .M...   MMMMNM,.NM   .M..,M       .M            
 *       M.          .M.     ,.,  MM  MM .MM .M   M    M, .M    MMMM .,          
 *      . M          .MM    M. .MM.MM      .M  M  .M  ..M   M. M  . MM           
 *         M           N   MM   .M. MM   .   MM   .M  . MMMMM. MM ,MMM.          
 *        .M            M.  M   ,M..N MM. MM .M   .M  ..M....M  NMM,  M          
 *        .MMM,        .M. . MMM   .M. M.   M. M . M.   MMNMMM.       M          
 *        M ..M         MM          .M .M.  M  .M. M.  .M   MMM      MM          
 *      .MMM..M          M           M ,M.  MM. M, M. .M... M M.,   M            
 *      .M .  .MM         M   .     ..M  M  M    ..M. .M...M  ,M  M.             
 *      .N..   .MM        MM   MMMMN..M..    .  MM.M .NN . M.MM.,                
 *      .M..     M         M. MM   ....MMMMMMMM .  M.,M . MM                     
 *       M.       MM.      .M M.  . M.,M.....   MM.M .M M .                      
 *      .MM ..    ,MM      .M .MMNMM   MMMMMMMM    . MM.                         
 *      .M  .NMM..  .M.     .M          M.       NMMM                            
 *       M..    . MMMMM      M..        M    . MM.  .M                           
 *       M MMM      .M M     .N    . MM .   MM..     .M                          
 *      .M   .MM     .   MM.  MM. MMM, ..MMM         . M                         
 *       M  ...M  .MMN    .MN  .MM    .MM,       .     MN                        
 *       M M...M .MMM. M.           .M.          M . M ..M.                      
 *      .M..M  M M   MM  MM   .     .M.     M    .MM  .MMMM                      
 *       N  M..M.    ..,M..MM.M .M. .M.      M .MM...M   . M                     
 *       .MM MM.M      . MN..M, M.MM.M.       MM ..N .    .M.,                   
 *       . MM. . MM        MM .M .  .M.    ,M . MM .MM     .MM.                  
 *        MM .     MM       .MM  M. .M. MMM...MM .MM .M. MM   M                  
 *       .M       ... MM     . .M .NMMMM .,MM ..  ..MM.M      .M,                
 *       M.M         .MMM . M . . MM.M. MMMM.     MM.  M      ..M                
 *   . MM   .MM   .. MMM      M     MMM     .MM M       M.  . M..M               
 *    MM       MM .M MM      .M     .M.      . MM .  .. MM  MM ..M               
 *     .MM     . MM.MM.    . N.     .M.       MM.MM. MM, MMM   ..M               
 *         MM.. M, .M       M.MMM . .M.  . MM.M.   MM   .MM   . M.               
 *           MMM. MN.     ,M.    MM..M.. MM    M  .M.   .M...,MN.                
 *          . MM   M      M ,  .M  .MMMM  MM   M  .M.   .M..M                    
 *          .M.. .      ..N.M,.M:   .M.   . M  M  .M. ...MNM                     
 *           .NMM,.M   ...M   M     .M.      M M  .M. ...M.                      
 *            .   .,NM ..MM..MMM.   .M.       MM...M.    M.                      
 *            .     .M  .MM..M. M.. .M.   ,,M. MM..M.. NM                        
 *             M      MMM  ,.M   .M..M.. .M,   M.MMM MM.                         
 *             MM  ,. M .  ..M   . NNMMMM     MM.....                            
 *                    .. MMMM.MM   ..M.   . M,                                   
 *                            . MM  .M.  MM..                                    
 *                               .NM.M. MM..                                     
 *                                 .MMN                                          
 *                                                              
 * 
 * 
 * </pre>
 * 
 * @author Justin
 *
 */


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
				
				//Utility.setIndicator(myPlayer, 1, "EQUIPPING");
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
				
				//Utility.setIndicator(myPlayer, 1, "DEFENSE");
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
