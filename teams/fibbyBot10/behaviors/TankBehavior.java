package fibbyBot10.behaviors;

import fibbyBot10.*;
import battlecode.common.*;

public class TankBehavior extends Behavior
{
	
	TankBuildOrder obj = TankBuildOrder.EQUIPPING;
	
	boolean hasRadar;
	boolean hasRailgun;
	boolean hasRegen;
	
	public TankBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
				for ( ComponentController c : myPlayer.myRC.components() )
				{
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
					if ( c.type() == ComponentType.RAILGUN )
						hasRailgun = true;
					if ( c.type() == ComponentType.REGEN )
						hasRegen = true;
				}
				if ( hasRadar && hasRailgun && hasRegen )
					obj = TankBuildOrder.MOVE_OUT;
				return;
	        	
			case MOVE_OUT:	
				
	        	myPlayer.myRC.setIndicatorString(1,"MOVE_OUT");
	        	if ( Utility.senseEnemies(myPlayer, myPlayer.myScanner.scannedRobotInfos ) != null )
	        		return;
	        	else if ( Clock.getRoundNum() > Constants.DEBRIS_TIME && Utility.senseDebris(myPlayer, myPlayer.myScanner.scannedRobotInfos) != null )
	        		return;
	        	else
	        		Utility.bounceNav(myPlayer);
	        	return;
	        	
		}
	}
	
	
	
	public String toString()
	{
		return "TankBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
}
