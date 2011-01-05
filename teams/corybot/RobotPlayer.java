package corybot;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer implements Runnable {
	
	
	//Controllers
	final RobotController myRC;
	final ArrayList<WeaponController> myWeapons;
	SensorController mySensor;
	BuilderController myBuilder;
	MovementController myMotor;
	
	//Higher level strategy
	Behavior myBehavior;
	
	
	

    public RobotPlayer(RobotController rc) {
    	
    	//initialize base controllers
    	myBuilder = null;
    	myMotor = null;
    	mySensor = null;
    	myWeapons = new ArrayList<WeaponController>();  	
        myRC = rc;
        
        Behavior myBehavior = null;
        
        
		//allocate initial controllers (sets movement, sensors, etc.)
		allocateControllers(myRC.newComponents());
        
    }

    
	public void run() {
		
		//This is the base entry point for the robot
		//	We need to determine what initial behavior it begins running.
		//	Currently decided based on chassis.
		switch(myRC.getChassis()) {
		case BUILDING:
			myBehavior = new BuilderBehavior(this);
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
			
			
			//First check if we've added new components to the robot
			//and execute the necessary callback
			components=myRC.newComponents();
			if(components.length!=0) {
				allocateControllers(components);
				myBehavior.newComponentCallback(components);
			}
			
			
			//Next, run the robot's behaviors
			try {
				myBehavior.run();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			
			//Lastly, set some debug strings
			myRC.setIndicatorString(0, myBehavior.toString());
			myRC.setIndicatorString(1, Utility.printComponentList(myRC.components()));
			myRC.setIndicatorString(2, Utility.robotMoveInfo(this));
			
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
				break;
			case BUILDER:
				myBuilder = (BuilderController)c;
				break;
			case MOTOR:
				myMotor = (MovementController)c;
				break;
			}
		}		
	}
	
	
	public void swapBehavior(Behavior b) {
		myBehavior = b;
	}
	
	
	
		
}
