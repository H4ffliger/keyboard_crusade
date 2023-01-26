package crusader;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.MapLocation;

import static crusader.Pathing.*;

public class Pathfinding {

    //public void pathfinding(){}

    /*public Direction pathfinding (RobotController rc) throws GameActionException{

        return Direction.EAST;

    }*/
    private static MapLocation stuckLocation;
    private static int stuckTimer = 10;
    private static int stuckDistance = 20;
    private static int distanceToDestination = 5;
    private static boolean altDirection = false;



    public static void returnToHomeBase(RobotController rc, int tx, int ty) throws GameActionException {
    }


    //ToDo: Do not move if on position
    public static void goToPosition(RobotController rc, int tx, int ty) throws GameActionException {
        processPosition(rc, new MapLocation(tx, ty));
    }

    public static void goToPosition(RobotController rc, MapLocation target) throws GameActionException {
        processPosition(rc, target);
    }


    public static void processPosition(RobotController rc, MapLocation target) throws GameActionException {

        if(stuckLocation == null){
            stuckLocation = rc.getLocation();
        }

        if(rc.getRoundNum() % stuckTimer == 0) {
            if(stuckLocation.distanceSquaredTo(rc.getLocation()) < stuckDistance /*&& distanceToDestination > rc.getLocation().distanceSquaredTo(target)*/){
                altDirection = !altDirection;
            }
            stuckLocation = rc.getLocation();
        }
        if(altDirection){
            rc.setIndicatorLine(rc.getLocation(), stuckLocation, 255, 255, 0);
            moveTowardsAlt(rc, new MapLocation(target.x, target.y));
        }
        else{
            moveTowards(rc, new MapLocation(target.x, target.y));
        }
    }

}