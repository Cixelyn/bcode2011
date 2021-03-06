package fibbyBot11;

import battlecode.common.*;
import java.util.*;


/**
 * The utility class does a few useful conversions and calculations
 * @author Cory
 *
 */

public class Utility
{
	
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
	 * Looks for healed allies and heals one if one is found
	 * @author JVen
	 * @param myPlayer The robot player
	 * @param nearbyRobots A list of RobotInfos of nearby robots
	 * @return The location of a harmed enemy, or null if none are found
	 */
	
	public static MapLocation healAllies(RobotPlayer myPlayer, ArrayList<RobotInfo> nearbyRobots) throws Exception
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
				if ( gun.type() == ComponentType.MEDIC && r.getTeam() == myPlayer.myRC.getTeam() )
				{
					if ( rInfo.chassis != Chassis.BUILDING )
						destination = rInfo.location;
					if(!gun.isActive() && rInfo.hitpoints < rInfo.maxHp && gun.withinRange(rInfo.location))
					{
						gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
					}
				}
			}
    	}
    	return destination;
	}
	
	/**
	 * Looks for enemies and shoots at one if one is found
	 * @author JVen
	 * @param myPlayer The robot player
	 * @param nearbyRobots A list of RobotInfos of nearby robots
	 * @return The location of a sensed enemy, or null if none are found
	 */
	
	public static MapLocation attackEnemies(RobotPlayer myPlayer, ArrayList<RobotInfo> nearbyRobots) throws Exception
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
				if ( gun.type() != ComponentType.MEDIC && r.getTeam() == myPlayer.myRC.getTeam().opponent() )
				{
					destination = rInfo.location;
					if(!gun.isActive() && rInfo.hitpoints > 0 && gun.withinRange(rInfo.location))
					{
						gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
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
	
	public static MapLocation attackDebris(RobotPlayer myPlayer, ArrayList<RobotInfo> nearbyRobots) throws Exception
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
	 * Shoot itself with a medic gun
	 * @author JVen
	 * @param myPlayer The robot player
	 */
	
	public static void healSelf(RobotPlayer myPlayer) throws Exception
	{
		WeaponController gun;
		
		for ( Object c : myPlayer.myWeapons )
		{
			gun = (WeaponController) c;
			if ( gun.type() == ComponentType.MEDIC )
			{
				if(!gun.isActive() && myPlayer.myRC.getHitpoints() < myPlayer.myRC.getMaxHp())
				{
					gun.attackSquare(myPlayer.myRC.getLocation(), myPlayer.myRC.getRobot().getRobotLevel());
				}
			}
		}
	}
	
	/**
	 * Uses black magic to determine spawn location based on whether off map squares are found in each direction
	 * @author JVen
	 * @param westEdge 1 if an off_map square is found to the west, 0 otherwise
	 * @param northEdge 1 if an off_map square is found to the north, 0 otherwise
	 * @param eastEdge 1 if an off_map square is found to the east, 0 otherwise
	 * @param southEdge 1 if an off_map square is found to the south, 0 otherwise
	 * @return Spawn location: 0 is west, increments clockwise
	 */
	public static int getSpawn(int westEdge, int northEdge, int eastEdge, int southEdge)
	{
		switch ((westEdge+1)*(2*northEdge+1)*(4*eastEdge+1)*(6*southEdge+1))
		{
			case 2:
				return 0;
			case 3:
				return 2;
			case 5:
				return 4;
			case 7:
				return 6;
			case 6:
				return 1;
			case 14:
				return 7;
			case 15:
				return 3;
			case 35:
				return 5;
		}
		return 8; // center spawn
	}
	
	/**
	 * Outputs enemy location based on int returned from getSpawn
	 * @author JVen
	 * @param hometown The location the spawn spawned
	 * @param spawn The region of the spawn
	 * @return The location of the enemy spawn
	 */
	public static MapLocation spawnOpposite(MapLocation hometown, int spawn)
	{
		switch (spawn)
		{
			case 2:
			return hometown.add(Direction.SOUTH, GameConstants.MAP_MAX_HEIGHT);
			case 4:
			return hometown.add(Direction.WEST, GameConstants.MAP_MAX_WIDTH);
			case 6:
			return hometown.add(Direction.NORTH, GameConstants.MAP_MAX_HEIGHT);
			case 0:
			return hometown.add(Direction.EAST, GameConstants.MAP_MAX_WIDTH);
			case 1:
			return hometown.add(Direction.SOUTH_EAST, Constants.MAP_MAX_SIZE);
			case 3:
			return hometown.add(Direction.SOUTH_WEST, Constants.MAP_MAX_SIZE);
			case 7:
			return hometown.add(Direction.NORTH_EAST, Constants.MAP_MAX_SIZE);
			case 5:
			return hometown.add(Direction.NORTH_WEST, Constants.MAP_MAX_SIZE);
			case 8:
			return hometown;
		}
		return null; // center spawn
	}
	
	/**
	 * Return string based on spawn location
	 * @author JVen
	 * @param spawn The region of the spawn
	 * @return String specifying starting spawn region
	 */
	
	public static String spawnString(int spawn)
	{
		switch (spawn)
		{
			case 0:
				return "west";
			case 1:
				return "northwest";
			case 2:
				return "north";
			case 3:
				return "northeast";
			case 4:
				return "east";
			case 5:
				return "southeast";
			case 6:
				return "south";
			case 7:
				return "southwest";
		}
		return "center";
	}
	
	//Max's Go here
	
	
	
	
	
	//Cory's Go here
	
	
	
	
	
}


