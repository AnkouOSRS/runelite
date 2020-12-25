package net.runelite.client.plugins.zuktimers;

import net.runelite.client.ui.overlay.infobox.InfoBox;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ZukTimerInfobox extends InfoBox
{
    private ZukTimer zukStopwatch;
    private String text;

    public ZukTimerInfobox(ZukTimersPlugin plugin, BufferedImage image, ZukTimer zukStopwatch)
    {
        super(image, plugin);
        this.zukStopwatch = zukStopwatch;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }

    String getFormattedTime()
    {
        final String formatString = "mm:ss";
        return DurationFormatUtils.formatDuration(zukStopwatch.getRemaining(), formatString, false);
    }
}
