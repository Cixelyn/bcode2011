package corybot;

import java.util.LinkedList;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
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
	private final int teamKey;
	
	
	final LinkedList<Message> messageQueue;
	
	
	
	public Messenger(RobotPlayer player) {
		teamKey=player.myRC.getTeam().hashCode();
		messageQueue = new LinkedList<Message>();
		myPlayer = player;		
		canSend = false;		
	}
		
	
	/**
	 * This call is run whenever the robot gains an antennae
	 */
	public void enableSender() {
		canSend = true;		
	}
	
	
	
	public void sendMsg(Message m) {
		if(canSend) {
			messageQueue.add(m);
		}
	}
	

	
	
	
	
	/**
	 * Very primitive receive function
	 */
	public void receiveAll() {
		int currTime = Clock.getRoundNum();
		
		Message[] rcv = myPlayer.myRC.getAllMessages();
		
		for(Message m: rcv) {
			if(validate(m)) {
				myPlayer.myBehavior.newMessageCallback(m);
			}
	
		}
	}
	
	
	public void sendAll() throws Exception{
		if(!messageQueue.isEmpty()) {
			myPlayer.myBroadcaster.broadcast(messageQueue.pop());
		}
	}
	
	
	
	
	
	
	
	/**
	 * Very dirty validation system
	 * @param m
	 * @return
	 */
	public boolean validate(Message m) {
		for(MapLocation l:m.locations) {
			if(l==null) return false;
		}
		return true;
	}
	
	

	

}
