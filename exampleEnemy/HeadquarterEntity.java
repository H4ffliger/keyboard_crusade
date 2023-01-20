package exampleEnemy;

import battlecode.common.MapLocation;

public class HeadquarterEntity extends ArrayEntity {

    private int neededResource;
    private int defenseStatus;
    public HeadquarterEntity(int index, MapLocation ownLocation) {
        super(index, ownLocation);
    }

    public int getNeededResource() {
        return neededResource;
    }

    public void setNeededResource(int neededResource) {
        this.neededResource = neededResource;
    }

    public int getDefenseStatus() {
        return defenseStatus;
    }

    public void setDefenseStatus(int defenseStatus) {
        this.defenseStatus = defenseStatus;
    }
}
