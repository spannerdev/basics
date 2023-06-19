package com.spanner.basics;

import com.spanner.basics.command.BasicsCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

public class Basics extends Extension {

    private final String VERSION = getOrigin().getVersion();

    MiniMessage mm = MiniMessage.miniMessage();
    @Override
    public void initialize() {
        MinecraftServer.getCommandManager().register(new BasicsCommand(this));

        getLogger().info( mm.deserialize( "Initialized Basics <yellow>v%s".formatted(VERSION) ) );
    }

    @Override
    public void terminate() {
        getLogger().info( mm.deserialize( "Unloaded Basics <yellow>v%s".formatted(VERSION) ) );
    }

    public String version() {
        return VERSION;
    }
}
