package beambot.behaviors; import beambot.*; import battlecode.common.*; import java.util.*;

/**
 * <pre>
 *
 *                                                                                                                       
 *                                                                                                                       
 *                                                                     ....                                              
 *                                                                      Z+.                                              
 *                                                                 .:MM..M.                                              
 *                                                            ..  M+      I  ,..            ........                     
 *                                                        .. ..DM      .. M?,8... . .,:~:~=7O8888888.                    
 *                                                        .,DM,,.        .  ..    ..    .. .       M.                    
 *                                                   . ,:NM.,..,D..NN..                          ..M.                    
 *                                                 ,.8M .  ...,M:.:.,.                            .D                     
 *                                                OM  ... + 78. M. . .                            D                      
 *                                             .N.,..    M? ,.   .          ?                    .M .                    
 *                                           . 8.         .  .  . . ..  ,M.D                    .M.                      
 *                                         ., M,.      .,NMMM .. ,MMZ8N  .M                     ,M                       
 *                                         .M8 M.     MD.  . 8= .. ?$O                          M..                      
 *                                       :MMMM M.   ,MMMMN   .M.  . ..                         I?.                       
 *                                    . :?.MM .M.  .M .MD   , M                              .+M.                        
 *                     .  .. .   . ...  M.    N    .O        M:                              ,M                          
 *                       , =77777+ . .  M.  .77 .  +        M$.                           ...M. .                        
 *               .. .8MMMZ . . .    M$..:MMMM .    .M.Z.?MMN                            . .MZ.                           
 *          . ...OMM.           ..Z+,:                    ..  ?                   . ...=MM, . .                          
 *          ..MMZ..,.           .+ ,O.                     ?O,O,                  .IN:...  ,MM                           
 *       . MM=. ,    .  .  ... . 7..M.                       . .  ,~MMI .  . . . .$MMMM$.. .   N,                        
 *    .  MM.     .,.NMMMDZ$?=$8MMMO,.   . ,..            .    .  7M                   .  7MMM:  ,M.                      
 *    .8M.  . ..,MM.              .: .  MZI,   .    . :M.       M                             .MM:MN.    .               
 *    M8.     ?M,....               MNM= ..MIM 7N,M7,M ..   ..+M~.                             .  =M?+NM .               
 *   78.    .NZ .                .....O:      ..   .      $D:. .                                   . MMZ ,               
 *  .M     .MO                .:MM.   ,,D$,.                 .MMMMMMN8$7?~..   ..                    ..M?                
 *   +M    ,M.                MM .    ..  .~.               . . .       ..,DMMMM8~.                  . .NM .             
 *    M?  .M                ?M.       =~Z                              .   ... .,.?MM8..               . ~N.             
 *    .MZ  8:.           .,MM,  ... ,MD.8                       .,8MMNM .             7M$                 M.             
 *      7M ?M          .. M$   ,.MM     D     .. .   ...        .,~M .  .8              IM  .             N.             
 *       .MMM,         , M?  .$M7, .    M.   .Z..  . ...         .  M..  .M              IN.              M              
 *        ,?MM         .M:. ,MM  .      M.    .         ?,          M .   M            . M .           . M .             
 *           NM        M+  NM  .        M.    7         ,Z        . M     M         .  MD          .,. MM,               
 *            OMM. .. MM  OM            ? .    =D  . =: .M        . M.    M     ....MMI.        ....8M7                  
 *          .  .MMZ ..M..=M.            .M     ..?7....  M.        M ..  I      7MM ,.       .,:MMM,.M                   
 *                ....M..MM               M .    . ..OZ.. +,    , M,.   .  =MMM~.,   . .~OMMMZ. .  . M.                  
 *                   .M7.MD           .  .? .      . .. ..D. ...,M,.  :NMMN  .. ?NMMMMMO.            M                   
 *                    +M.MM            .7..O        .. M.,.D .MM  MMMMMMMMMMDOZZ$Z$      .         .I .                  
 *                   ..,MIM            .,MMMMN?..    . O?NMMM ,. .....IM....M . ...                 M..                  
 *                     .,ZMM   .           D MMM8+ZZ7.. ,. .  .. . ,MMI.    M           .M       . M.                    
 *                       . ,MM8.          ,~.     ,8MMMMMMMMMMMMMM,..      :M           .M . .. =MM                      
 *                       . ,., .         .=..    . . .. .  ..N ...,.       IM            M+NNMMM8,:.                     
 *                                      ..= .        . M..  .,..           7M           ..  ... .                        
 *                                         M:.  .  .. .M..   .D            8M                                            
 *                                           IMMMMMMMM8.      M.       ....M.                                            
 *                                           ... . .. ..    ..?7,      ..:OM.                                            
 *                                                            .. DMMMMMO .                                               
 *                                                                                                                       
 *     
 *  
 *  
 * </pre> 
 * @author MAX
 *
 */
