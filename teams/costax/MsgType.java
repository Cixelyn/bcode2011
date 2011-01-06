package costax;


/**
 * These are all the possible messagetypes that can be broadcast.
 * @author Cory
 *
 */
public enum MsgType {
	
	
	//ttl 	- how long is this message valid?  0 = always valid
	//cback	- should this function trigger callback?
	
				//	ttl, cback
	MSG_HELLO	(	1  , true	),
	MSG_POWER_UP(   0  , true   ),
	MSG_MOVE_OUT(   0  , true   );
	
	
	
	public int ttl;
	public boolean shouldCallback;
	
	
	MsgType(int _ttl, boolean _cback) {
		ttl = _ttl;
		shouldCallback = _cback;		
	}
					
					
					
	
	
	
	
	
	

}
