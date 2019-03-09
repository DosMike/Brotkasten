package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class ServerChat {

    public void broadcast(Text text) {
        //don't broadcast to console
        for (Player online : Sponge.getServer().getOnlinePlayers())
            online.sendMessage(text);
    }

}
