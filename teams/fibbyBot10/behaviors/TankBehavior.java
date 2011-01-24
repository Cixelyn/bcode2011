package fibbyBot10.behaviors;

import fibbyBot10.*;
import battlecode.common.*;

public class TankBehavior extends Behavior
{
	
	OldNavigation nav = new OldNavigation(myPlayer);
	
	TankBuildOrder obj = TankBuildOrder.EQUIPPING;
	
	MapLocation allyLoc;
	MapLocation enemyLoc;
	MapLocation debrisLoc;
	
	boolean hasRadar;
	boolean hasRailgun;
	boolean hasMedic;
	
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
					if ( c.type() == ComponentType.MEDIC )
						hasMedic = true;
				}
				if ( hasRadar && hasRailgun && hasMedic )
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(Direction.NORTH); // hardcoded swarming?
					obj = TankBuildOrder.MOVE_OUT;
				}
				return;
	        	
			case MOVE_OUT:	
				
	        	myPlayer.myRC.setIndicatorString(1,"MOVE_OUT");
	        	allyLoc = Utility.healAllies(myPlayer, myPlayer.myScanner.scannedRobotInfos);
	        	enemyLoc = Utility.attackEnemies(myPlayer, myPlayer.myScanner.scannedRobotInfos );
	        	if ( enemyLoc != null )
	        	{
	        		if ( !myPlayer.myMotor.isActive() )
	        		{
	        			if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().opposite()) && myPlayer.myRC.getLocation().distanceSquaredTo(enemyLoc) <= ComponentType.RAILGUN.range )
	        				myPlayer.myMotor.moveBackward();
	        			else
	        				Utility.navStep(myPlayer, nav, enemyLoc);
	        		}
	        		return;
	        	}
	        	else if ( allyLoc != null )
	        	{
	        		Utility.navStep(myPlayer, nav, allyLoc);
	        		return;
	        	}
	        	else if ( Clock.getRoundNum() > Constants.DEBRIS_TIME && Utility.attackDebris(myPlayer, myPlayer.myScanner.scannedRobotInfos) != null )
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
