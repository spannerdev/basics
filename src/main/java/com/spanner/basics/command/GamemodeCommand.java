package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

import static com.spanner.basics.util.ConditionBuilder.condition;

public class GamemodeCommand extends Command {

    Argument<EntityFinder> targetsArg = ArgumentType.Entity("targets")
            .onlyPlayers(true)
            .setDefaultValue(new EntityFinder().setTargetSelector(EntityFinder.TargetSelector.SELF));

    Basics main = Basics.getInstance();
    MiniMessage mm = main.mm();
    public GamemodeCommand() {
        super("gamemode", "gm");

        setCondition(condition().player().permission("basics.gamemode").get());
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(mm.deserialize("<red>Usage: /gamemode <game mode> [targets]"));
        });

        addSubcommand(new SurvivalCommand());
        addSubcommand(new CreativeCommand());
        addSubcommand(new AdventureCommand());
        addSubcommand(new SpectatorCommand());

    }

    protected static void setGameMode(CommandSender sender, CommandContext context, GameMode gameMode) {
        EntityFinder entityFinder = context.get("targets");
        List<Entity> targets = entityFinder.find(sender);
        for (Entity target : targets) {
            if (target instanceof Player p) {
                p.setGameMode(gameMode);
            }
        }

    }

    private class SurvivalCommand extends Command {
        CommandExecutor executor = (sender,context)->setGameMode(sender,context,GameMode.SURVIVAL);
        private SurvivalCommand() {
            super("survival", "s");

            setCondition(condition().player().permission("basics.gamemode.survival").get());
            setDefaultExecutor(executor);

            addConditionalSyntax(
                    condition().player().permission("basics.gamemode.others.survival").get(),
                    executor,
                    targetsArg
            );
        }
    }

    private class CreativeCommand extends Command {
        CommandExecutor executor = (sender,context)->setGameMode(sender,context,GameMode.CREATIVE);
        private CreativeCommand() {
            super("creative", "c");

            setCondition(condition().player().permission("basics.gamemode.creative").get());
            setDefaultExecutor(executor);

            addConditionalSyntax(
                    condition().player().permission("basics.gamemode.others.creative").get(),
                    executor,
                    targetsArg
            );
        }
    }

    private class AdventureCommand extends Command {
        CommandExecutor executor = (sender,context)->setGameMode(sender,context,GameMode.ADVENTURE);
        private AdventureCommand() {
            super("adventure", "a");

            setCondition(condition().player().permission("basics.gamemode.adventure").get());
            setDefaultExecutor(executor);

            addConditionalSyntax(
                    condition().player().permission("basics.gamemode.others.adventure").get(),
                    executor,
                    targetsArg
            );
        }
    }

    private class SpectatorCommand extends Command {
        CommandExecutor executor = (sender,context)->setGameMode(sender,context,GameMode.SPECTATOR);
        private SpectatorCommand() {
            super("spectator", "sp");

            setCondition(condition().player().permission("basics.gamemode.spectator").get());
            setDefaultExecutor(executor);

            addConditionalSyntax(
                    condition().player().permission("basics.gamemode.others.spectator").get(),
                    executor,
                    targetsArg
            );
        }
    }

}
