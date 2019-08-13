package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.*;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class BossBarWrapper {

    protected Text name = Text.EMPTY;
    protected float percent = 0f;
    protected BossBarColor color = BossBarColors.WHITE;
    protected BossBarOverlay overlay = BossBarOverlays.PROGRESS;

    //I'm not using them and setCreateFog craps it's pants with recursion on forge
    protected boolean darkenSky = false, playMusic = false, createFog = false;

    protected final UUID uid = UUID.randomUUID();
    protected boolean visible = true;
    protected Set<UUID> players = new HashSet<>();
    private ServerBossBar instance;

    public BossBarWrapper() throws NoSuchElementException {
        instance = ServerBossBar.builder()
                .name(Text.EMPTY)
                .visible(false)
                .darkenSky(false)
                .createFog(false)
                .playEndBossMusic(false)
                .percent(1f)
                .color(BossBarColors.WHITE)
                .overlay(BossBarOverlays.PROGRESS)
                .build();
    }

    public Text getName() {
        return name;
    }

    public BossBarWrapper setName(Text name) {
        this.name = name;
        update();
        return this;
    }

    public float getPercent() {
        return percent;
    }

    public BossBarWrapper setPercent(float percent) {
        this.percent = percent;
        update();
        return this;
    }

    public BossBarColor getColor() {
        return color;
    }

    public BossBarWrapper setColor(BossBarColor color) {
        this.color = color;
        update();
        return this;
    }

    public BossBarOverlay getOverlay() {
        return overlay;
    }

    public BossBarWrapper setOverlay(BossBarOverlay overlay) {
        this.overlay = overlay;
        update();
        return this;
    }

    public UUID getUniqueId() {
        return uid;
    }

    public BossBarWrapper setVisible(boolean visible) {
        this.visible = visible;
        update();
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public BossBarWrapper addPlayer(Player player) {
        instance.addPlayer(player);
        return this;
    }

    public BossBarWrapper removePlayer(Player player) {
        instance.removePlayer(player);
        return this;
    }

    public Set<Player> getPlayers() {
        return players.stream()
                .map(uid->Sponge.getServer().getPlayer(uid).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    protected void update() {
        instance
            .setVisible(visible)
            .setPercent(percent)
            .setName(name)
            .setColor(color)
            .setOverlay(overlay)
        ;
    }

}
