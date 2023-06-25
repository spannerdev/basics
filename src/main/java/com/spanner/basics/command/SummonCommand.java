package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.utils.location.RelativeVec;

import java.util.Objects;

public class SummonCommand extends Command {

    CommandCondition condition = ((sender, commandString) ->
            sender instanceof Player &&
            sender.hasPermission("basics.summon")
    );

    Basics main = Basics.getInstance();
    MiniMessage mm = main.mm();
    public SummonCommand() {
        super("summon");

        setCondition(condition);
        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage(mm.deserialize("<red>Usage: /summon <entity type> [position] [class]"));
        }));

        var entityTypeArg = ArgumentType.EntityType("entity type");
        var positionArg = ArgumentType.RelativeVec3("position")
                .setDefaultValue(new RelativeVec(Vec.ZERO, RelativeVec.CoordinateType.RELATIVE, true,true,true));
        var classArg = ArgumentType.Enum("class", EntityClass.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED)
                .setDefaultValue(EntityClass.BASE);

        addConditionalSyntax(condition, this::summon, entityTypeArg);
        addConditionalSyntax(condition, this::summon, entityTypeArg, positionArg);
        addConditionalSyntax(condition, this::summon, entityTypeArg, positionArg, classArg);

    }

    private void summon(CommandSender sender, CommandContext context) {
        Player p = (Player) sender;

        EntityType entityType = context.get("entity type");
        RelativeVec relativeVec = context.get("position");
        EntityClass entityClass = context.get("class");

        Vec position = relativeVec.from(p);
        Entity e = entityClass.instantiate(entityType);
        e.setInstance(Objects.requireNonNull(p.getInstance()), position);
    }

    // From https://github.com/Minestom/Minestom @ demo/src/main/java/net/minestom/demo/commands/SummonCommand.Java
    private enum EntityClass {
        BASE(Entity::new),
        LIVING(LivingEntity::new),
        CREATURE(EntityCreature::new);
        private final EntityFactory factory;

        EntityClass(EntityFactory factory) {
            this.factory = factory;
        }

        public Entity instantiate(EntityType type) {
            return factory.newInstance(type);
        }
    }

    interface EntityFactory {
        Entity newInstance(EntityType type);
    }

}
