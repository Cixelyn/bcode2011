 package guileBot.behaviors;

import guileBot.*;
import battlecode.common.*;
import java.util.*;


public class ColossusBehavior extends Behavior
{
	
	private enum ColossusBuildOrder
	{
		EQUIPPING,
		DETERMINE_SPAWN,
		ADVANCE,
		RETREAT
	}
	
	
	ColossusBuildOrder obj = ColossusBuildOrder.EQUIPPING;
	
	MapLocation myLoc; // after trying to jump, set to jmpLoc if success and myLocation otherwise
	
	RobotInfo enemyInfo;
	
	int num = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int westEdge = -1;
	int spawn = -1;
	int rally = -1;
	int jump;
	
	int numBounces;
	
	boolean rallyChanged = false;
	
	ArrayDeque<MapLocation> prevLocs = new ArrayDeque<MapLocation>();
	
	
	public ColossusBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:	

				Utility.setIndicator(myPlayer, 1, "EQUIPPING");
				
				// Decide what kind of heavy I am
				if ( !Utility.compareComponents(myPlayer, Utility.countComponents(Constants.heavyLoadout0) ) && num != -1 )
					obj = ColossusBuildOrder.DETERMINE_SPAWN;
				else if ( !Utility.compareComponents(myPlayer, Utility.countComponents(Constants.heavyLoadout1) ) && num != -1 )
					obj = ColossusBuildOrder.DETERMINE_SPAWN;
				else if ( !Utility.compareComponents(myPlayer, Utility.countComponents(Constants.heavyLoadout2) ) && num != -1 )
					obj = ColossusBuildOrder.DETERMINE_SPAWN;
				else if ( !Utility.compareComponents(myPlayer, Utility.countComponents(Constants.heavyLoadout3) ) && num != -1 )
					obj = ColossusBuildOrder.DETERMINE_SPAWN;
				return;
	        	
