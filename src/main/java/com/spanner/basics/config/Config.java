package com.spanner.basics.config;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.spanner.basics.Basics;
import com.spanner.basics.module.BasicsModule;
import com.spanner.basics.permission.PermissionModule;
import net.minestom.server.permission.Permission;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

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

    public ConfigQueryResult query(String path) {
        var result = getArray(path);
        if (result == null) return null;
        return new ConfigQueryResult(result);
    }

    public Object getFirst(String path) {
        var result = get(path);
        if (!(result instanceof JSONArray jarray) || jarray.size() < 1) return null;
        return jarray.get(0);
    }

    public ConfigObject[] getObjectArray(String path) {
        JSONArray jarray = getArray(path);
        if (jarray == null) return null;

        return jarray.stream()
                .map(LinkedHashMap.class::cast)
                .map(ConfigObject::fromLinkedHashMap)
                .toArray(ConfigObject[]::new);
    }

    public String[] getStringArray(JSONArray jarray) {
        return jarray.stream().map(Object::toString).toArray(String[]::new); // TODO: Check that all values are actually strings
    }
    public String[] getStringArray(String path) {
        JSONArray jarray = getArray(path);
        if (jarray == null) return null;

        return getStringArray(jarray);
    }

    private JSONArray getArray(String path) {
        var value = get(path);
        if (!(value instanceof JSONArray jarray)) return null;
        return jarray;
    }

    public Permission getPermission(String path) {
        var value = get(path);
        if (!(value instanceof String permissionName) || permissionName.isBlank()) return null;

        return new Permission(permissionName); // TODO: Should these objects be shared/cached? Feels wrong to instantiate a new one every time
    }

    public Set<Permission> getPermissionSet(String path) {
        String[] sarray = getStringArray(path);
        if (sarray == null) return null;
        return Arrays.stream(sarray).map(Permission::new).collect(Collectors.toSet());
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
