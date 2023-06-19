package com.spanner.basics;

import com.spanner.basics.command.BasicsCommand;
import com.spanner.basics.config.Config;
import com.spanner.basics.module.BasicsModule;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Basics extends Extension {

    private final String VERSION = getOrigin().getVersion();

    private BasicsModule[] loadedModules;

    MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void initialize() {
        INSTANCE = this;

        MinecraftServer.getCommandManager().register(new BasicsCommand(this));
        Config.load();

        loadedModules = Config.getInstance().loadModules();

        getLogger().info( mm.deserialize( "Initialized Basics <yellow>v%s".formatted(VERSION) ) );
    }

    @Override
    public void terminate() {
        for (BasicsModule module : loadedModules) module.terminate();

        INSTANCE = null;

        getLogger().info( mm.deserialize( "Unloaded Basics <yellow>v%s".formatted(VERSION) ) );
    }

    static Basics INSTANCE = null;
    public static @NotNull Basics getInstance() {
        // TODO: Probably should check that it's not null, and warn/handle if it is
        return INSTANCE;
    }

    public String version() {
        return VERSION;
    }
}
