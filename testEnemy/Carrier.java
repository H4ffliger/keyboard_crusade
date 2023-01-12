package testEnemy;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static testEnemy.RobotPlayer.directions;
import static testEnemy.RobotPlayer.rng;

public class Carrier {

    static ArrayList<MapLocation> hqLocations = new ArrayList<>();

    static ArrayList<MapLocation> adWells = new ArrayList<>();
    static ArrayList<MapLocation> manaWells = new ArrayList<>();
    static int goal = 1, globalIndex = 0;

    static void runCarrier(RobotController rc) throws GameActionException {

        getImportantLocations(rc);
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
        if (goal == 1) {
            WellInfo[] nearbyWells = rc.senseNearbyWells();
            if (nearbyWells.length > 0) {
                MapLocation nearestWell = nearbyWells[0].getMapLocation();
                if (me.isAdjacentTo(nearestWell) || me.distanceSquaredTo(nearestWell) == 1) {
                    if (rc.canCollectResource(nearestWell, -1)) {
                        rc.collectResource(nearestWell, -1);
                        rc.setIndicatorString("Collecting, now have, AD:" +
                                rc.getResourceAmount(ResourceType.ADAMANTIUM) +
                                " MN: " + rc.getResourceAmount(ResourceType.MANA) +
                                " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                    }
                } else {
                    Direction dir = me.directionTo(nearestWell);
                    if (rc.canMove(dir)) {
                        rc.setIndicatorString("Move to nearest Well");
                        rc.move(dir);
                    }
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
                            rc.setIndicatorString("Collecting, now have, AD:" +
                                    rc.getResourceAmount(ResourceType.ADAMANTIUM) +
                                    " MN: " + rc.getResourceAmount(ResourceType.MANA) +
                                    " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                        }
                    } else {
                        Direction dir = me.directionTo(nearestWell);
                        if (rc.canMove(dir)) {
                            rc.setIndicatorString("Move to nearest Well");
                            rc.move(dir);
                        }
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

            // Try to gather from squares around us.
            /*for (int dx = -1; dx <= 1; dx++) {
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
            }*/
            if (rc.getResourceAmount(ResourceType.ADAMANTIUM) + rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ELIXIR) == 40) {
                goal = 2;
            }
        } else if (goal == 2) {
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
                        if (rc.canTransferResource(bestHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM))) {
                            rc.transferResource(bestHQ, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM));

                        }
                        if (rc.canTransferResource(bestHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA))) {
                            rc.transferResource(bestHQ, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA));
                           }
                        if (rc.canTransferResource(bestHQ, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR))) {
                            rc.transferResource(bestHQ, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR));
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
                goal = 1;
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
}
