package fibbyBot11.behaviors;

import fibbyBot11.*;
import battlecode.common.*;

public class TankBehavior extends Behavior
{
	
	OldNavigation nav = new OldNavigation(myPlayer);
	
	TankBuildOrder obj = TankBuildOrder.EQUIPPING;
	
	MapLocation allyLoc;
	MapLocation enemyLoc;
	MapLocation debrisLoc;
	
	int numProcessors;
	int numBlasters;
	boolean hasSMG;
	boolean hasRadar;
	boolean hasAntenna;
	boolean hasMedic;
	
	boolean isLeader;
	int currLeader = 9999;
	MapLocation currLeaderLoc;
	
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
				numProcessors = 0;
				numBlasters = 0;
				hasSMG = false;
				hasRadar = false;
				hasAntenna = false;
				hasMedic = false;
				for ( ComponentController c : myPlayer.myRC.components() )
				{
					if ( c.type() == ComponentType.PROCESSOR )
						numProcessors++;
					if ( c.type() == ComponentType.BLASTER )
						numBlasters++;
					if ( c.type() == ComponentType.SMG )
						hasSMG = true;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
					if ( c.type() == ComponentType.ANTENNA )
						hasAntenna = true;
					if ( c.type() == ComponentType.MEDIC )
						hasMedic = true;
				}
				if ( numProcessors >= 0 && numBlasters >= 2 && hasRadar && hasAntenna && hasMedic )
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(Direction.NORTH); // hard-coded start aids swarming
					obj = TankBuildOrder.MOVE_OUT;
				}
				return;
	        	
			case MOVE_OUT:	
				
	        	myPlayer.myRC.setIndicatorString(1,"MOVE_OUT");
	        	isLeader = false;
	        	if ( myPlayer.myRC.getRobot().getID() < currLeader )
	        	{
	        		Utility.setIndicator(myPlayer, 2, "I'm a leader!");
	        		isLeader = true;
	        	}
	        	else
	        	{
	        		Utility.setIndicator(myPlayer, 2, "Leader is " + Integer.toString(currLeader) + ".");
	        	}
	        	Utility.healSelf(myPlayer);
	        	allyLoc = Utility.healAllies(myPlayer, myPlayer.myScanner.scannedRobotInfos);
	        	enemyLoc = Utility.attackEnemies(myPlayer, myPlayer.myScanner.scannedRobotInfos );
	        	if ( enemyLoc != null )
	        	{
	        		if ( !myPlayer.myMotor.isActive() )
	        		{
	        			if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().opposite()) && myPlayer.myRC.getLocation().distanceSquaredTo(enemyLoc) <= ComponentType.BLASTER.range )
	        				myPlayer.myMotor.moveBackward();
	        			else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) && myPlayer.myRC.getDirection() == myPlayer.myRC.getLocation().directionTo(enemyLoc) )
	        				myPlayer.myMotor.moveForward();
	        			else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) && myPlayer.myRC.getDirection() != myPlayer.myRC.getLocation().directionTo(enemyLoc) )
	        				myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyLoc));
	        			else
	        				Utility.navStep(myPlayer, nav, enemyLoc);
	        		}
	        	}
	        	else
	        	{
		        	if ( Clock.getRoundNum() > Constants.DEBRIS_TIME )
		        		Utility.attackDebris(myPlayer, myPlayer.myScanner.scannedRobotInfos);
		        	if ( isLeader )
		        		Utility.bounceNav(myPlayer);
		        	else
		        		Utility.navStep(myPlayer, nav, currLeaderLoc);
	        	}
	        	if ( isLeader )
	        		myPlayer.myMessenger.sendIntLoc(MsgType.MSG_DET_LEADER, myPlayer.myRC.getRobot().getID(), myPlayer.myRC.getLocation());
	        	else
	        		myPlayer.myMessenger.sendIntLoc(MsgType.MSG_DET_LEADER, currLeader, currLeaderLoc);
	        	currLeader = 9999;
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
		if ( t == MsgType.MSG_DET_LEADER )
		{
			if ( msg.ints[Messenger.firstData] < currLeader )
			{
				currLeader = msg.ints[Messenger.firstData];
				currLeaderLoc = msg.locations[Messenger.firstData];
			}
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
}
