package suboptimal;

import battlecode.common.*;


/**
 * Convenient datastructure for storing information about robots
 * @author Cory
 *
 */
public class RobotList {
	public RobotInfo[] robotInfos;
	public Robot[] robots;
	
	
	/**
	 * Creates a new RobotList
	 * @param n size of list container
	 */
	public RobotList(int n) {
		robotInfos = new RobotInfo[n];
		robots = new Robot[n];
	}
	
	
}
