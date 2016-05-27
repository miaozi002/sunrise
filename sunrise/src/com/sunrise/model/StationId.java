package com.sunrise.model;

import java.io.Serializable;

public class StationId implements Serializable {
    private static final long serialVersionUID = 6457519630093291648L;

    public int stid;
    public int level1Id;
    public int level2Id;
    public int level3Id;

    @Override
    public int hashCode() {
        int result = stid;
        result = result * 31 + level1Id;
        result = result * 31 + level2Id;
        result = result * 31 + level3Id;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof StationId))
            return false;
        StationId other = (StationId) o;
        if (stid != other.stid)
            return false;
        if (level1Id != other.level1Id)
            return false;
        if (level2Id != other.level2Id)
            return false;
        if (level3Id != other.level3Id)
            return false;

        return true;
    }
}
