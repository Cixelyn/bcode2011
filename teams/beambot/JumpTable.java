package beambot;

import battlecode.common.*;



/**
 * This class does the calculations necessary to figure out a good jump location
 * Table is fully self sufficient and can be used in doing calculations.
 * @author Cory
 *
 */
public final class JumpTable {
	
	private final int dx; private final int dy;
	private final boolean isOmni;
	private final boolean isDiagonal;
	private final boolean isVertical;
	private final int currX;
	private final int currY;
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
		isOmni = (dir==Direction.OMNI);
		
		currX = myLoc.x;
		currY = myLoc.y;
		
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
		
		if(isOmni)
		{
			int xC = 0, yC = 0;
			
			switch(idx)
			{
				case 0:
					xC = 0; yC = -1; break;
				case 1:
					xC = 1; yC = -1; break;
				case 2:
					xC = 1; yC = 0; break;
				case 3:
					xC = 1; yC = 1; break;
				case 4:
					xC = 0; yC = 1; break;
				case 5:
					xC = -1; yC = 1; break;
				case 6:
					xC = -1; yC = 0; break;
				case 7:
					xC = -1; yC = -1; break;
				
				
				default:
					return null;
			}
			
			return new MapLocation(currX+xC*dx, currY+yC*dy);
		}
		
		//TODO add a modulus operation that allows for the jump tables to changed based on modulus operations
		
				
		/////////////////////////////////////////////////////////////////////
		///////////////////////DIAGONALS/////////////////////////////////////
		if(isDiagonal)
		{
			
			switch(idx)
			{
				case 0:
					//xC = 3; yC = 2;
					return new MapLocation(currX + 3*dx, currY + 2*dy);
				case 1:
					//xC = 2; yC = 3;
					return new MapLocation(currX + 2*dx, currY + 3*dy);
				case 2:
					//xC = 1; yC = 3;
					return new MapLocation(currX +   dx, currY + 3*dy);
				case 3:
					//xC = 3; yC = 1;
					return new MapLocation(currX + 3*dx, currY +   dy);
				case 4:
					//xC = 2; yC = 2;
					return new MapLocation(currX + 2*dx, currY + 2*dy);
				case 5:
					//xC = 4; yC = 0;
					return new MapLocation(currX + 4*dx, currY       );
				case 6:
					//xC = 0; yC = 4;
					return new MapLocation(currX       , currY + 4*dy);
				case 7:
					//xC = 2; yC = 1;
					return new MapLocation(currX + 2*dx, currY +   dy);
				case 8:
					//xC = 1; yC = 2;
					return new MapLocation(currX+  dx, currY+2*dy);
				case 9:
					//xC = 0; yC = 3;
					return new MapLocation(currX     , currY+3*dy);
				case 10:
					//xC = 3; yC = 0;
					return new MapLocation(currX+3*dx, currY     );
				case 11:
					//xC = 0; yC = 2;
					return new MapLocation(currX     , currY+2*dy);
				case 12:
					//xC = 2; yC = 0;
					return new MapLocation(currX+2*dx, currY     );
				case 13:
					//xC = -1; yC = 3;
					return new MapLocation(currX+ -1*dx, currY+3*dy);
				case 14:
					//xC = 3; yC = -1;
					return new MapLocation(currX+ 3*dx, currY+ -1*dy);
				case 15:
					//xC = 1; yC = 1;
					return new MapLocation(currX+   dx, currY+   dy);
				case 16:
					//xC = -1; yC = 2;
					return new MapLocation(currX+ -1*dx, currY + 2*dy);
				case 17:
					//xC = 2; yC = -1;
					return new MapLocation(currX+2*dx, currY+ -1*dy);
				case 18:
					//xC = 0; yC = 1;
					return new MapLocation(currX     , currY+dy);
				case 19:
					//xC = 1; yC = 0;
					return new MapLocation(currX + dx, currY       );
				case 20:
					//xC = -2; yC = 3;
					return new MapLocation(currX + -2*dx, currY + 3*dy);
				case 21:
					//xC = 3; yC = -2;
					return new MapLocation(currX+ 3 * dx, currY + -2*dy);
				case 22:
					//xC = -2; yC = 2;
					return new MapLocation(currX + -2*dx, currY + 2*dy);
				case 23:
					//xC = 2; yC = -2;
					return new MapLocation(currX +  2*dx, currY + -2*dy);
				case 24:
					//xC = -1; yC = 1;
					return new MapLocation(currX + -1*dx, currY  +    dy);
				case 25:
					//xC = 1; yC = -1;
					return new MapLocation(currX + dx, currY   + -1*dy);
				default:
					return null;
			}
		}
		
		
		/////////////////////////////////////////////////////////////////////
		//////////////////////////ORTHOGONALS////////////////////////////////
		
		else
		{
			int dirVec = 0, zeroVec = 0;
			
			switch(idx)
			{
			
				case 0:
					dirVec=4; zeroVec=0; break;
				case 1:
					dirVec=3; zeroVec=-1; break;
				case 2:
					dirVec=3; zeroVec=1; break;
				case 3:
					dirVec=3; zeroVec=0; break;
				case 4:
					dirVec=3; zeroVec=-2; break;
				case 5:
					dirVec=3; zeroVec=2; break;
				case 6:
					dirVec=2; zeroVec=-1; break;
				case 7:
					dirVec=2; zeroVec=1; break;
				case 8:
					dirVec=2; zeroVec=-2; break;
				case 9:
					dirVec=2; zeroVec=2; break;
				case 10:
					dirVec=2; zeroVec=0; break;
				case 11:
					dirVec=2; zeroVec=-3; break;
				case 12:
					dirVec=2; zeroVec=3; break;
				case 13:
					dirVec=1; zeroVec=-1; break;
				case 14:
					dirVec=1; zeroVec=1; break;
				case 15:
					dirVec=1; zeroVec=0; break;
				case 16:
					dirVec=1; zeroVec=-2; break;
				case 17:
					dirVec=1; zeroVec=2; break;
				case 18:
					dirVec=1; zeroVec=-3; break;
				case 19:
					dirVec=1; zeroVec=3; break;
				case 20:
					dirVec=0; zeroVec=-3; break;
				case 21:
					dirVec=0; zeroVec=3; break;
				case 22:
					dirVec=0; zeroVec=-4; break;
				case 23:
					dirVec=0; zeroVec=4; break;
				case 24:
					dirVec=0; zeroVec=-2; break;
				case 25:
					dirVec=0; zeroVec=2; break;
				case 26:
					dirVec=0; zeroVec=1; break;
				case 27:
					dirVec=0; zeroVec=-1; break;
				default:
					return null;
			}
			
			if ( isVertical )
				return new MapLocation(currX+zeroVec, currY+dirVec*dy);
			else
				return new MapLocation(currX+dirVec*dx, currY+zeroVec);	
		}
	}
	
}