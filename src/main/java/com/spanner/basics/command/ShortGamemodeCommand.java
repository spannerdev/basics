package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.GameMode;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.HashMap;
import java.util.Map;

import static com.spanner.basics.util.ConditionBuilder.condition;


public class ShortGamemodeCommand extends Command {

    Map<String, GameMode> modeMap = new HashMap<>();

    Argument<EntityFinder> targetsArg = ArgumentType.Entity("targets")
            .onlyPlayers(true)
            .setDefaultValue(new EntityFinder().setTargetSelector(EntityFinder.TargetSelector.SELF));

    CommandCondition condition = condition().player().permission("basics.gamemode.*").get();

    CommandExecutor executor = (sender, context) -> {
        GameMode gameMode = modeMap.get(context.getCommandName());
        assert gameMode != null;

        GamemodeCommand.setGameMode(sender, context, gameMode);
    };

    Basics main = Basics.getInstance();
    MiniMessage mm = main.mm();
    public ShortGamemodeCommand() {
        super("gmc","gms","gma","gmsp");

        modeMap.put("gmc", GameMode.CREATIVE);
        modeMap.put("gms", GameMode.SURVIVAL);
        modeMap.put("gma", GameMode.ADVENTURE);
        modeMap.put("gmsp", GameMode.SPECTATOR);

        setCondition(condition); // TODO: Should check permissions on the fly
        setDefaultExecutor(executor);

        addConditionalSyntax(
                condition,
                executor,
                targetsArg
        );

    }

}
