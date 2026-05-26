package com.bzvzn.visualping;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PingColorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        // 1. Ensure the sender is actually a player in the game (not the server console)
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // 2. Check if they provided exactly one argument
        if (args.length != 1) {
            player.sendMessage(Component.text("Usage: /pingcolor <#HEXCODE> or /pingcolor reset", NamedTextColor.RED));
            return true;
        }

        String input = args[0];

        if (input.equalsIgnoreCase("reset") || input.equalsIgnoreCase("clear")) {
            player.getPersistentDataContainer().remove(VisualPing.COLOR_KEY);
            player.sendMessage(Component.text("📍 Your ping color was reset!", NamedTextColor.GREEN));
            return true;
        }
        
        // Quality of Life: Add the '#' if the player forgot to type it
        if (!input.startsWith("#")) {
            input = "#" + input;
        }

        // 3. Validation: Regular Expression (Regex)
        // This ensures the string is exactly a '#' followed by 6 letters (A-F) or numbers (0-9)
        if (!input.matches("^#[0-9a-fA-F]{6}$")) {
            player.sendMessage(Component.text("Invalid color! Please use a valid Hex Code (e.g., #FF5555) or 'reset'.", NamedTextColor.RED));
            return true;
        }

        // 4. Save the valid color to the PersistentDataContainer (PDC)
        player.getPersistentDataContainer().set(VisualPing.COLOR_KEY, PersistentDataType.STRING, input.toUpperCase());

        // 5. Send a success message, colored in their newly chosen color!
        TextColor customColor = TextColor.fromHexString(input);
        if (customColor != null) {
            player.sendMessage(Component.text("📍 Your ping color has been updated!").color(customColor));
        }

        return true;
    }
}