package fibbyBot7;

import java.util.Random;

import battlecode.common.*;

public class RefineryBehavior extends Behavior
{
	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	Robot babyMule;
	Robot babyMarine;
	
	double lastRes = 0;
	double p;
	
	final Random random = new Random();
	double r;
	
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
    			lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case WAITING:
    			myPlayer.myRC.setIndicatorString(1, "WAITING");
				p = Utility.buildingProbability(myPlayer.myRC.getTeamResources(), lastRes, Chassis.LIGHT);
				r = random.nextDouble();
				if (r < Utility.marineMuleRatio()*p)
					obj = RefineryBuildOrder.MAKE_MARINE;
				else if (r < p)
					obj = RefineryBuildOrder.MAKE_MULE;
    			lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case MAKE_MULE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MULE");
    			while(!Utility.shouldBuild(myPlayer, myPlayer.myRC.getDirection()))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				Utility.buildChassisInDir(myPlayer, myPlayer.myRC.getDirection(), Chassis.LIGHT);
				babyMule = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = RefineryBuildOrder.EQUIP_MULE;
				lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case EQUIP_MULE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MULE");
    			Utility.equipFrontWithTwoComponents(myPlayer, babyMule, ComponentType.CONSTRUCTOR, Constants.SENSORTYPE);
    			obj = RefineryBuildOrder.WAITING;
    			lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case MAKE_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MARINE");
    			while(!Utility.shouldBuild(myPlayer, myPlayer.myRC.getDirection()))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				Utility.buildChassisInDir(myPlayer, myPlayer.myRC.getDirection(), Chassis.LIGHT);
				babyMarine = (Robot)myPlayer.mySensor.senseObjectAtLocation(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
				obj = RefineryBuildOrder.EQUIP_MARINE;
    			return;
    			
    		case EQUIP_MARINE:
    			myPlayer.myRC.setIndicatorString(1, "EQUIP_MARINE");
    			Utility.equipFrontWithSameComponents(myPlayer, babyMarine, Constants.GUNTYPE, Constants.GUNS);
    			Utility.equipFrontWithTwoComponents(myPlayer, babyMarine, Constants.ARMORTYPE, Constants.SENSORTYPE);
    			obj = RefineryBuildOrder.WAITING;
    			lastRes = myPlayer.myRC.getTeamResources();
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
