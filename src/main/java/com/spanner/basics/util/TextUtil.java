package com.spanner.basics.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TextUtil {


    public static List<Component> createTable(List<String> headers, List<List<String>> rows) {
        String[] h = headers.toArray(String[]::new);
        String[][] r = rows.stream().map(row -> row.toArray(String[]::new)).toArray(String[][]::new);
        return createTable(h,r);
    }
    public static List<Component> createTable(String[] headers, String[][] rows) {
        if (headers.length < 1) throw new IllegalArgumentException("Width of table must be greater than 0");
        // TODO: Check that all rows have same length, are same length as headers

        List<List<String>> columns = new ArrayList<>();
        List<Integer> columnWidths = new ArrayList<>();
        for (int i=0;i<headers.length;i++) {
            List<String> column = new ArrayList<>();
            for (int j=0;j<rows.length;j++) {
                column.add(rows[j][i]);
            }
            columnWidths.add(column.stream().map(String::length).max(Integer::compare).get());
        }

        List<List<String>> values = new ArrayList<>();
        values.add(List.of(headers));
        for (String[] row : rows) {
            values.add(List.of(row));
        }

        List<Component> components = new ArrayList<>();
        int columnNum;
        int rowNum = 0;
        for (List<String> row : values) {
            columnNum=0;
            StringBuilder rowText = new StringBuilder();
            for (String value : row) {
                int columnWidth = (columnWidths.get(columnNum) + 2);
                String padding = " ".repeat(columnWidth - value.length());
                rowText.append(value).append(padding);
                columnNum++;
            }
            Component component = Component.text(rowText.toString()).font(Key.key("minecraft:uniform"));
            if (rowNum == 0) component = component.decorate(TextDecoration.UNDERLINED);
            components.add( component );
            rowNum++;
        }
        return components;

    }

    public static Component entityName(Entity e) {
        Component customName = e.getCustomName();
        if (customName != null) return customName;
        if (e instanceof Player p) {
            Component displayName = p.getDisplayName();
            if (displayName != null) return displayName;
            return Component.text(p.getUsername());
        }

        return Component.text(e.getEntityType().name());
    }

    public static Component itemName(ItemStack i) {
        Component displayName = i.getDisplayName();
        if (displayName != null) return displayName;
        return Component.text(i.material().name());
    }


}
