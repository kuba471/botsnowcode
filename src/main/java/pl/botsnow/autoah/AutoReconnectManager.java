package pl.botsnow.autoah;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class AutoReconnectManager {
    private static int reconnectDelayTicks = 0;
    private static boolean reconnectTriggered = false;
    private static int loginCommandDelay = -1;

    public static void tick(MinecraftClient client) {
        if (client.currentScreen instanceof DisconnectedScreen) {
            if (AutoAhConfig.autoReconnect) {
                if (!reconnectTriggered) {
                    reconnectTriggered = true;
                    reconnectDelayTicks = Math.max(20, AutoAhConfig.reconnectDelaySeconds * 20);
                }
            }
        } else {
            reconnectTriggered = false;
        }

        if (reconnectDelayTicks > 0) {
            reconnectDelayTicks--;
            if (reconnectDelayTicks == 0) {
                reconnect(client);
            }
        }

        if (client.player != null && AutoAhConfig.autoLogin && !AutoAhConfig.loginPassword.isBlank()) {
            if (loginCommandDelay < 0) {
                loginCommandDelay = 60;
            }
        } else {
            loginCommandDelay = -1;
        }

        if (loginCommandDelay > 0) {
            loginCommandDelay--;
            if (loginCommandDelay == 0 && client.player != null) {
                client.player.networkHandler.sendChatCommand("login " + AutoAhConfig.loginPassword);
            }
        }
    }

    private static void reconnect(MinecraftClient client) {
        String host = AutoAhConfig.reconnectAddress == null || AutoAhConfig.reconnectAddress.isBlank()
                ? "anarchia.gg"
                : AutoAhConfig.reconnectAddress;

        ServerAddress address = ServerAddress.parse(host);
        ServerInfo info = new ServerInfo("Anarchia", host, ServerInfo.ServerType.OTHER);
        Screen parent = client.currentScreen;
        if (parent != null) {
            ConnectScreen.connect(parent, client, address, info, false);
        }
    }
}
