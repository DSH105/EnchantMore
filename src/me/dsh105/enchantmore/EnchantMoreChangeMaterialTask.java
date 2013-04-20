package me.dsh105.enchantmore;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnchantMoreChangeMaterialTask extends BukkitRunnable {
	Block block;
	Player player;
	EnchantMoreListener listener;
	Material material;
	int data;
	public EnchantMoreChangeMaterialTask(Block block, Player player, Material material, EnchantMoreListener listener) {
		this(block, player, material, -1, listener);
	}
	public EnchantMoreChangeMaterialTask(Block block, Player player, Material material, int data, EnchantMoreListener listener) {
		this.block = block;
		this.player = player;
		this.material = material;
		this.data = data;
		this.listener = listener;
	}
	@Override
	public void run() {
		if (listener.plugin.safeSetBlock(player, block, material)) {
			if (data != -1) {
				block.setData((byte) data);
			}
		}
	}
}
