package me.dsh105.enchantmore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EnchantMorePlayerMoveListener implements Listener {

	EnchantMore plugin;

    public EnchantMorePlayerMoveListener(EnchantMore plugin) {
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // TODO: WorldGuard

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null) { 
            return;
        }

        // TODO: Boots + Efficiency  = no slow down walking on soul sand, ice 
        // idea from http://dev.bukkit.org/server-mods/elemental-armor/
        // how to speed up? or potion speed effect?
        // http://forums.bukkit.org/threads/req-useful-gold-armor-read-first.59430/
        // GoldenSprint? faster while sneaking? "feels too laggy" - listens to player move
        // GoldenEnchant? "golden pants = super speed & flying while holding shift" for 1.8 beta
        //  also on player move, but if sprinting multiples velocity vector
        //  odd diamond block enchant deal
        ItemStack boots = player.getInventory().getBoots();

        if (boots != null && boots.getType() != Material.AIR) {
            // Boots + Power = witch's broom (sprint flying)
            if (EnchantMoreListener.hasEnch(boots, EnchantMoreListener.POWER, player)) {
                if (player.isSprinting()) {
                    double factor = 
                        EnchantMoreListener.getConfigDouble("velocityMultiplerPerLevel", 1.0, boots, EnchantMoreListener.POWER) 
                        * EnchantMoreListener.getLevel(boots, EnchantMoreListener.POWER);

                    Vector velocity = event.getTo().getDirection().normalize().multiply(factor);

                    // may get kicked for flying TODO: enable flying for user
                    player.setVelocity(velocity);

                    // TODO: mitigate? only launch once, so can't really fly, just a boost?
                    // TODO: setSprinting(false)
                    // cool down period? 

                    // TODO: damage the boots? use up or infinite??
                }
            }

            // Boots + Flame = firewalker (set ground on fire)
            if (EnchantMoreListener.hasEnch(boots, EnchantMoreListener.FLAME, player)) {
                Location to = event.getTo();
                Location from = event.getFrom();
                World world = from.getWorld();

                // get from where coming from
                int dx = from.getBlockX() - to.getBlockX();
                int dz = from.getBlockZ() - to.getBlockZ();

                // a few blocks behind, further if higher level
                dx *= EnchantMoreListener.getLevel(boots, EnchantMoreListener.FLAME) + 1;
                dz *= EnchantMoreListener.getLevel(boots, EnchantMoreListener.FLAME) + 1;

                // if moved from block (try not to set player on fire)
                if (dx != 0 || dz != 0) {
                    Block block = world.getBlockAt(from.getBlockX() + dx, to.getBlockY(), from.getBlockZ() + dz);
                    if (block.getType() == Material.AIR) {
                        block.setType(Material.FIRE);
                    }
                }
                // http://dev.bukkit.org/server-mods/firelord/ "The boots set the ground on fire!"
            }

            // TODO: Boots + Aqua Affinity = walk on water
            /*
            if (EnchantMoreListener.hasEnch(boots, EnchantMoreListener.AQUA_AFFINITY, player)) {
                World world = event.getTo().getWorld();
                Block block = event.getTo().getBlock();

                if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
                    // why does this reset pitch/yaw?
                    //Location meniscus = new Location(world, event.getTo().getX(), block.getLocation().getY(), event.getTo().getZ());
                    //Location meniscus = new Location(world, event.getTo().getX(), event.getTo().getY(), event.getTo().getZ());
                    //event.setTo(meniscus);
                    // really annoying, keeps bouncing, can't move fast
                    event.setTo(event.getTo().clone().add(0, 0.1, 0));
                }
                // see also: God Powers jesus raft
                // https://github.com/FriedTaco/godPowers/blob/master/godPowers/src/com/FriedTaco/taco/godPowers/Jesus.java
                // creates a block underneath you, quite complex
            }*/

            // TODO: Boots + Knockback = bounce on fall
            /*
            if (EnchantMoreListener.hasEnch(boots, EnchantMoreListener.KNOCKBACK, player)) {
                if (event.getTo().getY() < event.getFrom().getY()) {
                    Block block = event.getTo().getBlock();
                    Block land = block.getRelative(BlockFace.DOWN);

                    plugin.log.info("land="+land);
                    if (land.getType() != Material.AIR) {
                        int n = EnchantMoreListener.getLevel(boots, EnchantMoreListener.KNOCKBACK, player);
                        player.setVelocity(event.getPlayer().getVelocity().multiply(-n));
                    }
                }
            }
            */
        }
    }
}
