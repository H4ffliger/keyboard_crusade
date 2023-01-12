package examplefuncsplayer;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import java.util.Random;

import static examplefuncsplayer.Launcher.exploreID;
import static examplefuncsplayer.Pathfinding.goToPosition;

public class Strategy {

    static int eOffsetX;
    static int eOffsetY;

    //Amount of rounds until the agent changes the explore offset again.
    static int exploreSoftness = 13;

    /*
mapFieldID for example 9 or 16 for the amount of subfields of the map
 */
    static void explore(RobotController rc, int mapFieldID) throws GameActionException {
        int mapH = rc.getMapHeight();
        int mapW = rc.getMapWidth();
        int mapSize = mapH / 6;


        //Random exploring
        Random rnd = new Random(rc.getRoundNum()+exploreID);
        if(rc.getRoundNum() % exploreSoftness == 0){
            eOffsetX = rnd.nextInt(exploreSoftness*2)-exploreSoftness;
            eOffsetY = rnd.nextInt(exploreSoftness*2)-exploreSoftness;
            System.out.println("Changed the eOffsetX: of "+Integer.toString(exploreID) + ": " +Integer.toString(eOffsetX) +" | Offset " + Integer.toString(eOffsetY));
        }


        int tX = mapFieldID / 3 * mapW / 3 + mapSize + eOffsetX;
        int tY = mapFieldID % 3 * mapH / 3 + mapSize + eOffsetY;
        //System.out.println("mapFieldID: " + Integer.toString(mapFieldID) + " | tX: " + Integer.toString(tX) + " | tY" + Integer.toString(tY));

        try {
            rc.move(goToPosition(rc, tX, tY));
        }
        catch (Exception ignore){}

    }
}
