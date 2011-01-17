package suboptimal.behaviors;

import suboptimal.*;
import battlecode.common.*;


public class TestHeavyBehavior2 extends Behavior
{
	
	
	private enum TestHeavyBuildOrder2
	{
		EQUIPPING,
		ADVANCE,
		RETREAT
	}
	
	TestHeavyBuildOrder2 obj = TestHeavyBuildOrder2.EQUIPPING;
	
	final OldNavigation nav = new OldNavigation(myPlayer);
	//final JumpNavigation jumpNav = new JumpNavigation(myPlayer);
	
	MapLocation enemyLoc;
	int spawn;
	int rally; // index in Direction.values()
	MapLocation destination;
	
	int num=-1;
	int westEdge = 0;
	int northEdge = 0;
	int eastEdge = 0;
	int southEdge = 0;
	
	boolean hasJump;
	boolean hasRailgun;
	boolean hasRadar;
	int numSMGs;
	int numShields;
	
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
				hasRailgun = false;
				hasRadar = false;
				numSMGs = 0;
				numShields = 0;
				for ( int i = myPlayer.myRC.components().length; --i >= 0;)
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.JUMP )
						hasJump = true;
					if ( c.type() == ComponentType.RAILGUN )
						hasRailgun = true;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
					if ( c.type() == ComponentType.SMG )
						numSMGs++;
					if ( c.type() == ComponentType.SHIELD )
						numShields++;
				}
				if ( hasJump && hasRailgun && hasRadar && numSMGs >= 2 && numShields >= 5 )
				{
					myPlayer.sleep();
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
						Utility.setIndicator(myPlayer, 0, "I KNOW we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
					}
					else
					{
						rally = (3 * num) % 8;
						destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally + 4) % 8);
						Utility.setIndicator(myPlayer, 0, "I don't know where we spawned, heading " + Direction.values()[rally].toString() + "");
					}
					obj = TestHeavyBuildOrder2.ADVANCE;
				}
				return;
	        	
			case ADVANCE:	
				
	        	myPlayer.myRC.setIndicatorString(1, "ADVANCE");
	        	
	        	// Jump if you can
	        	
	        	/*if ( !myPlayer.myJump.isActive() )
		        	myPlayer.myJump.jump(jumpNav.jumpTo(rally));*/
	        	
	        	// Rerally code
	        	
	        	if ( spawn == -1 )
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
						Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
					}
	        	}
	        	else
	        	{
		        	// off_map found in orthogonal direction, try a different ORTHOGONAL direction!
		        	if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[rally],10)) == TerrainTile.OFF_MAP )
		        	{
		        		if ( num % 2 == 0 )
		        			rally = (rally + 2) % 8;
		        		else
		        			rally = (rally - 2) % 8;
		        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        	}
		        	// off_map found in orthogonal direction, try a different ORTHOGONAL direction!
		        	if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[rally],7)) == TerrainTile.OFF_MAP )
		        	{
		        		if ( num % 2 == 0 )
		        			rally = (rally + 3) % 8;
		        		else
		        			rally = (rally - 3) % 8;
		        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        	}
	        	}
	        	
	        	
	        	enemyLoc = Utility.attackEnemies(myPlayer);
	        	//Found an enemy
	        	if ( enemyLoc != null )
	        	{
        			if ( myPlayer.myRC.getLocation().distanceSquaredTo(enemyLoc) <= ComponentType.BLASTER.range )
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy in range, engaging!");
        				if ( !myPlayer.myMotor.isActive() )
        				{
	        				if ( myPlayer.myWeapons[0].withinRange(enemyLoc) )
	        					myPlayer.myMotor.moveBackward();
	        				else
	        					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyLoc));
        				}
        			}
        			else
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy detected, engaging.");
        				if ( !myPlayer.myMotor.isActive() )
        					Utility.navStep(myPlayer, nav, enemyLoc);
        			}
	        	}
	        	//There is no enemy
	        	else
	        	{
	        		Utility.setIndicator(myPlayer, 2, "No enemies nearby, advancing.");
		        	if ( Clock.getRoundNum() > Constants.DEBRIS_TIME )
		        		Utility.attackDebris(myPlayer);
		        	else
		        		Utility.navStep(myPlayer, nav, destination);
	        	}
	        	return;
	        	
		}
	}
	
	
	
	public String toString()
	{
		return "TestHeavyBehavior2";
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
			if ( num == -1 )
			{
				num = msg.ints[Messenger.firstData+1];
				Utility.setIndicator(myPlayer, 2, "I'm heavy " + Integer.toString(num) + "!");
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
