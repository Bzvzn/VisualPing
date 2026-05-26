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

        this.maxTicks = VisualPing.durationSeconds * 20;

        String hexColor = VisualPing.defaultColor;
        
        if (player.getPersistentDataContainer().has(VisualPing.COLOR_KEY, PersistentDataType.STRING)) {
            hexColor = player.getPersistentDataContainer().get(VisualPing.COLOR_KEY, PersistentDataType.STRING);
        }

        // Convert the hex string (e.g., "#FF0000") into actual Color objects
        Color bukkitColor = Color.fromRGB(
                Integer.valueOf(hexColor.substring(1, 3), 16),
                Integer.valueOf(hexColor.substring(3, 5), 16),
                Integer.valueOf(hexColor.substring(5, 7), 16)
        );
        TextColor kyoriColor = TextColor.fromHexString(hexColor);
        

        this.dustOptions = new Particle.DustOptions(bukkitColor, 1.5f);

        Location textLocation = this.target.clone().add(0, VisualPing.textHeightOffset, 0);
        

        this.textDisplay = world.spawn(textLocation, TextDisplay.class, display -> {
            display.text(Component.text(player.getName()).color(kyoriColor));
            display.setBillboard(Display.Billboard.CENTER);
            display.setShadowed(true);
            display.setBackgroundColor(Color.fromARGB(50, 0, 0, 0));
            display.setViewRange(15.0f);

            display.setTransformation(new org.bukkit.util.Transformation(
                new org.joml.Vector3f(),
                new org.joml.AxisAngle4f(),
                new org.joml.Vector3f(VisualPing.textScale, VisualPing.textScale, VisualPing.textScale),
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

        world.spawnParticle(Particle.DUST, target, 15, 0.3, 0.3, 0.3, 0, dustOptions, true);

        ticksRun += 2;
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