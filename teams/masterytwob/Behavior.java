package team068b;

import battlecode.common.*;

import team068.*;

public abstract class Behavior {
	
	public final RobotPlayer myPlayer;		//Our myplayer object
	int behaviorStartTime;			//Start of the new behavior
	public boolean overrideScanner;
	
	
	
	
	/**
	 * Constructor for the behavior system
	 * @param player
	 */
	public Behavior(RobotPlayer player){
		myPlayer = player;
		behaviorStartTime = Clock.getRoundNum();
		overrideScanner = false;
		
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
	
	
	
	/**
	 * Callback when damage is sustained
	 * @param damageTaken the amount of damage a robot has sustained 
	 */
	public abstract void onDamageCallback(double damageTaken);
	
	
	

}
