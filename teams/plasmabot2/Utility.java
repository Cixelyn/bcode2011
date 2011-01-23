package plasmabot2;

import battlecode.common.*;
import java.util.*;


/**
 * The utility class does a few useful conversions and calculations
 * @author Cory
 *
 */

public class Utility {
	
	public static final Random rand = new Random();
	
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
	 * If the ground robot myPlayer can move forward, it does so. Otherwise, it picks a random direction and turns.
	 * @author JVen
	 * @param myPlayer The ground robot
	 */
	
	public static void bounceNav(RobotPlayer myPlayer) throws Exception
	{
		int random = rand.nextInt(10);
		if (!myPlayer.myMotor.isActive())
		{
			if(myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
				myPlayer.myMotor.moveForward();
			else
			{
				if (random == 0 || random == 1 || random == 2)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 3)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 4)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 5 || random == 6 || random == 7)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 8)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 9)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
			}
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
		MapLocation loc;
		if ( dir != Direction.OMNI && dir != Direction.NONE )
		{
			loc = player.myRC.getLocation().add(dir);
			while ( player.myRC.getDirection() != dir )
			{
				player.sleep();
				if ( !player.myMotor.isActive() )
					player.myMotor.setDirection(dir);
			}
		}
		else
			loc = player.myRC.getLocation();
		while ( player.myRC.getTeamResources() < component.cost || player.myBuilder.isActive() )
			player.sleep();
		player.myBuilder.build(component, loc, level);
	}
	
	/**
	 * Looks for enemies and shoots at one if one is found
	 * @author JVen
	 * @param myPlayer The robot player
	 * @param nearbyRobots A list of RobotInfos of nearby robots
	 * @return The location of a sensed enemy, or null if none are found
	 */
	
	public static MapLocation senseEnemies(RobotPlayer myPlayer, ArrayList<RobotInfo> nearbyRobots) throws Exception
	{
		WeaponController gun;
		Robot r;
		MapLocation destination = null;
		
    	for ( RobotInfo rInfo : nearbyRobots )
    	{
    		r = rInfo.robot;
			for ( Object c : myPlayer.myWeapons )
			{
				gun = (WeaponController) c;
				if ( r.getTeam() == myPlayer.myRC.getTeam().opponent() )
				{
					if(!gun.isActive() && rInfo.hitpoints > 0 && gun.withinRange(rInfo.location))
					{
						gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
					 	destination = rInfo.location;
					}
				}
			}
    	}
    	return destination;
	}
	
	/**
	 * Looks for debris and shoots at one if one is found
	 * @author JVen
	 * @param myPlayer The robot player
	 * @param nearbyRobots A list of RobotInfos of nearby robots
	 * @return The location of a sensed debris, or null if none are found
	 */
	
	public static MapLocation senseDebris(RobotPlayer myPlayer, ArrayList<RobotInfo> nearbyRobots) throws Exception
	{
		WeaponController gun;
		Robot r;
		MapLocation destination = null;
		
    	for ( RobotInfo rInfo : nearbyRobots )
    	{
    		r = rInfo.robot;
			for ( Object c : myPlayer.myWeapons )
			{
				gun = (WeaponController) c;
				if ( r.getTeam() == Team.NEUTRAL )
				{
				 	destination = rInfo.location;
					if(!gun.isActive() && rInfo.hitpoints > 0 && gun.withinRange(rInfo.location))
						gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
				}
			}
    	}
    	return destination;
	}
	
	//Max's Go here
	
	
	
	
	
	//Cory's Go here
	
	
	
	
	
}


