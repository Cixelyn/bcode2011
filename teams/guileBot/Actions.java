package guileBot;

import battlecode.common.*;


/**
 * Action system that wraps commonly used movement functionality
 * @author Cory
 *
 */
public class Actions {
	
	private final RobotPlayer myPlayer;
	private final RobotController myRC;
	
	
	public Actions(RobotPlayer player) {
		myPlayer = player;
		myRC = player.myRC;
	}
	
	
	
	
	/**
	 * Attempts to make a jump in a particular direction.  If the jump fails
	 * or cannot be executed on that turn, this function returns false
	 * @param dir
	 * @return
	 */
	
	public static int JMP_SUCCESS = 0;
	public static int JMP_NOT_YET = 1;
	public static int JMP_NOT_POSSIBLE = 2;
	
	public int jumpInDir(Direction dir) throws GameActionException
	{
		
		//Make sure direction is valid (can be removed at a later point)
		if( dir.ordinal()>=8 )
			return JMP_NOT_POSSIBLE;
		
		
		//Grab an active jump
		int jumpEngine = availableJump();
		
		
		// First, lets make sure we are pointed in the correct direction (if we don't have a satellite
		// Also check that a jump is available -JVen
		if ( (myPlayer.mySensor.type() != ComponentType.SATELLITE && !myPlayer.myRC.getDirection().equals(dir)) || jumpEngine == -1 )
		{
			/*if ( !myPlayer.myMotor.isActive() )
				myPlayer.myMotor.setDirection(dir);*/  // Commented out by JVen. No set directions should be done here
			return JMP_NOT_YET;
		}
		
		//Now lets jump in the direction
		
		JumpTable jmp = new JumpTable(myPlayer.myRC.getLocation(),dir);
		MapLocation jmpLoc = jmp.nextLoc();
		
		while (jmpLoc!=null)
		{
			if ( canJump(jmpLoc) )
			{
				myPlayer.myJumps[jumpEngine].jump(jmpLoc);
				myPlayer.myLoc = jmpLoc;			//  Reset the Robot Location upon jump.
				return JMP_SUCCESS;
			}
			jmpLoc = jmp.nextLoc();
		}
			
		return JMP_NOT_POSSIBLE;
		
	}
	
	/**
	 * Same as above, except avoids enemies
	 * @author JVen
	 * @param dir The direction in which to jump
	 * @param enemyInfos A list of enemy RobotInfos 
	 * @return An int depending on whether the jump was successful or why it was not
	 */
	
	public int jumpInDir(Direction dir, RobotInfo[] enemyInfos) throws GameActionException
	{
		
		//Make sure direction is valid (can be removed at a later point)
		if( dir.ordinal()>=8 )
			return JMP_NOT_POSSIBLE;
		
		
		//Grab an active jump
		int jumpEngine = availableJump();
		
		
		// First, lets make sure we are pointed in the correct direction (if we don't have a satellite
		// Also check that a jump is available -JVen
		if ( (myPlayer.mySensor.type() != ComponentType.SATELLITE && !myPlayer.myRC.getDirection().equals(dir)) || jumpEngine == -1 )
		{
			/*if ( !myPlayer.myMotor.isActive() )
				myPlayer.myMotor.setDirection(dir);*/  // Commented out by JVen. No set directions should be done here
			return JMP_NOT_YET;
		}
		
		//Now lets jump in the direction
		
		JumpTable jmp = new JumpTable(myPlayer.myRC.getLocation(),dir);
		MapLocation jmpLoc = jmp.nextLoc();
		
		boolean enemyNearby;
		while (jmpLoc!=null)
		{
			if ( canJump(jmpLoc) )
			{
				enemyNearby = false;
				// check if there is any enemy near jmpLoc
				for ( int i = enemyInfos.length ; --i >= 0 ; )
				{
					if ( jmpLoc.distanceSquaredTo(enemyInfos[i].location) <= Utility.maxRange(enemyInfos[i]) )
					{
						enemyNearby = true;
						break;
					}
				}
				if ( !enemyNearby )
				{
					myPlayer.myJumps[jumpEngine].jump(jmpLoc);
					myPlayer.myLoc = jmpLoc;			//  Reset the Robot Location upon jump.
					return JMP_SUCCESS;
				}
			}
			jmpLoc = jmp.nextLoc();
		}
			
		return JMP_NOT_POSSIBLE;
		
	}
	
	/**
	 * Attempts to make a jump towards a mine while avoiding enemies
	 * @author JVen
	 * @param m The mine to jump towards
	 * @param enemyInfos A list of enemy RobotInfos
	 * @return An int depending on whether the jump was successful or why it was not
	 */
	
	public int jumpToMine(Mine m, RobotInfo[] enemyInfos) throws GameActionException
	{
		//TODO is passing RobotInfo[]s expensive?
		
		// Get a non-active JumpController, if available
		int jumpEngine = availableJump();
		
		
		// If we're already near the mine or no jumps are available, return
		if ( myPlayer.myRC.getLocation().distanceSquaredTo(m.getLocation()) <= 2 || jumpEngine == -1 )
			return JMP_NOT_YET;
		
		// Otherwise, jump towards the mine
		JumpTable jmp = new JumpTable(myPlayer.myRC.getLocation(),myPlayer.myRC.getLocation().directionTo(m.getLocation()));
		MapLocation jmpLoc = jmp.nextLoc();
		
		
		boolean enemyNearby;
		while ( jmpLoc != null )
		{
			// also check that we don't jump on a mine
			if ( canJump(jmpLoc) && myPlayer.mySensor.senseObjectAtLocation(jmpLoc, RobotLevel.MINE) == null )
			{
				// check that jmpLoc is closer to the mine (but not on it) and that we can jump there
				int newDist = jmpLoc.distanceSquaredTo(m.getLocation());
				if ( (newDist < myPlayer.myRC.getLocation().distanceSquaredTo(m.getLocation()) || newDist <= 2) && canJump(jmpLoc) )
				{
					enemyNearby = false;
					// check if there is any enemy near jmpLoc
					for ( int i = enemyInfos.length ; --i >= 0 ; )
					{
						if ( jmpLoc.distanceSquaredTo(enemyInfos[i].location) <= Utility.maxRange(enemyInfos[i]) )
						{
							enemyNearby = true;
							break;
						}
					}
					if ( !enemyNearby )
					{
						myPlayer.myJumps[jumpEngine].jump(jmpLoc);
						myPlayer.myLoc = jmpLoc;			//  Reset the Robot Location upon jump.
					return JMP_SUCCESS;
					}
				}
			}
			jmpLoc = jmp.nextLoc();
		}
			
		return JMP_NOT_POSSIBLE;
		
	}
	
	/**
	 * Quick function to do all the checks necessary to ensure that a square is jumpable.
	 * @param loc
	 * @return
	 * @throws GameActionException
	 */
	
	public boolean canJump(MapLocation loc) throws GameActionException
	{
		if ( myPlayer.myRC.senseTerrainTile(loc) != TerrainTile.LAND )
			return false;
		if ( !myPlayer.mySensor.canSenseSquare(loc) )
			return false;
		if ( myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) != null )
			return false;
		return true;
	}
	
	
	
	/**
	 * Quick function to determine if we have an active jump
	 * @return position in myJumps array with a usable jump. No usable jumps returns -1.
	 */
	public int availableJump() {
		for(int i=myPlayer.myJumps.length; --i>=0;) {
			if(!myPlayer.myJumps[i].isActive()) {
				return i;
			}
		}
		return -1;
	}
	
	
	
	
	
	
	

	
}

