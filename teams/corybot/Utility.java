package corybot;
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
	 * This utility function counts the number of weapons and returns back a presized array with component type counts.
	 * in an array ordered by ComponentClass Enum ordinals
	 * Very basic utility function, don't use for anything complicated.
	 * @param component
	 * @return
	 */	
	public static int[] componentTypeCounter(ComponentType[] component) {
		
		int[] output = new int[ComponentClass.values().length];
		
		for(ComponentType c:component) {
			output[c.componentClass.ordinal()]++;
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
		return (output + Integer.toString(player.myMotor.roundsUntilIdle()));
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
	
	
}


