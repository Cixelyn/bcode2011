package corybot;

import battlecode.common.*;

public abstract class Behavior {
	
	final RobotPlayer myPlayer;	
	
	public Behavior(RobotPlayer player){
		myPlayer = player;
	}
	
	
	/**
	 * Main Run Method
	 */
	public abstract void run();
	
	
	/**
	 * Returns name of behavior
	 */
	public abstract String toString();
	
	
	/**
	 * Callback when new component is detected
	 */
	public abstract void newComponentCallback(ComponentController[] components);
	
	
	

}
