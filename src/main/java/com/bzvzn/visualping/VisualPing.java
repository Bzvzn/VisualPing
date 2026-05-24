package com.bzvzn.visualping;

import org.bukkit.plugin.java.JavaPlugin;

public class VisualPing extends JavaPlugin {
    private static VisualPing instance;

    private final java.util.HashMap<java.util.UUID, ParticleTask> activePings = new java.util.HashMap<>();

    public java.util.HashMap<java.util.UUID, ParticleTask> getActivePings() {
        return activePings;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PingListener(), this);
        getCommand("pingcolor").setExecutor(new PingColorCommand());

        getLogger().info("VisualPing has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("VisualPing has been disabled.");
    }

    public static VisualPing getInstance() {
        return instance;
    }
}