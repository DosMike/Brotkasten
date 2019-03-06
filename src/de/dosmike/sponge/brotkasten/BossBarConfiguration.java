package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.boss.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Wraps values for a single BossBar */
public class BossBarConfiguration {

    private Text display = Text.EMPTY;
    private BossBarColor color = BossBarColors.WHITE;
    private BossBarOverlay overlay = BossBarOverlays.PROGRESS;
    private Float from=0f, to=1f;
    private int displayTime=200, passedTime;
    private int ticks=-1;

    private BossBarConfiguration() {}
    public BossBarConfiguration(BossBarColor color, BossBarOverlay overlay, Text display, int displayTime) {
        this.color = color;
        this.overlay = overlay;
        this.display = display;
        this.ticks=-1;
        if (displayTime <= 0) {
            this.from=1f;
            this.to=1f;
            this.displayTime=-1;
        } else {
            this.from=1f;
            this.to=0f;
            this.displayTime = displayTime*20;
        }
    }

    public void apply() {
        ServerBossBar bar = Brotkasten.getInstance().getServerBossBar();
        bar.setName(display);
        bar.setColor(color);
        bar.setOverlay(overlay);
        bar.setPercent(from);
        bar.setVisible(true);
        passedTime = 0;
    }

    /** @return true if all game ticks for this bossbar passed */
    public boolean tick() {
        passedTime++;
        BossBar bar = Brotkasten.getInstance().getServerBossBar();
        float newPercent = displayTime<1?1f:(float)(from+(to-from)*((double)passedTime/displayTime));
        if (ticks > 0)
            newPercent = (float)Math.floor(ticks * newPercent)/ticks;
        bar.setPercent(Math.max(0f, Math.min(1f, newPercent)));
        if (displayTime <= 0)
            return false; //never finish
        else
            return (passedTime >= displayTime); //time has passed
    }

    private static final Pattern p = Pattern.compile("(?:\\{([^}]*)})?(.*)");
    private static final Pattern n = Pattern.compile("([0-9]+(?:[,.][0-9]+)?).*");
    public static BossBarConfiguration fromString(String s) throws IllegalArgumentException {
        Matcher m = p.matcher(s);
        if (!m.matches())
            throw new IllegalArgumentException("Invalid Broascast format for BossBar");
        String[] args;
        {
            String tmp = m.group(1);
            if (tmp == null)
                args = new String[0];
            else
                args = tmp.split(",");
        }
        boolean step = false;
        BossBarConfiguration result = new BossBarConfiguration();
        result.display = TextSerializers.FORMATTING_CODE.deserialize(m.group(2).trim());
        int pa=0;
        for (String arg : args) {
            arg = arg.trim();
            if (arg.endsWith("%")) {
                pa++;
                Float f = getFloat(arg);
                if (f == null)
                    throw new IllegalArgumentException("Invalid percent format");
                else if (pa==1)
                    result.from = f/100f;
                else if (pa==2)
                    result.to = f/100f;
                else
                    throw new IllegalArgumentException("Third percent value can't be assigned");
            } else if (arg.endsWith("sec")) {
                Float time = getFloat(arg);
                if (time == null)
                    throw new IllegalArgumentException("Invalid time format, use 10sec or 1mim");
                if (time <= 0)
                    throw new IllegalArgumentException("Please specify a positive timespan");
                result.displayTime = (int)(time * 20);
                Brotkasten.l("Setting displaytime for %s to %d ticks", result.display.toPlain(), result.displayTime);
            } else if (arg.endsWith("min")) {
                Float time = getFloat(arg);
                if (time == null)
                    throw new IllegalArgumentException("Invalid time format, use 10sec or 1min");
                if (time <= 0)
                    throw new IllegalArgumentException("Please specify a positive timespan");
                Brotkasten.l("Setting displaytime for %s to %d ticks", result.display.toPlain(), result.displayTime);
                result.displayTime = (int) (time * 1200);
            } else if (arg.equalsIgnoreCase("infinite")) {
                result.displayTime = -1;
            } else if (arg.endsWith("ticks")) {
                Float amount = getFloat(arg);
                if (amount == null)
                    throw new IllegalArgumentException("Invalid tick format, use 0, 6, 10, 12 or 20 ticks");
                switch (amount.intValue()) {
                    case 0: {
                        result.overlay = BossBarOverlays.PROGRESS;
                        result.ticks = -1;
                        break;
                    }
                    case 6: {
                        result.overlay = BossBarOverlays.NOTCHED_6;
                        result.ticks = 6;
                        break;
                    }
                    case 10: {
                        result.overlay = BossBarOverlays.NOTCHED_10;
                        result.ticks = 10;
                        break;
                    }
                    case 12: {
                        result.overlay = BossBarOverlays.NOTCHED_12;
                        result.ticks = 12;
                        break;
                    }
                    case 20: {
                        result.overlay = BossBarOverlays.NOTCHED_20;
                        result.ticks = 20;
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid amount of ticks, use 0, 6, 10, 12 or 20 ticks");
                    }
                }
            } else if (arg.equalsIgnoreCase("step")) {
                step = true;
            } else if (arg.equalsIgnoreCase("white")) {
                result.color = BossBarColors.WHITE;
            } else if (arg.equalsIgnoreCase("green")) {
                result.color = BossBarColors.GREEN;
            } else if (arg.equalsIgnoreCase("blue")) {
                result.color = BossBarColors.BLUE;
            } else if (arg.equalsIgnoreCase("red")) {
                result.color = BossBarColors.RED;
            } else if (arg.equalsIgnoreCase("yellow")) {
                result.color = BossBarColors.YELLOW;
            } else if (arg.equalsIgnoreCase("pink")) {
                result.color = BossBarColors.PINK;
            } else if (arg.equalsIgnoreCase("purple")) {
                result.color = BossBarColors.PURPLE;
            }
        }

        if (!step) result.ticks=-1;

        return result;
    }

    private static Float getFloat(String s) {
        Matcher m = n.matcher(s);
        if (m.matches())
            return Float.parseFloat(m.group(1));
        return null;
    }

}
