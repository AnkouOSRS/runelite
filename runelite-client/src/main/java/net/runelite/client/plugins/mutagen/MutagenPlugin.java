/*
 * Copyright (c) 2019, Anthony Chen <https://github.com/achencoms>
 * Copyright (c) 2019, Adam <Adam@sigterm.info>
 * Copyright (c) 2020, Sean Dewar <https://github.com/seandewar>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.mutagen;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Set;

@PluginDescriptor(
        name = "Mutagen",
        description = "Change which mutagen your serp helm has",
        tags = {"serp", "helm", "mutagen"}
)
@Slf4j
public class MutagenPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private MutagenConfig mutagenConfig;

    @Inject
    private ItemManager itemManager;

    private int originalHelmID = -1;
    private final Set<Mutagen> MUTAGENS = ImmutableSet.of(Mutagen.NONE, Mutagen.TANZANITE, Mutagen.MAGMA);
    private final Set<Integer> SERP_HELMS =
            ImmutableSet.of(ItemID.SERPENTINE_HELM, ItemID.MAGMA_HELM, ItemID.TANZANITE_HELM);
    private final Set<Integer> SERP_HELMS_UNCHARGED = ImmutableSet.of(ItemID.SERPENTINE_HELM_UNCHARGED,
            ItemID.MAGMA_HELM_UNCHARGED, ItemID.TANZANITE_HELM_UNCHARGED);

    @Override
    protected void startUp() {
        recolorHelm();
    }

    @Override
    protected void shutDown() {
        recolorHelm(originalHelmID, originalHelmID);
        originalHelmID = -1;
    }

    @Provides
    MutagenConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MutagenConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (configChanged.getGroup().equals("mutagen")) {
            recolorHelm();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            recolorHelm();
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getItemContainer() != client.getItemContainer(InventoryID.EQUIPMENT)) {
            return;
        }
        recolorHelm();
    }

    @Subscribe
    public void onPlayerChanged(PlayerChanged event)
    {
        if (event.getPlayer().equals(client.getLocalPlayer()))
        {
            recolorHelm();
        }
    }

    private void recolorHelm() {
        log.debug("Recoloring serp helm to {}", mutagenConfig.mutagenToUse().getName());
        recolorHelm(mutagenConfig.mutagenToUse().getItemID(), mutagenConfig.mutagenToUse().getUnchargedID());
    }

    private void recolorHelm(int itemID, int unchargedID) {
        if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null) {
            PlayerComposition pc = client.getLocalPlayer().getPlayerComposition();
            if (pc != null)
            {
                int currentHelmID = pc.getEquipmentIds()[KitType.HEAD.getIndex()] - 512;
                int newHelmID = -1;

                if (SERP_HELMS.contains(currentHelmID))
                {
                    newHelmID = itemID;
                }
                else if (SERP_HELMS_UNCHARGED.contains(currentHelmID))
                {
                    newHelmID = unchargedID;
                }
                if (originalHelmID == -1) {
                    originalHelmID = currentHelmID;
                }
                if (newHelmID != -1)
                {
                    pc.getEquipmentIds()[KitType.HEAD.getIndex()] = newHelmID + 512;
                    pc.setHash();
                }
            }
        }
    }
}
