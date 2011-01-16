package suboptimal.behaviors;

import suboptimal.*;
import battlecode.common.*;


public class TestHeavyBehavior2 extends Behavior
{
	
	
	private enum TestHeavyBuildOrder2
	{
		EQUIPPING,
		MOVE_OUT
	}
	
	TestHeavyBuildOrder2 obj = TestHeavyBuildOrder2.EQUIPPING;
	
	final OldNavigation nav = new OldNavigation(myPlayer);
	
	MapLocation enemyLoc;
	int spawn;
	int rally; // index in Direction.values()
	MapLocation destination;
	
	int num;
	int westEdge = 0;
	int northEdge = 0;
	int eastEdge = 0;
	int southEdge = 0;
	
	boolean hasJump;
	boolean hasSatellite;
	boolean hasRegen;
	int numBlasters;
	
	public TestHeavyBehavior2(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
				hasJump = false;
				hasSatellite = false;
				hasRegen = false;
				int numBlasters = 0;
				for ( int i = myPlayer.myRC.components().length; --i>= 0;)
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.JUMP )
						hasJump = true;
					if ( c.type() == ComponentType.SATELLITE )
						hasSatellite = true;
					if ( c.type() == ComponentType.REGEN )
						hasRegen = true;
					if ( c.type() == ComponentType.BLASTER )
						numBlasters++;
				}
				if ( hasJump && hasSatellite && hasRegen && numBlasters >= 2 )
				{
					while ( myPlayer.mySensor.isActive() )
						myPlayer.sleep();
					if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.WEST, 10)) == TerrainTile.OFF_MAP )
						westEdge = 1;
					if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.NORTH, 10)) == TerrainTile.OFF_MAP )
						northEdge = 1;
					if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.EAST, 10)) == TerrainTile.OFF_MAP )
						eastEdge = 1;
					if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.SOUTH, 10)) == TerrainTile.OFF_MAP )
						southEdge = 1;
					spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
					if ( spawn != -1 )
					{
						rally = (spawn + 4) % 8;
						destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally + 4) % 8);
					}
					else
					{
						rally = (3 * num) % 8;
						destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally + 4) % 8);
					}
					obj = TestHeavyBuildOrder2.MOVE_OUT;
				}
				return;
	        	
			case MOVE_OUT:	
				
	        	myPlayer.myRC.setIndicatorString(1, "MOVE_OUT");
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
		        	else
		        		Utility.navStep(myPlayer, nav, destination);
	        	}
	        	
	        	// off_map found in orthogonal direction
	        	if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[rally],10)) == TerrainTile.OFF_MAP )
	        	{
	        		rally = (rally + 2) % 8; // try a different ORTHOGONAL direction!
	        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
	        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
	        	}
	        	// off_map found in diagonal direction
	        	if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[rally],7)) == TerrainTile.OFF_MAP )
	        	{
	        		rally = (rally + 3) % 8; // try a different ORTHOGONAL direction!
	        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
	        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
	        	}
	        	return;
	        	
		}
	}
	
	
	
	public String toString()
	{
		return "TestHeavyBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM_HEAVY )
		{
			num = msg.ints[Messenger.firstData+1];
			Utility.setIndicator(myPlayer, 2, "I'm heavy " + Integer.toString(num) + "!");
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
}
