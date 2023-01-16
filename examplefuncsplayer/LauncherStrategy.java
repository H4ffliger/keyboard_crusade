package examplefuncsplayer;

import battlecode.common.*;

import static examplefuncsplayer.Communication.*;
import static examplefuncsplayer.Pathing.moveTowards;
import static examplefuncsplayer.RobotPlayer.*;

public class LauncherStrategy {

    private static MapLocation targetLocation = null;

    private static boolean onEnemyHQ = false;

    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runLauncher(RobotController rc) throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        int lowestHealth = 100;
        int smallestDistance = 100;
        RobotInfo target = null;
        updateHeadquarterInfo(rc);
        clearObsoleteEnemies(rc);
        for (RobotInfo enemy : enemies) {
            //reportEnemy(rc, enemy.location);
            int enemyHealth = enemy.getHealth();
            int enemyDistance = enemy.getLocation().distanceSquaredTo(rc.getLocation());
            if (enemy.getType() != RobotType.HEADQUARTERS) {
                if (enemyHealth < lowestHealth) {
                    target = enemy;
                    lowestHealth = enemyHealth;
                    smallestDistance = enemyDistance;
                } else if (enemyHealth == lowestHealth) {
                    if (enemyDistance < smallestDistance) {
                        target = enemy;
                        smallestDistance = enemyDistance;
                    }
                }
            }
        }
        //tryWriteMessages(rc);
        if (target != null) {
            if (rc.canAttack(target.getLocation()))
                rc.attack(target.getLocation());
        } else if (!onEnemyHQ) {
            WellEntity[] wellEntities = getAllWells(rc);
            for (WellEntity wellEntity : wellEntities) {
                if (wellEntity != null) {
                    if (wellEntity.getDefenseStatus() == 0) {
                        wellEntity.setDefenseStatus(1);
                        updateWell(rc, wellEntity);
                        targetLocation = wellEntity.getOwnLocation();
                    }
                } else break;

            }
            if (targetLocation != null) {
                if (!targetLocation.equals(rc.getLocation())) {
                    moveTowards(rc, targetLocation);
                } else {
                    Clock.yield();
                }
            } else {
                WellInfo[] nearbyWells = rc.senseNearbyWells(rc.getLocation(), 1);
                if (nearbyWells.length>0&&nearbyWells[0].getMapLocation().equals(rc.getLocation())) {
                    rc.setIndicatorString("Stay on well");
                    Clock.yield();
                } else {
                    rc.setIndicatorString("I don't stand on a well");
                    WellInfo[] wells = rc.senseNearbyWells();
                    if (wells.length > 0) {
                        for (WellInfo well : wells) {
                            MapLocation wellLoc = well.getMapLocation();
                            if (!rc.canSenseRobotAtLocation(wellLoc)) {
                                moveTowards(rc, wellLoc);
                                break;
                            }
                        }
                    } else {
                        moveTowards(rc, new MapLocation(rc.getMapHeight() / 2, rc.getMapWidth() / 2));
                    }
                }
            }

        }

        //go to Enemy Headquarters if possible
        RobotInfo[] visibleEnemies = rc.senseNearbyRobots(-1, opponent);
        for (RobotInfo enemy : visibleEnemies) {
            if (enemy.getType() != RobotType.HEADQUARTERS) {
                MapLocation enemyLocation = enemy.getLocation();
                MapLocation robotLocation = rc.getLocation();
                if (rc.getLocation().distanceSquaredTo(enemyLocation) == 1) {

                    Clock.yield();
                }
                Direction moveDir = robotLocation.directionTo(enemyLocation);
                if (rc.canMove(moveDir)) {
                    rc.move(moveDir);
                }
            }
        }

        // Also try to move randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
