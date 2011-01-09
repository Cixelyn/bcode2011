package costax3;

import battlecode.common.*;
import java.util.*;

public class SightConstructorAntennaeBehavior extends Behavior
{

	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MuleBuildOrder obj = MuleBuildOrder.EQUIPPING;
	
	MapLocation destination;
	
	int dizziness;
	int tiredness;
	ArrayList<Integer> badMines = new ArrayList<Integer>(100);
	
	boolean hasConstructor;
	boolean hasSensor;
	boolean hasAntennae;
	boolean mineFound;
	boolean justTurned;
	
	Mine[] nearbyMines;
	MineInfo mInfo;
	
	public SightConstructorAntennaeBehavior(RobotPlayer player)
	{
		super(player);
	}

	
	public void run() throws Exception
	{
    	switch (obj)
    	{
    		case EQUIPPING:
    			myPlayer.myRC.setIndicatorString(1, "EQUIPPING");
    			hasConstructor = false;
    			hasSensor = false;
    			hasAntennae = false;
    			for(ComponentController c:myPlayer.myRC.components())
    			{
    				if (c.type() == ComponentType.CONSTRUCTOR)
    				{
    					hasConstructor = true;
    				}
    				if (c.type() == Constants.SENSORTYPE)
    				{
    					hasSensor = true;
    				}
    				if (c.type() == ComponentType.ANTENNA)
    				{
    					hasAntennae = true;
    				}
    			}
    			if (hasConstructor && hasSensor && hasAntennae)
    				obj = MuleBuildOrder.EXPAND;
    			return;
    			
    		case EXPAND:
    			bounceNav(myPlayer);
    			mineFound = false;
    			nearbyMines = myPlayer.mySensor.senseNearbyGameObjects(Mine.class);
    			for (Mine m:nearbyMines)
    			{
    				if(!mineFound)
        			{
        				mInfo = myPlayer.mySensor.senseMineInfo(m);
        				if(myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
        				{
            				mineFound = true;
            				destination = mInfo.mine.getLocation();
        				}
        			}
    			}
    			if (mineFound)
    				obj = MuleBuildOrder.CAP_MINE;
    			return;
    			
    		case CAP_MINE:
    			myPlayer.myRC.setIndicatorString(1, "CAP_MINE");
    			if(!myPlayer.mySensor.withinRange(mInfo.mine.getLocation()) || myPlayer.mySensor.senseObjectAtLocation(mInfo.mine.getLocation(), RobotLevel.ON_GROUND) == null)
    			{
        			if (myPlayer.myRC.getLocation().distanceSquaredTo(destination) > myPlayer.myBuilder.type().range)
        			{
        				Utility.navStep(myPlayer, robotNavigation, destination);
        				tiredness++;
            			if(tiredness > Constants.MINE_AFFINITY)
            			{
            				badMines.add(mInfo.mine.getID());
            				obj = MuleBuildOrder.EXPAND;
            				tiredness = 0;
            			}
        			}
        			else
        			{
        				if (buildChassisInDir(myPlayer, myPlayer.myRC.getLocation().directionTo(destination), Chassis.BUILDING))
        					obj = MuleBuildOrder.ADDON_MINE;
        				else
        					obj = MuleBuildOrder.EXPAND;
        				tiredness = 0;
        				return;
        			}
    			}
    			else
    			{
    				obj = MuleBuildOrder.EXPAND;
    				tiredness = 0;
    			}
    			return;
    			
    		case ADDON_MINE:
    			Utility.equipFrontWithOneComponent(myPlayer, ComponentType.RECYCLER);
    			obj = MuleBuildOrder.EXPAND;
    			return;
    	}
	}
	
	
	
	public String toString()
	{
		return "MuleBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
	
	}

	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		
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
	
	/**
	 * Equip the robot in front of you with n of same component
	 * @param the robot, the component to equip on him, how many
	 * @return 
	 */
	public static void bounceNav(RobotPlayer myPlayer) throws Exception
	{
		int random = myPlayer.myDice.nextInt(10);
		if (!myPlayer.myMotor.isActive())
		{
			if(myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
				myPlayer.myMotor.moveForward();
			else
			{
				if (random == 0 || random == 1 || random == 2)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 3)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 4)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 5 || random == 6 || random == 7)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 8)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
				else if (random == 9)
				{
					if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateLeft().rotateLeft()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft().rotateLeft());
					else if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().rotateRight().rotateRight()))
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight().rotateRight());
					else
						myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
				}
			}
		}
	}
}
