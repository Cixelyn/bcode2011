package costax;


/**
 * These are all the possible messagetypes that can be broadcast.
 * @author Cory
 *
 */
public enum MsgType {
	
	
	//ttl 	- how long is this message valid?  0 = always valid
	//cback	- should this function trigger callback?
	
				//	ttl, cback, ints, locs
	MSG_HELLO	(	1  , true , 0 , 0	),
	MSG_POWER_UP(   0  , true , 0 , 0   ),
	MSG_MOVE_OUT(   0  , true , 0 , 2   );
	
	
	
	public int ttl;
	public boolean shouldCallback;
	public int numInts;
	public int numLocs;
	

	MsgType(int _ttl, boolean _cback, int _numInts, int _numLocs) {
		ttl = _ttl;
		shouldCallback = _cback;	
		numInts = _numInts;
		numLocs = _numLocs;
	}
					
					
					
	
	
	
	
	
	

}
