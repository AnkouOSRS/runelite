/*
 * Copyright (c) 2018, Kamiel, <https://github.com/Kamielvf>
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
package net.runelite.client.plugins.cmhelper;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.*;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class CMHelperOverlay extends Overlay
{
	@Inject
	private ItemManager itemManager;

	@Inject
	private Client client;

	private CMHelperPlugin plugin;
	private CMHelperConfig config;

	@Inject
	CMHelperOverlay(CMHelperPlugin cmHelperPlugin, CMHelperConfig config)
	{
		this.plugin = cmHelperPlugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		List<ChestItem> chestItems = plugin.getChestItems();
		ItemComposition[] itemsToHighlight = plugin.getItemsToHighlightForCurrentChest();

		int i = 0;
		for (ChestItem item : chestItems)
		{
			if (ArrayUtils.contains(itemsToHighlight, item.getItemComposition()))
			{
				int itemId = item.getItemComposition().getId();
				final BufferedImage outline = itemManager.getItemOutline(itemId, 1, Color.YELLOW);

				if (item.getLocation() != null)
					graphics.drawImage(outline, item.getLocation().getX(), item.getLocation().getY(), null);
			}
		}


		return null;
	}
}
