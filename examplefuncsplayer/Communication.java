package examplefuncsplayer;

import battlecode.common.*;
import battlecode.world.Well;

import java.util.ArrayList;
import java.util.List;

import static examplefuncsplayer.RobotPlayer.turnCount;

class Message {
    public int idx;
    public int value;
    public int turnAdded;

    Message(int idx, int value, int turnAdded) {
        this.idx = idx;
        this.value = value;
        this.turnAdded = turnAdded;
    }
}

class Communication {

    private static final int OUTDATED_TURNS_AMOUNT = 30;
    private static final int AREA_RADIUS = RobotType.CARRIER.visionRadiusSquared;

    // Maybe you want to change this based on exact amounts which you can get on turn 1
    static final int STARTING_ISLAND_IDX = GameConstants.MAX_STARTING_HEADQUARTERS;
    static final int STARTING_WELL_IDX = 35 + GameConstants.MAX_STARTING_HEADQUARTERS;
    private static final int MAX_WELLS_IN_ARRAY = 15;
    private static final int STARTING_ENEMY_IDX = STARTING_WELL_IDX + MAX_WELLS_IN_ARRAY;

    private static final int TOTAL_BITS = 16;
    private static final int MAPLOC_BITS = 12;
    private static final int HQ_NEEDED_RESOURCE_BITS = 1;
    private static final int HQ_DEFENSE_BITS = 3;
    private static final int WELL_STATUS_BITS = 2;
    private static final int WELL_DEFENSE_BITS = 2;
    private static final int ISLAND_TEAM_BITS = 1;
    private static final int ISLAND_HEALTH_BITS = 3;
    private static final int ISLAND_HEALTH_SIZE = (int) Math.ceil(Anchor.ACCELERATING.totalHealth / 8.0);

    private static List<Message> messagesQueue = new ArrayList<>();
    private static HeadquarterEntity[] headquarterEntities = new HeadquarterEntity[GameConstants.MAX_STARTING_HEADQUARTERS];

    private static WellEntity[] wellEntities = new WellEntity[MAX_WELLS_IN_ARRAY];


