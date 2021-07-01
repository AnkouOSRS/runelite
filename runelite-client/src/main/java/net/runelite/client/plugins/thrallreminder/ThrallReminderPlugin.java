/*
 *  Copyright (c) 2018, trimbe <github.com/trimbe>
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.thrallreminder;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.runepouch.Runes;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

@PluginDescriptor(
	name = "[A] Thrall Reminder",
	description = "Remind the player to summon their thrall when they have the runes and its not on cooldown",
	enabledByDefault = false
)
@Slf4j
public class ThrallReminderPlugin extends Plugin
{
	@Inject
	private ThrallReminderConfig config;

	@Inject
	private Client client;

	private static final Set<Runes> RUNES_TIER_THREE = ImmutableSet.of(Runes.COSMIC, Runes.BLOOD, Runes.FIRE);

	@Provides
	ThrallReminderConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ThrallReminderConfig.class);
	}

	@Override
	protected void shutDown() throws Exception
	{

	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		int rune1 = client.getVar(Varbits.RUNE_POUCH_RUNE1);
		int rune2 = client.getVar(Varbits.RUNE_POUCH_RUNE2);
		int rune3 = client.getVar(Varbits.RUNE_POUCH_RUNE3);

		log.info("Rune 1: {}", rune1);
		log.info("Rune 1: {}", rune2);
		log.info("Rune 1: {}", rune3);
		log.info("Blood: {}", Runes.BLOOD.getId());
		log.info("Cosmic: {}", Runes.COSMIC.getId());
		log.info("Fire: {}", Runes.FIRE.getId());
	}
}
