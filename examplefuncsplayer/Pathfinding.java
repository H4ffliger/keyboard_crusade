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

    public static Direction returnToHomeBase (RobotController rc, int baseID) throws GameActionException{

        MapLocation currentLocation = new MapLocation(rc.getLocation().x, rc.getLocation().y);

        int tx = 20, ty = 20;

        return currentLocation.directionTo(new MapLocation(tx, ty));

        //Calculate the direction in which the agent should move
        //rc.getLocation(baseID);


    }


}
