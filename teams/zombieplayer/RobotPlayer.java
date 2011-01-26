package zombieplayer;

import java.util.ArrayList;
import java.util.HashSet;

import battlecode.common.*;
import battlecode.engine.instrumenter.lang.System;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {

    private final RobotController rc;
    double resourcesLastRound = 0;
	final int resourceBuffer = 20;
	Team myTeam;

	HashSet<ComponentType> recyclerComponents = new HashSet<ComponentType>();
	HashSet<ComponentType> armoryComponents = new HashSet<ComponentType>();
	HashSet<ComponentType> factoryComponents = new HashSet<ComponentType>();
	HashSet<ComponentType> constructorComponents = new HashSet<ComponentType>();
	final int zombieLevels = 20;
	final int roundsPerLevel = 250;
	ZombieBuild[] zombieBuild = new ZombieBuild[zombieLevels];
	int[] zombiesToBuild = new int[zombieLevels];
	
	ArrayList<WeaponController> weapons = new ArrayList<WeaponController>();
	MapLocation closestEnemy;
	
	
    public RobotPlayer(RobotController rc) {
        this.rc = rc;
    }
    
    public void run() {
    	while(true){
    		try{
    			initialize();
		        switch (rc.getChassis()) {
		        case BUILDING:
		        	runBuilding();
		        	break;
		        case LIGHT:
		        case MEDIUM:
		        case HEAVY:
		        	runZombie();
		        default:
		        	break;
		        }
    		} catch (Exception e) {
	            System.out.println("caught exception:");
	            e.printStackTrace();
	        }
	        rc.yield();
    	}
    }
    
	void initialize(){
		myTeam = rc.getTeam();
		
		recyclerComponents.add(ComponentType.SHIELD);
		recyclerComponents.add(ComponentType.PLATING);
		recyclerComponents.add(ComponentType.SMG);
		recyclerComponents.add(ComponentType.HAMMER);
		recyclerComponents.add(ComponentType.BLASTER);
		recyclerComponents.add(ComponentType.SIGHT);
		recyclerComponents.add(ComponentType.RADAR);
		recyclerComponents.add(ComponentType.ANTENNA);
		recyclerComponents.add(ComponentType.PROCESSOR);
		recyclerComponents.add(ComponentType.CONSTRUCTOR);
		
		armoryComponents.add(ComponentType.PLASMA);
		armoryComponents.add(ComponentType.BEAM);
		armoryComponents.add(ComponentType.SATELLITE);
		armoryComponents.add(ComponentType.NETWORK);
		armoryComponents.add(ComponentType.JUMP);
		armoryComponents.add(ComponentType.BUG);
		
		factoryComponents.add(ComponentType.HARDENED);
		factoryComponents.add(ComponentType.REGEN);
		factoryComponents.add(ComponentType.IRON);
		factoryComponents.add(ComponentType.RAILGUN);
		factoryComponents.add(ComponentType.MEDIC);
		factoryComponents.add(ComponentType.TELESCOPE);
		factoryComponents.add(ComponentType.DUMMY);
		factoryComponents.add(ComponentType.DROPSHIP);
		factoryComponents.add(ComponentType.DISH);
		
		constructorComponents.add(ComponentType.RECYCLER);
		constructorComponents.add(ComponentType.ARMORY);
		constructorComponents.add(ComponentType.FACTORY);
		
		zombieBuild[1] = new ZombieBuild(2, Chassis.LIGHT);
		zombieBuild[1].addComponent(ComponentType.HAMMER);
		zombieBuild[1].addComponent(ComponentType.SIGHT);
		
		zombieBuild[3] = new ZombieBuild(4, Chassis.LIGHT);
		zombieBuild[3].addComponent(ComponentType.HAMMER);
		zombieBuild[3].addComponent(ComponentType.SMG);
		zombieBuild[3].addComponent(ComponentType.PLATING);
		zombieBuild[3].addComponent(ComponentType.SIGHT);
		
		zombieBuild[5] = new ZombieBuild(6, Chassis.LIGHT);
		zombieBuild[5].addComponent(ComponentType.HAMMER);
		zombieBuild[5].addComponent(ComponentType.SMG);
		zombieBuild[5].addComponent(ComponentType.PLATING);
		zombieBuild[5].addComponent(ComponentType.PLATING);
		zombieBuild[5].addComponent(ComponentType.SIGHT);

		zombieBuild[7] = new ZombieBuild(8, Chassis.MEDIUM);
		zombieBuild[7].addComponent(ComponentType.HAMMER);
		zombieBuild[7].addComponent(ComponentType.HAMMER);
		zombieBuild[7].addComponent(ComponentType.SMG);
		zombieBuild[7].addComponent(ComponentType.SIGHT);
		
		zombieBuild[9] = new ZombieBuild(10, Chassis.MEDIUM);
		zombieBuild[9].addComponent(ComponentType.HAMMER);
		zombieBuild[9].addComponent(ComponentType.HAMMER);
		zombieBuild[9].addComponent(ComponentType.SMG);
		zombieBuild[9].addComponent(ComponentType.SIGHT);
		zombieBuild[9].addComponent(ComponentType.PLATING);
		zombieBuild[9].addComponent(ComponentType.PLATING);

		zombieBuild[11] = new ZombieBuild(10, Chassis.MEDIUM);
		zombieBuild[11].addComponent(ComponentType.HAMMER);
		zombieBuild[11].addComponent(ComponentType.HAMMER);
		zombieBuild[11].addComponent(ComponentType.SMG);
		zombieBuild[11].addComponent(ComponentType.SIGHT);
		zombieBuild[11].addComponent(ComponentType.PLATING);
		zombieBuild[11].addComponent(ComponentType.PLATING);
		zombieBuild[11].addComponent(ComponentType.PLATING);
		zombieBuild[11].addComponent(ComponentType.PLATING);
		zombieBuild[11].addComponent(ComponentType.PLATING);
		zombieBuild[11].addComponent(ComponentType.PLATING);

		zombieBuild[13] = new ZombieBuild(10, Chassis.HEAVY);
		zombieBuild[13].addComponent(ComponentType.SIGHT);
		zombieBuild[13].addComponent(ComponentType.HAMMER);
		zombieBuild[13].addComponent(ComponentType.HAMMER);
		zombieBuild[13].addComponent(ComponentType.SMG);
		zombieBuild[13].addComponent(ComponentType.PLATING);
		zombieBuild[13].addComponent(ComponentType.PLATING);
		zombieBuild[13].addComponent(ComponentType.PLATING);
		zombieBuild[13].addComponent(ComponentType.PLATING);
		zombieBuild[13].addComponent(ComponentType.PLATING);
		zombieBuild[13].addComponent(ComponentType.PLATING);
		zombieBuild[13].addComponent(ComponentType.PLATING);
		zombieBuild[13].addComponent(ComponentType.PLATING);

		zombieBuild[15] = new ZombieBuild(10, Chassis.HEAVY);
		zombieBuild[15].addComponent(ComponentType.SIGHT);
		zombieBuild[15].addComponent(ComponentType.HAMMER);
		zombieBuild[15].addComponent(ComponentType.HAMMER);
		zombieBuild[15].addComponent(ComponentType.HAMMER);
		zombieBuild[15].addComponent(ComponentType.HAMMER);
		zombieBuild[15].addComponent(ComponentType.SMG);
		zombieBuild[15].addComponent(ComponentType.PLATING);
		zombieBuild[15].addComponent(ComponentType.PLATING);
		zombieBuild[15].addComponent(ComponentType.PLATING);
		zombieBuild[15].addComponent(ComponentType.PLATING);
		zombieBuild[15].addComponent(ComponentType.PLATING);
		zombieBuild[15].addComponent(ComponentType.PLATING);
		zombieBuild[15].addComponent(ComponentType.PLATING);
		zombieBuild[15].addComponent(ComponentType.PLATING);
		
		zombieBuild[17] = new ZombieBuild(10, Chassis.HEAVY);
		zombieBuild[17].addComponent(ComponentType.RADAR);
		zombieBuild[17].addComponent(ComponentType.RAILGUN);
		
		zombieBuild[19] = new ZombieBuild(10, Chassis.HEAVY);
		zombieBuild[19].addComponent(ComponentType.RADAR);
		zombieBuild[19].addComponent(ComponentType.RAILGUN);
		zombieBuild[19].addComponent(ComponentType.RAILGUN);
		zombieBuild[19].addComponent(ComponentType.HARDENED);
		
	}
	
	void runBuilding(){
		while(true){
			BuilderController builder = getBuilder();
			if(builder != null){
				switch(builder.type()){
				case RECYCLER:
					runRecycler();
					break;
				case ARMORY:
					runArmory();
					break;
				case FACTORY:
					runFactory();
					break;
				default:
					System.out.println("builder type unknown!");
					break;
				}
			}
			else{
				if(componentInComponents(ComponentType.RAILGUN, rc.components())){
					runDefenseTower();
				}
			}
			rc.yield();
		}
	}
	
    void runRecycler(){
    	rc.setIndicatorString(0, "ZOMBIE ROBOT HARVESTER");
    	try {
			if(!isFactoryAdjacent()){
				while(true){
					rc.yield();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	while(true){
	    	try {
	    		faceDirection(findEmptyDirection());
	    		Robot zombie = nearbyZombie();
	    		if(zombie != null && isNight()){
		    		buildComponent(zombie);
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	rc.yield();
    	}
    }
    void runArmory(){
    	rc.setIndicatorString(0, "ZOMBIE ROBOT INFECTOR");
    	int[] levelsToBuild = {};
		for(int level:levelsToBuild){
			zombiesToBuild[level] = zombieBuild[level].numTimes;
		}
    	while(true){
	    	try {
	    		faceDirection(findEmptyDirection());
	    		Robot zombie = nearbyZombie();
	    		if(zombie != null && isNight()){
		    		buildComponent(zombie);
	    		}
	    		else{
		    		buildZombie();
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	rc.yield();
    	}
    }
    void runFactory(){
    	rc.setIndicatorString(0, "ZOMBIE ROBOT SPAWNING DEN");
    	int[] levelsToBuild = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
		for(int level:levelsToBuild){
			zombiesToBuild[level] = zombieBuild[level].numTimes;
		}
    	while(true){
	    	try {
	    		faceDirection(findEmptyDirection());
	    		Robot zombie = nearbyZombie();
	    		if(zombie != null && isNight()){
		    		buildComponent(zombie);
	    		}
	    		else{
		    		buildZombie();
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	rc.yield();
    	}
    }
    void runDefenseTower(){
    	rc.setIndicatorString(0, "ZOMBIE ROBOT TOWER OF DOOM");
    	while(true){
	    	try {
	    		if(isNight()){
	    			readyGuns();
	    			fireGunsNoDebris();
	    			if(closestEnemy != null){
	    				faceDirection(rc.getLocation().directionTo(closestEnemy));
	    			}
	    		}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	rc.yield();
    	}
    }
    
    Robot nearbyZombie() throws GameActionException{
    	SensorController sensor = getSensor();
    	for(Robot robot:sensor.senseNearbyGameObjects(Robot.class)){
    		if(sensor.senseRobotInfo(robot).chassis != Chassis.BUILDING && robot.getTeam() == myTeam){
    			return robot;
    		}
    	}
    	return null;
    }
    void buildZombie() throws GameActionException{
    	int zombieLevel = getZombieLevel();
    	if(zombieLevel < zombieLevels){
    		if(zombiesToBuild[zombieLevel] > 0){
    			if(getBuilder().roundsUntilIdle() == 0){
    				if(rc.getTeamResources()
    						>= zombieBuild[zombieLevel].chassis.cost + resourceBuffer){
	    				if(getBuilder().canBuild(zombieBuild[zombieLevel].chassis,
	    						rc.getLocation().add(rc.getDirection()))){
	    					getBuilder().build(zombieBuild[zombieLevel].chassis,
	    							rc.getLocation().add(rc.getDirection()));
	    					zombiesToBuild[zombieLevel]--;
	    				}
    				}
    			}
    		}
    	}
    }
    void buildComponent(Robot zombie) throws GameActionException{
    	int zombieLevel = getZombieLevel();
    	if(zombieLevel < zombieLevels){
	    	ComponentType[] components = getSensor().senseRobotInfo(zombie).components;
	    	ComponentType nextToBuild = zombieBuild[zombieLevel].nextToBuild(components,
	    			getBuilder().type());
	    	if(nextToBuild != null){
				if(getBuilder().roundsUntilIdle() == 0){
					if(rc.getTeamResources() >= nextToBuild.cost + resourceBuffer){
						getBuilder().build(nextToBuild,
								getSensor().senseRobotInfo(zombie).location,
								zombie.getRobotLevel());
					}
				}
	    	}
    	}
    }
    int getZombieLevel(){
    	return Clock.getRoundNum() / roundsPerLevel;
    }
    boolean isNight(){
    	return getZombieLevel() % 2 == 1;
    }
    boolean isFactoryAdjacent() throws GameActionException{
    	Robot[] robots = getSensor().senseNearbyGameObjects(Robot.class); 
    	for(Robot robot: robots){
    		if(robot.getTeam() == myTeam){
    			if(componentInComponents(ComponentType.FACTORY, getSensor().senseRobotInfo(robot).components)){
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    void deadRecon(Direction dir) throws GameActionException{
		if(rc.getRobot().getID() % 2 == 0){
			deadReconRight(dir);
		}
		else{
			deadReconLeft(dir);
		}
	}
	void deadReconRight(Direction dir) throws GameActionException{
		MovementController motor = getMotor();
		if(motor.roundsUntilIdle() == 0){
			if(motor.canMove(dir)){
				if(rc.getDirection() == dir){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir);
				}
			}
			else if(motor.canMove(dir.rotateRight())){
				if(rc.getDirection() == dir.rotateRight()){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir.rotateRight());
				}
			}
			else if(motor.canMove(dir.rotateLeft())){
				if(rc.getDirection() == dir.rotateLeft()){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir.rotateLeft());
				}
			}
			else if(motor.canMove(dir.rotateRight().rotateRight())){
				if(rc.getDirection() == dir.rotateRight().rotateRight()){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir.rotateRight().rotateRight());
				}
			}
			else if(motor.canMove(dir.rotateLeft().rotateLeft())){
				if(rc.getDirection() == dir.rotateLeft().rotateLeft()){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir.rotateLeft().rotateLeft());
				}
			}
		}
	}
	void deadReconLeft(Direction dir) throws GameActionException{
		MovementController motor = getMotor();
		if(motor.roundsUntilIdle() == 0){
			if(motor.canMove(dir)){
				if(rc.getDirection() == dir){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir);
				}
			}
			else if(motor.canMove(dir.rotateLeft())){
				if(rc.getDirection() == dir.rotateLeft()){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir.rotateLeft());
				}
			}
			else if(motor.canMove(dir.rotateRight())){
				if(rc.getDirection() == dir.rotateRight()){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir.rotateRight());
				}
			}
			else if(motor.canMove(dir.rotateLeft().rotateLeft())){
				if(rc.getDirection() == dir.rotateLeft().rotateLeft()){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir.rotateLeft().rotateLeft());
				}
			}
			else if(motor.canMove(dir.rotateRight().rotateRight())){
				if(rc.getDirection() == dir.rotateRight().rotateRight()){
					motor.moveForward();
				}
				else{
					motor.setDirection(dir.rotateRight().rotateRight());
				}
			}
		}
	}
	void faceDirection(Direction dir) throws GameActionException{
		MovementController motor = getMotor();
		if(dir != Direction.NONE &&
				dir != Direction.OMNI &&
				motor.roundsUntilIdle() == 0){
			motor.setDirection(dir);
		}
	}
	Direction findEmptyDirection(){
		Direction dir = Direction.NORTH;
		MovementController motor = getMotor();
		for(int i = 0; i < 8; i++){
			if(motor.canMove(dir)){
				return dir;
			}
			dir = dir.rotateLeft();
		}
		return Direction.NONE;
	}
	
	int componentWeight(ComponentType[] components){
		int totalWeight = 0;
		for(ComponentType component: components){
			totalWeight += component.weight;
		}
		return totalWeight;
	}
	int componentWeight(ArrayList<ComponentType> components){
		int totalWeight = 0;
		for(ComponentType component: components){
			totalWeight += component.weight;
		}
		return totalWeight;
	}
	int componentWeight(ComponentController[] components){
		int totalWeight = 0;
		for(ComponentController component: components){
			totalWeight += component.type().weight;
		}
		return totalWeight;
	}
	boolean componentInComponents(ComponentType component, ComponentType[] components){
		for(ComponentType comp: components){
			if(comp == component){
				return true;
			}
		}
		return false;
	}
	boolean componentInComponents(ComponentType component, ComponentController[] components){
		for(ComponentController comp: components){
			if(comp.type() == component){
				return true;
			}
		}
		return false;
	}
	boolean componentInComponentsTimes(ComponentType component, ComponentType[] components, int times){
		int seen = 0;
		for(ComponentType comp: components){
			if(comp == component){
				seen++;
			}
		}
		return seen >= times;
	}
	String printOffset(MapLocation loc){
		return "(" + (loc.x - rc.getLocation().x) + "," + (loc.y - rc.getLocation().y) + ")";
	}
	
	void runZombie(){
		rc.setIndicatorString(0, "LEVEL " + ((getZombieLevel() + 1) / 2) + " ZOMBIE ROBOT");
		Direction navDirection = Direction.NONE;
		while(navDirection == Direction.NONE){
			navDirection = findEmptyDirection();
			rc.yield();
		}
		MapLocation navTarget = null;
    	while(true){
	    	try {
	    		if(rc.getLocation().equals(navTarget)){
	    			navTarget = null;
	    		}
	    		if(navTarget != null){
	    			deadRecon(rc.getLocation().directionTo(navTarget));
	    		}
	    		else{
	    			deadRecon(navDirection);
	    		}
	    		readyGuns();
	    		if(getZombieLevel() <= 15){
	    			fireMelee();
	    		}
	    		else{
	    			fireGuns();
	    		}
	    		if(closestEnemy != null){
	    			navTarget = closestEnemy;
	    		}
	    		checkDeath();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	rc.yield();
    	}
	}
	void checkDeath(){
		if(!isNight()){
			rc.suicide();
		}
	}
	void readyGuns(){
		weapons.clear();
		for(ComponentController cc: rc.components()){
			if(cc.componentClass() == ComponentClass.WEAPON && cc.roundsUntilIdle() == 0){
				weapons.add((WeaponController) cc);
			}
		}
	}
	void fireMelee() throws GameActionException{
		SensorController sensor = getSensor();
		closestEnemy = null;
		rc.yield();
		if(sensor != null){
			for(Robot robot: sensor.senseNearbyGameObjects(Robot.class)){
				if(robot.getTeam() == myTeam.opponent()){
					if(closestEnemy == null){
						closestEnemy = sensor.senseLocationOf(robot);
					}
					
					for(WeaponController weapon: weapons){
						if(!weapon.isActive() &&
								weapon.withinRange(sensor.senseLocationOf(robot)) &&
								rc.getLocation().distanceSquaredTo(sensor.senseLocationOf(robot)) <= 2){
							if(weapon.type() == ComponentType.SMG){
								if(robot.getRobotLevel() == RobotLevel.IN_AIR){
									weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
								}
							}
							else if(weapon.type() == ComponentType.HAMMER){
								if(robot.getRobotLevel() == RobotLevel.ON_GROUND){
									weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
								}
							}
							else{
								weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
							}
							if(!sensor.canSenseObject(robot)){
								break;
							}
						}
					}
					
				}
			}
		}
		rc.yield();
		if(sensor != null){
			for(Robot robot: sensor.senseNearbyGameObjects(Robot.class)){
				if(robot.getTeam() != myTeam){
					if(closestEnemy == null){
						closestEnemy = sensor.senseLocationOf(robot);
					}
					for(WeaponController weapon: weapons){
						if(!weapon.isActive() &&
								weapon.withinRange(sensor.senseLocationOf(robot)) &&
								rc.getLocation().distanceSquaredTo(sensor.senseLocationOf(robot)) <= 2){
							if(weapon.type() == ComponentType.SMG){
								if(robot.getRobotLevel() == RobotLevel.IN_AIR){
									weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
								}
							}
							else if(weapon.type() == ComponentType.HAMMER){
								if(robot.getRobotLevel() == RobotLevel.ON_GROUND){
									weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
								}
							}
							else{
								weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
							}
							if(!sensor.canSenseObject(robot)){
								break;
							}
						}
					}
				}
			}
		}
	}
	void fireGuns() throws GameActionException{
		SensorController sensor = getSensor();
		closestEnemy = null;
		if(sensor != null){
			for(Robot robot: sensor.senseNearbyGameObjects(Robot.class)){
				if(robot.getTeam() == myTeam.opponent()){
					if(closestEnemy == null){
						closestEnemy = sensor.senseLocationOf(robot);
					}
					for(WeaponController weapon: weapons){
						if(!weapon.isActive() &&
								weapon.withinRange(sensor.senseLocationOf(robot))){
							if(weapon.type() == ComponentType.HAMMER){
								if(robot.getRobotLevel() == RobotLevel.ON_GROUND){
									weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
								}
							}
							else{
								weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
							}
							if(!sensor.canSenseObject(robot)){
								break;
							}
						}
					}
				}
			}
		}
		rc.yield();
		if(sensor != null){
			for(Robot robot: sensor.senseNearbyGameObjects(Robot.class)){
				if(robot.getTeam() != myTeam){
					for(WeaponController weapon: weapons){
						if(!weapon.isActive() &&
								weapon.withinRange(sensor.senseLocationOf(robot))){
							if(weapon.type() == ComponentType.HAMMER){
								if(robot.getRobotLevel() == RobotLevel.ON_GROUND){
									weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
								}
							}
							else{
								weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
							}
							if(!sensor.canSenseObject(robot)){
								break;
							}
						}
					}
				}
			}
		}
	}
	void fireGunsNoDebris() throws GameActionException{
		SensorController sensor = getSensor();
		closestEnemy = null;
		if(sensor != null){
			for(Robot robot: sensor.senseNearbyGameObjects(Robot.class)){
				if(robot.getTeam() == myTeam.opponent()){
					if(closestEnemy == null){
						closestEnemy = sensor.senseLocationOf(robot);
					}
					for(WeaponController weapon: weapons){
						if(!weapon.isActive() &&
								weapon.withinRange(sensor.senseLocationOf(robot))){
							if(weapon.type() == ComponentType.HAMMER){
								if(robot.getRobotLevel() == RobotLevel.ON_GROUND){
									weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
								}
							}
							else{
								weapon.attackSquare(sensor.senseLocationOf(robot), robot.getRobotLevel());
							}
							if(!sensor.canSenseObject(robot)){
								break;
							}
						}
					}
				}
			}
		}
	}
	
	BroadcastController getComm(){
		for(ComponentController cc: rc.components()){
			if(cc.componentClass() == ComponentClass.COMM){
				return (BroadcastController) cc;
			}
		}
		return null;
	}
	JumpController getJump(){
		for(ComponentController cc: rc.components()){
			if(cc.type() == ComponentType.JUMP){
				return (JumpController) cc;
			}
		}
		return null;
	}
	SensorController getSensor(){
		SensorController bestSensor = null;
		for(ComponentController cc: rc.components()){
			if(cc.componentClass() == ComponentClass.SENSOR){
				if(bestSensor == null || bestSensor.type().range < cc.type().range){
					bestSensor = (SensorController) cc;
				}
			}
		}
		return bestSensor;
	}
	BuilderController getBuilder(){
		for(ComponentController cc: rc.components()){
			if(cc.componentClass() == ComponentClass.BUILDER){
				return (BuilderController) cc;
			}
		}
		return null;
	}
	MovementController getMotor(){
		for(ComponentController cc: rc.components()){
			if(cc.componentClass() == ComponentClass.MOTOR){
				return (MovementController) cc;
			}
		}
		return null;
	}
    
	class ZombieBuild{
		
		public final int numTimes;
		public final Chassis chassis;
		private ArrayList<ComponentType> components;
		
		public ZombieBuild(int numTimes, Chassis chassis){
			this.numTimes = numTimes;
			this.chassis = chassis;
			this.components = new ArrayList<ComponentType>();
		}
		
		public void addComponent(ComponentType component){
			components.add(component);
		}
		
		public ComponentType nextToBuild(ComponentType[] builtSoFar, ComponentType building){
			ArrayList<ComponentType> componentsNeeded = (ArrayList<ComponentType>) components.clone();
			for(ComponentType built: builtSoFar){
				componentsNeeded.remove(built);
			}
			for(ComponentType needed:componentsNeeded){
				if(canBuildingBuild(building, needed)){
					return needed;
				}
			}
			return null;
		}
		
	}
	
	boolean canBuildingBuild(ComponentType building, ComponentType component){
		switch(building){
		case RECYCLER:
			return recyclerComponents.contains(component);
		case ARMORY:
			return armoryComponents.contains(component);
		case FACTORY:
			return factoryComponents.contains(component);
		case CONSTRUCTOR:
			return constructorComponents.contains(component);
		default:
			return false;
		}
	}
	
	
}
