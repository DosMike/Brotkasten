package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.LinkedList;
import java.util.List;

public class ServerChat {

    private List<Text> messageCache = new LinkedList<>();

    public void addMessage(String raw, CommandSource logTo) {
        Text text = ChatManager.deserializeUrlString(raw, logTo);
        messageCache.add(text != null ? text : Text.of(TextColors.RED, "<BROKEN ENTRY> ", TextColors.RESET, raw));
    }
    public void clearCache() {
        messageCache.clear();
    }

    public void broadcast(int cacheIndex) {
        //don't broadcast to console
        for (Player online : Sponge.getServer().getOnlinePlayers())
            online.sendMessage(messageCache.get(cacheIndex));
    }

}
