package examplefuncsplayer;

import battlecode.common.*;

import java.util.*;

import static examplefuncsplayer.Communication.addWell;
import static examplefuncsplayer.Communication.getHeadquarters;
import static examplefuncsplayer.Pathfinding.goToPosition;
import static examplefuncsplayer.RobotPlayer.*;
import static examplefuncsplayer.Strategy.explore;

public class Carrier {

    private static ArrayList<HeadquarterEntity> hqLocations = new ArrayList<>();
    private static MapLocation homeHQ = null;

    private static ArrayList<MapLocation> adWells = new ArrayList<>();
    private static ArrayList<WellEntity> adWellEntities = new ArrayList<>();
    private static ArrayList<MapLocation> manaWells = new ArrayList<>();

    private static ArrayList<WellEntity> mnWellEntities = new ArrayList<>();
    private static int goal = 1;

    //TODO: change strategy to place anchor
    private static Integer exploreID;

    static void runCarrier(RobotController rc) throws GameActionException {
        if (turnCount == 1) initialisation(rc);
        MapLocation me = rc.getLocation();
        if (turnCount % 2 == 0) {
            //every second turn sense Wells
            senseWellsAndAddNewOne(rc);
        }
        if (goal == 0) {
            //find new strategy
            if (rc.canTakeAnchor(homeHQ, Anchor.STANDARD)) {
                rc.takeAnchor(homeHQ, Anchor.STANDARD);
                goal = 3;
            } else {
                if (rng.nextInt(3) == 0) {
                    goal = 4;
                } else {
                    goal = 5;
                }
            }

        } else if (goal == 1) {
            rc.setIndicatorString("I go mining");
            goMining(rc, me, ResourceType.MANA);

            if (getTotalResources(rc) == 40) {
                goal = 2;
            }
        } else if (goal == 2) {
            rc.setIndicatorString("I go home");
            returnToHQ(rc, me);
        } else if (goal == 3) {
            rc.setIndicatorString("Me place Anchor");
            placeAnchor(rc, me);
        } else if (goal == 4) {
            rc.setIndicatorString("Me mine Ad");
            goMining(rc, me, ResourceType.ADAMANTIUM);
            if (getTotalResources(rc) == 40) {
                goal = 2;
            }
        } else if (goal == 5) {
            rc.setIndicatorString("Me mine Mn");
            goMining(rc, me, ResourceType.MANA);
            if (getTotalResources(rc) == 40) {
                goal = 2;
            }
        } else {
            // Also try to move randomly.
            Direction dir = directions[rng.nextInt(directions.length)];
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
            System.out.println("Goal = something else");
        }

    }

    private static void senseWellsAndAddNewOne(RobotController rc) throws GameActionException {
        WellInfo[] wellInfos = rc.senseNearbyWells();
        for (WellInfo wellInfo : wellInfos) {
            if (!manaWells.contains(wellInfo.getMapLocation())) {
                manaWells.add(wellInfo.getMapLocation());
                WellEntity addedEntity = new WellEntity(-1, wellInfo.getMapLocation());
                addedEntity.setWellStatus(1);
                mnWellEntities.add(addedEntity);
                addWell(rc, addedEntity);
            } else if (!adWells.contains(wellInfo.getMapLocation())) {
                adWells.add(wellInfo.getMapLocation());
                WellEntity addedEntity = new WellEntity(-1, wellInfo.getMapLocation());
                addedEntity.setWellStatus(2);
                adWellEntities.add(addedEntity);
                addWell(rc, addedEntity);
            }
        }
    }

    //unused
    private static void placeAnchor(RobotController rc, MapLocation me) throws GameActionException {
        if (rc.getAnchor() != null) {
            // If I have an anchor singularly focus on getting it to the first island I see
            int[] islands = rc.senseNearbyIslands();
            Set<MapLocation> islandLocs = new HashSet<>();
            for (int id : islands) {
                if (rc.senseTeamOccupyingIsland(id) != rc.getTeam()) {
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
                } else {
                    explore(rc, exploreID);
                }
            }
        }
    }

