package costax3;

import battlecode.common.*;


/**
 * This class builds and decodes various different message types which are sent through the messaging subsystem
 * @author Cory
 *
 */
public class Encoder {
	
	
////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////ROBOTDATA//////////////////////////////////////
	
	
	/*
	 * 	MSG ROBOT Binary Format - 32bit INT:
	 *	
	 *	T  N  A     HP     WGT  CHS     ID
	 * 	- --- - --------- ----- --- ----------
	 * 	0 000 0 000000000 00000 000 0000000000
	 * 
	 * 
	 * Team(1) + Notes(3) + Active(1) + HP(9) + Weight(5) + Chassis(3) + ID(10)
	 * 
	 * 
	 */
		
	public static int ROBOT_ID_OFFSET 		= 0;
	public static int ROBOT_ID_MASK 		= 0x3FF;
	public static int ROBOT_ID_LENGTH 		= 10;
	
	public static int ROBOT_CHASSIS_OFFSET 	= ROBOT_ID_LENGTH+ROBOT_ID_OFFSET;
	public static int ROBOT_CHASSIS_MASK 	= 0x1C00;
	public static int ROBOT_CHASSIS_LENGTH 	= 3;
	
	public static int ROBOT_WEIGHT_OFFSET 	= ROBOT_CHASSIS_LENGTH+ROBOT_CHASSIS_OFFSET;
	public static int ROBOT_WEIGHT_MASK 	= 0x3E000;
	public static int ROBOT_WEIGHT_LENGTH 	= 5;
	
	public static int ROBOT_HP_OFFSET 		= ROBOT_WEIGHT_LENGTH+ROBOT_WEIGHT_OFFSET;
	public static int ROBOT_HP_MASK 		= 0x7FC0000;
	public static int ROBOT_HP_LENGTH 		= 9;
		
	public static int ROBOT_ACTIVE_OFFSET 	= ROBOT_HP_LENGTH+ROBOT_HP_OFFSET;
	public static int ROBOT_ACTIVE_MASK 	= 0x8000000;
	public static int ROBOT_ACTIVE_LENGTH 	= 1;
	
	public static int ROBOT_NOTES_OFFSET 	= ROBOT_ACTIVE_LENGTH + ROBOT_ACTIVE_OFFSET;
	public static int ROBOT_NOTES_MASK 		= 0x70000000;
	public static int ROBOT_NOTES_LENGTH 	= 3;
	
	public static int ROBOT_TEAM_OFFSET 	= ROBOT_NOTES_LENGTH + ROBOT_NOTES_OFFSET;
	public static int ROBOT_TEAM_MASK 		= 0x80000000;
	public static int ROBOT_TEAM_LENGTH 	= 1;
	
	
	
	
	/**
	 * Encode RobotInfo 
	 * 
	 * @param notes any notes you want to put. <ol>
	 * Range: <code> -4 to 3 </code>, where the higher number means greater targeting priority <br>
	 * Exceeding the notes range will cause bad things to happen.  Don't do it
	 * 
	 * 
	 * @param r the {@link battlecode.common.Robot Robot} object you wish to encode.  <br>
	 * ID and Team are pulled from here.
	 * @param rinfo the {@link battlecode.common.RobotInfo RobotInfo} object you wish to encode.  <br>
	 * Activation State, HP, and Chassis Type are pulled from here.
	 * @return encoded 32 bit int
	 */
	public static int encodeRobotInfo(int notes, int rid, Team team, RobotInfo rinfo) { //FIXME: Figure out how to do weight later.
		return (((team)==Team.A ? 0:1) << ROBOT_TEAM_OFFSET) 			//Team
			| ((3-notes) << ROBOT_NOTES_OFFSET)							//Notes
			| ((rinfo.on?0:1) << ROBOT_ACTIVE_OFFSET)					//Activated		(0 for a robot that's on)
			| ((int)(rinfo.hitpoints*10.0) << ROBOT_HP_OFFSET) 			//HP			(Multiplied by 10)
			| (0 << ROBOT_WEIGHT_OFFSET)								//Weight			
			| (rinfo.chassis.ordinal())									//Chassis
			| (rid);													//ID
	}
	
	
	//Decoding Functions
	public static double decodeRobotHP(int data) {
		return  ((data & ROBOT_HP_MASK) >> ROBOT_HP_OFFSET)/10.0;		
	}
	
	//Chassis
	public static Chassis decodeRobotChassis(int data) {
		return (Chassis.values()[(data & ROBOT_CHASSIS_MASK) >> ROBOT_CHASSIS_OFFSET]);
	}
	
	//ID
	public static int decodeRobotID(int data) {
		return ((data & ROBOT_ID_MASK) >> ROBOT_ID_OFFSET);
	}
	
	//Activity
	public static boolean decodeRobotActivity(int data) {
		if (((data & ROBOT_ACTIVE_MASK) >> ROBOT_ACTIVE_OFFSET)==0) return true;	//Remember that we inverted activity
		return false;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////MSGDATA////////////////////////////////////////////
	
	//MSG DATA Binary Format - 32bit INT:
	//MsgType(6) + Timestamp(16) +ID(10)
	public static int MSG_ID_OFFSET = 0;
	public static int MSG_ID_MASK = 0x3FF;
	
	public static int MSG_TIMESTAMP_OFFSET = 10+MSG_ID_OFFSET;
	public static int MSG_TIMESTAMP_MASK = 0x3FFFC00;
	
	public static int MSG_TYPE_OFFSET = 16+MSG_TIMESTAMP_OFFSET;
	public static int MSG_TYPE_MASK = 0xFC000000;

	

	//Encoding Function -- Lower means older and more important
	public static int encodeMsgHeader(MsgType type, int time, int id) {
		return (type.ordinal()<<MSG_TYPE_OFFSET)
			| (time<<MSG_TIMESTAMP_OFFSET)
			| (id<<MSG_ID_OFFSET);		
	}
	
	//Decoding Functions
	public static MsgType decodeMsgType(int data) {
		return MsgType.values()[(data & MSG_TYPE_MASK) >> MSG_TYPE_OFFSET];
	}
	
	public static int decodeMsgTimeStamp(int data) {
		return (data &  MSG_TIMESTAMP_MASK) >> MSG_TIMESTAMP_OFFSET;
	}
	
	public static int decodeMsgID(int data) {
		return (data & MSG_ID_MASK) >> MSG_ID_OFFSET;
	}
	
	

	//TODO: Remove constants that add 0 for a small performance boost
	
	
	
	public static void main(String[] args) {
		
		
		int id = 101;
		double hitpoints = 48;
		double maxhp = 50;
		boolean on = true;
		Chassis ctype = Chassis.BUILDING;
		
		
		
		RobotInfo rinfo = new RobotInfo(null, null, hitpoints, maxhp, null, on, null, ctype);
		int result = encodeRobotInfo(0,id,Team.A,rinfo);
		
		System.out.println("Hello World");
		
		System.out.println(decodeRobotHP(result));	
		System.out.println(decodeRobotChassis(result).toString());
		System.out.println(decodeRobotID(result));
		System.out.println(decodeRobotActivity(result));
		
		
	}
	
	
	
	
	
	
	
	
	

}
