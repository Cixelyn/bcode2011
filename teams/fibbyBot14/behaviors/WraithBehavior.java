package fibbyBot14.behaviors;

import fibbyBot14.*;
import battlecode.common.*;

/**
 * 
 * <pre>
 * 
 *                                                                                 
 *                        MMM..                                                  
 *            ..         M    M.                                                 
 *            M..MM       M,  .MM.                                               
 *           M     M      .NM    M..MM                 MM MM                     
 *        . .M M..   M,      M    MM, M            ...M,.   .MM..                
 *        . NM  M.    MM      MM    M...M,.      MMM.   MM.   ..M.               
 *          M  M. N     M       N    .M  M    .MN   MM  . .MN    DM              
 *         M    M.M.     M       MM     M..  M  .      MM   .MM, MM              
 *         M,     M M    .M       .M,.  MMNN     .MMNM.   M ,  MM   M            
 *        .M       MM      M     MM. M. . M.    .M    M..  .,M . .MM  MM         
 *        .MN          M    .MMMM.. ..MN  .  MM ..      .M.  ..M  .  .M MM.      
 *           M.      MM  M .   M      M  MMM. M .     M ..MM MMMMMNMMMM   MM     
 *             M.   M.    N .  .M . .. M    MN.MM  .MM. MM M    MM. M,..MM M     
 *              MMNMM      NM     NM M. ,M ,M.    MMMM   .M       MM..MMMN M     
 *               .M..M,      M    MM.  M... . .   M .  M:.        .  M M  M.     
 *     ,.. .    M  .M .MM.    .M..M  M..,.  .MNM . .MM   M            MM M.      
 *   . M   .M   .    .M  NM.   MNMN   .M    M         N .             . .        
 *  .MMM  . NMNM.       M  M  M ..M      MMN           M.                        
 *  M. .M . .N..M       .MM  M M    M.    M             M                        
 *  M    M    M . M M      M  M.MN ..MM  .M     .M     N                         
 *  M      M   M.  M NM    MNMMMM     .MMMM,.   M       M                        
 *  M      .MM. M    M.N ,M     MM     . M.,M,  MN.      M                       
 *   M      .N.  M    MM M       MM.  ..M.M  MM M  M . .  M                      
 *   MN.    M.    M    ,         M,MN  MM MMM. M.M. MM,MMMM.                     
 *    .M . M..M..  M.. M          M. M.    M,M   M  ..M  .,.                     
 *      ..M   ..M, ,M.N,          .M .M    M  M. M .  N    M                     
 *         M     M..  M            . .MM   M   M.M   M . . MM                    
 *          .M .M..M   M           .M    MMNMM .. M:  .. M. N                    
 *            MM.M  M.MMM           .MM   M . .M   ,MM.  .,M.                    
 *             M..MMMM ..M            .M   .M ..MM  .  M                         
 *             .M.. M .  M             MM.   M  .MM                              
 *               .M .NMMM                M .  .N   M .                           
 *                 MM.N                  .MN, M..MMMM.                           
 *                                          M   M...M                            
 *                                           .M .MMN                             
 *                                                                              
 *                                                                               
 * 
 * </pre>
 * 
 */

public class WraithBehavior extends Behavior
{
	
	
	private enum WraithBuildOrder
	{
		EQUIPPING,
		DETERMINE_SPAWN,
		ADVANCE
	}
	
	WraithBuildOrder obj = WraithBuildOrder.EQUIPPING;
	
	int spawn;
	int rally; // index in Direction.values()
	RobotInfo enemyInfo;
	MapLocation destination;
	
	int num = -1;
	int northEdge = -1;
	int eastEdge = -1;
	int southEdge = -1;
	int westEdge = -1;
	
	boolean hasBlaster;
	boolean hasRadar;
	
	int numBounces = 0;
	int stepsOffDir = 0;
	
	public WraithBehavior(RobotPlayer player)
	{
		super(player);
	}


