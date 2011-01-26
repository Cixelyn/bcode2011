package fibbyBot14.behaviors;


import fibbyBot14.*;
import battlecode.common.*;;

public class HeroWraithBehavior extends Behavior
{

		MapLocation frontlineLoc;
		MapLocation enemyTowerLoc;
		
		int num;
		
		private enum HeroWraithBuildOrder
		{
			INITIALIZE,
			EQUIPPING,
			MOVE_TOWARDS_TOWER,
			FIGHT_TOWER,
			GO_HOME,
			GET_HEALED;
		}
		
		HeroWraithBuildOrder obj = HeroWraithBuildOrder.INITIALIZE;

		
		public HeroWraithBehavior(RobotPlayer player)
		{
			super(player);
		}
		
		public void run() throws Exception
		{
			
			switch (obj)
			{
				
				case INITIALIZE:
					
					Utility.setIndicator(myPlayer, 0, "INITIALIZE");
					if ( Clock.getRoundNum() < Constants.SECOND_MEDIVAC )
					{
						num = 1;
						frontlineLoc = myPlayer.myLoc.add(0, -9);
						enemyTowerLoc = frontlineLoc.add(0, -6);
					}
					obj = HeroWraithBuildOrder.EQUIPPING;
					return;
				
				case EQUIPPING:
					
					Utility.setIndicator(myPlayer, 0, "EQUIPPING");
					
					int numBeams = 0;
					int numShields = 0;
					for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
					{
						if ( myPlayer.myRC.components()[i].type() == ComponentType.BEAM )
							numBeams++;
						if ( myPlayer.myRC.components()[i].type() == ComponentType.SHIELD)
							numShields++;
					}
					if ( numBeams == 1 && numShields == 1 )
						obj = HeroWraithBuildOrder.MOVE_TOWARDS_TOWER;
						
					return;
				
				case MOVE_TOWARDS_TOWER:
					
					Utility.setIndicator(myPlayer, 1, "MOVE_TOWARDS_TOWER");
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					if ( num == 1 )
					{
						myPlayer.myMotor.setDirection(Direction.NORTH);
						for ( int steps = 0 ; steps < 9 ; steps++ )
						{
							while ( myPlayer.myMotor.isActive() )
								myPlayer.sleep();
							myPlayer.myMotor.moveForward();
						}
					}
					obj = HeroWraithBuildOrder.FIGHT_TOWER;
					return;
					
				case FIGHT_TOWER:
					
					Utility.setIndicator(myPlayer, 0, "FIGHT_TOWER");
					if ( myPlayer.myRC.getHitpoints() < Constants.THRESHOLD_LIFE )
					{
						obj = HeroWraithBuildOrder.GO_HOME;
					}
					else
					{
						if ( !myPlayer.myBeams[0].isActive() )
						{
							myPlayer.myBeams[0].attackSquare(enemyTowerLoc, RobotLevel.ON_GROUND);
						}
					}
					return;
					
				case GO_HOME:
					
					Utility.setIndicator(myPlayer, 0, "GO_HOME");
					if ( num == 1 )
					{
						for ( int steps = 0 ; steps < 9 ; steps++ )
						{
							while ( myPlayer.myMotor.isActive() )
								myPlayer.sleep();
							myPlayer.myMotor.moveBackward();
						}
					}
					obj = HeroWraithBuildOrder.GET_HEALED;
					return;
					
				case GET_HEALED:
					
					Utility.setIndicator(myPlayer, 0, "GET_HEALED");
					if ( myPlayer.myRC.getHitpoints() == myPlayer.myRC.getMaxHp() )
					{
						obj = HeroWraithBuildOrder.MOVE_TOWARDS_TOWER;
					}
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
			
		}

		@Override
		public String toString()
		{
			return "HeroWraithBehavior";
		}
}
