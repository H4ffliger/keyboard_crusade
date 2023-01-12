package testEnemy;

import battlecode.common.*;

import static testEnemy.RobotPlayer.*;

public class Headquarters {

    static void runHeadquarters(RobotController rc) throws GameActionException {
        // Pick a direction to build in.
        Direction dir = directions[rng.nextInt(directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);
        if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
            rc.buildRobot(RobotType.CARRIER, newLoc);
        }
        if (turnCount == 1) {
            writeLocationToArray(rc);
            usageOfFirstDistance(rc);
        }
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

    private static void writeLocationToArray(RobotController rc) throws GameActionException {
        int index = 0;
        while (rc.readSharedArray(index) != 0 || index == 63) {
            //System.out.println("Read to Array: " + index);
            index++;
        }
        if (rc.readSharedArray(index) == 0) {
            int x = rc.getLocation().x;
            int y = rc.getLocation().y;
            String xString, yString;
            if (x < 10) {
                xString = "0" + x;
            } else {
                xString = Integer.toString(x);
            }
            if (y < 10) {
                yString = "0" + y;
            } else {
                yString = Integer.toString(y);
            }
            String ownInformationIntegerString = 1 + xString + yString;
            int ownInformation = Integer.parseInt(ownInformationIntegerString);
            rc.writeSharedArray(index, ownInformation);
            rc.setIndicatorString(index + ". HQ is here: " + ownInformationIntegerString);
        }

    }

    private static void usageOfFirstDistance(RobotController rc) throws GameActionException {
        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            //searches for the first recurse
            int index = 0;
            while (rc.readSharedArray(index) != 0 || index == 63) {
                //System.out.println("Read to Array: " + index);
                index++;
            }
            if (rc.readSharedArray(index) == 0) {
                //Locations of Wells
                int x = well.getMapLocation().x;
                int y = well.getMapLocation().y;
                String xString, yString;
                if (x < 10) {
                    xString = "0" + x;
                } else {
                    xString = Integer.toString(x);
                }
                if (y < 10) {
                    yString = "0" + y;
                } else {
                    yString = Integer.toString(y);
                }
                //tells what ResourceType you have near.
                String ownInformationIntegerString;
                if (well.getResourceType() == ResourceType.ADAMANTIUM) {
                    ownInformationIntegerString = 2 + xString + yString;
                    System.out.println("Found ADAMANTIUM");
                } else if (well.getResourceType() == ResourceType.ELIXIR) {
                    ownInformationIntegerString = 3 + xString + yString;
                    System.out.println("Found ELIXIR");
                } else if (well.getResourceType() == ResourceType.MANA) {
                    ownInformationIntegerString = 4 + xString + yString;
                    System.out.println("Found MANA");
                } else ownInformationIntegerString = "0";
                int ownInformation = Integer.parseInt(ownInformationIntegerString);

                rc.writeSharedArray(index, ownInformation);
                System.out.println("Well Index + ownInformation send");

            }
        }
    }


}
