package finalbot; import battlecode.common.*;
/**
 *<pre>
 *                             _________________________
 *                           /                         \ 
 *  ========================[  C A R T O G R A P H E R  ]===========================    
 *                           \_________________________/
 * 
 *                                         __                                                  
 *                                      ,;'--`:.                                               
 *                                     //      \\                                              
 *                                    ((   __   ))                                             
 *                                     \\,'  `.//                                              
 *                                      ``.__,''                                               
 *                                      __(  )__                                               
 *                                  _.-'_......_`-._                                           
 *                                ,'.;::;''''''::`-.`.                                         
 *                               / /:;''    N   `':,\ \                                        
 *                              / /:/ . ,''  ``. , \:\ \                                       
 *                             : :::   ;.,-  -.;'   ::: :                                      
 *                             | |:|  : (':,.;') :  |:| |                                      
 *                             | |:| W:    `'    ;E |:| | 
 *                             : :::   \        /   ;:; ;                                      
 *                              \ \:\   `..__..'   /:/ /                                       
 *                               \ \::.     S    .::/ /                                        
 *                                 `.::::._  _..:::;,'                                         
 *                                   `:.::.`''::.:'                                        
 *                              _..--''.--     --.`--.._                                       
 *                             `._`--...________...--'_,'                                      
 *                                ``--...______...--''            
 * 
 * 
 *   __
 *	/  \ Notes ___________________________________________________________ 
 *  \__/
 *  
 *  	The cartographer class allows for map center estimation based on
 *  	the number of map edges you've seen, along with a confidence factor.
 *  
 *      You start off by thinking the center of the map is you.
 *      Then, as you explore the world, you learn that it's not just about you.
 * 
 *      Deep.....
 * 
 *</pre>
 * 
 * @author Cory
 *
 */
public class Cartographer {
	
	//Define locations I've visited
	private MapStoreBoolean hasVisited;
	
	
	
	//Define constants here for fast access
	private final RobotPlayer myPlayer;
	private final RobotController myRC;
	private ComponentType sensorType;
	
	//Seen Flags
	private boolean seenNorth;
	private boolean seenSouth;
	private boolean seenEast;
	private boolean seenWest;
	
	//Coordinates
	private int coordNorth;
	private int coordSouth;
	private int coordWest;
	private int coordEast;
	
	private int centerX;
	private int centerY;
	
	
	
	/**
	 * Instantiates a new Cartographer system.
	 * @param player
	 */
	public Cartographer(RobotPlayer player) {
		//Initialize my visited squares
		hasVisited = new MapStoreBoolean();
		
		//Initialize the main components
		myPlayer 	= player;
		myRC 		= player.myRC;
		
		//Initialize the coordinates and whether things have been seen
		seenNorth = seenSouth = seenEast = seenWest = false;
		
		int x = myRC.getLocation().x;
		int y = myRC.getLocation().y;
		coordNorth 	= y - GameConstants.MAP_MAX_HEIGHT;
		coordSouth 	= y + GameConstants.MAP_MAX_HEIGHT;
		coordWest 	= x - GameConstants.MAP_MAX_WIDTH;
		coordEast 	= x + GameConstants.MAP_MAX_WIDTH;

		centerY = y;
		centerX = x;
		
	
	}
	
	
	
	/**
	 * This function sets the current sensor type.
	 * The private flag sensorType is set depending on the controller type.
	 * @param c
	 */
	public void setSensor(ComponentController c) {
		sensorType = c.type();
	}
	
	
	
	/** 
	 * Internal function recalculates the map center any time new data has been found.
	 */
	private void updateMapCenter() {

		//If we've seen one edge but not the other, then we can update our estimate of the other edge.
		if(seenEast && !seenWest) {
			coordWest =  coordEast - GameConstants.MAP_MAX_WIDTH;
		}

		if(seenNorth && !seenSouth) {
			coordSouth = coordNorth + GameConstants.MAP_MAX_HEIGHT;
		}
		
		if(seenWest && !seenEast) {
			coordEast = coordWest + GameConstants.MAP_MAX_WIDTH;
		}
		
		if(seenSouth && !seenNorth) {
			coordNorth = coordSouth - GameConstants.MAP_MAX_HEIGHT;
		}
		
		centerY = (coordSouth+coordNorth)/2;
		centerX = (coordEast+coordWest)/2;
	}
	
	
	
	
	/**
	 * This function returns the current estimated center of the map.
	 * @return
	 */
	public MapLocation getMapCenter() {
		return new MapLocation(centerX,centerY);
	}
	
	/**
	 * This function returns the number of sides the robot has seen
	 * @author JVen
	 * @return The number of sides the robot has seen
	 */
	
	public int getConfidence()
	{
		int ans = 0;
		if ( seenNorth )
			ans++;
		if ( seenEast )
			ans++;
		if ( seenSouth )
			ans++;
		if ( seenWest )
			ans++;
		return ans;
	}
	
