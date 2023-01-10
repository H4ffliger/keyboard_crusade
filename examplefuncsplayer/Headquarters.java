package examplefuncsplayer;

import battlecode.common.*;

import static examplefuncsplayer.RobotPlayer.*;

public class Headquarters {

    static void runHeadquarters(RobotController rc) throws GameActionException {
        if(turnCount<2) {
            initiateArray(rc);
        }
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);
        if (rc.canBuildAnchor(Anchor.STANDARD)) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
            //rc.setIndicatorString("Building anchor! " + rc.getAnchor());
        }
        if (rng.nextBoolean()) {
            // Let's try to build a carrier.
            //rc.setIndicatorString("Trying to build a carrier");
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.buildRobot(RobotType.CARRIER, newLoc);
            }
        } else {
            // Let's try to build a launcher.
            //rc.setIndicatorString("Trying to build a launcher");
            if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }
        }
    }

    private static void initiateArray(RobotController rc) throws GameActionException{
        for (int i=0;i<4;i++) {
            if(rc.readSharedArray(i)==0) {
                int x = rc.getLocation().x;
                int y = rc.getLocation().y;
                String xString,yString;
                if (x<10) {
                    xString = "0" + x;
                } else {
                    xString = Integer.toString(x);
                }
                if (y<10) {
                    yString = "0" + y;
                } else {
                    yString = Integer.toString(y);
                }
                String ownInformationIntegerString = 1 + xString + yString;
                int ownInformation = Integer.parseInt(ownInformationIntegerString);
                rc.writeSharedArray(i,ownInformation);
                rc.setIndicatorString("HQ is here: " + ownInformationIntegerString);
            }
        }

    }
}
