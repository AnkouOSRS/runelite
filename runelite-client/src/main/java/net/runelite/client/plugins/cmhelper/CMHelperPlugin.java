/*
 * Copyright (c) 2018, Kamiel, <https://github.com/Kamielvf>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
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
package net.runelite.client.plugins.cmhelper;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import net.runelite.http.api.item.ItemPrice;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "CM Helper",
	description = "Helps manage chest in CM",
	tags = {"boxes", "overlay", "panel"}
)
public class CMHelperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CMHelperOverlay overlay;

	@Getter
	@Inject
	private ColorPickerManager colorPickerManager;

	@Inject
	private ItemManager itemManager;

	@Getter
	@Setter
	private boolean creatingScreenMarker = false;

	@Getter
	private boolean drawingScreenMarker = false;

	@Getter
	@Setter
	private Rectangle selectedWidgetBounds = null;

	@Setter
	@Getter
	private List<ChestItem> chestItems;

	@Getter
	private Widget[] chestWidgets;

	@Inject
	private ClientThread clientThread;

	@Inject
	private CMHelperConfig config;

	@Getter
	private Widget parentWidget;

	final int COX_CHEST_PRIVATE_ID = 583;
	final int COX_CHEST_WIDGET_ID = 17760262;

	private final Set<Chest> CHALLENGE_MODE_CHESTS = ImmutableSet.of(Chest.ICE_DEMON, Chest.UPPER_FARMING,
			Chest.UPPER_FLOOR_END, Chest.THIEVING, Chest.LOWER_FARMING, Chest.LOWER_FLOOR_END);

	@Getter
	Map<Chest, ItemComposition[]> itemsToHighlight;

	@Provides
	CMHelperConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CMHelperConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		chestItems = new ArrayList<>();
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		overlayManager.removeIf(CMHelperOverlay.class::isInstance);
		parentWidget = null;
		chestWidgets = null;
		chestItems = null;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{

	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		log.info("Item Container changed: {}", event.getContainerId());
//		final int COX_CHEST_PRIVATE_ID = 583;
		final int COX_CHEST_PUBLIC_ID = 583;

		if (COX_CHEST_PRIVATE_ID == event.getContainerId())
		{
			List<ChestItem> result = new ArrayList<>();
			Item[] items = event.getItemContainer().getItems();

			if (items.length > 0)
			{
				for (int i = 0; i < items.length; i ++)
				{
					int itemID = items[i].getId();
					if (itemManager.getItemComposition(itemManager.canonicalize(itemID)).getName().equals("null"))
						continue;
					ItemComposition itemComposition = itemManager.getItemComposition(itemManager.canonicalize(itemID));
					if (ArrayUtils.contains(config.iceDemonItems().split(","), itemComposition.getName()))
					{
						ChestItem chestItem = new ChestItem();
						chestItem.setItemComposition(itemComposition);
						if (chestWidgets != null && chestWidgets[i] != null)
						{
							chestItem.setLocation(chestWidgets[i].getCanvasLocation());
							log.info("Item {} location updated to match chest widget: {}", i, chestWidgets[i].getCanvasLocation());
						}
						result.add(chestItem);
						log.info("items[{}]: {}", i, itemComposition.getName());
					}
				}
			}
			setChestItems(result);
		}
	}

	protected ItemComposition[] getItemsToHighlightForCurrentChest()
	{
		ItemComposition[] result = {};
		Chest currentChest = getCurrentChest();

		if (currentChest == null)
			return result;

		return itemsToHighlight.get(currentChest);
	}

	@Subscribe
	public void onMenuOpened(final MenuOpened event)
	{
		final MenuEntry firstEntry = event.getFirstEntry();

		if (firstEntry == null)
		{
			return;
		}

		final int widgetId = firstEntry.getParam1();

		// Inventory item menu
		if (widgetId == COX_CHEST_WIDGET_ID)
		{
//			int itemId = firstEntry.getIdentifier();
			String itemName = Text.removeTags(event.getMenuEntries()[1].getTarget());
			List<ItemPrice> prices = itemManager.search(itemName);
			int itemId = prices.get(0).getId();
			ItemComposition itemComposition = itemManager.getItemComposition(itemManager.canonicalize(itemId));

			if (itemId == -1)
			{
				return;
			}

			MenuEntry[] menuList = client.getMenuEntries();
			final MenuEntry newMenu = new MenuEntry();
			newMenu.setOption("Toggle for this chest");
			newMenu.setTarget("");
			newMenu.setIdentifier(itemId);
			newMenu.setParam1(widgetId);
			newMenu.setType(MenuAction.RUNELITE.getId());
			client.setMenuEntries(ArrayUtils.add(menuList, newMenu));
			log.info("Client menu entries updated.");
		}
	}

	@Subscribe
	public void onMenuOptionClicked(final MenuOptionClicked event)
	{
//		if (event.getMenuAction() != MenuAction.RUNELITE)
//		{
//			return;
//		}
//
//		final String selectedMenu = Text.removeTags(event.getMenuTarget());
//
//		if (event.getMenuOption().equals(MENU_SET))
//		{
//			setTag(event.getId(), selectedMenu);
//		}
//		else if (event.getMenuOption().equals(MENU_REMOVE))
//		{
//			unsetTag(event.getId());
//		}
		if (event.getMenuAction() != MenuAction.RUNELITE || !event.getMenuOption().equals("Toggle for this chest"))
		{
			return;
		}
		log.info(event.getId()+"");
		final String selectedMenu = Text.removeTags(event.getMenuTarget());
		ItemComposition itemComposition = itemManager.getItemComposition(itemManager.canonicalize(event.getId()));

		log.info(itemComposition.getName() + " toggled");

		Chest currentChest = getCurrentChest();

		if (currentChest != null)
		{
			if (itemsToHighlight.containsKey(currentChest))
			{
				ItemComposition[] highlightedItems = itemsToHighlight.get(currentChest);
				if (ArrayUtils.contains(highlightedItems, itemComposition))
				{
					Arrays.asList(highlightedItems).removeIf(item -> item.getId() == itemComposition.getId());
				}
				else
				{
					itemsToHighlight.put(currentChest, ArrayUtils.add(highlightedItems, itemComposition));
				}
			}
		}
	}

	private Chest getCurrentChest()
	{
		if (client.getLocalPlayer() == null)
			return null;

		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();

		for (Chest chest : CHALLENGE_MODE_CHESTS)
		{
			if (chest.getLocalPoint().distanceTo(playerLocation) == 0)
			{
				return chest;
			}
		}
		return null;
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		Widget parent = client.getWidget(event.getGroupId(),6);
		if (parent == null || parent.getId() != COX_CHEST_WIDGET_ID && parent.getParentId() != 17760256)
			return;

		clientThread.invokeLater(() -> {
			parentWidget = parent;
			Widget[] dynamicChildren = parent.getDynamicChildren();
			if (dynamicChildren != null)
			{
				chestWidgets = dynamicChildren;
			}
			else
			{
				log.error("No dynamic children found");
			}
		});
	}
}
