package beambot.behaviors; import battlecode.common.*; import beambot.*;
import java.util.*;



/**
 * <pre>
 * 
 *                                           .                                     
 *                                       . M..M,                                 
 *                                          M.  M                                
 *                                           MM   M.                             
 *                                             M. ,.M                            
 *     .MNM                                     N M. M.                          
 *     M . M.                                   .N MM  M                         
 *    .M.  .MM                                   .N .MMMM                        
 *    .M     MM                                  .MM.DM..M.                      
 *     M      M                                    NM  M, M                      
 *     MM   .  M .                                  M   M. M.                    
 *     .M.  N  MM.                                   M. ..M M                    
 *     .M. M    MM                                   MM .M  M.M                  
 *     .M.M     .M.                                   MM M.   M.                 
 *     .M M       M                                    M. .  MDM                 
 *       M        MMM                                 .M  M. M...M               
 *       M.   ..M.. M.                                 M .,  M.   M              
 *      .MM   MN    .MM                     ..         .N  MMM    M,             
 *        M. .M       MM                 . MNMN MMM..  .M..M M   .M              
 *        MM M.        MM                M     M    .  .MMMM. M.   M             
 *         MM.          .M.              N.      M  M   M.  M..M   M             
 *         M.     .MMM    M...NMM.       N.   MM  MMN .M  .NM. .MNMM             
 *          M.    M  .MM..MMM   . MM     M    M .M..N.M .M.N M     M.MMM         
 *          MM  .M      .MM M.     .N    .M.   M M   .M .M  M. ....MM .M         
 *          .M.  M.       .M M.. . M.M  MMMMM.     MMM M M..MMM.   M .M.         
 *           M.   M,       ,M. M.  ...MM.MM. MM  .M  .M.M.M .M.N   M MM          
 *           ,M    M.        M .M. .. .N . . M.MM .M  M ...MMM  MM M.M           
 *             M   .M..       M  M M,M.M M M .M .M. M M  M M.M..  M.M            
 *            ..M  M..MMMM,  MM   .M.. MM M .  N:..  MM  .M  M..  MMM            
 *              .MM        ,..    .NN  M N.M MMM.MM,       M M.    .N            
 *                M              ,M, NMMM.M.       .M.     .MN..   MM            
 *                 M.           MN.   M..NM.        .MMM M. MM.. MM.M            
 *       .MMMMM    .M          M       M   M. ,M..DMMNM.  M MMMMM   .            
 *      .M   M ,MMMMMM        M         M  M..M.M..   .M . M .M.,M M.            
 *      .MM .M     MM MM    ,M.           .M.M. MM  M M.M.MM  ...MM              
 *          .MMMM   M. .M .DMMM          M M  M     MM .MM.N. MM                 
 *             .  MMMM  ..M M...M      .MMM. .MMMMM.MMMM.M.M N..                 
 *                    MMM  M  M.M    MM  .M.M M      M .M  M M                   
 *                       .MM.M.     M    .MM M.       M  .M.M.                   
 *                         ..MMMMM..M  MM. .          MMMMMM.                    
 *                             M  M.N,.MM..                                      
 *                              MN.  .MM. MM                                     
 *                                MM .. M.MM                                     
 *                                                                               
 *                                                                               
 * 
 * </pre>
 * The arbiter is a unit that runs around the map and attempts to destroy mines during the lategame.
 * @author Cory
 * @author FiBsTeR (JVen)
 *
 *
 * @see <a href="http://www.youtube.com/watch?v=muitsly5t6M">Arbiterssss</a>
 *
 */
public class ArbiterBehavior extends Behavior{
	
	
	private ArbiterBuildOrder state;
	private int[] arbiterLoadout;
	private MapStoreBoolean badMines = new MapStoreBoolean();
	
	MapLocation refineryLoc;
	MapLocation armoryLoc;
	MapLocation factoryLoc;
	Direction d;
	
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int westEdge = -1;
	int spawn = -1;
	int rally = -1;
	int num = -1;
	int numStuck = 0;
	boolean hasRebuilt = false;
	
	Mine minMine;
	Mine m;
	int minMineDist;

	ArrayDeque<MapLocation> prevLocs = new ArrayDeque<MapLocation>();
	
	public ArbiterBehavior(RobotPlayer player)
	{
		super(player);
		
		state = ArbiterBuildOrder.EQUIPPING;									//set our current state
		arbiterLoadout = Utility.countComponents(Constants.arbiterLoadout);		//precompute our unit loadout
	}

	
	private enum ArbiterBuildOrder
	{
		EQUIPPING,
		DETERMINE_SPAWN,
		EXPAND,
		COMPUTE_BUILDINGS_1,
		COMPUTE_BUILDINGS_2,
		COMPUTE_BUILDINGS_3,
		COMPUTE_BUILDINGS_4,
		BUILD_BUILDINGS,
		WAIT_FOR_ACK
	}
	
	

