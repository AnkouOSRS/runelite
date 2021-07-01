package net.runelite.client.plugins.thrallreminder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ThrallTier {
    ONE(1),
    TWO(2),
    THREE(3);

    private final int tier;
}
