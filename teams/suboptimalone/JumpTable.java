package suboptimalone;

import battlecode.common.*;




/**
 * This class constructs JumpTables
 * Table is fully self sufficient and can be used in doing calculations.
 * @author Cory
 *
 */
public final class JumpTable {

	private final int dx; private final int dy;
	private final boolean isDiagonal;
	private final boolean isVertical;
	private final MapLocation myLoc;
	
	
	private int idx;	//This is the current index we're checking
	
	/**
	 * Instantiates a new JumpTable that calculates based on directions
	 * @param loc
	 * @param dir
	 */
	public JumpTable(MapLocation loc, Direction dir) {
		myLoc=loc;
		dx = dir.dx;
		dy = dir.dy;
		isDiagonal = dir.isDiagonal();
		isVertical = (dy!=0);
		
		//initialize our index
		idx = -1;
	}
	
	
	/**
	 * This function returns the next best location to jump to.
	 * If there are no more entries remaining in the table, the function returns null
	 * @return MapLocation to jump to
	 */
	public MapLocation nextLoc() {
	
		//increment our index
		idx++;
		
		
		
		//add a modulus operation that allows for the jump tables to changed based on modulus operations
		
		
		if(isDiagonal) {
			switch(idx) {
			case 0:
				return new MapLocation(myLoc.x + dx*3, myLoc.y + dy*2);
			case 1:
				return new MapLocation(myLoc.x + dx*2, myLoc.y + dy*3);
			case 2:
				return new MapLocation(myLoc.x + dx  , myLoc.y + dy*3);
			case 3:
				return new MapLocation(myLoc.x + dx*3, myLoc.y + dy  );
			case 4:
				return new MapLocation(myLoc.x + dx*2, myLoc.y + dx*2);
			case 5:
				return new MapLocation(myLoc.x + dx*4, myLoc.y        );
			case 6:
				return new MapLocation(myLoc.x       , myLoc.y + dy*4 );
			case 7:
				return new MapLocation(myLoc.x + dx*2, myLoc.y + dy*2 );
			case 8:
				return new MapLocation(myLoc.x + dx*2, myLoc.y + dy   );
			case 9:
				return new MapLocation(myLoc.x + dx  , myLoc.y + dy*2  );
			case 10:
				return new MapLocation(myLoc.x       , myLoc.y + dy*3 );
			case 11:
				return new MapLocation(myLoc.x + dx*3, myLoc.y        );
				
			//////// SKIPPING QUITE A FEW!!!
			case 12:
				return new MapLocation(myLoc.x + dx*-2, myLoc.y+dy*2 );
			case 13:
				return new MapLocation(myLoc.x + dx* 2, myLoc.y+dy*-2 );
				
				
			default:
				return null;
			}
		} else { //Not Diagonal
			
			if(isVertical) {
				switch(idx) {
				case 0:
					return new MapLocation(myLoc.x     , myLoc.y+4*dy);
				case 1:
					return new MapLocation(myLoc.x + 1 , myLoc.y+3*dy);
				case 2:
					return new MapLocation(myLoc.x - 1 , myLoc.y+3*dy);
				case 3:
					return new MapLocation(myLoc.x + 2,  myLoc.y+3*dy);
				case 4:
					return new MapLocation(myLoc.x - 2,  myLoc.y+3*dy);
				case 5:
					return new MapLocation(myLoc.x    ,  myLoc.y+3*dy);
					
				//End of the range 3 squares
				case 6:
					return new MapLocation(myLoc.x + 1,  myLoc.y+2*dy);
				case 7:
					return new MapLocation(myLoc.x - 1,  myLoc.y+2*dy);
				case 8:
					return new MapLocation(myLoc.x    ,  myLoc.y+2*dy);
				
				
				//SKIPPING QUITE A FEW
				case 9:
					return new MapLocation(myLoc.x + 4, myLoc.y      );
				case 10:
					return new MapLocation(myLoc.x - 4, myLoc.y      );
				case 11:
					return new MapLocation(myLoc.x + 3, myLoc.y      );
				case 12:
					return new MapLocation(myLoc.x - 3, myLoc.y      );
					
					
				
				
				default:
					return null;
				}
			} else {
				switch(idx) {
				case 0:
					return new MapLocation(myLoc.x+4*dx, myLoc.y    );
				case 1:
					return new MapLocation(myLoc.x+3*dx, myLoc.y + 1);
				case 2:
					return new MapLocation(myLoc.x+3*dx, myLoc.y - 1);
				case 3:
					return new MapLocation(myLoc.x+3*dx, myLoc.y + 2);
				case 4:
					return new MapLocation(myLoc.x+3*dx, myLoc.y - 2);
				case 5:
					return new MapLocation(myLoc.x+3*dx, myLoc.y    );
				case 6:
					return new MapLocation(myLoc.x+2*dx, myLoc.y + 1);
				case 7:
					return new MapLocation(myLoc.x+2*dx, myLoc.y - 1);
				case 8:
					return new MapLocation(myLoc.x+2*dx, myLoc.y    );

				
				//SKIPPING QUITE A FEW
				case 9:
					return new MapLocation(myLoc.x    , myLoc.y + 4 );
				case 10:
					return new MapLocation(myLoc.x    , myLoc.y - 4 );
				case 11:
					return new MapLocation(myLoc.x    , myLoc.y + 3 );
				case 12:
					return new MapLocation(myLoc.x    , myLoc.y - 3 );
					
				
				
				
				default: 
					return null;
				}	
			}
		}

		
	}
	
}