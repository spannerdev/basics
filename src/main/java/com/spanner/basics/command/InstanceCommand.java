package com.spanner.basics.command;

import com.spanner.basics.Basics;
import com.spanner.basics.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.*;

public class InstanceCommand extends Command {

    CommandCondition permittedCondition = ((sender, commandString) ->
            sender.hasPermission("basics.instance")
    );
    CommandCondition permittedPlayerCondition = ((sender, commandString) ->
            sender instanceof Player &&
            sender.hasPermission("basics.instance")
    );

    ArgumentLiteral _list = ArgumentType.Literal("list");
    ArgumentLiteral _goto = ArgumentType.Literal("goto");

    Basics main = Basics.getInstance();
    MiniMessage mm = main.mm();
    public InstanceCommand() {
        super("instance");

        setCondition(permittedCondition);
        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage(mm.deserialize("<red>Usage: /instance <list|goto>"));
        }));

        /*
        The reason this command has not been added is because of this argument.
        For whatever reason, the SuggestionCallback isn't being called.
        See my help post below (I may raise this to being an issue later)

        (https://github.com/Minestom/Minestom/discussions/1885)
        */
        var instanceIdArg = ArgumentType.UUID("instance id") // TODO: I fear that this is mega mega inefficient
                .setSuggestionCallback(
                    (sender,context,suggestion) -> {
                        System.out.println("hi");
                        MinecraftServer.getInstanceManager().getInstances().stream()
                        .map(Instance::getUniqueId)
                        .map(UUID::toString)
                        .map(SuggestionEntry::new)
                        .forEach(suggestionEntry -> {
                            System.out.println(suggestionEntry.getEntry());
                            suggestion.addEntry(suggestionEntry);
                        });
                });
        var entityArg = ArgumentType.Entity("entity(s)")
                .setDefaultValue(new EntityFinder().setTargetSelector(EntityFinder.TargetSelector.SELF));

        addConditionalSyntax(permittedCondition, this::listInstances, _list);

        addConditionalSyntax(permittedPlayerCondition, this::gotoInstance, _goto, instanceIdArg);
        addConditionalSyntax(permittedCondition, this::gotoInstance, _goto, instanceIdArg, entityArg);
    }

    private void listInstances(CommandSender sender, CommandContext context) {
        Set<Instance> instances = MinecraftServer.getInstanceManager().getInstances();

        List<List<String>> rows = new ArrayList<>();
        for (Instance instance : instances) {
            String className = instance.getClass().getSimpleName();
            String uuid = instance.getUniqueId().toString();

            rows.add(List.of(uuid, className));
        }

        List<Component> messages = TextUtil.createTable(List.of("UUID","Class"), rows);
        messages.forEach(sender::sendMessage);

    }

    private void gotoInstance(CommandSender sender, CommandContext context) {
        UUID instanceId = context.get("instanceid");
        EntityFinder entityFinder = context.get("entity(s)");
        List<Entity> entities = entityFinder.find(sender);

        Instance i = MinecraftServer.getInstanceManager().getInstance(instanceId);
        if (i == null) {
            sender.sendMessage(mm.deserialize("<red>Instance not found. Use /instance list to see a list of instances"));
            return;
        }
        entities.forEach(entity -> {
            if (entity.getInstance() == i) {
                sender.sendMessage(mm.deserialize("<yellow>Entity already in that instance, skipping"));
            } else {
                entity.setInstance(i);
            }
        });
    }

}
