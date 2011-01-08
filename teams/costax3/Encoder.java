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
	
	//MSG ROBOT Binary Format - 32bit INT:
	//Team(1) + Notes(3) + Active(1) + HP(9) + Weight(5) + Chassis(3) + ID(10)
	
	/*
	public static int ROBOT_ID_OFFSET
	public static int ROBOT_CHASSIS_OFFSET
	
	public static int ROBOT_WEIGHT_OFFSET
	public static int ROBOT_HP_OFFSET
	public static int ROBOT_ACTIVE_OFFSET
	public static int ROBOT_NOTES_OFFSET
	
	public 
	*/
	
	
	public static int encodeRobotInfo(RobotInfo r) {
		return (int)r.hitpoints*10;
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
	
	
	
	
	
	
	
	
	
	
	
	
	

}
