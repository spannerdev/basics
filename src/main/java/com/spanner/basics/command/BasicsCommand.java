package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.CommandCondition;

public class BasicsCommand extends Command {

    CommandCondition condition = ((sender, commandString) ->
            sender instanceof ConsoleSender ||
            sender instanceof ServerSender ||
            sender.hasPermission("basics")
    );

    Basics main;
    MiniMessage mm = MiniMessage.miniMessage();
    public BasicsCommand(Basics main) {
        super("basics", "b");

        this.main = main;

        setCondition(condition);
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(mm.deserialize( "<bold><aqua>Basics <reset><yellow>v%s".formatted(main.version()) ));
        });

    }
}
