package net.runelite.client.plugins.cmhelper;

import lombok.Getter;
import net.runelite.api.coords.LocalPoint;

@Getter
public enum Chest {
    ICE_DEMON(1,"Ice Demon", new LocalPoint(10816,6208)),
    UPPER_FARMING(2,"Upper Farming", new LocalPoint(9408, 6336)),
    UPPER_FLOOR_END(3,"Upper Floor End", new LocalPoint(6336, 7104)),
    THIEVING(4,"Thieving", new LocalPoint(7232, 5312)),
    LOWER_FARMING(5,"Lower Farming", new LocalPoint(5184, 5568)),
    LOWER_FLOOR_END(6,"Lower Floor End", new LocalPoint(8896, 6592)),
    ;

    private final int id;
    private final String name;
    private final LocalPoint localPoint;


    Chest(int id, String name, LocalPoint localPoint) {
        this.id = id;
        this.name = name;
        this.localPoint = localPoint;
    }

}
