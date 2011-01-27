package fibbyBot16.behaviors;

import battlecode.common.*;
import fibbyBot16.*;

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
		if ( Clock.getRoundNum() > 500 && Clock.getRoundNum() < Constants.CAMP_TIME && myPlayer.myRC.getChassis() == Chassis.BUILDING )
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
			{
				if ( Clock.getRoundNum() < Constants.CAMP_TIME )
					myPlayer.swapBehavior(new SCVBehavior(myPlayer));
				else if ( Clock.getRoundNum() < Constants.BUNKER_TIME )
					myPlayer.swapBehavior(new ProbeBehavior(myPlayer));
				else
					myPlayer.swapBehavior(new MuleBehavior(myPlayer));
			}
			else if ( c == ComponentType.RECYCLER && myPlayer.myRC.getChassis() == Chassis.BUILDING )
			{
				if ( Clock.getRoundNum() < Constants.CAMP_TIME )
					myPlayer.swapBehavior(new RefineryBehavior(myPlayer));
				else
					myPlayer.swapBehavior(new AssimiliatorBehavior(myPlayer));
			}
			else if ( c == ComponentType.PLATING && myPlayer.myRC.getChassis() == Chassis.BUILDING )
				myPlayer.swapBehavior(new PylonBehavior(myPlayer));
			else if ( c == ComponentType.PLASMA && myPlayer.myRC.getChassis() == Chassis.BUILDING )
				myPlayer.swapBehavior(new BunkerBehavior(myPlayer));
			else if ( c == ComponentType.ARMORY && myPlayer.myRC.getChassis() == Chassis.BUILDING )
			{
				if ( Clock.getRoundNum() < Constants.BUNKER_TIME )
					myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
				else
					myPlayer.swapBehavior(new ScienceFacilityBehavior(myPlayer));
			}
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
