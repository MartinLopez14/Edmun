package com.springvuegradle.team6.models.location;

/**
 * Open Street Map Element Types. These are specified on all features on OSM.
 * Node: A point in space.
 * Ways: Linear features and area boundaries.
 * Relations: Describes the relationship between two or more elements.
 * For more information look here: https://wiki.openstreetmap.org/wiki/Elements
 */

public enum OSMElementType {
    NODE('N'),
    RELATION('R'),
    WAY('W');

    public final char code;
    private OSMElementType(char code) {
        this.code = code;
    }

    public static OSMElementType getFromStr(String type) {
        if (type.equals("relation")) return OSMElementType.RELATION;
        if (type.equals("node")) return  OSMElementType.NODE;
        if (type.equals("way")) return  OSMElementType.WAY;
        return null;
    }
}
