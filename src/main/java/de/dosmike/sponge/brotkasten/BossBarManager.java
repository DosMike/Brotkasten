package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class BossBarManager implements IBroadcastManager {

    private List<BossBarConfiguration> bossBars = new LinkedList<>();
    private BossBarConfiguration activeBossBar = null;
    private int activeBossBarIndex = 0;
    private int minDelay=0, passedTime=0;
    private boolean waiting=false;
    private static Set<UUID> hiddenBossBars = new HashSet<>();

    @Override
    public void setMinDelay(int minDelay) {
        this.minDelay = minDelay;
    }

    @Override
    public void load(Collection<String> broadcasts, CommandSource responsible) {
        bossBars.clear();
        for (String s : broadcasts) {
            try {
                bossBars.add(BossBarConfiguration.fromString(s));
            } catch (IllegalArgumentException e) {
                responsible.sendMessage(Text.of(TextColors.RED, e.getMessage()));
            }
        }
    }

    @Override
    public void tick() {
        passedTime++;
        if (activeBossBar == null) {
            if (!bossBars.isEmpty()) {
                activeBossBarIndex = 0;
                next();
            } else {
                if (Brotkasten.getInstance().getServerBossBar().isVisible())
                    Brotkasten.getInstance().getServerBossBar()
                            .setVisible(false)
                            .update();
            }
        } else if (waiting) {
            if (passedTime >= minDelay) {
                if (++activeBossBarIndex >= bossBars.size())
                    activeBossBarIndex = 0;
                next();
            }
        } else if (activeBossBar.tick()) {
            if (passedTime < minDelay) {
                Brotkasten.getInstance().getServerBossBar()
                        .setVisible(false)
                        .update();
                waiting = true;
            } else {
                if (++activeBossBarIndex >= bossBars.size())
                    activeBossBarIndex = 0;
                next();
            }
        }
    }

    public void set(BossBarConfiguration configuration) {
        activeBossBar = configuration;
        if (activeBossBar != null) {
            configuration.apply();
        } else {
            Brotkasten.getInstance().getServerBossBar()
                    .setVisible(false)
                    .update();
        }
        passedTime = 0;
        waiting = false;
    }

    private void next() {
        if (bossBars.isEmpty()) {
            Brotkasten.getInstance().getServerBossBar()
                    .setVisible(false)
                    .update();
        } else {
            activeBossBar = bossBars.get(activeBossBarIndex);
            activeBossBar.apply();
        }
        waiting = false;
        passedTime = 0;
    }

    public void skip() {
        if (bossBars.isEmpty()) {
            Brotkasten.getInstance().getServerBossBar()
                    .setVisible(false)
                    .update();
        } else {
            if (++activeBossBarIndex >= bossBars.size())
                activeBossBarIndex = 0;
            next();
        }
    }
    public void mute(Player player) {
        hiddenBossBars.add(player.getUniqueId());
        if (activeBossBar != null && !activeBossBar.doForceShow()) {
            Brotkasten.getInstance().getServerBossBar().updateMuteState(player);
        }
    }
    public void unmute(Player player) {
        hiddenBossBars.remove(player.getUniqueId());
        if (activeBossBar != null && !activeBossBar.doForceShow()) {
            Brotkasten.getInstance().getServerBossBar().updateMuteState(player);
        }
    }
    public boolean isMuted(Player player) {
        return hiddenBossBars.contains(player.getUniqueId());
    }

}
