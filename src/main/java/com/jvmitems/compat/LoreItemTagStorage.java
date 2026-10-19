package com.jvmitems.compat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class LoreItemTagStorage implements ItemTagStorage {

    private static final char COLOR_CHAR = '§';
    private static final String MARKER = COLOR_CHAR + "r" + COLOR_CHAR + "k";

    @Override
    public String getId(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return null;
        }
        List<String> lore = meta.getLore();
        for (String line : lore) {
            if (line.startsWith(MARKER)) {
                return decode(line.substring(MARKER.length()));
            }
        }
        return null;
    }

    @Override
    public ItemStack setId(ItemStack item, String id) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.removeIf(line -> line.startsWith(MARKER));
        lore.add(MARKER + encode(id));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String encode(String id) {
        byte[] bytes = id.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(COLOR_CHAR).append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(COLOR_CHAR).append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private String decode(String encoded) {
        try {
            String hexOnly = encoded.replace(String.valueOf(COLOR_CHAR), "");
            int length = hexOnly.length() / 2;
            byte[] bytes = new byte[length];
            for (int i = 0; i < length; i++) {
                int hi = Character.digit(hexOnly.charAt(i * 2), 16);
                int lo = Character.digit(hexOnly.charAt(i * 2 + 1), 16);
                bytes[i] = (byte) ((hi << 4) | lo);
            }
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException | RuntimeException e) {
            return null;
        }
    }
}
