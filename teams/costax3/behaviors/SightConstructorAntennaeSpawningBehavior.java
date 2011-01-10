package costax3.behaviors;

import java.util.Random;

import costax3.Constants;
import costax3.MsgType;
import costax3.RobotPlayer;
import costax3.Utility;

import battlecode.common.*;

public class SightConstructorAntennaeSpawningBehavior extends Behavior
{
	
	public enum RefineryBuildOrder 
	{
		EQUIPPING,
		WAITING,
		MAKE_MULE,
		EQUIP_MULE,
		MAKE_MARINE,
		EQUIP_MARINE
	}

	
	RefineryBuildOrder obj = RefineryBuildOrder.EQUIPPING;
	
	Robot babyMule;
	Robot babyMarine;
	
	double lastRes = 0;
	double p;
	
	final Random random = new Random();
	double r;
	
	public SightConstructorAntennaeSpawningBehavior(RobotPlayer player)
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
				p = buildingProbability(myPlayer.myRC.getTeamResources(), lastRes, Chassis.LIGHT);
				r = random.nextDouble();
				if (r < marineMuleRatio()*p)
					obj = RefineryBuildOrder.MAKE_MARINE;
				else if (r < p)
					obj = RefineryBuildOrder.MAKE_MULE;
    			lastRes = myPlayer.myRC.getTeamResources();
    			return;
    			
    		case MAKE_MULE:
    			myPlayer.myRC.setIndicatorString(1, "MAKE_MULE");
    			while(!shouldBuild(myPlayer, myPlayer.myRC.getDirection()))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				buildChassisInDir(myPlayer, myPlayer.myRC.getDirection(), Chassis.LIGHT);
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
    			while(!shouldBuild(myPlayer, myPlayer.myRC.getDirection()))
    			{
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					myPlayer.myRC.yield();
    			}
				buildChassisInDir(myPlayer, myPlayer.myRC.getDirection(), Chassis.LIGHT);
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
	
	public static boolean shouldBuild(RobotPlayer myPlayer, Direction dir) throws Exception
	{
		MapLocation loc = myPlayer.myRC.getLocation().add(dir);
		return (myPlayer.myRC.senseTerrainTile(loc) == TerrainTile.LAND && myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.ON_GROUND) == null && myPlayer.mySensor.senseObjectAtLocation(loc, RobotLevel.MINE) == null);
	}
	
	public static double marineMuleRatio()
	{
		if (Clock.getRoundNum() < 800)
			return 0.0;
		return 0.8;
	}
	
	public static boolean buildChassisInDir(RobotPlayer player, Direction dir, Chassis chassis) throws Exception
	{
		while (player.myRC.getTeamResources() < chassis.cost + Constants.RESERVE || player.myBuilder.isActive())
			player.sleep();
		/*GameObject rFront = player.mySensor.senseObjectAtLocation(player.myRC.getLocation().add(dir), chassis.level);
		if ( rFront == null )*/
		if ( player.myMotor.canMove(dir) )
		{
			player.myBuilder.build(chassis, player.myRC.getLocation().add(dir));
			return true;
		}
		else
			return false;
	}
	public static double buildingProbability(double currRes, double prevRes, Chassis chassis)
	{
		if ( (chassis == Chassis.BUILDING && Clock.getRoundNum() % 10 == 0) || (chassis == Chassis.LIGHT && Clock.getRoundNum() > 210 && Clock.getRoundNum() % 5 == 0))
		{
			if (currRes > chassis.cost + Constants.RESERVE && currRes - prevRes > chassis.upkeep)
				return 1.0;
		}
		return 0.0;
	}
}
