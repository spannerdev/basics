package com.spanner.basics.permission;

import com.spanner.basics.Basics;
import com.spanner.basics.module.BasicsModule;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.permission.Permission;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.*;

import static com.spanner.basics.config.Config.$;

public class PermissionModule implements BasicsModule {
    // TODO: Way to set permission to false

    Basics main;
    HashMap<String, PermissionGroup> permissionGroups = new HashMap<>();

    public PermissionModule() {
        this.main = Basics.getInstance();

        loadPermissionGroups();

        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, this::onPlayerLogin);
    }

    private void loadPermissionGroups() {
        // TODO: Lots of validation

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
    }

    private void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        JSONArray g = $("modules.permission.config.players.[?(@.uuid =~ /^"+p.getUuid()+"$/i)].group");
        if (g.size() != 1) return;
        String groupName = (String) g.get(0);
        if (groupName == null) return;
        PermissionGroup group = permissionGroups.get(groupName);
        if (group == null) return;
        group.givePermissions(p);
    }

    public BasicsModule initialize() {
        return this;
    }

    @Override
    public void terminate() {

    }

}
