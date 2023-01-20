package crusader;

import battlecode.common.MapLocation;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayEntity that = (ArrayEntity) o;
        return ownLocation.equals(that.ownLocation);
    }

}
