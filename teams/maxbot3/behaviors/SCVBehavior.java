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

		BUILD_ARMORY_A,
		BUILD_TOWER_2,
		GO_TO_17,
		BUILD_ARMORY_D,
		BUILD_TOWER_22,
		GO_TO_9,
		BUILD_TOWER_8,
		BUILD_TOWER_3,
		BUILD_TOWER_4,
		GO_TO_B,
		BUILD_TOWER_9,
		BUILD_TOWER_5,
		BUILD_TOWER_6,
		BUILD_TOWER_10,
		BUILD_TOWER_13,
		GO_TO_14,
		BUILD_ARMORY_B,
		GO_RIGHT_14,
		BUILD_TOWER_14,
		GO_TO_C,
		BUILD_TOWER_19,
		BUILD_TOWER_23,
		BUILD_TOWER_24,
		BUILD_TOWER_16,
		GO_TO_20,
		BUILD_ARMORY_C,
		BUILD_TOWER_21,
		GO_BELOW_25,
		BUILD_TOWER_25,
		BUILD_TOWER_26,
		GO_TO_11,
		BUILD_TOWER_15,
		BUILD_TOWER_12,
		GO_TO_7,
		BUILD_TOWER_11,
		GO_TO_1,
		BUILD_TOWER_7,
		GO_ABOVE_1,
		BUILD_TOWER_1,
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
				obj = SCVBuildOrder.CAP_MINES;
				return;
				
			case CAP_MINES:
				
				Utility.setIndicator(myPlayer, 0, "CAP_MINES");
				
				turn(Direction.NORTH_WEST);
				forward();
				turn(Direction.SOUTH_WEST);
				forward();
				turn(Direction.SOUTH);
				
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				backward();
				
				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				obj = SCVBuildOrder.BUILD_ARMORY_A;
				return;
				
			case BUILD_ARMORY_A:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_ARMORY_A");
				
				while ( myPlayer.myRC.getTeamResources() < 4*Chassis.BUILDING.cost + 2*ComponentType.ARMORY.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, Direction.WEST, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.WEST, ComponentType.ARMORY, RobotLevel.ON_GROUND);
				obj = SCVBuildOrder.BUILD_TOWER_2;
				return;

			case BUILD_TOWER_2:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_2");
				
				Utility.buildChassis(myPlayer, Direction.NORTH_WEST, Chassis.BUILDING);
				obj = SCVBuildOrder.GO_TO_17;
				return;
				
			case GO_TO_17:
				
				Utility.setIndicator(myPlayer, 0, "GO_TO_17");
				
				turn(Direction.EAST);
				forward();
				turn(Direction.SOUTH_EAST);
				forward();
				turn(Direction.SOUTH);
				forward();
				
				obj = SCVBuildOrder.BUILD_ARMORY_D;
				return;
				
			case BUILD_ARMORY_D:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_ARMORY_D");
				
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.ARMORY, RobotLevel.ON_GROUND);
				obj = SCVBuildOrder.BUILD_TOWER_22;
				return;
				
			case BUILD_TOWER_22:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_22");
				
				Utility.buildChassis(myPlayer, Direction.SOUTH_EAST, Chassis.BUILDING);
				obj = SCVBuildOrder.GO_TO_9;
				return;
				
			case GO_TO_9:
				
				Utility.setIndicator(myPlayer, 0, "GO_TO_9");
				
				turn(Direction.NORTH);
				forward();
				turn(Direction.NORTH_WEST);
				forward();
				obj = obj = SCVBuildOrder.BUILD_FACTORY;
				
			case BUILD_TOWER_8:
				
				obj = SCVBuildOrder.BUILD_FACTORY;
				return;
				
			case BUILD_TOWER_3:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_3");
				
				Utility.buildChassis(myPlayer, Direction.NORTH_WEST, Chassis.BUILDING);
				obj = SCVBuildOrder.BUILD_TOWER_4;
				return;
				
			case BUILD_TOWER_4:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_4");
				
				Utility.buildChassis(myPlayer, Direction.NORTH, Chassis.BUILDING);
				obj = SCVBuildOrder.GO_TO_B;
				return;
				
			case GO_TO_B:
				
				obj = SCVBuildOrder.BUILD_FACTORY;
				return;
				
			case BUILD_TOWER_9:
			
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_9");
				
				Utility.buildChassis(myPlayer, Direction.WEST, Chassis.BUILDING);
				obj = SCVBuildOrder.BUILD_TOWER_5;
				return;
				
			case BUILD_TOWER_5:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_5");
				
				Utility.buildChassis(myPlayer, Direction.NORTH, Chassis.BUILDING);
				obj = SCVBuildOrder.BUILD_TOWER_6;
				return;
				
			case BUILD_TOWER_6:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_6");
				
				Utility.buildChassis(myPlayer, Direction.NORTH_EAST, Chassis.BUILDING);
				obj = SCVBuildOrder.BUILD_TOWER_10;
				return;
				
			case BUILD_TOWER_10:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_10");
				
				Utility.buildChassis(myPlayer, Direction.EAST, Chassis.BUILDING);
				obj = SCVBuildOrder.BUILD_TOWER_13;
				return;
				
			case BUILD_TOWER_13:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_TOWER_13");
				
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				obj = SCVBuildOrder.GO_TO_14;
				return;
				
			case GO_TO_14:
				
				Utility.setIndicator(myPlayer, 0, "GO_TO_14");
				
				turn(Direction.NORTH_WEST);
				backward();
				obj = SCVBuildOrder.BUILD_ARMORY_B;
				return;
				
			case BUILD_ARMORY_B:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_ARMORY_B");
				
				Utility.buildChassis(myPlayer, Direction.NORTH_WEST, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.NORTH_WEST, ComponentType.ARMORY, RobotLevel.ON_GROUND);
				obj = SCVBuildOrder.BUILD_FACTORY;
				return;
				
				/*
				
				GO_TO_14,
				BUILD_ARMORY_B,
				GO_RIGHT_14,
				BUILD_TOWER_14,
				GO_TO_C,
				BUILD_TOWER_19,
				BUILD_TOWER_23,
				BUILD_TOWER_24,
				BUILD_TOWER_16,
				GO_TO_20,
				BUILD_ARMORY_C,
				BUILD_TOWER_21,
				GO_BELOW_25,
				BUILD_TOWER_25,
				BUILD_TOWER_26,
				GO_TO_11,
				BUILD_TOWER_15,
				BUILD_TOWER_12,
				GO_TO_7,
				BUILD_TOWER_11,
				GO_TO_1,
				BUILD_TOWER_7,
				GO_ABOVE_1,
				BUILD_TOWER_1,*/
				
			case BUILD_FACTORY:
				while (myPlayer.myMotor.isActive()) {
					myPlayer.sleep();
				}
				myPlayer.myMotor.setDirection(Direction.WEST);
				Utility.buildChassis(myPlayer, Direction.WEST, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.WEST, ComponentType.FACTORY, RobotLevel.ON_GROUND);
				myPlayer.myRC.suicide();
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
		while ( myPlayer.myMotor.isActive() )
			myPlayer.sleep();
		myPlayer.myMotor.setDirection(d);
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
				obj = SCVBuildOrder.BUILD_FACTORY;
				break;
		}
	}

}

