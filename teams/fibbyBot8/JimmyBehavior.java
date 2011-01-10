package fibbyBot8;

import battlecode.common.*;

public class JimmyBehavior extends Behavior {
	
	JimmyBuildOrder obj = JimmyBuildOrder.EQUIPPING;
	
	final Navigation robotNavigation = new Navigation(myPlayer);
	
	MapLocation hometown;
	MapLocation enemyLocation;
	MapLocation currDestination;
	MapLocation mainDestination;
	MapLocation newDestination;
	
	Direction direction;
	
	int dizziness;
	int staleness;
	
	boolean eeHanTiming;
	boolean enemyFound;
	boolean hasComm;
	boolean hasSensor;
	boolean hasDummy;
	
	public JimmyBehavior(RobotPlayer player) {
		super(player);
	}


	public void run() throws Exception {
		
		
		switch (obj) {
			case EQUIPPING:
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
				Utility.spin(myPlayer);
	            hasComm = false;
	            hasSensor = false;
	            hasDummy = false;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if (c.type()==ComponentType.DISH)
					{
						hasComm = true;
						myPlayer.myMessenger.enableSender();
					}
					if (c.type()==ComponentType.RADAR)
						hasSensor = true;
					if (c.type()==ComponentType.DUMMY)
						hasDummy = true;
				}
				if (hasComm && hasSensor && hasDummy)
				{
					obj = JimmyBuildOrder.WAITING;
				}
				return;
				
			case WAITING:
				myPlayer.myRC.setIndicatorString(1,"WAITING");
				Utility.spin(myPlayer);
				Utility.senseEnemies(myPlayer);
	        	if (eeHanTiming)
	        		obj = JimmyBuildOrder.MOVE_OUT;
	        	return;
	        	
			case MOVE_OUT:
	        	myPlayer.myRC.setIndicatorString(1,"MOVE_OUT");
	        	newDestination = Utility.senseEnemies(myPlayer);
	        	if (newDestination != null) // enemy found
	        	{
	        		staleness = 0;
	        		currDestination = newDestination;
	        	}
	        	if (currDestination != null && !myPlayer.myMotor.isActive())
	            {
	        		direction = robotNavigation.bugTo(currDestination);
	        		staleness++;
	        		if (staleness > Constants.OLDNEWS)
	        			currDestination = mainDestination;
	        		if (direction != Direction.OMNI && direction != Direction.NONE)
	        		{
	            		myPlayer.myMotor.setDirection(direction);
						if (staleness > Constants.OLDNEWS || myPlayer.myRC.getLocation().distanceSquaredTo(currDestination) >= Constants.GUNTYPE.range)
						{
							while(myPlayer.myMotor.isActive())
								myPlayer.myRC.yield();
							if (myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
								myPlayer.myMotor.moveForward();
	        			}
	        		}
	            }
	        	return;
		}
	}
	
	
	
	public String toString() {
		return "JimmyBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		if(t == MsgType.MSG_MOVE_OUT)
		{
			hometown = msg.locations[Messenger.firstData];
			enemyLocation = msg.locations[Messenger.firstData+1];
			currDestination = enemyLocation;
			mainDestination = enemyLocation;
			eeHanTiming = true;
		}
	}
}
