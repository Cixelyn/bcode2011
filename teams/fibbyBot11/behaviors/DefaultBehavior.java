package fibbyBot11.behaviors;

import battlecode.common.*;
import fibbyBot11.*;

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
			if ( c.type() == ComponentType.PROCESSOR )
			{
				myPlayer.swapBehavior(new TankBehavior(myPlayer));
				myPlayer.myScanner.setDetectionMode(Robot.class);
			}
			if ( c.type() == ComponentType.RECYCLER )
			{
				if ( Clock.getRoundNum() <= 2 )
					myPlayer.swapBehavior(new MainRefineryBehavior(myPlayer));
				else
					myPlayer.swapBehavior(new ExpoRefineryBehavior(myPlayer));
				myPlayer.myScanner.setDetectionMode(Robot.class);
			}
			if ( c.type() == ComponentType.ARMORY )
			{
				myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
				myPlayer.myScanner.setDetectionMode(Robot.class);
			}
			if ( c.type() == ComponentType.FACTORY )
			{
				myPlayer.swapBehavior(new FactoryBehavior(myPlayer));
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
