package examplefuncsplayer;

import battlecode.common.MapLocation;

public class WellEntity extends ArrayEntity {

    private int wellStatus;
    private int defenseStatus;

    public WellEntity(int index, MapLocation ownLocation) {
        super(index, ownLocation);
    }

    public int getWellStatus() {
        return wellStatus;
    }

    public void setWellStatus(int wellStatus) {
        this.wellStatus = wellStatus;
    }

    public int getDefenseStatus() {
        return defenseStatus;
    }

    public void setDefenseStatus(int defenseStatus) {
        this.defenseStatus = defenseStatus;
    }
}
