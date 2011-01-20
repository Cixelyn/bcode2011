package team068b.behaviors;

import team068b.*;
import battlecode.common.*;

public class MarineBehavior extends Behavior
{
	final OldNavigation nav = new OldNavigation(myPlayer);
	
	MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	MapLocation enemyLoc;
	MapLocation debrisLoc;
	
	boolean hasBlaster;
	boolean hasRadar;
	boolean hasShield;
	
	MapLocation enemyLocation;
	int spawn = -1;
	
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
				hasShield = false;
				for ( int i = myPlayer.myRC.components().length - 1 ; i >= 0 ; i-- )
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.BLASTER )
						hasBlaster = true;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
					if ( c.type() == ComponentType.SHIELD )
						hasShield = true;
				}
				if ( hasBlaster && hasRadar && hasShield )
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(Direction.NORTH); // hard-coded start aids swarming
					obj = MarineBuildOrder.MOVE_OUT;
				}
				return;
	        	
			case MOVE_OUT:	
				
	        	myPlayer.myRC.setIndicatorString(1, "MOVE_OUT");
	        	enemyLoc = Utility.attackEnemies(myPlayer);
	        	if ( enemyLoc != null )
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
	        	else
	        	{
		        	if ( Clock.getRoundNum() > Constants.DEBRIS_TIME )
		        		Utility.attackDebris(myPlayer);
	        		if ( spawn != -1 )
	        			Utility.navStep(myPlayer, nav, enemyLocation);
	        		else
	        			Utility.bounceNav(myPlayer);
	        	}
	        	if ( spawn != -1 && myPlayer.myRC.getDirection() == Direction.values()[(2*(spawn/2)+4)%8] && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),4)) == TerrainTile.OFF_MAP ) // 4 is smallest value that works for diagonal directions also
	        	{
	        		spawn = (2*(spawn/2) + 2) % 8; // try a different ORTHOGONAL direction!
	        		enemyLocation = Utility.spawnOpposite(myPlayer.myRC.getLocation(), spawn);
	        		Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ".");
	        	}
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
		if ( t == MsgType.MSG_ENEMY_LOC )
		{
			if ( msg.ints[Messenger.firstData] != -1 )
			{
				spawn = msg.ints[Messenger.firstData];
				enemyLocation = msg.locations[Messenger.firstData];
				if ( spawn != -1 )
					Utility.setIndicator(myPlayer, 0, "I think we spawned " + Direction.values()[spawn].toString() + ".");
				else
					Utility.setIndicator(myPlayer, 0, "I think we spawned center.");
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
