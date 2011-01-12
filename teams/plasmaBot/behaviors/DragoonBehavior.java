package plasmaBot.behaviors;

import plasmaBot.*;
import battlecode.common.*;

public class DragoonBehavior extends Behavior
{
	
	DragoonBuildOrder obj = DragoonBuildOrder.EQUIPPING;
	
	WeaponController gun;
	
	int guns;
	
	int plasma;
	int blasters;
	boolean hasSight;
	boolean hasShield;
	
	public DragoonBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			case EQUIPPING:
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
	            hasSight = false;
	            hasShield = false;
	            plasma=0;
	            blasters=0;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if ( c.type() == ComponentType.BLASTER )
						blasters=blasters+1;
					if ( c.type() == ComponentType.SIGHT )
						hasSight = true;
					if ( c.type() == ComponentType.PLASMA)
						plasma=plasma+1;
					if ( c.type() == ComponentType.SHIELD )
						hasShield = true;
				}
				if ( hasSight && hasShield && blasters==2 && plasma==2) {
					obj = DragoonBuildOrder.MOVE_OUT;
				}
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
		return "MarineBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		
	}
	public void onWakeupCallback(int lastActiveRound) {}
}