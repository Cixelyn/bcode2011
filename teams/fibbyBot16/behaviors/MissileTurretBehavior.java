package fibbyBot16.behaviors;


import fibbyBot16.*;
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
 * @author FiBsTeR
 *
 */


public class MissileTurretBehavior extends Behavior
{
	
	int locNum;
	MapLocation rockLoc;
	MapLocation target1;
	MapLocation target2;
	
	
	private enum MissileTurretBuildOrder
	{
		EQUIPPING,
		INITIALIZE,
		DETERMINE_CHOKEPOINTS,
		FIRE,
		SLEEP,
		SUICIDE
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
				if ( numBeams >= 5 )
					obj = MissileTurretBuildOrder.INITIALIZE;
					
				return;
			
			case INITIALIZE:
				
				Utility.setIndicator(myPlayer, 0, "INITIALIZE");
				if ( myPlayer.mainLoc != null )
				{
					locNum = getLocNum(myPlayer.myLoc, myPlayer.mainLoc);
					Utility.setIndicator(myPlayer, 2, "I am tower " + Integer.toString(locNum) + ".");
					setTargets();
					while ( myPlayer.myBeams[4].isActive() )
						myPlayer.sleep();
					obj = MissileTurretBuildOrder.FIRE;
				}
				
				return;
				
			case FIRE:
				
				Utility.setIndicator(myPlayer, 0, "FIRE");
				Utility.setIndicator(myPlayer, 1, "Attacking ground.");
				
				// see if it's shield/plating time
				if ( Clock.getRoundNum() > Constants.CAMP_TIME )
				{
					obj = MissileTurretBuildOrder.SUICIDE;
					return;
				}
				
				// make sure we can turn
				if ( myPlayer.myMotor.isActive() )
					return;
				
