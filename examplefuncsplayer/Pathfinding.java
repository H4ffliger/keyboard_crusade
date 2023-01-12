package examplefuncsplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.MapLocation;

public class Pathfinding {

    //public void pathfinding(){}

    /*public Direction pathfinding (RobotController rc) throws GameActionException{

        return Direction.EAST;

    }*/

    public static void returnToHomeBase (RobotController rc, int tx, int ty) throws GameActionException{
        goToPosition(rc,new MapLocation(tx,ty));
    }

    //ToDo: Do not move if on position
    public static void goToPosition (RobotController rc, int tx, int ty) throws GameActionException{
        goToPosition(rc,new MapLocation(tx,ty));
    }

    public static void goToPosition (RobotController rc, MapLocation target) throws GameActionException{

        MapLocation currentLocation = new MapLocation(rc.getLocation().x, rc.getLocation().y);

        Direction oTDirection = currentLocation.directionTo(target);
        Direction tDirection;


        if(rc.canMove(oTDirection)) {
            rc.move(oTDirection);
        }
        else if (rc.canMove(oTDirection.rotateLeft())) {
            rc.move(oTDirection.rotateLeft());
        }
        else if (rc.canMove(oTDirection.rotateRight())) {
            rc.move(oTDirection.rotateRight());
        }
        else {
            tDirection = oTDirection.rotateRight();
            //First concept for not getting stuckd directly
            for (int z = 0; z < 3; z++) {
                tDirection = tDirection.rotateRight();
                if (rc.canMove(tDirection)) {
                    rc.move(tDirection);
                }
            }
        }
    }



    /*public static void calculatePath (RobotController rc, int tx, int ty) throws GameActionException{
        MapLocation currentLocation = new MapLocation(rc.getLocation().x, rc.getLocation().y);
        //rc.canSenseLocation(0,0)
        //

    }*/


}
