package com.jvmitems.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<String, Long> lastUse = new HashMap<>();

    private String key(UUID playerId, String itemId) {
        return playerId.toString() + ":" + itemId;
    }

    public boolean isOnCooldown(UUID playerId, String itemId, int cooldownSeconds) {
        Long last = lastUse.get(key(playerId, itemId));
        if (last == null) {
            return false;
        }
        return (System.currentTimeMillis() - last) < (cooldownSeconds * 1000L);
    }

    public long remainingSeconds(UUID playerId, String itemId, int cooldownSeconds) {
        Long last = lastUse.get(key(playerId, itemId));
        if (last == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - last;
        long remainingMillis = (cooldownSeconds * 1000L) - elapsed;
        return Math.max(0, (remainingMillis + 999) / 1000);
    }

    public void setUsed(UUID playerId, String itemId) {
        lastUse.put(key(playerId, itemId), System.currentTimeMillis());
    }
}
