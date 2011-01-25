package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;

/**
 * 
 * <pre>
 * 
 *                                     
 *                              . .MM,  MMMM                                      
 *                            .MM . MMMMMM..M.                                    
 *                 MMMMM    .M..M ..    M   M                                    
 *             MM.MMMMMM MM. M. .      M,   M                                    
 *           MM M..     M..M     .NMM     .MM                                    
 *           M M.      .M   MM           MM,M                                    
 *           .  M.  .MM        MM.N  MMM    M                                    
 *          ..   .  ..      M   . ..... .M..M                                    
 *          ..             M             M  M                                    
 *          .M  .  .  , MM. .    .       N ..   . MM.                            
 *          .M .MMMMMM.      .MMM .  MM. .NM  .MM   ,M .                         
 *           M           MMMMMM.  .   .MMM  MM.        M                         
 *          ..M    MM  ..   ,.,  .MM      .M,      MM.  NM .                     
 *            M M,M.        M      .M       N:       ..,  MM                     
 *            M .M. MM.      M       MN.   ..MM .    ...      M..                
 *          ,.. M..M  M.   .MM..       N.   .M.M        . .MM .MM.               
 *        .MMMMMMMM    M   .  M.       M.    MNM.M, ..MMM        M .             
 *       MM.       M   M   M  ,M       .M   M. M .MNM            ,MM.            
 *      .M,        ,M.  M  M   M.       M   N  ,  .MM             MM.            
 *      .N.         M.  M M    M.    MMNM  M.  .. ..MM          M .M.            
 *      .M. .MM     .MMMMMM.  M    N.   MM,....M..  M.M.     MM                  
 *      .M. ,MM.     M   ..MM .    M   .MM..MMM. .M.M .MM MM   .MM..MM.          
 *      .N,   .     . M,    .M.      M M.        M .M.  .M M,.M  M.M   M         
 *      .MM           .M   M. .MM    ,ND  M     MM .M    M  .M,.  NM.  .M.       
 *      .MNNMMMMMMM....MM..... .  MM     ..    M .MMM.   M   MM.    MM   N       
 *      .M.N          .. M     . M N    M    MN.    .M.  M   M.MMMMMM M..M       
 *      .M.M       ,.....N     .M  M   .M    .M.     .MN.M   MM M M              
 *      .M M,.   MMM.   .M.  ..M   MMMMM     M          .M,  M.N  ..MM           
 *      .M .MM    .      .M.  M    N.M       M           ..  ...M    .M          
 *      .N  ,.          .MM. ..M   M. M       MM                  MMMM.          
 *        M MM ...  MMMM..MMMMM. M.N   M .MMMM   M ,                             
 *         M         .,  MMMN. M.M  N M. D .     .M,.                            
 *         .M   .M   MMM.  M MM.MM. M.   ,M.        M                            
 *         .MMMMM   M.  MMM.    . M .      M.       ..M                          
 *               M   M.M M .     ..N.      ,M         .M.                        
 *          .M  .MM.   M M .       N.  M. .  M   .  MM.M..                       
 *            M...NM. ,  ...       M.   MM.  . .MM. . .N..                       
 *             MMMM .MMMMMM.      .MM.       MM,. MMM..M.                        
 *              .M.     .M.,...MMMM  D.  .DM MM. .   MM.MM                       
 *               .M     MMM .  .   M  M .. MM     .M    . N                      
 *                ..   M.  M   MM. ...M  MM...MMMMMM.    .M                      
 *                  M., . MM  .. M  M   MN..M     ..NMM.  .M.                    
 *                   M   M M.   N     M.  M,        M .M.  ,                     
 *                    .. MM     M. M.   ...M.            M  N                    
 *                      .M.     M  M.   N  .MM      .M . .  M                    
 *                      .M.     .  ,M   M   ..M.      MM .M..                    
 *                        M.     MM  M. M        MM  .M.MM                       
 *                         M.,. .M ,MMM.             .                           
 *                          .MMMN .                                              
 * 
 * </pre>
 * 
 * @author Justin
 *
 */

public class SCVBehavior extends Behavior
{
	
	private enum SCVBuildOrder 
	{
		SHUT_OFF,
		GO_TO_TOWER_1,
		BUILD_FACTORY_1,
		BUILD_TOWER_1,
		GO_TO_TOWER_2,
		BUILD_FACTORY_2,
		BUILD_TOWER_2
	}
	
	SCVBuildOrder obj = SCVBuildOrder.SHUT_OFF;
	
	public SCVBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			
				
		}
    	
	}
	
	
	
	public String toString()
	{
		return "SCVBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{

	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}

}
