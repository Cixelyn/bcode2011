package fibbyBot16.behaviors;


import fibbyBot16.*;
import battlecode.common.*;;

/**
 * 
 * <pre>
 * 
 * 
 *
 *                                                                               
 *                                   . ..                                        
 *                                    N M                                        
 *                                   M. ,M.                                      
 *                                  NM.  ,.                                      
 *                                  MM.   N                                      
 *                                 M .M.   M.                                    
 *                               .M  .N.   .M                                    
 *                               M.   M     M                                    
 *                              M.. .DM      N                                   
 *                              M   M        MM                                  
 *                    . M M..  M   M.  .     M.M .                               
 *            .MM        . M. M   M    M    .M M....M.      MMN..                
 *            M.  M   ..  .M,MM  . MM..M     M.,M . .       M ,.M,               
 *           M    M   ..   N.M   M. ...M    M.  ..M...      M   .M               
 *          .M   .MMMMM.   M.M        .N    M    MM. M.MMM...   .M               
 *          .M,  .M ...MMMMMM.M M     .M    M     MMMNM, , M    .M .             
 *         ,NM . .MMM       M  MM     .N    M    .M.  .,.MM      MM,             
 *     .M,MM.MMM  .        M.  .M.     M   .M     .         MN. .M.M.            
 *     M. MMM.             M     M,    MM  .M      M         . MMM.M.            
 *    .N  MM.          MMMMM     MM.   M MMMMM    .MMM          .MM.M            
 *    .M M         MMM M .M      N.M   M . M  MM    MM....MM      M              
 *    .NM       MMM  . M .M    .M   M    M    . MM,.MMMMM..,MM..  .MN            
 *    .MM.    MM. MMM... .M    M.   ..    .       MMM.  . MMM,MM    .N           
 *    MM M. ,M.   M.  .M  M.  M      . M.           M      .M  .M    M           
 *   ..  M .MM  . M     MM M .M.      MM,           M.     .M   .MM  .M          
 *   M   . ...M. .N      M .M       N M.M         .MMM      M.  .. M. ,          
 *  .MM  . MMM.MMM.     .M  .       NM   ..       MM.M       MMMMM  .M.          
 *     M.MM..M           M   M      M   .M      .MN .M           .M .M           
 *      .M...M ...      M .   M.    M    M.      M   M        M.   M  M          
 *     M.N. .MM.. M     .      M   M.    .M     M          MM  M   M  M          
 *  .M.     M.  . M     .M.    M.  N       M  .M    MMM   M     M M.. .M.        
 *  ..M. D  M.  . M.      M .   M  M     ..MM M.   M  M.  M.    .. MM  M.        
 *    .M.M, .M.   M.      MMM.M ,M,M.     M  N.   .M. M   .MM    M  ,..M         
 *     .M   .M    MMM.   .M. .M.  MM.   MM  .N    .M..M   .NM    M    . .        
 *      M,  M..  .M..,MM ... .M. ,MM   M.    M    .M. N MM .M     M M.M          
 *      ... M.   .MMM.  .M... MMM  M .M.   .M.    .M   M. M.M    .M.M            
 *      .M,      .M   ,MMM...MMM .M MM      M ..,MMM  .M.    M    M M            
 *        MM..   .M      M...MM.  MM MN. ..NNMM .   M. M.    M   M .M            
 *         .M.   .M     .M...M . MM.. ,M  MM,       M.. M    M   M M.            
 *         .M.   .MMN.   M. .M    .. ..M N..MM.     M   MM..M.  .M               
 *          .M.  .M . MMMM. .M  . M..MMMM . MM..  .M. .M  ..M.   .               
 *           M.   M.MMMMMM. .MMMMM ,M.MM .MM.NMMMMMM..MMMMM,  . M                
 *            M,  M      M...M   MM. M  M  . .,    M ,M.  .M   N,                
 *            NM.M.     .M...M    M .    M.  M    . MM .  ..MMN                  
 *                .       M.M .   .M     M. M       .                            
 *                         ..      .N     MM                                     
 *                                  M    .M                                      
 *                                   D    M                                      
 *                                   .M .M                                       
 *                                                                                             
 * 
 * 
 * </pre>
 * 
 * @author FiBsTeR
 *
 */


public class PylonBehavior extends Behavior
{
	
	
	private enum PylonBuildOrder
	{
		EQUIPPING,
		SUP,
		SLEEP,
		SUICIDE
	}
	
	PylonBuildOrder obj = PylonBuildOrder.EQUIPPING;

	
	public PylonBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				Utility.setIndicator(myPlayer, 0, "EQUIPPING");
				
				int numHammers = 0;
				for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
				{
					if ( myPlayer.myRC.components()[i].type() == ComponentType.HAMMER )
						numHammers++;
				}
				if ( numHammers >= 1 )
					obj = PylonBuildOrder.SUP;
					
				return;
			
			case SUP:
				
				Utility.setIndicator(myPlayer, 0, "SUP");
				Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
				MapLocation enemyLoc = null;
				for ( Robot r : nearbyRobots )
				{
					if ( r.getTeam() == myPlayer.myRC.getTeam().opponent() )
						enemyLoc = myPlayer.mySensor.senseLocationOf(r);
				}
				if ( enemyLoc == null )
					enemyLoc = myPlayer.myLoc.add(Direction.NORTH, 2);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(enemyLoc));
				
				for ( WeaponController c : myPlayer.myHammers )
				{
					if ( !c.isActive() )
						c.attackSquare(enemyLoc, RobotLevel.ON_GROUND);
				}
				if ( Clock.getRoundNum() % 250 == 0 )
					obj = PylonBuildOrder.SUICIDE;
				return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 0, "SLEEP");
				Utility.setIndicator(myPlayer, 1, "zzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
			case SUICIDE:
				
				Utility.setIndicator(myPlayer, 0, "SUICIDE");
				Utility.setIndicator(myPlayer, 1, ":(");
				myPlayer.sleep();
				myPlayer.myRC.suicide();
				return;
				
		}
	}
	
	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}

	@Override
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}

	@Override
	public String toString()
	{
		return "PylonBehavior";
	}
}
