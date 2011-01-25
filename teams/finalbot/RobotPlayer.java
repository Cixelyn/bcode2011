package finalbot; import java.util.*; import finalbot.strategies.*; import finalbot.behaviors.*; import battlecode.common.*;

/**  
 *<pre>  
 *
 *  _____ __   _      _______ _     _ _______       ______ _______ _______  ______
 *    |   | \  |         |    |_____| |______      |_____/ |______ |_____| |_____/
 *  __|__ |  \_|         |    |     | |______      |    \_ |______ |     | |    \_
 *  _  _  _ _____ _______ _     _      _______ _     _ _______        ______ _______ _______  ______
 *  |  |  |   |      |    |_____|         |    |_____| |______       |  ____ |______ |_____| |_____/
 *  |__|__| __|__    |    |     |         |    |     | |______       |_____| |______ |     | |    \_
 *  
 *                                            TEAM #068
 *  __________________________       B A T T L E C O D E 2 0 1 1      ________________________________
 *  _______________________   \______________________________________/   _____________________________
 *                         \____________________________________________/
 *
 *   							
 *   
 *   __
 *	/  \ NOTES ___________________________________________________________ 
 *  \__/
 * 
 * Herein lies the source for team 068's bot.  We have labored  many hours over
 * the course of IAP in order to produce this masterwork of art.  As you browse
 * through our source, note the pains taken to make the most beautiful,
 * intelligent, and cunning bot possible.  Note that even our ASCII art is javadoc 
 * compliant and renders beautifully in editor in which this warrior was forged.
 * 
 *   __
 *	/  \ TABLE OF CONTENTS_________________________________________________ 
 *  \__/
 *
 * [Main]
 *    RobotPlayer.java 		- The Main VM entry-point
 *    
 * [Broadcasting]
 *    Messenger.java		- Messenging send and receive functions
 *    MsgType.java			- Types of messages you can send and receive.
 *    Encoder.java			- Does bit-shifting to pack data into ints for messenger
 *    
 * [Navigation]
 *    OldNavigation.java	- Implements a rudimentary bugnav
 *    Cartographer.java  	- Calculates map center based on map edges
 *    MapStoreBoolean.java	- This class allows a boolean to be set per map location
 *    JumpTable.java		- This class returns in order the possible jump locations for a robot
 *    
 * [Misc]
 * 	  Utility.java			- We just put a bunch of random stuff in here....
 *    Constants.java		- All of our tweakables go in here.
 *    Memory.java			- Round-to-round memory encoding and decoding functions go here.
 * 
 * [Behaviors]
 * 		A behavior is a state machine that defines the actions of a particular unit.
 * 		All behaviors extend Behavior.java
 * 
 * [Strategy]
 * 		A strategy is a set of behaviors that the bots should run on a particular round.
 *      All strategies extend Strategy.java
 *    
 *
 *
 *
 *
 *</pre>
 *         
 * @author Max Nelson
 * @author Cory Li
 * @author Justin Venezuela
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
 * @thanks Thanks to glassgiant.com for their ASCII art JPG-TXT converter -JVen
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
	
	
	private final ArrayList <JumpController> myJumpsInternal;
	public JumpController[] myJumps;
	
	private final ArrayList<WeaponController> mySMGsInternal;
	private final ArrayList<WeaponController> myBlastersInternal;
	private final ArrayList<WeaponController> myRailgunsInternal;
	private final ArrayList<WeaponController> myMedicsInternal;
	private final ArrayList<WeaponController> myHammersInternal;
	private final ArrayList<WeaponController> myBeamsInternal;
	
	public WeaponController[] mySMGs;
	public WeaponController[] myBlasters;
	public WeaponController[] myRailguns;
	public WeaponController[] myMedics;
	public WeaponController[] myHammers;
	public WeaponController[] myBeams;
	
	//Helper Subsystems
	public final Messenger myMessenger;
	public final Actions myActions;
	public final Memory myMemory;
	public final Cartographer myCartographer;
	
	
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
	public boolean hasSensor;
	
	public MapLocation myLoc;
	
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
    	
    	myJumpsInternal = new ArrayList<JumpController>();
    	mySMGsInternal = new ArrayList<WeaponController>();
    	myBlastersInternal = new ArrayList<WeaponController>();
    	myRailgunsInternal = new ArrayList<WeaponController>();
    	myMedicsInternal = new ArrayList<WeaponController>();
    	myHammersInternal = new ArrayList<WeaponController>();
    	myBeamsInternal = new ArrayList<WeaponController>();
    	
    	mySMGs = new WeaponController[0];
    	myBlasters = new WeaponController[0];
    	myRailguns = new WeaponController[0];
    	myMedics = new WeaponController[0];
    	myHammers = new WeaponController[0];
    	myBeams = new WeaponController[0];
    	
    	
    	myMessenger = new Messenger(this);
    	myActions = new Actions(this);
    	myMemory = new Memory(this);
    	myCartographer = new Cartographer(this);
    	
    	
    	
    	
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
    	myStrategy = new DefaultStrategy();
    	
    	
    	//Now based on the strategy, get what our behavior should be
    	myBehavior = myStrategy.selectBehavior(this, Clock.getRoundNum());
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
		//if(Constants.DEBUG_BYTECODE_OVERFLOW) startClock();
		
		
		///////////////////////////////////////////////////////////////
		//Set some global information
		myLoc = myRC.getLocation();
		
		
		
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
		
		
		
		////////////////////////////////////////////////////////////////
		//Run the map sensing code
		if(hasSensor) {myCartographer.runSensor();}
		
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
		//if(Constants.DEBUG_BYTECODE_OVERFLOW) stopClock();
		
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
		//if(Constants.DEBUG_BYTECODE_OVERFLOW) stopClock();
		myRC.yield();
		//if(Constants.DEBUG_BYTECODE_OVERFLOW) startClock();
	}
	
	
	
	/**
	 * This functions turns off a robot, while still allowing for proper byte-code counting.
	 */
	public void shutdown() {
		myRC.turnOff();
		//if(Constants.DEBUG_BYTECODE_OVERFLOW) startClock(); 	//we need to reset our clock.
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
						mySMGsInternal.add((WeaponController)c);		
						continue;
					case BLASTER:
						myBlastersInternal.add((WeaponController)c);	
						continue;
					case RAILGUN:
						myRailgunsInternal.add((WeaponController)c);	
						continue;
					case MEDIC:
						myMedicsInternal.add((WeaponController)c);		
						continue;
					case HAMMER:
						myHammersInternal.add((WeaponController)c);
						continue;
					case BEAM:
						myBeamsInternal.add((WeaponController)c);
						continue;
					default:
						Utility.printMsg(this, "WTF IS THIS WEAPON?!"); 
						continue;
					}
					
				/////////////////////////////////////////////////////////////////
				//SENSOR ALLOCATIONS
				case SENSOR:
					hasSensor = true;
					mySensor = (SensorController)c; 					
					myCartographer.setSensor(mySensor);					continue;					
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
						myJumpsInternal.add((JumpController)c);					continue;
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
		myJumps = myJumpsInternal.toArray(new JumpController[myJumpsInternal.size()]);
		myHammers = myHammersInternal.toArray(new WeaponController[myHammersInternal.size()]);
		myBeams = myBeamsInternal.toArray(new WeaponController[myBeamsInternal.size()]);
	}
	
	
	/**
	 * Returns the robot's age (number of rounds it has lived)
	 * @return
	 */
	public int getAge() {
		return Clock.getRoundNum() - myBirthday;
	}
	
	
	
	/*
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
	}*/
	
	
	
	
		
}
