 package masteryone;

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
	
	
	
	//Justin's Go Here
	
	public static final int MAP_MAX_SIZE = (int) Math.ceil(Math.sqrt(GameConstants.MAP_MAX_HEIGHT*GameConstants.MAP_MAX_HEIGHT + GameConstants.MAP_MAX_WIDTH * GameConstants.MAP_MAX_WIDTH));
	public static final int MINE_AFFINITY = 50; // how long SCV should chase a mine before giving up
	public static final int RESERVE = 2; // desired minimum flux after building
	public static final int MAX_FLYERS = 8; // the maximum number of flyers the armory should make
	public static final int FACTORY_TIME = 500; // minimum time factory can be built (may not be built immediately)
	public static final int HANBANG_TIME = 1000; // when tanks should be spawned
	public static final int DEBRIS_TIME = 2000; // when tanks should start killing debris
	public static final int REMAKE_FLYER_TIME = 1700; // when armory should be powered on and flyers remade
	public static final int TANKS_PER_EXPO = 2; // how many tanks to make per expo
	public static final int SCOUTING_DISTANCE = 7; // how far out the SCV should scout
	public static final int HOME_PROXIMITY = 25; // how far from home the SCV considers 'close enough'

	
	//Max's Go here
	public static final int RUN_AWAY_TIME=5;
	public static final int STEPS=4;
	
	
	
	
	//Cory's Go here
	
	
	
	
	
	
	
	
	
}