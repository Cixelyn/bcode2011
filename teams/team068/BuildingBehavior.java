package team068;

import battlecode.common.*;

public class BuildingBehavior extends Behavior
{

	public BuildingBehavior(RobotPlayer player)
	{
		super(player);
	}



	public void run() throws Exception
	{
		myPlayer.swapBehavior(new RefineryBehavior(myPlayer));
	}

	public String toString()
	{
		return "BuildingBehavior";
	}

	public void newComponentCallback(ComponentController[] components)
	{

	}
	
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		
	}
}
