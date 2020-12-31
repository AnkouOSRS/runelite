package net.runelite.client.plugins.mutagen;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;

public enum Mutagen {
    TANZANITE("Tanzanite Mutagen", ItemID.TANZANITE_HELM, ItemID.TANZANITE_HELM_UNCHARGED),
    MAGMA("Magma Mutagen", ItemID.MAGMA_HELM, ItemID.MAGMA_HELM_UNCHARGED),
    NONE("None", ItemID.SERPENTINE_HELM, ItemID.SERPENTINE_HELM_UNCHARGED);

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private int itemID;
    @Getter
    @Setter
    private int unchargedID;


    Mutagen(String name, int itemID, int unchargedID) {
        this.name = name;
        this.itemID = itemID;
        this.unchargedID = unchargedID;
    }
}
