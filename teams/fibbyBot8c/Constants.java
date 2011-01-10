package fibbyBot8c;

import battlecode.common.*;

/**
 * Constants, tweak me!
 * @author Max (and maybe a little JVen)
 *
 */

public class Constants
{
	public static final int MAP_MAX_SIZE = (int) Math.ceil(Math.sqrt(GameConstants.MAP_MAX_HEIGHT*GameConstants.MAP_MAX_HEIGHT + GameConstants.MAP_MAX_WIDTH * GameConstants.MAP_MAX_WIDTH));
	public static final int GUNS = 2; // number of guns per marine
	public static final ComponentType GUNTYPE = ComponentType.BLASTER; // gun on marines
	public static final ComponentType SENSORTYPE = ComponentType.SIGHT; // sensor on marines
	public static final ComponentType COMMTYPE = ComponentType.ANTENNA; // broadcaster on SCVs
	public static final ComponentType ARMORTYPE = ComponentType.SHIELD; // armor on marines
	public static final int MARINES = 2; // maximum number of marines per refinery
	public static final int OLDNEWS = 15; // number of rounds until marine stops chasing enemy out of range
	public static final int RESERVE = 5; // desired min flux
	public static final int SCOUTING_DISTANCE = 7; // distance SCV should go from home
	public static final int HOME_PROXIMITY = 9; // distance from start SCV considers home
	public static final int REBROADCAST_FREQ = 4; // how often to rebroadcast messages
	public static final int SCV_SEARCH_FREQ = 6; // how often SCVs should stop and spin to find mines and off_maps... no higher than ~5?
	public static final int MARINE_SEARCH_FREQ = 5; // how often marines should stop and spin to find enemy.. make really high to disable
	public static final int MINE_AFFINITY = 10; // how long to chase after empty gas before giving up
	public static final int ENEMIES_COUNT = 4;
	
	public static final int MULE_TIME = 200;
	public static final int EXPAND_TIME = 350;
	public static final int MARINE_TIME = 900;
	public static final int MID_GAME = 1100;
	public static final int LATE_GAME = 1600;
	public static final double MARINE_MULE_RATIO = 0.6;
	public static final int TRAVEL_TIME = 300;
	
	
	
	
	
	//Debug Flags
	public static final boolean DEBUG_BYTECODE_OVERFLOW = true;
	
	
	
	
	
	
	
}