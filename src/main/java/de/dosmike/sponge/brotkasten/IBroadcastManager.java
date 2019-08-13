package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.command.CommandSource;

import java.util.Collection;

public interface IBroadcastManager {

    void setMinDelay(int ticks);
    void load(Collection<String> broadcasts, CommandSource responsible);
    void tick();

}
