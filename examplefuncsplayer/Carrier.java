package examplefuncsplayer;

import battlecode.common.*;

import java.util.*;

import static examplefuncsplayer.RobotPlayer.*;

public class Carrier {

    static ArrayList<MapLocation> hqLocations = new ArrayList<>();

    static void runCarrier(RobotController rc) throws GameActionException {
        if (turnCount < 2) {
            rc.setIndicatorString("Initiate HQ");
            initiateHQ(rc);
        }
        if (rc.getAnchor() != null) {
            // If I have an anchor singularly focus on getting it to the first island I see
            int[] islands = rc.senseNearbyIslands();
            Set<MapLocation> islandLocs = new HashSet<>();
            for (int id : islands) {
                MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
                islandLocs.addAll(Arrays.asList(thisIslandLocs));
            }
            if (islandLocs.size() > 0) {
                MapLocation islandLocation = islandLocs.iterator().next();
                rc.setIndicatorString("Moving my anchor towards " + islandLocation);
                while (!rc.getLocation().equals(islandLocation)) {
                    Direction dir = rc.getLocation().directionTo(islandLocation);
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                    }
                }
                if (rc.canPlaceAnchor()) {
                    rc.setIndicatorString("Huzzah, placed anchor!");
                    rc.placeAnchor();
                }
            }
        }

        MapLocation me = rc.getLocation();

        // If we can see a well, move towards it
        WellInfo[] wells = rc.senseNearbyWells();
        if (wells.length > 1) {
            WellInfo well_one = wells[1];
            Direction dir = me.directionTo(well_one.getMapLocation());
            if (rc.canMove(dir))
                rc.move(dir);
        }

        // Try to gather from squares around us.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation wellLocation = new MapLocation(me.x + dx, me.y + dy);
                if (rc.canCollectResource(wellLocation, -1)) {
                    rc.collectResource(wellLocation, -1);
                    rc.setIndicatorString("Collecting, now have, AD:" +
                            rc.getResourceAmount(ResourceType.ADAMANTIUM) +
                            " MN: " + rc.getResourceAmount(ResourceType.MANA) +
                            " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                }
            }
        }

        //If full, go to the nearest HQ
        if (30 < rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR)) {
            if (hqLocations != null) {
                int minimalDistance = 1000;
                MapLocation bestHQ = null;
                for (MapLocation hq : hqLocations) {
                    int actualDistance = me.distanceSquaredTo(hq);
                    if (actualDistance < minimalDistance) {
                        minimalDistance = actualDistance;
                        bestHQ = hq;
                    }
                }
               /* rc.setIndicatorString("HQ: " + Objects.requireNonNull(bestHQ));
                if (rc.canSenseRobotAtLocation(bestHQ)) {
                    rc.transferResource(bestHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM));
                    rc.transferResource(bestHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA));
                    rc.transferResource(bestHQ, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR));
                } else {
                    Direction dir = me.directionTo(bestHQ);
                    if (rc.canMove(dir)) {
                        rc.move(dir);
                    }
                }*/
            } else {
                rc.setIndicatorString("HQ is null!");
            }
        }


        // Occasionally try out the carriers attack
        /*if (rng.nextInt(20) == 1) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            if (enemyRobots.length > 0) {
                if (rc.canAttack(enemyRobots[0].location)) {
                    rc.attack(enemyRobots[0].location);
                }
            }
        }*/


        // Also try to move randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    private static void initiateHQ(RobotController rc) throws GameActionException {
        for (int i = 0; i < 4; i++) {
            int hqLocation = rc.readSharedArray(i);
            String hqLocationString = Integer.toString(hqLocation);
            String xString = hqLocationString.substring(1, 3);
            String yString = hqLocationString.substring(3, 5);
            int dx = Integer.parseInt(xString);
            int dy = Integer.parseInt(yString);
            hqLocations.add(new MapLocation(dx, dy));
            rc.setIndicatorString("Set HQ to " + hqLocationString);
        }
    }
}
