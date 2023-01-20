package crusader;

import battlecode.common.*;
import scala.Int;

import java.util.ArrayList;
import java.util.Random;

import static crusader.Pathfinding.goToPosition;
import static crusader.Communication.getHeadquarters;
import static crusader.Strategy.explore;

public class Launcher {

    //static int radioArray;
    private static ArrayList<HeadquarterEntity> hqLocations = new ArrayList<>();
    private static Integer exploreID;
    private static Integer botID;

    //For attacks and exploring
    private static Integer armyDivider = 2;


    static void runLauncher(RobotController rc) throws GameActionException {

        //Creation process
        if (exploreID == null) {
            //ToDo: Change and coordinate with HQ shared array
            exploreID = new Random().nextInt(9);
            System.out.println("Rnd: " + Integer.toString(exploreID));
            //Should return a somewhat unique number
            botID = rc.getRobotCount() + rc.getRoundNum();

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
            if(rc.getRoundNum() > 250 && rc.getRoundNum() < 450 ||
                    rc.getRoundNum() > 700 && rc.getRoundNum() < 800 ||
                    rc.getRoundNum() > 1100 && rc.getRoundNum() < 1400){
                if(botID % armyDivider == 1){
                    rc.setIndicatorString("Attack explore");
                    explore(rc, exploreID);
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
