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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("scytheswing")
public interface ScytheSwingConfig extends Config
{
	@ConfigSection(
			name = "Scythe of Vitur",
			description = "Options to recolor the Scythe of Vitur swing",
			position = 0
	)
	String scytheSection = "scythe";

	@ConfigSection(
			name = "Sanguinesti Staff",
			description = "Options to recolor the Sanguinesti Staff animations",
			position = 1
	)
	String sangSection = "sang";

	@ConfigItem(
			keyName = "swingColor",
			name = "Scythe Swing",
			description = "Color of the swing graphic",
			position = 1,
			section = scytheSection
	)
	default Color swingColor()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "randomize",
			name = "Randomize swing color",
			description = "Choose a random color for the swing each time",
			position = 2,
			section = scytheSection
	)
	default boolean randomize()
	{
		return false;
	}

	@ConfigItem(
			keyName = "recolorScythe",
			name = "Regular Scythe",
			description = "Recolor the swing of the Sanguine Scythe of Vitur",
			position = 3,
			section = scytheSection
	)
	default boolean recolorScythe()
	{
		return false;
	}

	@ConfigItem(
			keyName = "recolorSanguine",
			name = "Sanguine Scythe",
			description = "Recolor the swing of the Sanguine Scythe of Vitur",
			position = 4,
			section = scytheSection
	)
	default boolean recolorSanguine()
	{
		return false;
	}

	@ConfigItem(
			keyName = "recolorHoly",
			name = "Holy Scythe",
			description = "Recolor the swing of the Holy Scythe of Vitur",
			position = 5,
			section = scytheSection
	)
	default boolean recolorHoly()
	{
		return false;
	}

	@ConfigItem(
			keyName = "batColor",
			name = "Sang Bat",
			description = "Color of the sanguinesti staff projectile",
			position = 1,
			section = sangSection
	)
	default Color batColor()
	{
		return Color.CYAN;
	}

	@ConfigItem(
			keyName = "discoBat",
			name = "Disco bat",
			description = "Choose a random color for the swing each time",
			position = 2,
			section = sangSection
	)
	default boolean discoBat()
	{
		return false;
	}

	@ConfigItem(
			keyName = "makeSangHoly",
			name = "Make Sang extras holy",
			description = "Swaps standard sanguinesti staff graphics with the holy variant",
			position = 3,
			section = sangSection
	)
	default boolean makeSangHoly()
	{
		return false;
	}

	@ConfigItem(
			keyName = "recolorSangStaff",
			name = "Regular Sang Bat",
			description = "Recolor the bat of the regular Sanguinesti Staff",
			position = 4,
			section = sangSection
	)
	default boolean recolorSangStaff()
	{
		return false;
	}

	@ConfigItem(
			keyName = "recolorHolySangStaff",
			name = "Holy Sang Bat",
			description = "Recolor the bat of the Holy Sanguinesti Staff",
			position = 5,
			section = sangSection
	)
	default boolean recolorHolySangStaff()
	{
		return false;
	}
}
