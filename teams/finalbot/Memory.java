package finalbot;

import battlecode.common.*;


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
	public static int idxCurrRound = 2;
	
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
