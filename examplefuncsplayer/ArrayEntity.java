package examplefuncsplayer;

import battlecode.common.MapLocation;

import java.util.Map;

public class ArrayEntity {

    private MapLocation ownLocation;
    private int index;

    public ArrayEntity(int index, MapLocation ownLocation) {
        this.index=index;
        this.ownLocation=ownLocation;
    }

    public MapLocation getOwnLocation() {
        return ownLocation;
    }

    public void setOwnLocation(MapLocation ownLocation) {
        this.ownLocation = ownLocation;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
