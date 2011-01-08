package costax3;

import battlecode.common.*;

public class LightBehavior extends Behavior {

	public LightBehavior(RobotPlayer player) {
		super(player);
	}

	public void run() {
		if (Clock.getRoundNum() <= 2)
			myPlayer.swapBehavior(new SCVBehavior(myPlayer));
		else
			myPlayer.myRC.setIndicatorString(1, "WHO AM I???");
	}
	
	
	
	public String toString() {
		return "LightBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components) {
		if (Clock.getRoundNum() > 2)
		{
			for(ComponentController c:components)
			{
				if (c.type() == ComponentType.CONSTRUCTOR)
				{
					myPlayer.swapBehavior(new MuleBehavior(myPlayer));
					return;
				}
				if (c.type() == Constants.GUNTYPE)
				{
					myPlayer.swapBehavior(new MarineBehavior(myPlayer));
					return;
				}
			}
		}
	}





	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		
	}
}
