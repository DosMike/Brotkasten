package de.dosmike.sponge.brotkasten;

import me.rojo8399.placeholderapi.PlaceholderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class PlaceholderBossBarWrapper extends BossBarWrapper {

    private PlaceholderService placeholders;

    public PlaceholderBossBarWrapper() throws NoSuchElementException {
        placeholders = Sponge.getServiceManager().provide(PlaceholderService.class).get();
    }

    @Override
    public PlaceholderBossBarWrapper addPlayer(Player player) {
        ServerBossBar bar = ServerBossBar.builder()
                .percent(percent)
                .overlay(overlay)
                .color(color)
                .name(getNamePlaceholder(name, player))
                .playEndBossMusic(playMusic)
                .createFog(createFog)
                .darkenSky(darkenSky)
                .visible(visible)
                .build()
                .addPlayer(player);
        players.add(player.getUniqueId());
        instances.put(player.getUniqueId(), bar);
        return this;
    }

    @Override
    public Set<Player> getPlayers() {
        return players.stream()
                .map(uid->Sponge.getServer().getPlayer(uid).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public void update() {
        for (Map.Entry<UUID, ServerBossBar> e : instances.entrySet()) {
            Player player = Sponge.getServer().getPlayer(e.getKey()).orElse(null);
            if (player == null) continue;
            if (forceShow || !Brotkasten.getInstance().bossBarManager.isMuted(player)) {
                e.getValue()
                        .setVisible(visible)
                        .setPercent(percent)
                        .setName(getNamePlaceholder(name, player))
                        .setColor(color)
                        .setOverlay(overlay)
                ;
            } else {
                e.getValue().setVisible(false);
            }
        }
    }

    private Text getNamePlaceholder(Text template, Player target) {
        return placeholders.replaceSourcePlaceholders(template, target);
    }

}
