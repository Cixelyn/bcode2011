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
			if ( c == ComponentType.CONSTRUCTOR )
				myPlayer.swapBehavior(new SCVBehavior(myPlayer));
			else if ( c == ComponentType.RECYCLER )
				myPlayer.swapBehavior(new RefineryBehavior(myPlayer));
			else if ( c == ComponentType.ARMORY )
				myPlayer.swapBehavior(new ArmoryBehavior(myPlayer));
			else if ( c == ComponentType.BEAM )
				myPlayer.swapBehavior(new MissileTurretBehavior(myPlayer));
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
}
