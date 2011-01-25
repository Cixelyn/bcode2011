package fibbyBot14;

import battlecode.common.*;

import java.util.*;


/**
 * The utility class does a few useful conversions and calculations
 * @author Cory
 *
 */

public class Utility
{
	
	
	public static void printMsg(RobotPlayer player, String s)
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
	 * Yields until there is enough money to build a chassis, then builds the chassis in the given direction, if the square is free
	 * @author JVen
	 * @param player The robot player
	 * @param dir The direction in which to build
	 * @param chassis The chassis type to build
	 */
	
	public static void buildChassis(RobotPlayer player, Direction dir, Chassis chassis) throws Exception
	{
		MapLocation loc = player.myRC.getLocation().add(dir);
		if ( dir != Direction.OMNI && dir != Direction.NONE )
			loc = player.myRC.getLocation().add(dir);
		else
			loc = player.myRC.getLocation();
		while ( player.myRC.getTeamResources() < chassis.cost + Constants.RESERVE || player.myBuilder.isActive() )
			player.sleep();
		if ( player.mySensor != null && player.mySensor.withinRange(loc) && player.mySensor.senseObjectAtLocation(loc, chassis.level) != null )
			return;
		player.myBuilder.build(chassis, loc);
		return;
	}

	/**
	 * Yields until there is enough money to build a component, then builds the component in the given direction, if an allied robot is there
	 * @author JVen
	 * @param player The robot player
	 * @param dir The direction in which to build
	 * @param component The component type to build
	 * @param level The robot level of the robot on which to build
	 */
	
	public static void buildComponent(RobotPlayer player, Direction dir, ComponentType component, RobotLevel level) throws Exception
	{
		MapLocation loc;
		if ( dir != Direction.OMNI && dir != Direction.NONE )
		{
			loc = player.myRC.getLocation().add(dir);
			if ( player.myRC.getDirection() != dir )
			{
				while ( player.myMotor.isActive() )
					player.sleep();
				player.myMotor.setDirection(dir);
			}
		}
		else
			loc = player.myRC.getLocation();
		while ( player.myRC.getTeamResources() < component.cost + Constants.RESERVE || player.myBuilder.isActive() )
			player.sleep();
		if ( player.mySensor != null && player.mySensor.withinRange(loc) )
		{
			Robot r = (Robot)player.mySensor.senseObjectAtLocation(loc, level);
			if ( r == null || r.getTeam() != player.myRC.getRobot().getTeam() )
				return;
			else
			{
				RobotInfo rInfo = player.mySensor.senseRobotInfo(r);
				if ( totalWeight(rInfo.components) + component.weight > rInfo.chassis.weight )
					return;
			}
		}
		player.myBuilder.build(component, loc, level);
		return;
	}
	
	/**
	 * Looks for healed allies and heals one if one is found
	 * @author JVen
	 * @param myPlayer The robot player
	 * @param nearbyRobots A list of RobotInfos of nearby robots
	 * @return The location of a harmed enemy, or null if none are found
	 */
	
