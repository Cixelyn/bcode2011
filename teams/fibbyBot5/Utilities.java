package fibbyBot5;

import java.util.ArrayList;
import battlecode.common.*;

public class Utilities
{
	private static final int RESERVE = 5;
	
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
		return "idk"; // should be unreachable
	}
	
	public static void buildComponent(RobotController myRC, BuilderController builder, ComponentType component, String desc, int rID)
	{
		try
		{
			myRC.setIndicatorString(2, "Building " + desc + "...");
			while (myRC.getTeamResources() < component.cost + RESERVE || builder.isActive())
			{
				myRC.yield();
			}
			builder.build(component, myRC.getLocation().add(myRC.getDirection()), RobotLevel.ON_GROUND);
			myRC.setIndicatorString(2, "Built " + desc + " on " + Integer.toString(rID));
		}
        catch (Exception e)
        {
            System.out.println("caught exception:");
            e.printStackTrace();
        }
	}
	
	public static Direction spawnOpposite(String spawn)
	{
		if(spawn == "north")
			return Direction.SOUTH;
		if(spawn == "east")
			return Direction.WEST;
		if(spawn == "south")
			return Direction.NORTH;
		if(spawn == "west")
			return Direction.EAST;
		if(spawn == "northwest")
			return Direction.SOUTH_EAST;
		if(spawn == "northeast")
			return Direction.SOUTH_WEST;
		if(spawn == "southwest")
			return Direction.NORTH_EAST;
		if(spawn == "southeast")
			return Direction.NORTH_WEST;
		return Direction.OMNI;
	}
}