    static void addHeadquarter(RobotController rc) throws GameActionException {
        MapLocation me = rc.getLocation();
        for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
            if (rc.readSharedArray(i) == 0) {
                int writeToSharedArray = locationToInt(rc, me) << HQ_NEEDED_RESOURCE_BITS + HQ_DEFENSE_BITS;
                rc.writeSharedArray(i, writeToSharedArray);
                break;
            }
        }
    }

    static void updateHeadquarterInfo(RobotController rc) throws GameActionException {
        for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
            if (rc.readSharedArray(i) != 0) {
                int readFromSharedArray = rc.readSharedArray(i) >> HQ_NEEDED_RESOURCE_BITS + HQ_DEFENSE_BITS;
                headquarterEntities[i] = (new HeadquarterEntity(i, intToLocation(rc, readFromSharedArray)));
            } else break;
        }
    }

    static ArrayList<HeadquarterEntity> getHeadquarters(RobotController rc) throws GameActionException {
        updateHeadquarterInfo(rc);
        ArrayList<HeadquarterEntity> headquarters = new ArrayList<>();
        for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
            if (headquarterEntities[i] != null) headquarters.add(headquarterEntities[i]);
            else break;
        }
        return headquarters;
    }

    static void tryWriteMessages(RobotController rc) throws GameActionException {
        messagesQueue.removeIf(msg -> msg.turnAdded + OUTDATED_TURNS_AMOUNT < turnCount);
        // Can always write (0, 0), so just checks are we in range to write
        if (rc.canWriteSharedArray(0, 0)) {
            while (messagesQueue.size() > 0) {
                Message msg = messagesQueue.remove(0); // Take from front or back?
                if (rc.canWriteSharedArray(msg.idx, msg.value)) {
                    rc.writeSharedArray(msg.idx, msg.value);
                }
            }
        }
    }

    static void updateIslandInfo(RobotController rc, int id) throws GameActionException {
        if (headquarterEntities[0] == null) {
            return;
        }
        MapLocation closestIslandLoc = null;
        int closestDistance = -1;
        MapLocation[] islandLocs = rc.senseNearbyIslandLocations(id);
        for (MapLocation loc : islandLocs) {
            int distance = headquarterEntities[0].getOwnLocation().distanceSquaredTo(loc);
            if (closestIslandLoc == null || distance < closestDistance) {
                closestDistance = distance;
                closestIslandLoc = loc;
            }
        }
        // Remember reading is cheaper than writing so we don't want to write without knowing if it's helpful
        int idx = id + STARTING_ISLAND_IDX;
        int oldIslandValue = rc.readSharedArray(idx);
        int updatedIslandValue = bitPackIslandInfo(rc, idx, closestIslandLoc);
        if (oldIslandValue != updatedIslandValue) {
            Message msg = new Message(idx, updatedIslandValue, turnCount);
            messagesQueue.add(msg);
        }
    }

    static int bitPackIslandInfo(RobotController rc, int islandId, MapLocation closestLoc) {
        int islandInt = locationToInt(rc, closestLoc);
        islandInt = islandInt << (TOTAL_BITS - MAPLOC_BITS);
        try {
            Team teamHolding = rc.senseTeamOccupyingIsland(islandId);
            islandInt += teamHolding.ordinal() << (TOTAL_BITS - MAPLOC_BITS - ISLAND_TEAM_BITS);
            int islandHealth = rc.senseAnchorPlantedHealth(islandId);
            int healthEncoding = (int) Math.ceil((double) islandHealth / ISLAND_HEALTH_SIZE);
            islandInt += healthEncoding;
            return islandInt;
        } catch (GameActionException e) {
            return islandInt;
        }
    }

    static Team readTeamHoldingIsland(RobotController rc, int islandId) {
        try {
            islandId = islandId + STARTING_ISLAND_IDX;
            int islandInt = rc.readSharedArray(islandId);
            int healthMask = 0b111;
            int health = islandInt & healthMask;
            int team = (islandInt >> ISLAND_HEALTH_BITS) & 0b1;
            if (health > 0) {
                return Team.values()[team];
            }
        } catch (GameActionException ignored) {
        }
        return Team.NEUTRAL;
    }

    static MapLocation readIslandLocation(RobotController rc, int islandId) {
        try {
            islandId = islandId + STARTING_ISLAND_IDX;
            int islandInt = rc.readSharedArray(islandId);
            int idx = islandInt >> (ISLAND_HEALTH_BITS + ISLAND_TEAM_BITS);
            return intToLocation(rc, idx);
        } catch (GameActionException ignored) {
        }
        return null;
    }

    static int readMaxIslandHealth(RobotController rc, int islandId) {
        try {
            islandId = islandId + STARTING_ISLAND_IDX;
            int islandInt = rc.readSharedArray(islandId);
            int healthMask = 0b111;
            int health = islandInt & healthMask;
            return health * ISLAND_HEALTH_SIZE;
        } catch (GameActionException e) {
            return -1;
        }
    }

    static void addWell(RobotController rc, WellEntity well) throws GameActionException {
        int status = -1;
        int index = STARTING_WELL_IDX;
        for (WellEntity wellEntity : wellEntities) {
            if (wellEntity != null) {
                if (wellEntity.equals(well)) {
                    status = 1;
                    wellEntity.setIndex(well.getIndex());
                    break;
                } else {
                    index++;
                }
            } else {
                break;
            }
        }
        int writeToSharedArray = locationToInt(rc, well.getOwnLocation()) << WELL_STATUS_BITS + WELL_DEFENSE_BITS;
        writeToSharedArray += well.getWellStatus() << WELL_DEFENSE_BITS;
        writeToSharedArray += well.getDefenseStatus();
        //System.out.println("Added Well with Location:" + well.getOwnLocation() + " and status: " + well.getWellStatus() + " and defense: " + well.getDefenseStatus());
        if (status != 1) {
            messagesQueue.add(new Message(index, writeToSharedArray, turnCount));
        } else {
            messagesQueue.add(new Message(well.getIndex(), writeToSharedArray, turnCount));
        }
        tryWriteMessages(rc);
    }

    static void updateWell(RobotController rc, WellEntity well) throws GameActionException {
        int writeToSharedArray = locationToInt(rc, well.getOwnLocation()) << WELL_STATUS_BITS + WELL_DEFENSE_BITS;
        writeToSharedArray += well.getWellStatus() << WELL_DEFENSE_BITS;
        writeToSharedArray += well.getDefenseStatus();
        //System.out.println("Added Well with Location:" + well.getOwnLocation() + " and status: " + well.getWellStatus() + " and defense: " + well.getDefenseStatus() + "on index: " + well.getIndex());
        messagesQueue.add(new Message(well.getIndex(), writeToSharedArray, turnCount));
        tryWriteMessages(rc);
    }

    static ArrayList<WellEntity> getAllWells(RobotController rc) throws GameActionException {
        ArrayList<WellEntity> wellEntityArrayList = new ArrayList<>();
        for (int i = STARTING_WELL_IDX; i < MAX_WELLS_IN_ARRAY + STARTING_WELL_IDX; i++) {
            if (rc.readSharedArray(i) != 0) {
                int readFromSharedArray = rc.readSharedArray(i);
                int defenseStatus = readFromSharedArray&0b11;
                int location = readFromSharedArray >> WELL_STATUS_BITS + WELL_DEFENSE_BITS;
                int status = readFromSharedArray >> WELL_DEFENSE_BITS;
                int statusBited = status & 0b11;
                WellEntity newEntity = new WellEntity(i, intToLocation(rc, location));
                newEntity.setDefenseStatus(defenseStatus);
                newEntity.setWellStatus(statusBited);
                //System.out.println("Read Well with Location:" + newEntity.getOwnLocation() + " and status: " + newEntity.getWellStatus() + " and defense: " + newEntity.getDefenseStatus() + "on index: " + newEntity.getIndex());
                wellEntities[i - STARTING_WELL_IDX] = (newEntity);
                wellEntityArrayList.add(newEntity);
            } else break;
        }
        return wellEntityArrayList;
    }


    static void clearObsoleteEnemies(RobotController rc) {
        for (int i = STARTING_ENEMY_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
            try {
                MapLocation mapLoc = intToLocation(rc, rc.readSharedArray(i));
                if (mapLoc == null) {
                    continue;
                }
                if (rc.canSenseLocation(mapLoc) && rc.senseNearbyRobots(mapLoc, AREA_RADIUS, rc.getTeam().opponent()).length == 0) {
                    Message msg = new Message(i, locationToInt(rc, null), turnCount);
                    messagesQueue.add(msg);
                }
            } catch (GameActionException ignored) {
            }

        }
    }

    static void reportEnemy(RobotController rc, MapLocation enemy) {
        int slot = -1;
        for (int i = STARTING_ENEMY_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
            try {
                MapLocation prevEnemy = intToLocation(rc, rc.readSharedArray(i));
                if (prevEnemy == null) {
                    slot = i;
                    break;
                } else if (prevEnemy.distanceSquaredTo(enemy) < AREA_RADIUS) {
                    return;
                }
            } catch (GameActionException ignore) {
            }
        }
        if (slot != -1) {
            Message msg = new Message(slot, locationToInt(rc, enemy), turnCount);
            messagesQueue.add(msg);
        }
    }

    static MapLocation getClosestEnemy(RobotController rc) {
        MapLocation answer = null;
        for (int i = STARTING_ENEMY_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
            final int value;
            try {
                value = rc.readSharedArray(i);
                final MapLocation m = intToLocation(rc, value);
                if (m != null && (answer == null || rc.getLocation().distanceSquaredTo(m) < rc.getLocation().distanceSquaredTo(answer))) {
                    answer = m;
                }
            } catch (GameActionException ignored) {
            }
        }
        return answer;
    }

    private static int locationToInt(RobotController rc, MapLocation m) {
        if (m == null) {
            return 0;
        }
        return 1 + m.x + m.y * rc.getMapWidth();
    }

    private static MapLocation intToLocation(RobotController rc, int m) {
        if (m == 0) {
            return null;
        }
        m--;
        return new MapLocation(m % rc.getMapWidth(), m / rc.getMapWidth());
    }
}