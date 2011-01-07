package costax2;
import battlecode.common.*;
import java.util.*;

public class Navigation2
{
	
	private final RobotPlayer player;
	private final RobotController myRC;
	private final SensorController sensor;
	
	private CostParent[][] memory;
	
	private MapLocation goal;
	private int gx;
	private int gy;
	
	private int m = 2*40 - 1;
	private int n = 2*30 - 1;
	//private int m = 2*GameConstants.MAP_MAX_WIDTH - 1;
	//private int n = 2*GameConstants.MAP_MAX_HEIGHT - 1;
	private final double INFINITY = m*n+1;
	
	private final PriorityQueue<MapLocation> open = new PriorityQueue<MapLocation>(m*n , new locComparator());
	private final HashSet<MapLocation> closed = new HashSet<MapLocation>(m*n);
	
	public Navigation2(RobotPlayer player, MapLocation goal_)
	{
		this.player = player;
		myRC = player.myRC;
		sensor = player.mySensor;
		goal = goal_;
		gx = x(goal);
		gy = y(goal);
		memory = new CostParent[m][n];
		
		for (int i = m-1 ; i >= 0; i--)
			for (int j = n-1 ; j >= 0 ; j--)
				memory[i][j] = new CostParent(h(i,j,gx,gy));
	}
	
	public class CostParent
	{
		public double estCost; // estimated cost from start to end through this loc
		public double actCost; // actual min cost from start to this loc
		public MapLocation parent = null;
		
		public CostParent (double estCost_)
		{
			estCost = estCost_;
		}
	}
	
	public class locComparator implements Comparator<MapLocation>
	{
		public int compare(MapLocation a, MapLocation b)
		{
			double costA = memory[x(a)][y(a)].estCost + memory[x(a)][y(a)].actCost;
			double costB = memory[x(b)][y(b)].estCost + memory[x(b)][y(b)].actCost;
			if (costA < costB)
				return -1; // may have to switch -1 and 1 :]
			if (costA > costB)
				return 1;
			else
				return 0;
		}
	}
	
	public int x(MapLocation loc)
	{
		return (m/2)+(loc.x - myRC.getLocation().x);
	}
	
	public int y(MapLocation loc)
	{
		return (n/2)+(loc.y - myRC.getLocation().y);
	}
	
	public double h(MapLocation a, MapLocation b)
	{
		return Math.sqrt(a.distanceSquaredTo(b));
	}
	
	public double h(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	
	public Direction getDir() throws Exception
	{
		
		MapLocation start = myRC.getLocation();
		MapLocation node;
		//MapLocation[] neighbors = new MapLocation[8];
		MapLocation[] neighbors = new MapLocation[4];
		MapLocation neighbor;
		double cost;
		
		for (int i = x(start)-3 ; i <= 3; i++)
			for (int j = y(start)-3 ; j <= 3 ; j++)
			{
				node = start.add(i-x(start),j-y(start)); 
				if (sensor.canSenseSquare(node) && (myRC.senseTerrainTile(node)!=TerrainTile.LAND || sensor.senseObjectAtLocation(node, RobotLevel.ON_GROUND) != null))
					memory[i][j].estCost = INFINITY;
			}
		
		memory[x(start)][y(start)].actCost = 0;
		open.add(start);
		
		while (!open.isEmpty())
		{
			node = open.poll();
			if (node.equals(goal))
				return getAns();
			closed.add(node);
			
			if (x(node) - 1 >= 0 && x(node) - 1 < m)
				neighbors[0] = node.add(-1, 0);
			else
				neighbors[0] = null;
			
			if (x(node) + 1 >= 0 && x(node) + 1 < m)
				neighbors[1] = node.add(1, 0);
			else
				neighbors[1] = null;
			
			if (y(node) - 1 >= 0 && y(node) - 1 < n)
				neighbors[2] = node.add(0, -1);
			else
				neighbors[2] = null;
			
			if (y(node) + 1 >= 0 && y(node) + 1 < n)
				neighbors[3] = node.add(0, 1);
			else
				neighbors[3] = null;
			
			/*if (x(node) - 1 >= 0 && x(node) - 1 < m && y(node) - 1 >= 0 && y(node) - 1 < n)
				neighbors[4] = node.add(-1, -1);
			else
				neighbors[4] = null;
			
			if (x(node) + 1 >= 0 && x(node) + 1 < m && y(node) - 1 >= 0 && y(node) - 1 < n)
				neighbors[5] = node.add(1, -1);
			else
				neighbors[5] = null;
			
			if (x(node) + 1 >= 0 && x(node) + 1 < m && y(node) - 1 >= 0 && y(node) - 1 < n)
				neighbors[6] = node.add(1, -1);
			else
				neighbors[6] = null;
			
			if (x(node) + 1 >= 0 && x(node) + 1 < m && y(node) + 1 >= 0 && y(node) + 1 < n)
				neighbors[7] = node.add(1, 1);
			else
				neighbors[7] = null;*/
			
			
			for(int k = 3; k >= 0; k--)
			{
				neighbor = neighbors[k];
				if (neighbor != null && !closed.contains(neighbor))
				{
					cost = memory[x(node)][y(node)].actCost + Math.sqrt(node.distanceSquaredTo(neighbor));
					if (!open.contains(neighbor))
					{
						open.add(neighbor);
						memory[x(neighbor)][y(neighbor)].parent = node;
						memory[x(neighbor)][y(neighbor)].actCost = cost;
					}
					else if (cost < memory[x(neighbor)][y(neighbor)].actCost)
						memory[x(neighbor)][y(neighbor)].actCost = cost;
				}
			}
		}
		return null;
	}
	
	public Direction getAns()
	{
		MapLocation start = myRC.getLocation();
		MapLocation ans = goal;
		MapLocation parent = memory[x(ans)][y(ans)].parent;
		while (!memory[x(ans)][y(ans)].parent.equals(start))
		{
			ans = parent;
			parent = memory[x(ans)][y(ans)].parent;
		}
		return start.directionTo(parent);
	}
}