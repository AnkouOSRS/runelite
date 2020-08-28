package net.runelite.client.plugins.coxlightcolors;

import com.google.inject.Guice;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ChatColorConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.inject.Inject;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CoxLightColorsPluginTest {

    @Mock
    @Bind
    Client client;

    @Mock
    @Bind
    Player player;

    @Mock
    @Bind
    ChatColorConfig chatColorConfig;

    @Mock
    @Bind
    CoxLightColorsConfig coxLightColorsConfig;

    @Inject
    CoxLightColorsPlugin coxLightColorsPlugin;

    @Before
    public void before()
    {
        Guice.createInjector(BoundFieldModule.of(this)).injectMembers(this);

        when(client.getLocalPlayer()).thenReturn(player);
        when(player.getName()).thenReturn("Ankou btw");
//        when(coxLightColorsConfig.specifyDragonClaws()).thenReturn(true);
    }

    @Test
    public void specialLootTest() {
        ChatMessage lootMessage = new ChatMessage(null, ChatMessageType.FRIENDSCHATNOTIFICATION, null, "<col=ef20ff>Special loot:</col>", "", 0);
        ChatMessage dropMessage = new ChatMessage(null, ChatMessageType.FRIENDSCHATNOTIFICATION, null, "<col=ef20ff>Ankou btw -</col> <col=ff0000>Dragon claws</col>", "", 0);
        coxLightColorsPlugin.onChatMessage(lootMessage);
        coxLightColorsPlugin.onChatMessage(dropMessage);
    }
}
