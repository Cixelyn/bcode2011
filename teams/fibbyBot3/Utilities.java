package fibbyBot3;

import java.util.ArrayList;

import battlecode.common.BuilderController;
import battlecode.common.ComponentController;
import battlecode.common.MovementController;
import battlecode.common.SensorController;
import battlecode.common.WeaponController;

public class Utilities
{
	public static ArrayList<?>[] getComponents(ComponentController[] components)
	{
		ArrayList<BuilderController> builders = new ArrayList<BuilderController>();
		ArrayList<MovementController> motors = new ArrayList<MovementController>();
		ArrayList<SensorController> sensors = new ArrayList<SensorController>();
		ArrayList<WeaponController> weapons = new ArrayList<WeaponController>();
		for(ComponentController c:components)
		{
			switch (c.componentClass())
			{
				case ARMOR: 
					break;
				case BUILDER: builders.add((BuilderController)c);
					break;
				case COMM:
					break;
				case MISC:
					break;
				case MOTOR: motors.add((MovementController)c);
					break;
				case SENSOR: sensors.add((SensorController)c);
					break;
				case WEAPON: weapons.add((WeaponController)c);
					break;
			}
		}
		ArrayList<?>[] componentList = {builders,motors,sensors,weapons};
		return componentList;
	}
}
