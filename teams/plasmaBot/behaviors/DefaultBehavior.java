package plasmaBot.behaviors;

import battlecode.common.*;
import plasmaBot.*;

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
					myPlayer.swapBehavior(new TestFlyerBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.BLASTER )
				myPlayer.swapBehavior(new MarineBehavior(myPlayer));
			if ( c.type() == ComponentType.RECYCLER )
			{
				if ( Clock.getRoundNum() <= 2 )
					myPlayer.swapBehavior(new MainRefineryBehavior(myPlayer));
				else
					myPlayer.swapBehavior(new ExpoRefineryBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.ARMORY )
				myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
			if ( c.type() == ComponentType.FACTORY )
				myPlayer.swapBehavior(new FactoryBehavior(myPlayer));
		}
	}


	public void newMessageCallback(MsgType type, Message msg)
	{
		
	}
	public void onWakeupCallback(int lastActiveRound) {}
	
}
