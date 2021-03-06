package fibbyBot15.behaviors;

import battlecode.common.*;
import fibbyBot15.*;

public class DefaultBehavior extends Behavior
{

	public DefaultBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		Utility.setIndicator(myPlayer, 0, "WHO AM I???");
		// new towers should spawn turned off to save flux
		if ( Clock.getRoundNum() > 500 && myPlayer.myRC.getChassis() == Chassis.BUILDING )
			myPlayer.myRC.turnOff();
	}

	public String toString()
	{	
		return "DefaultBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		for ( int i = components.length ; --i >= 0 ; )
		{
			ComponentType c = components[i].type();
			if ( c == ComponentType.CONSTRUCTOR && myPlayer.myRC.getChassis() == Chassis.LIGHT )
				myPlayer.swapBehavior(new SCVBehavior(myPlayer));
			else if ( c == ComponentType.RECYCLER && myPlayer.myRC.getChassis() == Chassis.BUILDING )
				myPlayer.swapBehavior(new RefineryBehavior(myPlayer));
			else if ( c == ComponentType.ARMORY && myPlayer.myRC.getChassis() == Chassis.BUILDING )
				myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
			else if ( c == ComponentType.FACTORY && myPlayer.myRC.getChassis() == Chassis.BUILDING )
				myPlayer.swapBehavior(new FactoryBehavior(myPlayer));
			else if ( c == ComponentType.BEAM && myPlayer.myRC.getChassis() == Chassis.BUILDING )
				myPlayer.swapBehavior(new MissileTurretBehavior(myPlayer));
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
}
