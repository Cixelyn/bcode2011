package suboptimal;

import battlecode.common.*;




/**
 * This class constructs JumpTables
 * @author Cory
 *
 */
public class JumpTable {

	private int dx; private int dy;
	private boolean isDiagonal;
	private boolean isVertical;
	private MapLocation myLoc;
	
	
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
				return new MapLocation(myLoc.x + dx*3, myLoc.y        );
			case 6:
				return new MapLocation(myLoc.x       , myLoc.y + dx*3 );
			case 7:
				return new MapLocation(myLoc.x + dx  , myLoc.y + dy*2 );
			case 8:
				return new MapLocation(myLoc.x + dx*2, myLoc.y + dx*2 );
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
				case 6:
					return new MapLocation(myLoc.x + 1,  myLoc.y+2*dy);
				case 7:
					return new MapLocation(myLoc.x - 1,  myLoc.y+2*dy);
				case 8:
					return new MapLocation(myLoc.x    ,  myLoc.y+2*dy);
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
				default: 
					return null;
				}	
			}
		}

		
	}
	
}