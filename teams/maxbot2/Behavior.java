package maxbot2;

import battlecode.common.*;

public abstract class Behavior {
	
	final RobotPlayer myPlayer;		//Our myplayer object
	int runtime;					//Our runtime counter
	
	
	public Behavior(RobotPlayer player){
		myPlayer = player;
		runtime = 0;
		
		//Execute Callbacks for starting units that start off with stuff
		newComponentCallback(myPlayer.myRC.components());
		
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
	public abstract void newMessageCallback(Message msg);


	
	
	

}
