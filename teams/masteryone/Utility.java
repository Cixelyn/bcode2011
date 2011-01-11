package masteryone;

import battlecode.common.*;



/**
 * The utility class does a few useful conversions and calculations
 * @author Cory
 *
 */

public class Utility {
	
	public static void println(String s)
	{
		if ( Constants.DEBUG )
			System.out.println(s);
	}
	
	public static void setIndicator(RobotPlayer player, int index, String s)
	{
		if ( Constants.DEBUG )
			player.myRC.setIndicatorString(index, s);
	}
	
	//Justin's Go Here
	
	
	/**
	 * Given an instance of bugNav and a non-null destination, navStep turns in the appropriate direction then moves forward.
	 * @author JVen
	 * @param myPlayer The robot player
	 * @param nav An instance of bugNav
	 * @param dest The destination to step towards
	 */
	
	public static void navStep(RobotPlayer myPlayer, OldNavigation nav, MapLocation dest) throws Exception
	{
		if (dest != null)
		{
			Direction direction = nav.bugTo(dest);
			if(direction != Direction.OMNI && direction != Direction.NONE)
			{
				while(myPlayer.myMotor.isActive())
					myPlayer.sleep();
				myPlayer.myMotor.setDirection(direction);
				while(myPlayer.myMotor.isActive() || !myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
					myPlayer.sleep();
				myPlayer.myMotor.moveForward();
			}
			else
			{
				//System.out.println("OMNI or NONE direction encountered.");
			}
		}
		else
		{
			//System.out.println("Null destination encountered.");
		}
	}
	
	/**
	 * Yields until there is enough money to build a chassis, then builds the chassis in the given direction, if the square is free
	 * @author JVen
	 * @param player The robot player
	 * @param dir The direction in which to build
	 * @param chassis The chassis type to build
	 * @return boolean True if chassis is built, false otherwise
	 */
	
	public static void buildChassis(RobotPlayer player, Direction dir, Chassis chassis) throws Exception
	{
		MapLocation loc = player.myRC.getLocation().add(dir);
		while ( player.myRC.getTeamResources() < chassis.cost || player.myBuilder.isActive() )
			player.sleep();
		player.myBuilder.build(chassis, loc);
	}

	/**
	 * Yields until there is enough money to build a component, then builds the component in the given direction, if an allied robot is there
	 * @author JVen
	 * @param player The robot player
	 * @param dir The direction in which to build
	 * @param component The component type to build
	 * @param level The robot level of the robot on which to build
	 * @return boolean True if component is built, false otherwise
	 */
	
	public static void buildComponent(RobotPlayer player, Direction dir, ComponentType component, RobotLevel level) throws Exception
	{
		MapLocation loc = player.myRC.getLocation().add(dir);
		while ( player.myRC.getDirection() != dir )
		{
			player.sleep();
			if ( !player.myMotor.isActive() )
				player.myMotor.setDirection(dir);
		}
		while ( player.myRC.getTeamResources() < component.cost || player.myBuilder.isActive() )
			player.sleep();
		player.myBuilder.build(component, loc, level);
	}
	
	/**
	 * Yields until there is enough money to build a component, then builds the component on self
	 * @author JVen
	 * @param player The robot player
	 * @param component The component type to build
	 * @return boolean True if component is built, false otherwise
	 */
	
	public static boolean buildComponentOnSelf(RobotPlayer player, ComponentType component) throws Exception
	{
		MapLocation loc = player.myRC.getLocation();
		while ( player.myRC.getTeamResources() < component.cost || player.myBuilder.isActive() )
			player.sleep();
		player.myBuilder.build(component, loc, player.myRC.getRobot().getRobotLevel());
		return true;
	}
	
	//Max's Go here
	
	
	
	
	
	//Cory's Go here
	
	
	
	
	
}


