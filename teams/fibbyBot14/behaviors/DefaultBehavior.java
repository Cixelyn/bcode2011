package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;

public class DefaultBehavior extends Behavior
{

	public DefaultBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		Utility.setIndicator(myPlayer, 0, "WHO AM I???");
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
			else if ( c == ComponentType.BEAM && myPlayer.myRC.getChassis() == Chassis.FLYING )
				myPlayer.swapBehavior(new HeroWraithBehavior(myPlayer));
			else if ( c == ComponentType.MEDIC && myPlayer.myRC.getChassis() == Chassis.FLYING )
				myPlayer.swapBehavior(new MedivacBehavior(myPlayer));
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
}