				if ( (Clock.getRoundNum() / 250) % 2 == 0 && (Clock.getRoundNum() / 500) < Constants.ALL_IN )
				{
					// its day time
					if ( (locNum == 2 || locNum == 22) && (Clock.getRoundNum() / 250) == 2 )
					{
						// its the second day cycle, kill rocks
						switch ( (Clock.getRoundNum() / 20) % 25 )
						{
							case 0:
								rockLoc = myPlayer.mainLoc.add(0,-3);break;
							case 1:
								rockLoc = myPlayer.mainLoc.add(1,-3);break;
							case 2:
								rockLoc = myPlayer.mainLoc.add(2,-3);break;
							case 3:
								rockLoc = myPlayer.mainLoc.add(4,-1);break;
							case 4:
								rockLoc = myPlayer.mainLoc.add(4,0);break;
							case 5:
								rockLoc = myPlayer.mainLoc.add(4,1);break;
							case 6:
								rockLoc = myPlayer.mainLoc.add(2,3);break;
							case 7:
								rockLoc = myPlayer.mainLoc.add(1,3);break;
							case 8:
								rockLoc = myPlayer.mainLoc.add(0,3);break;
							case 9:
								rockLoc = myPlayer.mainLoc.add(-2,1);break;
							case 10:
								rockLoc = myPlayer.mainLoc.add(-2,0);break;
							case 11:
								rockLoc = myPlayer.mainLoc.add(-2,-1);break;
							default:
								obj = MissileTurretBuildOrder.SLEEP;
								return;
						}
						if ( !myPlayer.myBeams[3].isActive() && myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(rockLoc) )
						{
							myPlayer.myBeams[0].attackSquare(rockLoc, RobotLevel.ON_GROUND);
							myPlayer.myBeams[1].attackSquare(rockLoc, RobotLevel.ON_GROUND);
							myPlayer.myBeams[2].attackSquare(rockLoc, RobotLevel.ON_GROUND);
							myPlayer.myBeams[3].attackSquare(rockLoc, RobotLevel.ON_GROUND);
							myPlayer.myBeams[4].attackSquare(rockLoc, RobotLevel.ON_GROUND);
						}
						else if ( myPlayer.myRC.getDirection() != myPlayer.myLoc.directionTo(rockLoc) )
							myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(rockLoc));
					}
					else
						obj = MissileTurretBuildOrder.SLEEP;
				}
				else if ( (Clock.getRoundNum() / 250) < 2*Constants.GREEDY_CYCLES + 1 )
				{
					// its a greedy night cycle, hold chokepoints
					if ( locNum == 2 )
					{
						if ( Clock.getRoundNum() % 2 == 0 )
						{
							if ( myPlayer.myRC.getDirection() == Direction.WEST )
							{
								myPlayer.myBeams[0].attackSquare(myPlayer.myLoc.add(-5, 2), RobotLevel.ON_GROUND);
								myPlayer.myBeams[1].attackSquare(myPlayer.myLoc.add(-5, 2), RobotLevel.ON_GROUND);
							}
							myPlayer.myMotor.setDirection(Direction.NORTH);
						}
						else if ( Clock.getRoundNum() % 2 == 1 )
						{
							if ( myPlayer.myRC.getDirection() == Direction.NORTH )
							{
								myPlayer.myBeams[2].attackSquare(myPlayer.myLoc.add(2, -5), RobotLevel.ON_GROUND);
								myPlayer.myBeams[3].attackSquare(myPlayer.myLoc.add(2, -5), RobotLevel.ON_GROUND);
								myPlayer.myBeams[4].attackSquare(myPlayer.myLoc.add(2, -5), RobotLevel.ON_GROUND);
							}
							myPlayer.myMotor.setDirection(Direction.WEST);
						}
					}
					else if ( locNum == 22 )
					{
						if ( Clock.getRoundNum() % 2 == 0 )
						{
							if ( myPlayer.myRC.getDirection() == Direction.EAST )
							{
								myPlayer.myBeams[0].attackSquare(myPlayer.myLoc.add(5, -2), RobotLevel.ON_GROUND);
								myPlayer.myBeams[1].attackSquare(myPlayer.myLoc.add(5, -2), RobotLevel.ON_GROUND);
								myPlayer.myBeams[4].attackSquare(myPlayer.myLoc.add(5, -2), RobotLevel.ON_GROUND);
							}
							myPlayer.myMotor.setDirection(Direction.SOUTH);
						}
						else if ( Clock.getRoundNum() % 2 == 1 )
						{
							if ( myPlayer.myRC.getDirection() == Direction.SOUTH )
							{
								myPlayer.myBeams[2].attackSquare(myPlayer.myLoc.add(-2, 5), RobotLevel.ON_GROUND);
								myPlayer.myBeams[3].attackSquare(myPlayer.myLoc.add(-2, 5), RobotLevel.ON_GROUND);
							}
							myPlayer.myMotor.setDirection(Direction.EAST);
						}
					}
					else
						obj = MissileTurretBuildOrder.SLEEP;
				}
				// it's a normal night cycle, protect your base!
				else if ( Clock.getRoundNum() % 2 == 0 )
				{
					if ( myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(target1) )
					{
						myPlayer.myBeams[0].attackSquare(target1, RobotLevel.ON_GROUND);
						myPlayer.myBeams[1].attackSquare(target1, RobotLevel.ON_GROUND);
					}
					myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(target2));
				}
				else if ( Clock.getRoundNum() % 2 == 1 )
				{
					if ( myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(target2) )
					{
						myPlayer.myBeams[2].attackSquare(target2, RobotLevel.ON_GROUND);
						myPlayer.myBeams[3].attackSquare(target2, RobotLevel.ON_GROUND);
						myPlayer.myBeams[4].attackSquare(target2, RobotLevel.ON_GROUND);
					}
					myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(target1));
				}
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

	public int getLocNum(MapLocation myLoc, MapLocation mainLoc)
	{
		// black magics
		int dx = myLoc.x - mainLoc.x;
		int dy = myLoc.y - mainLoc.y;
		switch ( (dx + 2) + 6*(dy + 2) + 1 )
		{
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			case 4:
				return 4;
			case 5:
				return 5;
			case 6:
				return 6;
			case 7:
				return 7;
			case 9:
				return 8;
			case 10:
				return 9;
			case 12:
				return 10;
			case 13:
				return 11;
			case 14:
				return 12;
			case 17:
				return 13;
			case 18:
				return 14;
			case 19:
				return 15;
			case 20:
				return 16;
			case 23:
				return 17;
			case 24:
				return 18;
			case 25:
				return 19;
			case 27:
				return 20;
			case 28:
				return 21;
			case 30:
				return 22;
			case 31:
				return 23;
			case 32:
				return 24;
			case 33:
				return 25;
			case 34:
				return 26;
			case 35:
				return 27;
			case 36:
				return 28;
		}
		return -1;
	}
	
	public void setTargets()
	{
		// depending on what tower you are, set your target squares
		if ( locNum == -1 )
			return;
		switch ( locNum )
		{
			case 1:
				target1 = myPlayer.myLoc.add(0,-2);
				target2 = myPlayer.myLoc.add(1,-2);
				return;
				
			case 2:
				target1 = myPlayer.myLoc.add(-2,0);
				target2 = myPlayer.myLoc.add(-2,-1);
				return;
				
			case 3:
				target1 = myPlayer.myLoc.add(0,-1);
				target2 = myPlayer.myLoc.add(0,-2);
				return;
				
			case 4:
				target1 = myPlayer.myLoc.add(0,-1);
				target2 = myPlayer.myLoc.add(0,-2);
				return;
				
			case 5:
				target1 = myPlayer.myLoc.add(2,-1);
				target2 = myPlayer.myLoc.add(2,0);
				return;
				
			case 6:
				target1 = myPlayer.myLoc.add(-1,-2);
				target2 = myPlayer.myLoc.add(0,-2);
				return;
				
			case 7:
				target1 = myPlayer.myLoc.add(-2,-1);
				target2 = myPlayer.myLoc.add(-2,0);
				return;
				
			case 8:
				target1 = myPlayer.myLoc.add(-2,-2);
				target2 = myPlayer.myLoc.add(-1,-2);
				return;
				
			case 9:
				target1 = myPlayer.myLoc.add(1,-2);
				target2 = myPlayer.myLoc.add(2,-2);
				return;
				
			case 10:
				target1 = myPlayer.myLoc.add(2,-1);
				target2 = myPlayer.myLoc.add(2,0);
				return;
				
			case 11:
				target1 = myPlayer.myLoc.add(-2,0);
				target2 = myPlayer.myLoc.add(-1,0);
				return;
				
			case 12:
				target1 = myPlayer.myLoc.add(-2,-1);
				target2 = myPlayer.myLoc.add(-3,-3);
				return;
				
			case 13:
				target1 = myPlayer.myLoc.add(2,-1);
				target2 = myPlayer.myLoc.add(3,-3);
				return;
				
			case 14:
				target1 = myPlayer.myLoc.add(1,0);
				target2 = myPlayer.myLoc.add(2,0);
				return;
				
			case 15:
				target1 = myPlayer.myLoc.add(-2,0);
				target2 = myPlayer.myLoc.add(-1,0);
				return;
				
			case 16:
				target1 = myPlayer.myLoc.add(-2,1);
				target2 = myPlayer.myLoc.add(-3,3);
				return;
				
			case 17:
				target1 = myPlayer.myLoc.add(2,1);
				target2 = myPlayer.myLoc.add(3,3);
				return;
				
			case 18:
				target1 = myPlayer.myLoc.add(1,0);
				target2 = myPlayer.myLoc.add(2,0);
				return;
				
			case 19:
				target1 = myPlayer.myLoc.add(-2,0);
				target2 = myPlayer.myLoc.add(-2,1);
				return;
				
			case 20:
				target1 = myPlayer.myLoc.add(-2,2);
				target2 = myPlayer.myLoc.add(-1,2);
				return;
				
			case 21:
				target1 = myPlayer.myLoc.add(1,2);
				target2 = myPlayer.myLoc.add(2,2);
				return;
				
			case 22:
				target1 = myPlayer.myLoc.add(2,0);
				target2 = myPlayer.myLoc.add(2,1);
				return;
				
			case 23:
				target1 = myPlayer.myLoc.add(0,2);
				target2 = myPlayer.myLoc.add(1,2);
				return;
				
			case 24:
				target1 = myPlayer.myLoc.add(-2,0);
				target2 = myPlayer.myLoc.add(-2,1);
				return;
				
			case 25:
				target1 = myPlayer.myLoc.add(0,1);
				target2 = myPlayer.myLoc.add(0,2);
				return;
				
			case 26:
				target1 = myPlayer.myLoc.add(0,1);
				target2 = myPlayer.myLoc.add(0,2);
				return;
				
			case 27:
				target1 = myPlayer.myLoc.add(2,0);
				target2 = myPlayer.myLoc.add(2,1);
				return;
				
			case 28:
				target1 = myPlayer.myLoc.add(-1,2);
				target2 = myPlayer.myLoc.add(0,2);
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
		if ( locNum == 2 || locNum == 22 || ((Clock.getRoundNum()/250) % 2 == 1 && (Clock.getRoundNum()/250) >= 2*Constants.GREEDY_CYCLES + 1) )
			obj = MissileTurretBuildOrder.FIRE;
	}

	@Override
	public String toString()
	{
		return "MissileTurretBehavior";
	}
}
