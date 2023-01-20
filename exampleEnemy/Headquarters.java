package exampleEnemy;

import battlecode.common.*;

import static exampleEnemy.Communication.*;
import static exampleEnemy.RobotPlayer.turnCount;

public class Headquarters {
    //TODO: Do limits dynamically
    //TODO: Make better strategy

    static void runHeadquarters(RobotController rc) throws GameActionException {
        if (turnCount == 1) {
            writeLocationToArray(rc);
            usageOfFirstDistance(rc);

        } else {
            updateStatus(rc);
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
            if ((rc.canBuildRobot(RobotType.CARRIER, buildPlace))) {
                rc.buildRobot(RobotType.CARRIER, buildPlace);
            } else if (rc.canBuildRobot(RobotType.LAUNCHER, buildPlace)) {
                rc.buildRobot(RobotType.LAUNCHER, buildPlace);
            }
        }
    }

    private static void updateStatus(RobotController rc) {
        int status;

        int recurseManagementSinus = 30;
        int recurseManagement = rc.getResourceAmount(ResourceType.ADAMANTIUM) - rc.getResourceAmount(ResourceType.MANA);
        if (recurseManagement > recurseManagementSinus){
            status = 1;
            //System.out.println("recurseManagement overflow ADAMANTIUM ");
        }
        if (recurseManagement < -recurseManagementSinus){
            status = 0;
            //System.out.println("recurseManagement overflow MANA ");
        }
        else {/*System.out.println("recurseManagement EQUILAIZED!!");*/}

    }

    private static void writeLocationToArray(RobotController rc) throws GameActionException {
        addHeadquarter(rc);
        //System.out.println("Write my location");
    }

    private static void usageOfFirstDistance(RobotController rc) throws GameActionException {
        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            WellEntity wellEntity= new WellEntity(-1,well.getMapLocation());
            if (well.getResourceType()==ResourceType.ADAMANTIUM) wellEntity.setWellStatus(2);
            else wellEntity.setWellStatus(1);
            addWell(rc,wellEntity);
        }
        tryWriteMessages(rc);
    }


}
