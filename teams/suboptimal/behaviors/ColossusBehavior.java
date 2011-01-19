package suboptimal.behaviors;

import suboptimal.*;
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
	
	
	private final OldNavigation myNav = new OldNavigation(myPlayer);
	
	ColossusBuildOrder obj = ColossusBuildOrder.EQUIPPING;
	
	int num = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int westEdge = -1;
	int spawn = -1;
	int rally = -1;
	int permRally = -1;
	int numBounces;
	ArrayDeque<MapLocation> prevFiveLocs = new ArrayDeque<MapLocation>();
	
	
	private static final int[] componentLoadOut1 = Utility.countComponents(new ComponentType[]    
                               {ComponentType.RADAR,ComponentType.JUMP,ComponentType.SHIELD,
								ComponentType.RAILGUN,ComponentType.SMG,ComponentType.SMG,ComponentType.SMG,
								ComponentType.HARDENED
								});
	
	private static final int[] componentLoadOut2 = Utility.countComponents(new ComponentType[]    
                               {ComponentType.RADAR,ComponentType.JUMP,ComponentType.SHIELD,
								ComponentType.RAILGUN,ComponentType.BLASTER,
								ComponentType.SHIELD,ComponentType.SHIELD,ComponentType.SHIELD,ComponentType.SHIELD
								});
	
	private static final int[] componentLoadOut3 = Utility.countComponents(new ComponentType[]    
	                           {ComponentType.RADAR,ComponentType.JUMP,ComponentType.SHIELD,
								ComponentType.BLASTER,ComponentType.BLASTER,ComponentType.BLASTER,ComponentType.SMG,
								ComponentType.PLASMA,ComponentType.PLASMA
								});
	
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
				
				if (Utility.compareComponents(myPlayer, componentLoadOut1) || Utility.compareComponents(myPlayer, componentLoadOut2) || Utility.compareComponents(myPlayer, componentLoadOut3))
				{
					obj = ColossusBuildOrder.DETERMINE_SPAWN;
				}
				return;
	        	
			case DETERMINE_SPAWN:
				
				myPlayer.myRC.setIndicatorString(1, "DETERMINE_SPAWN");
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
				}
				spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
				if ( spawn != -1 )
				{
					if ( num < 3 || num > 8 )
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
					Utility.setIndicator(myPlayer, 0, "I KNOW we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
				}
				else
				{
					rally = (2 * num + 1) % 8;
					Utility.setIndicator(myPlayer, 0, "I don't know where we spawned, heading " + Direction.values()[rally].toString() + ".");
				}
				permRally = rally;
				obj = ColossusBuildOrder.ADVANCE;
				return;
				
			case ADVANCE:	
				
				Utility.setIndicator(myPlayer, 1, "ADVANCE");
				
				// Rerally code
	        	if ( spawn != -1 )
	        	{
		        	// off_map found in orthogonal direction, try a different ORTHOGONAL direction!
		        	if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[rally],6)) == TerrainTile.OFF_MAP )
		        	{
		        		if ( numBounces == 0 )
		        			rally = (rally + 2) % 8;
		        		else if ( numBounces == 1 )
		        			rally = (rally + 4) % 8;
		        		else
		        		{
		        			if ( num % 2 == 0 )
			        			rally = (rally + 2) % 8;
			        		else
			        			rally = (rally + 6) % 8;
		        		}
		        		spawn = (rally + 4) % 8;
		        		permRally = rally;
		        		Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
		        	// off_map found in orthogonal direction with diagonal rally, try a different ORTHOGONAL direction!
		        	else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 1) % 8;
		        		spawn = (rally + 4) % 8;
		        		permRally = rally;
		        		Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
		        	// off_map found in orthogonal direction with diagonal rally, try a different ORTHOGONAL direction!
		        	else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 7) % 8;
		        		spawn = (rally + 4) % 8;
		        		permRally = rally;
		        		Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
	        	}
	        	else
	        	{
	        		if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[rally],6)) == TerrainTile.OFF_MAP )
		        	{
		        		if ( numBounces == 0 )
		        			rally = (rally + 2) % 8;
		        		else if ( numBounces == 1 )
		        			rally = (rally + 4) % 8;
		        		else
		        		{
		        			if ( num % 2 == 0 )
			        			rally = (rally + 2) % 8;
			        		else
			        			rally = (rally + 6) % 8;
		        		}
		        		Utility.setIndicator(myPlayer, 0, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
	        		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 3) % 8;
		        		Utility.setIndicator(myPlayer, 0, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
	        		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 5) % 8;
		        		Utility.setIndicator(myPlayer, 0, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
	        	}
				
	        	
				boolean shouldJump = true;
				for (WeaponController w:myPlayer.myWeapons) {
					if (w.type() == ComponentType.RAILGUN && w.isActive()) {
						shouldJump=false;
					}
				}
				if (shouldJump)
				{
					if ( spawn != -1 )
						Utility.setIndicator(myPlayer, 2, "Current rally: " + Direction.values()[rally] + ".");
					int jump=jumpInDir(Direction.values()[rally]);
					if ( jump == JMP_SUCCESS )
					{
						prevFiveLocs.add(myPlayer.myRC.getLocation());
						if ( prevFiveLocs.size() > 5 )
							prevFiveLocs.pollFirst();
					}
					if ( jump == JMP_NOT_POSSIBLE || (prevFiveLocs.size() >= 5 && prevFiveLocs.peekFirst().distanceSquaredTo(myPlayer.myRC.getLocation()) < ComponentType.JUMP.range) )
					{
						prevFiveLocs.clear();
						if ( spawn % 2 == 0 )
							rally = (rally + 3) % 8;
						else if ( spawn % 2 == 1 )
							rally = (rally + 5) % 8;
						permRally = rally;
						Utility.setIndicator(myPlayer, 0, "I'm stuck, rerallying " + Direction.values()[rally].toString() + ".");
					}
				}
				
				
	        	//RUN SUPER SPECIAL CUSTOM SENSOR CODE
	        	Robot[] robots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
	        	
	        	
	        	//QUICK CODE FOR JUST FINDING THE NEAREST ROBOT
	        	//FIXME: I'll eventually get around to adding some extra thing;
	        	//		TODO:add prioritization based on components, etc.	        	
	        	Robot 		nearestEnemyRobot			= null;
	        	RobotInfo	nearestEnemyRobotInfo		= null;
	        	int			nearestEnemyRobotDistance	= 999;
	        	Direction	nearestEnemyRobotDirection  = null;
	        	
	        	MapLocation myLoc = myPlayer.myRC.getLocation();
	        	
	        	for(int i=robots.length; --i>=0;) {
	        		if(robots[i].getTeam()==myPlayer.myOpponent) {
	        			RobotInfo rinfo = myPlayer.mySensor.senseRobotInfo(robots[i]);
	        			
	        			int robotDistance = myLoc.distanceSquaredTo(rinfo.location);
	        			if(robotDistance < nearestEnemyRobotDistance) {
	        				nearestEnemyRobot = robots[i];
	        				nearestEnemyRobotInfo = rinfo;
	        				nearestEnemyRobotDistance = robotDistance;
	        				nearestEnemyRobotDirection = myLoc.directionTo(nearestEnemyRobotInfo.location);
	        			}
	        		}
	        	}
	        	
	        	
	        	//I AM ENGAGED IN BLOODY COMBAT	
	        	if(nearestEnemyRobot!=null) {	
	        		
	        		rally = nearestEnemyRobotDirection.ordinal();
	        		//HOW FAR AWAY IS THE ENEMY
	        		if(nearestEnemyRobotDistance<=36) {	// checks range: [0,25]
							for(WeaponController w:myPlayer.myWeapons) { 
								if(!w.isActive() && w.withinRange(nearestEnemyRobotInfo.location)) {	//FIXME: Overkill if using more than one weapon
									w.attackSquare(nearestEnemyRobotInfo.location, nearestEnemyRobot.getRobotLevel());
								}
							}
					}
					
					
	        		//if I'm too closet to enemy units, move back
					if(nearestEnemyRobotDistance<=26 && nearestEnemyRobotDistance > 16) {
						if (!myPlayer.myMotor.isActive() && !myPlayer.myRC.getDirection().equals(nearestEnemyRobotDirection)) {//I'm good				
							myPlayer.myMotor.setDirection(nearestEnemyRobotDirection);
						}
					} else if(nearestEnemyRobotDistance<=16) {					//I'm too close!
						myPlayer.myActions.backUpInDir(nearestEnemyRobotDirection.opposite());
					} else { //I'm too far
						myPlayer.myActions.moveInDir(myNav.bugTo(nearestEnemyRobotInfo.location));
					}
					return;
					
				}
	        	else if (myPlayer.hasTakenDamage) {
	        		if (!myPlayer.myMotor.isActive()) {
	        			myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
	        		}
	        	}
	        	else {
	        		rally = permRally;
	        	}
	        	     
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Quick function to do all the checks necessary to ensure that a square is jumpable.
	 * @param loc
	 * @return
	 * @throws GameActionException
	 */
	public boolean canJump(MapLocation loc) throws GameActionException {
		if(myPlayer.myRC.senseTerrainTile(loc) != TerrainTile.LAND) return false;
		if(!myPlayer.mySensor.canSenseSquare(loc)) return false;
		if(myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND)!=null) return false;
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
	
	public int jumpInDir(Direction dir) throws GameActionException{
		//Make sure direction is valid (can be removed at a later point)
		if(dir.ordinal()>=8) return JMP_NOT_POSSIBLE;
		
		//First, lets make sure we are pointed in the correct direction
		if(!myPlayer.myRC.getDirection().equals(dir)) {
			if(!myPlayer.myMotor.isActive()) {
				myPlayer.myMotor.setDirection(dir);
			}
			return JMP_NOT_YET;
			
		}
		
		//Now lets jump in the direction
		if(!myPlayer.myJump.isActive()) {
			
			JumpTable jmp = new JumpTable(myPlayer.myRC.getLocation(),dir);
			MapLocation jmpLoc = jmp.nextLoc();
			while (jmpLoc!=null) {
				if (canJump(jmpLoc)) {
					myPlayer.myJump.jump(jmpLoc);
					return JMP_SUCCESS;
				}
				jmpLoc = jmp.nextLoc();
			}
			
			return JMP_NOT_POSSIBLE;
		}
		return JMP_NOT_YET;
		

		
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
				Utility.setIndicator(myPlayer, 2, "I'm heavy " + Integer.toString(num) + "!");
			}
		}
	}

	public void newComponentCallback(ComponentController[] components) {}	
	public void onWakeupCallback(int lastActiveRound) {}
	public void onDamageCallback(double damageTaken) {}
	
}
