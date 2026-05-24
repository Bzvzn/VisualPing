package com.bzvzn.visualping;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleTask extends BukkitRunnable {

    private final Location target;
    private final World world;
    private final java.util.UUID playerUUID;
    private TextDisplay textDisplay;
    
    private final int maxTicks;
    private int ticksRun = 0;
    
    private final Particle.DustOptions dustOptions;

    public ParticleTask(Player player, Location targetHitLocation) {
        this.playerUUID = player.getUniqueId();

        this.world = targetHitLocation.getWorld();
        
        this.target = targetHitLocation.clone();

        // 1. Calculate duration in server ticks (20 ticks = 1 second)
        int seconds = VisualPing.getInstance().getConfig().getInt("ping.duration-seconds", 5);
        this.maxTicks = seconds * 20;

        // 2. Fetch the player's custom color from the PersistentDataContainer (PDC)
        String hexColor = VisualPing.getInstance().getConfig().getString("ping.default-color", "#FFAA00");
        NamespacedKey colorKey = new NamespacedKey(VisualPing.getInstance(), "ping_color");
        
        if (player.getPersistentDataContainer().has(colorKey, PersistentDataType.STRING)) {
            hexColor = player.getPersistentDataContainer().get(colorKey, PersistentDataType.STRING);
        }

        // Convert the hex string (e.g., "#FF0000") into actual Color objects
        Color bukkitColor = Color.fromRGB(
                Integer.valueOf(hexColor.substring(1, 3), 16),
                Integer.valueOf(hexColor.substring(3, 5), 16),
                Integer.valueOf(hexColor.substring(5, 7), 16)
        );
        TextColor kyoriColor = TextColor.fromHexString(hexColor);
        
        // Define the particle size and color (1.5f is slightly larger than normal)
        this.dustOptions = new Particle.DustOptions(bukkitColor, 1.5f);

        // 3. Spawn the new TextDisplay Entity
        double heightOffset = VisualPing.getInstance().getConfig().getDouble("ping.text-height-offset", 1);
        Location textLocation = this.target.clone().add(0, heightOffset, 0);

        float textScale = (float) VisualPing.getInstance().getConfig().getDouble("ping.text-scale", 2.0);
        
        // We use a lambda to configure the entity right as it spawns
        this.textDisplay = world.spawn(textLocation, TextDisplay.class, display -> {
            display.text(Component.text(player.getName()).color(kyoriColor));
            display.setBillboard(Display.Billboard.CENTER);
            display.setShadowed(true);
            display.setBackgroundColor(Color.fromARGB(50, 0, 0, 0));
            display.setViewRange(15.0f);

            display.setTransformation(new org.bukkit.util.Transformation(
                new org.joml.Vector3f(),
                new org.joml.AxisAngle4f(),
                new org.joml.Vector3f(textScale, textScale, textScale),
                new org.joml.AxisAngle4f()
            ));
        });
    }

    @Override
    public void run() {
        if (ticksRun >= maxTicks) {
            cancel(); 
            return;
        }

        world.spawnParticle(Particle.DUST, target, 30, 0.3, 0.3, 0.3, 0, dustOptions, true);

        ticksRun += 5; 
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        if (textDisplay != null && !textDisplay.isDead()) {
            textDisplay.remove();
        }
        VisualPing.getInstance().getActivePings().remove(playerUUID);
        super.cancel();
    }
}