package net.runelite.client.plugins.playermodicon;

import com.google.common.base.Splitter;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.vars.AccountType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(name = "Player Mod Icon", enabledByDefault = false)
public class PlayerModIconPlugin extends Plugin
{
    @Inject
    @Getter
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Getter
    private static List<String> players = new ArrayList<>();

    @Override
    public void startUp()
    {

    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {

    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {

    }

    private void loadSprites()
    {

    }

    @Override
    public void shutDown()
    {
        clientThread.invoke(() -> client.runScript(ScriptID.CHAT_PROMPT_INIT));
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
//        if (event.getName() == null || client.getLocalPlayer() == null || client.getLocalPlayer().getName() == null)
//        {
//            return;
//        }
//
//        boolean isLocalPlayer =
//                Text.standardize(event.getName()).equalsIgnoreCase(Text.standardize(client.getLocalPlayer().getName()));
//
//        if (isLocalPlayer || players.contains(Text.standardize(event.getName().toLowerCase())))
//        {
//            event.getMessageNode().setName(
//                    getImgTag(iconIds.getOrDefault(selectedIcon, IconID.NO_ENTRY.getIndex())) +
//                            Text.removeTags(event.getName()));
//        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if (!event.getEventName().equals("setChatboxInput"))
        {
            return;
        }

        updateChatbox();
    }

    @Subscribe
    public void onBeforeRender(BeforeRender event)
    {
        updateChatbox(); // this stops flickering when typing
    }

    private void updateChatbox()
    {
        Widget chatboxTypedText = client.getWidget(WidgetInfo.CHATBOX_INPUT);

//        if (getIconIdx() == -1)
//        {
//            return;
//        }

        if (chatboxTypedText == null || chatboxTypedText.isHidden())
        {
            return;
        }

        String[] chatbox = chatboxTypedText.getText().split(":", 2);
        String rsn = Objects.requireNonNull(client.getLocalPlayer()).getName();
        AccountType accType = client.getAccountType();
        int ironIndex = -1;

        switch(accType)
        {
            case IRONMAN:
                ironIndex = IconID.IRONMAN.getIndex();
        }



        chatboxTypedText.setText((getImgTag(ironIndex) != null ? getImgTag(ironIndex) : "")
                + getImgTag(IconID.PLAYER_MODERATOR.getIndex()) + Text.removeTags(rsn) + ":" + chatbox[1]);
    }

    private String getImgTag(int i)
    {
        if (i == -1)
            return null;
        return "<img=" + i + ">";
    }
}
