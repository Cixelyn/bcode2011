package costax3.behaviors;

import costax3.MsgType;
import costax3.RobotPlayer;
import battlecode.common.*;

public class BuildingBehavior extends Behavior {

	public BuildingBehavior(RobotPlayer player) {
		super(player);
	}



	public void run() throws Exception {
		if (Clock.getRoundNum() <= 2)
			myPlayer.swapBehavior(new MainRefineryBehavior(myPlayer));
		else
			myPlayer.myRC.setIndicatorString(1, "WHAT AM I???");
	}

	public String toString() {
		return "BuildingBehavior";
	}

	public void newComponentCallback(ComponentController[] components) {
		if (Clock.getRoundNum() > 2)
		{
			for(ComponentController c:components)
			{
				if (c.type() == ComponentType.RECYCLER)
				{
					myPlayer.swapBehavior(new ExpoRefineryBehavior(myPlayer));
					myPlayer.myMessenger.toggleReceive(true);
					return;
				}
				if (c.type() == ComponentType.FACTORY)
				{
					myPlayer.swapBehavior(new FactoryBehavior(myPlayer));
					myPlayer.myMessenger.toggleReceive(true);
					return;
				}
			}
		}
	}
	
	
	public void newMessageCallback(MsgType t, Message msg) {
		
		
	}

}
