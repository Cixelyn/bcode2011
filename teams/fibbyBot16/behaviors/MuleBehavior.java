package fibbyBot16.behaviors;

import battlecode.common.*;
import fibbyBot16.*;

/**
 *
 * @author FiBsTeR
 *
 */

public class MuleBehavior extends Behavior
{
	
	private enum MuleBuildOrder 
	{
		INITIALIZE,
		BUILD_CAMP,
		SLEEP,
		SUICIDE
	}
	
	MuleBuildOrder obj = MuleBuildOrder.INITIALIZE;
	
	public MuleBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case INITIALIZE:
				
				Utility.setIndicator(myPlayer, 0, "INITIALIZE");
				while ( myPlayer.myBuilder.isActive() )
					myPlayer.sleep();
				obj = MuleBuildOrder.BUILD_CAMP;
				return;
				
			case BUILD_CAMP:
				
				Utility.setIndicator(myPlayer, 0, "BUILD_CAMP");
				
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.ARMORY, RobotLevel.ON_GROUND);
				
				turn(Direction.NORTH);
				forward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.SOUTH_WEST, Chassis.BUILDING);
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				
				turn(Direction.SOUTH_EAST);
				forward();
				forward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.NORTH_WEST, Chassis.BUILDING);
				Utility.buildChassis(myPlayer, Direction.WEST, Chassis.BUILDING);
				
				turn(Direction.SOUTH_EAST);
				forward();
				turn(Direction.SOUTH);
				forward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				turn(Direction.SOUTH_EAST);
				backward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.SOUTH_EAST, Chassis.BUILDING);
				Utility.buildComponent(myPlayer, Direction.SOUTH_EAST, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
				
				turn(Direction.SOUTH_WEST);
				forward();
				turn(Direction.WEST);
				forward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.NORTH_EAST, Chassis.BUILDING);
				Utility.buildChassis(myPlayer, Direction.NORTH, Chassis.BUILDING);
				
				turn(Direction.NORTH_WEST);
				forward();
				forward();
				myPlayer.sleep();
				
				Utility.buildChassis(myPlayer, Direction.SOUTH_EAST, Chassis.BUILDING);
				Utility.buildChassis(myPlayer, Direction.EAST, Chassis.BUILDING);
				
				obj = MuleBuildOrder.SUICIDE;
				return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 0, "SLEEP");
				Utility.setIndicator(myPlayer, 1, "zzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
			case SUICIDE:
				
				Utility.setIndicator(myPlayer, 0, "SUICIDE");
				Utility.setIndicator(myPlayer, 1, ":(");
				myPlayer.sleep();
				myPlayer.myRC.suicide();
				return;
				
		}
    	
	}
	
	public void turn(Direction d) throws Exception
	{
		if ( myPlayer.myRC.getDirection() != d )
		{
			while ( myPlayer.myMotor.isActive() )
				myPlayer.sleep();
			myPlayer.myMotor.setDirection(d);
		}
	}
	
	public void forward() throws Exception
	{
		while ( myPlayer.myMotor.isActive() )
			myPlayer.sleep();
		myPlayer.myMotor.moveForward();
	}
	
	public void backward() throws Exception
	{
		while ( myPlayer.myMotor.isActive() )
			myPlayer.sleep();
		myPlayer.myMotor.moveBackward();
	}
	
	public String toString()
	{
		return "MuleBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{

	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}

}
