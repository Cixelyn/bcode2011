package fibbyBot2;

import battlecode.common.*;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;
	private final int SMGS = 5;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

	public void run() {
		ComponentController[] components = myRC.newComponents();
		System.out.println(myRC.components().length);
		//System.out.println(java.util.Arrays.toString(components));
		//System.out.flush();
		if(myRC.getChassis()==Chassis.BUILDING)
			runBuilder((MovementController)components[0],(SensorController)components[1], (BuilderController)components[2]);
		else
			runMotor((MovementController)components[0]);
	}

	public void runBuilder(MovementController motor, SensorController weakSensor, BuilderController builder) {
		boolean initialized = false;
		SensorController sensor = weakSensor;
		int guns;
		int rGuns;
		int rID;
		RobotInfo rInfo;
		GameObject[] nearbyRobots;
		while (true) {
            try {
				myRC.yield();
				guns = 0;
				for(ComponentController c:myRC.components())
				{
					if (c.type()==ComponentType.SMG)
						guns = guns+1;
				}
				myRC.setIndicatorString(0, "I haz "+Integer.toString(guns)+" guns!");
            	if (!initialized)
            	{
            		while(myRC.getTeamResources()<2*ComponentType.RADAR.cost)
            		{
            			myRC.yield();
            		}
            		initialized = true;
            		builder.build(ComponentType.RADAR,myRC.getLocation(),RobotLevel.ON_GROUND);
            		myRC.setIndicatorString(1, "Radar installed!");
					for(ComponentController c:myRC.components())
					{
						if (c.type()==ComponentType.RADAR)
						{
							sensor = (SensorController)c;
						}
					}
					myRC.yield();
            	}

				nearbyRobots = sensor.senseNearbyGameObjects(GameObject.class);
				System.out.println("nearbyRobots : "+Integer.toString(nearbyRobots.length));
				for (GameObject r:nearbyRobots)
				{
					if(r.getTeam()!=Team.NEUTRAL && myRC.getTeamResources()>=2*ComponentType.SMG.cost)
					{
						rID = r.getID();
						rInfo = sensor.senseRobotInfo((Robot)r);
						//System.out.println("Robot"+Integer.toString(rID)+" : "+rInfo.components);
						if(r.getID() > 100 && rInfo.chassis == Chassis.LIGHT && myRC.getLocation().distanceSquaredTo(rInfo.location)<4)
						{
							rGuns = 0;
							for(ComponentType c:rInfo.components)
							{
								if (c==ComponentType.SMG)
									rGuns = guns+1;
							}
							if (rGuns<SMGS)
							{
								motor.setDirection(myRC.getLocation().directionTo(rInfo.location));
								myRC.yield();
								boolean built = false;
								while (!built)
								{
									if (myRC.getTeamResources()>=2*ComponentType.SMG.cost)
									{
										built = true;
										builder.build(ComponentType.SMG,rInfo.location,RobotLevel.ON_GROUND);
										//builder.build(ComponentType.SMG,myRC.getLocation().add(myRC.getDirection()),RobotLevel.ON_GROUND);
										myRC.setIndicatorString(2, "Just placed gun on "+Integer.toString(rID)+".");
									}
									myRC.yield();
								}
							}
						}
					}
					myRC.yield();
				}
				if(!motor.canMove(myRC.getDirection()))
				{
					motor.setDirection(myRC.getDirection().rotateRight());
					myRC.yield();
				}
				else if(myRC.getTeamResources()>=Chassis.LIGHT.cost)
				{
					builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()));
					myRC.yield();
				}

            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}

    public void runMotor(MovementController motor) {
        int guns;
        while (true) {
            try {
                /*** beginning of main loop ***/
                guns = 0;
				for(ComponentController c:myRC.components())
				{
					if (c.type()==ComponentType.SMG)
						guns = guns+1;
				}
				myRC.setIndicatorString(1, "I haz "+Integer.toString(guns)+" guns!");
                if (myRC.getRobot().getID()<=100 || guns >= SMGS)
                {
	                if (motor.canMove(myRC.getDirection())) {
	                   motor.moveForward();
	                   myRC.yield();
	                   myRC.yield();
	                   myRC.yield();
	                } else {
	                    motor.setDirection(myRC.getDirection().rotateLeft());
	                }
                }
                myRC.yield();

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }
}
