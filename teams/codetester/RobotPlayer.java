package codetester;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer implements Runnable {
	
	private final RobotController myRC;
	
    public RobotPlayer(RobotController rc) {
    	myRC = rc;
    }

    
	public void run() {
		System.out.println(GameConstants.MINE_ROUNDS);
		
		while(true) {}

	}
	
	
}
