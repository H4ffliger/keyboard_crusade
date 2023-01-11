package examplefuncsplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.MapLocation;

public class Pathfinding {

    public Direction pathfinding (RobotController rc) throws GameActionException{

        return Direction.EAST;

    }

    public Direction returnToHomeBase (RobotController rc, int baseID) throws GameActionException{

        MapLocation currentLocation = new MapLocation(rc.getLocation().x, rc.getLocation().y);


        //Calculate the direction in which the agent should move
        //rc.getLocation(baseID);


        return Direction.EAST;

    }


}
