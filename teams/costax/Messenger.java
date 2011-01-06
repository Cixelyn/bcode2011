package costax;

import java.util.LinkedList;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Team;



/**
 * Messenger Class.  Loosely based on the old Lazer6 messaging code
 * 
 * 
 * <pre>
 * MESSAGE BLOCK FORMAT------------------------------------------|           
 *    idx       0           1            2            3          |
 *    ints [ hash       , header     , data      , data..........|
 *    locs [ source     , origin     , data      , data..........|
 *    strs [-----------------------------------------------------|
 * </pre>
 * @author Cory
 *
 */
public class Messenger {
	
	//public variable
	final RobotPlayer myPlayer;

	//send component needs to be enabled
	private boolean canSend;
	
	//static limits
	private static final int ROUND_MOD = 4;
	private static final int ID_MOD = 1024;
	private final int teamKey;
	private final int myID;
	
	
	//Defined indexes for readability
	public static final int idxHash = 0;
	public static final int idxHeader = 1;
	public static final int idxSender = 0;
	public static final int idxOrigin = 1;
	public static final int firstData = 2;
	public static final int minSize = firstData;

	final LinkedList<Message> messageQueue;
	
	
	private boolean[][] hasHeard = new boolean[ROUND_MOD][];
	
	
	public Messenger(RobotPlayer player) {
		
		myPlayer = player;							//Assign the player
		canSend = false;							//Default robot doesn't have antennae
		messageQueue = new LinkedList<Message>();	//Build Queue
		
				
		//Initialize our entire 'has heard' table
		for(int i=0; i<ROUND_MOD; i++) {
			hasHeard[i] = new boolean[ID_MOD];
		}
		
		//set ID and key	
		myID = myPlayer.myRC.getRobot().getID();
		if(myPlayer.myRC.getTeam()==Team.A) {
			teamKey = 131071; //first 6 digit mersenne prime
		} else {
			teamKey  = 174763; //first 6 digit wagstaff prime
		}	
	}
		
	
	
	
	/**
	 * This call is run whenever the robot gains an antennae
	 * Note that components can never be removed, so a robot cannot lose it's sending ability.
	 */
	public void enableSender() {
		canSend = true;		
	}
	
	
	
	
	/**
	 * Internal sending function
	 * @param m message to send where the relevant location blocks and int blocks reserved for headers
	 * and such are left blank.  sendMsg computs the hashes and inserts them in.
	 */
	private void sendMsg(MsgType type, Message m) {
		
		//debug code to make sure we're not calling something that can't be done.
		assert canSend;
		
		//fill in message
		int currTime = Clock.getRoundNum();
		MapLocation myLoc = myPlayer.myRC.getLocation();
		
		
		m.ints[idxHeader] = Encoder.encodeMsgHeader(type, currTime, myID);		
		
		m.locations[idxSender] = myLoc;		//sender location
		m.locations[idxOrigin] = myLoc;		//origin location
		
		
		m.ints[idxHash] = teamKey; 			//FIXME: super simple hash
		
		messageQueue.add(m);		
		
		//I've heard my own message
		hasHeard[currTime%ROUND_MOD][myID%ID_MOD] = true;		
	}
	
	
	
	/**
	 * This internal function builds a <code>battlecode.common.Message</code> with 
	 * <code>iSize</code> ints and <code>lSize</code> locations
	 * @param iSize number of ints
	 * @param lSize number of locations
	 * @return
	 */
	private Message buildMessage(int iSize, int lSize) {
		Message m = new Message();
		m.ints = new int[minSize+iSize];
		m.locations = new MapLocation[minSize+lSize];
		
		MapLocation myLoc = myPlayer.myRC.getLocation();
		return m;
	}
	
	
	public void sendNotice(MsgType t) {
		sendMsg(t,buildMessage(minSize,minSize));
	}
	
	public void sendDoubleLoc(MsgType t, MapLocation loc1, MapLocation loc2) {
		Message m = buildMessage(0,2);
		m.locations[firstData  ] = loc1;
		m.locations[firstData+1] = loc2;
		
		sendMsg(t,m);				
	}
	
	

	
	
	/**
	 * Very primitive receive function
	 */
	public void receiveAll() {
		
		Message[] rcv = myPlayer.myRC.getAllMessages();
		
		for(Message m: rcv) {
			if(validate(m)) {
				
				MsgType t = Encoder.decodeMsgType(m.ints[idxHeader]);
				
				if(t.shouldCallback) {
					myPlayer.myBehavior.newMessageCallback(t,m);
				}
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
