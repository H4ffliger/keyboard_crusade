package crusader;

import battlecode.common.*;
import scala.Int;

import javax.print.attribute.standard.Destination;
import java.util.ArrayList;
import java.util.Random;

import static crusader.Pathfinding.goToPosition;
import static crusader.Communication.getHeadquarters;
import static crusader.Pathing.moveTowards;
import static crusader.Strategy.*;

public class Launcher {

    //static int radioArray;
    private static ArrayList<HeadquarterEntity> hqLocations = new ArrayList<>();
    private static Integer exploreID;
    private static Integer botID;
    private static Integer spawnX;
    private static Integer spawnY;

    private static Integer alive = 0;

    private static MapLocation saveSpace;
    private static MapLocation enemySpace;

    //For attacks and exploring
    private static Integer armyDivider = 4;

    private static int protectForXMoves = 0;

    private static int earlyGameRush = 0;
    private static int getEarlyGameRushHQCheck;

    private static boolean campFlg = false;

    private static boolean protectHomeFlg = false;

    private static MapLocation toAttack;
    private static int toAttackFollowCooldown = 0;


    static void runLauncher(RobotController rc) throws GameActionException {

        //Creation process
        if (exploreID == null) {
            //ToDo: Change and coordinate with HQ shared array
            exploreID = new Random().nextInt(9);
            //System.out.println("Rnd: " + Integer.toString(exploreID));
            //Should return a somewhat unique number
            botID = rc.getRobotCount() + rc.getRoundNum();
            spawnX = rc.getLocation().x;
            spawnY = rc.getLocation().y;
            saveSpace = rc.getLocation();
            Direction dS = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2).directionTo(rc.getLocation());
            for (int z = 0; z < 10; z++) {
                saveSpace = saveSpace.add(dS);
            }
            MapLocation tempLocation = rc.getLocation();
            enemySpace = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
            for (int z = rc.getMapHeight() / 4 + rc.getMapWidth() / 4; z >= 0; z--) {
                Direction dE = tempLocation.directionTo(new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2));
                tempLocation= tempLocation.add(dE);
                enemySpace = enemySpace.add(dE);
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

            //If there are no launchers attack Carriers
            if(target == enemies[0] && target.getType() != RobotType.LAUNCHER){
                for(RobotInfo info :enemies) {

                    if (info.getType()==RobotType.CARRIER||info.getType()==RobotType.AMPLIFIER||info.getType()==RobotType.BOOSTER) {
                        if(info.getHealth() < lowestHealth) {
                            lowestHealth = info.getHealth();
                            target = info;
                        }
                    }
                }
            }
            toAttack = target.location;
            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");
                rc.attack(toAttack);
                toAttackFollowCooldown =3;
            } else {
                //goToPosition(rc,toAttack);
            }
        }

        //Move with the pathfinding module
        //ToDo: Temporary exploring function for testing
        try {
            alive ++;
           /*if (rc.getRoundNum() < 200) {
                explore(rc, exploreID);

            } else {*/
            //defendHQ(rc);

            /*ToDo: Create function to check if we are dominating the center,
            currently just hardcode spreading, integrate communication for coordinated attack
            ToDO: There are currently false positive retreats
             */

            if(rc.getRoundNum() < 1000 && rc.senseNearbyRobots(20, rc.getTeam()).length > 2+(rc.getMapHeight()+rc.getMapWidth())/20  && alive > 10 && earlyGameRush == 0/*|| (rc.getRoundNum() < 10 && earlyGameRush == 0)*/ && protectHomeFlg == false){
                earlyGameRush = 300 + (rc.getMapHeight()+rc.getMapWidth())*2;
                getEarlyGameRushHQCheck = 20+(rc.getMapWidth()+ rc.getMapHeight())/2;
                rc.setIndicatorString("Rush decision");
            }
            //Early rush
            else if (earlyGameRush > 0) {
                if(earlyGameRush == 150){
                    getEarlyGameRushHQCheck = 10+(rc.getMapWidth()+ rc.getMapHeight())/3;
                }
                if(campFlg == false){
                    getEarlyGameRushHQCheck --;
                    if(getEarlyGameRushHQCheck <= 0){
                        earlyGameRush -= 10;
                    }
                }

                if(campFlg == false){
                    earlyGameRush --;
                }

                if(earlyGameRush > 150){
                    RobotInfo tRinfo[] = rc.senseNearbyRobots(15, rc.getTeam().opponent());
                    for (RobotInfo robot: tRinfo) {
                        if(robot.getType().equals(RobotType.HEADQUARTERS)){
                            if(robot.getLocation().distanceSquaredTo(rc.getLocation())< 13) {
                                System.out.println("camping");
                                getEarlyGameRushHQCheck = 30;
                                campFlg = true;
                            }
                        }
                    }
                    if(botID % armyDivider >= 1) {
                        attack(rc, enemySpace.x, enemySpace.y, 4);
                        rc.setIndicatorString("Early rush diagonal");
                    }
                    else{
                        rc.setIndicatorString("Attack center");
                        goToPosition(rc, rc.getMapWidth() / 2, rc.getMapHeight() / 2);
                    }
                }
                else{
                    if(botID % armyDivider >= 2) {
                        attack(rc, enemySpace.x, spawnY, 4);
                        rc.setIndicatorString("Early rush on enemySpace X");
                    }
                    else{
                        attack(rc, spawnX, enemySpace.y, 4);
                        rc.setIndicatorString("Early rush on enemySpace Y");
                    }
                }

            }
            /*else if(rc.getRoundNum()/10  +(rc.getMapWidth()+rc.getMapHeight())/10 > rc.getRobotCount() && rc.getRoundNum() < 600 && rc.getRoundNum() > 60 && protectForXMoves == 0) {
                //Protect HQ, because the center is lost and we will save the robots
                protectForXMoves = 300 -  (rc.getRoundNum()-100);
                rc.setIndicatorString("Fall back decision");
            }

            else if(protectForXMoves > 0){
                protectForXMoves --;
                protectHQ(rc, spawnX, spawnY, 3);
                rc.setIndicatorString("Protect HQ for " + Integer.toString(protectForXMoves) + "moves.");
                if(protectForXMoves == 0){
                    protectForXMoves = -1;
                }
            }
            else if(protectForXMoves < 0){
                protectForXMoves --;
                if(botID % armyDivider >= 1) {
                    attack(rc, enemySpace.x, enemySpace.y);
                    rc.setIndicatorString("Attack enemy space at X: " +
                            Integer.toString(enemySpace.x) + " Y: " + Integer.toString(enemySpace.y));
                }
                else {
                    attack(rc, exploreID);
                    rc.setIndicatorString("Attack explore");
                }
                if(protectForXMoves < -200 + (rc.getMapHeight()+rc.getMapWidth())*3){
                    protectForXMoves = 0;
                }
            }

             */
            else if(rc.getRoundNum() > 1200 && rc.getRoundNum() < 1500 ||
                    rc.getRoundNum() > 1800 && rc.getRoundNum() < 2000){
                if(botID % armyDivider >= 3){
                    rc.setIndicatorString("Attack explore");
                    attack(rc, exploreID);
                }
                else if(botID % armyDivider == 2){
                    //System.out.println("TTTT");
                    attack(rc, enemySpace.x, enemySpace.y);
                    rc.setIndicatorString("Attack enemy space at X: " +
                            Integer.toString(enemySpace.x) + " Y: " + Integer.toString(enemySpace.y));
                }
                else{
                    if(rc.getRoundNum() > 120 + (rc.getMapHeight()+rc.getMapHeight())/2){
                        if(botID % armyDivider >= 1) {
                            rc.setIndicatorString("Attack center");
                            goToPosition(rc, rc.getMapWidth() / 2, rc.getMapHeight() / 2);
                        }
                        else{
                            rc.setIndicatorString("Protecting Home");
                            protectHomeFlg = true;
                            toAttackFollowCooldown --;
                            if(toAttackFollowCooldown > 0){
                                moveTowards(rc, toAttack);
                            }
                            else {
                                attack(rc, spawnX, spawnY, 6);
                            }
                        }
                    }
                    else{
                        rc.setIndicatorString("Attack center");
                        goToPosition(rc, rc.getMapWidth() / 2, rc.getMapHeight() / 2);
                    }

                }

            }
            else{
                if(rc.getRoundNum() > 120 + (rc.getMapHeight()+rc.getMapHeight())/2){
                    if(botID % armyDivider >= 1) {
                        rc.setIndicatorString("Attack center");
                        goToPosition(rc, rc.getMapWidth() / 2, rc.getMapHeight() / 2);
                    }
                    else{
                        rc.setIndicatorString("Protecting Home");
                        protectHomeFlg = true;
                        toAttackFollowCooldown --;
                        if(toAttackFollowCooldown > 0){
                            moveTowards(rc, toAttack);
                        }
                        else {
                            attack(rc, spawnX, spawnY, 6);
                        }
                    }
                }
                else{
                    rc.setIndicatorString("Attack center");
                    goToPosition(rc, rc.getMapWidth() / 2, rc.getMapHeight() / 2);
                }
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
