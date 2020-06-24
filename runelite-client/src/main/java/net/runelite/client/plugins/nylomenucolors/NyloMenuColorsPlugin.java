/*
 * Copyright (c) 2018, Ankou <https://github.com/ankouosrs>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.nylomenucolors;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

import static net.runelite.api.MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET;

@PluginDescriptor(
	name = "Nylo Menu Colors",
	description = "Recolors menu entries for Nylocas in Theatre of Blood"
)
@Slf4j
public class NyloMenuColorsPlugin extends Plugin
{
	private static final Set<MenuAction> NPC_MENU_ACTIONS = ImmutableSet.of(MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION,
			MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION, MenuAction.NPC_FIFTH_OPTION, MenuAction.SPELL_CAST_ON_NPC,
			MenuAction.ITEM_USE_ON_NPC);

	// Large, small, large ragger, small ragger, and boss nylos all have different IDs, these are in no particular order.
	private static final Set<Integer> NYLO_MELEE_IDS = ImmutableSet.of(8342, 8345, 8351, 8355, 8348);
	private static final Set<Integer> NYLO_RANGE_IDS = ImmutableSet.of(8343, 8352, 8346, 8357, 8349);
	private static final Set<Integer> NYLO_MAGE_IDS = ImmutableSet.of(8344, 8347, 8353, 8356, 8350);

	@Inject
	private Client client;

	@Inject
	private NyloMenuColorsConfig config;

	@Provides
	NyloMenuColorsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NyloMenuColorsConfig.class);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		int type = event.getType();

		if (type >= MENU_ACTION_DEPRIORITIZE_OFFSET)
		{
			type -= MENU_ACTION_DEPRIORITIZE_OFFSET;
		}

		final MenuAction menuAction = MenuAction.of(type);

		if (NPC_MENU_ACTIONS.contains(menuAction))
		{
			NPC npc = client.getCachedNPCs()[event.getIdentifier()];

			Color color = null;
			if (!npc.isDead())
			{
				if (NYLO_MAGE_IDS.contains(npc.getId()))
				{
					color = config.mageColor();
				}
				else if (NYLO_MELEE_IDS.contains(npc.getId()))
				{
					color = config.meleeColor();
				}
				else if (NYLO_RANGE_IDS.contains(npc.getId()))
				{
					color = config.rangeColor();
				}
			}

			if (color != null)
			{
				MenuEntry[] menuEntries = client.getMenuEntries();
				final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
				final String target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
				menuEntry.setTarget(target);
				client.setMenuEntries(menuEntries);
			}
		}
	}
}
