package corybot;

import battlecode.common.*;

public abstract class Behavior {
	
	final RobotPlayer myPlayer;	
	
	public Behavior(RobotPlayer player){
		myPlayer = player;
	}
	
	
	/**
	 * Main Run Method
	 * @throws Exception 
	 */
	public abstract void run() throws Exception;
	
	
	
	/**
	 * Returns name of behavior
	 */
	public abstract String toString();
	
	
	
	/**
	 * Callback when new component is detected
	 */
	public abstract void newComponentCallback(ComponentController[] components);
	
	
	
	/**
	 * Callback when new messages are received
	 * @param msg received message (that warrants a callback)
	 */
	public abstract void newMessageCallback(MsgType msg);

	
	
	

}
