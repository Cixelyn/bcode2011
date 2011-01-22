package guileBot.behaviors;

import battlecode.common.*;
import guileBot.*;



/**
 * The arbiter is a unit that runs around the map and attempts to destroy mines during the lategame.
 * @author Cory
 *
 */
public class ArbiterBehavior extends Behavior{
	
	
	private ArbiterBuildOrder state;
	private int[] arbiterLoadout;
	
	

	public ArbiterBehavior(RobotPlayer player) {
		super(player);
		
		state = ArbiterBuildOrder.EQUIPPING;									//set our current state
		arbiterLoadout = Utility.countComponents(Constants.arbiterLoadout);		//precompute our unit loadout
	}

	
	private enum ArbiterBuildOrder
	{
		EQUIPPING,
		SEARCH_AND_DESTROY
	}
	
	

	public void run() throws Exception {
		
		switch(state) {
		
		
		case EQUIPPING:
			System.out.println(arbiterLoadout);
			System.out.println(Utility.countComponents(myPlayer.myRC.components()));
			Utility.setIndicator(myPlayer, 1, "EQUIPPING ARBITER");
			if(Utility.compareComponents(myPlayer, arbiterLoadout)) {
				state = ArbiterBuildOrder.SEARCH_AND_DESTROY;
			}
			return;
		
			
		case SEARCH_AND_DESTROY:
			Utility.setIndicator(myPlayer, 1, "SEARCH_AND_DESTROY");
			
			
			
						
			
			
			
			
			
			
			
			
			
			return;
		
		
		
		}
	}

	
	public void newComponentCallback(ComponentController[] components) {
	}

	public void newMessageCallback(MsgType type, Message msg) {
	}

	public void onDamageCallback(double damageTaken) {
		Utility.printMsg(myPlayer, "I GOT HIT!  I shouldn't have been hit :(");
	}

	public void onWakeupCallback(int lastActiveRound) {		
	}
		
	
	
	
	
	
	public String toString() {
		return "Arbiter	Behavior";
	}

}