    private static void returnToHQ(RobotController rc, MapLocation me) throws GameActionException {
        if (hqLocations != null) {
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
                int minimalDistance = 100000;
                HeadquarterEntity bestHQ = null;
                for (HeadquarterEntity hq : hqLocations) {
                    int actualDistance = me.distanceSquaredTo(hq.getOwnLocation());
                    if (actualDistance < minimalDistance) {
                        minimalDistance = actualDistance;
                        bestHQ = hq;
                    }
                }
                if (bestHQ != null) {
                    goToPosition(rc, bestHQ.getOwnLocation());
                }
                System.out.println("HomeHQ is null");
            }

        } else {
            rc.setIndicatorString("HQ are null!");
        }

    }

    private static void goMining(RobotController rc, MapLocation me, MapLocation target) throws GameActionException {
        if (me.isAdjacentTo(target) || me.distanceSquaredTo(target) == 1) {
            if (rc.canCollectResource(target, -1)) rc.collectResource(target, -1);
        } else {
            goToPosition(rc, target);
            rc.setIndicatorString("Move to nearest Well");
        }
    }

    private static void goMining(RobotController rc, MapLocation me, ResourceType rt) throws GameActionException {
        ArrayList<WellEntity> selectWells = null;
        switch (rt) {
            case NO_RESOURCE:
                break;
            case ADAMANTIUM:
                selectWells = adWellEntities;
                break;
            case MANA:
                selectWells = mnWellEntities;
                break;
            case ELIXIR:
                break;
        }
        if (selectWells != null && selectWells.size() > 0) {
            rc.setIndicatorString("Did find a well");
            int minimalDistance = 100000;
            WellEntity nearestWell = null;
            for (WellEntity oneWell : selectWells) {
                int actualDistance = me.distanceSquaredTo(oneWell.getOwnLocation());
                if (actualDistance < minimalDistance) {
                    minimalDistance = actualDistance;
                    nearestWell = oneWell;
                }
            }
            if (nearestWell != null) {
                rc.setIndicatorString("Going to mine");
                goMining(rc, me, nearestWell.getOwnLocation());
            } else {
                System.out.println("NearestWell is null");
            }

        } else {
            rc.setIndicatorString("Didn't find Well of type: " + rt);
            goMining(rc, me);
        }
    }

    private static void goMining(RobotController rc, MapLocation me) throws GameActionException {
        WellInfo[] nearbyWells = rc.senseNearbyWells();
        for (WellInfo well : nearbyWells) {
            MapLocation wellLoc = well.getMapLocation();
            if (!adWells.contains(wellLoc) || !manaWells.contains(wellLoc)) {
                if (well.getResourceType() == ResourceType.ADAMANTIUM) {
                    adWells.add(wellLoc);
                    adWellEntities.add(new WellEntity(-1, wellLoc));
                } else if (well.getResourceType() == ResourceType.MANA) {
                    manaWells.add(wellLoc);
                    mnWellEntities.add(new WellEntity(-1, wellLoc));
                }
            }
        }
        if (nearbyWells.length > 0) {
            MapLocation nearestWell = nearbyWells[0].getMapLocation();
            goMining(rc, me, nearestWell);

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
                goMining(rc, me, nearestWell);
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
        hqLocations = getHeadquarters(rc);
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots) {
            if (robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.HEADQUARTERS) {
                homeHQ = robot.getLocation();
                break;
            }
        }
        /*while (rc.readSharedArray(globalIndex) != 0) {
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
        }*/
    }

    private static void initialisation(RobotController rc) throws GameActionException {
        getImportantLocations(rc);
    }

    static int getTotalResources(RobotController rc) {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM)
                + rc.getResourceAmount(ResourceType.MANA)
                + rc.getResourceAmount(ResourceType.ELIXIR);
    }
}