	public void run() throws Exception {
		
		switch(state)
		{
		
		
			case EQUIPPING:
				
				// System.out ???? -JVen
				//System.out.println(arbiterLoadout);
				//System.out.println(Utility.countComponents(myPlayer.myRC.components()));
				Utility.setIndicator(myPlayer, 1, "EQUIPPING ARBITER");
				if( Utility.compareComponents(myPlayer, arbiterLoadout) ) {
					state = ArbiterBuildOrder.DETERMINE_SPAWN;
				}
				return;
			
			case DETERMINE_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "DETERMINE_SPAWN");
				
				if ( myPlayer.mySensor.canSenseSquare(myPlayer.myLoc.add(Direction.NORTH, 10)) )
				{
					if ( myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.NORTH, 10)) == TerrainTile.OFF_MAP )
						northEdge = 1;
					else
						northEdge = 0;
				}
				if ( myPlayer.mySensor.canSenseSquare(myPlayer.myLoc.add(Direction.EAST, 10)) )
				{
					if ( myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.EAST, 10)) == TerrainTile.OFF_MAP )
						eastEdge = 1;
					else
						eastEdge = 0;
				}
				if ( myPlayer.mySensor.canSenseSquare(myPlayer.myLoc.add(Direction.SOUTH, 10)) )
				{
					if ( myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.SOUTH, 10)) == TerrainTile.OFF_MAP )
						southEdge = 1;
					else
						southEdge = 0;
				}
				if ( myPlayer.mySensor.canSenseSquare(myPlayer.myLoc.add(Direction.WEST, 10)) )
				{
					if ( myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.WEST, 10)) == TerrainTile.OFF_MAP )
						westEdge = 1;
					else
						westEdge = 0;
				}
				spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
				if ( spawn != -1 )
				{
					if ( spawn % 2 == 1 )
					{
						if ( num % 2 == 0 )
							rally = (spawn + 1) % 8;
						else
							rally = (spawn + 7) % 8;
					}
					else
					{
						if ( num % 2 == 0 )
							rally = (spawn + 2) % 8;
						else
							rally = (spawn + 6) % 8;
					}
					Utility.setIndicator(myPlayer, 2, "I KNOW we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
				}
				else
				{
					rally = Direction.NORTH.ordinal();
					Utility.setIndicator(myPlayer, 2, "I don't know where we spawned, heading " + Direction.values()[rally].toString() + ".");
				}
				state = ArbiterBuildOrder.EXPAND;
				return;
				
			case EXPAND:
				Utility.setIndicator(myPlayer, 1, "EXPAND");
				
				
				//////////////////////////////////////////////////////////////////////////////////
				// SENSING
				//	 this custom sensing code is designed to be as compact and fast as possible.
				//   NOTE: everything is filled in and accessed backwards,
				//   so we can break when a null is detected :D
				//
				GameObject[] objects = myPlayer.mySensor.senseNearbyGameObjects(GameObject.class);
				
				Mine[] mines = new Mine[64]; int mineIndex = 0;
				Robot[] enemies = new Robot[64]; int enemyIndex = 0;
				
				Robot onTop;
				
				for ( int i = objects.length ; --i >= 0 ; )
				{
					
					GameObject obj = objects[i];
					
					if(obj.getTeam()==myPlayer.myOpponent)
					{ 
						// Enemy Robot Detected
						enemies[enemyIndex] = (Robot)obj; //cast it correctly
						enemyIndex++;					
					}
					else if(obj.getRobotLevel()==RobotLevel.MINE)
					{
						// Mine Detected
						MapLocation mineLoc = ((Mine)obj).getLocation();
						onTop = (Robot)myPlayer.mySensor.senseObjectAtLocation(mineLoc, RobotLevel.ON_GROUND);
						if ( !badMines.at(mineLoc) && (onTop == null || onTop.getTeam() == myPlayer.myRC.getTeam().opponent() || myPlayer.mySensor.senseRobotInfo(onTop).chassis != Chassis.BUILDING) )
						{
							mines[mineIndex] = (Mine)obj;
							mineIndex++;
						}
					}
					else
					{
						// Debris Detected, ignore
					}
					
				}
				
				Utility.setIndicator(myPlayer, 0, "Number of enemies:"+enemyIndex+", Number of good mines:"+mineIndex);
				
				// fill in enemyInfos
				
				RobotInfo[] enemyInfos = new RobotInfo[enemyIndex];
				for ( int i = -1 ; ++i < enemyIndex ; )
					enemyInfos[i] = myPlayer.mySensor.senseRobotInfo(enemies[i]);
				
				// get closest mine if we're not currently pursuing one
				if ( minMine == null )
				{
					minMineDist = 9999; // sentinel value
					
					for ( int i = -1 ; ++i < mineIndex ; )
					{
						m = mines[i];
						if ( myPlayer.myLoc.distanceSquaredTo(m.getLocation()) < minMineDist )
						{
							minMine = m;
							minMineDist = myPlayer.myLoc.distanceSquaredTo(m.getLocation());
						}
					}
				}
				else
					minMineDist = myPlayer.myLoc.distanceSquaredTo(minMine.getLocation());
				
				if ( minMineDist <= 2 && minMineDist > 0 )
				{
					// there is a mine, and it's within building range
					if ( myPlayer.mySensor.senseObjectAtLocation(minMine.getLocation(), RobotLevel.ON_GROUND) == null && myPlayer.myRC.getTeamResources() > Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					{
						Utility.buildChassis(myPlayer, myPlayer.myLoc.directionTo(minMine.getLocation()), Chassis.BUILDING);
						Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(minMine.getLocation()), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
						minMine = null;
					}
					// rebuild main
					if ( num == 0 && !hasRebuilt && Clock.getRoundNum() > Constants.REBUILD_TIME && myPlayer.myRC.getTeamResources() > Constants.MAD_BANK )
					{
						refineryLoc = minMine.getLocation();
						state = ArbiterBuildOrder.COMPUTE_BUILDINGS_1;
					}
				}
				else if ( minMine != null )
				{
					Utility.setIndicator(myPlayer, 2, "Free mine detected!");
					// there is a mine, but it's away from building range
					int jump = myPlayer.myActions.jumpToMine(minMine, enemyInfos); // TODO is passing enemyInfos expensive???
					if ( jump == Actions.JMP_NOT_POSSIBLE )
					{
						badMines.set(minMine.getLocation());
						myPlayer.myActions.jumpInDir(Direction.values()[rally], enemyInfos);
						minMine = null;
					}
				}
				else
				{
					
					// no mines
					
					
					// Off map rerally code
	        		if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[rally],10)) == TerrainTile.OFF_MAP )
		        	{
	        			if ( num % 2 == 0 )
							rally = (rally + 6) % 8;
						else
							rally = (rally + 2) % 8;
		        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
		        	}
	        		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[(rally-1)%8],10)) == TerrainTile.OFF_MAP )
		        	{
	        			if ( num % 2 == 0 )
							rally = (rally + 1) % 8;
						else
							rally = (rally + 7) % 8;
		        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
		        	}
	        		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[(rally+1)%8],10)) == TerrainTile.OFF_MAP )
		        	{
	        			if ( num % 2 == 0 )
							rally = (rally + 7) % 8;
						else
							rally = (rally + 1) % 8;
		        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
		        	}
	        		else
	        			Utility.setIndicator(myPlayer, 2, "No mines detected, rallied " + Direction.values()[rally].toString() + ".");
					
					int jump = myPlayer.myActions.jumpInDir(Direction.values()[rally], enemyInfos);
					if ( jump == Actions.JMP_SUCCESS )
					{
						// Jumped successfully
						prevLocs.add(myPlayer.myLoc);
						if ( prevLocs.size() > Constants.STUCK_JUMPS )
							prevLocs.pollFirst();
						
						// No enemy found before jumping, check again after
						Utility.attackEnemies(myPlayer);
					}
					else if ( jump == Actions.JMP_NOT_POSSIBLE || (prevLocs.size() >= Constants.STUCK_JUMPS && prevLocs.peekFirst().distanceSquaredTo(myPlayer.myLoc) < ComponentType.JUMP.range) )
					{
						// "Can't jump there, somethins in the way"
						prevLocs.clear();
						rally = (3*numStuck) % 8;
						numStuck++;
						Utility.setIndicator(myPlayer, 2, "I'm stuck, rerallying " + Direction.values()[rally].toString() + ".");
					}
					
				}
				
				
				//////// SPIN AND ATTACK
				RobotInfo enemyInfo = Utility.attackEnemies(myPlayer);
				if ( !myPlayer.myMotor.isActive() )
				{
					if ( enemyInfo != null )
						myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(enemyInfo.location));
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
				}
				
				
				
				return;
			
			case COMPUTE_BUILDINGS_1:
				
				Utility.setIndicator(myPlayer, 1, "COMPUTE_BUILDINGS_1");
				Utility.setIndicator(myPlayer, 2, "Trying to get factory and refinery next to armory...");
				d = myPlayer.myLoc.directionTo(refineryLoc);
				if ( myPlayer.myMotor.canMove(d.rotateLeft()) && myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft()) )
				{
					armoryLoc = myPlayer.myLoc.add(d.rotateLeft());
					factoryLoc = myPlayer.myLoc.add(d.rotateLeft().rotateLeft());
					state = ArbiterBuildOrder.BUILD_BUILDINGS;
				}
				else if ( myPlayer.myMotor.canMove(d.rotateRight()) && myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) )
				{
					armoryLoc = myPlayer.myLoc.add(d.rotateRight());
					factoryLoc = myPlayer.myLoc.add(d.rotateRight().rotateRight());
					state = ArbiterBuildOrder.BUILD_BUILDINGS;
				}
				else if ( !d.isDiagonal() )
				{
					if ( myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft()) && myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft().rotateLeft()) )
					{
						armoryLoc = myPlayer.myLoc.add(d.rotateLeft().rotateLeft());
						factoryLoc = myPlayer.myLoc.add(d.rotateLeft().rotateLeft().rotateLeft());
						state = ArbiterBuildOrder.BUILD_BUILDINGS;
					}
					else if ( myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft()) && myPlayer.myMotor.canMove(d.opposite()) )
					{
						armoryLoc = myPlayer.myLoc.add(d.rotateLeft().rotateLeft());
						factoryLoc = myPlayer.myLoc.add(d.opposite());
						state = ArbiterBuildOrder.BUILD_BUILDINGS;
					}
					else if ( myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) && myPlayer.myMotor.canMove(d.rotateRight().rotateRight().rotateRight()) )
					{
						armoryLoc = myPlayer.myLoc.add(d.rotateRight().rotateRight());
						factoryLoc = myPlayer.myLoc.add(d.rotateRight().rotateRight().rotateRight());
						state = ArbiterBuildOrder.BUILD_BUILDINGS;
					}
					else if ( myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) && myPlayer.myMotor.canMove(d.opposite()) )
					{
						armoryLoc = myPlayer.myLoc.add(d.rotateRight().rotateRight());
						factoryLoc = myPlayer.myLoc.add(d.opposite());
						state = ArbiterBuildOrder.BUILD_BUILDINGS;
					}
				}
				else
					state = ArbiterBuildOrder.COMPUTE_BUILDINGS_2;
				return;
    			
			case COMPUTE_BUILDINGS_2:
				
				Utility.setIndicator(myPlayer, 1, "COMPUTE_BUILDINGS_2");
				Utility.setIndicator(myPlayer, 2, "Trying to get factory next to armory...");
				for ( int i = 8 ; --i >= 0 ; )
				{
					d = Direction.values()[i];
					if ( myPlayer.myMotor.canMove(d) && myPlayer.myMotor.canMove(d.rotateRight()) )
					{
						armoryLoc = myPlayer.myLoc.add(d);
						factoryLoc = myPlayer.myLoc.add(d.rotateRight());
						state = ArbiterBuildOrder.BUILD_BUILDINGS;
						return;
					}
					else if ( myPlayer.myMotor.canMove(d) && !d.isDiagonal() && myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) )
					{
						armoryLoc = myPlayer.myLoc.add(d);
						factoryLoc = myPlayer.myLoc.add(d.rotateRight().rotateRight());
						state = ArbiterBuildOrder.BUILD_BUILDINGS;
						return;
					}
				}
				state = ArbiterBuildOrder.COMPUTE_BUILDINGS_3;
				return;
    			
			case COMPUTE_BUILDINGS_3:
				
				Utility.setIndicator(myPlayer, 1, "COMPUTE_BUILDINGS_3");
				Utility.setIndicator(myPlayer, 2, "Trying to get factory next to refinery...");
				d = myPlayer.myLoc.directionTo(refineryLoc);
				if ( myPlayer.myMotor.canMove(d.rotateLeft()) )
					factoryLoc = myPlayer.myLoc.add(d.rotateLeft());
				else if ( myPlayer.myMotor.canMove(d.rotateRight()) )
					factoryLoc = myPlayer.myLoc.add(d.rotateRight());
				if ( !d.isDiagonal() )
				{
					if ( myPlayer.myMotor.canMove(d.rotateLeft().rotateLeft()) )
						factoryLoc = myPlayer.myLoc.add(d.rotateLeft().rotateLeft());
					else if ( myPlayer.myMotor.canMove(d.rotateRight().rotateRight()) )
						factoryLoc = myPlayer.myLoc.add(d.rotateRight().rotateRight());
				}
				if ( factoryLoc == null )
					state = ArbiterBuildOrder.COMPUTE_BUILDINGS_4;
				else
				{
					for ( int i = 8 ; --i >= 0 ; )
					{
						d = Direction.values()[i];
						if ( myPlayer.myMotor.canMove(d) && !myPlayer.myLoc.add(d).equals(factoryLoc) )
						{
							armoryLoc = myPlayer.myLoc.add(d);
							state = ArbiterBuildOrder.BUILD_BUILDINGS;
							return;
						}
					}
					factoryLoc = null;
					state = ArbiterBuildOrder.COMPUTE_BUILDINGS_4;
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
						armoryLoc = myPlayer.myLoc.add(d);
					}
					else if ( myPlayer.myMotor.canMove(d) && factoryLoc == null )
					{
						Utility.setIndicator(myPlayer, 2, "Factory location found!");
						factoryLoc = myPlayer.myLoc.add(d);
						state = ArbiterBuildOrder.BUILD_BUILDINGS;
						return;
					}
				}
				myPlayer.sleep();
				Utility.setIndicator(myPlayer, 2, "No room for factory and armory. Moving on.");
				myPlayer.sleep();
				state = ArbiterBuildOrder.EXPAND;
				return;
				
			case BUILD_BUILDINGS:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_BUILDINGS");
				Utility.setIndicator(myPlayer, 2, "");
				
				//////// SPIN AND ATTACK
				enemyInfo = Utility.attackEnemies(myPlayer);
				if ( !myPlayer.myMotor.isActive() )
				{
					if ( enemyInfo != null )
						myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(enemyInfo.location));
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
				}
				
				while ( myPlayer.myRC.getTeamResources() < 2 * Chassis.BUILDING.cost + ComponentType.ARMORY.cost + ComponentType.FACTORY.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, myPlayer.myLoc.directionTo(armoryLoc), Chassis.BUILDING);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(armoryLoc), ComponentType.ARMORY, RobotLevel.ON_GROUND);
				Utility.buildChassis(myPlayer, myPlayer.myLoc.directionTo(factoryLoc), Chassis.BUILDING);
				Utility.buildComponent(myPlayer, myPlayer.myLoc.directionTo(factoryLoc), ComponentType.FACTORY, RobotLevel.ON_GROUND);
				myPlayer.sleep();
				myPlayer.myRC.turnOn(refineryLoc, RobotLevel.ON_GROUND);
				myPlayer.sleep();
				state = ArbiterBuildOrder.WAIT_FOR_ACK;
				return;
				
			case WAIT_FOR_ACK:
				
				Utility.setIndicator(myPlayer, 1, "WAIT_FOR_ACK");
				Utility.setIndicator(myPlayer, 2, "");
				
				//////// SPIN AND ATTACK
				enemyInfo = Utility.attackEnemies(myPlayer);
				if ( !myPlayer.myMotor.isActive() )
				{
					if ( enemyInfo != null )
						myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(enemyInfo.location));
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
				}
				
				RobotInfo refinery = myPlayer.mySensor.senseRobotInfo((Robot)myPlayer.mySensor.senseObjectAtLocation(refineryLoc, RobotLevel.ON_GROUND));
				if ( !refinery.location.add(refinery.direction).equals(myPlayer.myLoc) )
					return;
				RobotInfo factory = myPlayer.mySensor.senseRobotInfo((Robot)myPlayer.mySensor.senseObjectAtLocation(factoryLoc, RobotLevel.ON_GROUND));
				if ( !factory.location.add(factory.direction).equals(myPlayer.myLoc) )
					return;
				RobotInfo armory = myPlayer.mySensor.senseRobotInfo((Robot)myPlayer.mySensor.senseObjectAtLocation(armoryLoc, RobotLevel.ON_GROUND));
				if ( !armory.location.add(armory.direction).equals(myPlayer.myLoc) )
					return;
				state = ArbiterBuildOrder.EXPAND;
				return;
				
		}
	}

	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}

	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM )
		{
			if ( num == -1 )
				num = msg.ints[Messenger.firstData+1] - Constants.MAX_DRONES;
		}
	}

	public void onDamageCallback(double damageTaken)
	{
		//Utility.printMsg(myPlayer, "I GOT HIT!  I shouldn't have been hit. :(");
	}

	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
		
	
	
	
	
	
	public String toString() {
		return "ArbiterBehavior";
	}

}
