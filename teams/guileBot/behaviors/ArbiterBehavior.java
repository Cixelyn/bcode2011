package guileBot.behaviors; import battlecode.common.*; import guileBot.*;



/**
 * <pre>
 * 
 *                                           .                                     
 *                                       . M..M,                                 
 *                                          M.  M                                
 *                                           MM   M.                             
 *                                             M. ,.M                            
 *     .MNM                                     N M. M.                          
 *     M . M.                                   .N MM  M                         
 *    .M.  .MM                                   .N .MMMM                        
 *    .M     MM                                  .MM.DM..M.                      
 *     M      M                                    NM  M, M                      
 *     MM   .  M .                                  M   M. M.                    
 *     .M.  N  MM.                                   M. ..M M                    
 *     .M. M    MM                                   MM .M  M.M                  
 *     .M.M     .M.                                   MM M.   M.                 
 *     .M M       M                                    M. .  MDM                 
 *       M        MMM                                 .M  M. M...M               
 *       M.   ..M.. M.                                 M .,  M.   M              
 *      .MM   MN    .MM                     ..         .N  MMM    M,             
 *        M. .M       MM                 . MNMN MMM..  .M..M M   .M              
 *        MM M.        MM                M     M    .  .MMMM. M.   M             
 *         MM.          .M.              N.      M  M   M.  M..M   M             
 *         M.     .MMM    M...NMM.       N.   MM  MMN .M  .NM. .MNMM             
 *          M.    M  .MM..MMM   . MM     M    M .M..N.M .M.N M     M.MMM         
 *          MM  .M      .MM M.     .N    .M.   M M   .M .M  M. ....MM .M         
 *          .M.  M.       .M M.. . M.M  MMMMM.     MMM M M..MMM.   M .M.         
 *           M.   M,       ,M. M.  ...MM.MM. MM  .M  .M.M.M .M.N   M MM          
 *           ,M    M.        M .M. .. .N . . M.MM .M  M ...MMM  MM M.M           
 *             M   .M..       M  M M,M.M M M .M .M. M M  M M.M..  M.M            
 *            ..M  M..MMMM,  MM   .M.. MM M .  N:..  MM  .M  M..  MMM            
 *              .MM        ,..    .NN  M N.M MMM.MM,       M M.    .N            
 *                M              ,M, NMMM.M.       .M.     .MN..   MM            
 *                 M.           MN.   M..NM.        .MMM M. MM.. MM.M            
 *       .MMMMM    .M          M       M   M. ,M..DMMNM.  M MMMMM   .            
 *      .M   M ,MMMMMM        M         M  M..M.M..   .M . M .M.,M M.            
 *      .MM .M     MM MM    ,M.           .M.M. MM  M M.M.MM  ...MM              
 *          .MMMM   M. .M .DMMM          M M  M     MM .MM.N. MM                 
 *             .  MMMM  ..M M...M      .MMM. .MMMMM.MMMM.M.M N..                 
 *                    MMM  M  M.M    MM  .M.M M      M .M  M M                   
 *                       .MM.M.     M    .MM M.       M  .M.M.                   
 *                         ..MMMMM..M  MM. .          MMMMMM.                    
 *                             M  M.N,.MM..                                      
 *                              MN.  .MM. MM                                     
 *                                MM .. M.MM                                     
 *                                                                               
 *                                                                               
 * 
 * </pre>
 * The arbiter is a unit that runs around the map and attempts to destroy mines during the lategame.
 * @author Cory
 *
 */
public class ArbiterBehavior extends Behavior{
	
	
	private ArbiterBuildOrder state;
	private int[] arbiterLoadout;
	
	

	public ArbiterBehavior(RobotPlayer player) {
		super(player);
		
		state = ArbiterBuildOrder.EQUIPPING;									//set our current state
		arbiterLoadout = Utility.countComponents(Constants.arbiterLoadout);		//precompute our unit loadout
	}

	
	private enum ArbiterBuildOrder
	{
		EQUIPPING,
		SEARCH_AND_DESTROY
	}
	
	

	public void run() throws Exception {
		
		switch(state) {
		
		
		case EQUIPPING:
			
			System.out.println(arbiterLoadout);
			System.out.println(Utility.countComponents(myPlayer.myRC.components()));
			Utility.setIndicator(myPlayer, 1, "EQUIPPING ARBITER");
			if( Utility.compareComponents(myPlayer, arbiterLoadout) ) {
				state = ArbiterBuildOrder.SEARCH_AND_DESTROY;
			}
			return;
		
			
		case SEARCH_AND_DESTROY:
			Utility.setIndicator(myPlayer, 1, "SEARCH_AND_DESTROY");
			
			
			
			
			//////////////////////////////////////////////////////////////////////////////////
			// SENSING
			//	 this custom sensing code is designed to be as compact and fast as possible.
			//
			GameObject[] objects = myPlayer.mySensor.senseNearbyGameObjects(GameObject.class);
			
			Mine[] mines = new Mine[64]; int mineIndex = 0;
			Robot[] enemies = new Robot[64]; int enemyIndex = 0;
			
			for(int i=objects.length; --i>=0;) {
				
				GameObject obj = objects[i];
				
				
				
				if(obj.getTeam()==myPlayer.myOpponent) { 		//Enemy Robot Detected
					enemies[i] = (Robot)obj; //cast it correctly
					enemyIndex++;					
				}
				else {					
					if(obj.getRobotLevel()==RobotLevel.MINE) {	//Mine Detected
						mines[i] = (Mine)obj;
						mineIndex++;
					} else {									//Debris Detected
						
					}
				}				
			}
			
			
			Utility.setIndicator(myPlayer, 2, "E:"+enemyIndex+" M:"+mineIndex);
			
			
			
			return;
		
		
		
		}
	}

	
	public void newComponentCallback(ComponentController[] components) {
	}

	public void newMessageCallback(MsgType type, Message msg) {
	}

	public void onDamageCallback(double damageTaken) {
		Utility.printMsg(myPlayer, "I GOT HIT!  I shouldn't have been hit :(");
	}

	public void onWakeupCallback(int lastActiveRound) {		
	}
		
	
	
	
	
	
	public String toString() {
		return "Arbiter	Behavior";
	}

}
