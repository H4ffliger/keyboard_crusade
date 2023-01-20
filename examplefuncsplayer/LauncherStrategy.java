package examplefuncsplayer;

import battlecode.common.*;
import battlecode.world.Well;

import java.util.ArrayList;

import static examplefuncsplayer.Communication.*;
import static examplefuncsplayer.Pathing.moveTowards;
import static examplefuncsplayer.RobotPlayer.*;

public class LauncherStrategy {

    private static MapLocation targetLocation = null;
    private static ArrayEntity targetEntity;

    private static boolean onEnemyHQ = false;

    private static int goal = 0;

    private static ArrayList<WellEntity> wellEntities = new ArrayList<>();

    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runLauncher(RobotController rc) throws GameActionException {
        //Try to attack if enemies are around
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        RobotInfo targetRobot = null;
        int lowestHealth = 400;
        for (RobotInfo enemy : enemies) {
            //reportEnemy(rc, enemy.location);
            int enemyHealth = enemy.getHealth();
            if (enemy.getType() != RobotType.HEADQUARTERS) {
                if (enemyHealth < lowestHealth) {
                    targetRobot = enemy;
                    lowestHealth = enemyHealth;
                }
            } else {
                reportEnemy(rc, enemy.location);
            }
        }
        if (targetRobot != null) {
            if (rc.canAttack(targetRobot.getLocation()))
                rc.attack(targetRobot.getLocation());
        }
        tryWriteMessages(rc);
        //Accomplish one goal
        updateHeadquarterInfo(rc);
        clearObsoleteEnemies(rc);

        if (turnCount % 2 == 0) senseImportantLocations(rc);

        //decide what to do
        rc.setIndicatorString("Goal: " + goal);
        if (goal == 0) {
            decideWhatToDo(rc);
        }

        rc.setIndicatorString("Goal: " + goal);
        switch (goal) {
            case 1: {
                defend(rc);
                break;
            }
            case 2: {
                scout(rc);
                break;
            }
            case 3: {
                goToTarget(rc);
                break;
            }
            case 4: {
                holdPosition(rc);
            }
        }


        /*ArrayList<WellEntity> wellEntities = getAllWells(rc);
        for (WellEntity wellEntity : wellEntities) {
            if (wellEntity != null) {
                if (wellEntity.getDefenseStatus() == 0) {
                    wellEntity.setDefenseStatus(1);
                    updateWell(rc, wellEntity);
                    targetLocation = wellEntity.getOwnLocation();
                }
            } else break;

        }
        */
        /*if (targetLocation != null) {
            if (!targetLocation.equals(rc.getLocation())) {
                moveTowards(rc, targetLocation);
            } else {
                Clock.yield();
            }
        } else {
            WellInfo[] nearbyWells = rc.senseNearbyWells(rc.getLocation(), 1);
            for (WellInfo wellInfo : nearbyWells) {
                if (wellInfo.getMapLocation().equals(rc.getLocation())) {
                    rc.setIndicatorString("Stay on well");
                    Clock.yield();
                }
            }
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

*/
        //go to Enemy Headquarters if possible


        // Also try to move randomly.
        /*Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }*/
    }

    //TODO sense enemy HQ, targets...
    private static void senseImportantLocations(RobotController rc) throws GameActionException {
        WellInfo[] nearbyWells = rc.senseNearbyWells();
        wellEntities = getAllWells(rc);
        for (WellInfo wellInfo : nearbyWells) {
            WellEntity actualWellEntity = new WellEntity(-1, wellInfo.getMapLocation());
            if (wellEntities.contains(actualWellEntity)) {
                break;
            } else {
                addWell(rc, actualWellEntity);
            }
        }
    }

    private static void holdPosition(RobotController rc) {
        Clock.yield();
    }

    private static void scout(RobotController rc) throws GameActionException {
        moveTowards(rc, new MapLocation(rc.getMapHeight() / 2, rc.getMapWidth() / 2));
        goal = 0;
    }

    private static void goToTarget(RobotController rc) throws GameActionException {
        if (targetEntity != null) {
            targetLocation = targetEntity.getOwnLocation();
        }
        RobotInfo[] visibleEnemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for (RobotInfo enemy : visibleEnemies) {
            if (enemy.getType() != RobotType.HEADQUARTERS) {
                goal = 3;
            //    rc.setIndicatorString("Goal set to: " + goal);
                targetLocation = enemy.getLocation();
                MapLocation enemyLocation = enemy.getLocation();
                if (rc.getLocation().distanceSquaredTo(enemyLocation) > 4) {
                    goal = 4;
              //      rc.setIndicatorString("Goal set to: " + goal);
                    Clock.yield();
                }
            }
        }
        if (targetLocation != null) moveTowards(rc, targetLocation);
        else goal = 0;
     //   rc.setIndicatorString("Goal set to: " + goal);

    }

    private static void defend(RobotController rc) throws GameActionException {
        if (targetEntity != null) {
            if (targetEntity.getOwnLocation().equals(rc.getLocation())) {
                goal = 4;
            //    rc.setIndicatorString("Goal set to: " + goal);
            } else {
                moveTowards(rc, targetEntity.getOwnLocation());
            }
        } else {
            goal = 0;
           // rc.setIndicatorString("Goal set to: " + goal);
        }
    }


    private static void decideWhatToDo(RobotController rc) throws GameActionException {
        wellEntities = getAllWells(rc);
        for (WellEntity wellEntity : wellEntities) {
            if (wellEntity.getDefenseStatus() == 0) {
                wellEntity.setDefenseStatus(1);
                targetEntity = wellEntity;
                updateWell(rc, wellEntity);
                goal = 1;
          //      rc.setIndicatorString("Goal set to: " + goal);
                break;
            }
        }
        if (goal != 1) {
            targetLocation = getClosestEnemy(rc);
            if (targetLocation!=null) {
                goal = 3;
            } else {
                goal = 2;
            }
        //    rc.setIndicatorString("Goal set to: " + goal);
        } else {
            goal = 2;
       //     rc.setIndicatorString("Goal set to: " + goal);
        }
    }
}
