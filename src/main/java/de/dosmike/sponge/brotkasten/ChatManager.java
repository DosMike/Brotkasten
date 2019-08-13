package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager implements IBroadcastManager {

    private int messageIndex=0;
    private int messageCount=0;
    private int minDelay = 0, passedTime = 0;

    @Override
    public void setMinDelay(int ticks) {
        this.minDelay = ticks;
    }

    private static final Pattern p = Pattern.compile("\\[(.+)]\\((.+)\\)");

    @Override
    public void load(Collection<String> broadcasts, CommandSource responsible) {
        messageCount = broadcasts.size();
        Brotkasten.getInstance().getServerChat().clearCache();
        for (String s : broadcasts) {
            Brotkasten.getInstance().getServerChat().addMessage(s, responsible);
        }
    }

    public static Text deserializeUrlString(String FORMATSTRING, CommandSource logTo) {
        int previousEnd=0;
        Text.Builder builder = Text.builder();
        Matcher m = p.matcher(FORMATSTRING);
        while (m.find()) {
            if (m.start()>previousEnd) {
                String sub = FORMATSTRING.substring(previousEnd, m.start());
                builder.append(TextSerializers.FORMATTING_CODE.deserialize(sub));
            }
            Text.Builder clickable = Text.builder();
            clickable.append(TextSerializers.FORMATTING_CODE.deserialize(m.group(1)));
            String act = m.group(2);
            if (act.length()>1 && act.charAt(0)=='/') {
                clickable.onClick(TextActions.runCommand(act));
                clickable.onHover(TextActions.showText(Text.of("Run command: ", act)));
            } else {
                try {
                    clickable.onClick(TextActions.openUrl(new URL(act)));
                    clickable.onHover(TextActions.showText(Text.of("Visit: ", act)));
                } catch (MalformedURLException e) {
                    logTo.sendMessage(Text.of(TextColors.RED, "Invalid or broken text action ", act , " at: ", FORMATSTRING));
                    return null;
                }
            }
            builder.append(clickable.build());

            previousEnd = m.end();
        }
        if (previousEnd < FORMATSTRING.length()-1) {
            String sub = FORMATSTRING.substring(previousEnd, FORMATSTRING.length());
            builder.append(TextSerializers.FORMATTING_CODE.deserialize(sub));
        }
        return builder.build();
    }

    @Override
    public void tick() {
        if (messageCount==0) return;
        if (++passedTime>=minDelay) {
            if (++messageIndex >= messageCount) {
                messageIndex = 0;
            }
            Brotkasten.getInstance().getServerChat().broadcast(messageIndex);
            passedTime=0;
        }
    }
}
