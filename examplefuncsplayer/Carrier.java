package examplefuncsplayer;

import battlecode.common.*;

import java.util.*;

import static examplefuncsplayer.Pathfinding.goToPosition;
import static examplefuncsplayer.RobotPlayer.*;
import static examplefuncsplayer.Strategy.explore;

public class Carrier {

    private static ArrayList<MapLocation> hqLocations = new ArrayList<>();
    private static MapLocation homeHQ = new MapLocation(1, 1);

    private static ArrayList<MapLocation> adWells = new ArrayList<>();
    private static ArrayList<MapLocation> manaWells = new ArrayList<>();
    private static int goal = 1, globalIndex = 0;

    //TODO: change strategy to place anchor
    private static Integer exploreID;

    static void runCarrier(RobotController rc) throws GameActionException {
        getImportantLocations(rc);
        if (turnCount == 1) initialisation(rc);
        MapLocation me = rc.getLocation();
        if (goal == 0) {
            //find new strategy
            if (rc.canTakeAnchor(homeHQ, Anchor.STANDARD)) {
                rc.takeAnchor(homeHQ, Anchor.STANDARD);
                goal = 3;
            } else {
                goal = 1;
            }
        } else if (goal == 1) {
            rc.setIndicatorString("I go mining");
            goMining(rc, me);

            if (rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR) == 40) {
                goal = 2;
            }
        } else if (goal == 2) {
            rc.setIndicatorString("I go home");
            returnToHQ(rc, me);
        } else if (goal == 3) {
            rc.setIndicatorString("Me place Anchor");
            placeAnchor(rc, me);
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


    //unused
    private static void placeAnchor(RobotController rc, MapLocation me) throws GameActionException {
        if (rc.getAnchor() != null) {
            // If I have an anchor singularly focus on getting it to the first island I see
            int[] islands = rc.senseNearbyIslands();
            Set<MapLocation> islandLocs = new HashSet<>();
            for (int id : islands) {
                if (!rc.senseTeamOccupyingIsland(id).isPlayer()) {
                    MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
                    islandLocs.addAll(Arrays.asList(thisIslandLocs));
                }
            }
            if (islandLocs.size() > 0) {
                int minimalDistance = 100000;
                MapLocation nearestIland = null;
                for (MapLocation island : islandLocs) {
                    int actualDistance = me.distanceSquaredTo(island);
                    if (actualDistance < minimalDistance) {
                        minimalDistance = actualDistance;
                        nearestIland = island;
                    }
                }
                rc.setIndicatorString("Moving my anchor towards " + nearestIland);
                System.out.println("BEFOR WHILE");
                while (!rc.getLocation().equals(nearestIland)) {
                    goToPosition(rc, nearestIland);
                    Clock.yield();
                }
                if (rc.canPlaceAnchor()) {
                    rc.setIndicatorString("Huzzah, placed anchor!");
                    rc.placeAnchor();
                    goal = 2;
                }
            } else {
                if (exploreID == null) {
                    //ToDo: Change and coordinate with HQ shared array
                    exploreID = new Random().nextInt(9);
                    System.out.println("Rnd: " + exploreID);
                } else {
                    explore(rc, exploreID);
                }
            }
        }
    }

    private static void returnToHQ(RobotController rc, MapLocation me) throws GameActionException {
        if (hqLocations != null) {
            /*int minimalDistance = 100000;
            MapLocation bestHQ = null;
            for (MapLocation hq : hqLocations) {
                int actualDistance = me.distanceSquaredTo(hq);
                if (actualDistance < minimalDistance) {
                    minimalDistance = actualDistance;
                    bestHQ = hq;
                }
            }*/
            if (homeHQ != null) {
                if (rc.canSenseRobotAtLocation(homeHQ) && me.isAdjacentTo(homeHQ)) {
                    if (rc.canTransferResource(homeHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM))) {
                        rc.transferResource(homeHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM));

                    }
                    if (rc.canTransferResource(homeHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA))) {
                        rc.transferResource(homeHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA));
                    }
                    if (rc.canTransferResource(homeHQ, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR))) {
                        rc.transferResource(homeHQ, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR));
                    }
                    if (rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR) == 0) {
                        goal = 0;
                    }
                } else {
                    goToPosition(rc, homeHQ);
                    rc.setIndicatorString("Move to HQ");
                }
            } else {
                System.out.println("HomeHQ is null");
            }

        } else {
            rc.setIndicatorString("HQ are null!");
        }

    }

    private static void goMining(RobotController rc, MapLocation me) throws GameActionException {
        WellInfo[] nearbyWells = rc.senseNearbyWells();
        if (nearbyWells.length > 0) {
            MapLocation nearestWell = nearbyWells[0].getMapLocation();
            if (me.isAdjacentTo(nearestWell) || me.distanceSquaredTo(nearestWell) == 1) {
                if (rc.canCollectResource(nearestWell, -1)) {
                    rc.collectResource(nearestWell, -1);
                    rc.setIndicatorString("Collecting, now have, AD:" + rc.getResourceAmount(ResourceType.ADAMANTIUM) + " MN: " + rc.getResourceAmount(ResourceType.MANA) + " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                }
            } else {
                goToPosition(rc, nearestWell);
                rc.setIndicatorString("Move to nearest Well");
            }

        } else if (adWells.size() != 0 || manaWells.size() != 0) {
            int minimalDistance = 100000;
            MapLocation nearestWell = null;
            for (MapLocation adWell : adWells) {
                int actualDistance = me.distanceSquaredTo(adWell);
                if (actualDistance < minimalDistance) {
                    minimalDistance = actualDistance;
                    nearestWell = adWell;
                }
            }
            for (MapLocation manaWell : manaWells) {
                int actualDistance = me.distanceSquaredTo(manaWell);
                if (actualDistance < minimalDistance) {
                    minimalDistance = actualDistance;
                    nearestWell = manaWell;
                }
            }
            if (nearestWell != null) {
                if (me.isAdjacentTo(nearestWell) || me.distanceSquaredTo(nearestWell) == 1) {
                    if (rc.canCollectResource(nearestWell, -1)) {
                        rc.collectResource(nearestWell, -1);
                        rc.setIndicatorString("Collecting, now have, AD:" + rc.getResourceAmount(ResourceType.ADAMANTIUM) + " MN: " + rc.getResourceAmount(ResourceType.MANA) + " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                    }
                } else {
                    goToPosition(rc, nearestWell);
                    rc.setIndicatorString("Move to nearest Well");
                }
            } else {
                System.out.println("NearestWell is null");
            }

        } else {
            rc.setIndicatorString("Wells are null!");
        }
        // Also try to move randomly.
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);

        }
    }

    private static void getImportantLocations(RobotController rc) throws GameActionException {
        while (rc.readSharedArray(globalIndex) != 0) {
            int information = rc.readSharedArray(globalIndex);
            String informationString = Integer.toString(information);
            String firstBitString = informationString.substring(0, 1);
            String xString = informationString.substring(1, 3);
            String yString = informationString.substring(3, 5);
            int firstBit = Integer.parseInt(firstBitString);
            int dx = Integer.parseInt(xString);
            int dy = Integer.parseInt(yString);
            if (firstBit == 1) {
                hqLocations.add(new MapLocation(dx, dy));
            } else if (firstBit == 2) {
                adWells.add(new MapLocation(dx, dy));
            } else if (firstBit == 3) {
                manaWells.add(new MapLocation(dx, dy));
            }
            //System.out.println("Set HQ to " + hqLocationString);
            globalIndex++;
        }
    }

    private static void initialisation(RobotController rc) {
        int minimalDistance = 10;
        for (MapLocation hq : hqLocations) {
            int actualDistance = rc.getLocation().distanceSquaredTo(hq);
            if (actualDistance < minimalDistance) {
                homeHQ = hq;
                System.out.println("Found Home HQ");
                break;
            }
        }
    }
}
