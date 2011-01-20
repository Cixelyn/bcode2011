 package suboptimal;

import battlecode.common.ComponentType;
import battlecode.common.GameConstants;



/**
 * Constants, tweak me!
 * @author Max (and maybe a little JVen)
 *
 */

public class Constants
{
	
	//Debug Flags
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_BYTECODE_OVERFLOW = true;
	public static final boolean DEBUG_TO_FILE = true;
	
	public static final boolean CUSTOM_INDICATORS = false;
	public static final String INDICATOR0 = "GGGGGGGGGGGGGGGGGGG"; // -____-;;   -JVen
	public static final String INDICATOR1 = "GGGGGGGGGGGGGGGGGGG";
	public static final String INDICATOR2 = "GGGGGGGGGGGGGGGGGGG";
	
	
	
	//Justin's Go Here
	
	public static final int MAP_MAX_SIZE = (int) Math.ceil(Math.sqrt(GameConstants.MAP_MAX_HEIGHT*GameConstants.MAP_MAX_HEIGHT + GameConstants.MAP_MAX_WIDTH * GameConstants.MAP_MAX_WIDTH));
	public static final int MINE_AFFINITY = 20; // how long SCV should chase a mine before giving up
	public static final int RESERVE = 4; // desired minimum flux after building
	public static final int MAX_WRAITHS = 2; // the maximum number of wraiths the armory should make
	public static final int MAX_DRONES = 5; // the maximum number of drones the armory should make
	public static final int DEBRIS_TIME = 1500; // when marines should start killing debris
	public static final int REMAKE_FLYER_TIME = 1300;
	public static final int SCOUTING_DISTANCE = 7; // how far out the SCV should scout
	public static final int HOME_PROXIMITY = 25; // how far from home the SCV considers 'close enough'
	public static final int RALLY_WAIT = 50; // how long refinery should wait to determine rally
	public static final int RERALLY_TTL = 5; // if off_map is encountered and marine sends msg saying to change spawn, the rebroadcast will die after this # of rounds
	public static final int RALLY_TIMEOUT = 5*ComponentType.JUMP.delay; // how long heavy should wait before changing rally upon stuckness
	
	//Max's Go here
	public static final int RUN_AWAY_TIME=5;
	public static final int STEPS=0;
	public static final int TIMEOUT=100;
	public static final int BLASTERS_PER_TOWER = 2; //   WE REQUIRE (2*numBlasters + numShields) <= 15
	public static final int SHIELDS_PER_TOWER = 2;  //   WE REQUIRE (2*numBlasters + numShields) <= 15 
	public static final int ZIGZAG_STEPS = 4;
	
	
	
	
	//Cory's Go here
	
	
	
	
	
	
	
	
	
	
}