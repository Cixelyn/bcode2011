package fibbyBot13.behaviors;

import battlecode.common.*;
import fibbyBot13.*;

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
			if ( c.type() == ComponentType.CONSTRUCTOR && myPlayer.myRC.getChassis() == Chassis.LIGHT )
			{
				myPlayer.swapBehavior(new SCVBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.CONSTRUCTOR && myPlayer.myRC.getChassis() == Chassis.FLYING )
			{
				myPlayer.swapBehavior(new FlyingDroneBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.RECYCLER && myPlayer.myRC.getChassis() == Chassis.BUILDING )
			{
				myPlayer.swapBehavior(new RefineryBehavior(myPlayer));
			}
			if ( c.type() == ComponentType.ARMORY && myPlayer.myRC.getChassis() == Chassis.BUILDING )
			{
				myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
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
