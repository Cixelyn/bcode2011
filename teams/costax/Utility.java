package costax;
import java.util.ArrayList;

import battlecode.common.*;


/**
 * The utility class does a few useful conversions and calculations
 * @author Cory
 *
 */
public class Utility {
	
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
	 * Helper function to build a chassis
	 * @param player
	 * @param chassis
	 * @param loc
	 * @return
	 * @throws GameActionException
	 */
	public static boolean buildChassisAt(RobotPlayer player,Chassis chassis,MapLocation loc) throws GameActionException {
		
		//Add in early exit cases first.
		if(player.myRC.getTeamResources()<chassis.cost) { //Not enough money
			return false;
		} if (!player.myBuilder.withinRange(loc)) { //Not within range (TEMPORARY can be removed later)
			return false;
		}
		
		//If we passed this point we _should_ be able to build the chassis
		player.myBuilder.build(chassis,loc);
		return true;
	}
	
	
	
	/**
	 * Helper function to build a component
	 * @param player
	 * @param component
	 * @param loc
	 * @param level
	 * @return
	 * @throws GameActionException
	 */
	public static boolean buildComponentAt(RobotPlayer player,ComponentType component,MapLocation loc, RobotLevel level) throws GameActionException {
		
		//Add in early exit cases first.
		if(player.myRC.getTeamResources()<component.cost) { //Not enough money
			return false;
		} if (!player.myBuilder.withinRange(loc)) { //Not within range  (TEMPORARY, can be removed later)
			return false;
		}
		
		//Note that this call will still fail if there is no robot there so be careful.
		
		
		//If we passed this point we _should_ be able to build the component
		player.myBuilder.build(component, loc, level);
		return true;
	}
	
	/**
	 * Helper function to build a component by JVen
	 * DOES NOT FOLLOW THE PARADIGM OF NOT YIELDING INSIDE BEHAVIOR
	 * @param player
	 * @param component
	 * @return
	 */
	public static void buildComponent(RobotPlayer player, ComponentType component) throws Exception
	{
		while (player.myRC.getTeamResources() < component.cost + Constants.RESERVE || player.myBuilder.isActive())
		{
			player.myRC.yield();
		}
		player.myBuilder.build(component, player.myRC.getLocation().add(player.myRC.getDirection()), RobotLevel.ON_GROUND);
	}
	
	/**
	 * Helper function to build a chassis by JVen
	 * DOES NOT FOLLOW THE PARADIGM OF NOT YIELDING INSIDE BEHAVIOR
	 * @param player
	 * @param chassis
	 * @return
	 */
	public static void buildChassis(RobotPlayer player, Chassis chassis) throws Exception
	{
		while (player.myRC.getTeamResources() < chassis.cost + Constants.RESERVE || player.myBuilder.isActive())
		{
			player.myRC.yield();
		}
		player.myBuilder.build(chassis, player.myRC.getLocation().add(player.myRC.getDirection()));
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
	
	
	public static boolean hasComponent(ComponentType query, ComponentType[] list) {
		for(ComponentType c:list) {
			if(c==query) return true;
		}
		return false;
	}
	public static boolean hasComponent(ComponentType query, ComponentController[] list) {
		for(ComponentController c:list) {
			if(c.type()==query) return true;
		}
		return false;
	}
	
	/**
	 * Uses black magic to determine spawn location based on whether off map squares are found in each direction
	 * @param off map square to the west? 0 = no, 1 = yes
	 * @param off map square to the north? 0 = no, 1 = yes
	 * @param off map square to the east? 0 = no, 1 = yes
	 * @param off map square to the south? 0 = no, 1 = yes
	 * @return String stating spawn location... why String? idk
	 */
	public static String getSpawn(int westEdge, int northEdge, int eastEdge, int southEdge)
	{
		switch ((westEdge+1)*(2*northEdge+1)*(4*eastEdge+1)*(6*southEdge+1))
		{
			case 2:
				return "west";
			case 3:
				return "north";
			case 5:
				return "east";
			case 7:
				return "south";
			case 6:
				return "northwest";
			case 14:
				return "southwest";
			case 15:
				return "northeast";
			case 35:
				return "southeast";
		}
		return "idk"; // should be unreachable
	}
	
	/**
	 * Outputs enemy direction based on strings returned from getSpawn
	 * @param strings returned from getSpawn
	 * @return direction where enemy is
	 */
	public static Direction spawnOpposite(String spawn)
	{
		if(spawn == "north")
			return Direction.SOUTH;
		if(spawn == "east")
			return Direction.WEST;
		if(spawn == "south")
			return Direction.NORTH;
		if(spawn == "west")
			return Direction.EAST;
		if(spawn == "northwest")
			return Direction.SOUTH_EAST;
		if(spawn == "northeast")
			return Direction.SOUTH_WEST;
		if(spawn == "southwest")
			return Direction.NORTH_EAST;
		if(spawn == "southeast")
			return Direction.NORTH_WEST;
		return Direction.OMNI;
	}
	
}


