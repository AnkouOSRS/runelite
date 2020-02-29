/*
 * Copyright (c) 2019, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.crabstuntimer;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Crab Stun Timers",
	description = "Show crab stun timers",
	tags = {"overlay", "raid", "pvm", "timers"},
	enabledByDefault = false
)
public class CrabStunPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private CrabStunConfig config;

	@Provides
	CrabStunConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(CrabStunConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {

	}

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CrabStunOverlay overlay;

	@Inject
	private NPCManager npcManager;

	@Getter(AccessLevel.PACKAGE)
	private final List<CrabStun> stunEvents = new ArrayList<>();

	private int[] crabIDs = {7576, 7577, 7578, 7579};

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onNpcChanged(NpcChanged event) {
		final int Z_OFFSET_COX_UPPER_FLOOR = 3;
		final Duration STUN_TIME_DUO = Duration.ofSeconds(20);

		NPC npc = event.getNpc();
		System.out.println("NPC changed! " + npc.getName());

		int graphic = event.getNpc().getGraphic();
		if (ArrayUtils.contains(crabIDs, npc.getId())) {
			if (graphic == 245) {
				System.out.println("Crab stunned!");
				WorldPoint worldPoint = npc.getWorldLocation();
				CrabStun stunEvent = new CrabStun(npc, worldPoint, Instant.now(), (int) STUN_TIME_DUO.toMillis(),
						Z_OFFSET_COX_UPPER_FLOOR);
				stunEvents.add(stunEvent);
			}
		}
	}
}
