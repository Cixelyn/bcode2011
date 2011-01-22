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
		
		// First, lets make sure we are pointed in the correct direction
		if ( !myPlayer.myRC.getDirection().equals(dir) || myPlayer.myJump.isActive() )
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
				myPlayer.myJump.jump(jmpLoc);
				return JMP_SUCCESS;
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
	
	
	
	
	
	
	
	

	
}

