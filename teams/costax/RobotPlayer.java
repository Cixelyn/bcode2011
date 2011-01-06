package costax;

import java.util.ArrayList;
import battlecode.common.*;

public class RobotPlayer implements Runnable {
	
	
	//Controllers
	final RobotController myRC;
	
	
	SensorController mySensor;
	BuilderController myBuilder;
	MovementController myMotor;
	BroadcastController myBroadcaster;
	final ArrayList<WeaponController> myWeapons;
	
	//Helper Subsystems
	final Messenger myMessenger;
	final Navigation myNavigation;
	final Scanner myScanner;
	
	
	//Higher level strategy
	Behavior myBehavior;
	
	
	

    public RobotPlayer(RobotController rc) {
    	
    	//this absolutely must be set first
    	myRC = rc;
    	
    	//initialize base controllers
    	myBuilder = null;
    	myMotor = null;
    	mySensor = null;
    	myBroadcaster = null;
    	
    	myWeapons = new ArrayList<WeaponController>();
    	
    	myMessenger = new Messenger(this);
    	myNavigation = new Navigation(this);
    	myScanner = new Scanner(this);
    	
       
        Behavior myBehavior = null;
        
    }

    
	public void run() {
		
		//This is the base entry point for the robot
		//	We need to determine what initial behavior it begins running.
		//	Currently decided based on chassis.
		switch(myRC.getChassis()) {
		case BUILDING:
			myBehavior = new BuildingBehavior(this);
			break;
		case LIGHT:
			myBehavior = new LightBehavior(this);
			break;
		case MEDIUM:
		case HEAVY:
		default:
			System.out.println("Error");
		}
		
		
		/**
		 * This is our main loop for executing code
		 */
		ComponentController[] components;
		while(true) {
			
			
			///////////////////////////////////////////////////////////////
			//Receive all messages
			try {
				myMessenger.receiveAll();
			} catch(Exception e) {e.printStackTrace();}

			
			
			///////////////////////////////////////////////////////////////
			//First check if we've added new components to the robot
			//and execute the necessary callback
			try{
				components=myRC.newComponents();
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


			/////////////////////////////////////////////////////////////
			//Next, run the robot's behaviors
			try {
				myBehavior.run();
			} catch(Exception e) {e.printStackTrace();}
			
			
			
			/////////////////////////////////////////////////////////////
			//Increment the robot's timer
			myBehavior.runtime++;
			
			
			/////////////////////////////////////////////////////////////
			//Send all messages
			try {
				myMessenger.sendAll();
			} catch(Exception e) {e.printStackTrace();}
			
			
			/////////////////////////////////////////////////////////////
			//Lastly, set some debug strings
			myRC.setIndicatorString(0, myBehavior.toString() +" "+ Utility.robotMoveInfo(this));
			myRC.setIndicatorString(1, Utility.printComponentList(myRC.components()));
			
			
			/////////////////////////////////////////////////////////////
			//Then yield
			myRC.yield();
			
		}
		
		
	}
	
	
	public void allocateControllers(ComponentController[] components) {
		
		System.out.println("Added: "+java.util.Arrays.toString(components));
		
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
			default:
				System.out.println("NotController");
				
			}
		}		
	}
	
	
	
	
	
	
	public void swapBehavior(Behavior b) {
		myBehavior = b;
	}
	
	
	
		
}