	public void run() throws Exception
	{
		
		switch (obj)
		{
			
			case EQUIPPING:
				
				Utility.setIndicator(myPlayer, 1, "EQUIPPING");
				hasBlaster = false;
				hasRadar = false;
				for ( int i = myPlayer.myRC.components().length; --i >= 0;)
				{
					ComponentController c = myPlayer.myRC.components()[i];
					if ( c.type() == ComponentType.BLASTER )
						hasBlaster = true;
					if ( c.type() == ComponentType.RADAR )
						hasRadar = true;
				}
				if ( hasBlaster && hasRadar && num != -1 )
					obj = WraithBuildOrder.DETERMINE_SPAWN;
				return;
				
			case DETERMINE_SPAWN:
				
				Utility.setIndicator(myPlayer, 1, "DETERMINE_SPAWN");
				
				while ( westEdge == -1 || northEdge == -1 || eastEdge == -1 || southEdge == -1 )
				{
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myRC.getLocation().add(Direction.NORTH, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.NORTH, 6)) == TerrainTile.OFF_MAP )
							northEdge = 1;
						else
							northEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myRC.getLocation().add(Direction.EAST, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.EAST, 6)) == TerrainTile.OFF_MAP )
							eastEdge = 1;
						else
							eastEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myRC.getLocation().add(Direction.SOUTH, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.SOUTH, 6)) == TerrainTile.OFF_MAP )
							southEdge = 1;
						else
							southEdge = 0;
					}
					if ( myPlayer.mySensor.canSenseSquare(myPlayer.myRC.getLocation().add(Direction.WEST, 6)) )
					{
						if ( myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.WEST, 6)) == TerrainTile.OFF_MAP )
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
				obj = WraithBuildOrder.ADVANCE;
				return;
	        	
			case ADVANCE:	
				
				Utility.setIndicator(myPlayer, 1, "ADVANCE");
	        	
				// Off map rerally code
        		if ( rally % 2 == 0 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[rally],6)) == TerrainTile.OFF_MAP )
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
	        	}
        		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally-1)%8],6)) == TerrainTile.OFF_MAP )
	        	{
        			// we have reached the closest side to the enemy corner, rerally to corner
	        		if ( num % 2 == 0 )
	        			rally = (rally + 1) % 8;
	        		else
	        			rally = (rally + 7) % 8;
	        	}
        		else if ( rally % 2 == 1 && myPlayer.myRC.senseTerrainTile(myPlayer.myRC.getLocation().add(Direction.values()[(rally+1)%8],6)) == TerrainTile.OFF_MAP )
	        	{
        			// we have reached the closest side to the enemy corner, rerally to corner
	        		if ( num % 2 == 0 )
	        			rally = (rally + 7) % 8;
	        		else
	        			rally = (rally + 1) % 8;
	        		Utility.setIndicator(myPlayer, 2, "Off map found, rerallying " + Direction.values()[rally].toString() + ".");
	        		numBounces++;
	        	}
	        	
	        	
	        	enemyInfo = Utility.attackEnemies(myPlayer);
	        	//Found an enemy
	        	if ( enemyInfo != null && !myPlayer.myRC.getLocation().equals(enemyInfo.location) )
	        	{
	        		if ( enemyInfo.on && enemyInfo.chassis == Chassis.MEDIUM || enemyInfo.chassis == Chassis.HEAVY )
	        		{
	        			Utility.setIndicator(myPlayer, 2, "Aw damn, those are some big guns! AHHHHH!");
	        			rally = (rally + 4)%8;
        				if ( !myPlayer.myMotor.isActive() )
	        				myPlayer.myMotor.setDirection(Direction.values()[rally]);
	        		}
	        		else if ( myPlayer.myRC.getLocation().distanceSquaredTo(enemyInfo.location) <= 16 )
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy in range, backing up!");
        				if ( !myPlayer.myMotor.isActive() )
        				{
	        				if ( myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection().opposite()).distanceSquaredTo(enemyInfo.location) > myPlayer.myRC.getLocation().distanceSquaredTo(enemyInfo.location) && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection().opposite()))
	        					myPlayer.myMotor.moveBackward();
	        				else
	        					myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyInfo.location));
        				}
        			}
        			else if ( enemyInfo.on && enemyInfo.chassis != Chassis.BUILDING && myPlayer.myRC.getLocation().distanceSquaredTo(enemyInfo.location) <= 26 )
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy detected, halting.");
        				if ( !myPlayer.myMotor.isActive() )
        				{
        					if ( myPlayer.myRC.getDirection() != myPlayer.myRC.getLocation().directionTo(enemyInfo.location) )
        						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyInfo.location));
        					else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) && enemyInfo.location.directionTo(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection())) != enemyInfo.direction && enemyInfo.location.directionTo(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection())) != enemyInfo.direction.rotateLeft() && enemyInfo.location.directionTo(myPlayer.myRC.getLocation().add(myPlayer.myRC.getDirection())) != enemyInfo.direction.rotateRight() )
        						myPlayer.myMotor.moveForward();
        				}
        			}
        			else
        			{
        				Utility.setIndicator(myPlayer, 2, "Enemy detected, engaging.");
        				if ( !myPlayer.myMotor.isActive() )
        				{
        					if ( myPlayer.myRC.getDirection() == myPlayer.myRC.getLocation().directionTo(enemyInfo.location) && myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
        						myPlayer.myMotor.moveForward();
        					else if ( myPlayer.myRC.getDirection() != myPlayer.myRC.getLocation().directionTo(enemyInfo.location) )
        						myPlayer.myMotor.setDirection(myPlayer.myRC.getLocation().directionTo(enemyInfo.location));
        				}
        			}
	        	}
	        	//There is no enemy
	        	else
	        	{
	        		Utility.setIndicator(myPlayer, 2, "No enemies nearby, advancing.");
		        	if ( !myPlayer.myMotor.isActive() )
		        	{
		        		if ( myPlayer.myRC.getDirection() == Direction.values()[rally] )
		        		{
		        			if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
		        			{
		        				myPlayer.myMotor.moveForward();
		        				stepsOffDir = 0;
		        			}
		        			else
		        			{
		        				if ( num % 2 == 0 )
		        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
		        				if ( num % 2 == 1 )
		        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
		        				stepsOffDir++;
		        			}
		        		}
		        		else
		        		{
		        			if ( stepsOffDir < 3 )
		        			{
		        				if ( myPlayer.myMotor.canMove(Direction.values()[rally]) )
		        				{
		        					myPlayer.myMotor.setDirection(Direction.values()[rally]);
			        				stepsOffDir = 0;
		        				}
		        				else if ( myPlayer.myMotor.canMove(myPlayer.myRC.getDirection()) )
			        			{
			        				myPlayer.myMotor.moveForward();
			        				stepsOffDir++;
			        			}
			        			else
			        			{
			        				if ( num % 2 == 0 )
			        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateRight());
			        				if ( num % 2 == 1 )
			        					myPlayer.myMotor.setDirection(myPlayer.myRC.getDirection().rotateLeft());
			        				stepsOffDir++;
			        			}
		        			}
		        			else
		        			{
		        				myPlayer.myMotor.setDirection(Direction.values()[rally]);
		        				stepsOffDir = 0;
		        			}
		        		}
		        	}
	        	}
	        	return;
	        	
		}
	}
	
	
	
	public String toString()
	{
		return "WraithBehavior";
	}


	@Override
	public void newComponentCallback(ComponentController[] components)
	{
		
	}


	@Override
	public void newMessageCallback(MsgType t, Message msg)
	{
		if ( t == MsgType.MSG_SEND_NUM )
		{
			if ( num == -1 )
			{
				num = msg.ints[Messenger.firstData+1];
				Utility.setIndicator(myPlayer, 0, "I'm wraith " + Integer.toString(num) + "!");
			}
		}
	}
	
	public void onWakeupCallback(int lastActiveRound)
	{
		
	}
	
	public void onDamageCallback(double damageTaken)
	{
		
	}
	
}
