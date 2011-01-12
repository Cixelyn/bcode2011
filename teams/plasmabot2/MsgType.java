package plasmabot2;


/**
 * These are all the possible messagetypes that can be broadcast.
 * @author Cory
 *
 */
public enum MsgType {
	
	
	//ttl 	- how long is this message valid?  0 = always valid
	//cback	- should this function trigger callback?
	//ints - number of ints in message
	//locs - number of locs in message
	
				//	ttl, cback, ints, locs
	MSG_HELLO	(	1  , true , 0 , 0	),
	MSG_SEND_ID (   0  , true , 1 , 0   ),
	MSG_SEND_DOCK(  0  , true , 0 , 1   ),
	MSG_SEND_NUM(   0  , true , 1 , 0   );
	
	
	public int ttl;
	public boolean shouldCallback;
	public int numInts;
	public int numLocs;
	

	
	MsgType(int _ttl, boolean _cback, int _numInts, int _numLocs) {
		ttl = _ttl;
		shouldCallback = _cback;	
		numInts = _numInts+Messenger.minSize;	//We need to add minSize because of padding from headers
		numLocs = _numLocs+Messenger.minSize;	//This allows us to save bytecodes in the validation loop
	}
					
					
					
	
	
	
	
	
	

}
