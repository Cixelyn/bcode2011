package plasmabot2.behaviors;

import plasmabot2.*;
import battlecode.common.*;

public class MarineBehavior extends Behavior
{
	
	MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	WeaponController gun;
	
	int guns;
	
	boolean hasBlaster;
	boolean hasRadar;
	boolean hasShield;
	
	public MarineBehavior(RobotPlayer player)
	{
		super(player);
		overrideScanner = true;	//disable the scanner subsystem
	}

	
	
	
	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			//Fully Equip the Marine
			case EQUIPPING:
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
	            hasBlaster = false;
	            hasRadar = false;
	            hasShield = false;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if ( c.type() == ComponentType.BLASTER )
						hasBlaster = true;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
					if ( c.type() == ComponentType.SHIELD )
						hasShield = true;
				}
				if ( hasBlaster && hasRadar && hasShield )
					obj = MarineBuildOrder.MOVE_OUT;
				return;
	        	
				
			//Running the main loop
			case MOVE_OUT:	
				
				
				
				
				
				//Scan for enemy robots.
				Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
				for(Robot r:nearbyRobots) {
				
				}
				
				
				
				
				
				
				
				
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
	public void onDamageCallback(double damageTaken) {}
}
