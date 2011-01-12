package plasmabot2.behaviors;

import plasmabot2.*;
import battlecode.common.*;

public class FactoryBehavior extends Behavior
{

	public FactoryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		Utility.setIndicator(myPlayer, 1, "FACTORY");
	}

	public String toString()
	{
		return "FactoryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
			
	}
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound) {}
	public void onDamageCallback(double damageTaken) {}

}
