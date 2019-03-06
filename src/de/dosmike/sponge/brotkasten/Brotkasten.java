package de.dosmike.sponge.brotkasten;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.io.IOException;

@Plugin(id="brotkasten", name="Brotkasten", version="0.1", authors={"DosMike"})
final public class Brotkasten {
    public static void main(String[] args) { System.err.println("This plugin can not be run as executable!"); }

    static Brotkasten instance = null;
    private static ServerBossBar serverBossBar;

    public Brotkasten() {
        instance = this;
    }

    @Inject
    private Logger logger;

    public static void l(String format, Object... args) {
        instance.logger.info(String.format(format, args));
    }

    public static void w(String format, Object... args) {
        instance.logger.warn(String.format(format, args));
    }

    public ServerBossBar getServerBossBar() {
        return serverBossBar;
    }

    public static Brotkasten getInstance() {
        return instance;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandRegistra.registerCommands();
        loadConfig(null);
        Sponge.getEventManager().registerListeners(this, new EventListener());
        serverBossBar = ServerBossBar.builder()
                .visible(false)
                .darkenSky(false)
                .createFog(false)
                .playEndBossMusic(false)
                .name(Text.of("Template"))
                .percent(1f)
                .color(BossBarColors.WHITE)
                .overlay(BossBarOverlays.PROGRESS)
                .build();

        broadcastTask = Task.builder()
                .execute(this::BroadcastTick)
                .intervalTicks(1)
                .submit(this);
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        if (broadcastTask!=null)
            broadcastTask.cancel();
    }

    @Inject
    @DefaultConfig(sharedRoot = true)
    public ConfigurationLoader<CommentedConfigurationNode> loader;

    void loadConfig(@Nullable CommandSource responsible) {
        try {
            CommentedConfigurationNode root = loader.load(ConfigurationOptions.defaults());

            ConfigurationNode group = root.getNode("BossBar");
            if (group.isVirtual()) {
                ConfigurationLoader<CommentedConfigurationNode> defaults =
                        HoconConfigurationLoader.builder()
                                .setURL(Sponge.getAssetManager()
                                        .getAsset(this, "defaults.conf").get()
                                        .getUrl())
                                .build();
                root.mergeValuesFrom(defaults.load(ConfigurationOptions.defaults()));
                loader.save(root);
            }

            bossBarManager.load(group.getNode("Messages").getList(TypeToken.of(String.class)), responsible==null?Sponge.getServer().getConsole():responsible);
            bossBarManager.setMinDelay(Math.max(1, group.getNode("MinDelay").getInt(20)*20));

            group = root.getNode("Chat");

            chatManager.load(group.getNode("Messages").getList(TypeToken.of(String.class)), responsible==null?Sponge.getServer().getConsole():responsible);
            chatManager.setMinDelay(Math.max(1, group.getNode("MinDelay").getInt(20)*20));
        } catch (IOException|ObjectMappingException e) {
            e.printStackTrace();
            if (responsible != null)
                responsible.sendMessage(Text.of(TextColors.RED, "Config could not be loaded"));
            return;
        }
        if (responsible != null)
            responsible.sendMessage(Text.of(TextColors.GREEN, "Config reloaded"));
    }

    BossBarManager bossBarManager = new BossBarManager();
    ChatManager chatManager = new ChatManager();

    private Task broadcastTask = null;
    private void BroadcastTick() {
        bossBarManager.tick();
        chatManager.tick();
    }

}