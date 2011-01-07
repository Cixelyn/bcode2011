package costax2;

import battlecode.common.*;
import battlecode.world.InternalRobot;

public class RecyclerBehavior extends Behavior{

	public RecyclerBehavior(RobotPlayer player) {
		super(player);
	}
	
	
	
	/**
	 * This builder will do in the following order:
	 * 		Turn until it can see forward
	 * 		Sense a nearby robot
	 * 		If robot doesn't have radar, add a radar to that robot
	 * 		Otherwise, add a gun to that robot
	 */	
	public void run() throws Exception {
		
		
		//Check for a robot in front of me.
		MapLocation inFront = myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection());
		GameObject obj = myPlayer.mySensor.senseObjectAtLocation(inFront,RobotLevel.ON_GROUND);
		
		//If there is a robot in front of me
		if(obj!=null) {
			
				if(obj instanceof Robot ) {
	
					if(obj.getTeam()==myPlayer.myRC.getTeam()) { //if it is on my team 
						
						//Sense components on the robot
						RobotInfo rinfo = myPlayer.mySensor.senseRobotInfo((Robot)obj);
						int sensorCount = Utility.componentClassCounter(rinfo.components)[ComponentClass.SENSOR.ordinal()];
						int weaponCount = Utility.componentClassCounter(rinfo.components)[ComponentClass.WEAPON.ordinal()];
		
						if(sensorCount==0 && Utility.canAdd(ComponentType.RADAR, rinfo)) {  //then build a sensor on the robot
							Utility.buildComponentAt(myPlayer, ComponentType.RADAR, inFront,RobotLevel.ON_GROUND);
							return;
						} else if(weaponCount==0 && Utility.canAdd(ComponentType.SMG, rinfo)) { //then build a weapon on the robot
							Utility.buildComponentAt(myPlayer, ComponentType.SMG, inFront, RobotLevel.ON_GROUND);
							return;
						}
					}
				}
			
		}
	
		//Otherwise, turn until I can see forward
		if(!myPlayer.myMotor.canMove(myPlayer.myRC.getDirection())) {
			myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
			return;
		}
		
		//Spawn a robot
		if(Utility.buildChassisAt(myPlayer, Chassis.LIGHT, inFront)) {
			return;
		}
		
		myPlayer.myRC.setIndicatorString(2,Double.toString(myPlayer.myScanner.averageResourceRate()));
		
		//Else we can't do anything.		
		return;
				
		
	}
	
	
	
	public String toString() {
		return "BuilderBehavior";
	}
	




	@Override
	public void newComponentCallback(ComponentController[] components) {
		// TODO Auto-generated method stub
		
	}








	@Override
	public void newMessageCallback(MsgType t, Message msg) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	

}
