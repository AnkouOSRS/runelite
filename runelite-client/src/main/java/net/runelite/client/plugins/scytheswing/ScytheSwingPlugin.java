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

package net.runelite.client.plugins.scytheswing;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

@PluginDescriptor(
	name = "[A] Tob Weapon Recolors",
	description = "Change the color of the scythe swing graphic",
	enabledByDefault = false
)
@Slf4j
public class ScytheSwingPlugin extends Plugin
{
	@Inject
	private ScytheSwingConfig config;

	private static final Set<Integer> SCYTHE_SWING_IDS = ImmutableSet.of(478, 1231, 506, 1172);
	private static final Set<Integer> SANGUINE_SCYTHE_SWING_IDS = ImmutableSet.of(1891, 1892);
	private static final Set<Integer> HOLY_SCYTHE_SWING_IDS = ImmutableSet.of(1897, 1896);

	private static final int SANG_BAT_ID = 1539;
	private static final int SANG_PLAYER_GRAPHIC = 1540;
	private static final int SANG_HIT_GRAPHIC = 1541;
	private static final int SANG_HEAL_GRAPHIC = 1542;

	private static final int HOLY_BAT_ID = 1899;
	private static final int HOLY_SANG_PLAYER_GRAPHIC = 1900;
	private static final int HOLY_SANG_HIT_GRAPHIC = 1901;
	private static final int HOLY_SANG_HEAL_GRAPHIC = 1902;

	@Provides
	ScytheSwingConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ScytheSwingConfig.class);
	}

	@Override
	protected void shutDown() throws Exception
	{

	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated event)
	{
		GraphicsObject obj = event.getGraphicsObject();
		if ((SCYTHE_SWING_IDS.contains(obj.getId()) && config.recolorScythe())
				|| (SANGUINE_SCYTHE_SWING_IDS.contains(obj.getId()) && config.recolorSanguine())
				|| (HOLY_SCYTHE_SWING_IDS.contains(obj.getId()) && config.recolorHoly()))
		{
			Color color = config.randomize() ? new Color((int)(Math.random() * 0x1000000)) : config.swingColor();
			recolorAllFaces(obj.getModel(), color);
		}
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{
		Projectile proj = event.getProjectile();
		if ((SANG_BAT_ID == proj.getId() && config.recolorSangStaff())
				|| (HOLY_BAT_ID == proj.getId() && config.recolorHolySangStaff()))
		{
			Color color = config.discoBat() ? new Color((int)(Math.random() * 0x1000000)) : config.batColor();
			recolorAllFaces(proj.getModel(), color);
		}
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event)
	{
		if (config.makeSangHoly())
		{
			Actor actor = event.getActor();
			int graphic = actor.getGraphic();
			if (SANG_HIT_GRAPHIC == graphic)
			{
				actor.setGraphic(HOLY_SANG_HIT_GRAPHIC);
			}
			else if (SANG_HEAL_GRAPHIC == graphic)
			{
				actor.setGraphic(HOLY_SANG_HEAL_GRAPHIC);
			}
			else if (SANG_PLAYER_GRAPHIC == graphic)
			{
				actor.setGraphic(HOLY_SANG_PLAYER_GRAPHIC);
			}
		}
	}

	private void recolorAllFaces(Model model, Color color)
	{
		if (model == null || color == null)
		{
			return;
		}

		int rs2hsb = colorToRs2hsb(color);
		int[] faceColors1 = model.getFaceColors1();
		int[] faceColors2 = model.getFaceColors2();
		int[] faceColors3 = model.getFaceColors3();

		replaceFaceColorValues(faceColors1, faceColors2, faceColors3, rs2hsb);
	}

	private void recolorAllFaces(Model model, Color color1, Color color2, Color color3)
	{
		if (model == null || color1 == null || color2 == null || color3 == null)
		{
			return;
		}

		int rs2hsb1 = colorToRs2hsb(color1);
		int rs2hsb2 = colorToRs2hsb(color2);
		int rs2hsb3 = colorToRs2hsb(color3);
		int[] faceColors1 = model.getFaceColors1();
		int[] faceColors2 = model.getFaceColors2();
		int[] faceColors3 = model.getFaceColors3();

		replaceFaceColorValues(faceColors1, faceColors2, faceColors3, rs2hsb1, rs2hsb2, rs2hsb3);
	}

	private int colorToRs2hsb(Color color)
	{
		float[] hsbVals = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

		// "Correct" the brightness level to avoid going to white at full saturation, or having a low brightness at
		// low saturation
		hsbVals[2] -= Math.min(hsbVals[1], hsbVals[2] / 2);

		int encode_hue = (int)(hsbVals[0] * 63);
		int encode_saturation = (int)(hsbVals[1] * 7);
		int encode_brightness = (int)(hsbVals[2] * 127);
		return (encode_hue << 10) + (encode_saturation << 7) + (encode_brightness);
	}

	private void replaceFaceColorValues(int[] faceColors1, int[] faceColors2, int[] faceColors3, int globalReplacement)
	{
		if (faceColors1.length > 0)
		{
			for (int i = 0; i < faceColors1.length; i++)
			{
				faceColors1[i] = globalReplacement;
			}
		}
		if (faceColors2.length > 0)
		{
			for (int i = 0; i < faceColors2.length; i++)
			{
				faceColors2[i] = globalReplacement;
			}
		}
		if (faceColors3.length > 0)
		{
			for (int i = 0; i < faceColors3.length; i++)
			{
				faceColors3[i] = globalReplacement;
			}
		}
	}

	private void replaceFaceColorValues(int[] faceColors1, int[] faceColors2, int[] faceColors3, int globalReplacement1,
										int globalReplacement2, int globalReplacement3)
	{
		if (faceColors1.length > 0)
		{
			for (int i = 0; i < faceColors1.length; i++)
			{
				faceColors1[i] = globalReplacement1;
			}
		}
		if (faceColors2.length > 0)
		{
			for (int i = 0; i < faceColors2.length; i++)
			{
				faceColors2[i] = globalReplacement2;
			}
		}
		if (faceColors3.length > 0)
		{
			for (int i = 0; i < faceColors3.length; i++)
			{
				faceColors3[i] = globalReplacement3;
			}
		}
	}
}
