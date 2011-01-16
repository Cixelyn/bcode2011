package suboptimal.behaviors;

import suboptimal.*;
import battlecode.common.*;




/**
 * Current marine iteration receives spawn location from towers, and then rushes forward in the correct direction.
 * Small micro battle tricks added in order to make combat a little bit better
 * 
 * @since MASTeRYtwo v1.0
 * @author FiBsTeR
 *
 */
public class MarineBehavior extends Behavior
{
	
	
	private enum MarineBuildOrder
	{
		EQUIPPING,
		MOVE_OUT
	}
	
	private final OldNavigation nav = new OldNavigation(myPlayer);
	private MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	private MapLocation enemyLoc;
	
	boolean hasBlaster;
	boolean hasRadar;
	boolean hasAntenna;
	
	boolean isLeader;
	int currLeader;
	MapLocation currLeaderLoc;
	
	MapLocation enemyLocation;
	int spawn = -1; // -1 if unknown
	int realSpawn = -1; // -1 if unknown

	int oldSpawn = -1; // -1 if unknown
	
	boolean offMapFound = false;
	
	public MarineBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
				hasBlaster = false;
				hasRadar = false;
				hasAntenna = false;
				for ( int i = myPlayer.myRC.components().length; --i>= 0;)
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.BLASTER )
						hasBlaster = true;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
					if ( c.type() == ComponentType.ANTENNA )
						hasAntenna = true;
				}
				if ( hasBlaster && hasRadar && hasAntenna )
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(Direction.NORTH); // hard-coded start aids swarming
					currLeader = myPlayer.myRC.getRobot().getID(); // set high to ensure that each robot initially thinks he is his own leader
					obj = MarineBuildOrder.MOVE_OUT;
				}
				return;
	        	
			case MOVE_OUT:	
				
	        	myPlayer.myRC.setIndicatorString(1, "MOVE_OUT");
	        	isLeader = false;
	        	if ( myPlayer.myRC.getRobot().getID() <= currLeader )
	        	{
	        		//Utility.setIndicator(myPlayer, 2, "I'm a leader!");
	        		isLeader = true;
	        	}
	        	//else
	        		//Utility.setIndicator(myPlayer, 2, "Leader is " + Integer.toString(currLeader) + ".");
	        	
	        	
	        	enemyLoc = Utility.attackEnemies(myPlayer);
	        	//Found an enemy
	        	if ( enemyLoc != null && !myPlayer.myRC.getLocation().equals(enemyLoc) )
	        	{
	        		if ( !myPlayer.myMotor.isActive() )
	        		{
	        			if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().opposite()) && myPlayer.myRC.getLocation().distanceSquaredTo(enemyLoc) <= ComponentType.BLASTER.range && myPlayer.myRC.getDirection() == myPlayer.myRC.getLocation().directionTo(enemyLoc) )
	        				myPlayer.myMotor.moveBackward();
	        			else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) && myPlayer.myRC.getLocation().distanceSquaredTo(enemyLoc) > ComponentType.BLASTER.range && myPlayer.myRC.getDirection() == myPlayer.myRC.getLocation().directionTo(enemyLoc) )
	        				myPlayer.myMotor.moveForward();
	        			else if ( myPlayer.myRC.getDirection() != myPlayer.myRC.getLocation().directionTo(enemyLoc) )
	        				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyLoc));
	        			else	
	        				Utility.navStep(myPlayer, nav, enemyLoc);
	        		}
	        	}
	        	//There is no enemy
	        	else
	        	{
		        	if ( Clock.getRoundNum() > Constants.DEBRIS_TIME )
		        		Utility.attackDebris(myPlayer);
		        	if ( isLeader )
		        		Utility.navStep(myPlayer, nav, enemyLocation);
		        	else
		        		Utility.navStep(myPlayer, nav, currLeaderLoc);
	        	}
	        	
	        	// off_map found
	        	if ( myPlayer.myRC.getDirection() == Direction.values()[(2*(spawn/2)+4)%8] && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),4)) == TerrainTile.OFF_MAP ) // 4 is smallest value that works for diagonal directions also
	        	{
	        		offMapFound = true;
	        		oldSpawn = spawn;
	        		spawn = (2*(spawn/2) + 2) % 8; // try a different ORTHOGONAL direction!
	        		enemyLocation = Utility.spawnOpposite(myPlayer.myRC.getLocation(), spawn);
	        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[(spawn+4)%8].toString() + ".");
	        		myPlayer.myMessenger.sendDoubleIntLoc(MsgType.MSG_RERALLY, oldSpawn, spawn, enemyLocation);
	        	}
	        	// Leader Code
	        	else if ( isLeader )
	        	{
	        		if ( realSpawn != -1 )
	        			myPlayer.myMessenger.sendDoubleIntDoubleLoc(MsgType.MSG_REAL_DET_LEADER, spawn, myPlayer.myRC.getRobot().getID(), enemyLocation, myPlayer.myRC.getLocation());
	        		else
	        			myPlayer.myMessenger.sendDoubleIntDoubleLoc(MsgType.MSG_DET_LEADER, spawn, myPlayer.myRC.getRobot().getID(), enemyLocation, myPlayer.myRC.getLocation());
	        	}
	        	else
	        	{
	        		if ( realSpawn != -1 )
	        			myPlayer.myMessenger.sendDoubleIntDoubleLoc(MsgType.MSG_REAL_DET_LEADER, spawn, currLeader, enemyLocation, currLeaderLoc);
	        		else
	        			myPlayer.myMessenger.sendDoubleIntDoubleLoc(MsgType.MSG_DET_LEADER, spawn, currLeader, enemyLocation, currLeaderLoc);
	        	}
	        	currLeader = myPlayer.myRC.getRobot().getID();
	        	return;
	        	
		}
	}
	
	
	
	public String toString()
	{
		return "MarineBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		
		if ( t == MsgType.MSG_DET_LEADER )
		{
			if ( msg.ints[Messenger.firstData+1] < currLeader )
			{
				currLeader = msg.ints[Messenger.firstData+1];
				currLeaderLoc = msg.locations[Messenger.firstData+1];
			}
		}
		
		else if ( t == MsgType.MSG_REAL_DET_LEADER )
		{
			if ( msg.ints[Messenger.firstData+1] < currLeader )
			{
				currLeader = msg.ints[Messenger.firstData+1];
				currLeaderLoc = msg.locations[Messenger.firstData+1];
			}
			if ( realSpawn == -1 )
			{
				realSpawn = msg.ints[Messenger.firstData];
				enemyLocation = msg.locations[Messenger.firstData];
				spawn = realSpawn;
				Utility.setIndicator(myPlayer, 0, "I KNOW we spawned " + Direction.values()[spawn].toString() + ".");
				myPlayer.myMessenger.sendIntLoc(MsgType.MSG_REAL_ENEMY_LOC, realSpawn, enemyLocation);
			}
		}
		
		else if ( t == MsgType.MSG_ENEMY_LOC )
		{
			if ( realSpawn == -1 && spawn == -1 )
			{
				spawn = msg.ints[Messenger.firstData];
				enemyLocation = msg.locations[Messenger.firstData];
				Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ".");
			}
		}
		
		else if ( t == MsgType.MSG_REAL_ENEMY_LOC )
		{
			if ( realSpawn == -1 )
			{
				realSpawn = msg.ints[Messenger.firstData];
				enemyLocation = msg.locations[Messenger.firstData];
				spawn = realSpawn;
				Utility.setIndicator(myPlayer, 0, "I KNOW we spawned " + Direction.values()[spawn].toString() + ".");
				myPlayer.myMessenger.sendIntLoc(MsgType.MSG_REAL_ENEMY_LOC, realSpawn, enemyLocation);
			}
		}
		
		else if ( t == MsgType.MSG_RERALLY )
		{
			if ( msg.ints[Messenger.firstData] == spawn && ( realSpawn == -1 || offMapFound ) )
			{
				spawn = msg.ints[Messenger.firstData + 1];
				enemyLocation = msg.locations[Messenger.firstData];
  				Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[(spawn+4)%8].toString() + ".");
			}
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
}
