package examplefuncsplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.MapLocation;

public class Pathfinding {

    //public void pathfinding(){}

    public Direction pathfinding (RobotController rc) throws GameActionException{

        return Direction.EAST;

    }

    public static Direction returnToHomeBase (RobotController rc, int tx, int ty) throws GameActionException{
        return goToPosition(rc, tx, ty);
    }

    public static Direction goToPosition (RobotController rc, int tx, int ty) throws GameActionException{

        MapLocation currentLocation = new MapLocation(rc.getLocation().x, rc.getLocation().y);

        Direction oTDirection = currentLocation.directionTo(new MapLocation(tx, ty));
        Direction tDirection;


        if(rc.canMove(oTDirection)) {
            return oTDirection;
        }
        else if (rc.canMove(oTDirection.rotateLeft())) {
            return oTDirection.rotateLeft();
        }
        else if (rc.canMove(oTDirection.rotateRight())) {
            return oTDirection.rotateLeft();
        }
        tDirection = oTDirection.rotateRight();
        //First concept for not getting stuckd directly
        for(int z = 0; z < 3; z++){
            tDirection = tDirection.rotateRight();
            if(rc.canMove(tDirection)){
                return tDirection;
            }
        }

        return null;
    }


}
