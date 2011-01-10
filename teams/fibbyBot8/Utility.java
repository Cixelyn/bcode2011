package fibbyBot8;

import battlecode.common.*;
import java.util.*;



/**
 * The utility class does a few useful conversions and calculations
 * @author Cory
 *
 */
public class Utility
{
	
	static final Random rand = new Random();
	
	public static final String[] componentStr = new String[] {
		"Shd",
		"Hrd",
		"Reg",
		"Pls",
		"Irn",
		"Plt",
		"Smg",
		"Bls",
		"Rlg",
		"Hmr",
		"Bea",
		"Mdc",
		"Sat",
		"Tel",
		"Sgt",
		"Rdr",
		"Ant",
		"Dsh",
		"Net",
		"Prc",
		"Jmp",
		"Dmy",
		"Bug",
		"Drp",
		"Rcy",
		"Fac",
		"Con",
		"Arm",
		"Mov",
		"Mov",
		"Mov",
		"Mov",
		"Mov",
		"Sen"};
	
	public static final String[] dirStr = new String[] {
		"N","NE","E","SE","S","SW","W","NW","X","O"};
	
	
	/**
	 * <code>printComponentList</code> converts a list of componentControllers into a shorthand string for easy reading.
	 * Check <code>componentStr</code> for a list of abbreviations.
	 * @param commponentControllers the list of ComponentControllers to converts into a string
	 * @return string where each component is abbreviated with a 3 letter code
	 * @version values computed from 1.0.0
	 */
	public static String printComponentList(ComponentController[] componentControllers) {
		String output = "";
		for(ComponentController c:componentControllers) {
			output += componentStr[c.type().ordinal()];
		}
		return output;
	}
	
	/**
	 * Convert array of ComponentControllers into ArrayLists of controllers
	 * separated by class
	 * @param components
	 * @return array of ArrayLists of component controllers
	 */	
	public static ArrayList<?>[] getComponents(ComponentController[] components)
	{
		ArrayList<BroadcastController> broadcasters = new ArrayList<BroadcastController>();
		ArrayList<BuilderController> builders = new ArrayList<BuilderController>();
		ArrayList<MovementController> motors = new ArrayList<MovementController>();
		ArrayList<SensorController> sensors = new ArrayList<SensorController>();
		ArrayList<WeaponController> weapons = new ArrayList<WeaponController>();
		for(ComponentController c:components)
		{
			switch (c.componentClass())
			{
				case ARMOR:
					break;
				case BUILDER: builders.add((BuilderController)c);
					break;
				case COMM: broadcasters.add((BroadcastController)c);
					break;
				case MISC:
					break;
				case MOTOR: motors.add((MovementController)c);
					break;
				case SENSOR: sensors.add((SensorController)c);
					break;
				case WEAPON: weapons.add((WeaponController)c);
					break;
			}
		}
		ArrayList<?>[] componentList = {broadcasters,builders,motors,sensors,weapons};
		return componentList;
	}
	
	/**
	 * This utility function counts the number of weapons and returns back a presized array with component type counts.
	 * in an array ordered by ComponentClass Enum ordinals
	 * Very basic utility function, don't use for anything complicated.
	 * @param component
	 * @return
	 */	
	public static int[] componentClassCounter(ComponentType[] component) {
		
		int[] output = new int[ComponentClass.values().length];
		
		for(ComponentType c:component) {
			output[c.componentClass.ordinal()]++;
		}
		return output;
	}
	
	/**
	 * This utility function counts the number of weapons and returns back a presized array with component type counts.
	 * in an array ordered by ComponentClass Enum ordinals
	 * Very basic utility function, don't use for anything complicated.
	 * @param component
	 * @return
	 */
	public static int[] componentClassCounter(ComponentController[] component) {
		
		int[] output = new int[ComponentClass.values().length];
		
		for(ComponentController c:component) {
			output[c.componentClass().ordinal()]++;
		}
		return output;
	}
	
	
	
	/**
	 * This function returns a string detailing some movement information on the robot
	 * @param player
	 * @return
	 */
	public static String robotMoveInfo(RobotPlayer player) {
		String output = dirStr[player.myRC.getDirection().ordinal()];
		return (output + ":" + Integer.toString(player.myMotor.roundsUntilIdle()));
	}
	
