package costax3;

import java.util.ArrayList;
import java.util.Random;

import costax3.behaviors.Behavior;
import costax3.strategy.*;

import battlecode.common.*;

/**
 * COSTAX: BOT OF DEATH.
 * @author COry, juSTin, mAX
 * @author Team 068, "In the Rear, With the Gear"
 * @version 1.0
 * @since Battlecode 2011
 * 
 * @lyrics 
 * <pre> So…
 * Don’t you worry ’bout me
 * I won’ let you be alone
 * One day we’ll live in peace
 * In our Supply Depot home
 * If I gotta leave
 * Let this song be our goodbye
 * Ohhhhhhh
 * Your love will always be my guide
 * back to the mineral line…. <3 
 * </pre>
 * 
 * 
 * @see <a href="http://www.youtube.com/watch?v=8zwP9ErgIWs">SCV Love Song</a>
 *
 */
public class RobotPlayer implements Runnable {
	
	
	//Controllers
	public final RobotController myRC;
	
	public SensorController mySensor;
	public BuilderController myBuilder;
	public MovementController myMotor;
	public BroadcastController myBroadcaster;
	public final ArrayList<WeaponController> myWeapons;
	
	//Helper Subsystems
	public final Messenger myMessenger;
	public final Navigation myNavigation;
	public final Scanner myScanner;
	
	
	//Misc Stats
	private final int myBirthday;
	private int executeStartTime;
	private int executeStartByte;
	
	
	//Useful Toolkits
	public final Random myDice;
	
	
	
