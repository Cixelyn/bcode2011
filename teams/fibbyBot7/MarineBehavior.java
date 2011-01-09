package fibbyBot7;

import battlecode.common.*;

public class MarineBehavior extends Behavior
{
	
	MarineBuildOrder obj = MarineBuildOrder.EQUIPPING;
	
	WeaponController gun;
	
	int guns;
	
	boolean hasSensor;
	boolean hasArmor;
	boolean justTurned;
	
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
					if (c.type()==Constants.GUNTYPE)
					{
						guns++;
						if (!myPlayer.myWeapons.contains((WeaponController)c))
							myPlayer.myWeapons.add((WeaponController)c);
					}
					if (c.type()==Constants.SENSORTYPE)
					{
						hasSensor = true;
						myPlayer.mySensor = (SensorController)c;
					}
					if (c.type()==Constants.ARMORTYPE)
						hasArmor = true;
				}
				if (guns >= Constants.GUNS && hasSensor && hasArmor)
					obj = MarineBuildOrder.MOVE_OUT;
				return;
	        	
			case MOVE_OUT:
	        	myPlayer.myRC.setIndicatorString(1,"MOVE_OUT");
	        	if(Utility.senseEnemies(myPlayer) == null && Utility.senseDebris(myPlayer) == null)
	        		Utility.bounceNav(myPlayer);
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
}
