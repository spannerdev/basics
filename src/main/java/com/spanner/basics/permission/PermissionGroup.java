package com.spanner.basics.permission;

import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PermissionGroup {

    private String name;
    private Set<Permission> permissions;
    private Set<PermissionGroup> inherits;
    private int permissionLevel;

    public PermissionGroup(String name, Set<Permission> permissions, int permissionLevel, Set<PermissionGroup> inherits) {
        this.name = name;
        this.permissions = permissions;
        this.permissionLevel = permissionLevel;
        this.inherits = inherits;
    }
    public PermissionGroup(String name, Set<Permission> permissions, int permissionLevel) {
        this(name, permissions, permissionLevel, new HashSet<>());
    }
    public PermissionGroup(String name, int permissionLevel) {
        this(name, new HashSet<>(), permissionLevel, new HashSet<>());
    }

    public void givePermissions(Player player) {
        // TODO: This only sets the permissions when they log in. If permissions change, it will not update.

        player.setPermissionLevel(this.permissionLevel);

        permissions.forEach(player::addPermission);
        inherits.stream().map(PermissionGroup::getPermissions).forEach(ps -> ps.forEach(player::addPermission));
    }

    public Set<Permission> getPermissions() {
        return Objects.requireNonNull(Set.copyOf(permissions));
    }

    public void addInherits(PermissionGroup group) {
        inherits.add(group);
    }

    public String getName() {
        return name;
    }
}
