package me.jb.itemcheck.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    @org.jetbrains.annotations.NotNull
    public static String setColorsMessage(String string) {
        return translateHexColorCodes(ChatColor.translateAlternateColorCodes('&', string));
    }

    @NotNull
    public static String setupMessage(Player player, String message) {
        return setColorsMessage(PlaceholderAPI.setPlaceholders(player, translateHexColorCodes(message)));
    }

    private static String translateHexColorCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

}