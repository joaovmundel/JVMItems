package com.jvmitems.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;

final class HelpComponents implements HelpMenu.HelpRenderer {

    private final Player player;

    HelpComponents(Player player) {
        this.player = player;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void line(List<HelpMenu.Segment> segments) {
        TextComponent root = new TextComponent("");
        for (HelpMenu.Segment segment : segments) {
            TextComponent part = new TextComponent(
                    TextComponent.fromLegacyText(HelpMenu.color(segment.text)));

            if (segment.hover != null) {
                part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(HelpMenu.color(segment.hover))));
            }
            if (segment.command != null) {
                part.setClickEvent(new ClickEvent(
                        segment.run ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND,
                        segment.command));
            }
            root.addExtra(part);
        }
        player.spigot().sendMessage(root);
    }
}
