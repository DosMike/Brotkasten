package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class EventListener {

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Brotkasten.getInstance().getServerBossBar().addPlayer(event.getTargetEntity());
    }

    @Listener
    public void onPlayerPart(ClientConnectionEvent.Disconnect event) {
        Brotkasten.getInstance().getServerBossBar().removePlayer(event.getTargetEntity());
        Brotkasten.getInstance().bossBarManager.unmute(event.getTargetEntity());
    }

}
