 package guileBot;

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
	public static final int DEBRIS_TIME = 1200; // when units should start killing debris
	public static final int SCOUTING_DISTANCE = 7; // how far out the SCV should scout
	public static final int HOME_PROXIMITY = 25; // how far from home the SCV considers 'close enough'
	public static final int SCRAMBLE_TIME = 2100; // when units should rally to a diametrically opposite location
	public static final int STUCK_JUMPS = 5; // how many jumps a heavy takes in the same area before considering himself stuck
	public static final int ARBITER_TIME = 8; // how many units come out before the arbiter (NOT A ROUND NUMBER, SILLY!)
	
	//Max's Go here
	public static final int RUN_AWAY_TIME=5;
	public static final int STEPS=0;
	public static final int TIMEOUT=100;
	public static final int BLASTERS_PER_TOWER = 2; //   WE REQUIRE (2*numBlasters + numShields) <= 15
	public static final int SHIELDS_PER_TOWER = 2;  //   WE REQUIRE (2*numBlasters + numShields) <= 15 
	public static final int ZIGZAG_STEPS = 4;
	public static final int TRACING_THRESHOLD = 40;
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	////// HEAVY LOADOUTS ///////////////////////////////////////////////////////////////////
	
	
	public static final ComponentType[] heavyLoadout0 = new ComponentType[]    // currHeavy == 0
  		                       {ComponentType.RADAR,ComponentType.JUMP,ComponentType.SHIELD,
  								ComponentType.RAILGUN,ComponentType.RAILGUN,ComponentType.SMG
  								};
  	
	public static final ComponentType[] heavyLoadout1 = new ComponentType[]    // currHeavy % 3 == 0, currHeavy != 0
                                 {ComponentType.RADAR,ComponentType.JUMP,ComponentType.SHIELD,
  								ComponentType.RAILGUN,ComponentType.SMG,ComponentType.SMG,ComponentType.SMG,
  								ComponentType.HARDENED
  								};
  	
	public static final ComponentType[] heavyLoadout2 = new ComponentType[]    // currHeavy % 3 == 1
                                 {ComponentType.RADAR,ComponentType.JUMP,ComponentType.SHIELD,
  								ComponentType.RAILGUN,ComponentType.BLASTER,
  								ComponentType.SHIELD,ComponentType.SHIELD,ComponentType.SHIELD,ComponentType.SHIELD
  								};
  	
	public static final ComponentType[] heavyLoadout3 = new ComponentType[]    // currHeavy % 3 == 2  
  	                           {ComponentType.RADAR,ComponentType.JUMP,ComponentType.SHIELD,
  								ComponentType.BLASTER,ComponentType.BLASTER,ComponentType.BLASTER,ComponentType.SMG,
  								ComponentType.PLASMA,ComponentType.PLASMA
  								};
	
	public static final ComponentType[] arbiterLoadout = new ComponentType[]
	                               {ComponentType.SATELLITE,ComponentType.CONSTRUCTOR,
									ComponentType.JUMP,ComponentType.JUMP,
									ComponentType.HAMMER};
	
	
}