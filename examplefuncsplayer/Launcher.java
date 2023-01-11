package examplefuncsplayer;

import battlecode.common.*;

import java.nio.file.Path;

import static examplefuncsplayer.Pathfinding.returnToHomeBase;
import static examplefuncsplayer.RobotPlayer.directions;
import static examplefuncsplayer.RobotPlayer.rng;

public class Launcher {
    static void runLauncher(RobotController rc) throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length >= 0) {
            // MapLocation toAttack = enemies[0].location;
            MapLocation toAttack = rc.getLocation().add(Direction.EAST);

            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");
                rc.attack(toAttack);
            }
        }

        // Also try to move randomly.
        /*
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
         */

        //Move with the pathfinding module
        try {
            rc.move(returnToHomeBase(rc, 0));
        }
        catch (Exception e){}


    }

}
