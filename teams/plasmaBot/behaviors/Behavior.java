package plasmaBot.behaviors;

import battlecode.common.*;

import plasmaBot.*;

public abstract class Behavior {
	
	final RobotPlayer myPlayer;		//Our myplayer object
	int behaviorStartTime;			//Start of the new behavior
	
	
	
	/**
	 * Constructor for the behavior system
	 * @param player
	 */
	public Behavior(RobotPlayer player){
		myPlayer = player;
		behaviorStartTime = Clock.getRoundNum();
		
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
	 * @param type the type of message
	 * @param msg received message (that warrants a callback)
	 */
	public abstract void newMessageCallback(MsgType type, Message msg);


	/**
	 * This callback is triggered whenever a unit is reactivated.
	 * Useful for lategame strategies, etc.
	 * @param lastActiveRound the last round the robot was reported awake
	 */
	public abstract void onWakeupCallback(int lastActiveRound);
	
	
	
	

}
