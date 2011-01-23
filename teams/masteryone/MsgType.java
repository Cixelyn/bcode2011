package masteryone;


/**
 * These are all the possible messagetypes that can be broadcast.
 * @author Cory
 *
 */
public enum MsgType {
	
	
	//ttl 	 - how long is this message valid?  0 = always valid
	//cback	 - should this message trigger callback?
	//rbcast - should this message trigger rebroadcasting?
	//ints   - number of ints in message
	//locs   - number of locs in message
	
				//	ttl, cback,  rbcast,  ints, locs	
	MSG_HELLO	(	1  , true , false , 0 , 0	),
	MSG_SEND_ID (   0  , true , false , 1 , 0   ),
	MSG_SEND_DOCK(  0  , true , false , 0 , 1   ),
	MSG_SEND_NUM(   0  , true , false , 2 , 1   ),
	MSG_STOP_TANKS( 0  , true , false , 0 , 0   ),
	MSG_START_TANKS(0  , true , false , 0 , 0   ),
	MSG_ENEMY_LOC(  0  , true , false , 1 , 1   ),
	MSG_DET_LEADER( 0  , true , false , 2 , 2   ),
	MSG_MINES(      0  , true , false , 0 , 5   );
	
	public int ttl;
	public boolean shouldCallback;
	public boolean shouldRebroadcast;
	public int numInts;
	public int numLocs;
	
	
	MsgType(int _ttl, boolean _cback, boolean _rbcast, int _numInts, int _numLocs) {
		ttl = _ttl;
		shouldCallback = _cback;	
		shouldRebroadcast = _rbcast;
		numInts = _numInts+Messenger.minSize;	//We need to add minSize because of padding from headers
		numLocs = _numLocs+Messenger.minSize;	//This allows us to save bytecodes in the validation loop
	}
					
	
	
	
	
	
	

}
