package fibbyBot14; import battlecode.common.*;
/**
 *<pre>
 * 
 *                             _________________
 *                           /                   \ 
 *  ========================[   B E H A V I O R   ]===========================    
 *                           \___________________/
 *   
 *   
 * 		The abstract behavior class defines the base class for all other
 * 		types of behaviors.  Several different functions and callbacks are defined.
 * 
 * 		The important one:
 * 			- run(): the main body / execution block of the behavior
 * 		
 * 		There are also several callbacks that are triggered upon certain events:
 * 			- newComponentCallback(): triggered when the unit is equipped
 * 			- onWakeupCallback()    : triggered when the unit reactivates from shutdown
 *   
 *   
 * 
 * 
 * </pre>
 * @author Cory
 *
 */
public abstract class Behavior
{
	
	public final RobotPlayer myPlayer;		//Our myplayer object
	int behaviorStartTime;			//Start of the new behavior
	public boolean overrideScanner;
	
	
	
	
	/**
	 * Constructor for the behavior system
	 * @param player
	 */
	public Behavior(RobotPlayer player)
	{
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
	 * This callback is triggered whenever a unit is reactivated.
	 * Useful for lategame strategies, etc.
	 * @param lastActiveRound the last round the robot was reported awake
	 */
	public abstract void onWakeupCallback(int lastActiveRound);
	
	

}
