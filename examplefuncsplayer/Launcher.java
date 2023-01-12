package examplefuncsplayer;

import battlecode.common.*;

import java.util.ArrayList;

import static examplefuncsplayer.Pathfinding.returnToHomeBase;

public class Launcher {

    //static int radioArray;
    static ArrayList<MapLocation> hqLocations = new ArrayList<>();
    static void runLauncher(RobotController rc) throws GameActionException {


        explore();

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
        try {
            rc.move(returnToHomeBase(rc, hqLocations.get(0).x, hqLocations.get(0).y));
        }
        catch (Exception ignore){}


    }

    /*
    mapFieldID for example 9 or 16 for the amount of subfields of the map
     */
    static void explore(RobotController rc, int mapFieldID){
        int mapH = rc.getMapHeight();
        int mapW = rc.getMapWidth();

        int mapSize = mapH /20;
        int tX, tY;

        for (int z = 9-1; z >= 0; z--){
            tX = z % mapSize;
            tY = z / mapSize;
            System.out.println("Explore map Size: " + Integer.toString(mapSize));
        }

    }

}
