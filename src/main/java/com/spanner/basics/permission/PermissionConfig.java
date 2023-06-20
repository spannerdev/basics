package com.spanner.basics.permission;

import com.spanner.basics.config.Config;
import com.spanner.basics.config.ConfigObject;
import com.spanner.basics.config.ConfigQueryResult;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.*;

import static com.spanner.basics.config.Config.$;

public class PermissionConfig {

    private static final Map<String, PermissionGroup> permissionGroups = loadPermissionGroups();

    public static Map<String, PermissionGroup> loadPermissionGroups() {
        // TODO: Lots of validation
        Map<String, PermissionGroup> permissionGroups = new HashMap<>();

        Config config = Config.getInstance();

        ConfigObject[] configPermGroups = config.getObjectArray("modules.permission.config.groups");
        int i = 0;
        for (ConfigObject groupObj : configPermGroups) {
            String name = groupObj.getAsString("name");

            Set<Permission> permissionSet = config.getPermissionSet("modules.permission.config.groups["+i+"].permissions");
            int permissionLevel = groupObj.getAsNumber("permission_level").intValue();

            permissionGroups.put(name, new PermissionGroup(name, permissionSet, permissionLevel));
            i++;
        }
        permissionGroups.values().forEach(group -> {
            String[] inherits = config.getStringArray(config.query("modules.permission.config.groups.[?(@.name == '"+group.getName()+"')].inherit").<JSONArray>first());
            for (String groupName : inherits) {
                group.addInherits(permissionGroups.get(groupName));
            }
        });
        return permissionGroups;
    }

    public static void setPermissions(Player p) {
        Object result = Config.getInstance().query("modules.permission.config.players.[?(@.uuid =~ /^"+p.getUuid()+"$/i)].group").first();
        if (result == null) return;
        String groupName = (String) result;
        PermissionGroup group = permissionGroups.get(groupName);

        if (group == null) return;
        group.givePermissions(p);
    }

}
