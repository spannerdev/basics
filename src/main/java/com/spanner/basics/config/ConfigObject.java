package com.spanner.basics.config;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigObject extends JSONObject {

    public static ConfigObject fromLinkedHashMap(LinkedHashMap<String, Object> linkedHashMap) {
        ConfigObject o = new ConfigObject();
        o.putAll(linkedHashMap);
        return o;
    }

}
