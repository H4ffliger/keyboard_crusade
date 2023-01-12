package examplefuncsplayer;

import battlecode.common.*;
import scala.Int;

import java.util.ArrayList;
import java.util.Random;

import static examplefuncsplayer.Pathfinding.*;
import static examplefuncsplayer.RobotPlayer.rng;
import static examplefuncsplayer.Strategy.explore;

public class Launcher {

    //static int radioArray;
    static ArrayList<MapLocation> hqLocations = new ArrayList<>();
    static Integer exploreID;


    static void runLauncher(RobotController rc) throws GameActionException {

        //Creation process
        if(exploreID == null){
            //ToDo: Change and coordinate with HQ shared array
            exploreID = new Random().nextInt(9);
            System.out.println("Rnd: " + Integer.toString(exploreID));
        }

        //Set the local hq Positions
        for (int i = 0; i < 4; i++) {
            String hqLocationString = Integer.toString(rc.readSharedArray(i));
            if(!hqLocationString.equals("0")) {
                int dx = Integer.parseInt(hqLocationString.substring(1, 3));
                int dy =  Integer.parseInt(hqLocationString.substring(3, 5));
                hqLocations.add(new MapLocation(dx, dy));
            }
        }

        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            // MapLocation toAttack = enemies[0].location;
            MapLocation toAttack = rc.getLocation().add(Direction.EAST);

            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");
                rc.attack(toAttack);
            }
        }

        //Move with the pathfinding module
        //ToDo: Temporary exploring function for testing
        try {
            if (rc.getRoundNum() < 200) {
                explore(rc, exploreID);
            } else {
                rc.move(returnToHomeBase(rc, hqLocations.get(0).x, hqLocations.get(0).y));
            }
        }
        catch (Exception e){
            rc.setIndicatorString("Error: " + e.toString());
        }
    }
}
