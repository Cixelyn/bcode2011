package finalbot.behaviors;

import battlecode.common.*;
import finalbot.*;

public class DefaultBehavior extends Behavior
{

	public DefaultBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		//Utility.setIndicator(myPlayer, 1, "WHO AM I???");
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
			else if ( c.type() == ComponentType.CONSTRUCTOR && myPlayer.myRC.getChassis() == Chassis.FLYING )
			{
				myPlayer.swapBehavior(new FlyingDroneBehavior(myPlayer));
				Utility.setIndicator(myPlayer, 1, ""); // >:[ Clear out your "WHO AM I" indicator string Max!!! -Jven
			}
			else if ( c.type() == ComponentType.BLASTER && myPlayer.myRC.getChassis() == Chassis.FLYING )
			{
				myPlayer.swapBehavior(new WraithBehavior(myPlayer));
			}
			else if ( c.type() == ComponentType.RADAR && myPlayer.myRC.getChassis() == Chassis.HEAVY )
			{
				myPlayer.swapBehavior(new ColossusBehavior(myPlayer));
			}
			else if ( c.type() == ComponentType.CONSTRUCTOR && myPlayer.myRC.getChassis() == Chassis.HEAVY )
			{
				myPlayer.swapBehavior(new ArbiterBehavior(myPlayer));
			}
			else if ( c.type() == ComponentType.RAILGUN && myPlayer.myRC.getChassis() == Chassis.BUILDING )
			{
				myPlayer.swapBehavior(new MissileTurretBehavior(myPlayer));
			}
			else if ( c.type() == ComponentType.RECYCLER && myPlayer.myRC.getChassis() == Chassis.BUILDING )
			{
				myPlayer.swapBehavior(new RefineryBehavior(myPlayer));
			}
			else if ( c.type() == ComponentType.ARMORY && myPlayer.myRC.getChassis() == Chassis.BUILDING )
			{
				myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
			}
			else if ( c.type() == ComponentType.FACTORY && myPlayer.myRC.getChassis() == Chassis.BUILDING )
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
