package corybot;


/**
 * These define the valid different packets that can be sent over the air.
 * @author Cory
 *
 */
public enum PacketHeader {
	
							//ttl , cback,  mem
	PKT_ROBOT 			(     1   ,	false,  false ),
	PKT_MINELOC 		(	  1   , true,   true  );
	
	
	public int ttl; 
	public boolean shouldCallback;
	public boolean shouldRemember;
	
	PacketHeader(int ttl, boolean shouldCallback, boolean shouldRemember) {
		this.ttl = ttl;
		this.shouldCallback = shouldCallback;
		this.shouldRemember = shouldRemember;
		
	}
	
	
	

}
