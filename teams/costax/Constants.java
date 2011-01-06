package costax;

import battlecode.common.*;

/**
 * Constants, tweak me!
 * @author JVen
 *
 */

public class Constants
{
	public static final int MAP_MAX_SIZE = (int) Math.ceil(Math.sqrt(GameConstants.MAP_MAX_HEIGHT*GameConstants.MAP_MAX_HEIGHT + GameConstants.MAP_MAX_WIDTH * GameConstants.MAP_MAX_WIDTH));
	public static final int GUNS = 2; // number of guns per marine
	public static final ComponentType GUNTYPE = ComponentType.BLASTER; // gun on marines
	public static final ComponentType SENSORTYPE = ComponentType.SIGHT; // sensor on marines
	public static final ComponentType COMMTYPE = ComponentType.ANTENNA; // broadcaster on marines
	public static final ComponentType ARMORTYPE = ComponentType.SHIELD; // armor on marines
	public static final int MARINES = 2; // maximum number of marines per expo refinery
	public static final int OLDNEWS = 15; // number of rounds until marine stops chasing enemy out of range
	public static final int RESERVE = 5; // desired min flux
	public static final int SCOUTING_DISTANCE = 7; // distance SCV should go from home
	public static final int HOME_PROXIMITY = 9; // distance from start SCV considers home
	public static final int MAX_SHEEP = 50; // how many sheep to count before transmitting while sleeping
}