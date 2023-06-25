package com.spanner.basics.command;

import com.spanner.basics.Basics;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.entity.EntityFinder;

import java.util.List;

import static com.spanner.basics.util.ConditionBuilder.condition;
import static com.spanner.basics.util.TextUtil.entityName;
import static com.spanner.basics.util.TextUtil.itemName;

public class GiveCommand extends Command {

    CommandCondition giveCondition = condition().player().permission("basics.give").get();
    CommandCondition giveOtherCondition = condition().player().permission("basics.give.others").get();

    Basics main = Basics.getInstance();
    MiniMessage mm = main.mm();
    public GiveCommand() {
        super("give", "i");

        setCondition(giveCondition);
        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(mm.deserialize("<red>Usage: /give <item> [quantity] [targets] [transaction type]"));
        });

        var itemArg = ArgumentType.ItemStack("item");
        var qtyArg = ArgumentType.Integer("quantity")
                .min(1)
                .setDefaultValue(1);
        var targetsArg = ArgumentType.Entity("targets")
                .onlyPlayers(true)
                .setDefaultValue(new EntityFinder().setTargetSelector(EntityFinder.TargetSelector.SELF));
        var transactionOptionArg = ArgumentType.Enum("transaction type", TransactionOptionType.class)
                .setFormat(ArgumentEnum.Format.LOWER_CASED)
                .setDefaultValue(TransactionOptionType.ALL);

        addConditionalSyntax(
                giveCondition,
                this::give,
                itemArg, qtyArg, transactionOptionArg
        );

        addConditionalSyntax(
                giveOtherCondition,
                this::give,
                itemArg, qtyArg, targetsArg, transactionOptionArg
        );
    }

    private void give(CommandSender sender, CommandContext context) {
        Player player = (Player)sender;

        ItemStack itemStack = context.get("item");
        Integer quantity = context.get("quantity");
        itemStack = itemStack.withAmount(quantity);

        EntityFinder entityFinder = context.get("targets");
        List<Entity> targets;
        if (entityFinder != null) {
            targets = entityFinder.find(sender);
        } else {
            targets = List.of(player);
        }

        TransactionOptionType transactionOptionType = context.get("transaction type");
        TransactionOption<?> transactionOption = transactionOptionType.getTransactionOption();
        for (Entity target : targets) {
            if (target instanceof Player p) {
                var result = p.getInventory().addItemStack(itemStack, transactionOption);
                if (result.getClass() == Boolean.class) {
                    Boolean boolResult = (Boolean)result;
                    if (!boolResult) {
                        sender.sendMessage(mm.deserialize(
                                "Failed to give to <target>",
                                Placeholder.component("target", entityName(p))
                        ));
                        continue;
                    }
                }
                sender.sendMessage(mm.deserialize(
                        "Gave <target> <item> x<quantity>",
                        Placeholder.component("target", entityName(p)),
                        Placeholder.component("item", itemName(itemStack)),
                        Placeholder.unparsed("quantity", quantity.toString())
                ));
            }
        }
    }

    private enum TransactionOptionType {
         ALL(TransactionOption.ALL)
        ,ALL_OR_NOTHING(TransactionOption.ALL_OR_NOTHING)
        ,DRY_RUN(TransactionOption.DRY_RUN)
        ;

        final TransactionOption<?> transactionOption;
        private TransactionOptionType(TransactionOption<?> transactionOption) {
            this.transactionOption = transactionOption;
        }
        TransactionOption<?> getTransactionOption() {
            return this.transactionOption;
        }
    }

}
