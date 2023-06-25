package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandData;
import net.minestom.server.command.builder.CommandResult;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;
import net.minestom.server.utils.location.RelativeVec;

import java.util.List;

import static com.spanner.basics.util.ConditionBuilder.condition;
import static com.spanner.basics.util.TextUtil.entityName;

public class TeleportCommand extends Command {

    Basics main = Basics.getInstance();
    MiniMessage mm = main.mm();
    public TeleportCommand() {
        super("teleport", "tp");

        setCondition(condition()
                .permission("basics.teleport")
                .get());
        setDefaultExecutor((sender, context) -> {
            context.getMap().forEach((string, obj) -> {
                sender.sendMessage(string + " " + obj.toString());
            });
            sender.sendMessage(mm.deserialize("<red>Usage: /teleport <location|destination>"));
        });

        var locationArg = ArgumentType.RelativeVec3("location");
        var entitiesArg = ArgumentType.Entity("targets");
        var destinationEntityArg = ArgumentType.Entity("destination");


        addConditionalSyntax(
                condition().player().permission("basics.teleport.location").get(),
                this::tpPos,
                locationArg
        );

        addConditionalSyntax(
                condition().player().permission("basics.teleport.destination").get(),
                this::tpDest,
                destinationEntityArg
        );

        /*
        Until issue #1327 is fixed, there is no way that I can implement vanilla teleportation behaviour.
        This is because when running (for example) /teleport @e[type=minecraft:pig,limit=1] @p, it will
        interpret the first argument as destinationEntityArg for some reason, and fail to evaluate the next part.

        I can only guess as to why this happens, maybe the parser doesn't go back in the tree if the argument
        type matched, but it reached the end of a syntax.

        (https://github.com/Minestom/Minestom/issues/1327)


        addConditionalSyntax(
                condition().player().permission("basics.teleport.others.location").get(),
                this::tpPos,
                entitiesArg, locationArg
        );

        addConditionalSyntax(
                condition().player().permission("basics.teleport.others.destination").get(),
                this::tpDest,
                entitiesArg, destinationEntityArg
        );
        */
    }

    private void tpPos(CommandSender sender, CommandContext context) {
        Player p = (Player) sender;

        EntityFinder finder = context.get("targets");
        List<Entity> entities;
        if (finder != null) {
            entities = finder.find(sender);
        } else {
            entities = List.of(p);
        }

        RelativeVec relativeVec = context.get("location");
        Pos pos = relativeVec.from(p).asPosition();

        entities.forEach(entity -> entity.teleport(pos) );
    }

    private void tpDest(CommandSender sender, CommandContext context) {
        Player p = (Player) sender;

        EntityFinder finder = context.get("targets");
        List<Entity> entities;
        if (finder != null) {
            entities = finder.find(sender);
        } else {
            entities = List.of(p);
        }

        EntityFinder dest = context.get("destination");
        List<Entity> destEntitys = dest.find(sender);
        if (destEntitys.size() != 1) {
            getDefaultExecutor().apply(sender,context); // Note: vanilla doesn't do this, it just returns. but we're not vanilla
            return;
        }
        Entity destEntity = destEntitys.get(0);

        entities.forEach(entity -> {
            sender.sendMessage(mm.deserialize(
                    "Teleported <target> to <destination>",
                    Placeholder.component("target",entityName(entity)),
                    Placeholder.component("destination",entityName(destEntity))
            ));
            entity.teleport(destEntity.getPosition());
        } );
    }

}
