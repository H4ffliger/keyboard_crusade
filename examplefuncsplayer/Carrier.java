package examplefuncsplayer;

import battlecode.common.*;

import java.util.*;

import static examplefuncsplayer.RobotPlayer.*;

public class Carrier {

    static ArrayList<MapLocation> hqLocations = new ArrayList<>();
    static int goal = 1;

    static void runCarrier(RobotController rc) throws GameActionException {
        if (turnCount == 1) {
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
        if(goal==1) {
            // If we can see a well, move towards it
            WellInfo[] wells = rc.senseNearbyWells();
            if (wells.length > 1) {
                WellInfo well_one = wells[1];
                Direction dir = me.directionTo(well_one.getMapLocation());
                if (rc.canMove(dir))
                    rc.move(dir);
            } else {
                // Also try to move randomly.
                Direction dir = directions[rng.nextInt(directions.length)];
                if (rc.canMove(dir)) {
                    rc.move(dir);
                }
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
            if (rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR) == 40) {
                goal=2;
                System.out.println("Goal = 2");
            }
        } else if(goal == 2) {
            if (hqLocations != null) {
                int minimalDistance = 100000;
                MapLocation bestHQ = null;
                for (MapLocation hq : hqLocations) {
                    int actualDistance = me.distanceSquaredTo(hq);
                    if (actualDistance < minimalDistance) {
                        minimalDistance = actualDistance;
                        bestHQ = hq;
                    }
                }
                if (bestHQ != null) {
                    if (rc.canSenseRobotAtLocation(bestHQ) && me.isAdjacentTo(bestHQ)) {
                        System.out.println("Can act on HQ: ");
                        System.out.println("Can act on HQ: " + rc.canTransferResource(bestHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM)));
                        if (rc.canTransferResource(bestHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM))) {
                            System.out.println("Transfer resource: " + rc.getResourceAmount(ResourceType.ADAMANTIUM));
                            rc.transferResource(bestHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM));

                        }
                        if (rc.canTransferResource(bestHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA))) {
                            rc.transferResource(bestHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA));
                            System.out.println("Transfer resource: " + rc.getResourceAmount(ResourceType.MANA));
                        }
                        if (rc.canTransferResource(bestHQ, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR))) {
                            rc.transferResource(bestHQ, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR));
                            System.out.println("Transfer resource: " + rc.getResourceAmount(ResourceType.ELIXIR));
                        }
                    } else {
                        Direction dir = me.directionTo(bestHQ);
                        if (rc.canMove(dir)) {
                            rc.setIndicatorString("Move to HQ");
                            rc.move(dir);
                        }
                    }
                } else {
                    System.out.println("BestHQ is null");
                }

            } else {
                rc.setIndicatorString("HQ are null!");
            }
            if (rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR) == 0) {
                goal=1;
                System.out.println("Goal = 1");
            }
        } else {
            // Also try to move randomly.
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
            System.out.println("Goal = something else");
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



    }

    private static void initiateHQ(RobotController rc) throws GameActionException {
        int i = 0;
        while (rc.readSharedArray(i) != 0) {
            int hqLocation = rc.readSharedArray(i);
            String hqLocationString = Integer.toString(hqLocation);
            String xString = hqLocationString.substring(1, 3);
            String yString = hqLocationString.substring(3, 5);
            int dx = Integer.parseInt(xString);
            int dy = Integer.parseInt(yString);
            hqLocations.add(new MapLocation(dx, dy));
            //System.out.println("Set HQ to " + hqLocationString);
            i++;
        }
    }
}
