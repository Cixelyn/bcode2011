package fibbyBot12;

import battlecode.common.*;
import java.util.*;

public class JumpNavigation
{
	
	public final RobotPlayer myPlayer;
	
	public final int[][][] rallyScores = 
	{
			
			// NORTH
			{
				{  -99   ,  -99   ,  -99   ,  -99   ,   5    ,  -99   ,  -99   ,  -99   ,  -99   },
				{  -99   ,  -99   ,   4    ,   4    ,   3    ,   4    ,   4    ,  -99   ,  -99   },
				{  -99   ,   0    ,   0    ,   1    ,   2    ,   1    ,   0    ,   0    ,  -99   },
				{  -99   ,  -1    ,   0    ,   0    ,  -1    ,   0    ,   0    ,  -1    ,  -99   },
				{  -2    ,  -3    ,  -4    ,  -1    ,  -99   ,  -1    ,  -4    ,  -3    ,  -2    },
				{  -99   ,  -1    ,  -2    ,  -3    ,  -5    ,  -3    ,  -2    ,  -1    ,  -99   },
				{  -99   ,  -4    ,  -5    ,  -6    ,  -7    ,  -6    ,  -5    ,  -4    ,  -99   },
				{  -99   ,  -99   ,  -7    ,  -7    ,  -8    ,  -7    ,  -7    ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -99   ,  -99   ,  -9    ,  -99   ,  -99   ,  -99   ,  -99   }
			},
			
			// NORTHEAST
			{
				{  -99   ,  -99   ,  -99   ,  -99   ,   0    ,  -99   ,  -99   ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -2    ,   0    ,   0    ,   3    ,   4    ,  -99   ,  -99   },
				{  -99   ,  -3    ,  -2    ,  -1    ,  -1    ,   1    ,   2    ,   4    ,  -99   },
				{  -99   ,  -4    ,  -3    ,  -2    ,  -1    ,  -1    ,   1    ,   3    ,  -99   },
				{  -6    ,  -5    ,  -4    ,  -3    ,  -99   ,  -1    ,  -1    ,   0    ,   0    },
				{  -99   ,  -6    ,  -5    ,  -4    ,  -3    ,  -2    ,  -1    ,   0    ,  -99   },
				{  -99   ,  -7    ,  -6    ,  -5    ,  -4    ,  -3    ,  -2    ,   2    ,  -99   },
				{  -99   ,  -99   ,  -7    ,  -6    ,  -5    ,  -4    ,  -3    ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -99   ,  -99   ,  -6    ,  -99   ,  -99   ,  -99   ,  -99   }
			},
			
			// EAST
			{
				{  -99   ,  -99   ,  -99   ,  -99   ,  -2    ,  -99   ,  -99   ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -4    ,  -1    ,  -3    ,   -1   ,   0    ,  -99   ,  -99   },
				{  -99   ,  -7    ,  -5    ,  -2    ,  -4    ,   0    ,   0    ,   4    ,  -99   },
				{  -99   ,  -7    ,  -6    ,  -3    ,  -1    ,   0    ,   1    ,   4    ,  -99   },
				{  -9    ,  -8    ,  -7    ,  -5    ,  -99   ,  -1    ,   2    ,   3    ,   5    },
				{  -99   ,  -7    ,  -6    ,  -3    ,  -1    ,   0    ,   1    ,   4    ,  -99   },
				{  -99   ,  -7    ,  -5    ,  -2    ,  -4    ,   0    ,   0    ,   4    ,  -99   },
				{  -99   ,  -99   ,  -4    ,  -1    ,  -3    ,  -1    ,   0    ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -99   ,  -99   ,  -2    ,  -99   ,  -99   ,  -99   ,  -99   }
			},
			
			// SOUTHEAST
			{
				{  -99   ,  -99   ,  -99   ,  -99   ,  -6    ,  -99   ,  -99   ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -7    ,  -6    ,  -5    ,  -4    ,  -3    ,  -99   ,  -99   },
				{  -99   ,  -7    ,  -6    ,  -5    ,  -4    ,  -3    ,  -2    ,   2    ,  -99   },
				{  -99   ,  -6    ,  -5    ,  -4    ,  -3    ,  -2    ,  -1    ,   0    ,  -99   },
				{  -6    ,  -5    ,  -4    ,  -3    ,  -99   ,  -1    ,  -1    ,   0    ,   0    },
				{  -99   ,  -4    ,  -3    ,  -2    ,  -1    ,  -1    ,   1    ,   3    ,  -99   },
				{  -99   ,  -3    ,  -2    ,  -1    ,  -1    ,   1    ,   2    ,   4    ,  -99   },
				{  -99   ,  -99   ,  -2    ,   0    ,   0    ,   3    ,   4    ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -99   ,  -99   ,   0    ,  -99   ,  -99   ,  -99   ,  -99   }
			},
			
			// SOUTH
			{
				{  -99   ,  -99   ,  -99   ,  -99   ,  -9    ,  -99   ,  -99   ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -7    ,  -7    ,  -8    ,  -7    ,  -7    ,  -99   ,  -99   },	
				{  -99   ,  -4    ,  -5    ,  -6    ,  -7    ,  -6    ,  -5    ,  -4    ,  -99   },
				{  -99   ,  -1    ,  -2    ,  -3    ,  -5    ,  -3    ,  -2    ,  -1    ,  -99   },
				{  -2    ,  -3    ,  -4    ,  -1    ,  -99   ,  -1    ,  -4    ,  -3    ,  -2    },
				{  -99   ,  -1    ,   0    ,   0    ,  -1    ,   0    ,   0    ,  -1    ,  -99   },
				{  -99   ,   0    ,   0    ,   1    ,   2    ,   1    ,   0    ,   0    ,  -99   },
				{  -99   ,  -99   ,   4    ,   4    ,   3    ,   4    ,   4    ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -99   ,  -99   ,   5    ,  -99   ,  -99   ,  -99   ,  -99   }
			},
			
			// SOUTHWEST
			{
				{  -99   ,  -99   ,  -99   ,  -99   ,  -6    ,  -99   ,  -99   ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -3    ,  -4    ,  -5    ,  -6    ,  -7    ,  -99   ,  -99   },
				{  -99   ,  -2    ,  -2    ,  -3    ,  -4    ,  -5    ,  -6    ,  -7    ,  -99   },
				{  -99   ,   0    ,  -1    ,  -2    ,  -3    ,  -4    ,  -5    ,  -6    ,  -99   },
				{   0    ,   0    ,  -1    ,  -1    ,  -99   ,  -3    ,  -4    ,  -5    ,  -6    },
				{  -99   ,   3    ,   1    ,  -1    ,  -1    ,  -2    ,  -3    ,  -4    ,  -99   },
				{  -99   ,   4    ,   2    ,   1    ,  -1    ,  -1    ,  -2    ,  -3    ,  -99   },
				{  -99   ,  -99   ,   4    ,   3    ,   0    ,   0    ,  -2    ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -99   ,  -99   ,   0    ,  -99   ,  -99   ,  -99   ,  -99   }
			},
			
			// WEST
			{
				{  -99   ,  -99   ,  -99   ,  -99   ,  -2    ,  -99   ,  -99   ,  -99   ,  -99   },
				{  -99   ,  -99   ,   0    ,  -1    ,  -3    ,  -1    ,  -4    ,  -99   ,  -99   },
				{  -99   ,   4    ,   0    ,   0    ,  -4    ,  -2    ,  -5    ,  -7    ,  -99   },
				{  -99   ,   4    ,   1    ,   0    ,  -1    ,  -3    ,  -6    ,  -7    ,  -99   },
				{   5    ,   3    ,   2    ,  -1    ,  -99   ,  -5    ,  -7    ,  -8    ,  -9    },
				{  -99   ,   4    ,   1    ,   0    ,  -1    ,  -2    ,  -6    ,  -7    ,  -99   },
				{  -99   ,   4    ,   0    ,   0    ,  -4    ,  -2    ,  -5    ,  -7    ,  -99   },
				{  -99   ,  -99   ,   0    ,  -1    ,  -3    ,  -1    ,  -4    ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -99   ,  -99   ,  -2    ,  -99   ,  -99   ,  -99   ,  -99   }
			},
				
			// NORTHWEST
			{
				{  -99   ,  -99   ,  -99   ,  -99   ,   0    ,  -99   ,  -99   ,  -99   ,  -99   },
				{  -99   ,  -99   ,   4    ,   3    ,   0    ,   0    ,  -2    ,  -99   ,  -99   },
				{  -99   ,   4    ,   2    ,   1    ,  -1    ,  -1    ,  -2    ,  -3    ,  -99   },
				{  -99   ,   3    ,   1    ,  -1    ,  -1    ,  -2    ,  -3    ,  -4    ,  -99   },
				{   0    ,   0    ,  -1    ,  -1    ,  -99   ,  -3    ,  -4    ,  -5    ,  -6    },
				{  -99   ,   0    ,  -1    ,  -2    ,  -3    ,  -4    ,  -5    ,  -6    ,  -99   },
				{  -99   ,  -2    ,  -2    ,  -3    ,  -4    ,  -5    ,  -6    ,  -7    ,  -99   },
				{  -99   ,  -99   ,  -3    ,  -4    ,  -5    ,  -6    ,  -7    ,  -99   ,  -99   },
				{  -99   ,  -99   ,  -99   ,  -99   ,  -6    ,  -99   ,  -99   ,  -99   ,  -99   }
			}
			
	};
	
