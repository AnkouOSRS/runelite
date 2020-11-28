package net.runelite.client.plugins.zuktimers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ZukProgress {
    PRE_FIRST_SET(0, "First set has spawned", "After 1st full shield rotation", "first_set_50.png"),
    ABOVE_SIX_HUNDRED(1, "Zuk is under 600 HP", "This will pause the set timer", "zuk_paused_50.png"),
    PAUSED(2, "Zuk is under 480 HP", "This will spawn jad", "jad_50.png"),
    UNPAUSED(3, "Healers: Zuk <= 240 HP", "Sets spawn every 3.5 min", "healer_50.png");

    private final int id;
    private final String title;
    private final String message;
    private final String iconImageName;
}
