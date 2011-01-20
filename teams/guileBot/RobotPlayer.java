package guileBot;

import java.util.*;

import guileBot.strategies.*;
import guileBot.behaviors.*;


import battlecode.common.*;

/**
 * MASTeRY: BOT OF DEATH.
 * @author MAx, juSTin, coRY
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
	public JumpController myJump;
	
	private final ArrayList<WeaponController> mySMGsInternal;
	private final ArrayList<WeaponController> myBlastersInternal;
	private final ArrayList<WeaponController> myRailgunsInternal;
	private final ArrayList<WeaponController> myMedicsInternal;
	public WeaponController[] mySMGs;
	public WeaponController[] myBlasters;
	public WeaponController[] myRailguns;
	public WeaponController[] myMedics;
	
	//Helper Subsystems
	public final Messenger myMessenger;
	public final Actions myActions;
	public final Memory myMemory;
	
	
	//Misc Stats
	public final int myBirthday;
	public final MapLocation myBirthplace;
	
	private int executeStartTime;
	private int executeStartByte;
	private int lastActiveRound;
	private double lastRoundHP;
	private int bytecodeLimit;
	public double myLastRes;
	public boolean hasTakenDamage;
	
	public int numKills;
	
	
	public final Team myOpponent;
	
	
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
    	myBirthplace = myRC.getLocation();
    	myDice = new Random(myRC.getRobot().getID()*myBirthday);
    	myLastRes = 9999;
    	bytecodeLimit = GameConstants.BYTECODE_LIMIT_BASE;
    	
    	myOpponent = myRC.getTeam().opponent();
    	
    	lastActiveRound = myBirthday;
    	lastRoundHP = rc.getHitpoints();
    	
    	
    	//initialize base controllers
    	myBuilder = null;
    	myMotor = null;
    	mySensor = null;
    	myBroadcaster = null;
    	
    	mySMGsInternal = new ArrayList<WeaponController>();
    	myBlastersInternal = new ArrayList<WeaponController>();
    	myRailgunsInternal = new ArrayList<WeaponController>();
    	myMedicsInternal = new ArrayList<WeaponController>();
    	mySMGs = new WeaponController[0];
    	myBlasters = new WeaponController[0];
    	myRailguns = new WeaponController[0];
    	
    	
    	myMessenger = new Messenger(this);
    	myActions = new Actions(this);
    	myMemory = new Memory(this);
    	
    	
    	
    	
    	if(Constants.CUSTOM_INDICATORS) {
    		myRC.setIndicatorString(0, Constants.INDICATOR0);
    		myRC.setIndicatorString(1, Constants.INDICATOR1);
    		myRC.setIndicatorString(2, Constants.INDICATOR2);
    	}
    	

    	
    	
    	
    	
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
		//Check if we've just woken up
		if(myRC.wasTurnedOff()){
			myBehavior.onWakeupCallback(lastActiveRound);
		}
		lastActiveRound = Clock.getRoundNum();
		
		
		
		/////////////////////////////////////////////////////////////////
		//Check if we've sustained damage.
		double damage = lastRoundHP - myRC.getHitpoints();
		if(damage>0.1) {
			hasTakenDamage = true;
			myBehavior.onDamageCallback(damage);
		}
		
		
		
		
		
		
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
			
			
			//Yield.
			myRC.yield();
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
		
		//////////////////////////////////////////////////////////////
		// Set some variables and reset some flags.
		myLastRes = myRC.getTeamResources();
		lastRoundHP = myRC.getHitpoints(); 
		hasTakenDamage = false;
		
		//////////////////////////////////////////////////////////////
		
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

				//////////////////////////////////////////////////////////////////
				//WEAPONS ALLOCATIONS
				case WEAPON:
					switch(c.type()) {
					case SMG:
						mySMGsInternal.add((WeaponController)c);		continue;
					case BLASTER:
						myBlastersInternal.add((WeaponController)c);	continue;
					case RAILGUN:
						myRailgunsInternal.add((WeaponController)c);	continue;
					case MEDIC:
						myMedicsInternal.add((WeaponController)c);		continue;
					default:
						Utility.printMsg(this, "WTF IS THIS WEAPON?!"); continue;
					}
					
				/////////////////////////////////////////////////////////////////
				//SENSOR ALLOCATIONS
				case SENSOR:
					mySensor = (SensorController)c; 					continue;
				case BUILDER:
					myBuilder = (BuilderController)c; 					continue;
				case MOTOR:
					myMotor = (MovementController)c;					continue;
				case COMM:
					myBroadcaster = (BroadcastController)c;
					myMessenger.enableSender();
					continue;
				case ARMOR:
					continue;
					
				/////////////////////////////////////////////////////////////////
				//MISC ALLOCATIONS
				case MISC:
					switch(c.type()) {
					case PROCESSOR:
						bytecodeLimit += GameConstants.BYTECODE_LIMIT_ADDON;	continue;
					case JUMP:
						myJump = (JumpController) c;							continue;
					}
				default:
					Utility.printMsg(this, "WTF IS THIS CONTROLLER?!");			continue;
			}
		}	
		
		
		
		//We can afford this expensive call because allocation doesn't happen often.
		//Also, because myWeapons only increases and never decreases, we shouldn't ever get nulls
		mySMGs = mySMGsInternal.toArray(new WeaponController[mySMGsInternal.size()]);
		myBlasters = myBlastersInternal.toArray(new WeaponController[myBlastersInternal.size()]);
		myRailguns = myRailgunsInternal.toArray(new WeaponController[myRailgunsInternal.size()]);
		myMedics = myMedicsInternal.toArray(new WeaponController[myMedicsInternal.size()]);
		
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
		if(executeStartTime!=Clock.getRoundNum()) {
				int currRound = Clock.getRoundNum();
				int byteCount = (bytecodeLimit-executeStartByte) + (currRound-executeStartTime-1) * bytecodeLimit + Clock.getBytecodeNum();
				Utility.printMsg(this,"Warning: Unit over Bytecode Limit @"+executeStartTime+"-"+currRound +":"+ byteCount);
		}	
	}
	
	
	
	
		
}
