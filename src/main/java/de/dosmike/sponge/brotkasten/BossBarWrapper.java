package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.*;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class BossBarWrapper {

    protected HashMap<UUID, ServerBossBar> instances = new HashMap<>();

    protected Text name = Text.EMPTY;
    protected float percent = 0f;
    protected BossBarColor color = BossBarColors.WHITE;
    protected BossBarOverlay overlay = BossBarOverlays.PROGRESS;

    //I'm not using them and setCreateFog craps it's pants with recursion on forge
    protected boolean darkenSky = false, playMusic = false, createFog = false;

    protected final UUID uid = UUID.randomUUID();
    protected boolean visible = true;
    protected Set<UUID> players = new HashSet<>();
    protected boolean forceShow = false;

    public BossBarWrapper() throws NoSuchElementException {
    }

    public void setForceShow(boolean force) {
        forceShow = force;
    }
    public boolean doForceShow() {
        return forceShow;
    }

    public Text getName() {
        return name;
    }

    public BossBarWrapper setName(Text name) {
        this.name = name;
        return this;
    }

    public float getPercent() {
        return percent;
    }

    public BossBarWrapper setPercent(float percent) {
        this.percent = percent;
        return this;
    }

    public BossBarColor getColor() {
        return color;
    }

    public BossBarWrapper setColor(BossBarColor color) {
        this.color = color;
        return this;
    }

    public BossBarOverlay getOverlay() {
        return overlay;
    }

    public BossBarWrapper setOverlay(BossBarOverlay overlay) {
        this.overlay = overlay;
        return this;
    }

    public UUID getUniqueId() {
        return uid;
    }

    public BossBarWrapper setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public BossBarWrapper addPlayer(Player player) {
        ServerBossBar bar = ServerBossBar.builder()
                .percent(percent)
                .overlay(overlay)
                .color(color)
                .name(name)
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

    public BossBarWrapper removePlayer(Player player) {
        players.remove(player.getUniqueId());
        ServerBossBar bar = instances.remove(player.getUniqueId());
        if (bar != null)
            bar.removePlayers(bar.getPlayers());
        return this;
    }

    public Set<Player> getPlayers() {
        return players.stream()
                .map(uid->Sponge.getServer().getPlayer(uid).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /** Needs to be called after performing changes to go through all
     * bossbars for the different players and actually display the changes */
    public void update() {
        for (Map.Entry<UUID, ServerBossBar> e : instances.entrySet()) {
            Player player = Sponge.getServer().getPlayer(e.getKey()).orElse(null);
            if (player == null) continue;
            if (forceShow || !Brotkasten.getInstance().bossBarManager.isMuted(player)) {
                e.getValue()
                        .setVisible(visible)
                        .setPercent(percent)
                        .setName(name)
                        .setColor(color)
                        .setOverlay(overlay)
                ;
            } else {
                e.getValue().setVisible(false);
            }
        }
    }

    public void updateMuteState(Player player) {
        if (players.contains(player.getUniqueId())) {
            if (!forceShow && Brotkasten.getInstance().bossBarManager.isMuted(player)) {
                instances.get(player.getUniqueId()).setVisible(false);
            } else {
                instances.get(player.getUniqueId()).setVisible(visible);
            }
        }
    }

}
