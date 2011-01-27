package fibbyBot16.behaviors;


import fibbyBot16.*;
import battlecode.common.*;;

/**
 * 
 * <pre>
 * 
 * 
 *                                                  
 * 
 * 
 * 
 * @author FiBsTeR
 *
 */


public class BunkerBehavior extends Behavior
{
	
	
	private enum MissileTurretBuildOrder
	{
		EQUIPPING,
		SUP,
		SLEEP,
		SUICIDE
	}
	
	MissileTurretBuildOrder obj = MissileTurretBuildOrder.EQUIPPING;

	
	public BunkerBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				Utility.setIndicator(myPlayer, 0, "EQUIPPING");
				
				int numPlasmas = 0;
				for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
				{
					if ( myPlayer.myRC.components()[i].type() == ComponentType.PLASMA )
						numPlasmas++;
				}
				if ( numPlasmas >= 10 )
					obj = MissileTurretBuildOrder.SUP;
					
				return;
			
			case SUP:
				
				Utility.setIndicator(myPlayer, 0, "SUP");
				return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 0, "SLEEP");
				Utility.setIndicator(myPlayer, 1, "zzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
			case SUICIDE:
				
				Utility.setIndicator(myPlayer, 0, "SUICIDE");
				Utility.setIndicator(myPlayer, 1, ":(");
				myPlayer.sleep();
				myPlayer.myRC.suicide();
				return;
				
		}
	}
	
	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}

	@Override
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}

	@Override
	public String toString()
	{
		return "BunkerBehavior";
	}
}
