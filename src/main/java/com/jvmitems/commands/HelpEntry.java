package com.jvmitems.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class HelpEntry {

    private final String category;
    private final String command;
    private final String args;
    private final String description;
    private final String permission;
    private final List<String> hints;

    HelpEntry(String category, String command, String args, String description, String permission, String... hints) {
        this.category = category;
        this.command = command;
        this.args = args == null ? "" : args;
        this.description = description;
        this.permission = permission;
        this.hints = hints == null || hints.length == 0
                ? Collections.<String>emptyList()
                : Collections.unmodifiableList(Arrays.asList(hints));
    }

    String getCategory() {
        return category;
    }

    String getCommand() {
        return command;
    }

    String getArgs() {
        return args;
    }

    String getDescription() {
        return description;
    }

    String getPermission() {
        return permission;
    }

    List<String> getHints() {
        return hints;
    }

    boolean matches(String query) {
        String lower = query.toLowerCase(java.util.Locale.ROOT);
        return category.toLowerCase(java.util.Locale.ROOT).contains(lower)
                || command.toLowerCase(java.util.Locale.ROOT).contains(lower)
                || args.toLowerCase(java.util.Locale.ROOT).contains(lower)
                || description.toLowerCase(java.util.Locale.ROOT).contains(lower);
    }
}
