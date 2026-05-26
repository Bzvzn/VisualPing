package com.bzvzn.visualping;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PingReloadCommand implements CommandExecutor{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        
        // 1. Check permissions (only admins should be able to reload the config)
        if (!sender.hasPermission("visualping.reload")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        // 2. Reload the config from disk and update the cached variables
        VisualPing.getInstance().loadSettings();

        // 3. Send success message
        sender.sendMessage("§a[VisualPing] Configuration successfully reloaded!");
        
        return true;
    }
}
