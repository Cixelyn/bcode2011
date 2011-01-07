package maxbot2;
import java.util.PriorityQueue;

import battlecode.common.*;

public class NavigationTangent {
	PriorityQueue<MapLocation> open;
	
	


	public NavigationTangent(RobotPlayer player, RobotController RC, MovementController motorController) {
		open = new PriorityQueue<MapLocation>();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////TANGENTBUGNAV/////////////////////////////////////////////////////////
	
	public Direction tangentBugTo(MapLocation destination) {
		return null;
		
	}
}