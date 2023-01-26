package crusader;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.MapLocation;

import static crusader.Pathing.moveTowards;
import static crusader.Pathing.moveTowardsCarrierStuck;

public class Pathfinding {

    //public void pathfinding(){}

    /*public Direction pathfinding (RobotController rc) throws GameActionException{

        return Direction.EAST;

    }*/

    public static void returnToHomeBase(RobotController rc, int tx, int ty) throws GameActionException {
        goToPosition(rc, new MapLocation(tx, ty));
    }

    //ToDo: Do not move if on position
    public static void goToPosition(RobotController rc, int tx, int ty) throws GameActionException {
        goToPosition(rc, new MapLocation(tx, ty));
    }

    public static void goToPosition(RobotController rc, MapLocation target) throws GameActionException {
        moveTowards(rc, target);
    }

    public static void goToPositionCarrierStuck(RobotController rc, MapLocation target) throws GameActionException {
        moveTowardsCarrierStuck(rc, target);
    }
}