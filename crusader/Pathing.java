package crusader;

import battlecode.common.*;

public class Pathing {
    // Basic bug nav - Bug 0

    static Direction currentDirection = null;

    static void moveTowards(RobotController rc, MapLocation target) throws GameActionException {
        if (rc.getLocation().equals(target)) {
            return;
        }
        if (!rc.isMovementReady()) {
            return;
        }
        Direction d = rc.getLocation().directionTo(target);
        if (rc.canMove(d)) {

            RobotInfo tRinfo[] = rc.senseNearbyRobots(15, rc.getTeam().opponent());
            for (RobotInfo robot: tRinfo) {
                if(robot.getType().equals(RobotType.HEADQUARTERS)){
                    if(robot.getLocation().distanceSquaredTo(rc.getLocation())< 13) {
                        Direction retreatFromHQ = robot.getLocation().directionTo(rc.getLocation());
                        if (rc.canMove(retreatFromHQ)) {
                            rc.move(retreatFromHQ);
                            return;
                        } else if (rc.canMove(retreatFromHQ.rotateRight())) {
                            rc.move(retreatFromHQ.rotateRight());
                            return;

                        } else if (rc.canMove(retreatFromHQ.rotateLeft())) {
                            rc.move(retreatFromHQ.rotateLeft());
                            return;
                        }
                    }
                }
            }
            if(rc.canMove(d)) {
                rc.move(d);
            }
            currentDirection = null; // there is no obstacle we're going around
        } else {
            // Going around some obstacle: can't move towards d because there's an obstacle there
            // Idea: keep the obstacle on our right hand

            if (currentDirection == null) {
                currentDirection = d;
            }
            // Try to move in a way that keeps the obstacle on our right
            for (int i = 0; i < 8; i++) {
                if (rc.canMove(currentDirection)) {
                    rc.move(currentDirection);
                    currentDirection = currentDirection.rotateRight();
                    break;
                } else {
                    currentDirection = currentDirection.rotateLeft();
                }
            }
        }
    }
}
