package beambot;
import battlecode.common.*;
import java.util.*;

public class BugNavigation
{
	
	private final RobotPlayer myPlayer;
	private final int m = GameConstants.MAP_MAX_WIDTH;
	private final int n = GameConstants.MAP_MAX_HEIGHT;
	private int[][] visited = new int[m][n];
	private final PriorityQueue<Integer> dirs = new PriorityQueue<Integer>();
	private MapLocation lastDest;
	
	public BugNavigation(RobotPlayer player)
	{
		myPlayer = player;
	}

	private int visited(MapLocation loc)
	{
		return visited[(loc.x) % m][(loc.y)% n];
	}
	
	public Direction bugTo(MapLocation dest)
	{
		
		// Set some variables
		MapLocation loc = myPlayer.myRC.getLocation();
		Direction dir = myPlayer.myRC.getDirection();
		Direction desiredDir = loc.directionTo(dest);
		Direction d;
		
		// If we have a new destination, reset our visited table
		if ( dest != lastDest )
		{
			for ( int i = m-1 ; i >= 0 ; i-- )
				for ( int j = n-1 ; j >= 0 ; j-- )
					visited[i][j] = 0;
		}
		
		// Visit the current location
		visited[(loc.x) % m][(loc.y)% n]++;
		
		// Destination reached!
		if ( loc == dest )
			return Direction.OMNI;
		
		// For each adjacent square, store the direction from loc and its distance from dest
		// Note that we're storing a tuple of ints as one int for simplicity
		// We also don't include non-traversable squares or already visited squares
		dirs.clear();
		for ( int i = 7 ; i >= 0 ; i-- )
		{
			d = Direction.values()[i];
			if ( myPlayer.myMotor.canMove(d) && visited(loc.add(d)) == 0 )
				dirs.add(10*(loc.add(d).distanceSquaredTo(dest)) + i);
		}
		
		return null;
		
		
		
	}
}