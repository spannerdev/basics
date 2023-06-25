package com.spanner.basics.util;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ServerSender;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;

import java.util.ArrayList;

public class ConditionBuilder {

    private ArrayList<Permission> permissions = new ArrayList<>();
    private ArrayList<CommandCondition> conditions = new ArrayList<>();
    private Class<? extends CommandSender> commandSenderType = null;

    public static ConditionBuilder condition() {
        return new ConditionBuilder();
    }

    public ConditionBuilder permission(String permissionName) {
        return permission(new Permission(permissionName));
    }
    public ConditionBuilder permission(Permission permission) {
        permissions.add(permission);
        return this;
    }

    public ConditionBuilder player() {
        commandSenderType = Player.class;
        return this;
    }

    public ConditionBuilder server() {
        commandSenderType = ServerSender.class;
        return this;
    }

    public ConditionBuilder condition(CommandCondition condition) {
        conditions.add(condition);
        return this;
    }

    public CommandCondition get() {
        return ((sender, commandString) -> {
            if (commandSenderType != null) {
                if (!commandSenderType.isInstance(sender)) return false;
            }

            for (CommandCondition condition : conditions) {
                if (!condition.canUse(sender, commandString)) return false;
            }

            for (Permission permission : permissions) {
                if (!sender.hasPermission(permission)) return false;
            }
            return true;
        });
    }

}
