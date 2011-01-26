package maxbot3.behaviors;

import battlecode.common.*;
import maxbot3.*;

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
	
	MapLocation spawnLoc;
	MapLocation armory1Loc;
	MapLocation tower1Loc;
	MapLocation armory2Loc;
	MapLocation tower2Loc;
	MapLocation factoryLoc;
	
	int wakeTime = 0;
	
	private enum SCVBuildOrder 
	{
		INITIALIZE,
		CAP_MINES,
		GO_TO_TOWER_1,
		BUILD_ARMORY_1,
		BUILD_TOWER_1,
		GO_TO_TOWER_2,
		BUILD_ARMORY_2,
		BUILD_TOWER_2,
		GO_TO_FACTORY,
		BUILD_FACTORY,
		SLEEP,
		SUICIDE
	}
	
	SCVBuildOrder obj = SCVBuildOrder.INITIALIZE;
	
	public SCVBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case INITIALIZE:
				
				Utility.setIndicator(myPlayer, 0, "INITIALIZE");
				Utility.setIndicator(myPlayer, 1, "");
				spawnLoc = myPlayer.myLoc;
				armory1Loc = spawnLoc.add(-2,-2);
				tower1Loc = spawnLoc.add(-3,-2);
				armory2Loc = spawnLoc.add(0,2);
				tower2Loc = spawnLoc.add(1,2);
				factoryLoc = spawnLoc.add(-2,-1);
				obj = SCVBuildOrder.CAP_MINES;
				return;
				
			case CAP_MINES:
				
				Utility.setIndicator(myPlayer, 0, "CAP_MINES");
				Utility.setIndicator(myPlayer, 1, "");
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.moveForward();
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.SOUTH_WEST);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.moveForward();
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.SOUTH);
				
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.moveBackward();
				
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				obj = SCVBuildOrder.SLEEP;
				return;
				
			case GO_TO_TOWER_1:
				
				Utility.setIndicator(myPlayer, 0, "GO_TO_TOWER_1");
				Utility.setIndicator(myPlayer, 1, "");
				obj = SCVBuildOrder.BUILD_ARMORY_1;
				return;
				
			case BUILD_ARMORY_1:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_ARMORY_1");
				Utility.setIndicator(myPlayer, 1, "");
				while ( myPlayer.myRC.getTeamResources() < 4*Chassis.BUILDING.cost + 2*ComponentType.ARMORY.cost + 8*ComponentType.BEAM.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, myPlayer.myLoc.directionTo(armory1Loc), Chassis.BUILDING);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(armory1Loc), ComponentType.ARMORY, RobotLevel.ON_GROUND);
				obj = SCVBuildOrder.BUILD_TOWER_1;
				return;
				
			case BUILD_TOWER_1:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_1");
				Utility.setIndicator(myPlayer, 1, "");
				Utility.buildChassis(myPlayer, myPlayer.myLoc.directionTo(tower1Loc), Chassis.BUILDING);
				obj = SCVBuildOrder.GO_TO_TOWER_2;
				return;
				
			case GO_TO_TOWER_2:
				
				Utility.setIndicator(myPlayer, 0, "GO_TO_TOWER_2");
				Utility.setIndicator(myPlayer, 1, "");
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.EAST);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.moveForward();
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.SOUTH_EAST);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.moveForward();
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.SOUTH);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.moveForward();
				
				obj = SCVBuildOrder.BUILD_ARMORY_2;
				return;
				
			case BUILD_ARMORY_2:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_ARMORY_2");
				Utility.setIndicator(myPlayer, 1, "");
				Utility.buildChassis(myPlayer, myPlayer.myLoc.directionTo(armory2Loc), Chassis.BUILDING);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(armory2Loc), ComponentType.ARMORY, RobotLevel.ON_GROUND);
				obj = SCVBuildOrder.BUILD_TOWER_2;
				return;
				
			case BUILD_TOWER_2:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_2");
				Utility.setIndicator(myPlayer, 1, "");
				Utility.buildChassis(myPlayer, myPlayer.myLoc.directionTo(tower2Loc), Chassis.BUILDING);
				obj = SCVBuildOrder.GO_TO_FACTORY;
				return;
				
			case GO_TO_FACTORY:
				
				Utility.setIndicator(myPlayer, 0, "GO_TO_FACTORY");
				Utility.setIndicator(myPlayer, 1, "");
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.NORTH);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.moveForward();
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
				
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.moveForward();
				
				myPlayer.sleep();
				myPlayer.myRC.turnOn(myPlayer.myLoc.add(Direction.SOUTH_WEST), RobotLevel.ON_GROUND);
				obj = SCVBuildOrder.BUILD_FACTORY;
				return;
				
			case BUILD_FACTORY:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_FACTORY");
				Utility.setIndicator(myPlayer, 1, "");
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.FACTORY.cost + Chassis.FLYING.cost + ComponentType.BEAM.cost + ComponentType.SHIELD.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, myPlayer.myLoc.directionTo(factoryLoc), Chassis.BUILDING);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(factoryLoc), ComponentType.FACTORY, RobotLevel.ON_GROUND);
				myPlayer.myRC.turnOn(armory1Loc, RobotLevel.ON_GROUND);
				obj = SCVBuildOrder.SUICIDE;
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
		wakeTime++;
		switch ( wakeTime )
		{
			case 1:
				obj = SCVBuildOrder.GO_TO_TOWER_1;
				return;
		}
	}

}
