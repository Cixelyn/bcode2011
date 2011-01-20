package suboptimal;

import battlecode.common.*;

import java.util.*;


/**
 * The utility class does a few useful conversions and calculations
 * @author Cory
 *
 */

public class Utility {
	
	public static final Random rand = new Random();
	
	public static void printMsg(RobotPlayer player, String s)
	{
		if ( Constants.DEBUG )
			if (Constants.DEBUG_TO_FILE) {
				player.myRC.addMatchObservation(s);
			} else{
				System.out.println(s);
			}
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
				switch(random) {
				
					case 0:
					case 1:
					case 2:
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
						return;
						
					case 3:
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
						return;

					case 4:
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
						return;
						
					case 5:
					case 6:
					case 7:
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
						return;

					case 8:
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
						return;
						
					case 9:
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
						return;
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
	 * Will try to build a component in ONE ROUND: don't use this unless you know you need to
	 * @author JVen
	 * @param player The robot player
	 * @param dir The direction in which to build
	 * @param component The component type to build
	 * @param level The robot level of the robot on which to build
	 * @return boolean True if component is built, false otherwise
	 */
	
	public static boolean tryBuildComponent(RobotPlayer player, Direction dir, ComponentType component, RobotLevel level) throws Exception
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
				return false;
			}
		}
		else
			loc = player.myRC.getLocation();
		if ( player.myRC.getTeamResources() < component.cost + Constants.RESERVE || player.myBuilder.isActive() )
			return false;
		if ( player.mySensor != null && player.mySensor.withinRange(loc) )
		{
			Robot r = (Robot)player.mySensor.senseObjectAtLocation(loc, level);
			if ( r == null || r.getTeam() != player.myRC.getRobot().getTeam() )
				return false;
			else
			{
				RobotInfo rInfo = player.mySensor.senseRobotInfo(r);
				if ( totalWeight(rInfo.components) + component.weight > rInfo.chassis.weight )
					return false;
			}
		}
		player.myBuilder.build(component, loc, level);
		return true;
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
			for ( int j = myPlayer.myWeapons.length; --j>= 0;)
			{
				gun = myPlayer.myWeapons[j];
				if ( gun.type() == ComponentType.MEDIC && r.getTeam() == myPlayer.myRC.getTeam() )
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
		
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class); 
		
