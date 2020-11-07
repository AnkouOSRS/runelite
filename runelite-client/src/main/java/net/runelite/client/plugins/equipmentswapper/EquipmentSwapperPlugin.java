/*
 * Copyright (c) 2018, Infinitay <https://github.com/Infinitay>
 * Copyright (c) 2018-2019, Shaun Dreclin <https://github.com/ShaunDreclin>
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

package net.runelite.client.plugins.equipmentswapper;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.kit.KitType;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@PluginDescriptor(
	name = "Equipment Swapper",
	description = "Show chat notifications for daily tasks upon login"
)
public class EquipmentSwapperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private EquipmentSwapperConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	private long lastReset;
	private boolean loggingIn;

	@Provides
	EquipmentSwapperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EquipmentSwapperConfig.class);
	}

	@Override
	public void startUp()
	{
		loggingIn = true;
	}

	@Override
	public void shutDown()
	{
		lastReset = 0L;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGING_IN)
		{
			loggingIn = true;
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted event)
	{
		if (event.getCommand().equals("swapgear")) {
			if (client.getLocalPlayer() != null) {
				PlayerComposition pc = client.getLocalPlayer().getPlayerComposition();
				pc.getEquipmentIds()[KitType.HEAD.getIndex()] = 25001 + 512;
				pc.getEquipmentIds()[KitType.TORSO.getIndex()] = 25004 + 512;
				pc.getEquipmentIds()[KitType.LEGS.getIndex()] = 25007 + 512;
				pc.getEquipmentIds()[KitType.BOOTS.getIndex()] = 25010 + 512;
				pc.getEquipmentIds()[KitType.WEAPON.getIndex()] = 25013 + 512;
				pc.setHash();
			}
		}
	}
}
