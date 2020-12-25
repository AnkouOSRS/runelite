package net.runelite.client.plugins.mutagen;

public enum Mutagen {
    TANZANITE("Tanzanite Mutagen", 13197),
    MAGMA("Magma Mutagen", 13199);

    private String name;
    private int id;

    Mutagen(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
