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

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;

class CrabStunOverlay extends Overlay
{
	private final Client client;
	private final CrabStunPlugin plugin;

	private final Duration STUN_TIME_RANDOMNESS_INTERVAL = Duration.ofSeconds(5);

	@Inject
	private CrabStunOverlay(Client client, CrabStunPlugin plugin)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		List<CrabStun> locations = plugin.getStunEvents();
		if (locations.isEmpty())
		{
			return null;
		}

		Instant now = Instant.now();
		for (Iterator<CrabStun> it = locations.iterator(); it.hasNext();)
		{
			Color pieFillColor = Color.YELLOW;
			Color pieBorderColor = Color.ORANGE;
			CrabStun stun = it.next();

			float percent = (now.toEpochMilli() - stun.getStartTime().toEpochMilli()) / ((float) stun.getStunDuration()
					+ STUN_TIME_RANDOMNESS_INTERVAL.toMillis());
			if (percent > .8) {
				pieFillColor = Color.RED;
		}

			WorldPoint worldPoint = stun.getWorldPoint();
			LocalPoint loc = LocalPoint.fromWorld(client, worldPoint);
			if (loc == null || percent > 1.0f)
			{
				it.remove();
				continue;
			}

			Point point = Perspective.localToCanvas(client, loc, client.getPlane(), stun.getZOffset());
			if (point == null)
			{
				it.remove();
				continue;
			}

			ProgressPieComponent ppc = new ProgressPieComponent();
			ppc.setBorderColor(pieBorderColor);
			ppc.setFill(pieFillColor);
			ppc.setPosition(point);
			ppc.setProgress(percent);
			ppc.render(graphics);
		}
		return null;
	}
}
