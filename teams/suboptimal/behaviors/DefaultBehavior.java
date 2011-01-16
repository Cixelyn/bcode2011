package suboptimal.behaviors;

import battlecode.common.*;
import suboptimal.*;

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
		for ( int i = components.length - 1 ; i >= 0 ; i-- )
		{
			ComponentController c = components[i];
			if ( c.type() == ComponentType.CONSTRUCTOR )
			{
				if ( Clock.getRoundNum() <= 2 )
					myPlayer.swapBehavior(new SCVBehavior(myPlayer));
				else
				{
					myPlayer.swapBehavior(new FlyingDroneBehavior(myPlayer));
					Utility.setIndicator(myPlayer, 1, ""); // >:[ Clear out your "WHO AM I" indicator string Max!!! -Jven
				}
			}
			if ( c.type() == ComponentType.REGEN )
			{
				myPlayer.swapBehavior(new TestHeavyBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.RECYCLER )
			{
				myPlayer.swapBehavior(new RefineryBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.ARMORY )
			{
				myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.FACTORY )
			{
				myPlayer.swapBehavior(new FactoryBehavior(myPlayer));
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
