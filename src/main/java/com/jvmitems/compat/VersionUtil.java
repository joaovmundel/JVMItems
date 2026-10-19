package com.jvmitems.compat;

import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;

public final class VersionUtil {

    private static final String NMS_VERSION = detectNmsVersion();
    private static Boolean pdcSupport;

    private VersionUtil() {
    }

    private static String detectNmsVersion() {
        try {
            String pkg = Bukkit.getServer().getClass().getPackage().getName();
            String[] parts = pkg.split("\\.");
            if (parts.length >= 4) {
                return parts[3];
            }
        } catch (Exception ignored) {
            // segue com string vazia abaixo
        }
        return "";
    }

    public static String nmsVersion() {
        return NMS_VERSION;
    }

    public static boolean isLegacyAttributeApi() {
        return "v1_12_R1".equals(NMS_VERSION);
    }

    public static boolean hasPersistentDataContainer() {
        if (pdcSupport == null) {
            try {
                ItemMeta.class.getMethod("getPersistentDataContainer");
                pdcSupport = Boolean.TRUE;
            } catch (NoSuchMethodException e) {
                pdcSupport = Boolean.FALSE;
            }
        }
        return pdcSupport;
    }
}