public class UltraliskBehavior extends Behavior
{
	
	private enum ColossusBuildOrder
	{
		EQUIPPING,
		DETERMINE_SPAWN,
		ADVANCE,
		RETREAT
	}
	
	
	ColossusBuildOrder obj = ColossusBuildOrder.EQUIPPING;
	
	RobotInfo enemyInfo;
	
	int num = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int westEdge = -1;
	int spawn = -1;
	int rally = -1;
	int permRally = -1;
	int timeOffPerm = 0;
	int jump;
	
	int numBounces;
	int numStuck;
	
	int maxRange;
	
	boolean rallyChanged = false;
	
	ArrayDeque<MapLocation> prevLocs = new ArrayDeque<MapLocation>();
	
	
	public UltraliskBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:	

				Utility.setIndicator(myPlayer, 1, "EQUIPPING ULTRA");
				
				// Decide what kind of heavy I am
				int[] currentLoadOut = Utility.countComponents(myPlayer.myRC.components());
				if (Utility.compareComponents(currentLoadOut, Constants.heavyLoadout2 ) && num != -1 )
				{
					maxRange = ComponentType.HAMMER.range;
					obj = ColossusBuildOrder.DETERMINE_SPAWN;
				}
				return;
	        	
			case DETERMINE_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "DETERMINE_SPAWN");
				
				while ( westEdge == -1 || northEdge == -1 || eastEdge == -1 || southEdge == -1 )
				{
					Utility.attackEnemies(myPlayer);
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myLoc.add(Direction.NORTH, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.NORTH, 6)) == TerrainTile.OFF_MAP )
							northEdge = 1;
						else
							northEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myLoc.add(Direction.EAST, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.EAST, 6)) == TerrainTile.OFF_MAP )
							eastEdge = 1;
						else
							eastEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myLoc.add(Direction.SOUTH, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.SOUTH, 6)) == TerrainTile.OFF_MAP )
							southEdge = 1;
						else
							southEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myLoc.add(Direction.WEST, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.WEST, 6)) == TerrainTile.OFF_MAP )
							westEdge = 1;
						else
							westEdge = 0;
					}
					while ( myPlayer.myMotor.isActive() )
						myPlayer.sleep();
					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
					myPlayer.sleep();
				}
				spawn = Utility.getSpawn(westEdge, northEdge, eastEdge, southEdge);
				if ( spawn != -1 )
				{
					if ( num < 2 || num >= 6 )
						rally = (spawn + 4) % 8;
					else if ( num % 2 == 0 )
					{
						numBounces = 3; // automatically patrol the edge of map
						if ( spawn % 2 == 1 )
							rally = (spawn + 3) % 8;
						else
							rally = (spawn + 2) % 8;
					}
					else if ( num % 2 == 1 )
					{
						numBounces = 3; // automatically patrol the edge of map
						if ( spawn % 2 == 1 )
							rally = (spawn + 5) % 8;
						else
							rally = (spawn + 6) % 8;
					}
					Utility.setIndicator(myPlayer, 2, "I KNOW we spawned " + Direction.values()[spawn].toString() + ", heading " + Direction.values()[rally].toString() + ".");
				}
				else
				{
					numBounces = 3; // automatically patrol the edge of map
					rally = (2 * num) % 8;
					Utility.setIndicator(myPlayer, 2, "I don't know where we spawned, heading " + Direction.values()[rally].toString() + ".");
				}
				permRally = rally;
				obj = ColossusBuildOrder.ADVANCE;
				return;
				
			case ADVANCE:	
				
				Utility.setIndicator(myPlayer, 1, "ADVANCE");
				
				// Rally to center if we're confident enough in where it is
				if ( Clock.getRoundNum() == Constants.SCRAMBLE_TIME )
				{
					if ( myPlayer.myLoc.distanceSquaredTo(myPlayer.myCartographer.getMapCenter()) < ComponentType.JUMP.range )
						Utility.setIndicator(myPlayer, 2, "I'm at the center of the map already!!");
					else
					{
						switch ( myPlayer.myCartographer.getConfidence() )
						{
							case 0:
								
								Utility.setIndicator(myPlayer, 2, "I've never seen a map edge before. Are they pretty?");
								rallyChanged = true;
								break;
								
							case 1:
								
								Utility.setIndicator(myPlayer, 2, "I've only seen one map edge, I'm not scrambling.");
								rallyChanged = true;
								break;
								
							case 2:
								
								Utility.setIndicator(myPlayer, 2, "I've seen two map edges, I'd rather not scramble.");
								rallyChanged = true;
								break;
								
							case 3:
								
								rally = myPlayer.myLoc.directionTo(myPlayer.myCartographer.getMapCenter()).ordinal();
								Utility.setIndicator(myPlayer, 2, "I'm pretty sure the center is " + Direction.values()[rally].toString() + ", rerallying.");
								permRally = rally;
								rallyChanged = true;
								break;
								
							case 4:
								
								rally = myPlayer.myLoc.directionTo(myPlayer.myCartographer.getMapCenter()).ordinal();
								Utility.setIndicator(myPlayer, 2, "I KNOW the center is " + Direction.values()[rally].toString() + ", rerallying.");
								permRally = rally;
								rallyChanged = true;
								break;
						}
					}
				}
				
        		// Attacking code
        		enemyInfo = Utility.attackEnemies(myPlayer);
        		
        		// No enemy found
        		if ( enemyInfo == null || myPlayer.myLoc.distanceSquaredTo(enemyInfo.location) > maxRange )
        		{
        			// enemy is sensed but is far
        			if ( enemyInfo != null )
        			{
        				rally = myPlayer.myLoc.directionTo(enemyInfo.location).ordinal();
        				Utility.setIndicator(myPlayer, 2, "Enemy on the horizon, rerallying " + Direction.values()[rally].toString() + ".");
        				// THIS IS NOT A PERMANENT RALLY
        				rallyChanged = true;
        				
        				if ( !myPlayer.myMotor.isActive() )
        				{
	        				if ( (myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location) || myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location).rotateLeft() || myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location).rotateRight()) && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
	        					myPlayer.myMotor.moveForward();
	        				else if ( myPlayer.myRC.getDirection() != myPlayer.myLoc.directionTo(enemyInfo.location) )
	        					myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(enemyInfo.location));
        				}
        			}
        			else
        			{
	        			// Off map rerally code
	        			if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[rally],6)) == TerrainTile.OFF_MAP )
	    	        	{
	    	        		if ( numBounces == 0 )
	    	        			rally = (rally + 2) % 8; // we have reached the enemy side, everyone search together
	    	        		else if ( numBounces == 1 )
	    	        			rally = (rally + 4) % 8; // we have searched one part of the enemy side, everyone go back together
	    	        		else
	    	        		{
	    	        			// we have cleared the enemy side, spread out and patrol the sides of the map
	    	        			if ( num % 2 == 0 )
	    		        			rally = (rally + 2) % 8;
	    		        		else
	    		        			rally = (rally + 6) % 8;
	    	        		}
	    	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
	    	        		numBounces++;
	    	        		permRally = rally;
	    	        		rallyChanged = true;
	    	        	}
	            		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
	    	        	{
	    	        		// we have reached the closest side to the enemy corner, rerally to corner
	    	        		if ( num % 2 == 0 )
	    	        			rally = (rally + 1) % 8;
	    	        		else
	    	        			rally = (rally + 7) % 8;
	    	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
	    	        		numBounces++;
	    	        		permRally = rally;
	    	        		rallyChanged = true;
	    	        	}
	            		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
	    	        	{
	            			// we have reached the closest side to the enemy corner, rerally to corner
	    	        		if ( num % 2 == 0 )
	    	        			rally = (rally + 7) % 8;
	    	        		else
	    	        			rally = (rally + 1) % 8;
	    	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
	    	        		numBounces++;
	    	        		permRally = rally;
	    	        		rallyChanged = true;
	    	        	}
        			}
	            		
	        			// Try to jump
	        			jump = myPlayer.myActions.jumpInDir(Direction.values()[rally]); // myLoc is set here if jump is successful
						if ( jump == Actions.JMP_SUCCESS )
						{
							// Jumped successfully
							prevLocs.add(myPlayer.myLoc);
							if ( prevLocs.size() > Constants.STUCK_JUMPS )
								prevLocs.pollFirst();
							
							// check if we're pursuing a non-permanent rally
							timeOffPerm++;
							if ( timeOffPerm >= Constants.OLD_NEWS )
							{
								rally = permRally;
								timeOffPerm = 0;
							}
							
							// No enemy found before jumping, check again after
							enemyInfo = Utility.attackEnemies(myPlayer);
						}
						else if ( jump == Actions.JMP_NOT_POSSIBLE || (prevLocs.size() >= Constants.STUCK_JUMPS && prevLocs.peekFirst().distanceSquaredTo(myPlayer.myLoc) < ComponentType.JUMP.range) )
						{
							// "Can't jump there, somethins in the way"
							prevLocs.clear();
							rally = (3*numStuck) % 8;
							numStuck++;
							Utility.setIndicator(myPlayer, 2, "I'm stuck, rerallying " + Direction.values()[rally].toString() + ".");
							permRally = rally;
							rallyChanged = true;
						}
        		}
        		
        		// Enemy in range, either before or after jump. Enable the micros
        		if ( enemyInfo != null && myPlayer.myLoc.distanceSquaredTo(enemyInfo.location) <= maxRange )
        		{
        			if ( myPlayer.myLoc.distanceSquaredTo(enemyInfo.location) < maxRange )
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy in range, backing up!");
        				if ( !myPlayer.myMotor.isActive() )
        				{
	        				if ( (myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location)
	        						|| myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location).rotateLeft()
	        						|| myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location).rotateRight() )
	        						&& myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().opposite())
	        						&& myPlayer.myLoc.add(myPlayer.myRC.getDirection().opposite()).distanceSquaredTo(enemyInfo.location) <= maxRange )
	        					myPlayer.myMotor.moveBackward();
	        				else if ( myPlayer.myRC.getDirection() != myPlayer.myLoc.directionTo(enemyInfo.location) )
	        					myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(enemyInfo.location));
        				}
        			}
        			else
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy detected, engaging.");
        				if ( !myPlayer.myMotor.isActive() )
        				{
	        				if ( (myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location) || myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location).rotateLeft() || myPlayer.myRC.getDirection() == myPlayer.myLoc.directionTo(enemyInfo.location).rotateRight()) && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()))
	        					myPlayer.myMotor.moveForward();
	        				else if ( myPlayer.myRC.getDirection() != myPlayer.myLoc.directionTo(enemyInfo.location) )
	        					myPlayer.myMotor.setDirection(myPlayer.myLoc.directionTo(enemyInfo.location));
        				}
        			}
        		}
        		// No enemy found before/after jumping
        		else if ( enemyInfo == null && !myPlayer.myMotor.isActive() )
        		{
        			if ( !rallyChanged )
        				Utility.setIndicator(myPlayer, 2, "No enemies detected, rallied " + Direction.values()[rally].toString() + "."	);
        			// Make sure I'm not getting flanked
        			if ( myPlayer.hasTakenDamage )
    	        		myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().opposite());
        			// Move forward if you can
        			else if ( myPlayer.myRC.getDirection() != Direction.values()[rally] )
        				myPlayer.myMotor.setDirection(Direction.values()[rally]);
        			else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) && jump != Actions.JMP_SUCCESS )
        				myPlayer.myMotor.moveForward();
        		}
        		rallyChanged = false;
	        	return;	        	     
		}
	}
	
	
	
	
	
	
	
	
	
	

	

	
	
	public String toString()
	{
		return "ColossusBehavior";
	}


	
	

	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM )
		{
			if ( num == -1 )
			{
				num = msg.ints[Messenger.firstData+1];
				if ( num == 0 )
					Utility.setIndicator(myPlayer, 0, "I'm heavy " + Integer.toString(num) + ", double railguns all the way!");
				else if ( num % 3 == 1 )
					Utility.setIndicator(myPlayer, 0, "I'm heavy " + Integer.toString(num) + ", prismatic core online!");
				else if ( num % 3 == 2 )
					Utility.setIndicator(myPlayer, 0, "I'm heavy " + Integer.toString(num) + ", rawrrrrrrrr!");
				else if ( num % 3 == 0 )
					Utility.setIndicator(myPlayer, 0, "I'm heavy " + Integer.toString(num) + ", pl4$m4 r 4 n))b$!");
			}
		}
	}

	public void newComponentCallback(ComponentController[] components)
	{
		
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
	
}