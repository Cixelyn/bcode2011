package guileBot;

import battlecode.common.*;
import java.lang.Math;


/**
 * THIS CLASS DOESN'T WORK YET!
 * @author Cory
 *
 */
public class Database {
	private static final int DB_SIZE = 1024;
	
	public final boolean[] enemyProfiledDB;
	public final boolean[] enemyInvincibleDB;
	
	private int numWeapons;
	private int numLastFired;
		
	public Database() {
		enemyProfiledDB = new boolean[DB_SIZE];
		enemyInvincibleDB = new boolean[DB_SIZE];
		numWeapons = 0;
		numLastFired = 0;
	}
	
	
	public void storeEnemyRobotDamage(int damage, int numFiredWeps, int robotID) {
		numLastFired = numFiredWeps;
	}
	

	
	public boolean isEnemyInvincible(int robotID) {
		return false;
	}
	
	
	
	public void setNumWeapons(int num) {
		numWeapons = num;
	}
	
	
	
	
	

}