	/**
	 * Helper function to build a component on self by JVen
	 * DOES NOT FOLLOW THE PARADIGM OF NOT YIELDING INSIDE BEHAVIOR
	 * Currently modified to use sleep() though ~coryli
	 * @param player
	 * @param component
	 * @return 
	 */
	public static void buildComponentOnSelf(RobotPlayer player, ComponentType component) throws Exception
	{
		while (player.myRC.getTeamResources() < component.cost + Constants.RESERVE || player.myBuilder.isActive())
			player.sleep();
		player.myBuilder.build(component, player.myRC.getLocation(), player.myRC.getRobot().getRobotLevel());
	}
	
	/**
	 * Helper function to build a component in the direction i'm facing, on the ground by JVen
	 * DOES NOT FOLLOW THE PARADIGM OF NOT YIELDING INSIDE BEHAVIOR
	 * Currently modified to use sleep() though ~coryli
	 * @param player
	 * @param component
	 * @return true if built
	 */
	public static boolean buildComponentOnFrontGround(RobotPlayer player, ComponentType component) throws Exception
	{
		while (player.myRC.getTeamResources() < component.cost + Constants.RESERVE || player.myBuilder.isActive())
			player.sleep();
		Robot rFront = (Robot) player.mySensor.senseObjectAtLocation(player.myRC.getLocation().add(player.myRC.getDirection()), RobotLevel.ON_GROUND);
		if( rFront != null && rFront.getTeam() == player.myRC.getTeam() )
		{
			player.myBuilder.build(component, player.myRC.getLocation().add(player.myRC.getDirection()), RobotLevel.ON_GROUND);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Helper function to build a component in the direction i'm facing, in the air by JVen
	 * DOES NOT FOLLOW THE PARADIGM OF NOT YIELDING INSIDE BEHAVIOR
	 * Currently modified to use sleep() though ~coryli
	 * @param player
	 * @param component
	 * @return true if built
	 */
	public static boolean buildComponentOnFrontAir(RobotPlayer player, ComponentType component) throws Exception
	{
		while (player.myRC.getTeamResources() < component.cost + Constants.RESERVE || player.myBuilder.isActive())
			player.sleep();
		Robot rFront = (Robot) player.mySensor.senseObjectAtLocation(player.myRC.getLocation().add(player.myRC.getDirection()), RobotLevel.IN_AIR);
		if( rFront != null && rFront.getTeam() == player.myRC.getTeam() )
		{
			player.myBuilder.build(component, player.myRC.getLocation().add(player.myRC.getDirection()), RobotLevel.IN_AIR);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Helper function to build a chassis by JVen
	 * DOES NOT FOLLOW THE PARADIGM OF NOT YIELDING INSIDE BEHAVIOR
	 * Current modified to use sleep() though ~coryli
	 * @param player
	 * @param chassis
	 * @return true if built
	 */
	public static boolean buildChassisInDir(RobotPlayer player, Direction dir, Chassis chassis) throws Exception
	{
		while (player.myRC.getTeamResources() < chassis.cost + Constants.RESERVE || player.myBuilder.isActive())
			player.sleep();
		/*GameObject rFront = player.mySensor.senseObjectAtLocation(player.myRC.getLocation().add(dir), chassis.level);
		if ( rFront == null )*/
		if ( player.myMotor.canMove(dir) )
		{
			player.myBuilder.build(chassis, player.myRC.getLocation().add(dir));
			return true;
		}
		else
			return false;
	}
	
	/**
	 * This helper function determines whether adding a component is permissible given the weight of a robot
	 * @param toAdd the component to add
	 * @param receiver the robot who will receive the component
	 * @return
	 */
	public static boolean canAdd(ComponentType toAdd, RobotInfo receiver) {
		int currWeight=0;
		for(ComponentType c:receiver.components) {
			currWeight+=c.weight;			
		}
		
		if(toAdd.weight + currWeight <= receiver.chassis.weight) { //We can actually add it
			return true;
		} else { //Too heavy
			return false;
		}
	}
	
	
	/**
	 * Does <code>query</code> component exist in <code>list</code>
	 * @param query
	 * @param list
	 * @return
	 */
	public static boolean hasComponent(ComponentType query, ComponentType[] list) {
		for(ComponentType c:list) {
			if(c==query) return true;
		}
		return false;
	}
	
	/**
	 * Does <code>query></code> component exist in <code>list</code>
	 * @param query
	 * @param list
	 * @return
	 */
	public static boolean hasComponent(ComponentType query, ComponentController[] list) {
		for(ComponentController c:list) {
			if(c.type()==query) return true;
		}
		return false;
	}
	
	/**
	 * Uses black magic to determine spawn location based on whether off map squares are found in each direction (see SCV code for meaning of spawn int)
	 * @param off map square to the west? 0 = no, 1 = yes
	 * @param off map square to the north? 0 = no, 1 = yes
	 * @param off map square to the east? 0 = no, 1 = yes
	 * @param off map square to the south? 0 = no, 1 = yes
	 * @return String stating spawn location... why String? idk
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
		return 8; // should be unreachable
	}
	
	/**
	 * return string based on spawn location
	 * @param spawn integer given by SCV denoting spawn
	 * @return String stating spawn location... why String? idk
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
		return "unknown"; // should be unreachable
	}
	
	
	/**
	 * Outputs enemy direction based on strings returned from getSpawn
	 * @param string returned from getSpawn
	 * @return direction where enemy is
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
		return null; // should be unreachable
	}
	
	/**
	 * Outputs array of waypoint directions for scvs based on spawn
	 * @param string returned from getSpawn
	 * @return waypoint directions for scvs
	 */
	public static Direction[] spawnAdjacent(int spawn)
	{
		Direction[] waypointDirs = new Direction[4]; // 1st scv dir, 2nd scv dir, 1st scv next dir, 2nd scv next dir
		switch (spawn)
		{
			case 2:
			waypointDirs[0] = Direction.EAST;
			waypointDirs[1] = Direction.WEST;
			waypointDirs[2] = Direction.SOUTH;
			waypointDirs[3] = Direction.SOUTH;
			break;
			
			case 6:
			waypointDirs[0] = Direction.EAST;
			waypointDirs[1] = Direction.WEST;
			waypointDirs[2] = Direction.NORTH;
			waypointDirs[3] = Direction.NORTH;
			break;
			
			case 4:
			waypointDirs[0] = Direction.NORTH;
			waypointDirs[1] = Direction.SOUTH;
			waypointDirs[2] = Direction.WEST;
			waypointDirs[3] = Direction.WEST;
			break;

			case 0:
			waypointDirs[0] = Direction.NORTH;
			waypointDirs[1] = Direction.SOUTH;
			waypointDirs[2] = Direction.EAST;
			waypointDirs[3] = Direction.EAST;
			break;

			case 1:
			waypointDirs[0] = Direction.EAST;
			waypointDirs[1] = Direction.SOUTH;
			waypointDirs[2] = Direction.SOUTH;
			waypointDirs[3] = Direction.EAST;
			break;

			case 3:
			waypointDirs[0] = Direction.WEST;
			waypointDirs[1] = Direction.SOUTH;
			waypointDirs[2] = Direction.SOUTH;
			waypointDirs[3] = Direction.WEST;
			break;

			case 7:
			waypointDirs[0] = Direction.EAST;
			waypointDirs[1] = Direction.NORTH;
			waypointDirs[2] = Direction.NORTH;
			waypointDirs[3] = Direction.EAST;
			break;

			case 5:
			waypointDirs[0] = Direction.WEST;
			waypointDirs[1] = Direction.NORTH;
			waypointDirs[2] = Direction.NORTH;
			waypointDirs[3] = Direction.WEST;
			break;

			case 8:
			waypointDirs[0] = Direction.WEST;
			waypointDirs[1] = Direction.NORTH;
			waypointDirs[2] = Direction.SOUTH;
			waypointDirs[3] = Direction.EAST;
			break;
		}
		return waypointDirs;
	}
	
	// this method was before the abstraction of Messenger in r~100
	
	/*public static Message sendAttackMsg(RobotPlayer myPlayer, MapLocation hometown, MapLocation enemyLocation)
	{
		Message attackMsg = new Message();
		attackMsg.ints = Constants.ATTACK;
		MapLocation[] spawnMsg = {hometown, enemyLocation};
		attackMsg.locations = spawnMsg;
		myPlayer.myMessenger.sendMsg(attackMsg);
		return attackMsg;
	}*/
	
	/**
	 * Looks for enemies and shoots at them
	 * @param myPlayer, you know what that is
	 * @return location of last enemy fired at, null if none
	 */
	public static MapLocation senseEnemies(RobotPlayer myPlayer) throws Exception
	{
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
		WeaponController gun;
		RobotInfo rInfo;
		MapLocation destination = null;
    	for(Robot r:nearbyRobots)
    	{
			for (Object c:myPlayer.myWeapons)
			{
				gun = (WeaponController) c;
				if(r.getTeam()==myPlayer.myRC.getTeam().opponent())
				{
					rInfo = myPlayer.mySensor.senseRobotInfo(r);
				 	destination = rInfo.location;
					if(!gun.isActive() && rInfo.hitpoints>0 && gun.withinRange(rInfo.location))
						gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
				}
			}
    	}
    	return destination;
	}
	
	/**
	 * Looks for enemies and shoots at them
	 * @param myPlayer, you know what that is
	 * @return location of last enemy fired at, null if none
	 */
	public static MapLocation senseDebris(RobotPlayer myPlayer) throws Exception
	{
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
		WeaponController gun;
		RobotInfo rInfo;
		MapLocation destination = null;
    	for(Robot r:nearbyRobots)
    	{
			for (Object c:myPlayer.myWeapons)
			{
				gun = (WeaponController) c;
				if(r.getTeam()==Team.NEUTRAL)
				{
					rInfo = myPlayer.mySensor.senseRobotInfo(r);
				 	destination = rInfo.location;
					if(!gun.isActive() && rInfo.hitpoints>0 && gun.withinRange(rInfo.location))
						gun.attackSquare(rInfo.location, rInfo.robot.getRobotLevel());
				}
			}
    	}
    	return destination;
	}
	
	/**
	 * Outputs appropriate map size given a direction
	 * @param direction
	 * @return map size
	 */
	public static int dirSize(Direction dir)
	{
		if (dir == Direction.NORTH || dir == Direction.SOUTH)
			return GameConstants.MAP_MAX_HEIGHT;
		if (dir == Direction.EAST || dir == Direction.WEST)
			return GameConstants.MAP_MAX_WIDTH;
		else
			return Constants.MAP_MAX_SIZE;
	}
	
	/**
	 * Given nav and destination, turns in the right direction and takes a step in that direction
	 * @param myPlayer, nav, destination location
	 * @return nothing!
	 */
	public static void navStep(RobotPlayer myPlayer, Navigation robotNavigation, MapLocation dest) throws Exception
	{
		if (dest != null)
		{
			Direction direction = robotNavigation.bugTo(dest);
			if(direction != Direction.OMNI && direction != Direction.NONE)
			{
				while(myPlayer.myMotor.isActive())
					myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(direction);
				while(myPlayer.myMotor.isActive() || !myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
					myPlayer.myRC.yield();
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
	 * WEEEEEEEEEEEEEEEEEEEEEEE
	 * @param player
	 * @return fun
	 */
	public static void spin(RobotPlayer myPlayer) throws Exception
	{
		if (!myPlayer.myMotor.isActive())
			myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());  // WEEEEEEEEEEE!!!!
	}
	
	/**
	 * check if a builder should build in the given direction (except when building jimmy)
	 * square should be land, square should not contain robots, square should not contain mines, square should not be where jimmy is
	 * @param direction to check, location of jimmy
	 * @return well?
	 */
	public static boolean shouldBuild(RobotPlayer myPlayer, Direction dir, MapLocation jimmyHome) throws Exception
	{
		MapLocation loc = myPlayer.myRC.getLocation().add(dir);
		return (myPlayer.myRC.senseTerrainTile(loc) == TerrainTile.LAND) && (myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) == null) && (myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.MINE) == null && (jimmyHome == null || !loc.equals(jimmyHome)));
	}
	
	/**
	 * check if a builder should build in the given direction
	 * square should be land, square should not contain robots, square should not contain mines
	 * @param direction to check
	 * @return well?
	 */
	public static boolean shouldBuild(RobotPlayer myPlayer, Direction dir) throws Exception
	{
		MapLocation loc = myPlayer.myRC.getLocation().add(dir);
		return (myPlayer.myRC.senseTerrainTile(loc) == TerrainTile.LAND && myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) == null && myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.MINE) == null);
	}
	
	/**
	 * check if a builder should build jimmy in the given direction
	 * square should be where jimmy is, square should be land, square should not contain robots, square should not contain mines
	 * @param direction to check, location of jimmy
	 * @return well?
	 */
	public static boolean shouldBuildJimmy(RobotPlayer myPlayer, Direction dir, MapLocation jimmyHome) throws Exception
	{
		MapLocation loc = myPlayer.myRC.getLocation().add(dir);
		return ((jimmyHome == null || loc.equals(jimmyHome)) && myPlayer.myRC.senseTerrainTile(loc) == TerrainTile.LAND) && (myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) == null) && (myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.MINE) == null);
	}
	
	/**
	 * used by SCV when returning home after scouting in a direction
	 * @param list of squares traversed while scouting
	 * @return 
	 */
	public static void backtrack(RobotPlayer myPlayer, LinkedList<MapLocation> breadcrumbs) throws Exception
	{
		MapLocation dest;
		if(!breadcrumbs.isEmpty())
		{
			dest = breadcrumbs.pollLast();
			Direction direction = myPlayer.myRC.getLocation().directionTo(dest);
			if(direction != Direction.OMNI && direction != Direction.NONE)
			{
				while(myPlayer.myMotor.isActive())
					myPlayer.myRC.yield();
				myPlayer.myMotor.setDirection(direction);
				while(myPlayer.myMotor.isActive() || !myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
					myPlayer.myRC.yield();
				myPlayer.myMotor.moveForward();
			}
			//Utility.navStep(myPlayer, robotNavigation, breadcrumbs.pop());
		}
	}
	
	/**
	 * Equip the robot in front of you with one component
	 * @param the robot and the component to equip on him
	 * @return 
	 */
	public static void equipFrontWithOneComponent(RobotPlayer myPlayer, Robot r, ComponentType c1) throws Exception
	{
		boolean r1 = false;
		while (!r1)
		{
			Robot rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), r.getRobotLevel());
			if(rFront != null && (rFront).getID() == r.getID())
			{
				RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(rFront);
				if(rInfo.components != null)
				{
					for (ComponentType c:rInfo.components)
					{
						if (c == c1)
							r1 = true;
					}
				}
				if (!r1)
				{
					if (r.getRobotLevel() == RobotLevel.ON_GROUND)
						Utility.buildComponentOnFrontGround(myPlayer, c1);
					else
						Utility.buildComponentOnFrontAir(myPlayer, c1);
				}
			}
			else
				return;
		}
		return;
	}
	
