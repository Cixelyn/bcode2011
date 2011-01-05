package fibbyBot4;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer implements Runnable {

	private final RobotController myRC;
	private final int GUNS = 2;
	private final ComponentType GUNTYPE = ComponentType.BLASTER;
	private final ComponentType SENSORTYPE = ComponentType.SIGHT;
	private final ComponentType COMMTYPE = ComponentType.ANTENNA;
	private final ComponentType ARMORTYPE = ComponentType.SHIELD; 
	private final int MARINES = 2;
	private static final int OLDNEWS = 5;
	private static final int RESERVE = 5;

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
			myRC.setIndicatorString(0, "In the rear with the gear!");
			ImSCV.run(this, myRC, broadcasters, builders, motors, sensors, weapons);
		}
		else
		{
			myRC.setIndicatorString(0, "Go, go, go!");
			ImMarine.run(this, myRC, broadcasters, builders, motors, sensors, weapons);
		}
	}
}
