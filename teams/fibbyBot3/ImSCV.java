package fibbyBot3;


import battlecode.common.*;
import java.util.ArrayList;

public class ImSCV
{
	
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{
		//SensorController sensor = (SensorController)sensors.get(0);
		//MovementController motor = (MovementController)motors.get(0);
		//BuilderController builder = (BuilderController)builders.get(0);
		//Navigation robotNavigation=new Navigation(this,myRC,motor);
		
        while (true)
        {
            try
            {
            	//myRC.setIndicatorString(0, Integer.toString(Clock.getRoundNum())); // DEBUG
				/*else if (!motor.isActive())
                {
					if (motor.canMove(myRC.getDirection())) {
	                   motor.moveForward();
	                } else {
	                    motor.setDirection(myRC.getDirection().rotateLeft());
	                }
                }*/
                myRC.yield();
            }
            catch (Exception e)
            {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
	}
}
