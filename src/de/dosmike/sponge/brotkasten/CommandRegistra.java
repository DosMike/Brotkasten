package de.dosmike.sponge.brotkasten;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandFlags;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;

final public class CommandRegistra {

    public static void registerCommands() {
        Sponge.getCommandManager().register(Brotkasten.getInstance(), CommandSpec.builder()
                .permission("brotkasten.command.brotkasten")
                .description(Text.of("Start a round of minesweeper"))
                .arguments(
                        GenericArguments.none()
                ).child(CommandSpec.builder()
                        .permission("brotkasten.command.brotkasten.reload")
                        .description(Text.of("Reload the config"))
                        .executor((src, args)->{
                            Brotkasten.getInstance().loadConfig(src);
                            return CommandResult.success();
                        })
                .build(), "reload")
        .build(), "brotkasten", "brot");

        Sponge.getCommandManager().register(Brotkasten.getInstance(), CommandSpec.builder()
                .permission("brotkasten.command.bossbar")
                .description(Text.of("Start a round of minesweeper"))
                .arguments(
                        GenericArguments.none()
                ).child(CommandSpec.builder()
                        .permission("brotkasten.command.bossbar.set")
                        .description(Text.of("Overwrite the bossbar"))
                        .arguments(
                                GenericArguments.flags()
                                        .valueFlag(GenericArguments.string(Text.of("seconds")), "-time")
                                        .valueFlag(GenericArguments.string(Text.of("color")), "-color")
                                .buildWith(GenericArguments.remainingJoinedStrings(Text.of("message")))
                        )
                        .executor((src, args)->{
                            try {
                                BossBarColor color;
                                BossBarOverlay overlay = BossBarOverlays.NOTCHED_12;
                                Text display = TextSerializers.FORMATTING_CODE.deserialize(args.<String>getOne("message").orElse(""));
                                int displayTime;
                                String tmp = args.<String>getOne("seconds").orElse("infinite");
                                if (tmp.equalsIgnoreCase("infinite"))
                                    displayTime = -1;
                                else try {
                                    displayTime = Integer.valueOf(tmp);
                                } catch (NumberFormatException n) {
                                    throw new CommandException(Text.of(TextColors.RED, "Number or 'infinite' expected for -time"));
                                }
                                tmp = args.<String>getOne("color").orElse("purple");
                                if (tmp.equalsIgnoreCase("white")) {
                                    color = BossBarColors.WHITE;
                                } else if (tmp.equalsIgnoreCase("green")) {
                                    color = BossBarColors.GREEN;
                                } else if (tmp.equalsIgnoreCase("blue")) {
                                    color = BossBarColors.BLUE;
                                } else if (tmp.equalsIgnoreCase("red")) {
                                    color = BossBarColors.RED;
                                } else if (tmp.equalsIgnoreCase("yellow")) {
                                    color = BossBarColors.YELLOW;
                                } else if (tmp.equalsIgnoreCase("pink")) {
                                    color = BossBarColors.PINK;
                                } else if (tmp.equalsIgnoreCase("purple")) {
                                    color = BossBarColors.PURPLE;
                                } else {
                                    throw new CommandException(Text.of(TextColors.RED, "Invalid value for -color; 'white', 'green', 'blue', 'red', 'yellow', 'pink' or 'purple' expected"));
                                }
                                BossBarConfiguration bbc = new BossBarConfiguration(
                                        color, overlay, display, displayTime
                                );
                                Brotkasten.getInstance().bossBarManager.set(bbc);

                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                throw new CommandException(Text.of(TextColors.RED, e.getMessage()));
                            }
                            return CommandResult.success();
                        })
                        .build(), "set")
                .child(CommandSpec.builder()
                        .permission("brotkasten.command.bossbar.skip")
                        .description(Text.of("Start the next boss bar immediately"))
                        .arguments(
                                GenericArguments.none()
                        )
                        .executor((src, args)->{
                            try {
                                Brotkasten.getInstance().bossBarManager.skip();
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                                throw new CommandException(Text.of(TextColors.RED, e.getMessage()));
                            }
                            src.sendMessage(Text.of(TextColors.GREEN, "BossBars Continued"));
                            return CommandResult.success();
                        })
                        .build(), "skip", "next", "continue")
                .build(), "bossbar", "bb");
    }

}
