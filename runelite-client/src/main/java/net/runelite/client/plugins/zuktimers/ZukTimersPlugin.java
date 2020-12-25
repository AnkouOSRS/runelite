package net.runelite.client.plugins.zuktimers;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@PluginDescriptor(
        name = "Zuk Timers",
        description = "Timers for the sets of Rangers and Magers that spawn during Zuk",
        tags = {"pvm", "boss", "inferno", "timers"}
)
public class ZukTimersPlugin extends Plugin
{
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private InfoBoxManager infoBoxManager;

//    @Inject
//    private ZukTimerManager zukTimerManager;

    @Inject
    private ItemManager itemManager;

    private ZukTimersPanel panel;
    private NavigationButton navButton;

    @Inject
    private ScheduledExecutorService executorService;
    private ScheduledFuture panelUpdateFuture;

    private ZukTimerInfobox timerBox;
    private ZukTimer zukStopwatch;
    private ZukProgress zukProgress;
    private boolean zukStarted = false;

    private final Set<Integer> SET_IDS = ImmutableSet.of(7699, 7703, 7698, 7702);
    private final int ZUK_ID = 7706;
    private final BufferedImage INFOBOX_ICON = ImageUtil.getResourceStreamFromClass(getClass(), "first_set.png");

    @Override
    protected void startUp() throws Exception
    {
        zukStopwatch = new ZukTimer("Zuk", 210000);
        zukProgress = ZukProgress.PRE_FIRST_SET;
        panel = new ZukTimersPanel(itemManager, zukStopwatch, zukProgress, timerBox);

        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "zuk.png");
        navButton = NavigationButton.builder()
                .tooltip("Zuk Timers")
                .icon(icon)
                .panel(panel)
                .priority(4)
                .build();

        clientToolbar.addNavigation(navButton);
        panelUpdateFuture = executorService.scheduleAtFixedRate(this::updatePanel, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void shutDown() throws Exception
    {
        infoBoxManager.removeInfoBox(timerBox);
        timerBox = null;
        zukStarted = false;

        if (panelUpdateFuture != null)
        {
            panelUpdateFuture.cancel(true);
            panelUpdateFuture = null;
        }

        clientToolbar.removeNavigation(navButton);
    }

    private void updatePanel()
    {
        timerBox.setText(formatStopwatchTime(zukStopwatch.getDisplayTime()));
        panel.update();
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        int id = event.getNpc().getId();
        if (ZUK_ID == id && !zukStarted)
        {
            log.info("Zuk spawned!");
            onZukSpawned();
        }
        else if (SET_IDS.contains(id) && zukStarted && !zukStopwatch.active)
        {
            log.info("First set spawned");
            if (!zukStopwatch.start())
            {
                zukStopwatch.reset();
                zukStopwatch.start();
            }
        }
    }

    private void onZukSpawned()
    {
        log.info("Zuk spawned");
        zukStarted = true;
//        zukStopwatch.setDuration(TimeUnit.MILLISECONDS.convert(210, TimeUnit.SECONDS));

        if (timerBox == null)
        {
            timerBox = new ZukTimerInfobox(this, INFOBOX_ICON, zukStopwatch);
            infoBoxManager.addInfoBox(timerBox);
        }
    }

    private void onFirstSetSpawned()
    {
        boolean result = zukStopwatch.start();
        log.info("Started stopwatch on first set spawn, returned: {}", result);
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted event)
    {
        String command = event.getCommand();

        if (command.equals("spawn"))
        {
            int id = Integer.parseInt(event.getArguments()[0]);
            if (1 == id)
                onZukSpawned();
            if (2 == id)
                onFirstSetSpawned();
        }
        if (command.equals("gettime"))
        {
            log.info("remaining: {}", zukStopwatch.getRemaining());
            log.info("duration: {}", zukStopwatch.getDuration());

        }
    }

    private String formatStopwatchTime(long time)
    {
        final String formatString = "mm:ss";
        return DurationFormatUtils.formatDuration(time, formatString, false);
    }
}