			case DETERMINE_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "DETERMINE_SPAWN");
				
				while ( westEdge == -1 || northEdge == -1 || eastEdge == -1 || southEdge == -1 )
				{
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myRC.getLocation().add(Direction.NORTH, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.NORTH, 6)) == TerrainTile.OFF_MAP )
							northEdge = 1;
						else
							northEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myRC.getLocation().add(Direction.EAST, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.EAST, 6)) == TerrainTile.OFF_MAP )
							eastEdge = 1;
						else
							eastEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myRC.getLocation().add(Direction.SOUTH, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.SOUTH, 6)) == TerrainTile.OFF_MAP )
							southEdge = 1;
						else
							southEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myRC.getLocation().add(Direction.WEST, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.WEST, 6)) == TerrainTile.OFF_MAP )
							westEdge = 1;
						else
							westEdge = 0;
					}
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
					myPlayer.sleep();
				}
				spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
				if ( spawn != -1 )
				{
					if ( num < 2 || num >= 6 )
						rally = (spawn + 4) % 8;
					else if ( num % 2 == 0 )
					{
						numBounces = 3; // automatically patrol the edge of map
						if ( spawn % 2 == 1 )
							rally = (spawn + 3) % 8;
						else
							rally = (spawn + 2) % 8;
					}
					else if ( num % 2 == 1 )
					{
						numBounces = 3; // automatically patrol the edge of map
						if ( spawn % 2 == 1 )
							rally = (spawn + 5) % 8;
						else
							rally = (spawn + 6) % 8;
					}
					Utility.setIndicator(myPlayer, 2, "I KNOW we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
				}
				else
				{
					numBounces = 3; // automatically patrol the edge of map
					rally = (2 * num) % 8;
					Utility.setIndicator(myPlayer, 2, "I don't know where we spawned, heading " + Direction.values()[rally].toString() + ".");
				}
				obj = ColossusBuildOrder.ADVANCE;
				return;
				
			case ADVANCE:	
				
				Utility.setIndicator(myPlayer, 1, "ADVANCE");
				
				// Rally to center if we're confident enough in where it is
				if ( Clock.getRoundNum() == Constants.SCRAMBLE_TIME )
				{
					
					switch ( myPlayer.myCartographer.getConfidence() )
					{
						case 0:
							
							Utility.setIndicator(myPlayer, 2, "I've never seen a map edge before. Are they pretty?");
							rallyChanged = true;
							break;
							
						case 1:
							
							Utility.setIndicator(myPlayer, 2, "I've only seen one map edge, I'm not scrambling.");
							rallyChanged = true;
							break;
							
						case 2:
							
							Utility.setIndicator(myPlayer, 2, "I've seen two map edges, I'd rather not scramble.");
							rallyChanged = true;
							break;
							
						case 3:
							
							rally = myPlayer.myRC.getLocation().directionTo(myPlayer.myCartographer.getMapCenter()).ordinal();
							Utility.setIndicator(myPlayer, 2, "I'm pretty sure the center is " + Direction.values()[rally].toString() + ", rerallying.");
							rallyChanged = true;
							break;
							
						case 4:
							
							rally = myPlayer.myRC.getLocation().directionTo(myPlayer.myCartographer.getMapCenter()).ordinal();
							Utility.setIndicator(myPlayer, 2, "I KNOW the center is " + Direction.values()[rally].toString() + ", rerallying.");
							rallyChanged = true;
							break;
					}
				}
				
        		// Attacking code
        		myLoc = myPlayer.myRC.getLocation();
        		enemyInfo = Utility.attackEnemies(myPlayer);
        		
        		// No enemy found
        		if ( enemyInfo == null )
        		{
        			
        			// Off map rerally code
            		if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[rally],6)) == TerrainTile.OFF_MAP )
    	        	{
    	        		if ( numBounces == 0 )
    	        			rally = (rally + 2) % 8; // we have reached the enemy side, everyone search together
    	        		else if ( numBounces == 1 )
    	        			rally = (rally + 4) % 8; // we have searched one part of the enemy side, everyone go back together
    	        		else
    	        		{
    	        			// we have cleared the enemy side, spread out and patrol the sides of the map
    	        			if ( num % 2 == 0 )
    		        			rally = (rally + 2) % 8;
    		        		else
    		        			rally = (rally + 6) % 8;
    	        		}
    	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
    	        		numBounces++;
    	        		rallyChanged = true;
    	        	}
            		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
    	        	{
    	        		// we have reached the closest side to the enemy corner, rerally to corner
    	        		if ( num % 2 == 0 )
    	        			rally = (rally + 1) % 8;
    	        		else
    	        			rally = (rally + 7) % 8;
    	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
    	        		numBounces++;
    	        		rallyChanged = true;
    	        	}
            		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
    	        	{
            			// we have reached the closest side to the enemy corner, rerally to corner
    	        		if ( num % 2 == 0 )
    	        			rally = (rally + 7) % 8;
    	        		else
    	        			rally = (rally + 1) % 8;
    	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
    	        		numBounces++;
    	        		rallyChanged = true;
    	        	}
            		
        			// Try to jump
        			jump = jumpInDir(Direction.values()[rally]); // myLoc is set here if jump is successful
					if ( jump == JMP_SUCCESS )
					{
						// Jumped successfully
						prevLocs.add(myLoc);
						if ( prevLocs.size() > Constants.STUCK_JUMPS )
							prevLocs.pollFirst();
						
						// No enemy found before jumping, check again after
						enemyInfo = Utility.attackEnemies(myPlayer);
					}
					else if ( jump == JMP_NOT_POSSIBLE || (prevLocs.size() >= Constants.STUCK_JUMPS && prevLocs.peekFirst().distanceSquaredTo(myLoc) < ComponentType.JUMP.range) )
					{
						// "Can't jump there, somethins in the way"
						prevLocs.clear();
						if ( num % 2 == 0 )
							rally = (rally + 3) % 8;
						else if ( num % 2 == 1 )
							rally = (rally + 5) % 8;
						Utility.setIndicator(myPlayer, 2, "I'm stuck, rerallying " + Direction.values()[rally].toString() + ".");
						rallyChanged = true;
					}
        		}
        		
        		// Enemy in range, either before or after jump. Enable the micros
        		if ( enemyInfo != null )
        		{
        			if ( myLoc.distanceSquaredTo(enemyInfo.location) <= 16 )
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy in range, backing up!");
        				if ( !myPlayer.myMotor.isActive() )
        				{
	        				if ( (myPlayer.myRC.getDirection() == myLoc.directionTo(enemyInfo.location) || myPlayer.myRC.getDirection() == myLoc.directionTo(enemyInfo.location).rotateLeft() || myPlayer.myRC.getDirection() == myLoc.directionTo(enemyInfo.location).rotateRight()) && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().opposite()))
	        					myPlayer.myMotor.moveBackward();
	        				else if ( myPlayer.myRC.getDirection() != myLoc.directionTo(enemyInfo.location) )
	        					myPlayer.myMotor.setDirection(myLoc.directionTo(enemyInfo.location));
        				}
        			}
        			else
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy detected, engaging.");
        				if ( !myPlayer.myMotor.isActive() )
        				{
	        				if ( (myPlayer.myRC.getDirection() == myLoc.directionTo(enemyInfo.location) || myPlayer.myRC.getDirection() == myLoc.directionTo(enemyInfo.location).rotateLeft() || myPlayer.myRC.getDirection() == myLoc.directionTo(enemyInfo.location).rotateRight()) && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
	        					myPlayer.myMotor.moveForward();
	        				else if ( myPlayer.myRC.getDirection() != myLoc.directionTo(enemyInfo.location) )
	        					myPlayer.myMotor.setDirection(myLoc.directionTo(enemyInfo.location));
        				}
        			}
        		}
        		// No enemy found before/after jumping
        		else if ( !myPlayer.myMotor.isActive() )
        		{
        			if ( !rallyChanged )
        				Utility.setIndicator(myPlayer, 2, "No enemies detected, rallied " + Direction.values()[rally].toString() + "."	);
        			// Make sure I'm not getting flanked
        			if ( myPlayer.hasTakenDamage )
    	        		myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
        			// Move forward if you can
        			else if ( myPlayer.myRC.getDirection() != Direction.values()[rally] )
        				myPlayer.myMotor.setDirection(Direction.values()[rally]);
        			else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) && jump != JMP_SUCCESS )
        				myPlayer.myMotor.moveForward();
        		}
        		rallyChanged = false;
	        	return;	        	     
		}
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
				myLoc = jmpLoc; // added by JVen
				return JMP_SUCCESS;
			}
			jmpLoc = jmp.nextLoc();
		}
			
		return JMP_NOT_POSSIBLE;
		
	}
	
	
	
	
	
	
	public String toString()
	{
		return "ColossusBehavior";
	}


	
	

	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM_HEAVY )
		{
			if ( num == -1 )
			{
				num = msg.ints[Messenger.firstData+1];
				if ( num == 0 )
					Utility.setIndicator(myPlayer, 0, "I'm heavy " + Integer.toString(num) + ", double railguns all the way!");
				else if ( num % 3 == 0 )
					Utility.setIndicator(myPlayer, 0, "I'm heavy " + Integer.toString(num) + ", smudge safeties off!");
				else if ( num % 3 == 1 )
					Utility.setIndicator(myPlayer, 0, "I'm heavy " + Integer.toString(num) + ", $MG$ R 4 N))B$!");
				else if ( num % 3 == 2 )
					Utility.setIndicator(myPlayer, 0, "I'm heavy " + Integer.toString(num) + ", tr-tr-triple blaster!");
			}
		}
	}

	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
	
}
