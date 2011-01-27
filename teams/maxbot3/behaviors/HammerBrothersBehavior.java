package maxbot3.behaviors;


import maxbot3.*;
import battlecode.common.*;;

public class HammerBrothersBehavior extends Behavior
{

		MapLocation frontlineLoc;
		MapLocation enemyTowerLoc;
		
		int num;
		int id=0;
		int numOfAttacks=0;
		boolean finishedTurningOn=false;
		
		private enum HammerBrothersBuildOrder
		{
			INITIALIZE,
			EQUIPPING,
			GET_IN_POSITION,
			BUM_RUSH,
			ATTACK_TOWER,
			SLEEPING;
		}
		
		HammerBrothersBuildOrder obj = HammerBrothersBuildOrder.INITIALIZE;

		
		public HammerBrothersBehavior(RobotPlayer player)
		{
			super(player);
		}
		
		public void run() throws Exception
		{
			
			switch (obj)
			{
				
				case INITIALIZE:
					
					Utility.setIndicator(myPlayer, 0, "INITIALIZE");
					num = 1;
					frontlineLoc = myPlayer.myLoc.add(0, -9);
					enemyTowerLoc = frontlineLoc.add(0, -6);
					obj = HammerBrothersBuildOrder.EQUIPPING;
					return;
				
				case EQUIPPING:
					
					Utility.setIndicator(myPlayer, 0, "EQUIPPING");
					
					int numHammers=0;
					for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
					{
						if ( myPlayer.myRC.components()[i].type() == ComponentType.HAMMER )
							numHammers++;
					}
					if ( numHammers==8)
						obj = HammerBrothersBuildOrder.GET_IN_POSITION;
						
					return;
				
				case GET_IN_POSITION:
					
					Utility.setIndicator(myPlayer, 0, "GET_IN_POSITION");
					while (myPlayer.myMotor.isActive()) {
						myPlayer.sleep();
					}
					Utility.moveInDirection(myPlayer, Direction.NORTH_EAST);
					Utility.moveInDirection(myPlayer, Direction.NORTH_EAST);
					while (myPlayer.myMotor.isActive()) {
						myPlayer.sleep();
					}
					myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
					if (!myPlayer.myMotor.canMove(Direction.NORTH_WEST)) {
						id=5;
						obj=HammerBrothersBuildOrder.SLEEPING;
						return;
					}
					Utility.moveInDirection(myPlayer, Direction.NORTH_WEST);
					while (myPlayer.myMotor.isActive()) {
						myPlayer.sleep();
					}
					if (myPlayer.myMotor.canMove(Direction.NORTH_WEST)) {
						Utility.moveInDirection(myPlayer, Direction.NORTH_WEST);
						id=1;
						obj=HammerBrothersBuildOrder.SLEEPING;
					}
					else {
						while (myPlayer.myMotor.isActive()) {
							myPlayer.sleep();
						}
						myPlayer.myMotor.setDirection(Direction.WEST);
						while (myPlayer.myMotor.isActive()) {
							myPlayer.sleep();
						}
						if (myPlayer.myMotor.canMove(Direction.WEST)) {
							Utility.moveInDirection(myPlayer, Direction.WEST);
							while (myPlayer.myMotor.isActive()) {
								myPlayer.sleep();
							}
							if (myPlayer.myMotor.canMove(Direction.WEST)) {
								Utility.moveInDirection(myPlayer, Direction.WEST);
								id=2;
								obj=HammerBrothersBuildOrder.SLEEPING;
							}
							else {
								id=3;
								obj=HammerBrothersBuildOrder.SLEEPING;
							}
						}
						else {
							id=4;
							Utility.setIndicator(myPlayer, 1, id + "");
							obj=HammerBrothersBuildOrder.BUM_RUSH;
							return;
						}
					}
					return;
					
				case BUM_RUSH:
					Utility.setIndicator(myPlayer, 0, "BUM RUSH");
					Utility.setIndicator(myPlayer, 1, id+"");
					Utility.setIndicator(myPlayer, 2, Clock.getRoundNum()+"");
					while (Clock.getRoundNum()<1000) {
						myPlayer.sleep();
					}
					if (id==4 && !finishedTurningOn) {
						myPlayer.myRC.turnOn(new MapLocation(myPlayer.myLoc.x-1,myPlayer.myLoc.y), RobotLevel.ON_GROUND);
						myPlayer.myRC.turnOn(new MapLocation(myPlayer.myLoc.x-1,myPlayer.myLoc.y-1), RobotLevel.ON_GROUND);
						myPlayer.myRC.turnOn(new MapLocation(myPlayer.myLoc.x+1,myPlayer.myLoc.y+1), RobotLevel.ON_GROUND);
						finishedTurningOn=true;
					}
					if (Clock.getRoundNum()>=1224) {
						myPlayer.myRC.suicide();
					}
					else if (Clock.getRoundNum()>=1000) {
						if (id==1) {
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH_WEST);
							while (myPlayer.myMotor.isActive()) {
								myPlayer.sleep();
							}
							myPlayer.myMotor.setDirection(Direction.EAST);
							obj = HammerBrothersBuildOrder.ATTACK_TOWER;
						}
						if (id==2) {
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH_EAST);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH_WEST);
							while (myPlayer.myMotor.isActive()) {
								myPlayer.sleep();
							}
							myPlayer.myMotor.setDirection(Direction.NORTH_EAST);
							obj = HammerBrothersBuildOrder.ATTACK_TOWER;
						}
						if (id==3) {
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							while (myPlayer.myMotor.isActive()) {
								myPlayer.sleep();
							}
							myPlayer.myMotor.setDirection(Direction.NORTH);
							obj = HammerBrothersBuildOrder.ATTACK_TOWER;
						}
						if (id==4) {
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH_WEST);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH_EAST);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							
							while (myPlayer.myMotor.isActive()) {
								myPlayer.sleep();
							}
							myPlayer.myMotor.setDirection(Direction.WEST);
							obj = HammerBrothersBuildOrder.ATTACK_TOWER;
						}
						if (id==5) {
							Utility.moveInDirection(myPlayer, Direction.NORTH_WEST);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH_WEST);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH);
							Utility.moveInDirection(myPlayer, Direction.NORTH_EAST);
							
							while (myPlayer.myMotor.isActive()) {
								myPlayer.sleep();
							}
							myPlayer.myMotor.setDirection(Direction.NORTH_WEST);
							obj = HammerBrothersBuildOrder.ATTACK_TOWER;
						}
					}
					myPlayer.sleep();
					return;
					
				case ATTACK_TOWER:
					if (Clock.getRoundNum()==1250) {
						myPlayer.myRC.suicide();
					}
					WeaponController gun;
					Utility.setIndicator(myPlayer, 0, "ATTACKING!");
					Utility.setIndicator(myPlayer, 1, numOfAttacks+"");
					for ( int j = myPlayer.myHammers.length; --j >= 0 ; )
					{
						gun =  myPlayer.myHammers[j];
						if ( !gun.isActive() && gun.withinRange(myPlayer.myLoc.add(myPlayer.myRC.getDirection()))) {
							gun.attackSquare(myPlayer.myLoc.add(myPlayer.myRC.getDirection()), RobotLevel.ON_GROUND);
							numOfAttacks++;
						}
					}
					return;
				case SLEEPING:
					Utility.setIndicator(myPlayer, 0, "SLEEP");
					Utility.setIndicator(myPlayer, 1, "zzzzzz");
					myPlayer.myRC.turnOff();
					return;
					
			}
		}

		@Override
		public void newComponentCallback(ComponentController[] components)
		{
			
		}

		@Override
		public void onWakeupCallback(int lastActiveRound)
		{
			try {
				myPlayer.myRC.turnOn(new MapLocation(myPlayer.myLoc.x-1,myPlayer.myLoc.y), RobotLevel.ON_GROUND);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			obj=HammerBrothersBuildOrder.BUM_RUSH;
		}

		@Override
		public String toString()
		{
			return "HeroWraithBehavior";
		}
}