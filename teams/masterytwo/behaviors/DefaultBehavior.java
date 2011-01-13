package masterytwo.behaviors;

import battlecode.common.*;
import masterytwo.*;

public class DefaultBehavior extends Behavior
{

	public DefaultBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		Utility.setIndicator(myPlayer, 1, "WHO AM I???");
	}

	public String toString()
	{
		return "DefaultBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		for ( ComponentController c : components )
		{
			if ( c.type() == ComponentType.CONSTRUCTOR )
			{
				if ( Clock.getRoundNum() <= 2 )
					myPlayer.swapBehavior(new SCVBehavior(myPlayer));
				else
					myPlayer.swapBehavior(new FlyingDroneBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.BLASTER )
			{
				myPlayer.swapBehavior(new MarineBehavior(myPlayer));
				myPlayer.myScanner.setDetectionMode(Robot.class);
			}
			if ( c.type() == ComponentType.RECYCLER )
			{
				myPlayer.swapBehavior(new RefineryBehavior(myPlayer));
				myPlayer.myScanner.setDetectionMode(Robot.class);
			}
			if ( c.type() == ComponentType.ARMORY )
			{
				myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
				myPlayer.myScanner.setDetectionMode(Robot.class);
			}
		}
	}


	public void newMessageCallback(MsgType type, Message msg)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
}
