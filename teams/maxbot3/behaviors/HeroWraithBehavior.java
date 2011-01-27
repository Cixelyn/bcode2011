package maxbot3.behaviors;


import maxbot3.*;
import battlecode.common.*;;

public class HeroWraithBehavior extends Behavior
{
		
		private enum HeroWraithBuildOrder
		{
			INITIALIZE,
			EQUIPPING,
			BUILD_TOWER,
			REBUILD_MAIN,
			SLEEP
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
					
					Utility.setIndicator(myPlayer, 0, "BUILD_TOWER");
					
					turn(Direction.NORTH_EAST);
					forward();
					forward();
					turn(Direction.NORTH);
					for ( int i = 15; --i >= 0 ; )
						forward();
					myPlayer.sleep();
					
					Utility.buildChassis(myPlayer, Direction.NORTH, Chassis.BUILDING);
					Utility.buildComponent(myPlayer, Direction.NORTH, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
					
					turn(Direction.NORTH);
					forward();
					forward();
					
					obj = HeroWraithBuildOrder.SLEEP;
					return;
					
				case REBUILD_MAIN:
					
					Utility.setIndicator(myPlayer, 0, "BUILD_TOWER");
					
					// rounds where main is not lost
					if ( Clock.getRoundNum() / 500 == 5 )
					{
						// give refinery some zzz time
						myPlayer.sleep();
						myPlayer.sleep();
						myPlayer.sleep();
						myPlayer.sleep();
						myPlayer.sleep();
						myPlayer.sleep();
						myPlayer.myRC.turnOn(myPlayer.myLoc.add(Direction.SOUTH), RobotLevel.ON_GROUND);
						obj = HeroWraithBuildOrder.SLEEP;
						return;
					}
					
					turn(Direction.SOUTH);
					for ( int i = 19; --i >= 0 ; )
						forward();
					myPlayer.sleep();
					
					Utility.buildChassis(myPlayer, Direction.OMNI, Chassis.BUILDING);
					Utility.buildComponent(myPlayer, Direction.OMNI, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
					Utility.buildChassis(myPlayer, Direction.SOUTH, Chassis.BUILDING);
					Utility.buildComponent(myPlayer, Direction.SOUTH, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
					Utility.buildChassis(myPlayer, Direction.SOUTH_WEST, Chassis.BUILDING);
					Utility.buildComponent(myPlayer, Direction.SOUTH_WEST, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
					Utility.buildChassis(myPlayer, Direction.WEST, Chassis.BUILDING);
					Utility.buildComponent(myPlayer, Direction.WEST, ComponentType.RECYCLER, RobotLevel.ON_GROUND);
					
					turn(Direction.NORTH);
					for ( int i = 17; --i >= 0 ; )
						forward();
					myPlayer.sleep();
					
					myPlayer.myRC.turnOn(myPlayer.myLoc.add(Direction.NORTH), RobotLevel.ON_GROUND);
					forward();
					forward();
					
					obj = HeroWraithBuildOrder.SLEEP;
					return;
					
				case SLEEP:
					
					Utility.setIndicator(myPlayer, 0, "SLEEP");
					Utility.setIndicator(myPlayer, 1, "zzzzzz");
					myPlayer.myRC.turnOff();
					return;
					
			}
		}

		public void turn(Direction d) throws Exception
		{
			while ( myPlayer.myMotor.isActive() )
				myPlayer.sleep();
			myPlayer.myMotor.setDirection(d);
		}
		
		public void forward() throws Exception
		{
			while ( myPlayer.myMotor.isActive() )
				myPlayer.sleep();
			myPlayer.myMotor.moveForward();
		}
		
		public void backward() throws Exception
		{
			while ( myPlayer.myMotor.isActive() )
				myPlayer.sleep();
			myPlayer.myMotor.moveBackward();
		}
		
		@Override
		public void newComponentCallback(ComponentController[] components)
		{
			
		}

		@Override
		public void onWakeupCallback(int lastActiveRound)
		{
			obj = HeroWraithBuildOrder.REBUILD_MAIN;
		}

		@Override
		public String toString()
		{
			return "HeroWraithBehavior";
		}
}
