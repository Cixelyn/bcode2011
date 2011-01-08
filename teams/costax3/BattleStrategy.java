package costax3;



import costax3.Navigation;
import costax3.RobotPlayer;
import costax3.Utility;
import battlecode.common.BroadcastController;
import battlecode.common.Clock;
import battlecode.common.RobotController;

public abstract class BattleStrategy {
	protected final RobotPlayer player;
	protected final RobotController myRC;
	protected final Navigation myNavi;
	protected int executeStartTime;
	private boolean started;
	protected BroadcastController myRadio;
	
	
	
	//Debug Stuff
	//private boolean debug_BytecodeOverflow = true;
	
	

	public BattleStrategy(RobotPlayer player) {
		this.player = player;
		this.myRC = player.myRC;
		this.myRadio = player.myBroadcaster;
		this.myNavi = player.myNavigation;
		started = false;
	}


	public abstract void runBehaviors();



	public void execute() {

		

		//RUN STANDARD STRATEG
		try {//RUN STANDARD BEHAVIORS
			runBehaviors();
		} catch (Exception e) {
			//System.out.println("Exception caught by RobotPlayer: runBehaviors");
			e.printStackTrace();
		}

		
		
		/*
		//CHECK OUR CLOCK
		if(debug_BytecodeOverflow){
			if(executeStartTime!=Clock.getRoundNum()) {
				int byteCount = (6000-executeStartByte) + (Clock.getRoundNum()-executeStartTime-1) * 6000 + Clock.getBytecodeNum();
				System.out.println("Warning: Unit over Bytecode Limit: "+ byteCount);
			}
		}
		*/
		

		//YIELD AND END TURN
		player.myRC.yield();

	}
}