package crusader;

import battlecode.common.*;
import scala.Int;

import java.util.ArrayList;
import java.util.Random;

import static crusader.Pathfinding.goToPosition;
import static crusader.Communication.getHeadquarters;
import static crusader.Strategy.*;

public class Launcher {

    //static int radioArray;
    private static ArrayList<HeadquarterEntity> hqLocations = new ArrayList<>();
    private static Integer exploreID;
    private static Integer botID;
    private static Integer spawnX;
    private static Integer spawnY;

    private static MapLocation saveSpace;

    //For attacks and exploring
    private static Integer armyDivider = 2;

    private static int protectForXMoves = 0;


    static void runLauncher(RobotController rc) throws GameActionException {

        //Creation process
        if (exploreID == null) {
            //ToDo: Change and coordinate with HQ shared array
            exploreID = new Random().nextInt(9);
            System.out.println("Rnd: " + Integer.toString(exploreID));
            //Should return a somewhat unique number
            botID = rc.getRobotCount() + rc.getRoundNum();
            spawnX = rc.getLocation().x;
            spawnY = rc.getLocation().y;
            saveSpace = rc.getLocation();
            Direction d = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2).directionTo(rc.getLocation());
            for(int z = 0; z<10; z++){
                saveSpace = saveSpace.add(d);
            }
        }

        if(hqLocations.size()==0) {
            //Set the local hq Positions
            hqLocations=getHeadquarters(rc);
        }

        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            //Attack first Launcher
            RobotInfo target=enemies[0];
            int lowestHealth = 400;

            for(RobotInfo info :enemies) {

                //Attack Launcher with lowest health first.
                if (info.getType()==RobotType.LAUNCHER||info.getType()==RobotType.DESTABILIZER) {
                    if(info.getHealth() < lowestHealth) {
                        lowestHealth = info.getHealth();
                        target = info;
                    }
                }
            }
            MapLocation toAttack = target.location;
            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");
                rc.attack(toAttack);
            } else {
                //goToPosition(rc,toAttack);
            }
        }

        //Move with the pathfinding module
        //ToDo: Temporary exploring function for testing
        try {
           /*if (rc.getRoundNum() < 200) {
                explore(rc, exploreID);

            } else {*/
            //defendHQ(rc);

            /*ToDo: Create function to check if we are dominating the center,
            currently just hardcode spreading, integrate communication for coordinated attack
             */

            if(rc.getRoundNum()/10 + 10 > rc.getRobotCount() && rc.getRoundNum() < 600 && rc.getRoundNum() > 150 && protectForXMoves == 0) {
                //Protect HQ, because the center is lost and we will save the robots
                protectForXMoves = 300 -  (rc.getRoundNum()-100);
            }

            if(protectForXMoves > 0){
                protectForXMoves --;
                protectHQ(rc, saveSpace.x, saveSpace.y, 3);
                rc.setIndicatorString("Protect HQ for " + Integer.toString(protectForXMoves) + "moves.");
                if(protectForXMoves == 0){
                    protectForXMoves = -1;
                }
            }
            else if(protectForXMoves < 0){
                protectForXMoves --;
                attack(rc, exploreID);
                rc.setIndicatorString("Attack explore");
                if(protectForXMoves < -200){
                    protectForXMoves = 0;
                }
            }
            else if(rc.getRoundNum() > 450 && rc.getRoundNum() < 650 ||
                    rc.getRoundNum() > 1000 && rc.getRoundNum() < 1100 ||
                    rc.getRoundNum() > 1300 && rc.getRoundNum() < 1400){
                if(botID % armyDivider == 1){
                    rc.setIndicatorString("Attack explore");
                    attack(rc, exploreID);
                }
                else{
                    rc.setIndicatorString("Attack center");
                    goToPosition(rc, rc.getMapHeight()/2,rc.getMapWidth()/2);
                }

            }
            else{
                rc.setIndicatorString("Attack center");
                goToPosition(rc, rc.getMapHeight()/2,rc.getMapWidth()/2);
            }


            //returnToHomeBase(rc, hqLocations.get(0).x, hqLocations.get(0).y);

            //}
        } catch (GameActionException e) {
            System.out.println(rc.getType() + " Exception");
            e.printStackTrace();
        }
    }

    private static void defendHQ(RobotController rc) throws GameActionException{

    }
}