	/**
	 * Equip the robot in front of you with one component when robot is unknown
	 * @param the component to equip on him
	 * @return 
	 */
	public static void equipFrontWithOneComponent(RobotPlayer myPlayer, ComponentType c1) throws Exception
	{
		boolean r1 = false;
		while (!r1)
		{
			Robot rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND); // assumed to be on ground
			if(rFront != null)
			{
				RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(rFront);
				if(rInfo.components != null)
				{
					for (ComponentType c:rInfo.components)
					{
						if (c == c1)
							r1 = true;
					}
				}
				if (!r1)
					Utility.buildComponentOnFrontGround(myPlayer, c1);
			}
			else
				return;
		}
		return;
	}
	
	
	/**
	 * Equip the robot in front of you with two components
	 * @param the robot and the two components to equip on him
	 * @return 
	 */
	public static void equipFrontWithTwoComponents(RobotPlayer myPlayer, Robot r, ComponentType c1, ComponentType c2) throws Exception
	{
		boolean r1 = false;
		boolean r2 = false;
		while (!r1 || !r2)
		{
			Robot rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), r.getRobotLevel());
			if(rFront != null && (rFront).getID() == r.getID())
			{
				RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(rFront);
				if(rInfo.components != null)
				{
					for (ComponentType c:rInfo.components)
					{
						if (c == c1)
							r1 = true;
						if (c == c2)
							r2 = true;
					}
				}
				if (!r1)
				{
					if (r.getRobotLevel() == RobotLevel.ON_GROUND)
						Utility.buildComponentOnFrontGround(myPlayer, c1);
					else
						Utility.buildComponentOnFrontAir(myPlayer, c1);
				}
				if (!r2)
				{
					if (r.getRobotLevel() == RobotLevel.ON_GROUND)
						Utility.buildComponentOnFrontGround(myPlayer, c2);
					else
						Utility.buildComponentOnFrontAir(myPlayer, c2);
				}
			}
			else
				return;
		}
		return;
	}
	
	/**
	 * Equip the robot in front of you with three components
	 * @param the robot and the three components to equip on him
	 * @return 
	 */
	public static void equipFrontWithThreeComponents(RobotPlayer myPlayer, Robot r, ComponentType c1, ComponentType c2, ComponentType c3) throws Exception
	{
		boolean r1 = false;
		boolean r2 = false;
		boolean r3 = false;
		while (!r1 || !r2 || !r3)
		{
			Robot rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), r.getRobotLevel());
			if(rFront != null && (rFront).getID() == r.getID())
			{
				RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(rFront);
				if(rInfo.components != null)
				{
					for (ComponentType c:rInfo.components)
					{
						if (c == c1)
							r1 = true;
						if (c == c2)
							r2 = true;
						if (c == c3)
							r3 = true;
					}
				}
				if (!r1)
				{
					if (r.getRobotLevel() == RobotLevel.ON_GROUND)
						Utility.buildComponentOnFrontGround(myPlayer, c1);
					else
						Utility.buildComponentOnFrontAir(myPlayer, c1);
				}
				if (!r2)
				{
					if (r.getRobotLevel() == RobotLevel.ON_GROUND)
						Utility.buildComponentOnFrontGround(myPlayer, c2);
					else
						Utility.buildComponentOnFrontAir(myPlayer, c2);
				}
				if (!r3)
				{
					if (r.getRobotLevel() == RobotLevel.ON_GROUND)
						Utility.buildComponentOnFrontGround(myPlayer, c3);
					else
						Utility.buildComponentOnFrontAir(myPlayer, c3);
				}
			}
			else
				return;
		}
		return;
	}
	
	/**
	 * Equip the robot in front of you with n of same component
	 * @param the robot, the component to equip on him, how many
	 * @return 
	 */
	public static void equipFrontWithSameComponents(RobotPlayer myPlayer, Robot r, ComponentType c1, int k) throws Exception
	{
		int r1 = 0;
		Robot rFront = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), r.getRobotLevel());
		if(rFront != null && rFront.getID() == r.getID())
		{
			RobotInfo rInfo = myPlayer.mySensor.senseRobotInfo(rFront);
			if(rInfo.components != null)
			{
				for (ComponentType c:rInfo.components)
				{
					if (c == c1)
						r1++;
				}
			}
			while (r1 < k)
			{
				if ( (r.getRobotLevel() == RobotLevel.ON_GROUND && Utility.buildComponentOnFrontGround(myPlayer, c1)) || (r.getRobotLevel() == RobotLevel.IN_AIR && Utility.buildComponentOnFrontAir(myPlayer, c1)))
					r1++;
			}
		}
		else
			return;
		return;
	}
	
	/**
	 * Equip the robot in front of you with n of same component
	 * @param the robot, the component to equip on him, how many
	 * @return 
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
	
	public static double buildingProbability(double currRes, double prevRes, Chassis chassis)
	{
		if ( (chassis == Chassis.BUILDING && Clock.getRoundNum() % 10 == 0) || (chassis == Chassis.LIGHT && Clock.getRoundNum() > Constants.MULE_TIME && Clock.getRoundNum() % 5 == 0))
		{
			if (currRes > chassis.cost + Constants.RESERVE && currRes - prevRes > chassis.upkeep)
				return 1.0;
		}
		return 0.0;
	}
	
	public static double marineMuleRatio()
	{
		if (Clock.getRoundNum() < 800)
			return 0.0;
		return 0.8;
	}
}


