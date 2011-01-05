package corybot;

import battlecode.common.MapLocation;


/**
 * The packet format is as follows:
 * 		4 bit header, 28 bit data field.  With MapLocation
 * 
 * 
 * 
 * @author Cory
 *
 */
public class Packet {
	
	public static int HEADER_OFFSET = 28;
	public static int HEADER_MASK 	= 0xF0000000;
	public static int DATA_MASK 	= 0x0FFFFFFF;
	
	
	int iData;
	MapLocation mData;
	
	Packet(int intData, MapLocation mapData) {
		iData = intData;
		mData = mapData;
	}
	
	
	public int getHeader() {
		return (iData & HEADER_MASK) >> HEADER_OFFSET; 
	}
	public int getData() {
		return (iData & DATA_MASK);
	}
}
