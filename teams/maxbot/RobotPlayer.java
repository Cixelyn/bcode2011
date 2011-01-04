package maxbot;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }
    
    public void run() {
		ComponentController [] components = myRC.newComponents();
		System.out.println(java.util.Arrays.toString(components));
		System.out.flush();
		MovementController motor=(MovementController)components[0];
		Navigation robotNavigation=new Navigation(this,myRC,motor);
		MapLocation destination=myRC.getLocation().add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTH);
		if(myRC.getChassis()==Chassis.LIGHT) {
			for (ComponentController componentController : components) {
				if (componentController.type().equals(ComponentType.CONSTRUCTOR)) {
					while (true) {
						myRC.yield();
						Direction direction = robotNavigation.bugTo(destination);
						try {
							motor.setDirection(direction);
							myRC.yield();
							motor.moveForward();
							myRC.yield();
						} catch (GameActionException e) {
							e.printStackTrace();
						}
					}
				}
			}
			while (true) {
				myRC.yield();
			}
		}
		else {
			while (true) {
				myRC.yield();
			}
		}
    }

	/*public void run() {
		ComponentController [] components = myRC.newComponents();
		System.out.println(java.util.Arrays.toString(components));
		System.out.flush();
		if(myRC.getChassis()==Chassis.BUILDING)
			runBuilder((MovementController)components[0],(BuilderController)components[2]);
		else {
			while (true) {
            	int guns=0;
            	for (ComponentController componentControl : myRC.components()) {
            		if (componentControl.type().equals(ComponentType.SMG)) {
            			guns=guns+1;
            		}
            	}
            	myRC.setIndicatorString(0, Integer.toString(guns));
				myRC.yield();
			}
		}
		//else
			//runMotor((MovementController)components[0]);
	}

	public void testit(MovementController m) {
		m.withinRange(myRC.getLocation());
	}

	public void runBuilder(MovementController motor, BuilderController builder) {
	
		while (true) {
            try {
            	myRC.setIndicatorString(1,myRC.getDirection().toString());
            	int guns=0;
            	for (ComponentController componentControl : myRC.components()) {
            		if (componentControl.type().equals(ComponentType.SMG)) {
            			guns=guns+1;
            		}
            	}
            	myRC.setIndicatorString(0, Integer.toString(guns));

				myRC.yield();

				if(!motor.canMove(myRC.getDirection())) {
					motor.setDirection(myRC.getDirection().rotateRight());
					myRC.yield();
				}
				if(myRC.getTeamResources()>=2*Chassis.LIGHT.cost+ComponentType.SMG.cost) {
					
					MapLocation location=myRC.getLocation().add(myRC.getDirection());
					
					builder.build(Chassis.LIGHT,location);
					myRC.yield();
					
					location=myRC.getLocation().add(myRC.getDirection());
					builder.build(ComponentType.SMG,location,RobotLevel.ON_GROUND);
				}

            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}

    public void runMotor(MovementController motor) {
        
        while (true) {
            try {
                *//*** beginning of main loop ***//*
                while (motor.isActive()) {
                    myRC.yield();
                }

                if (motor.canMove(myRC.getDirection())) {
                    //System.out.println("about to move");
                    motor.moveForward();
                } else {
                    motor.setDirection(myRC.getDirection().rotateRight());
                }

                *//*** end of main loop ***//*
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }*/
}

