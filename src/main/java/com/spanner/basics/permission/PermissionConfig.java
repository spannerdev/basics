package com.spanner.basics.permission;

import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;
import net.minidev.json.JSONArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.spanner.basics.config.Config.$;

public class PermissionConfig {

    private static final Map<String, PermissionGroup> permissionGroups = loadPermissionGroups();

    public static Map<String, PermissionGroup> loadPermissionGroups() {
        // TODO: Lots of validation
        Map<String, PermissionGroup> permissionGroups = new HashMap<>();

        JSONArray configPermGroups = $("modules.permission.config.groups");
        configPermGroups.forEach(group -> {
            Map<String, Object> obj = (Map<String, Object>) group;


            String name = (String) obj.get("name");
            JSONArray permissions = (JSONArray) obj.get("permissions");
            Set<Permission> permissionSet = new HashSet<>(permissions.stream().map(Object::toString).map(Permission::new).toList());
            int permissionLevel = (int) obj.get("permission_level");

            permissionGroups.put(name, new PermissionGroup(name, permissionSet, permissionLevel));
        });
        permissionGroups.values().forEach(group -> {
            JSONArray inherits = (JSONArray) ((JSONArray) $("modules.permission.config.groups.[?(@.name == '"+group.getName()+"')].inherit")).get(0);
            inherits.forEach(inheritGroup -> group.addInherits(permissionGroups.get(inheritGroup)));
        });
        return permissionGroups;
    }

    public static void setPermissions(Player p) {
        JSONArray results = $("modules.permission.config.players.[?(@.uuid =~ /^"+p.getUuid()+"$/i)].group");
        if (results.size() != 1) return;
        String groupName = (String) results.get(0);

        if (groupName == null) return;
        PermissionGroup group = permissionGroups.get(groupName);

        if (group == null) return;
        group.givePermissions(p);
    }

}
