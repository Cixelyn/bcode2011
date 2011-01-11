package masteryone.behaviors;

import battlecode.common.*;
import masteryone.MsgType;
import masteryone.RobotPlayer;

public class FlyingDroneBehavior extends Behavior {
	
	FlyingDroneActions obj = FlyingDroneActions.EQUIPPING;
	
	boolean hasSight = false;
	boolean hasConstructor = false;
	boolean foundID = false;
	int ID;
	
	Direction initialDirection;

	public FlyingDroneBehavior(RobotPlayer player) {
		super(player);
	}
	
	@Override
	public void run() throws Exception {
		
		if (hasSight && hasConstructor) { //finally we are good to go!
			obj=FlyingDroneActions.FLYING_DRONE_ID;
		}
		
    	switch (obj) {
    	
    		case EQUIPPING: {
    			myPlayer.sleep();
    		}
    		
    		case FLYING_DRONE_ID: {
    			if (foundID) {
    				setDirectionID(ID);
    			}
    		}
    		case EXPAND: {
    			if (!myPlayer.myMotor.isActive()) {
    			}
    		}
    	}
		
	}
	@Override
	public void newComponentCallback(ComponentController[] components) {
		for (ComponentController component: components) {
			if (component.type().equals(ComponentType.SIGHT)) {
				hasSight=true;
			}
			if (component.type().equals(ComponentType.CONSTRUCTOR)) {
				hasConstructor=true;
			}
		}
	}

	@Override
	public void newMessageCallback(MsgType type, Message msg) {
		if (type.equals(MsgType.MSG_DRONE_ID)) {
			foundID=true;
			ID=msg.ints[0];
		}
	}

	@Override
	public String toString() {
		return "FlyingDroneBehavior";
	}
	
	public void setDirectionID(int ID) throws GameActionException {
		if (ID==0) {
			myPlayer.myMotor.setDirection(Direction.WEST);
		}
		if (ID==1) {
			myPlayer.myMotor.setDirection(Direction.EAST);
		}
		if (ID==2) {
			myPlayer.myMotor.setDirection(Direction.NORTH);
		}
	}

}
