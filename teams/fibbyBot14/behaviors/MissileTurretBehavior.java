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
	
	MapLocation choke1;
	MapLocation choke2;
	
	
	private enum MissileTurretBuildOrder
	{
		EQUIPPING,
		DETERMINE_CHOKEPOINTS,
		FIRE
	}
	
	MissileTurretBuildOrder obj = MissileTurretBuildOrder.EQUIPPING;

	
	public MissileTurretBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				Utility.setIndicator(myPlayer, 0, "EQUIPPING");
				
				int numBeams = 0;
				for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
				{
					if ( myPlayer.myRC.components()[i].type() == ComponentType.BEAM )
						numBeams++;
				}
				if ( numBeams >= 4 )
					obj = MissileTurretBuildOrder.DETERMINE_CHOKEPOINTS;
					
				return;
			
			case DETERMINE_CHOKEPOINTS:
				
				Utility.setIndicator(myPlayer, 0, "DETERMINE_CHOKEPOINTS");
				Utility.setIndicator(myPlayer, 1, "Looking for armory...");
				
				for ( int i = Direction.values().length; --i >= 0 ; )
				{
					Direction d = Direction.values()[i];
					Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(d), RobotLevel.ON_GROUND);
					if ( r != null )
					{
						RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(r);
						if ( rInfo.chassis == Chassis.BUILDING )
						{
							Utility.setIndicator(myPlayer, 1, "Armory found.");
							if ( d == Direction.WEST )
							{
								choke1 = myPlayer.myLoc.add(5,-2);
								choke2 = myPlayer.myLoc.add(-2,5);
								if ( Constants.ATTACK_TIME % 2 == 0 )
									myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(choke1));
								else
									myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(choke2));
								obj = MissileTurretBuildOrder.FIRE;
								return;
							}
							else if ( d == Direction.EAST )
							{
								Utility.setIndicator(myPlayer, 1, "Armory found.");
								choke1 = myPlayer.myLoc.add(-5,2);
								choke2 = myPlayer.myLoc.add(2,-5);
								if ( Constants.ATTACK_TIME % 2 == 0 )
									myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(choke1));
								else
									myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(choke2));
								obj = MissileTurretBuildOrder.FIRE;
								return;
							}
						}
					}
				}
				
				return;
				
			case FIRE:
				
				Utility.setIndicator(myPlayer, 0, "FIRE");
				Utility.setIndicator(myPlayer, 1, "Attacking ground.");
				
				if ( Clock.getRoundNum() < Constants.ATTACK_TIME )
					return;
				else if ( Clock.getRoundNum() % 2 == 0 )
				{
					myPlayer.myBeams[0].attackSquare(choke1, RobotLevel.ON_GROUND);
					myPlayer.myBeams[1].attackSquare(choke1, RobotLevel.ON_GROUND);
					myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(choke2));
				}
				else if ( Clock.getRoundNum() % 2 == 1 )
				{
					myPlayer.myBeams[2].attackSquare(choke2, RobotLevel.ON_GROUND);
					myPlayer.myBeams[3].attackSquare(choke2, RobotLevel.ON_GROUND);
					myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(choke1));
				}
				return;
				
		}
	}

	@Override
	public void newComponentCallback(ComponentController[] components) {
		
	}

	@Override
	public void onWakeupCallback(int lastActiveRound) {
		 
	}

	@Override
	public String toString()
	{
		return "MissileTurretBehavior";
	}
}
