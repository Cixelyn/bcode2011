package guileBot.behaviors; import battlecode.common.*; import guileBot.*;
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
	private ArrayList<Integer> badMines = new ArrayList<Integer>(GameConstants.MINES_MAX);
	
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int westEdge = -1;
	int spawn = -1;
	int rally = -1;

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
		SEARCH_AND_DESTROY
	}
	
	

	public void run() throws Exception {
		
		switch(state) {
		
		
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
					rally = (spawn + 1) % 8;
				else
					rally = (spawn + 2) % 8;
				Utility.setIndicator(myPlayer, 2, "I KNOW we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
			}
			else
			{
				rally = Direction.NORTH.ordinal();
				Utility.setIndicator(myPlayer, 2, "I don't know where we spawned, heading " + Direction.values()[rally].toString() + ".");
			}
			state = ArbiterBuildOrder.SEARCH_AND_DESTROY;
			return;
			
		case SEARCH_AND_DESTROY:
			Utility.setIndicator(myPlayer, 1, "SEARCH_AND_DESTROY");
			
			//////// SPIN!!!!!!!
			if ( !myPlayer.myMotor.isActive() )
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
			
			
			//////////////////////////////////////////////////////////////////////////////////
			// SENSING
			//	 this custom sensing code is designed to be as compact and fast as possible.
			//   NOTE: everything is filled in and accessed backwards,
			//   so we can break when a null is detected :D
			//
			GameObject[] objects = myPlayer.mySensor.senseNearbyGameObjects(GameObject.class);
			
			Mine[] mines = new Mine[64]; int mineIndex = 0;
			Robot[] enemies = new Robot[64]; int enemyIndex = 0;
			
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
					if ( !badMines.contains(obj.getID()) && myPlayer.mySensor.senseObjectAtLocation(((Mine)obj).getLocation(), RobotLevel.ON_GROUND) == null )
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
			
			Utility.setIndicator(myPlayer, 0, "E:"+enemyIndex+" M:"+mineIndex);
			
			// fill in enemyInfos
			
			RobotInfo[] enemyInfos = new RobotInfo[enemyIndex];
			for ( int i = -1 ; ++i < enemyIndex ; )
				enemyInfos[i] = myPlayer.mySensor.senseRobotInfo(enemies[i]);
			
			// get closest mine
			
			int minMineDist = 9999; // sentinel value
			Mine minMine = null;
			Mine m;
			
			for ( int i = -1 ; ++i < mineIndex ; )
			{
				m = mines[i];
				if ( myPlayer.myLoc.distanceSquaredTo(m.getLocation()) < minMineDist )
				{
					minMine = m;
					minMineDist = myPlayer.myLoc.distanceSquaredTo(m.getLocation());
				}
			}
			
			if ( minMineDist <= 2 && minMineDist > 0 )
			{
				while ( myPlayer.myRC.getTeamResources() > Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
					myPlayer.sleep();
				Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(minMine.getLocation()), Chassis.BUILDING);
				Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(minMine.getLocation()), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
			}
			else if ( minMine != null )
			{
				Utility.setIndicator(myPlayer, 2, "Free mine detected!");
				// there is a mine
				int jump = myPlayer.myActions.jumpToMine(minMine, enemyInfos); // TODO is passing enemyInfos expensive???
				if ( jump == Actions.JMP_NOT_POSSIBLE )
				{
					badMines.add(minMine.getID());
					myPlayer.myActions.jumpInDir(Direction.values()[rally], enemyInfos);
				}
			}
			else
			{
				
				// no mines
				
				
				// Off map rerally code
        		if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[rally],10)) == TerrainTile.OFF_MAP )
	        	{
	        		rally = (rally + 6) % 8;
	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
	        	}
        		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[(rally-1)%8],10)) == TerrainTile.OFF_MAP )
	        	{
	        		rally = (rally + 1) % 8;
	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
	        	}
        		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[(rally+1)%8],10)) == TerrainTile.OFF_MAP )
	        	{
	        		rally = (rally + 7) % 8;
	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
	        	}
        		else
        			Utility.setIndicator(myPlayer, 2, "No mines detected, rallied " + Direction.values()[rally].toString() + ".");
				
				myPlayer.myActions.jumpInDir(Direction.values()[rally], enemyInfos);
				
			}
			
			
			
			
			
			
			return;
		
		
		
		}
	}

	
	public void newComponentCallback(ComponentController[] components) {
	}

	public void newMessageCallback(MsgType type, Message msg) {
	}

	public void onDamageCallback(double damageTaken) {
		Utility.printMsg(myPlayer, "I GOT HIT!  I shouldn't have been hit. :(");
	}

	public void onWakeupCallback(int lastActiveRound) {		
	}
		
	
	
	
	
	
	public String toString() {
		return "ArbiterBehavior";
	}

}
