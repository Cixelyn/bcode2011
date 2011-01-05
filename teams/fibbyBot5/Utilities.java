package fibbyBot5;

import java.util.ArrayList;

import battlecode.common.BroadcastController;
import battlecode.common.BuilderController;
import battlecode.common.ComponentController;
import battlecode.common.MovementController;
import battlecode.common.SensorController;
import battlecode.common.WeaponController;

public class Utilities
{
	public static ArrayList<?>[] getComponents(ComponentController[] components)
	{
		ArrayList<BroadcastController> broadcasters = new ArrayList<BroadcastController>();
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
				case COMM: broadcasters.add((BroadcastController)c);
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
		ArrayList<?>[] componentList = {broadcasters,builders,motors,sensors,weapons};
		return componentList;
	}
	
	public static String getSpawn(int westEdge, int northEdge, int eastEdge, int southEdge)
	{
		switch ((westEdge+1)*(2*northEdge+1)*(4*eastEdge+1)*(6*southEdge+1))
		{
			case 2:
				return "west";
			case 3:
				return "north";
			case 5:
				return "east";
			case 7:
				return "south";
			case 6:
				return "northwest";
			case 14:
				return "southwest";
			case 15:
				return "northeast";
			case 35:
				return "southeast";
		}
		return "FAILURE"; // should be unreachable
	}
}
