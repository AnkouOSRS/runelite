package net.runelite.client.plugins.zuktimers;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@PluginDescriptor(
        name = "Zuk Timers",
        description = "Timers for the sets of Rangers and Magers that spawn during Zuk",
        tags = {"pvm", "boss", "inferno", "timers"}
)
public class ZukTimersPlugin extends Plugin
{
    @Inject
    private ClientToolbar clientToolbar;

//    @Inject
//    private ZukTimerManager zukTimerManager;

    @Inject
    private ItemManager itemManager;

    private ZukTimersPanel panel;
    private NavigationButton navButton;

    @Inject
    private ScheduledExecutorService executorService;
    private ScheduledFuture panelUpdateFuture;

    @Override
    protected void startUp() throws Exception
    {
        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "zuk.png");
//        final BufferedImage icon = itemManager.getImage(ItemID.TZREKZUK);
        panel = new ZukTimersPanel(itemManager);
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

        if (panelUpdateFuture != null)
        {
            panelUpdateFuture.cancel(true);
            panelUpdateFuture = null;
        }

        clientToolbar.removeNavigation(navButton);
    }

    private void updatePanel()
    {
        panel.update();
    }
}
