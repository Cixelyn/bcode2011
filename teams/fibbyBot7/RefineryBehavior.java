package fibbyBot7;

import battlecode.common.*;

public class RefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	Robot babyMule;
	Robot babyMarine;
	
	public RefineryBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{

		switch(obj)
    	{
    		case EQUIPPING:
    			myPlayer.myRC.setIndicatorString(1, "EQUIPPING");
    			for(ComponentController c:myPlayer.myRC.components())
				{
					if (c.type()==ComponentType.RECYCLER)
						obj = RefineryBuildOrder.WAITING;
				}
    			return;
    			
    		case WAITING:
    			myPlayer.myRC.setIndicatorString(1, "WAITING");
    			if(Clock.getRoundNum() >= Constants.MULE_TIME && Clock.getRoundNum() < Constants.EXPAND_TIME)
    				obj = RefineryBuildOrder.MAKE_MULE;
    			if(Clock.getRoundNum() >= Constants.MARINE_TIME)
        			obj = RefineryBuildOrder.MAKE_MARINE;
    			return;
    			
    		case MAKE_MULE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MULE");
    			while(!Utility.shouldBuild(myPlayer, myPlayer.myRC.getDirection()))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				Utility.buildChassis(myPlayer, Chassis.LIGHT);
				babyMule = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = RefineryBuildOrder.EQUIP_MULE;
    			return;
    			
    		case EQUIP_MULE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MULE");
    			Utility.equipFrontWithTwoComponents(myPlayer, babyMule, ComponentType.CONSTRUCTOR, Constants.SENSORTYPE);
    			obj = RefineryBuildOrder.WAITING;
    			return;
    			
    		case MAKE_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MARINE");
    			while(!Utility.shouldBuild(myPlayer, myPlayer.myRC.getDirection()))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				Utility.buildChassis(myPlayer, Chassis.LIGHT);
				babyMarine = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = RefineryBuildOrder.EQUIP_MARINE;
    			return;
    			
    		case EQUIP_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MARINE");
    			Utility.equipFrontWithSameComponents(myPlayer, babyMarine, Constants.GUNTYPE, Constants.GUNS);
    			Utility.equipFrontWithTwoComponents(myPlayer, babyMarine, Constants.ARMORTYPE, Constants.SENSORTYPE);
    			obj = RefineryBuildOrder.WAITING;
    			return;
    	}
		
	}

	public String toString()
	{
		return "RefineryBehavior";
	}
	
	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void newMessageCallback(MsgType t, Message msg)
	{
		
	}

}