	//Higher level strategy
	public Behavior myBehavior;
	public final Strategy myStrategy;
	
	
	/**
	 * The main entry-point into the battlecode vm.
	 * @param rc the robot's base robot controller
	 */
    public RobotPlayer(RobotController rc) {
    	
    	//this absolutely must be set first
    	myRC = rc;
    	
    	//variables and utilities that other pieces depend on
    	myBirthday = Clock.getRoundNum();
    	myDice = new Random(myRC.getRobot().getID()*myBirthday);
    	
    	//initialize base controllers
    	myBuilder = null;
    	myMotor = null;
    	mySensor = null;
    	myBroadcaster = null;
    	myWeapons = new ArrayList<WeaponController>();
    	myMessenger = new Messenger(this);
    	myNavigation = new Navigation(this);
    	myScanner = new Scanner(this);
    	
    	
    	
    	
    	
    	//////////////////////////////////////////////////////////////////////////////////////////////////////
    	//
    	//		IF YOU WANT TO CHANGE THE SET OF ROBOT DEFAULT BEHAVIORS
    	//		THEN LOOK AT THE FOLLOWING LINES
    	//
    	//		BASICALLY MAKE A NEW STRATEGY THAT DEFINES WHAT ALL THE DEFAULT STRATEGIES SHOULD BE
    	//		DON'T BREAK PLZ OK.
    	//
    	/////////////////////////////////////////////////////////////////////////////////////////////////////
    	//Setup the initial strategy based on team memory
    	if(myRC.getTeamMemory()[0]==0) {
    		myStrategy = new DefaultStrategy();
    	} else {
    		myStrategy = null;
    	}
    	
    	//Now based on the strategy, get what our behavior should be
    	myBehavior = myStrategy.selectBehavior(this, Clock.getBytecodeNum());
    	////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    }



	/**
	 * This function runs all the calls that a robot should do before executing its behaviors
	 * @see #run()
	 * @see #postRun()
	 */
	private void preRun() {
		
		///////////////////////////////////////////////////////////////
		//Begin Debug Routines		
		if(Constants.DEBUG_BYTECODE_OVERFLOW) startClock();
		
		
		
		///////////////////////////////////////////////////////////////
		//Receive all messages
		if(myMessenger.shouldReceive) {
			try {
				myMessenger.receiveAll();
			} catch(Exception e) {e.printStackTrace();}
		}

		
		
		///////////////////////////////////////////////////////////////
		//First check if we've added new components to the robot
		//and execute the necessary callback
		try{
			ComponentController[] components=myRC.newComponents();
			if(components.length!=0) {
				allocateControllers(components);
				myBehavior.newComponentCallback(components);
			}
		} catch(Exception e) {e.printStackTrace();}
		
		
		/////////////////////////////////////////////////////////////
		//Run the scanning subsystems
		try {
			myScanner.InitialScan();				
		} catch(Exception e) {e.printStackTrace();}
	}
	
	
	/**
	 * The main run function 
	 */
	public void run() {
		
		//Our Main loop for running code
		while(true) {
			
			//Run Preflight Operations
			preRun();
			
			try {
				myBehavior.run();
			} catch(Exception e) {e.printStackTrace();}
			
			//Run Postflight operations
			postRun();
		}	
	}


	/**
	 * This function does all the calls that a robot should do after its behaviors.
	 * @see Messenger#sendAll()
	 */
	private void postRun() {			

		
		/////////////////////////////////////////////////////////////
		//Send all messages
		try {
			myMessenger.sendAll();
		} catch(Exception e) {e.printStackTrace();}
		
		
		
		
		//////////////////////////////////////////////////////////////
		//Run our debug routines.
		if(Constants.DEBUG_BYTECODE_OVERFLOW) stopClock();
		
		/////////////////////////////////////////////////////////////
		//Then yield
		myRC.yield();
		
	}
	
	
	
	/**
	 * This function allows the writing of more procedural code rather
	 * than a function-based state machine.  Call <code>sleep</code> whenever 
	 * want to run an in-sequence yield.  This allows the message handling and
	 * the component allocations to still take place.
	 * @see #postRun()
	 * @see #preRun()
	 */
	public void sleep() {
		postRun();
		myRC.yield();
		preRun();
	}
	
	
	
	
	/**
	 * Better have a good reason for running <code>minSleep</code> rather than normal {@link #sleep()}
	 * since none of the standard message processing or sensor scans happen.
	 */
	public void minSleep() {
		if(Constants.DEBUG_BYTECODE_OVERFLOW) stopClock();
		myRC.yield();
		if(Constants.DEBUG_BYTECODE_OVERFLOW) startClock();
	}
	
	
	
	
	/**
	 * This function swaps the current running behavior of the bot.
	 * @see #myBehavior
	 * @param b
	 */
	public void swapBehavior(Behavior b) {
		myBehavior = b;
	}


	/**
	 * This system allocates the controller stack based on passed in components.  The allocation happens in {@link #preRun()}.
	 * @param components
	 */
	private void allocateControllers(ComponentController[] components) {
		
		//System.out.println("Added: "+java.util.Arrays.toString(components));
		
		for(ComponentController c : components) {
			switch(c.componentClass()) {
			case WEAPON:
				myWeapons.add((WeaponController)c);
				break;
			case SENSOR:
				mySensor = (SensorController)c;
				myScanner.enableScanner();
				break;
			case BUILDER:
				myBuilder = (BuilderController)c;
				break;
			case MOTOR:
				myMotor = (MovementController)c;
				break;
			case COMM:
				myBroadcaster = (BroadcastController)c;
				myMessenger.enableSender();
				break;
			case ARMOR:
				break;
			case MISC:
				break;
			default:
				System.out.println("NotController");
				
			}
		}		
	}
	
	
	/**
	 * Returns the robot's age (number of rounds it has lived)
	 * @return
	 */
	public int getAge() {
		return Clock.getRoundNum() - myBirthday;
	}
	
	
	
	
	public void startClock() {
		executeStartTime = Clock.getRoundNum();
		executeStartByte = Clock.getBytecodeNum();
		
		
		
	}
	
	public void stopClock() {
		if(Constants.DEBUG_BYTECODE_OVERFLOW){
			if(executeStartTime!=Clock.getRoundNum()) {
				int byteCount = (6000-executeStartByte) + (Clock.getRoundNum()-executeStartTime-1) * 6000 + Clock.getBytecodeNum();
				System.out.println("Warning: Unit over Bytecode Limit: "+ byteCount);
			}
		}		
	}
	
	
	
	
		
}
