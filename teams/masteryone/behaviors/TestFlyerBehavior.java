package masteryone.behaviors;

import battlecode.common.*;
import masteryone.*;

public class TestFlyerBehavior extends Behavior
{

	int num = -1;
	
	public TestFlyerBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		if ( num != -1 )
		{
			Utility.setIndicator(myPlayer, 1, "I'm number " + Integer.toString(num) + "!");
			while ( myPlayer.myMotor.isActive() )
				myPlayer.sleep();
			if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
				myPlayer.myMotor.moveForward();
			else
				myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		}
		else
			Utility.setIndicator(myPlayer, 1, "I'm a flyer...");
	}

	public String toString()
	{
		return "TestFlyerBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{

	}


	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM )
		{
			if ( num == -1 )
				num = msg.ints[Messenger.firstData];
		}
	}
}
