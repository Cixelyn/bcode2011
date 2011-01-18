package suboptimal.behaviors;

import suboptimal.*;
import battlecode.common.*;


public class ColossusBehavior extends Behavior
{
		
	int num = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int westEdge = -1;
	int spawn = -1;
	int rally = -1;
	int numBounces;
	
	private enum ColossusBuildOrder
	{
		EQUIPPING,
		DETERMINE_SPAWN,
		ADVANCE,
		RETREAT
	}
	
	ColossusBuildOrder obj = ColossusBuildOrder.EQUIPPING;
	private final OldNavigation myNav = new OldNavigation(myPlayer);
	
	
	
	
	private static final int[] componentLoadOut = Utility.countComponents(new ComponentType[]    
                               {ComponentType.RAILGUN,ComponentType.SMG,ComponentType.SMG,
								ComponentType.RADAR,ComponentType.JUMP,
								ComponentType.SHIELD,ComponentType.SHIELD,ComponentType.SHIELD,ComponentType.SHIELD,ComponentType.SHIELD,
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
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
				
				if (Utility.compareComponents(myPlayer, componentLoadOut))
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
					rally = (spawn + 4) % 8;
					Utility.setIndicator(myPlayer, 0, "I KNOW we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
				}
				else
				{
					rally = (2 * num + 1) % 8;
					Utility.setIndicator(myPlayer, 0, "I don't know where we spawned, heading " + Direction.values()[rally].toString() + ".");
				}
				obj = ColossusBuildOrder.ADVANCE;
				return;
				
			case ADVANCE:	
        		
				
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
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
		        	// off_map found in orthogonal direction with diagonal rally, try a different ORTHOGONAL direction!
		        	if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 1) % 8;
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
		        	// off_map found in orthogonal direction with diagonal rally, try a different ORTHOGONAL direction!
		        	if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 7) % 8;
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
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
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
	        		if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 3) % 8;
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
		        	if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 5) % 8;
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
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
	        		
	        		//HOW FAR AWAY IS THE ENEMY
	        		if(nearestEnemyRobotDistance<=16) {	// checks range: [0,16]
							for(WeaponController w:myPlayer.myWeapons) { 
								if(!w.isActive() && w.withinRange(nearestEnemyRobotInfo.location)) {	//FIXME: Overkill if using more than one weapon
									w.attackSquare(nearestEnemyRobotInfo.location, nearestEnemyRobot.getRobotLevel());
								}
							}
					}
					
					
	        		//if I'm too closet to enemy units, move back
					if(nearestEnemyRobotDistance<=16 && nearestEnemyRobotDistance > 9) {
						return;  //I'm good					
					} else if(nearestEnemyRobotDistance<=9) {					//I'm too close!
						myPlayer.myActions.backUpInDir(nearestEnemyRobotDirection.opposite());
					} else { //I'm too far
						myPlayer.myActions.moveInDir(myNav.bugTo(nearestEnemyRobotInfo.location));
					}
					return;
					
					
				//I AM NOT ENGAGED IN COMBAT, SO RUN NAVIGATION
				} else{
					Utility.setIndicator(myPlayer, 1, "Jump Navigation");
					jumpInDir(Direction.values()[rally]);	
		        	return;
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
		if(!myPlayer.myRC.senseTerrainTile(loc).equals(TerrainTile.LAND)) return false;
		if(!myPlayer.mySensor.canSenseSquare(loc)) return false;
		if(myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND)!=null) return false;
		return true;
	}
	
	
	
	
	
	/**
	 * Private class used to do the jump calculations
	 * Eventually we should split into a separate file because of the overhead of inner classes
	 * @author Cory
	 *
	 */
	private class JumpTable {

		private int dx; private int dy;
		private boolean isDiagonal;
		private boolean isVertical;
		private MapLocation myLoc;
		
		public int NUM_CHOICES = 4;
		
		
		public JumpTable(MapLocation loc, Direction dir) {
			myLoc=loc;
			dx = dir.dx;
			dy = dir.dy;
			isDiagonal = dir.isDiagonal();
			isVertical = (dy!=0);
		}
		
		public MapLocation nextLoc(int i) {
			if(isDiagonal) {
				switch(i) {
				case 0:
					return new MapLocation(myLoc.x + dx*3, myLoc.y + dy*2);
				case 1:
					return new MapLocation(myLoc.x + dx*2, myLoc.y + dy*3);
				case 2:
					return new MapLocation(myLoc.x + dx*1, myLoc.y + dy*3);
				case 3:
					return new MapLocation(myLoc.x + dx*3, myLoc.y + dy*1);
				case 4:
					return new MapLocation(myLoc.x + dx*2, myLoc.y + dx*2);
				}
			} else { //Not Diagonal
				
				if(isVertical) {
					switch(i) {
					case 0:
						return new MapLocation(myLoc.x     , myLoc.y+4*dy);
					case 1:
						return new MapLocation(myLoc.x + 1 , myLoc.y+3*dy);
					case 2:
						return new MapLocation(myLoc.x - 1 , myLoc.y+3*dy);
					case 3:
						return new MapLocation(myLoc.x + 2,  myLoc.y+3*dy);
					case 4:
						return new MapLocation(myLoc.x - 2,  myLoc.y+3*dy);
					}
				} else {
					switch(i) {
					case 0:
						return new MapLocation(myLoc.x+4*dx, myLoc.y    );
					case 1:
						return new MapLocation(myLoc.x+3*dx, myLoc.y + 1);
					case 2:
						return new MapLocation(myLoc.x+3*dx, myLoc.y - 1);
					case 3:
						return new MapLocation(myLoc.x+3*dx, myLoc.y + 2);
					case 4:
						return new MapLocation(myLoc.x+3*dx, myLoc.y - 2);
					}	
				}
			}
			
			return null;
			
		}
		
	}
	
	
	
	/**
	 * Attempts to make a jump in a particular direction.  If the jump fails
	 * or cannot be executed on that turn, this function returns false
	 * @param dir
	 * @return
	 */
	public boolean jumpInDir(Direction dir) throws GameActionException{
		//Make sure direction is valid (can be removed at a later point)
		if(dir.ordinal()>=8) return false;
		
		//First, lets make sure we are pointed in the correct direction
		if(!myPlayer.myRC.getDirection().equals(dir)) {
			if(!myPlayer.myMotor.isActive()) {
				myPlayer.myMotor.setDirection(dir);
			}
			return false;
			
		}
		
		//Now lets jump in the direction
		if(!myPlayer.myJump.isActive()) {
			
			JumpTable jmp = new JumpTable(myPlayer.myRC.getLocation(),dir);

			for(int i=jmp.NUM_CHOICES; --i>0;) {
				MapLocation jmpLoc = jmp.nextLoc(jmp.NUM_CHOICES-i);
				
				if(canJump(jmpLoc)) {
					myPlayer.myJump.jump(jmpLoc);
					return true;
				}
			}
			return false;
		}else {
			return false;
		}

		
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
