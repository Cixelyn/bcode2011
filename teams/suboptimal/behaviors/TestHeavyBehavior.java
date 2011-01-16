package suboptimal.behaviors;

import suboptimal.*;
import battlecode.common.*;


public class TestHeavyBehavior extends Behavior
{
	
	
	private enum TestHeavyBuildOrder
	{
		EQUIPPING,
		MOVE_OUT
	}
	
	private TestHeavyBuildOrder obj = TestHeavyBuildOrder.EQUIPPING;
	
	int num;
	
	boolean hasJump;
	boolean hasSatellite;
	boolean hasRegen;
	int numBlasters;
	
	public TestHeavyBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				myPlayer.myRC.setIndicatorString(1,"EQUIPPING");
				hasJump = false;
				hasSatellite = false;
				hasRegen = false;
				int numBlasters = 0;
				for ( int i = myPlayer.myRC.components().length; --i>= 0;)
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.JUMP )
						hasJump = true;
					if ( c.type() == ComponentType.SATELLITE )
						hasSatellite = true;
					if ( c.type() == ComponentType.REGEN )
						hasRegen = true;
					if ( c.type() == ComponentType.BLASTER )
						numBlasters++;
				}
				if ( hasJump && hasSatellite && hasRegen && numBlasters >= 2 )
					obj = TestHeavyBuildOrder.MOVE_OUT;
				return;
	        	
			case MOVE_OUT:	
				
	        	myPlayer.myRC.setIndicatorString(1, "MOVE_OUT");
	        	Utility.bounceNav(myPlayer);
	        	return;
	        	
		}
	}
	
	
	
	public String toString()
	{
		return "TestHeavyBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM_FLYER )
		{
			num = msg.ints[Messenger.firstData+1];
			Utility.setIndicator(myPlayer, 2, "I'm heavy " + Integer.toString(num) + "!");
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
}
