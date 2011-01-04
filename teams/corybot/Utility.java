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
	
	

}


