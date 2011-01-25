package finalbot;

import battlecode.common.*;
import java.lang.Math;


/**
 * This class allows for the logging of various prespecified messages into the team message arrays
 * @author Cory
 *
 */
public class Memory {
	private final long[] memory;
	RobotController myRC;
	
	public Memory(RobotPlayer player) {
		myRC = player.myRC;
		memory = myRC.getTeamMemory();
	}
		
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////// GAME ROUND MEMORY ///////////////////////////////////////////
	public static int idxCurrRound = 0;
	
	/**
	 * this function should only ever be called once per game.
	 * it will get currend round. 
	 * @return
	 */
	public long getCurrRound() {
		return memory[idxCurrRound];
	}
	
	public void setNextRound(long num) {
		myRC.setTeamMemory(idxCurrRound, num);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////// NUMBER OF GUNS //////////////////////////////////////////////
	
	public void setNumGuns(int index, int smgs, int blasters, int railguns, int beams, int hammers, int guns) {
		myRC.setTeamMemory(index, 
			  smgs
			| blasters<<8
			| railguns<<16
			| beams<<24
			| hammers<<32
			| guns<<40
		);
	}

	
	
	
	
	//Easy Access Constants
	public static final int idxSMG = 0;
	public static final int idxBlasters = 1;
	public static final int idxRailguns = 2;
	public static final int idxBeams = 3;
	public static final int idxHammers = 4;
	public static final int idxGuns = 5;
	
	/**
	 * This function returns the max number of each gun type seen in the previous round.
	 * Use the constants idxSMG, idxBlasters, etc. to figure out indicies.
	 * @return 
	 */
	public int[] getMaxNumGuns() {
		int smgs, blasters, railguns, beams, hammers, guns;
		smgs=blasters=railguns=beams=hammers=guns=0;

		for(int i=1; i<memory.length; i++) {
			long data = memory[i];
			smgs 	= Math.max(smgs 	, (int) ((data)     & 0xFF));
			blasters= Math.max(blasters	, (int) ((data>>8)  & 0xFF));
			railguns= Math.max(railguns , (int) ((data>>16) & 0xFF));
			beams   = Math.max(beams	, (int) ((data>>24) & 0xFF));
			hammers = Math.max(hammers 	, (int) ((data>>32) & 0xFF));
			guns	= Math.max(guns     , (int) ((data>>40) & 0xFF));
		}
		
		
		return new int[]{smgs,blasters,railguns,beams,hammers,guns};
		
	}
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////// WRAITH MEMORY CODE //////////////////////////////////////////

	public static int idxWraithKills = 1;
	public static int MEMORY_MAX_Wraiths=8;

	/**
	 * Sets the number of kills a particular flier got.  Since it's a long, you can
	 * only have a total of 8 flier memories.
	 */
	public void setWraithsKills(int wraithnum, int numkills) {
		int shift = 8*wraithnum;
		myRC.setTeamMemory(idxWraithKills, numkills<<shift, 0xFF<<shift);
	}

	/**
	 * This functions computes the total number of fliers killed.
	 * It's a fairly expensive function, so don't call it too often, mmk?
	 * @return
	 */
	public int getWraithKills() {
		long data = memory[idxWraithKills];

		int count=0;
		for(int i=0; i<8; i++) {
			System.out.println((data >> (8*i)) & 0xFF);
			count += (data >> (8*i)) & 0xFF;   			
		}
		return count;
	}
}
