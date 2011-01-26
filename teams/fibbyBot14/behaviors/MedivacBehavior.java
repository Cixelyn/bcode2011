package fibbyBot14.behaviors;

import battlecode.common.*;
import fibbyBot14.*;

/**
 * 
 * 
 * @author Justin
 *
 */

public class MedivacBehavior extends Behavior
{
	
	MapLocation frontlineLoc; 
	MapLocation heroWraithLoc;
	int num = -1;
	
	private enum MedivacBuildOrder 
	{
		INITIALIZE,
		GO_TO_FRONTLINE,
		HEAL_HERO_WRAITH,
		RETURN_HOME,
		SLEEP
	}
	
	MedivacBuildOrder obj = MedivacBuildOrder.INITIALIZE;
	
	public MedivacBehavior(RobotPlayer player)
	{
		super(player);
	}

	

	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case INITIALIZE:
				
				Utility.setIndicator(myPlayer, 0, "INITIALIZE");
				Utility.setIndicator(myPlayer, 1, "Ready for dust off!");
				
				if ( Clock.getRoundNum() < Constants.SECOND_MEDIVAC )
				{
					num = 1;
					frontlineLoc = myPlayer.myLoc.add(0, -5);
					heroWraithLoc = myPlayer.myLoc.add(1, -8);
				}
				else
				{
					num = 2;
					frontlineLoc = myPlayer.myLoc.add(0, -6);
					heroWraithLoc = myPlayer.myLoc.add(2, -8);
				}
				obj = MedivacBuildOrder.GO_TO_FRONTLINE;
				return;
				
			case GO_TO_FRONTLINE:
				
				Utility.setIndicator(myPlayer, 0, "GO_TO_FRONTLINE");
				Utility.setIndicator(myPlayer, 1, "Picking up or dropping off?");
				
				if ( num == 1 )
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(Direction.NORTH);
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
				}
				else if ( num == 2 )
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(Direction.NORTH);
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
				}
				
				obj = MedivacBuildOrder.HEAL_HERO_WRAITH;
				return;
				
			case HEAL_HERO_WRAITH:
				
				Utility.setIndicator(myPlayer, 0, "HEAL_HERO_WRAITH");
				Utility.setIndicator(myPlayer, 1, "In the pipe, five by five.");
				myPlayer.myMedics[0].attackSquare(heroWraithLoc, RobotLevel.IN_AIR);
				return;
				
			case RETURN_HOME:
				
				Utility.setIndicator(myPlayer, 0, "RETURN_HOME");
				Utility.setIndicator(myPlayer, 1, "Somebody get me out of this mess!");
				
				if ( num == 1 )
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(Direction.SOUTH);
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
				}
				else if ( num == 2 )
				{
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(Direction.SOUTH);
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
					
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.moveForward();
				}
				
				obj = MedivacBuildOrder.SLEEP;
				return;
				
			case SLEEP:
				
				Utility.setIndicator(myPlayer, 0, "SLEEP");
				Utility.setIndicator(myPlayer, 1, "zzzzzz");
				myPlayer.myRC.turnOff();
				return;
				
		}
    	
	}
	
	
	
	public String toString()
	{
		return "MedivacBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{

	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}

}
