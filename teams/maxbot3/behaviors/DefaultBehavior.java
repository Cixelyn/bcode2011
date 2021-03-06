package maxbot3.behaviors;

import battlecode.common.*;
import maxbot3.*;

public class DefaultBehavior extends Behavior
{

	public DefaultBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		Utility.setIndicator(myPlayer, 0, "WHO AM I???");
		if ( Clock.getRoundNum() > Constants.SENSOR_TOWER_TIME && Clock.getRoundNum() < Constants.SENSOR_TOWER_TIME + 50 )
			myPlayer.swapBehavior(new SensorTowerBehavior(myPlayer));
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
			else if ( c == ComponentType.HAMMER && myPlayer.myRC.getChassis() == Chassis.HEAVY )
				myPlayer.swapBehavior(new HammerBrothersBehavior(myPlayer));
			else if ( c == ComponentType.CONSTRUCTOR && myPlayer.myRC.getChassis() == Chassis.FLYING )
				myPlayer.swapBehavior(new HeroWraithBehavior(myPlayer));
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
}
