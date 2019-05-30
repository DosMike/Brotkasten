package de.dosmike.sponge.brotkasten;

import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.impl.utils.TextUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class PlaceholderServerChat extends ServerChat {

    private List<String> rawCache = new LinkedList<>();
    private PlaceholderService service;

    public PlaceholderServerChat() throws NoSuchElementException {
        service = Sponge.getServiceManager().provide(PlaceholderService.class).get();
    }

    @Override
    public void addMessage(String raw, CommandSource logTo) {
        rawCache.add(raw);
    }

    @Override
    public void clearCache() {
        rawCache.clear();
    }

    @Override
    public void broadcast(int index) {
        //this is done in this inconvenient way because parsing placeholders on clickable text removes text-actions
        for (Player online : Sponge.getServer().getOnlinePlayers()) {
            //replace placeholders without formatting (since formats will break in url parsing)
            TextTemplate template = TextUtils.toTemplate(Text.of(rawCache.get(index)), service.getDefaultPattern());
            String templated = service.replaceSourcePlaceholders(template, online).toPlain();

            //parse formats and urls
            Text result = ChatManager.deserializeUrlString(templated, Sponge.getServer().getConsole());
            if (result != null)
                online.sendMessage(result);
            else {
                online.sendMessage(Text.of(TextColors.RED, "<BROKEN ENTRY> ", TextColors.RESET, rawCache.get(index)));
            }
        }
    }
}
