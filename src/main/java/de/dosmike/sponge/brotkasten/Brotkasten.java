package de.dosmike.sponge.brotkasten;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import de.dosmike.sponge.VersionChecker;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
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

@Plugin(id="brotkasten", name="Brotkasten", version="0.4.1")
final public class Brotkasten {
    public static void main(String[] args) { System.err.println("This plugin can not be run as executable!"); }

    static Brotkasten instance = null;
    private static BossBarWrapper serverBossBar;
    private static ServerChat serverChat;

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

    public BossBarWrapper getServerBossBar() {
        return serverBossBar;
    }

    public static Brotkasten getInstance() {
        return instance;
    }

    public ServerChat getServerChat() {
        return serverChat;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandRegistra.registerCommands();
        Sponge.getEventManager().registerListeners(this, new EventListener());
        try {
            //Check if PlaceHolderAPI is present
            getClass().getClassLoader().loadClass("me.rojo8399.placeholderapi.impl.PlaceholderAPIPlugin");
            //use placeholder API
            serverBossBar = new PlaceholderBossBarWrapper();
            serverChat = new PlaceholderServerChat();
        } catch (Exception e) {
            serverBossBar = new BossBarWrapper();
            serverChat = new ServerChat();
        }
        loadConfig(null);
        VersionChecker.checkVersion(this);

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
            chatManager.setMinDelay(Math.max(1, group.getNode("Delay").getInt(20)*20));

            CommentedConfigurationNode vcnode = root.getNode("VersionChecker");
            if (vcnode.isVirtual()) { //patch value into config if missing
                vcnode.setValue(false);
                vcnode.setComment("It's strongly recommended to enable automatic version checking,\n" +
                        "This will also inform you about changes in dependencies.\n" +
                        "Set this value to true to allow this Plugin to check for Updates on Ore");
                loader.save(root);
                VersionChecker.setVersionCheckingEnabled(
                        Sponge.getPluginManager().fromInstance(this).get().getId(),
                        false);
            } else {
                VersionChecker.setVersionCheckingEnabled
                        (Sponge.getPluginManager().fromInstance(this).get().getId(),
                        vcnode.getBoolean(false)
                        );
            }
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
