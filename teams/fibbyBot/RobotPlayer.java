package fibbyBot;

import battlecode.common.*;
//import java.util.HashMap;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

	public void run() {
		ComponentController[] components = myRC.newComponents();
		System.out.println(java.util.Arrays.toString(components));
		System.out.flush();
		if(myRC.getChassis()==Chassis.BUILDING)
			runBuilder((MovementController)components[0],(SensorController)components[1], (BuilderController)components[2]);
		else
			runMotor((MovementController)components[0]);
	}

	public void runBuilder(MovementController motor, SensorController sensor, BuilderController builder) {
	
		while (true) {
            try {

				myRC.yield();
				//HashMap<Integer, Integer> guns = new HashMap<Integer, Integer>();

				GameObject[] nearbyRobots = sensor.senseNearbyGameObjects(GameObject.class);
				System.out.println("nearbyRobots : "+nearbyRobots.toString());
				for (GameObject r:nearbyRobots)
				{
					if(myRC.getTeamResources()>=ComponentType.SMG.cost)
					{
						int rID = r.getID();
						RobotInfo rInfo = sensor.senseRobotInfo((Robot)r);
						System.out.println("Robot"+Integer.toString(rID)+" : "+rInfo.components);
						if(r.getID() > 100 && rInfo.chassis == Chassis.LIGHT && rInfo.components.length < 7)
						{
							//guns.put(rID, guns.get(rID)+1);
							builder.build(ComponentType.SMG,rInfo.location,RobotLevel.ON_GROUND);
						}
					}		
				}
				if(!motor.canMove(myRC.getDirection()))
					motor.setDirection(myRC.getDirection().rotateRight());
				else if(myRC.getTeamResources()>=Chassis.LIGHT.cost)
					builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()));

            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}

    public void runMotor(MovementController motor) {
        
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (motor.isActive()) {
                    myRC.yield();
                }
                if (myRC.getRobot().getID()<=100 || (myRC.getRobot().getID() > 100 && myRC.components().length >= 7))
                {
	                if (motor.canMove(myRC.getDirection())) {
	                    //System.out.println("about to move");
	                   // motor.moveForward();
	                } else {
	                    motor.setDirection(myRC.getDirection().rotateRight());
	                }
                }

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
