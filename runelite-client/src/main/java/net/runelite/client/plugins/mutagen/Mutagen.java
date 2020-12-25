package net.runelite.client.plugins.mutagen;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;

public enum Mutagen {
    TANZANITE("Tanzanite Mutagen", ItemID.TANZANITE_HELM),
    MAGMA("Magma Mutagen", ItemID.MAGMA_HELM),
    NONE("None", ItemID.SERPENTINE_HELM);

    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String name;

    Mutagen(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
