package examplefuncsplayer;

import battlecode.common.*;

import java.nio.file.Path;
import java.util.ArrayList;

import static examplefuncsplayer.Pathfinding.returnToHomeBase;
import static examplefuncsplayer.RobotPlayer.directions;
import static examplefuncsplayer.RobotPlayer.rng;

public class Launcher {

    static int radioArray;
    static ArrayList<MapLocation> hqLocations = new ArrayList<MapLocation>();
    static void runLauncher(RobotController rc) throws GameActionException {


        //radioArray = rc.readSharedArray(0);

        //Set the local hq Positions
        for (int i = 0; i < 4; i++) {
            String hqLocationString = Integer.toString(rc.readSharedArray(i));
            int dx = Integer.parseInt(hqLocationString.substring(1, 3));
            int dy =  Integer.parseInt(hqLocationString.substring(3, 5));
            hqLocations.add(new MapLocation(dx, dy));
            //rc.setIndicatorString("Set HQ to " + hqLocationString);
        }



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
            rc.move(returnToHomeBase(rc, hqLocations.get(0).x, hqLocations.get(0).y));
        }
        catch (Exception e){}


    }

}
