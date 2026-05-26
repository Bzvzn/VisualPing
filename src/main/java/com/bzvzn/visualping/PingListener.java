package com.bzvzn.visualping;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class PingListener implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // 1. Check if it's a RIGHT CLICK
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return; 
        }

        Material requiredItem = Material.matchMaterial(VisualPing.pointerItem);

        if (requiredItem == null) {
            VisualPing.getInstance().getLogger().warning("Invalid pointer-item in config: " + VisualPing.pointerItem);
            return;
        }
        
        // Holding wrong item
        if (player.getInventory().getItemInMainHand().getType() != requiredItem) {
            return; 
        }

        // 3. Check if the player is sneaking (holding Shift)
        if (!player.isSneaking()) {
            return; 
        }


        // Cooldown
        if (!player.hasPermission("visualping.bypass")) {
            if (cooldowns.containsKey(player.getUniqueId())) {
                long timeSinceLastPing = System.currentTimeMillis() - cooldowns.get(player.getUniqueId());
                long totalCooldownMillis = VisualPing.cooldownSeconds * 1000L;

                if (timeSinceLastPing < totalCooldownMillis) {
                    double remainingSeconds = (totalCooldownMillis - timeSinceLastPing) / 1000.0;
                    String formattedTime = String.format(java.util.Locale.US, "%.1f", remainingSeconds);
                    player.sendActionBar(net.kyori.adventure.text.Component.text("§c⏳ Cooldown: " + formattedTime + "s"));
                    return;
                }
            }
        }

        // 5. Raytracing: Find the block the player is looking at
        org.bukkit.util.RayTraceResult rayTrace = player.rayTraceBlocks(VisualPing.maxDistance);

        // 6. Process the hit
        if (rayTrace != null && rayTrace.getHitBlock() != null) {

            if (!player.hasPermission("visualping.bypass")) {
                cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            }

            player.sendActionBar(net.kyori.adventure.text.Component.text("§a📍 Target pinged!"));

            org.bukkit.Location exactHit = rayTrace.getHitPosition().toLocation(player.getWorld());


            org.bukkit.block.BlockFace face = rayTrace.getHitBlockFace();
            if (face != null) {
                exactHit.add(face.getDirection().multiply(0.4)); 
            }

            HashMap<UUID, ParticleTask> activePings = VisualPing.getInstance().getActivePings();
            if (activePings.containsKey(player.getUniqueId())) {
                activePings.get(player.getUniqueId()).cancel();
            }

            ParticleTask newTask = new ParticleTask(player, exactHit);
            activePings.put(player.getUniqueId(), newTask);

            newTask.runTaskTimer(VisualPing.getInstance(), 0L, 2L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        cooldowns.remove(playerUUID);

        ParticleTask task = VisualPing.getInstance().getActivePings().remove(playerUUID);
        if (task != null) task.cancel();
    }
}