package corybot;

import java.util.LinkedList;

import battlecode.common.Clock;
import battlecode.common.Message;



/**
 * Messenger Class.  Loosly based on the old Broadcaster class from Lazerguns pewpew
 * <pre>
 * MESSAGE BLOCK FORMAT------------------------------------------|           
 *    idx       0           1            2            3          |
 *    ints [ hash       , info       , data      , data..........|
 *    locs [ source     , origin     , data      , data..........|
 * </pre>
 * @author Cory
 *
 */


public class Messenger {
	
	//public variable
	final RobotPlayer myPlayer;

	//send component needs to be enabled
	boolean canSend;
	
	//static limits
	private static final int ID_MOD = 1024;
	
	
	final LinkedList<Packet> packets;
	
	
	
	public Messenger(RobotPlayer player) {
		packets = new LinkedList<Packet>();
		myPlayer = player;		
		canSend = false;		
	}
		
	
	/**
	 * This call is run whenever the robot gains an antennae
	 */
	public void enableSender() {
		canSend = true;		
	}
	
	
	
	/**
	 * This is the only thing you should be calling.
	 * @param p
	 */
	public void sendPacket(Packet p) {}
	
	
	
	public void receiveAll() {
		
		int currTime = Clock.getRoundNum();
		
		
		
		
		
	}
	
	public void sendAll() {
		
		
	}
	
	
	

}
