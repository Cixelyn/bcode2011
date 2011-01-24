package beambot; import battlecode.common.GameConstants; import battlecode.common.MapLocation;


/**
 * This little class allows booelan data about the map grid to be stored.
 * Advantage is that the modulus operations are performed automatically
 * so that you never forget and have your bot explode into a million
 * of little glittery pieces when the OutOfBounds exception gets thrown.
 * 
 * @author Cory
 *
 */
public class MapStoreBoolean {
	
	private final boolean[][] data;
	private static int maxX = GameConstants.MAP_MAX_WIDTH;
	private static int maxY = GameConstants.MAP_MAX_HEIGHT;
	
	public MapStoreBoolean() {
		data = new boolean[maxX][maxY];
	}
	
	public void set(MapLocation loc, boolean flag) {
		data[loc.x%maxX][loc.y%maxY]= flag; 
	}
	
	public boolean at(MapLocation loc) {
		return data[loc.x%maxX][loc.y%maxY];
	}
	
}