	public static MapLocation healAllies(RobotPlayer myPlayer) throws Exception
	{
		WeaponController gun;
		RobotInfo rInfo;
		MapLocation destination = null;
		Robot r;
		
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
		
    	for ( int i = nearbyRobots.length; --i>=0;)
    	{
    		r = nearbyRobots[i];
			for ( int j = myPlayer.myMedics.length; --j>= 0;)
			{
				gun = myPlayer.myMedics[j];
				if ( r.getTeam() == myPlayer.myRC.getTeam() )
				{
					rInfo = myPlayer.mySensor.senseRobotInfo(r);
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
	 * Looks for enemies and shoots at the closest one if one is found
	 * NOTE: As of now, it does not re-sense when firing to determine killshots.
	 * This DOES require rescanning since trying to compute without a satellite
	 * ignores armor
	 * @author JVen
	 * @param myPlayer The robot player
	 * @param nearbyRobots A list of RobotInfos of nearby robots
	 * @return The location of a sensed enemy, or null if none are found
	 */
	
	public static RobotInfo attackEnemies(RobotPlayer myPlayer) throws Exception
	{
		WeaponController gun;
		RobotInfo rInfo;
		Robot r;
		
		RobotInfo enemyMin1 = null;
		RobotInfo enemyMin2 = null;
		int minDist1 = 9998; // sentinel value
		int minDist2 = 9999; // sentinel value
		
		MapLocation myLoc = myPlayer.myRC.getLocation();
		
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
		
		for ( int i = nearbyRobots.length; --i>=0;)
    	{
    		r = nearbyRobots[i];
    		if ( r.getTeam() == myPlayer.myRC.getTeam().opponent() )
    		{
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
    			int dist = myLoc.distanceSquaredTo(rInfo.location);
    			
    			if ( dist < minDist1 )
    			{
    				if ( enemyMin2 == null )
    				{
    					minDist1 = dist;
    					minDist2 = dist;
    					enemyMin1 = rInfo;
    					enemyMin2 = rInfo;
    				}
    				else
    				{
    					minDist2 = minDist1;
    					minDist1 = dist;
    					enemyMin2 = enemyMin1;
    					enemyMin1 = rInfo;
    				}
    			}
    			else if ( dist < minDist2 )
    			{
    				minDist2 = myLoc.distanceSquaredTo(rInfo.location);
					enemyMin2 = rInfo;
    			}
    		}
    	}
		if ( enemyMin1 != null )
		{
			for ( int j = myPlayer.mySMGs.length; --j >= 0 ; )
			{
				gun =  myPlayer.mySMGs[j];
				if ( !gun.isActive() && gun.withinRange(enemyMin1.location))
					gun.attackSquare(enemyMin1.location, enemyMin1.robot.getRobotLevel());
			}
			for ( int j = myPlayer.myBlasters.length; --j >= 0 ; )
			{
				gun =  myPlayer.myBlasters[j];
				if ( !gun.isActive() && gun.withinRange(enemyMin1.location))
					gun.attackSquare(enemyMin1.location, enemyMin1.robot.getRobotLevel());
			}
			for ( int j = myPlayer.myRailguns.length; --j >= 0 ; )
			{
				gun =  myPlayer.myRailguns[j];
				if ( !gun.isActive() && gun.withinRange(enemyMin1.location))
					gun.attackSquare(enemyMin1.location, enemyMin1.robot.getRobotLevel());
			}
			for ( int j = myPlayer.myBeams.length; --j >= 0 ; )
			{
				gun =  myPlayer.myBeams[j];
				if ( !gun.isActive() && gun.withinRange(enemyMin1.location))
					gun.attackSquare(enemyMin1.location, enemyMin1.robot.getRobotLevel());
			}
			for ( int j = myPlayer.myHammers.length; --j >= 0 ; )
			{
				gun =  myPlayer.myHammers[j];
				if ( !gun.isActive() && gun.withinRange(enemyMin1.location) && enemyMin1.robot.getRobotLevel() == RobotLevel.ON_GROUND )
					gun.attackSquare(enemyMin1.location, RobotLevel.ON_GROUND);
			}
		}
		return enemyMin1;
			
	}
	
	
	
	/**
	 * Looks for debris and shoots at the closest one if one is found
	 * @author JVen
	 * @param myPlayer The robot player
	 * @param nearbyRobots A list of RobotInfos of nearby robots
	 * @return The location of a sensed debris, or null if none are found
	 */
	
	public static MapLocation attackDebris(RobotPlayer myPlayer) throws Exception
	{
		WeaponController gun;
		RobotInfo rInfo;
		Robot r;
		
		RobotInfo rockMin = null;
		int minDist = 9999; // sentinel value
		
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
		
		for ( int i = nearbyRobots.length; --i>=0;)
    	{
    		r = nearbyRobots[i];
    		if ( r.getTeam() == Team.NEUTRAL )
    		{
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
    			if ( myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location) < minDist )
    			{
    				minDist = myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location);
    				rockMin = rInfo;
    			}
    		}
    	}
		if ( rockMin != null )
		{
			// NEVER WASTE SMGs ON DEBRIS
			for ( int j = myPlayer.myBlasters.length; --j>=0;)
			{
				gun =  myPlayer.myBlasters[j];
				if ( !gun.isActive() && gun.withinRange(rockMin.location))
					gun.attackSquare(rockMin.location, rockMin.robot.getRobotLevel());
			}
			for ( int j = myPlayer.myRailguns.length; --j>=0;)
			{
				gun =  myPlayer.myRailguns[j];
				if ( !gun.isActive() && gun.withinRange(rockMin.location))
					gun.attackSquare(rockMin.location, rockMin.robot.getRobotLevel());
			}
			for ( int j = myPlayer.myBeams.length; --j >= 0 ; )
			{
				gun =  myPlayer.myBeams[j];
				if ( !gun.isActive() && gun.withinRange(rockMin.location))
					gun.attackSquare(rockMin.location, rockMin.robot.getRobotLevel());
			}
			for ( int j = myPlayer.myHammers.length; --j >= 0 ; )
			{
				gun =  myPlayer.myHammers[j];
				if ( !gun.isActive() && gun.withinRange(rockMin.location))
					gun.attackSquare(rockMin.location, rockMin.robot.getRobotLevel());
			}
			return rockMin.location;
		}
		return null;
		
	}
	
	/**
	 * Shoot itself with a medic gun
	 * @author JVen
	 * @param myPlayer The robot player
	 */
	
	public static void healSelf(RobotPlayer myPlayer) throws Exception
	{
		WeaponController gun;
		
		if ( myPlayer.myRC.getHitpoints() < myPlayer.myRC.getMaxHp() )
		{
			for ( int j = myPlayer.myMedics.length; --j>=0;)
			{
				gun = (WeaponController) myPlayer.myMedics[j];
				if(!gun.isActive())
					gun.attackSquare(myPlayer.myRC.getLocation(), myPlayer.myRC.getRobot().getRobotLevel());
			}
		}
	}
	
	/**
	 * Returns total weight of a list of components
	 * @author JVen
	 * @param components An array of components
	 * @return The total weight of components
	 */
	
	public static int totalWeight(ComponentType[] components)
	{
		int ans = 0;
		for ( int i = components.length ; --i >= 0 ; )
			ans += components[i].weight;
		return ans;
	}
	
	/**
	 * Returns total cost of a list of components
	 * @author JVen
	 * @param components An array of components
	 * @return The total cost of components
	 */
	
	public static int totalCost(ComponentType[] components)
	{
		int ans = 0;
		for ( int i = components.length ; --i >= 0 ; )
			ans += components[i].cost;
		return ans;
	}
	
}


