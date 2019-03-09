package de.dosmike.sponge.brotkasten;

import me.rojo8399.placeholderapi.PlaceholderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.NoSuchElementException;

public class PlaceholderServerChat extends ServerChat {

    private PlaceholderService service;

    public PlaceholderServerChat() throws NoSuchElementException {
        service = Sponge.getServiceManager().provide(PlaceholderService.class).get();
    }

    @Override
    public void broadcast(Text text) {
        for (Player online : Sponge.getServer().getOnlinePlayers())
            online.sendMessage(service.replaceSourcePlaceholders(text, online));
    }
}
