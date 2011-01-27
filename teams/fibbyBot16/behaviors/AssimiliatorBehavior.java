package fibbyBot16.behaviors;

import battlecode.common.*;
import fibbyBot16.*;
import java.util.*;


/**
 * 
 * 
 * <pre>
 * 
 * 
 * 
 *                                                                               
 *                                                                               
 *                                                ....                           
 *                                               .MNMM .                         
 *                 ..                          ..       MM,                      
 *                 MMMM                        .M          N.                    
 *             ..M.  .                         M. M.        M.                   
 *            .MM      M                       M.  .MN  ...MM.                   
 *           MM      .NM.   ..M,    MM....MMM..M.M.    .M . M.                   
 *          M      , N ..   MM.MMMMM .MNNM  .MMM. .M      . M.                   
 *          MM    MM.  M.M..M .M   M..M  .M .N.. M..NMMM   MM.                   
 *        .M..MMM    .M .  .M .,.  M,  .MMMMMMM..M..N.     .M.,                  
 *        .N.       ,M ,     N  M..MNMMMM.. . ...M.M.,M. MM  M.                  
 *       .DM.MM ..MM.NM...M   M.MM          MM.   M.  .... M  M                  
 *     ..M M.      MM      ...MM  . ...       MM    MM  MMMM  M,.                
 *    .M   M.     M M        M  MM.....  NM.   .M    MMM.,M..M.M                 
 *    MN.  N.    .M M   N.  . .M. .  .     .M..  M    M..M  ..NMMM               
 *   .M    MMM...MMMM   M M  M MM.   .MM     M.  N. . MM...MMM                   
 *   .M    M.......M    MM,M MM .       MM    M   MM .. .M..M.    N              
 *   .M..  M.      M    M M M  MM.. .MM.  .    .  M  MM.. M     ,MM..            
 *    ..MMMN.      M.DMMMM..M.M.       M, M    M.M.M..  .M     MM..M             
 *      .. M.     MM ... .MMM           M .    MM M     MM  .MM. ..M.            
 *        .MMM,   M    .N.MM            M  D..M  MMM   .. .MM,   .M.N.           
 *        .M .,M M.  . M,MM             .M M M..MN .M. M.       M. .N.           
 *       MM   ..MMM.  M.M .             .M M.N M.   .M  M     MM.  .M.           
 *       M  ..M..  .MM M.M.             M .MN,.M.    M  M.DMM  M .M.M.           
 *       MMNM.     .M .MM              M.  .M.MMM  ...M M.  . M..  MM.           
 *                 M...MM            . M  MM..M. MMMMMM.M. ..MN.   .M            
 *                 ,.M..M,          ,M    M .M.    ...M.N..,..M.     M.          
 *                   M   M         MM.   M.  M.       M M   .M        M          
 *                 .M    MM...MMM.      M  ...        M.M.   M         M         
 *                 .N      ,..       , N.  MM..       M M.  .N         M         
 *                 .M                M MMMM.,.NMMM,   MMMM  .N.         M        
 *                   M..      .. MMM               MM.    .N.N........  M.       
 *                    MN... MMMM .                 M..    ,N.NMMM......MM        
 *                    . ... .                      M.   MM,.   . ........        
 *                                                 ..MM.                         
 *                                                                               
 *                                                                               
 *                                               
 * 
 * 
 * </pre>
 * 
 * 
 * @author FiBsTeR
 *
 */


public class AssimiliatorBehavior extends Behavior
{
	
	MapLocation pylonLoc;
	
	int pylonsEquipped;
	
	private enum AssimilatorBuildOrder 
	{
		FIND_PYLON,
		EQUIP_PYLON,
		SLEEP,
		SUICIDE
	}
	
	AssimilatorBuildOrder obj = AssimilatorBuildOrder.FIND_PYLON;
	
	public AssimiliatorBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{

    		case FIND_PYLON:
				
				Utility.setIndicator(myPlayer, 0, "FIND_PYLON");
				Utility.setIndicator(myPlayer, 1, "Looking for pylon...");
				
				if ( Clock.getRoundNum() > 4000 )
					obj = AssimilatorBuildOrder.SLEEP;
				
				for ( int i = Direction.values().length; --i >= 0 ; )
				{
					Direction d = Direction.values()[i];
					
					Robot r = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myLoc.add(d), RobotLevel.ON_GROUND);
					if ( r != null && r.getTeam() == myPlayer.myRC.getTeam() )
					{
						RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(r);
						if ( rInfo.chassis == Chassis.BUILDING && Utility.totalWeight(rInfo.components) == 0 )
						{
							Utility.setIndicator(myPlayer, 1, "Pylon found.");
							pylonLoc = myPlayer.myLoc.add(d);
							obj = AssimilatorBuildOrder.EQUIP_PYLON;
							pylonsEquipped++;
							return;
						}
					}
				}
				
				if ( pylonsEquipped == 5 || Clock.getRoundNum() % 250 == 0 )
					obj = AssimilatorBuildOrder.SUICIDE;
				
				return;
			
			case EQUIP_PYLON:
				
				Utility.setIndicator(myPlayer, 0, "EQUIP_PYLON");
				Utility.setIndicator(myPlayer, 1, "Equipping pylon.");
				for ( int i = 10 ; --i >= 0 ; )
					Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(pylonLoc), ComponentType.HAMMER, RobotLevel.ON_GROUND);
				obj = AssimilatorBuildOrder.FIND_PYLON;
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

	public int timingu()
	{
		// this is where the magic happens, baby
		// depending on what round it is, the spawned units reach us slower
		// turn on the towers timingu rounds after the night cycle started
		switch ( (Clock.getRoundNum() / 500) )
		{
			case 0:
				return 30;
			case 1:
				return 30;
			case 2:
				return 30;
			case 3:
				return 130;
			case 4:
				return 130;
			case 5:
				return 70;
			default:
				return 0;
		}
	}
	
	public String toString()
	{
		return "AssimilatorBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	

}
