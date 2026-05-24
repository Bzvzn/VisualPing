package com.bzvzn.visualping;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class PingListener implements Listener {

    // HashMap to store the last time a player used the ping (for cooldowns)
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // 1. Check if it's a RIGHT CLICK
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return; // If it's a left click or something else, stop here.
        }

        // 2. Read the required item from the config (defaults to STICK if not found)
        String configuredItemName = VisualPing.getInstance().getConfig().getString("ping.pointer-item", "STICK");
        Material requiredItem = Material.matchMaterial(configuredItemName);
        
        if (requiredItem == null || player.getInventory().getItemInMainHand().getType() != requiredItem) {
            return; // Not holding the right item.
        }

        // 3. Check if the player is sneaking (holding Shift)
        if (!player.isSneaking()) {
            return; 
        }

        // 4. Cooldown System (Anti-Spam)
        int cooldownSeconds = VisualPing.getInstance().getConfig().getInt("ping.cooldown-seconds", 3);
        if (!player.hasPermission("visualping.bypass")) {
    
            if (cooldowns.containsKey(player.getUniqueId())) {
                long timeSinceLastPing = System.currentTimeMillis() - cooldowns.get(player.getUniqueId());
                if (timeSinceLastPing < (cooldownSeconds * 1000L)) {
                    return;
                }
            }
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }

        // 5. Raytracing: Find the block the player is looking at
        int maxDistance = VisualPing.getInstance().getConfig().getInt("ping.max-distance", 50);
        org.bukkit.util.RayTraceResult rayTrace = player.rayTraceBlocks(maxDistance);

        if (rayTrace != null && rayTrace.getHitBlock() != null) {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            player.sendActionBar(net.kyori.adventure.text.Component.text("§a📍 Target pinged!"));

            org.bukkit.Location exactHit = rayTrace.getHitPosition().toLocation(player.getWorld());


            org.bukkit.block.BlockFace face = rayTrace.getHitBlockFace();
            if (face != null) {
                exactHit.add(face.getDirection().multiply(0.4)); 
            }

            java.util.HashMap<UUID, ParticleTask> activePings = VisualPing.getInstance().getActivePings();
            if (activePings.containsKey(player.getUniqueId())) {
                activePings.get(player.getUniqueId()).cancel();
            }

            ParticleTask newTask = new ParticleTask(player, exactHit);
            activePings.put(player.getUniqueId(), newTask);
            newTask.runTaskTimer(VisualPing.getInstance(), 0L, 5L);
        }
    }
}