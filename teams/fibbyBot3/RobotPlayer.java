package fibbyBot3;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;
	public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

	public void run()
	{
		//ComponentController[] components = myRC.newComponents();
		//System.out.println(myRC.components().length);
		//System.out.println(java.util.Arrays.toString(components));
		//System.out.flush();
		ArrayList<?>[] componentList = Utilities.getComponents(myRC.components());
		ArrayList<?> builders = componentList[0];
		ArrayList<?> motors = componentList[1];
		ArrayList<?> sensors = componentList[2];
		ArrayList<?> weapons = componentList[3];
		if(myRC.getChassis()==Chassis.BUILDING)
		{
			//myRC.setIndicatorString(0, "(refinery)");
			ImRefinery.run(this, myRC, builders, motors, sensors, weapons);
		}
		else if (builders.size()>0)
		{
			//myRC.setIndicatorString(0, "In the rear with the gear!");
			ImSCV.run(this, myRC, builders, motors, sensors, weapons);
		}
		else
		{
			//myRC.setIndicatorString(0, "Go, go, go!");
			ImMarine.run(this, myRC, builders, motors, sensors, weapons);
		}
	}
}
