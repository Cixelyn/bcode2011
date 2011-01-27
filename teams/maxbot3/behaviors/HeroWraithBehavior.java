package maxbot3.behaviors;


import maxbot3.*;
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
			BUILD_TOWER
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
					num = 1;
					frontlineLoc = myPlayer.myLoc.add(0, -9);
					enemyTowerLoc = frontlineLoc.add(0, -6);
					obj = HeroWraithBuildOrder.EQUIPPING;
					return;
				
				case EQUIPPING:
					
					Utility.setIndicator(myPlayer, 0, "EQUIPPING");
					
					int numConstructors = 0;
					for ( int i = myPlayer.myRC.components().length ; --i >= 0 ; )
					{
						if ( myPlayer.myRC.components()[i].type() == ComponentType.CONSTRUCTOR)
							numConstructors++;
					}
					if ( numConstructors==1 )
						obj = HeroWraithBuildOrder.BUILD_TOWER;
						
					return;
				
				case BUILD_TOWER:
					Utility.moveInDirection(myPlayer, Direction.NORTH_EAST);
					Utility.moveInDirection(myPlayer, Direction.NORTH_EAST);
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
					Utility.moveInDirection(myPlayer, Direction.NORTH);
					Utility.moveInDirection(myPlayer, Direction.NORTH);
					Utility.moveInDirection(myPlayer, Direction.NORTH);
					Utility.moveInDirection(myPlayer, Direction.NORTH);
					Utility.moveInDirection(myPlayer, Direction.NORTH);
					Utility.buildChassis(myPlayer, Direction.NORTH, Chassis.BUILDING);
					myPlayer.myRC.suicide();
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
