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

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Mutagen",
	description = "Change which mutagenToUse your serp helm has",
	tags = {"serp", "helm", "mutagenToUse"}
)
@Slf4j
public class MutagenPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MutagenConfig mutagenConfig;

	private int originalHelmID = -1;

	@Override
	protected void startUp()
	{
        if (originalHelmID == -1 && client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null)
        {
            originalHelmID = client.getLocalPlayer().getPlayerComposition().getEquipmentIds()[KitType.HEAD.getIndex()];
        }
	    recolorHelm();
	}

	@Override
	protected void shutDown()
	{
	    recolorHelm(originalHelmID);
	}

	@Provides
	MutagenConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MutagenConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (configChanged.getGroup().equals("mutagen"))
		{
		    recolorHelm();
		}
	}

	private void recolorHelm()
    {
    	log.info("Recoloring serp helm to {}", mutagenConfig.mutagenToUse().getName());
        recolorHelm(mutagenConfig.mutagenToUse().getId());
    }

    private void recolorHelm(int helmID)
    {
        if (client.getGameState() == GameState.LOGGED_IN && client.getLocalPlayer() != null)
        {
            PlayerComposition pc = client.getLocalPlayer().getPlayerComposition();
            if (originalHelmID == -1)
			{
				originalHelmID = pc.getEquipmentIds()[KitType.HEAD.getIndex()];
			}
			log.info("Original helm ID: {}", originalHelmID);
            log.info("New helm ID: {}", helmID + 512);
            pc.getEquipmentIds()[KitType.HEAD.getIndex()] = helmID + 512;
        }
    }

    @Subscribe
    private void onCommandExecuted(CommandExecuted event)
	{
		if (event.getCommand().equals("serp"))
		{
			client.getLocalPlayer().getPlayerComposition().getEquipmentIds()[KitType.HEAD.getIndex()] = 13711;
		}
	}
}
