package com.spanner.basics.config;

import net.minidev.json.JSONArray;

import java.util.List;
import java.util.function.Function;

public class ConfigQueryResult {

    List<?> result;

    protected ConfigQueryResult(JSONArray result) {
        this.result = result;
    }

    public <T> T[] get() {
        return (T[]) result.toArray();
    }

    public <T> T get(int i) {
        if (result.size() <= i) return null;
        return (T) result.get(i);
    }

    public <T> T first() {
        return get(0);
    }

    public Object last() {
        return get(result.size()-1);
    }

    public ConfigQueryResult cast(Class<?> clazz) {
        return map(clazz::cast);
    }

    public ConfigQueryResult map(Function<? super Object,?> function) {
        this.result = result.stream().map(function).toList();
        return this;
    }

}
