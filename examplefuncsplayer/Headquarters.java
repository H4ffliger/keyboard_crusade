package examplefuncsplayer;

import battlecode.common.*;

import static examplefuncsplayer.Communication.addHeadquarter;
import static examplefuncsplayer.RobotPlayer.*;

public class Headquarters {
    //TODO: Do limits dynamically
    //TODO: Make better strategy
    private static int producedCarrier = 0;
    private static int carrierLimit = 20;

    private static int producedLauncher = 0;
    private static int launcherLimit = 1117;

    static void runHeadquarters(RobotController rc) throws GameActionException {
        if (turnCount == 1) {
            writeLocationToArray(rc);
            usageOfFirstDistance(rc);
        }
        if (rc.canBuildAnchor(Anchor.STANDARD) && rc.getRoundNum() > 50) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
            //rc.setIndicatorString("Building anchor! " + rc.getAnchor());
        }

        // Let's try to build a carrier.
        //rc.setIndicatorString("Trying to build a carrier");
        for (Direction dirLoop : Direction.allDirections()) {
            MapLocation buildPlace = rc.getLocation().add(dirLoop);
            if ((rc.canBuildRobot(RobotType.CARRIER, buildPlace)) && (producedCarrier < carrierLimit)) {
                rc.buildRobot(RobotType.CARRIER, buildPlace);
                producedCarrier++;

            } else if (rc.canBuildRobot(RobotType.LAUNCHER, buildPlace) && (producedLauncher < launcherLimit)) {
                rc.buildRobot(RobotType.LAUNCHER, buildPlace);
                launcherLimit++;
            }
        }
    }

    private static void writeLocationToArray(RobotController rc) throws GameActionException {
        addHeadquarter(rc);
        System.out.println("Write my location");
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
