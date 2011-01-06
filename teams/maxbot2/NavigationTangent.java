package maxbot2;
import java.util.ArrayList;
import java.util.PriorityQueue;

import battlecode.common.*;

public class NavigationTangent {
	private final RobotPlayer player;
	private final RobotController myRC;
	private final MovementController motor;
	private Integer[][] memory;
	private Integer[][] closed;
	private MapLocation current;
	PriorityQueue<MapLocation> open;
	
	


	public NavigationTangent(RobotPlayer player, RobotController RC, MovementController motorController) {
		this.player = player;
		myRC = RC;
		motor=motorController;
		memory = new Integer[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
		closed = new Integer[GameConstants.MAP_MAX_WIDTH][GameConstants.MAP_MAX_HEIGHT];
		open = new PriorityQueue<MapLocation>();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////TANGENTBUGNAV/////////////////////////////////////////////////////////
	
	public Direction tangentBugTo(MapLocation destination) {
		return null;
		
	}
}