	/**
	 * This function will eventually return the best direction to explore in.
	 * @return
	 */
	public Direction unexploredDirection() {
		//TODO: Fill this in.
		return null;
	}
	
	
	
	

	
	/**
	 * Sense the terrain and enter the information into the mapping engine.
	 */
	public void runSensor() {
			MapLocation myLoc = myRC.getLocation();
			Direction myDir = myRC.getDirection();
			
			hasVisited.set(myLoc);
			
			int dx = myDir.dx;
			int dy = myDir.dy;
			
			switch(sensorType) {
			
			//Cases for the radar
			case RADAR:
				if(dx>=0) { //East
					if(!seenEast){
						if(myRC.senseTerrainTile(myLoc.add(Direction.EAST, 6)) == TerrainTile.OFF_MAP) {
							seenEast = true;
							coordEast = myLoc.x+6;
							updateMapCenter();
						}
					}
				}
				if(dx<=0) { //West
					if(!seenWest){
						if(myRC.senseTerrainTile(myLoc.add(Direction.WEST, 6)) == TerrainTile.OFF_MAP) {
							seenWest = true;
							coordWest = myLoc.x-6;
							updateMapCenter();
						}
					}
				}
				if(dy<=0) { //North
					if(!seenNorth){
						if(myRC.senseTerrainTile(myLoc.add(Direction.NORTH, 6)) == TerrainTile.OFF_MAP) {
							seenNorth = true;
							coordNorth = myLoc.y-6;
							updateMapCenter();
						}
					}
				}
				if(dy>=0) { //South
					if(!seenSouth){
						if(myRC.senseTerrainTile(myLoc.add(Direction.SOUTH, 6)) == TerrainTile.OFF_MAP) {
							seenSouth = true;
							coordSouth = myLoc.y+6;
							updateMapCenter();
						}
					}
				}
				return;
			
			case SIGHT:
				if(dx>0) { //East
					if(!seenEast){
						if(myRC.senseTerrainTile(myLoc.add(Direction.EAST, 3)) == TerrainTile.OFF_MAP) {
							seenEast = true;
							coordEast = myLoc.x+6;
							updateMapCenter();
						}
					}
				}
				if(dx<0) { //West
					if(!seenWest){
						if(myRC.senseTerrainTile(myLoc.add(Direction.WEST, 3)) == TerrainTile.OFF_MAP) {
							seenWest = true;
							coordWest = myLoc.x-6;
							updateMapCenter();
						}
					}
				}
				if(dy<0) { //North
					if(!seenNorth){
						if(myRC.senseTerrainTile(myLoc.add(Direction.NORTH, 3)) == TerrainTile.OFF_MAP) {
							seenNorth = true;
							coordNorth = myLoc.y-6;
							updateMapCenter();
						}
					}
				}
				if(dy>0) { //South
					if(!seenSouth){
						if(myRC.senseTerrainTile(myLoc.add(Direction.SOUTH, 3)) == TerrainTile.OFF_MAP) {
							seenSouth = true;
							coordSouth = myLoc.y+6;
							updateMapCenter();
						}
					}
				}
				return;
			case SATELLITE:
				if(!seenNorth) {
					int dist=scanFirstVoid(Direction.NORTH,10);
					if(dist>=0) {
						seenNorth = true;
						coordNorth = myLoc.y-dist;
						updateMapCenter();
					}	
				}
				if(!seenSouth) {
					int dist=scanFirstVoid(Direction.SOUTH,10);
					if(dist>=0) {
						seenSouth = true;
						coordSouth = myLoc.y+dist;
						updateMapCenter();
					}	
				}
				if(!seenWest)  {
					int dist=scanFirstVoid(Direction.WEST,10);
					if(dist>=0) {
						seenWest = true;
						coordWest = myLoc.x-dist;
						updateMapCenter();
					}	
				}
				if(!seenEast)  {
					int dist=scanFirstVoid(Direction.EAST,10);
					if(dist>=0) {
						seenEast = true;
						coordEast = myLoc.x+dist;
						updateMapCenter();
					}		
				}
	
				
			case TELESCOPE:
				return;
			case BUILDING_SENSOR:
				return;
					
			default: 
				Utility.printMsg(myPlayer,"Cartographer Error: Wrong Sensor Type");
				return;
			}
	}
	
	
	public int scanFirstVoid(Direction dir, int dist) {
		if(myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(dir, dist)) == TerrainTile.OFF_MAP) {
			while(--dist>=0) {
				if(myPlayer.myRC.senseTerrainTile(myPlayer.myLoc.add(dir, dist)) == TerrainTile.LAND) {
					return dist;
				}
			}
		}
		
		return -1;
	}
	
	

}