		for ( int i = nearbyRobots.length; --i>=0;)
    	{
    		r = nearbyRobots[i];
    		if ( r.getTeam() == myPlayer.myRC.getTeam().opponent() )
    		{
    			rInfo = myPlayer.mySensor.senseRobotInfo(r);
    			if ( myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location) < minDist1 )
    			{
    				if ( enemyMin2 == null )
    				{
    					minDist1 = myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location);
    					minDist2 = myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location);
    					enemyMin1 = rInfo;
    					enemyMin2 = rInfo;
    				}
    				else
    				{
    					minDist2 = minDist1;
    					minDist1 = myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location);
    					enemyMin2 = enemyMin1;
    					enemyMin1 = rInfo;
    				}
    			}
    			else if ( myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location) < minDist2 )
    			{
    				minDist2 = myPlayer.myRC.getLocation().distanceSquaredTo(rInfo.location);
					enemyMin2 = rInfo;
    			}
    		}
    	}
		if ( enemyMin1 != null )
		{
			for ( int j = myPlayer.myWeapons.length; --j>=0;)
			{
				gun =  myPlayer.myWeapons[j];
				if ( gun.type() != ComponentType.MEDIC && !gun.isActive() && gun.withinRange(enemyMin1.location))
					gun.attackSquare(enemyMin1.location, enemyMin1.robot.getRobotLevel());
			}
			return enemyMin1;
		}
		return null;
			
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
			for ( int j = myPlayer.myWeapons.length; --j>=0;)
			{
				gun =  myPlayer.myWeapons[j];
				if ( gun.type() != ComponentType.MEDIC && !gun.isActive() && gun.withinRange(rockMin.location))
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
			for ( int j = myPlayer.myWeapons.length; --j>=0;)
			{
				gun = (WeaponController) myPlayer.myWeapons[j];
				if ( gun.type() == ComponentType.MEDIC )
				{
					if(!gun.isActive())
					{
						gun.attackSquare(myPlayer.myRC.getLocation(), myPlayer.myRC.getRobot().getRobotLevel());
					}
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
				return 6;
			case 3:
				return 0;
			case 5:
				return 2;
			case 7:
				return 4;
			case 6:
				return 7;
			case 14:
				return 5;
			case 15:
				return 1;
			case 35:
				return 3;
		}
		return -1; // center spawn
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
			case 0:
			return hometown.add(Direction.SOUTH, GameConstants.MAP_MAX_HEIGHT);
			case 1:
			return hometown.add(Direction.SOUTH_WEST, Constants.MAP_MAX_SIZE);
			case 2:
			return hometown.add(Direction.WEST, GameConstants.MAP_MAX_WIDTH);
			case 3:
			return hometown.add(Direction.NORTH_WEST, Constants.MAP_MAX_SIZE);
			case 4:
			return hometown.add(Direction.NORTH, GameConstants.MAP_MAX_HEIGHT);
			case 5:
			return hometown.add(Direction.NORTH_EAST, Constants.MAP_MAX_SIZE);
			case 6:
			return hometown.add(Direction.EAST, GameConstants.MAP_MAX_WIDTH);
			case 7:
			return hometown.add(Direction.SOUTH_EAST, Constants.MAP_MAX_SIZE);
		}
		return null; // should not be reachable
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
		for ( int i = components.length-1 ; i >= 0 ; i-- )
			ans += components[i].weight;
		return ans;
	}
	
	//Max's Go here
	
	
	public static void bounceNavForFlyers(RobotPlayer myPlayer, int zigzag) throws GameActionException {
		if (!myPlayer.myMotor.isActive()) {
			boolean bounce=false;
			if (myPlayer.myRC.getDirection().isDiagonal()) {
				if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)) {
					bounce=true;
				}
			}
			else if (!myPlayer.myRC.getDirection().isDiagonal()) {
				if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)) {
					bounce=true;
				}
			}
			if (bounce) {
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
				}
			else if (zigzag==0) { //don't zig or zag, just keep on moving forward
				
				if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
					myPlayer.myMotor.moveForward();
				}
				else {
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
				}
			}
			else if (zigzag==1) { //turn 90 degrees left 
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
			}
			else if (zigzag==2) { //turn 90 degrees right
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
			}
		}
	}

 	//Cory's Go here
 	
		
		public static int NUM_COMPONENT_TYPES = ComponentType.values().length;
		/**
		 * Counts the number of component types given an array of component types.
		 * Useful in determining the loadout of a robot.
		 * @param components
		 * @return int array filled with counts of each ComponentType
		 */
		public static int[] countComponents(ComponentType[] components) {
			int[] list = new int[NUM_COMPONENT_TYPES];
			for(int i=components.length; --i>=0;) {
				list[components[i].ordinal()]++;
			}
			return list;
		}
		
		/**
		 * Alternate version of {@link #countComponents(ComponentType[])} which counts an array of ComponentController instead
		 * allowing the output of <code>myRC.components()</code> to be counted.
		 * @see #countComponents(ComponentType[])
		 * @param components
		 * @return int array filled with counts of each ComponentType
		 */
		public static int[] countComponents(ComponentController[] components) {
			int[] list = new int[NUM_COMPONENT_TYPES];
			for(int i=components.length; --i>=0;) {
				list[components[i].type().ordinal()]++;
			}
			return list;
		}
		
		
		/**
		 * Fast method to compare two components and ensure that they're equal  
		 * 
		 * @return
		 */
		public static boolean compareComponents(RobotPlayer player, int[] requestedLoadOut ) {
			
			int[] currentLoadOut = countComponents(player.myRC.components()); 
			
			for(int i=NUM_COMPONENT_TYPES; --i>=0;) {
				if(currentLoadOut[i]<requestedLoadOut[i]) return false;
			}
			return true;
		}
	
	
	
	

	/**
	 * Returns true if the line segment from source to dest intersects the point (interior AND perimeter)
	 * @author JVen
	 * @param point The square whose existence on the line segment is questioned
	 * @param source One of the ends of the line segment
	 * @param dest The other end of the line segment
	 * @return True if point is on the segment, false otherwise
	 */
	
	public static boolean isOnLine(MapLocation point, MapLocation source, MapLocation dest)
	{

		double x1 = source.x;
		double y1 = source.y;
		double x2 = dest.x;
		double y2 = dest.x;
		double x = point.x;
		double y = point.y;
		double m = (y2 - y1) / (x2 - x1);
		double mInv = (x2 - x1) / (y2 - y1);
		return 
		( (x1 == x2) && ((y >= y1 && y <= y2) || (y >= y2 && y <= y1)) ) ||                    // vertical line
		( (y1 == y2) && ((x >= x1 && x <= x2) || (x >= x2 && x <= x1)) ) ||                    // horizontal line
		( (m * (x - 0.5 - x1) + y1 >= y - 0.5) && (m * (x - 0.5 - x1) + y1 <= y + 0.5) ) ||    // intersect left side of square
		( (m * (x + 0.5 - x1) + y1 >= y - 0.5) && (m * (x + 0.5 - x1) + y1 <= y + 0.5) ) ||    // intersect right side of square
		( (mInv * (y - 0.5 - y1) + x1 >= x - 0.5) && (m * (y - 0.5 - y1) + x1 <= x + 0.5) ) || // intersect bottom side of square
		( (mInv * (y + 0.5 - y1) + x1 >= x - 0.5) && (m * (y + 0.5 - y1) + x1 <= x + 0.5) );   // intersect top side of square

	}
	
	
	
}


