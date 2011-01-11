package masteryone.behaviors;

import masteryone.*;
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

}
