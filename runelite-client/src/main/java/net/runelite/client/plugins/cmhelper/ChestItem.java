package net.runelite.client.plugins.cmhelper;

import net.runelite.api.ItemComposition;
import net.runelite.api.Point;

public class ChestItem {
    private ItemComposition itemComposition;
    private Point location;

    public ItemComposition getItemComposition() {
        return itemComposition;
    }

    public void setItemComposition(ItemComposition itemComposition) {
        this.itemComposition = itemComposition;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
