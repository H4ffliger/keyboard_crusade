package examplefuncsplayer;

import battlecode.common.*;
import scala.Int;

import java.util.ArrayList;

import static examplefuncsplayer.Pathfinding.*;
import static examplefuncsplayer.RobotPlayer.rng;

public class Launcher {

    //static int radioArray;
    static ArrayList<MapLocation> hqLocations = new ArrayList<>();
    static Integer exploreID;
    int eOffsetX;
    int eOffsetY;

    //Amount of rounds until the agent changes the explore offset again.
    int exploreSoftness = 10;

    static void runLauncher(RobotController rc) throws GameActionException {

        //Creation process
        if(exploreID == null){
            //ToDo: Change and coordinate with HQ shared array
            exploreID = rc.getRoundNum()%9;
            //System.out.println("Rnd: " + Integer.toString(exploreID));
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

    /*
    mapFieldID for example 9 or 16 for the amount of subfields of the map
     */
    static void explore(RobotController rc, int mapFieldID) throws GameActionException {
        int mapH = rc.getMapHeight();
        int mapW = rc.getMapWidth();
        int mapSize = mapH / 6;

        //if(rc.getRoundNum()/);

        int tX = mapFieldID / 3 * mapW / 3 + mapSize;
        int tY = mapFieldID % 3 * mapH / 3 + mapSize;
       //System.out.println("mapFieldID: " + Integer.toString(mapFieldID) + " | tX: " + Integer.toString(tX) + " | tY" + Integer.toString(tY));

        try {
            rc.move(goToPosition(rc, tX, tY));
        }
        catch (Exception ignore){}

        }

    }
