package team068.behaviors;

import battlecode.common.*;
import team068.*;

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
		CALCULATE_MEMORY,
		GET_INITIAL_IDS,
		WAIT_FOR_ANTENNA,
		NORMAL_SPAWN,
		WEIRD_SPAWN,
		WEIRD_VACATE,
		WEIRD_REFINERY,
		COMPUTE_BUILDINGS_1,
		COMPUTE_BUILDINGS_2,
		COMPUTE_BUILDINGS_3,
		COMPUTE_BUILDINGS_4,
		BUILD_BUILDINGS,
		SLEEP,
		GIVE_UP
	}
	
	final OldNavigation nav = new OldNavigation(myPlayer);
	
	SCVBuildOrder obj = SCVBuildOrder.CALCULATE_MEMORY;
	
	MapLocation hometown = myPlayer.myRC.getLocation();
	MapLocation unitDock;
	MapLocation[] mineLocs = {null, null, null, null};
	int[] mineIDs = {-1, -1, -1, -1};
	MapLocation armoryLoc;
	MapLocation factoryLoc;
	MapLocation towerLoc;
	MapLocation loc;
	Direction d;
	
	Robot[] nearbyRobots;
	Mine[] nearbyMines;
	Robot r;
	Mine m;
	Mine currMine;
	RobotInfo rInfo;
	
	int dizziness = 0;
	int tiredness = 0;
	int minesCapped = 0;
	int towerType = -1;
	
	boolean hasAntenna = false;
	boolean mineFound;
	
	boolean steppedOff = false;
	boolean triedOtherSide = false;
	
	Robot rFront;
	Robot rLeft;
	Robot rRight;
	int frontRefinery;
	int leftRefinery;
	int rightRefinery;
	
	public SCVBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			case CALCULATE_MEMORY:
				
				///////////////////////////////////////
				//Round number calculations
					long currRound = myPlayer.myMemory.getCurrRound();
					Utility.printMsg(myPlayer, "Round: "+currRound);
					myPlayer.myMemory.setNextRound(currRound+1);
				
				obj = SCVBuildOrder.WAIT_FOR_ANTENNA;
				return;
		
		
			
			case WAIT_FOR_ANTENNA:
				
				Utility.setIndicator(myPlayer, 1, "WAIT_FOR_ANTENNA");
				if ( hasAntenna )
					obj = SCVBuildOrder.NORMAL_SPAWN;
				return;
				
			case NORMAL_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "NORMAL_SPAWN");
				Utility.setIndicator(myPlayer, 2, "");
				
				frontRefinery = 0;
				leftRefinery = 0;
				rightRefinery = 0;
				rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				rLeft = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft()), RobotLevel.ON_GROUND);
				rRight = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight()), RobotLevel.ON_GROUND);
				if ( rFront != null && rFront.getTeam() == myPlayer.myRC.getTeam() )
					frontRefinery = 1;
				if ( rLeft != null && rLeft.getTeam() == myPlayer.myRC.getTeam() )
					leftRefinery = 1;
				if ( rRight != null && rRight.getTeam() == myPlayer.myRC.getTeam() )
					rightRefinery = 1;
				
				// we are facing our 2 starting refineries, the 2 untaken mines are behind them
				if ( !myPlayer.myRC.getDirection().isDiagonal() && frontRefinery + leftRefinery + rightRefinery >= 2 )
				{
					// mine found 2 spaces away
					if ( myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(), 2), RobotLevel.MINE) != null )
					{
						// path found to the left
						if ( rightRefinery == 1 && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()) )
						{
							mineLocs[0] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
							mineIDs[0] = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND).getID();
							mineLocs[1] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight());
							mineIDs[1] = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateRight()), RobotLevel.ON_GROUND).getID();
							while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveForward();
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveForward();
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		    				myPlayer.sleep();
		    				Utility.setIndicator(myPlayer, 2, "Building!");
		    				mineLocs[2] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
		    				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
								myPlayer.sleep();
	        				Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
	        				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
	        				myPlayer.sleep();
	        				mineIDs[2] = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND).getID();
	        				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveBackward();
		    				myPlayer.sleep();
		    				mineLocs[3] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
		    				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
								myPlayer.sleep();
	        				Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
	        				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
	        				myPlayer.sleep();
	        				mineIDs[3] = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND).getID();
	        				obj = SCVBuildOrder.COMPUTE_BUILDINGS_1;
						}
						// path found to the right
						else if ( leftRefinery == 1 && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()) )
						{
							mineLocs[0] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
							mineIDs[0] = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND).getID();
							mineLocs[1] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft());
							mineIDs[1] = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().rotateLeft()), RobotLevel.ON_GROUND).getID();
							while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveForward();
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveForward();
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
		    				myPlayer.sleep();
		    				Utility.setIndicator(myPlayer, 2, "Building!");
		    				mineLocs[2] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
		    				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
								myPlayer.sleep();
	        				Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
	        				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
	        				myPlayer.sleep();
	        				mineIDs[2] = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND).getID();
	        				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		    				while (myPlayer.myMotor.isActive())
		    					myPlayer.sleep();
		    				myPlayer.myMotor.moveBackward();
		    				myPlayer.sleep();
		    				mineLocs[3] = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
		    				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
								myPlayer.sleep();
	        				Utility.buildChassis(myPlayer, myPlayer.myRC.getDirection(), Chassis.BUILDING);
	        				Utility.buildComponent(myPlayer, myPlayer.myRC.getDirection(), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
	        				myPlayer.sleep();
	        				mineIDs[3] = myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND).getID();
		    				obj = SCVBuildOrder.COMPUTE_BUILDINGS_1;
						}
						else if ( !triedOtherSide )
						{
							triedOtherSide = true;
							if ( rightRefinery == 1 )
							{
								while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
			    				while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.moveForward();
			    				while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
							}
							else if ( leftRefinery == 1 )
							{
								while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
			    				while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.moveForward();
			    				while (myPlayer.myMotor.isActive())
			    					myPlayer.sleep();
			    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
							}
						}
						// both sides failed, bug naving
						else
							obj = SCVBuildOrder.WEIRD_SPAWN;
					}
					// no mine found
					else
						obj = SCVBuildOrder.WEIRD_SPAWN;
				}
				// we are not facing the 2 starting refineries -> turn
				else
				{
					dizziness++;
    				while (myPlayer.myMotor.isActive())
    					myPlayer.sleep();
    				if ( myPlayer.myRC.getDirection().isDiagonal() )
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
    				else
    					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    				if ( dizziness >= 8 )
    					obj = SCVBuildOrder.WEIRD_SPAWN;
				}
				return;
				
			case WEIRD_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "WEIRD_SPAWN");
				Utility.setIndicator(myPlayer, 2, "");
				
				mineFound = false;
				nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class); 
				for ( int i = nearbyMines.length ; --i >= 0 ; )
				{
					m = nearbyMines[i];
    				if ( !mineFound && m.getTeam() == Team.NEUTRAL && myPlayer.mySensor.senseObjectAtLocation(m.getLocation(), RobotLevel.ON_GROUND) == null )
    				{
        				mineFound = true;
        				currMine = m;
        				loc = m.getLocation();
    				}
    			}
    			if ( !mineFound && dizziness < 5 )
    			{
    				dizziness++;
    				while (myPlayer.myMotor.isActive())
    					myPlayer.sleep();
    				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
    			}
    			if ( !mineFound && dizziness >= 5 )
    			{
    				dizziness = 0;
    				if ( !steppedOff )
    					obj = SCVBuildOrder.WEIRD_VACATE;
    				else
    					obj = SCVBuildOrder.GIVE_UP;
    			}
    			if ( mineFound )
    			{
    				dizziness = 0;
    				obj = SCVBuildOrder.WEIRD_REFINERY;
    			}
    			return;
				
			case WEIRD_VACATE:
				
				Utility.setIndicator(myPlayer, 1, "WEIRD_VACATE");
				for ( int i = 8 ; --i >= 0 ; )
				{
					d = Direction.values()[i];
					if ( myPlayer.myMotor.canMove(d) )
					{
						while ( myPlayer.myMotor.isActive() )
							myPlayer.sleep();
						myPlayer.myMotor.setDirection(d.opposite());
						myPlayer.sleep();
						myPlayer.myMotor.moveBackward();
						obj = SCVBuildOrder.WEIRD_SPAWN;
						return;
					}
				}
				return;
    			
			case WEIRD_REFINERY:
    			
				Utility.setIndicator(myPlayer, 1, "WEIRD_REFINERY");
				Utility.setIndicator(myPlayer, 2, "Giving up in " + Integer.toString(Constants.MINE_AFFINITY - tiredness) + "...");
    			if ( tiredness < Constants.MINE_AFFINITY && (!myPlayer.mySensor.withinRange(loc) || myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) == null) )
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(loc) > myPlayer.myBuilder.type().range)
        			{
        				Utility.navStep(myPlayer, nav, loc);
        				tiredness++;
        			}
        			else
        			{
        				Utility.setIndicator(myPlayer, 2, "Building!");
        				while ( myPlayer.myRC.getTeamResources() < Chassis.BUILDING.cost + ComponentType.RECYCLER.cost )
							myPlayer.sleep();
        				Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(loc), Chassis.BUILDING);
        				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(loc), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
        				minesCapped++;
        				if (minesCapped == 2)
        				{
        					obj = SCVBuildOrder.COMPUTE_BUILDINGS_1;
        					mineLocs[3] = loc;
        				}
        				else
        				{
        					obj = SCVBuildOrder.WEIRD_SPAWN;
        					mineLocs[2] = loc;
        				}
        				tiredness = 0;
        				return;
        			}
    			}
    			else
    			{
    				obj = SCVBuildOrder.GIVE_UP;
    				tiredness = 0;
    			}
    			return;
    		
			case COMPUTE_BUILDINGS_1:
				
				Utility.setIndicator(myPlayer, 1, "COMPUTE_BUILDINGS_1");
				Utility.setIndicator(myPlayer, 2, "Trying to get factory and refinery next to armory...");
				d = myPlayer.myRC.getLocation().directionTo(mineLocs[3]);
				if ( myPlayer.myMotor.canMove(d.rotateLeft()) && myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft()) )
				{
					armoryLoc = myPlayer.myRC.getLocation().add(d.rotateLeft());
					factoryLoc = myPlayer.myRC.getLocation().add(d.rotateLeft().rotateLeft());
					obj = SCVBuildOrder.BUILD_BUILDINGS;
				}
				else if ( myPlayer.myMotor.canMove(d.rotateRight()) && myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) )
				{
					armoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight());
					factoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight().rotateRight());
					obj = SCVBuildOrder.BUILD_BUILDINGS;
				}
				else if ( !d.isDiagonal() )
				{
					if ( myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft()) && myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft().rotateLeft()) )
					{
						armoryLoc = myPlayer.myRC.getLocation().add(d.rotateLeft().rotateLeft());
						factoryLoc = myPlayer.myRC.getLocation().add(d.rotateLeft().rotateLeft().rotateLeft());
						obj = SCVBuildOrder.BUILD_BUILDINGS;
					}
					else if ( myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft()) && myPlayer.myMotor.canMove(d.opposite()) )
					{
						armoryLoc = myPlayer.myRC.getLocation().add(d.rotateLeft().rotateLeft());
						factoryLoc = myPlayer.myRC.getLocation().add(d.opposite());
						obj = SCVBuildOrder.BUILD_BUILDINGS;
					}
					else if ( myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) && myPlayer.myMotor.canMove(d.rotateRight().rotateRight().rotateRight()) )
					{
						armoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight().rotateRight());
						factoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight().rotateRight().rotateRight());
						obj = SCVBuildOrder.BUILD_BUILDINGS;
					}
					else if ( myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) && myPlayer.myMotor.canMove(d.opposite()) )
					{
						armoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight().rotateRight());
						factoryLoc = myPlayer.myRC.getLocation().add(d.opposite());
						obj = SCVBuildOrder.BUILD_BUILDINGS;
					}
				}
				else
					obj = SCVBuildOrder.COMPUTE_BUILDINGS_2;
				return;
    			
			case COMPUTE_BUILDINGS_2:
				
				Utility.setIndicator(myPlayer, 1, "COMPUTE_BUILDINGS_2");
				Utility.setIndicator(myPlayer, 2, "Trying to get factory next to armory...");
				for ( int i = 8 ; --i >= 0 ; )
				{
					d = Direction.values()[i];
					if ( myPlayer.myMotor.canMove(d) && myPlayer.myMotor.canMove(d.rotateRight()) )
					{
						armoryLoc = myPlayer.myRC.getLocation().add(d);
						factoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight());
						obj = SCVBuildOrder.BUILD_BUILDINGS;
						return;
					}
					else if ( myPlayer.myMotor.canMove(d) && !d.isDiagonal() && myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) )
					{
						armoryLoc = myPlayer.myRC.getLocation().add(d);
						factoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight().rotateRight());
						obj = SCVBuildOrder.BUILD_BUILDINGS;
						return;
					}
				}
				obj = SCVBuildOrder.COMPUTE_BUILDINGS_3;
				return;
    			
			case COMPUTE_BUILDINGS_3:
				
				Utility.setIndicator(myPlayer, 1, "COMPUTE_BUILDINGS_3");
				Utility.setIndicator(myPlayer, 2, "Trying to get factory next to refinery...");
				d = myPlayer.myRC.getLocation().directionTo(mineLocs[3]);
				if ( myPlayer.myMotor.canMove(d.rotateLeft()) )
					factoryLoc = myPlayer.myRC.getLocation().add(d.rotateLeft());
				else if ( myPlayer.myMotor.canMove(d.rotateRight()) )
					factoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight());
				if ( !d.isDiagonal() )
				{
					if ( myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft()) )
						factoryLoc = myPlayer.myRC.getLocation().add(d.rotateLeft().rotateLeft());
					else if ( myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) )
						factoryLoc = myPlayer.myRC.getLocation().add(d.rotateRight().rotateRight());
				}
				if ( factoryLoc == null )
					obj = SCVBuildOrder.COMPUTE_BUILDINGS_4;
				else
				{
					for ( int i = 8 ; --i >= 0 ; )
					{
						d = Direction.values()[i];
						if ( myPlayer.myMotor.canMove(d) && !myPlayer.myRC.getLocation().add(d).equals(factoryLoc) )
						{
							armoryLoc = myPlayer.myRC.getLocation().add(d);
							obj = SCVBuildOrder.BUILD_BUILDINGS;
							return;
						}
					}
					factoryLoc = null;
					obj = SCVBuildOrder.COMPUTE_BUILDINGS_4;
				}
				return;
    			
			case COMPUTE_BUILDINGS_4:
				
				Utility.setIndicator(myPlayer, 1, "COMPUTE_BUILDINGS_4");
				Utility.setIndicator(myPlayer, 2, "This map blows! Going for anything I can get...");
				for ( int i = 8 ; --i >= 0 ; )
				{
					d = Direction.values()[i];
					if ( myPlayer.myMotor.canMove(d) && armoryLoc == null )
					{
						Utility.setIndicator(myPlayer, 2, "Armory location found!");
						armoryLoc = myPlayer.myRC.getLocation().add(d);
					}
					else if ( myPlayer.myMotor.canMove(d) && factoryLoc == null )
					{
						Utility.setIndicator(myPlayer, 2, "Factory location found!");
						factoryLoc = myPlayer.myRC.getLocation().add(d);
						obj = SCVBuildOrder.BUILD_BUILDINGS;
						return;
					}
				}
				myPlayer.sleep();
				Utility.setIndicator(myPlayer, 2, "No room for factory and armory. GG.");
				myPlayer.sleep();
				myPlayer.myRC.suicide();
				return;
				
			case BUILD_BUILDINGS:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_BUILDINGS");
				Utility.setIndicator(myPlayer, 2, "");
				unitDock = myPlayer.myRC.getLocation();
				while ( myPlayer.myRC.getTeamResources() < 2 * Chassis.BUILDING.cost + ComponentType.ARMORY.cost + ComponentType.FACTORY.cost + Utility.totalCost(Constants.heavyLoadout0) - 2*Constants.RESERVE ) // subtract to give time for SCV to broadcast stuff and suicide
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(armoryLoc), Chassis.BUILDING);
				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(armoryLoc), ComponentType.ARMORY, RobotLevel.ON_GROUND);
				Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryLoc), Chassis.BUILDING);
				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(factoryLoc), ComponentType.FACTORY, RobotLevel.ON_GROUND);
				myPlayer.sleep();
				myPlayer.myRC.turnOn(mineLocs[3], RobotLevel.ON_GROUND);
				myPlayer.sleep();
				myPlayer.myMessenger.sendLoc(MsgType.MSG_SEND_DOCK, unitDock);
				myPlayer.sleep();
				myPlayer.myRC.suicide();
				return;
    			
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 1, "SLEEP");
				Utility.setIndicator(myPlayer, 2, "zzzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
			case GIVE_UP:
				
				Utility.setIndicator(myPlayer, 1, "GIVE_UP");
				Utility.setIndicator(myPlayer, 2, "This is crazy!! Going back to my supply depot home.");
				if (myPlayer.myRC.getLocation().distanceSquaredTo(hometown) > 0)
    				Utility.navStep(myPlayer, nav, hometown);
				else
					obj = SCVBuildOrder.COMPUTE_BUILDINGS_1;
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
		for ( ComponentController c : components )
		{
			if ( c.type() == ComponentType.ANTENNA )
				hasAntenna = true;
		}
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{

	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
}
