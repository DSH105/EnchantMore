package io.github.dsh105.enchantmore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ConcurrentHashMap;

public class EnchantMoreTapShiftTask extends BukkitRunnable {
    static ConcurrentHashMap<String, Integer> playerSneakCount = new ConcurrentHashMap<String, Integer>();
    static ConcurrentHashMap<String, BukkitTask> playerTimeoutTasks = new ConcurrentHashMap<String, BukkitTask>();
    EnchantMoreListener listener;
    Player player;

    public EnchantMoreTapShiftTask(EnchantMoreListener listener, Player player) {
        this.listener = listener;
        this.player = player;
    }

    @Override
    public void run() {
        playerSneakCount.put(player.getName(), 0);
    }

    public static void scheduleTimeout(Player player, EnchantMoreListener listener) { // Schedule ourselves to run after player has waited too long between shift taps
        int timeoutTicks = 20 / 2; // Window of time must hit shift twice for hover jump to be activated (500 ms)
        BukkitTask timeoutTask = Bukkit.getScheduler().runTaskLater(listener.plugin, new EnchantMoreTapShiftTask(listener, player), timeoutTicks);
        playerTimeoutTasks.put(player.getName(), timeoutTask);
    }

    public static int bumpSneakCount(Player player) { // Called each time a player uses shift
        int count = getSneakCount(player);
        count += 1;
        playerSneakCount.put(player.getName(), count);
        if ((playerTimeoutTasks != null) && (playerTimeoutTasks.containsKey(player.getName()))) {
            BukkitTask timeoutTask = playerTimeoutTasks.get(player.getName());
            Bukkit.getScheduler().cancelTask(timeoutTask.getTaskId());
        }
        return count;
    }

    private static int getSneakCount(Player player) {
        if (playerSneakCount.containsKey(player.getName())) {
            return playerSneakCount.get(player.getName());
        } else return 0;
    }

    public static boolean isDoubleTapShift(Player player) {
        return getSneakCount(player) >= 2;
    }

    public static boolean isTripleTapShift(Player player) {
        return getSneakCount(player) >= 3;
    }
}
