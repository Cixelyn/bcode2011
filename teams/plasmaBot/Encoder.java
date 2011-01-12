package plasmaBot;

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
	 * Range: <code> -3 to 3 </code>, where the higher number means greater targeting priority <br>
	 * Exceeding the notes range will cause bad things to happen.  Don't do it
	 * 
	 * 
	 * @param r the {@link battlecode.common.Robot Robot} object you wish to encode.  <br>
	 * ID and Team are pulled from here.
	 * @param rinfo the {@link battlecode.common.RobotInfo RobotInfo} object you wish to encode.  <br>
	 * Activation State, HP, and Chassis Type are pulled from here.
	 * @return encoded 32 bit int
	 */
	public static int encodeRobotInfo(int notes, Robot r, RobotInfo rinfo) { //FIXME: Figure out how to do weight later.
		return (((r.getTeam())==Team.A ? 0:1) << ROBOT_TEAM_OFFSET)		//Team
			| ((3-notes) << ROBOT_NOTES_OFFSET)							//Notes
			| ((rinfo.on?0:1) << ROBOT_ACTIVE_OFFSET)					//Activated		(0 for a robot that's on)
			| ((int)(rinfo.hitpoints*10.0) << ROBOT_HP_OFFSET) 			//HP			(Multiplied by 10)
			| (0 << ROBOT_WEIGHT_OFFSET)								//Weight			
			| (rinfo.chassis.ordinal() << ROBOT_CHASSIS_OFFSET)			//Chassis
			| (r.getID() << ROBOT_ID_OFFSET);							//ID
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
	
	//Notes
	public static int decodeRobotNotes(int data) {
		return 3-((data & ROBOT_NOTES_MASK) >> ROBOT_NOTES_OFFSET);
	}
	
	
	//TODO: Not sure if encoding teams works.  Check that if something breaks.
	
	
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

	
	/////////////////Encoding Function -- Lower means older and more important
	
	/**
	 * Encodes a message header into a 32-bit int for transmission
	 * @param type Type of message you wish to send
	 * @param time Timestamp
	 * @param id Your own ID
	 * @return
	 */
	public static int encodeMsgHeader(MsgType type, int time, int id) {
		return (type.ordinal()<<MSG_TYPE_OFFSET)
			| (time<<MSG_TIMESTAMP_OFFSET)
			| (id<<MSG_ID_OFFSET);		
	}
	
	//////////////////Decoding Functions
	/**
	 * Decodes a message type from the packed header format
	 * @param data packed header
	 * @return message type
	 */
	public static MsgType decodeMsgType(int data) {
		return MsgType.values()[(data & MSG_TYPE_MASK) >> MSG_TYPE_OFFSET];
	}
	
	/**
	 * Decodes timestamp from the packed header format
	 * @param data packed header
	 * @return message timestamp
	 */
	public static int decodeMsgTimeStamp(int data) {
		return (data &  MSG_TIMESTAMP_MASK) >> MSG_TIMESTAMP_OFFSET;
	}
	
	/**
	 * Decodes sender ID from the packed header format
	 * @param data packed header
	 * @return message sender id
	 */
	public static int decodeMsgID(int data) {
		return (data & MSG_ID_MASK) >> MSG_ID_OFFSET;
	}
	


	

}
