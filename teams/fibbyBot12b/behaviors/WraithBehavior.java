package fibbyBot12b.behaviors;

import fibbyBot12b.*;
import battlecode.common.*;


public class WraithBehavior extends Behavior
{
	
	
	private enum WraithBuildOrder
	{
		EQUIPPING,
		DETERMINE_SPAWN,
		ADVANCE
	}
	
	WraithBuildOrder obj = WraithBuildOrder.EQUIPPING;
	
	int spawn;
	int rally; // index in Direction.values()
	RobotInfo enemyInfo;
	MapLocation destination;
	
	int num = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int westEdge = -1;
	
	boolean hasBlaster;
	boolean hasRadar;
	
	int numBounces = 0;
	int stepsOffDir = 0;
	
	public WraithBehavior(RobotPlayer player)
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
				for ( int i = myPlayer.myRC.components().length; --i >= 0;)
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.BLASTER )
						hasBlaster = true;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
				}
				if ( hasBlaster && hasRadar && num != -1 )
					obj = WraithBuildOrder.DETERMINE_SPAWN;
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
					destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally + 4) % 8);
					Utility.setIndicator(myPlayer, 0, "I KNOW we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
				}
				else
				{
					rally = (2 * num + 1) % 8;
					destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally + 4) % 8);
					Utility.setIndicator(myPlayer, 0, "I don't know where we spawned, heading " + Direction.values()[rally].toString() + ".");
				}
				obj = WraithBuildOrder.ADVANCE;
				return;
	        	
			case ADVANCE:	
				
	        	myPlayer.myRC.setIndicatorString(1, "ADVANCE");
	        	
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
			        			rally = (rally - 2) % 8;
		        		}
		        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
		        	// off_map found in orthogonal direction with diagonal rally, try a different ORTHOGONAL direction!
		        	if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 1) % 8;
		        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
		        	// off_map found in orthogonal direction with diagonal rally, try a different ORTHOGONAL direction!
		        	if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally - 1) % 8;
		        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
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
			        			rally = (rally - 2) % 8;
		        		}
		        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
	        		if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 3) % 8;
		        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
		        	if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
		        	{
		        		rally = (rally + 5) % 8;
		        		destination = Utility.spawnOpposite(myPlayer.myRC.getLocation(), (rally+4)%8);
		        		Utility.setIndicator(myPlayer, 0, "Rerallying " + Direction.values()[rally].toString() + ".");
		        		numBounces++;
		        	}
	        	}
	        	
	        	
	        	enemyInfo = Utility.attackEnemies(myPlayer);
	        	//Found an enemy
	        	if ( enemyInfo != null )
	        	{
        			if ( myPlayer.myRC.getLocation().distanceSquaredTo(enemyInfo.location) <= ComponentType.BLASTER.range )
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy in range, retreating!");
        				if ( !myPlayer.myMotor.isActive() )
        				{
	        				if ( myPlayer.myWeapons[0].withinRange(enemyInfo.location) && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().opposite()))
	        					myPlayer.myMotor.moveBackward();
	        				else
	        					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyInfo.location));
        				}
        			}
        			else
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy detected, engaging.");
        				if ( !myPlayer.myMotor.isActive() )
        				{
        					if ( myPlayer.myRC.getDirection() == myPlayer.myRC.getLocation().directionTo(enemyInfo.location) && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
        						myPlayer.myMotor.moveForward();
        					else if ( myPlayer.myRC.getDirection() != myPlayer.myRC.getLocation().directionTo(enemyInfo.location) )
        						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyInfo.location));
        				}
        			}
	        	}
	        	//There is no enemy
	        	else
	        	{
	        		Utility.setIndicator(myPlayer, 2, "No enemies nearby, advancing.");
		        	if ( !myPlayer.myMotor.isActive() )
		        	{
		        		if ( myPlayer.myRC.getDirection() == Direction.values()[rally] )
		        		{
		        			if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
		        			{
		        				myPlayer.myMotor.moveForward();
		        				stepsOffDir = 0;
		        			}
		        			else
		        			{
		        				if ( num % 2 == 0 )
		        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
		        				if ( num % 2 == 1 )
		        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		        				stepsOffDir++;
		        			}
		        		}
		        		else
		        		{
		        			if ( stepsOffDir < 3 )
		        			{
		        				if ( myPlayer.myMotor.canMove(Direction.values()[rally]) )
		        				{
		        					myPlayer.myMotor.setDirection(Direction.values()[rally]);
			        				stepsOffDir = 0;
		        				}
		        				else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
			        			{
			        				myPlayer.myMotor.moveForward();
			        				stepsOffDir++;
			        			}
			        			else
			        			{
			        				if ( num % 2 == 0 )
			        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
			        				if ( num % 2 == 1 )
			        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
			        				stepsOffDir++;
			        			}
		        			}
		        			else
		        			{
		        				myPlayer.myMotor.setDirection(Direction.values()[rally]);
		        				stepsOffDir = 0;
		        			}
		        		}
		        	}
	        	}
	        	return;
	        	
		}
	}
	
	
	
	public String toString()
	{
		return "WraithBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM_FLYER )
		{
			if ( num == -1 )
			{
				num = msg.ints[Messenger.firstData+1];
				Utility.setIndicator(myPlayer, 2, "I'm wraith " + Integer.toString(num) + "!");
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
