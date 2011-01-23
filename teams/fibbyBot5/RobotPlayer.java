package fibbyBot5;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

	public void run()
	{
		ArrayList<?>[] componentList = Utilities.getComponents(myRC.components());
		ArrayList<?> broadcasters = componentList[0];
		ArrayList<?> builders = componentList[1];
		ArrayList<?> motors = componentList[2];
		ArrayList<?> sensors = componentList[3];
		ArrayList<?> weapons = componentList[4];
		if(myRC.getChassis()==Chassis.BUILDING && builders.size() > 0)
		{
			myRC.setIndicatorString(0, "(refinery)");
			ImRefinery.run(this, myRC, broadcasters, builders, motors, sensors, weapons);
		}
		else if(myRC.getChassis()==Chassis.BUILDING && builders.size() == 0)
		{
			myRC.setIndicatorString(0, "(expo)");
			ImExpo.run(this, myRC, broadcasters, builders, motors, sensors, weapons);
		}
		else if (builders.size()>0)
		{
			myRC.setIndicatorString(0, "(SCV)");
			ImSCV.run(this, myRC, broadcasters, builders, motors, sensors, weapons);
		}
		else
		{
			myRC.setIndicatorString(0, "(marine)");
			ImMarine.run(this, myRC, broadcasters, builders, motors, sensors, weapons);
		}
	}
}
