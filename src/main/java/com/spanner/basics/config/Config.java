package com.spanner.basics.config;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.spanner.basics.Basics;
import com.spanner.basics.module.BasicsModule;
import com.spanner.basics.permission.PermissionModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Config {

    private final static int MIN_COMPATIBLE_VERSION = 1;
    private final static int CURRENT_VERSION = 1;

    private static Config INSTANCE = null;

    private Object configuration;
    private Config(Object configuration) {
        this.configuration = configuration;

        INSTANCE = this;
    }

    public BasicsModule[] loadModules() {
        ArrayList<BasicsModule> loadedModules = new ArrayList<>();

        if ($("modules.permission.enabled")) loadedModules.add( new PermissionModule().initialize() );

        return loadedModules.toArray(BasicsModule[]::new);
    }

    public static <T> T $(String path) { // Truly evil method name, but equally beautiful.
        return getInstance().get(path);
    }

    public <T> T get(String path) {
        try {
            return JsonPath.read(this.configuration, "$." + path);
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    public static Config getInstance() {
        if (INSTANCE != null) return INSTANCE;
        return load();
    }

    public static Config load() {
        Config config;
        try {
            InputStream stream = Basics.getInstance().getResource("config.json");
            if (stream == null) return null; // TODO: Throw error, should not continue loading
            Object configuration = Configuration.defaultConfiguration().jsonProvider().parse(stream, "UTF-8");
            stream.close();
            config = new Config(configuration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (config.<Integer>get("version") < MIN_COMPATIBLE_VERSION) {
            Basics.getInstance().getLogger().warn("Config version is outdated, please update it!");
        }

        return config;
    }


}
