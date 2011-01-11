package masteryone.behaviors;

import battlecode.common.*;
import masteryone.*;

public class ExpoRefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	int isLeader = -1;
	
	public ExpoRefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch(obj)
    	{
		
    		case EQUIPPING:
    			
    			Utility.setIndicator(myPlayer, 1, "EQUIPPING");
    			Utility.buildComponentOnSelf(myPlayer, ComponentType.ANTENNA);
    			obj = RefineryBuildOrder.DETERMINE_LEADER;
    			return;
    			
    		case WAITING:
    			
    			Utility.setIndicator(myPlayer, 1, "WAITING");
    			return;
    	}
		
	}

	public String toString()
	{
		return "ExpoRefineryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_ID )
		{
			if ( msg.ints[Messenger.firstData] < myPlayer.myRC.getRobot().getID() )
				isLeader = 0;
			else
				isLeader = 1;
		}
	}

}
