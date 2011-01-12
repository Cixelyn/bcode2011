package plasmabot2.behaviors;

import plasmabot2.*;
import plasmabot2.MsgType;
import battlecode.common.*;

public class DragoonBehavior extends Behavior
{
	
	DragoonBuildOrder obj = DragoonBuildOrder.EQUIPPING;
	OldNavigation navigation = new OldNavigation(myPlayer);
	
	WeaponController gun;
	
	int guns;
	
	int ID=0;
	MapLocation initialGoal;
	
	int plasma;
	int blasters;
	boolean hasSight; 
	boolean hasShield;
	boolean foundEdge=false;
	
	public DragoonBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			case EQUIPPING:
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
	            hasSight = false;
	            hasShield = false;
	            plasma=0;
	            blasters=0;
				for(ComponentController c:myPlayer.myRC.components())
				{
					if ( c.type() == ComponentType.BLASTER )
						blasters=blasters+1;
					if ( c.type() == ComponentType.SIGHT )
						hasSight = true;
					if ( c.type() == ComponentType.PLASMA)
						plasma=plasma+1;
					if ( c.type() == ComponentType.SHIELD )
						hasShield = true;
				}
				if ( hasSight && hasShield && blasters==2 && plasma==2 && initialGoal!=null) {
					obj = DragoonBuildOrder.MOVE_OUT;
				}
				return;
	        	
			case MOVE_OUT:
	        	myPlayer.myRC.setIndicatorString(1,"MOVE_OUT");
	        	if ( Utility.senseEnemies(myPlayer, myPlayer.myScanner.scannedRobotInfos ) != null )
	        		return;
	        	else if ( Clock.getRoundNum() > Constants.DEBRIS_TIME && Utility.senseDebris(myPlayer, myPlayer.myScanner.scannedRobotInfos) != null )
	        		return;
	        	else {
	        		if (foundEdge) {
	        			Utility.bounceNav(myPlayer);
	        		}
	        		if (myPlayer.myRC.getDirection().isDiagonal()) {
	        			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),2)).equals(TerrainTile.OFF_MAP)) {
	        				foundEdge=true;
	        			}
	        		}
	        		if (!myPlayer.myRC.getDirection().isDiagonal()) {
	        			if (myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection(),3)).equals(TerrainTile.OFF_MAP)) {
	        				foundEdge=true;
	        			}
	        		}
	        		if (foundEdge) {
	        			Utility.bounceNav(myPlayer);
	        		}
	        		else {
	        			System.out.println(initialGoal);
		        		Direction direction = navigation.bugTo(initialGoal);
		        		if (myPlayer.myRC.getDirection().equals(direction) && !myPlayer.myMotor.isActive()) {
		        			myPlayer.myMotor.moveForward();
		        		}
		        		else if  (!myPlayer.myRC.getDirection().equals(direction) && !myPlayer.myMotor.isActive()) {
		        			myPlayer.myMotor.setDirection(direction);
		        		}
	        		}
	        	}
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
	public void newMessageCallback(MsgType t, Message msg) {
		if (t.equals(MsgType.MSG_SEND_NUM)) {
			System.out.println("hello");
			ID=msg.ints[Messenger.firstData]%8;
			System.out.println(ID);
			if (ID==0) {
				initialGoal=new MapLocation(myPlayer.myRC.getLocation().x-65,myPlayer.myRC.getLocation().y-65);
			}
			if (ID==1) {
				initialGoal=new MapLocation(myPlayer.myRC.getLocation().x,myPlayer.myRC.getLocation().y-65);
			}
			if (ID==2) {
				initialGoal=new MapLocation(myPlayer.myRC.getLocation().x+65,myPlayer.myRC.getLocation().y-65);
			}
			if (ID==3) {
				initialGoal=new MapLocation(myPlayer.myRC.getLocation().x+65,myPlayer.myRC.getLocation().y);
			}
			if (ID==4) {
				initialGoal=new MapLocation(myPlayer.myRC.getLocation().x+65,myPlayer.myRC.getLocation().y+65);
			}
			if (ID==5) {
				initialGoal=new MapLocation(myPlayer.myRC.getLocation().x,myPlayer.myRC.getLocation().y+65);
			}
			if (ID==6) {
				initialGoal=new MapLocation(myPlayer.myRC.getLocation().x-65,myPlayer.myRC.getLocation().y+65);
			}
			if (ID==7) {
				initialGoal=new MapLocation(myPlayer.myRC.getLocation().x-65,myPlayer.myRC.getLocation().y);
			}
			System.out.println(initialGoal);
		}
	}
	
	public void onWakeupCallback(int lastActiveRound) {}
	public void onDamageCallback(double damageTaken) {}
}