package fibbyBot14.behaviors;


import fibbyBot14.*;
import battlecode.common.*;;

public class HeroWraithBehavior extends Behavior {

		
		int steps=0;
		
		
		private enum HeroWraithBuildOrder
		{
			EQUIPPING,
			MOVE_TOWARDS_TOWER,
			FIGHT_TOWER,
			GO_HOME,
			GET_HEALED;
		}
		
		HeroWraithBuildOrder obj = HeroWraithBuildOrder.EQUIPPING;

		
		public HeroWraithBehavior(RobotPlayer player)
		{
			super(player);
		}
		
		public void run() throws Exception
		{
			
			switch (obj)
			{
				
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
					if ( numBeams == 1 && numShields ==1)
						obj = HeroWraithBuildOrder.MOVE_TOWARDS_TOWER;
						
					return;
				
				case MOVE_TOWARDS_TOWER:
					if (!myPlayer.myMotor.isActive()) {
						if (myPlayer.myRC.getDirection()!=Direction.SOUTH) {
							myPlayer.myMotor.setDirection(Direction.SOUTH);
						}
						else {
							myPlayer.myMotor.moveForward();
							steps++;
						}
						if (steps==11) {
							steps=0;
							obj = HeroWraithBuildOrder.FIGHT_TOWER;
						}
					}
					return;
				case FIGHT_TOWER:
					if (myPlayer.myRC.getHitpoints()<Constants.THRESHOLD_LIFE) {
						obj=HeroWraithBuildOrder.GO_HOME;
					}
					else {
						if (!myPlayer.myBeams[0].isActive()) {
							myPlayer.myBeams[0].attackSquare(myPlayer.myRC.getLocation().add(Direction.SOUTH,6), RobotLevel.ON_GROUND);
						}
					}
					return;
				case GO_HOME:
					if (!myPlayer.myMotor.isActive()) {
						myPlayer.myMotor.moveBackward();
						steps++;
						if (steps==11) {
							steps=0;
							obj = HeroWraithBuildOrder.GET_HEALED;
						}
					}
					return;
				case GET_HEALED:
					if (myPlayer.myRC.getHitpoints()==(myPlayer.myRC.getMaxHp())) {
						obj=HeroWraithBuildOrder.MOVE_TOWARDS_TOWER;
					}
			}
		}

		@Override
		public void newComponentCallback(ComponentController[] components) {
			
		}

		@Override
		public void onWakeupCallback(int lastActiveRound) {
			
		}

		@Override
		public String toString() {
			return null;
		}
}