	public JumpNavigation ( RobotPlayer player )
	{
		myPlayer = player;
	}
	
	
	/**
	 * Takes in a rally direction and outputs a map location to jump to
	 * @author JVen
	 * @param rally An index of Direction.values()
	 * @return A MapLocation to jump to
	 */
	
	public MapLocation jumpTo ( int rally ) throws Exception
	{
		
		MapLocation myLoc = myPlayer.myRC.getLocation();
		
		// make sure we have jump and satellite
		
		if ( myPlayer.myJump == null || myPlayer.mySensor == null || myPlayer.mySensor.type() != ComponentType.SATELLITE )
			return null;
		
		// initialize possible jump locations with score 0
		
		int[][] locScores = new int[9][9];
		
		// add scores based on rally and incorporate void/off_map tiles
		// TODO possibly only sense for null object at the end
		
		for ( int i = 9 ; --i >= 0 ; )
			for ( int j = 9 ; --j >= 0 ; )
			{
				if ( myPlayer.myRC.senseTerrainTile(myLoc.add(j - 4, i - 4)) != TerrainTile.LAND || myPlayer.mySensor.senseObjectAtLocation(myLoc.add(j - 4, i - 4), RobotLevel.ON_GROUND) != null )
					locScores[i][j] = -99;
				else
					locScores[i][j] += rallyScores[rally][i][j];
			}
		
		// get enemy robot info around us
		
		Robot[] nearbyRobots = myPlayer.mySensor.senseNearbyGameObjects(Robot.class);
		ArrayList<MapLocation> rLocs = new ArrayList<MapLocation>(100);
		ArrayList<Integer> rRanges = new ArrayList<Integer>(100);
		
		Robot r;
		RobotInfo rInfo;
		ComponentType c;
		int maxRange; // 0 means less than 25, 1 means 25, 2 means 36
		// TODO Incorporate enemy direction and sneak up on them ninja style
		
		for ( int i = nearbyRobots.length ; --i >= 0 ; )
		{
			r = nearbyRobots[i];
			if ( r.getTeam() == myPlayer.myRC.getTeam().opponent() )
			{
				rInfo = myPlayer.mySensor.senseRobotInfo(r);
				
				// check if we're dealing with railguns, SMGs, or beams
				maxRange = 0;
				for ( int j = rInfo.components.length ; --j >= 0 ; )
				{
					c = rInfo.components[j];
					if ( c == ComponentType.RAILGUN && maxRange == 0 )
						maxRange = 1;
					if ( c == ComponentType.SMG || c == ComponentType.BEAM )
					{
						maxRange = 2;
						break;
					}
				}
				rLocs.add(rInfo.location);
				rRanges.add(maxRange);
			}
		}
		
		// incorporate enemy robot info into scores
		
		MapLocation loc;
		MapLocation rLoc;
		
		for ( int i = rLocs.size() ; --i >= 0 ; )
		{
			rLoc = rLocs.get(i);
			for ( int j = 9 ; --j >= 0 ; )
				for ( int k = 9 ; --k >= 0 ; )
				{
					// don't consider voids, off_maps, occupied squares, out of range squares
					// TODO occupied square restriction removed if removed above
					if ( locScores[j][k] > -60 )
					{
						loc = myLoc.add(k - 4, j - 4);
						if ( rRanges.get(i) >= 0 )
						{
							if ( loc.distanceSquaredTo(rLoc) <= 2 ) // 1 or 2
								locScores[j][k] += -10;
							else if ( loc.distanceSquaredTo(rLoc) <= 5 ) // 4 or 5
								locScores[j][k] += -2;
							else if ( loc.distanceSquaredTo(rLoc) <= 9 ) // 8 or 9
								locScores[j][k] += -1;
							else if ( loc.distanceSquaredTo(rLoc) <= 16 ) // 10 or 13 or 16
								locScores[j][k] += 10; // changed from 5 on PDF
							else if ( rRanges.get(i) >= 1 )
							{
								if ( loc.distanceSquaredTo(rLoc) <= 25 ) // 17 or 18 or 20 or 25
									locScores[j][k] += -10;
								else if ( rRanges.get(i) == 2 )
								{
									if ( loc.distanceSquaredTo(rLoc) <= 36 ) // 26 or 29 or 32 or 34 or 36
										locScores[j][k] += -10;
								}
							}
						}
					}
				}
		}
		
		// TODO incorporate enemy directions here!
		
		
		// get maximum score
		
		int maxScore = -99; // sentinel value
		int maxIndices = 0; // if max indices are (3,5), maxIndices = 35. 10*i + j  (works since 0 <= i,j <= 8)
		
		for ( int i = 9 ; --i >= 0 ; )
			for ( int j = 9 ; --j >= 0 ; )
			{
				if ( locScores[i][j] >= maxScore )
				{
					maxScore = locScores[i][j];
					maxIndices = 10*i + j;
				}
			}
		
		
		// return maximum square
		
		return myLoc.add((maxIndices % 10) - 4, (maxIndices / 10) - 4);
		
	}
	
}
