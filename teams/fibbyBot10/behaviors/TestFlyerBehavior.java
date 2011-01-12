package fibbyBot10.behaviors;

import battlecode.common.*;
import fibbyBot10.*;

public class TestFlyerBehavior extends Behavior
{

	TestFlyerBuildOrder obj = TestFlyerBuildOrder.EQUIPPING;

	MapLocation[] farSquares = new MapLocation[4];
	boolean[] offMapFound = new boolean[4];
	
	Mine currMine = null;
	
	int num = -1;
	
	public TestFlyerBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		
		switch ( obj )
		{
			
			case EQUIPPING:
				
				Utility.setIndicator(myPlayer, 1, "EQUIPPING");
				if ( num != -1 )
					obj = TestFlyerBuildOrder.SET_INITIAL_DIR;
				return;
					
			case SET_INITIAL_DIR:
				
				Utility.setIndicator(myPlayer, 1, "SET_INITIAL_DIR");
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(Direction.values()[num%8]);
				obj = TestFlyerBuildOrder.EXPAND;
				return;
				
			case EXPAND:
				
				Utility.setIndicator(myPlayer, 1, "EXPAND");
				Utility.setIndicator(myPlayer, 2, "I'm number " + Integer.toString(num) + "!");
				for ( Mine m : myPlayer.myScanner.detectedMines )
				{
					if ( myPlayer.mySensor.senseObjectAtLocation(m.getLocation(), RobotLevel.ON_GROUND) == null )
					{
						currMine = m;
						obj = TestFlyerBuildOrder.GOTO_MINE;
						return;
					}
				}
				
				farSquares[0] = myPlayer.myRC.getLocation().add(Direction.NORTH, 3);
				farSquares[1] = myPlayer.myRC.getLocation().add(Direction.EAST, 3); // 
				farSquares[2] = myPlayer.myRC.getLocation().add(Direction.SOUTH, 3);
				farSquares[3] = myPlayer.myRC.getLocation().add(Direction.WEST, 3);
				offMapFound[0] = false;
				offMapFound[1] = false;
				offMapFound[2] = false;
				offMapFound[3] = false;
				
				for ( int i = 0 ; i <= 3 ; i++ )
				{
					if ( myPlayer.mySensor.canSenseSquare(farSquares[i]) && myPlayer.myRC.senseTerrainTile(farSquares[i]) == TerrainTile.OFF_MAP )
						offMapFound[i] = true;
				}
				if ( offMapFound[0] || offMapFound[1] || offMapFound[2] || offMapFound[3] )
				{
					obj = TestFlyerBuildOrder.BOUNCE;
				}
				else
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
						myPlayer.myMotor.moveForward();
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
				}
				return;
				
			case BOUNCE:
				
				Utility.setIndicator(myPlayer, 1, "BOUNCE");
				while ( myPlayer.myMotor.isActive() )
					myPlayer.sleep();
				if ( offMapFound[0] ) // north
				{
					if ( myPlayer.myRC.getDirection() == Direction.NORTH )
					{
						if ( num % 2 == 0 )
							myPlayer.myMotor.setDirection(Direction.SOUTH_EAST);
						else if ( num % 2 == 1 )
							myPlayer.myMotor.setDirection(Direction.SOUTH_WEST);
					}
					else if ( myPlayer.myRC.getDirection() == Direction.NORTH_EAST )
						myPlayer.myMotor.setDirection(Direction.SOUTH_EAST);
					else if ( myPlayer.myRC.getDirection() == Direction.NORTH_WEST )
						myPlayer.myMotor.setDirection(Direction.SOUTH_WEST);
				}
				else if ( offMapFound[1] ) // east
				{
					if ( myPlayer.myRC.getDirection() == Direction.EAST )
					{
						if ( num % 2 == 0 )
							myPlayer.myMotor.setDirection(Direction.NORTH_EAST);
						else if ( num % 2 == 1 )
							myPlayer.myMotor.setDirection(Direction.SOUTH_EAST);
					}
					else if ( myPlayer.myRC.getDirection() == Direction.NORTH_EAST )
						myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
					else if ( myPlayer.myRC.getDirection() == Direction.SOUTH_EAST )
						myPlayer.myMotor.setDirection(Direction.SOUTH_WEST);
				}
				else if ( offMapFound[2] ) // south
				{
					if ( myPlayer.myRC.getDirection() == Direction.SOUTH )
					{
						if ( num % 2 == 0 )
							myPlayer.myMotor.setDirection(Direction.NORTH_EAST);
						else if ( num % 2 == 1 )
							myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
					}
					else if ( myPlayer.myRC.getDirection() == Direction.SOUTH_EAST )
						myPlayer.myMotor.setDirection(Direction.NORTH_EAST);
					else if ( myPlayer.myRC.getDirection() == Direction.SOUTH_WEST )
						myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
				}
				else if ( offMapFound[3] ) // west
				{
					if ( myPlayer.myRC.getDirection() == Direction.WEST )
					{
						if ( num % 2 == 0 )
							myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
						else if ( num % 2 == 1 )
							myPlayer.myMotor.setDirection(Direction.SOUTH_WEST);
					}
					else if ( myPlayer.myRC.getDirection() == Direction.NORTH_WEST )
						myPlayer.myMotor.setDirection(Direction.NORTH_EAST);
					else if ( myPlayer.myRC.getDirection() == Direction.SOUTH_WEST )
						myPlayer.myMotor.setDirection(Direction.SOUTH_EAST);
				}
				obj = TestFlyerBuildOrder.EXPAND;
					
				return;
				
			case GOTO_MINE:
				
				Utility.setIndicator(myPlayer, 1, "GOTO_MINE");
				if ( myPlayer.mySensor.senseObjectAtLocation(currMine.getLocation(), RobotLevel.ON_GROUND) != null )
				{
					obj = TestFlyerBuildOrder.EXPAND;
					return;
				}
				else if ( myPlayer.myRC.getLocation().distanceSquaredTo(currMine.getLocation()) <= 2 )
				{
					obj = TestFlyerBuildOrder.BUILD_REFINERY;
					return;
				}
				else
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					if ( myPlayer.myRC.getDirection() != myPlayer.myRC.getLocation().directionTo(currMine.getLocation()) )
						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(currMine.getLocation()));
					else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
						myPlayer.myMotor.moveForward();
				}
				return;
				
			case BUILD_REFINERY:
				
				Utility.setIndicator(myPlayer, 1, "BUILD_REFINERY");
				if ( myPlayer.mySensor.senseObjectAtLocation(currMine.getLocation(), RobotLevel.ON_GROUND) != null )
				{
					obj = TestFlyerBuildOrder.EXPAND;
					return;
				}
				else if ( myPlayer.myRC.getTeamResources() > Chassis.BUILDING.cost + ComponentType.RECYCLER.cost + Constants.RESERVE )
				{
					Utility.buildChassis(myPlayer, myPlayer.myRC.getLocation().directionTo(currMine.getLocation()), Chassis.BUILDING);
					Utility.buildComponent(myPlayer, myPlayer.myRC.getLocation().directionTo(currMine.getLocation()), ComponentType.RECYCLER, RobotLevel.ON_GROUND);
					obj = TestFlyerBuildOrder.EXPAND;
				}
				return;
				
		}
	}

	public String toString()
	{
		return "TestFlyerBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{

	}


	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM )
		{
			if ( num == -1 )
				num = msg.ints[Messenger.firstData];
		}
	}
	public void onWakeupCallback(int lastActiveRound) {}
	public void onDamageCallback(double damageTaken) {}
}
