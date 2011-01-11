package masteryone.behaviors;

import masteryone.*;
import battlecode.common.*;

public class MarineBehavior extends Behavior
{
	
	MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	WeaponController gun;
	
	int guns;
	
	boolean hasSensor;
	boolean hasArmor;
	
	public MarineBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			case EQUIPPING:
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
	            guns = 0;
	            hasSensor = false;
	            hasArmor = false;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if ( c.type() == ComponentType.BLASTER )
					{
						guns++;
						if (!myPlayer.myWeapons.contains((WeaponController)c))
							myPlayer.myWeapons.add((WeaponController)c);
					}
					if ( c.type() == ComponentType.SIGHT )
					{
						hasSensor = true;
						myPlayer.mySensor = (SensorController)c;
					}
					if (c.type() == ComponentType.SHIELD )
						hasArmor = true;
				}
				if (guns >= 2 && hasSensor && hasArmor)
					obj = MarineBuildOrder.MOVE_OUT;
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
	public void onWakeupCallback() {}
}
