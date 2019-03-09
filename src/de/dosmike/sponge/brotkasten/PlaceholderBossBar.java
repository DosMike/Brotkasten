package de.dosmike.sponge.brotkasten;

import me.rojo8399.placeholderapi.PlaceholderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.*;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class PlaceholderBossBar implements ServerBossBar {

    private Text name = Text.EMPTY;
    private float percent = 0f;
    private BossBarColor color = BossBarColors.WHITE;
    private BossBarOverlay overlay = BossBarOverlays.PROGRESS;
    private boolean darkenSky = false, playMusic = false, createFog = false;
    private final UUID uid = UUID.randomUUID();
    private boolean visible = true;
    private Set<UUID> players = new HashSet<>();
    private Map<UUID, ServerBossBar> instances = new HashMap<>();
    private PlaceholderService placeholders;

    public PlaceholderBossBar() throws NoSuchElementException {
        placeholders = Sponge.getServiceManager().provide(PlaceholderService.class).get();
    }

    @Override
    public Text getName() {
        return name;
    }

    /** Set the name of this BossBar.<br>
     * Accepts placeholders */
    @Override
    public PlaceholderBossBar setName(Text name) {
        this.name = name;
        update();
        return this;
    }

    @Override
    public float getPercent() {
        return percent;
    }

    @Override
    public PlaceholderBossBar setPercent(float percent) {
        this.percent = percent;
        update();
        return this;
    }

    @Override
    public BossBarColor getColor() {
        return color;
    }

    @Override
    public PlaceholderBossBar setColor(BossBarColor color) {
        this.color = color;
        update();
        return this;
    }

    @Override
    public BossBarOverlay getOverlay() {
        return overlay;
    }

    @Override
    public PlaceholderBossBar setOverlay(BossBarOverlay overlay) {
        this.overlay = overlay;
        update();
        return this;
    }

    @Override
    public boolean shouldDarkenSky() {
        return darkenSky;
    }

    @Override
    public PlaceholderBossBar setDarkenSky(boolean darkenSky) {
        this.darkenSky = darkenSky;
        update();
        return this;
    }

    @Override
    public boolean shouldPlayEndBossMusic() {
        return playMusic;
    }

    @Override
    public PlaceholderBossBar setPlayEndBossMusic(boolean playEndBossMusic) {
        this.playMusic = playEndBossMusic;
        update();
        return this;
    }

    @Override
    public boolean shouldCreateFog() {
        return createFog;
    }

    @Override
    public PlaceholderBossBar setCreateFog(boolean createFog) {
        this.createFog = createFog;
        update();
        return this;
    }

    @Override
    public UUID getUniqueId() {
        return uid;
    }

    @Override
    public PlaceholderBossBar setVisible(boolean visible) {
        this.visible = visible;
        update();
        return this;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public PlaceholderBossBar addPlayer(Player player) {
        ServerBossBar bar = ServerBossBar.builder()
                .percent(getPercent())
                .overlay(getOverlay())
                .color(getColor())
                .name(getName())
                .playEndBossMusic(shouldPlayEndBossMusic())
                .createFog(shouldCreateFog())
                .darkenSky(shouldDarkenSky())
                .visible(isVisible())
                .build()
                .addPlayer(player);
        players.add(player.getUniqueId());
        instances.put(player.getUniqueId(), bar);
        return this;
    }

    @Override
    public PlaceholderBossBar removePlayer(Player player) {
        players.remove(player.getUniqueId());
        ServerBossBar bar = instances.remove(player.getUniqueId());
        if (bar != null)
            bar.removePlayers(bar.getPlayers());
        return this;
    }

    @Override
    public Set<Player> getPlayers() {
        return players.stream()
                .map(uid->Sponge.getServer().getPlayer(uid).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void update() {
        for (Map.Entry<UUID, ServerBossBar> e : instances.entrySet()) {
            Player player = Sponge.getServer().getPlayer(e.getKey()).orElse(null);
            if (player == null) continue;
            e.getValue()
                    .setVisible(isVisible())
                    .setPercent(getPercent())
                    .setName(getNamePlaceholder(getName(), player))
                    .setColor(getColor())
                    .setOverlay(getOverlay())
                    .setCreateFog(shouldCreateFog())
                    .setDarkenSky(shouldDarkenSky())
                    .setPlayEndBossMusic(shouldPlayEndBossMusic());
        }
    }

    private Text getNamePlaceholder(Text template, Player target) {
        return placeholders.replaceSourcePlaceholders(template, target);
    }

}
