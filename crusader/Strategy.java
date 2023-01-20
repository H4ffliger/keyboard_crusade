package crusader;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.WellInfo;

import java.util.ArrayList;
import java.util.Random;

import static crusader.Pathfinding.goToPosition;

public class Strategy {

    private static ArrayList<MapLocation> localWells = new ArrayList<>();
    private static ArrayList<MapLocation> localIslands = new ArrayList<>();
    private static int eOffsetX;
    private static int eOffsetY;
    //Amount of rounds until the agent changes the explore offset again.
    private static int exploreSoftness = 13;

    /*
mapFieldID for example 9 or 16 for the amount of subfields of the map
 */
    static void explore(RobotController rc, int mapFieldID) throws GameActionException {
        int mapH = rc.getMapHeight();
        int mapW = rc.getMapWidth();
        int mapSize = mapH / 6;


        //Random exploring
        Random rnd = new Random(rc.getRoundNum() + mapFieldID);
        if (rc.getRoundNum() % exploreSoftness == 0) {
            eOffsetX = rnd.nextInt(exploreSoftness * 2) - exploreSoftness;
            eOffsetY = rnd.nextInt(exploreSoftness * 2) - exploreSoftness;
            //System.out.println("Changed the eOffsetX: of " + mapFieldID + ": " + eOffsetX + " | Offset " + eOffsetY);
        }
        int tX = mapFieldID / 3 * mapW / 3 + mapSize + eOffsetX;
        int tY = mapFieldID % 3 * mapH / 3 + mapSize + eOffsetY;
        //System.out.println("mapFieldID: " + Integer.toString(mapFieldID) + " | tX: " + Integer.toString(tX) + " | tY" + Integer.toString(tY));

        senseInformation(rc);
        goToPosition(rc, tX, tY);
    }


    static void attack(RobotController rc, int mapFieldID) throws GameActionException {
        int mapH = rc.getMapHeight();
        int mapW = rc.getMapWidth();
        int mapSize = mapH / 6;


        //Random exploring
        Random rnd = new Random(rc.getRoundNum());
        if (rc.getRoundNum() % exploreSoftness == 0) {
            eOffsetX = rnd.nextInt(exploreSoftness * 2) - exploreSoftness;
            eOffsetY = rnd.nextInt(exploreSoftness * 2) - exploreSoftness;
            //System.out.println("Changed the eOffsetX: of " + mapFieldID + ": " + eOffsetX + " | Offset " + eOffsetY);
        }
        int tX = mapFieldID / 3 * mapW / 3 + mapSize + eOffsetX;
        int tY = mapFieldID % 3 * mapH / 3 + mapSize + eOffsetY;
        //System.out.println("mapFieldID: " + Integer.toString(mapFieldID) + " | tX: " + Integer.toString(tX) + " | tY" + Integer.toString(tY));

        senseInformation(rc);
        goToPosition(rc, tX, tY);
    }


    static void protectHQ(RobotController rc, int spawnX,int spawnY, int protectionRange) throws GameActionException {

        //Random exploring

        if(rc.getLocation().distanceSquaredTo( new MapLocation(spawnX, spawnY)) > protectionRange){
            goToPosition(rc, spawnX, spawnY);
        }
        else{
            Random rnd = new Random(rc.getRoundNum());
            if (rc.getRoundNum() % exploreSoftness == 0) {
                eOffsetX = rnd.nextInt(exploreSoftness * 2) - exploreSoftness;
                eOffsetY = rnd.nextInt(exploreSoftness * 2) - exploreSoftness;
                //System.out.println("Changed the eOffsetX: of " + mapFieldID + ": " + eOffsetX + " | Offset " + eOffsetY);
            }
            int tX = rc.getLocation().x + eOffsetX;
            int tY = rc.getLocation().y + eOffsetY;
            goToPosition(rc, tX, tY);
        }
        //System.out.println("mapFieldID: " + Integer.toString(mapFieldID) + " | tX: " + Integer.toString(tX) + " | tY" + Integer.toString(tY));
        //senseInformation(rc);
    }







        static void senseInformation(RobotController rc) throws GameActionException {
        WellInfo[] wells = rc.senseNearbyWells();
        //System.out.println("Sensing information--");
        /*
        int[] islandsIndex = rc.senseNearbyIslands();
        MapLocation islandLocation;
        for (int island:islandsIndex) {
            try {
                islandLocation = rc.senseNearbyIslandLocations(islandsIndex);
                localIslands.contains(islands)
            } catch (GameActionException e) {
                e.printStackTrace();

                System.out.println("Sensing information++");
            }
            }
         */

        for (int x = wells.length - 1; x >= 0; x--) {
            if (!localWells.contains(wells[x].getMapLocation())) {
                localWells.add(wells[x].getMapLocation());
                //System.out.println("localWell added to localWell list!");
                //System.out.println(localWells);
            }
        }
        /*
        for(int x = islands.length-1; x >=0; x--){

            if (!localIslands.contains(new MapLocation(islands[x].x, islands[x].y))) {
                localIslands.add(new MapLocation(islands[x].x, islands[x].y));
                System.out.println("localIsland added to localIsland list!");
                System.out.println(localIslands);
        }
    }*/
    }

    //rc.senseNearbyIslands();
    //rc.senseNearbyRobots();
}

