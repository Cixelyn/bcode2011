package fibbyBot4;

import battlecode.common.RobotController;
import java.util.ArrayList;

public class ImExpo
{
	public static void run(RobotPlayer player, RobotController myRC, ArrayList<?> broadcasters, ArrayList<?> builders, ArrayList<?> motors, ArrayList<?> sensors, ArrayList<?> weapons)
	{
		while(true)
		{
			try
			{
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
