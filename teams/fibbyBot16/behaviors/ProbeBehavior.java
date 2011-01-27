package fibbyBot16.behaviors;

import battlecode.common.*;
import fibbyBot16.*;

/**
 * 
 * <pre>
 * 
 * 
 * 
 *                                                                               
 *                                                                               
 *                       .                                                       
 *                    MM, MM                  ...                                
 *                    M . ..M.               .MMM.                               
 *              .     M      MM .            .    M..                            
 *         ..MMM..    M .     .M            N     .M                             
 *         MM.  MM    MM.       M  .,      M      .M.                            
 *        M       M .  . MN      M. .M,    MM      M.                            
 *      M.MM.     . MMD .. M.     M. .MM  N   M,.  M                             
 *     ..M..MM        ..M  .MM     M. . .M.    .MM.M.                            
 *       M.   MM.        M  .MM  ..M.M,  M         M.                            
 *        M    .NM.      .M  M.MMNM  .M..,M      .M                              
 *        ,M      .M..  .M.    M. DM. ,.MM.M.....M ....                          
 *          M        M M .      .M..M    .MMMMMMMMMMMMMMM,                       
 *            MM..                MM MMMM .        ...   MM..                    
 *            M  ..MMMM           ..MMM .          ..MM  ..NM                    
 *            MMMM .       NM.     MM .   .  MMMMMMN ..MM    M                   
 *            M..  M.         .MM M  MM  .MM         MM..M    M.                 
 *            M..   M            M.  MM  M.           .MM,     M                 
 *            .M.,,.MMM.         M.  M   NM           M.M      M.                
 *               MM.. . MMN .    M.  .M  M.MMM     .MM..M      M.                
 *                .NM ,M. . .MMN.MM   M  .M   MMMMMM. .M      .MM.               
 *                    MM..       MM  M      MM   ...MM.      .M MM ....,.        
 *                       MM..    M.  M           ..  .MMM   MM .MM.,MM. M,       
 *                         .MM .M.    MM.  ...           .M   .M..N .MM  .M      
 *                           . MMMMM   ..MM...        MMM.    M  .M ..M   ,M     
 *                           .M.    M.     ... . .M.....      .MM. M..N..MM,.    
 *                          M .    .M      ..MMMMMMMM. MM          .M.M..        
 *                        .M.      .M.        ..... .,M  M            M          
 *                       .M .      .M     M..M    .M. M,  M.         MM          
 *                       M.        N MN   M. M       M.N...M.       ,M           
 *                     ,M.       M,    MMM .M., MMMM.  ,M.  M.    ,MM,           
 *                    .M .      N       . M. M  . ..MMM. M   M.  ,M.             
 *                    M,.     .M          .M          M. M.   M M M              
 *                   M.        .MM        M            MMM  ..M   M              
 *                 . N           .M.     M             M  MMM     .M             
 *                ,MM             .MN.   M       .     M.  MM..     ..           
 *                M                  MM.MM, MMM..M.    M      MM   .M            
 *               MM        . .       ,M .,  .M    M    M        M.   M           
 *              M .       M. M      M..      .M   M   .M.      . M.   M          
 *            .M.       MM    .M.  M.         M. M    .M          M . N          
 *           .M        M.    .MMMM.          .MMM.    .M.         ,MMM           
 *           M.      M      .M.....           M       .M.                        
 *          M.     .M,     MM.             . .M .     .M.                        
 *          M.    .M      M.               ...N .     M..                        
 *         .M  .MM     ,MN                  ..M,     M                           
 *           M.M.     .M.                     ..MMMMM,                           
 *             MM    MM                       ........                           
 *                 ..                                       
 * 
 *   
 * </pre>
 * 
 * @author FiBsTeR
 *
 */

public class ProbeBehavior extends Behavior
{
	
	private enum ProbeBuildOrder 
	{
		INITIALIZE,
		CAP_MINES,
		GO_TO_CAMPSITE,
		BUILD_CAMP,
		SLEEP,
		SUICIDE
	}
	
	ProbeBuildOrder obj = ProbeBuildOrder.INITIALIZE;
	
	public ProbeBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case INITIALIZE:
				
				Utility.setIndicator(myPlayer, 0, "INITIALIZE");
				while ( myPlayer.myBuilder.isActive() )
					myPlayer.sleep();
				obj = ProbeBuildOrder.CAP_MINES;
				return;
				
			case CAP_MINES:
				
				Utility.setIndicator(myPlayer, 0, "CAP_MINES");
				
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.EAST, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.EAST, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				turn(Direction.SOUTH_EAST);
				backward();
				myPlayer.sleep();
				
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.SOUTH_EAST, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH_EAST, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				
				obj = ProbeBuildOrder.GO_TO_CAMPSITE;
				return;
				
			case GO_TO_CAMPSITE:
				
				Utility.setIndicator(myPlayer, 0, "GO_TO_CAMPSITE");	
				turn(Direction.NORTH_WEST);
				obj = ProbeBuildOrder.BUILD_CAMP;
				return;
				
			case BUILD_CAMP:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_CAMP");
				
				Utility.buildChassis(myPlayer, Direction.NORTH_WEST, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.NORTH_WEST, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				Utility.buildChassis(myPlayer, Direction.NORTH, Chassis.BUILDING);
				
				turn(Direction.SOUTH_WEST);
				forward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.NORTH, Chassis.BUILDING);
				Utility.buildChassis(myPlayer, Direction.NORTH_EAST, Chassis.BUILDING);
				
				turn(Direction.NORTH_WEST);
				forward();
				forward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.SOUTH_EAST, Chassis.BUILDING);
				Utility.buildChassis(myPlayer, Direction.EAST, Chassis.BUILDING);
				
				turn(Direction.NORTH_EAST);
				forward();
				forward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.SOUTH_WEST, Chassis.BUILDING);
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildChassis(myPlayer, Direction.SOUTH_EAST, Chassis.BUILDING);
				
				obj = ProbeBuildOrder.SUICIDE;
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
	
	public void turn(Direction d) throws Exception
	{
		if ( myPlayer.myRC.getDirection() != d )
		{
			while ( myPlayer.myMotor.isActive() )
				myPlayer.sleep();
			myPlayer.myMotor.setDirection(d);
		}
	}
	
	public void forward() throws Exception
	{
		while ( myPlayer.myMotor.isActive() )
			myPlayer.sleep();
		myPlayer.myMotor.moveForward();
	}
	
	public void backward() throws Exception
	{
		while ( myPlayer.myMotor.isActive() )
			myPlayer.sleep();
		myPlayer.myMotor.moveBackward();
	}
	
	public String toString()
	{
		return "ProbeBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{

	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}

}
