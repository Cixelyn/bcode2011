package masteryone.behaviors;

import battlecode.common.*;
import masteryone.*;

public class DefaultBehavior extends Behavior {

	public DefaultBehavior(RobotPlayer player) {
		super(player);
	}


	public void newComponentCallback(ComponentController[] components) {
		for ( ComponentController c : components )
		{
			if ( c.type() == ComponentType.CONSTRUCTOR )
			{
				if ( Clock.getRoundNum() <= 2 )
					myPlayer.swapBehavior(new SCVBehavior(myPlayer));
			}
		}
	}


	public void newMessageCallback(MsgType type, Message msg) {

		
	}

	
	public void run() throws Exception {
		Utility.setIndicator(myPlayer, 1, "WHO AM I???");
		myPlayer.sleep();
	}


	public String toString() {
		return null;
	}
	

}
