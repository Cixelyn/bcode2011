package team068;

import battlecode.common.*;



/**
 * This class does the calculations necessary to figure out a good jump location
 * Table is fully self sufficient and can be used in doing calculations.
 * @author Cory
 *
 */
public final class JumpTable {
	
	private final int dx; private final int dy;
	private final boolean isDiagonal;
	private final boolean isVertical;
	private final MapLocation myLoc;

	private int idx; //This is the current index we're checking
	
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
		
		
		/////////////////////////////////////////////////////////////////////
		///////////////////////DIAGONALS/////////////////////////////////////
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
			
			
		/////////////////////////////////////////////////////////////////////
		//////////////////////////ORTHOGONALS////////////////////////////////
		} else {
			
			int dirVec; int zeroVec;
			
			switch(idx) {
			
			//LETS DEAL WITH THE FAR ONES FIRST
			case 0:
				dirVec=4; zeroVec=0; break;
			case 1:
				dirVec=3; zeroVec=+1; break;
			case 2:
				dirVec=3; zeroVec=-1; break;
			case 3:
				dirVec=3; zeroVec=+2; break;
			case 4:
				dirVec=3; zeroVec=-2; break;
			case 5:
				dirVec=3; zeroVec=0; break;
			case 6:
				dirVec=2; zeroVec=+1; break;
			case 7:
				dirVec=2; zeroVec=-1; break;
			case 8:
				dirVec=2; zeroVec=0; break;
			case 9:
				dirVec=0; zeroVec=+4; break;
			case 10:
				dirVec=0; zeroVec=-4; break;
			case 11:
				dirVec=0; zeroVec=+3; break;
			case 12:
				dirVec=0; zeroVec=-3; break;
			default:
				return null;
			}
			
			if(isVertical) {
				return new MapLocation(myLoc.x+zeroVec, myLoc.y+dirVec*dy);
			} else{
				return new MapLocation(myLoc.x+dirVec*dx, myLoc.y+zeroVec);
			}
		}
		
	}
	
}