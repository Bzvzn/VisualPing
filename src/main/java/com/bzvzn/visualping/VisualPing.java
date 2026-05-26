package com.bzvzn.visualping;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class VisualPing extends JavaPlugin {
    private static VisualPing instance;

    private final java.util.HashMap<UUID, ParticleTask> activePings = new HashMap<>();

    public static NamespacedKey COLOR_KEY;

    // Cache for config
    public static String pointerItem;
    public static int maxDistance;
    public static int durationSeconds;
    public static int cooldownSeconds;
    public static String defaultColor;
    public static double textHeightOffset;
    public static float textScale;


    public java.util.HashMap<UUID, ParticleTask> getActivePings() {
        return activePings;
    }


    @Override
    public void onEnable() {
        instance = this;
        COLOR_KEY = new NamespacedKey(this, "ping_color");

        saveDefaultConfig();
        loadSettings();

        getServer().getPluginManager().registerEvents(new PingListener(), this);
        getCommand("pingcolor").setExecutor(new PingColorCommand());
        getCommand("pingreload").setExecutor(new PingReloadCommand());

        getLogger().info("VisualPing has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        for (ParticleTask task : activePings.values()) {
            task.cancel();
        }
        activePings.clear();
        getLogger().info("VisualPing has been disabled.");
    }

    public void loadSettings() {
        reloadConfig();
        pointerItem = getConfig().getString("ping.pointer-item", "STICK");
        maxDistance = getConfig().getInt("ping.max-distance", 50);
        durationSeconds = getConfig().getInt("ping.duration-seconds", 5);
        cooldownSeconds = getConfig().getInt("ping.cooldown-seconds", 3);
        defaultColor = getConfig().getString("ping.default-color", "#FFAA00");
        textHeightOffset = getConfig().getDouble("ping.text-height-offset", 1.0);
        textScale = (float) getConfig().getDouble("ping.text-scale", 2.0);
    }

    public static VisualPing getInstance() {
        return instance;
    }
}