package com.spanner.basics.permission;

import com.spanner.basics.Basics;
import com.spanner.basics.module.BasicsModule;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minidev.json.JSONArray;

import java.util.*;

import static com.spanner.basics.config.Config.$;

public class PermissionModule implements BasicsModule {
    // TODO: Way to set permission to false

    Basics main;

    public PermissionModule() {
        this.main = Basics.getInstance();

        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, this::onPlayerLogin);
    }

    private void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        PermissionConfig.setPermissions(p);
    }

    public BasicsModule initialize() {
        return this;
    }

    @Override
    public void terminate() {

    }

}
