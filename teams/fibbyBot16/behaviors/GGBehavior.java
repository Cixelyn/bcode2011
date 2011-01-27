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
 * </pre>
 * 
 * @author FiBsTeR
 *
 */


public class GGBehavior extends Behavior
{

	
	public GGBehavior(RobotPlayer player)
	{
		super(player);
	}
	
	public void run() throws Exception
	{
		Utility.setIndicator(myPlayer, 0, "GG");
		Utility.setIndicator(myPlayer, 1, "Thanks DRW for the awesome challenge!");
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
		return "GGBehavior";
	}
}
