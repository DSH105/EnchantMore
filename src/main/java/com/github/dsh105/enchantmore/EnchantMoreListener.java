package com.github.dsh105.enchantmore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.v1_6_R3.Packet63WorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.MemorySection;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class EnchantMoreListener implements Listener {

	enum EnchantMoreItemCategory {
	    IS_HOE,
	    IS_SWORD,
	    IS_PICKAXE,
	    IS_SHOVEL,
	    IS_AXE,
	    IS_FARMBLOCK,
	    IS_EXCAVATABLE,
	    IS_WOODENBLOCK,

	    IS_HELMET,
	    IS_CHESTPLATE,
	    IS_LEGGINGS,
	    IS_BOOTS
	}
    final static Enchantment PROTECTION = Enchantment.PROTECTION_ENVIRONMENTAL;
    final static Enchantment FIRE_PROTECTION = Enchantment.PROTECTION_FIRE;
    final static Enchantment FEATHER_FALLING = Enchantment.PROTECTION_FALL;
    final static Enchantment BLAST_PROTECTION = Enchantment.PROTECTION_EXPLOSIONS;
    final static Enchantment PROJECTILE_PROTECTION = Enchantment.PROTECTION_PROJECTILE;
    final static Enchantment RESPIRATION = Enchantment.OXYGEN;
    final static Enchantment AQUA_AFFINITY = Enchantment.WATER_WORKER;
    final static Enchantment SHARPNESS = Enchantment.DAMAGE_ALL;
    final static Enchantment SMITE = Enchantment.DAMAGE_UNDEAD;
    final static Enchantment BANE_OF_ARTHROPODS = Enchantment.DAMAGE_ARTHROPODS;
    final static Enchantment KNOCKBACK = Enchantment.KNOCKBACK;
    final static Enchantment FIRE_ASPECT = Enchantment.FIRE_ASPECT;
    final static Enchantment LOOTING = Enchantment.LOOT_BONUS_MOBS;
    final static Enchantment EFFICIENCY = Enchantment.DIG_SPEED;
    final static Enchantment SILK_TOUCH = Enchantment.SILK_TOUCH;
    final static Enchantment UNBREAKING = Enchantment.DURABILITY;
    final static Enchantment FORTUNE = Enchantment.LOOT_BONUS_BLOCKS;
    final static Enchantment POWER = Enchantment.ARROW_DAMAGE;
    final static Enchantment PUNCH = Enchantment.ARROW_KNOCKBACK;
    final static Enchantment FLAME = Enchantment.ARROW_FIRE;
    final static Enchantment INFINITY = Enchantment.ARROW_INFINITE;
    
    static boolean defaultEffectEnabledState = true;
    
    static ConcurrentHashMap<Integer, Boolean> effectEnabledMap = new ConcurrentHashMap<Integer, Boolean>(); //List of enabled enchantment effects by packed enchantment and item id
    static ConcurrentHashMap<Integer, String> effectConfigSections = new ConcurrentHashMap<Integer, String>();
    static ConcurrentHashMap<Integer, EnchantMoreItemCategory> itemToCategory = new ConcurrentHashMap<Integer, EnchantMoreItemCategory>();
    static ConcurrentHashMap<String, Enchantment> enchByName = new ConcurrentHashMap<String, Enchantment>();
    static ConcurrentHashMap<Enchantment, String> nameByEnch = new ConcurrentHashMap<Enchantment, String>();
    static ConcurrentHashMap<EnchantMoreItemCategory, Object> categoryToItem = new ConcurrentHashMap<EnchantMoreItemCategory, Object>();
    static Random random = new Random();
    
    static HashSet<String> disableMsgCooldown = new HashSet<String>();
    static HashSet<String> disablePermMsgCooldown = new HashSet<String>();
    
    final int SPAWN_EGG_ID = 383;
	
	static EnchantMore plugin;
	public EnchantMoreListener(EnchantMore plugin) {
		this.plugin = plugin;
		loadConfig();
	}
	
	public static boolean hasEnch(ItemStack item, Enchantment ench, final Player player) {
		if (item == null) {
			return false;
		}
		if (getEffectEnabled(item.getTypeId(), ench)) { //Check if the effect is disabled in configuration
			if (item.containsEnchantment(ench)) {
				return checkPerm(item, ench, player);
			}
		}
		else if (plugin.getConfig().getBoolean("debugDisabledEffects")) {
			if (!disableMsgCooldown.contains(player.getName())) {
				player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + item.getType() + " (" + item.getTypeId() + ") + " + ench + " = " + packEnchItem(item.getTypeId(), ench) + " is disabled.");
				disableMsgCooldown.add(player.getName());
				
				BukkitTask task = new BukkitRunnable() {
					public void run() {
						disableMsgCooldown.remove(player.getName());
					}
				}.runTaskLater(plugin, plugin.getConfig().getInt("disableMsgCooldownTicks", 6000));
			}
			return false;
		}
		return false;
	}
	   //hello people
	public static boolean checkPerm(ItemStack item, Enchantment ench, final Player player) {
		if ((player.hasPermission(getPermission(item, ench)) && plugin.getConfig().getBoolean("usePermissions")) || !plugin.getConfig().getBoolean("usePermissions")) {
			return true;
		}
		else {
			if (plugin.getConfig().getBoolean("sendPermissionMessage")) {
				if (plugin.getConfig().getBoolean("useCooldown")) {
					if (!disablePermMsgCooldown.contains(player.getName())) {
						player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + item.getType() + " (" + item.getTypeId() + ") + " + ench + " = " + packEnchItem(item.getTypeId(), ench) + " requires " + ChatColor.GOLD + getPermission(item, ench) + ChatColor.RED + " permission.");
						disablePermMsgCooldown.add(player.getName());
						
						BukkitTask task = new BukkitRunnable() {
							public void run() {
								disablePermMsgCooldown.remove(player.getName());
							}
						}.runTaskLater(plugin, plugin.getConfig().getInt("permMsgCooldownTicks", 6000));
					}
				}
				else {
					player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Effect " + item.getType() + " (" + item.getTypeId() + ") + " + ench + " = " + packEnchItem(item.getTypeId(), ench) + " requires " + ChatColor.GOLD + getPermission(item, ench) + ChatColor.RED + " permission.");
				}
			}
		}
		return false;
	}
	
	
	public static String getPermission(ItemStack item, Enchantment ench) {
		if (nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase() == null || item == null) {
			return "enchantmore.enchantmore";
		}
		if (isHoe(item.getType())) {
			return "enchantmore.hoe." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		if (isSword(item.getType())) {
			return "enchantmore.sword." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		if (isPickaxe(item.getType())) {
			return "enchantmore.pickaxe." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		if (isShovel(item.getType())) {
			return "enchantmore.shovel." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		if (isAxe(item.getType())) {
			return "enchantmore.axe." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		if (isHelmet(item.getType())) {
			return "enchantmore.helmet." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		if (isChestplate(item.getType())) {
			return "enchantmore.chestplate." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		if (isLeggings(item.getType())) {
			return "enchantmore.leggings." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		if (isBoots(item.getType())) {
			return "enchantmore.boots." + nameByEnch.get(ench).replace("_", "").replace(" ", "").toLowerCase();
		}
		return "enchantmore." + item.getType().toString().toLowerCase().replace("_", "").replace(" ", "") + "." + nameByEnch.get(ench).replace(" ", "").replace(" ", "").toLowerCase();
	}
	
	public static int getLevel(ItemStack item, Enchantment ench) { //Get the enchantment level
		//TODO: Add level cap in config
		//TODO: Permission for overriding max level
		return item.getEnchantmentLevel(ench);
	}
    public static String getConfigSection(ItemStack item, Enchantment ench) {
        return effectConfigSections.get(packEnchItem(item.getTypeId(), ench));
    }
        
    public int getConfigInt(String name, int defaultValue, ItemStack item, Enchantment ench) {
        return plugin.getConfig().getInt(getConfigSection(item, ench) + "." + name, defaultValue);
    }

    public static double getConfigDouble(String name, double defaultValue, ItemStack item, Enchantment ench) {
        return plugin.getConfig().getDouble(getConfigSection(item, ench) + "." + name, defaultValue);
    }

    public boolean getConfigBoolean(String name, boolean defaultValue, ItemStack item, Enchantment ench) {
        return plugin.getConfig().getBoolean(getConfigSection(item, ench) + "." + name, defaultValue);
    }
    public String getConfigString(String name, String defaultValue, ItemStack item, Enchantment ench) {
        return plugin.getConfig().getString(getConfigSection(item, ench) + "." + name, defaultValue);
    }
    public Material getConfigMaterial(String name, Material defaultValue, ItemStack item, Enchantment ench) {
    	String s = getConfigString(name, null, item, ench);
    	if (s == null) {
    		return defaultValue;
    	}
        int id = getTypeIdByName(s);
        if (id == -1) {
        	return defaultValue;
        }
        return Material.getMaterial(id);
    }
    private void loadConfig() {
    	defaultEffectEnabledState = plugin.getConfig().getBoolean("defaultEffectEnabled", true); //Check config value if effect is enabled by default
    	//Enchantment IDs
    	MemorySection enchIDSection = (MemorySection)plugin.getConfig().get("enchantmentIDs");
    	if (enchIDSection == null) {
    		plugin.getLogger().severe("Error! Failed to load enchantment id data from config!");
    	}
    	else {
    		for (String enchName : enchIDSection.getKeys(false)) { //Map a list of enchantment names and ids from the config
        		int id = plugin.getConfig().getInt("enchantmentIDs." + enchName);
        		Enchantment ench = Enchantment.getById(id);
        		enchByName.put(enchName.toLowerCase(), ench);
        		enchByName.put(ench.getName().toLowerCase(), ench);
        		enchByName.put(String.valueOf(id), ench);
        		
        		nameByEnch.put(ench, enchName);
        	}
    	}
    	//Items and categories
    	MemorySection itemSection = (MemorySection)plugin.getConfig().get("items");
    	if (itemSection == null) {
    		plugin.getLogger().severe("Error! Failed to load item data from config!");
    	}
    	else {
    		for (String categoryName : itemSection.getKeys(false)) {
        		EnchantMoreItemCategory category = getCategoryByName(categoryName); //Item Categories
        		if (category == null) {
        			plugin.getLogger().warning("Item category '" + categoryName + "' invalid and ignored by EnchantMore.");
        			continue;
        		}
        		//Items in categories
        		List<String> itemNames = plugin.getConfig().getStringList("items." + categoryName);
        		for (String itemName : itemNames) {
        			String[] parts = itemName.split(";", 2);
        			int id = getTypeIdByName(parts[0]);
        			if (id == -1) {
        				plugin.getLogger().warning("Item '" + itemName + "' invalid and ignored by EnchantMore.");
        				continue;
        			}
        			//Item Data Field Check (Optional)
        			int packedId = id;
        			if (parts.length > 1) {
        				int data = 0;
            			try {
            				data = Integer.parseInt(parts[1], 10);
            			}
            			catch (Exception e) {
            				plugin.getLogger().warning("Item '" + parts[0] + "' invalid and ignored by EnchantExtreme");
            				continue;
            			}
            			packedId += data << 10;
        			}
        			if (itemToCategory.contains(packedId)) { //Check if item already exists in a category
        				plugin.getLogger().warning("Overlapping item  '" + itemName + "' (" + id + "). Category " + itemToCategory.get(id) + "is not equal to " + category + "! Ignored by EnchantMore.");
        				continue;
        			}
        			itemToCategory.put(packedId, category);
        			Object obj = categoryToItem.get(category);
        			if (obj == null) {
        				obj = new ArrayList<Integer>();
        			}
        			if (!(obj instanceof ArrayList)) {
        				plugin.getLogger().warning("Internal error adding items to category: " + categoryToItem);
        				continue;
        			}
        			List list = (List) obj;
        			list.add(id);
        			categoryToItem.put(category, list);
        		}
        	}
    	}
    	//Item Id and Enabled Effects
    	MemorySection effectsSection = (MemorySection) plugin.getConfig().get("effects");
    	for (String effectName : effectsSection.getKeys(false)) {
    		String sectionName = "effects." + effectName;
    		boolean enable = plugin.getConfig().getBoolean(sectionName + ".enable");
    		String[] parts = effectName.split(" \\+ ", 2);
    		if (parts.length != 2) {
    			plugin.getLogger().warning("Effect name '" + effectName + "' invalid and ignored by EnchantMore.");
    			continue;
    		}
    		String itemName = parts[0];
    		String enchName = parts[1];
    		Enchantment ench = enchByName.get(enchName.toLowerCase());
    		if (ench == null) {
    			plugin.getLogger().warning("Enchantment name '" + enchName + "' invalid and ignored by EnchantMore.");
    			continue;
    		}
    		//Item can be a category or an item name
    		EnchantMoreItemCategory category = getCategoryByName(itemName);
    		if (category != null) { //It's a category!
    			Object obj = categoryToItem.get(category);
    			if (obj == null || !(obj instanceof List)) {
    				plugin.getLogger().warning("Item Category '" + itemName + "'invalid and ignored by EnchantMore.");
    				continue;
    			}
    			List list = (List) obj;
    			for (Object item : list) {
    				if (item instanceof Integer) {
    					putEffectEnabled(((Integer) item).intValue(), ench, enable);
    					//effectConfigSections.put(packEnchItem(((Integer)item).intValue(), ench), sectionName);
    				}
    			}
    		}
    		else { //It's not a category
    			int id = getTypeIdByName(itemName);
    			if (id == -1) {
    				plugin.getLogger().warning("Item name '" + itemName + "' invalid and ignored by EnchantMore.");
    				continue;
    			}
    			putEffectEnabled(id, ench, enable);
    			effectConfigSections.put(packEnchItem(id, ench), sectionName);
    		}
    	}
    }
    static int packEnchItem(int itemId, Enchantment ench) { //Pack enchantment id and item id into a single integer for easy look up.
    	return itemId + (ench.getId()) << 20;
    }
    private void putEffectEnabled(int itemId, Enchantment ench, boolean enable) { //Enable an effect
    	int packed = packEnchItem(itemId, ench);
    	if (plugin.verboseLogger()) {
    		plugin.getLogger().info("Effect " + Material.getMaterial(itemId) + "(" + itemId + ") + " + ench + " = " + packed + " = " + enable);
    	}
    	/*if (effectEnabledMap.get(packed) != null) {
    		plugin.getLogger().severe("Overlapping effect! " + Material.getMaterial(itemId) + " (" + itemId + ") + " + ench + " = " + packed + " = " + enable);
    	}*/
    	effectEnabledMap.put(packed, enable);
    }
    public static boolean getEffectEnabled(int itemId, Enchantment ench) { //Check if an effect is enabled.
    	int packed = packEnchItem(itemId, ench);
    	Object obj = effectEnabledMap.get(packed);
    	if (obj == null) {
    		if (plugin.verboseLogger()) {
    			plugin.getLogger().info("Default state returned for " + Material.getMaterial(itemId) + " (" + itemId + ") + " + ench);
    		}
    		return defaultEffectEnabledState;
    	}
    	return ((Boolean) obj).booleanValue();
    }
    public EnchantMoreItemCategory getCategoryByName(String name) { //Get a category based on a string
    	try {
    		return EnchantMoreItemCategory.valueOf("IS_" + name.toUpperCase());
    	}
    	catch (IllegalArgumentException e) {
    		return null;
    	}
    }
    // Get material type ID, either from name or integer string
    // @returns -1 if error
    public int getTypeIdByName(String name) {
    	Material material = Material.matchMaterial(name);
    	if (material != null) {
    		return material.getId();
    	}
    	else {
    		if (name.equalsIgnoreCase("flint & steel")) {
    			return Material.FLINT_AND_STEEL.getId();
    		}
    		try {
    			return Integer.parseInt(name, 10);
    		}
    		catch (Exception e) {
    			return -1;
    		}
    	}
    }
	public static void damage(ItemStack tool, Player player) {
		damage(tool, 1, player);
	}
    public static boolean isHoe(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_HOE;
    }

    public static boolean isSword(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_SWORD;
    }

    public static boolean isPickaxe(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_PICKAXE;
    }

    public static boolean isShovel(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_SHOVEL;
    }

    public static boolean isAxe(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_AXE;
    }
    
    public static boolean isHelmet(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_HELMET;
    }
    
    public static boolean isChestplate(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_CHESTPLATE;
    }
    
    public static boolean isLeggings(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_LEGGINGS;
    }
    
    public static boolean isBoots(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_BOOTS;
    }

    public static boolean isFarmBlock(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_FARMBLOCK;
    }

    public static boolean isExcavatable(int id) {
        return itemToCategory.get(id) == EnchantMoreItemCategory.IS_EXCAVATABLE;
    }

    public static boolean isExcavatable(Material m) {
        return itemToCategory.get(m.getId()) == EnchantMoreItemCategory.IS_EXCAVATABLE;
    }

    public static boolean isWoodenBlock(Material m, byte data) {
        return itemToCategory.get(m.getId() + (data << 10)) == EnchantMoreItemCategory.IS_WOODENBLOCK;
    }
    public boolean canSmelt(ItemStack raw) {
    	ItemStack result = null;
    	Iterator<Recipe> itr = Bukkit.recipeIterator();
    	while (itr.hasNext()) {
    		Recipe recipe = itr.next();
    		if (!(recipe instanceof FurnaceRecipe)) continue;
    		if (((FurnaceRecipe) recipe).getInput().getType() != raw.getType()) continue;
    		result = recipe.getResult();
    	}
    	if (result != null) {
    		return true;
    	}
    	return false;
    }
    public ItemStack smelt(ItemStack raw) {
    	ItemStack result = null;
    	Iterator<Recipe> itr = Bukkit.recipeIterator();
    	while (itr.hasNext()) {
    		Recipe recipe = itr.next();
    		if (!(recipe instanceof FurnaceRecipe)) continue;
    		if (((FurnaceRecipe) recipe).getInput().getType() != raw.getType()) continue;
    		result = recipe.getResult();
    	}
    	return result;
    }
    @SuppressWarnings("deprecation") //It's deprecated, but there is currently no replacement :(
	public static void updateInventory(Player player) {
    	player.updateInventory();
    }
	public static void damage(ItemStack tool, double amount, Player player) {
		if (tool.getDurability() > tool.getType().getMaxDurability()) {
			player.getInventory().remove(tool);
		}
		else {
			tool.setDurability((short) (tool.getDurability() + amount));
			updateInventory(player);
		}
	}
    private void growStructure(Location loc, Player player) {
    	int x = loc.getBlockX(),
    			y = loc.getBlockY(),
    			z = loc.getBlockZ();
    	World world = loc.getWorld();
    	ItemStack boneMeal = (new ItemStack(Material.INK_SACK, 1, (short)15));
    	net.minecraft.server.v1_6_R3.ItemStack craftBoneMeal = CraftItemStack.asNMSCopy(boneMeal);
    	net.minecraft.server.v1_6_R3.Item.INK_SACK.interactWith(craftBoneMeal, ((CraftPlayer)player).getHandle(), ((CraftWorld)world).getHandle(), x, y, z, 0, x, y, z);
    }
    private String serialiseLocation(Location loc) { //Automagically transform a location into a String. Magic ain't it?
    	if (loc == null) {
    		return "()";
    	}
    	else {
    		return "(" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName() + ")";
    	}
    }
    private short getEntityTypeId(Entity entity) { //Get the Entity Id of a creature (for spawn eggs)
    	Class<?>[] classes = entity.getClass().getInterfaces();
    	if (classes.length != 1) {
    		return -1;
    	}
    	Class<?> clazz = classes[0];
    	EntityType creatureType = EntityType.fromName(clazz.getSimpleName());
    	if (creatureType == null) {
    		return -1;
    	}
    	return entity.getType().getTypeId();
    }
    private int getFireTicks(int level) { //Get time to burn entity for depending on the enchantment level
    	//TODO: Configurable ticks per level
    	return 20 * 10 * level;
    }
    private void fellTree(Player player, Block start, ItemStack tool, int level, int id1, int id2, int id3) {
    	start.breakNaturally();
    	for (int dx = -level; dx <= level; dx += 1) {
    		for (int dy = -level; dy <= level; dy += 1) {
    			for (int dz = -level; dz <= level; dz += 1) {
    				Block branch = start.getRelative(dx, dy, dz);
    				if ((branch != null) && (branch.getTypeId() == id1 || branch.getTypeId() == id2 || branch.getTypeId() == id3)) {
    					if (branch.breakNaturally()) {
    						plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(branch, player));
    					}
    				}
    			}
    		}
    	}
    }
    private void hedgeTrimmer(Player player, Block start, ItemStack till, int level, int itemId, Enchantment ench) {
    	//TODO: do a sphere! or other shapes! topiary
    	int packed = packEnchItem(itemId, ench);
    	String shape = "square";
    	if (plugin.getConfig().getString(packed + ".shape") == "sphere") {
    		shape = "sphere";
    	}
    	if (shape == "square") {
    		for (int dx = -level; dx <= level; dx += 1) {
        		for (int dy = -level; dy <= level; dy += 1) {
        			for (int dz = -level; dz <= level; dz += 1) {
        				Block leaf = start.getRelative(dx, dy, dz);
        				if (leaf != null && leaf.getType() == Material.LEAVES) {
        					leaf.breakNaturally();
        					plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(leaf, player));
        				}
        			}
        		}
        	}
    	}
    	else if (shape == "sphere") {
    		List<Location> blocks = circle(start.getLocation(), level, level, true, true);
    		for (Location loc : blocks) {
    			Block block = loc.getBlock();
    			block.breakNaturally();
    			plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
    		}
    	}
    }
    private List<Location> circle(Location loc, int r, int h, boolean hollow, boolean sphere) {
    	List<Location> blocks = new ArrayList<Location>();
    	int cx = loc.getBlockX(),
    			cy = loc.getBlockY(),
    			cz = loc.getBlockZ();
    	for (int x = cx - r; x <= cx +r; x++)
    		for (int z = cz - r; z <= cz +r; z++)
    			for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
    				double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
    				if (dist < r*r && !(hollow && dist < (r-1)*(r-1))) {
    					Location l = new Location(loc.getWorld(), x, y, z);
    					blocks.add(l);
    				}
    			}
    	return blocks;
    }
    private Collection<ItemStack> uncraft(ItemStack result) { //Uncraft an item, without the use of NMS :D
    	List<Recipe> recipes = Bukkit.getRecipesFor(result);
    	if (!recipes.isEmpty()) {
    		Collection<ItemStack> inputs = new ArrayList<ItemStack>();
    		Random r = new Random();
        	for (Recipe recipe : recipes) {
        		int rt = r.nextInt(recipes.toArray().length);
        		Recipe finalRecipe = recipes.get(rt);
        		if (finalRecipe instanceof ShapedRecipe) {
        			ShapedRecipe sr = (ShapedRecipe) finalRecipe;
        			Map<Character, ItemStack> index = sr.getIngredientMap();
        			for (ItemStack itemstack : index.values()) {
        				inputs.add(itemstack);
        			}
        		}
        		else if (finalRecipe instanceof ShapelessRecipe) {
        			ShapelessRecipe sr = (ShapelessRecipe) finalRecipe;
        			List<ItemStack> index = sr.getIngredientList();
        			for (ItemStack itemstack : index) {
        				inputs.add(itemstack);
        			}
        		}
        	}
        	return inputs;
    	}
    	Collection<ItemStack> inputs = new ArrayList<ItemStack>();
    	inputs.add(result);
    	return inputs;
    }
    private boolean isSplashPotion(ItemStack item) { //Check if a potion is a splash potion (updated from old NMS code)
    	if (item.getType() != Material.POTION) {
    		return false;
    	}
    	Potion potion = new Potion(item.getDurability());
    	return potion.isSplash();
    }
   
    public EntityType creatureTypeFromId(int eid) {//TODO: Update this every time Minecraft brings in new mobs.
    	try {
    		return EntityType.fromId(eid);
    	}
    	catch (NoSuchMethodError e) {}
    	switch (eid) { //Updated to support all entity types.
    	case 1: return EntityType.DROPPED_ITEM;
    	case 2: return EntityType.EXPERIENCE_ORB;
    	
    	case 9: return EntityType.PAINTING;
    	case 10: return EntityType.ARROW;
    	case 11: return EntityType.SNOWBALL;
    	case 12: return EntityType.FIREBALL;
    	case 13: return EntityType.SMALL_FIREBALL;
    	case 14: return EntityType.ENDER_PEARL;
    	case 15: return EntityType.ENDER_SIGNAL;
    	case 16: return EntityType.SPLASH_POTION;
    	case 17: return EntityType.THROWN_EXP_BOTTLE;
    	case 18: return EntityType.ITEM_FRAME;
    	case 19: return EntityType.WITHER_SKULL;
    	
    	case 20: return EntityType.PRIMED_TNT;
    	case 21: return EntityType.FALLING_BLOCK;
    	case 22: return EntityType.FIREWORK;
    	
    	case 41: return EntityType.BOAT;
    	case 42: return EntityType.MINECART;
    	case 43: return EntityType.MINECART;
    	case 44: return EntityType.MINECART;
    	case 45: return EntityType.MINECART;
    	case 46: return EntityType.MINECART;
    	case 47: return EntityType.MINECART;
    	
    	case 50: return EntityType.CREEPER;
    	case 51: return EntityType.SKELETON;
    	case 52: return EntityType.SPIDER;
    	case 53: return EntityType.GIANT;
    	case 54: return EntityType.ZOMBIE;
    	case 55: return EntityType.SLIME;
    	case 56: return EntityType.GHAST;
    	case 57: return EntityType.PIG_ZOMBIE;
    	case 58: return EntityType.ENDERMAN;
    	case 59: return EntityType.CAVE_SPIDER;
    	case 60: return EntityType.SILVERFISH;
    	case 61: return EntityType.BLAZE;
    	case 62: return EntityType.MAGMA_CUBE;
    	case 63: return EntityType.ENDER_DRAGON;
    	case 64: return EntityType.WITHER;
    	case 66: return EntityType.WITCH;
    	
    	case 65: return EntityType.BAT;
    	case 90: return EntityType.PIG;
    	case 91: return EntityType.SHEEP;
    	case 92: return EntityType.COW;
    	case 93: return EntityType.CHICKEN;
    	case 94: return EntityType.SQUID;
    	case 95: return EntityType.WOLF;
    	case 96: return EntityType.MUSHROOM_COW;
    	case 97: return EntityType.SNOWMAN;
    	case 98: return EntityType.OCELOT;
    	case 99: return EntityType.IRON_GOLEM;
    	
    	case 120: return EntityType.VILLAGER;
    	
    	case 200: return EntityType.ENDER_CRYSTAL;
    	}
		return null;
    }
    public void fireworkEffect(String effects, World world, Location loc) {
    	ArrayList<Color> colors = new ArrayList<Color>();
		boolean flicker = false;
		boolean trail = false;
		FireworkEffect.Type type = FireworkEffect.Type.BALL;
		if (effects.contains("aqua")) {
			colors.add(Color.AQUA);
		}
		if (effects.contains("black")) {
			colors.add(Color.BLACK);
		}
		if (effects.contains("blue")) {
			colors.add(Color.BLUE);
		}
		if (effects.contains("fuchsia")) {
			colors.add(Color.FUCHSIA);
		}
		if (effects.contains("gray")) {
			colors.add(Color.GRAY);
		}
		if (effects.contains("green")) {
			colors.add(Color.GREEN);
		}
		if (effects.contains("lime")) {
			colors.add(Color.LIME);
		}
		if (effects.contains("maroon")) {
			colors.add(Color.MAROON);
		}
		if (effects.contains("navy")) {
			colors.add(Color.NAVY);
		}
		if (effects.contains("olive")) {
			colors.add(Color.OLIVE);
		}
		if (effects.contains("orange")) {
			colors.add(Color.ORANGE);
		}
		if (effects.contains("purple")) {
			colors.add(Color.PURPLE);
		}
		if (effects.contains("red")) {
			colors.add(Color.RED);
		}
		if (effects.contains("silver")) {
			colors.add(Color.SILVER);
		}
		if (effects.contains("teal")) {
			colors.add(Color.TEAL);
		}
		if (effects.contains("white")) {
			colors.add(Color.WHITE);
		}
		if (effects.contains("yellow")) {
			colors.add(Color.YELLOW);
		}
		if (colors.isEmpty()) {
			colors.add(Color.WHITE);
		}
		
		if (effects.contains("flicker")) {
			flicker = true;
		}
		if (effects.contains("trail")) {
			trail = true;
		}
		
		if (effects.contains("small")) {
			type = FireworkEffect.Type.BALL;
		}
		if (effects.contains("large")) {
			type = FireworkEffect.Type.BALL_LARGE;
		}
		if (effects.contains("star")) {
			type = FireworkEffect.Type.STAR;
		}
		if (effects.contains("burst")) {
			type = FireworkEffect.Type.BURST;
		}
		if (effects.contains("creeper")) {
			type = FireworkEffect.Type.CREEPER;
		}
		
		FireworkEffect fe = FireworkEffect.builder().flicker(flicker)
				.trail(trail)
				.withColor(colors)
				.withFade(colors)
				.with(type)
				.build();
		
		try {
			playFirework(world, loc, fe);
		} catch (Exception e){}
    }
    private static Object[] dataStore = new Object[5];
    public static void playFirework(World world, Location loc, FireworkEffect fe) throws Exception {
        Firework fw = (Firework) world.spawn(loc, Firework.class);
        if(dataStore[0] == null) dataStore[0] = getMethod(world.getClass(), "getHandle");
        if(dataStore[2] == null) dataStore[2] = getMethod(fw.getClass(), "getHandle");
        dataStore[3] = ((Method) dataStore[0]).invoke(world, (Object[]) null);
        dataStore[4] = ((Method) dataStore[2]).invoke(fw, (Object[]) null);
        if(dataStore[1] == null) dataStore[1] = getMethod(dataStore[3].getClass(), "broadcastEntityEffect");
        FireworkMeta data = (FireworkMeta) fw.getFireworkMeta();
        data.addEffect(fe);
        fw.setFireworkMeta(data);
        ((Method) dataStore[1]).invoke(dataStore[3], new Object[] {dataStore[4], (byte) 17});
        fw.remove();
    }
    private static Method getMethod(Class<?> cl, String method) {
        for(Method m : cl.getMethods()) if(m.getName().equals(method)) return m;
        return null;
    }
    public static void setValue(Object instance, String fieldName, Object value) throws Exception {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(instance, value);
	}
	public static void sendPacketToLocation(Location loc, Object packet) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getWorld() == loc.getWorld()) {
				/*if(((loc.getX() + 35) > loc.getX() || loc.getX() > (loc.getX() - 35)) || ((loc.getZ() + 35) > loc.getZ() || loc.getZ() > (loc.getZ() - 35)) || ((loc.getY() + 35) > loc.getY() || loc.getY() > (loc.getY() - 35))){
					try {
						Method getHandle = p.getClass().getMethod("getHandle");
						Object nmsPlayer = getHandle.invoke(p);
						Field con_field = nmsPlayer.getClass().getField("playerConnection");
						Object con = con_field.get(nmsPlayer);
						Method packet_method = getMethod(con.getClass(), "sendPacket");
						packet_method.invoke(con, packet);
					} catch (Exception e) {}
				}*/
				try {
					Method getHandle = p.getClass().getMethod("getHandle");
					Object nmsPlayer = getHandle.invoke(p);
					Field con_field = nmsPlayer.getClass().getField("playerConnection");
					Object con = con_field.get(nmsPlayer);
					Method packet_method = getMethod(con.getClass(), "sendPacket");
					packet_method.invoke(con, packet);
				} catch (Exception e) {}
			}
			try {
				Method getHandle = p.getClass().getMethod("getHandle");
				Object nmsPlayer = getHandle.invoke(p);
				Field con_field = nmsPlayer.getClass().getField("playerConnection");
				Object con = con_field.get(nmsPlayer);
				Method packet_method = getMethod(con.getClass(), "sendPacket");
				packet_method.invoke(con, packet);
			} catch (Exception e) {}
		}
	}
	public String capitalise(String s) {
		String finalString = "";
		if (s.contains(" ")) {
			StringBuilder builder = new StringBuilder();
			String[] sp = s.split(" ");
			for (String string : sp) {
				string = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
				builder.append(string);
				builder.append(" ");
			}
			builder.deleteCharAt(builder.length() - 1);
			finalString = builder.toString();
		}
		else {
			finalString = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
		}
		return finalString;
	}
    //Events
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Block block = event.getClickedBlock();
    	ItemStack item = event.getItem();
    	Action action = event.getAction();
    	Player player = event.getPlayer();
    	World world = player.getWorld();
    	if (!plugin.canBuild(player, block)) {
    		return;
    	}
    	if (item == null) {
    		return;
    	}
    	//Actions that do not require a block
    	//This event does not seem to fire when the air is right clicked while holding a bow...Huh.
    	//TODO: Fix this
    	if (item.getType() == Material.BOW && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
    		//Bow + Efficiency = Instant Shoot
    		if (hasEnch(item, EFFICIENCY, player)) {
    			if (player.getInventory().contains(Material.ARROW)) {
    				Arrow arrow = (Arrow) player.launchProjectile(Arrow.class);
    				int slot = player.getInventory().first(Material.ARROW);
    				ItemStack i = player.getInventory().getItem(slot);
    				i.setAmount(i.getAmount() - 1);
    				player.getInventory().setItem(slot, i);
    			}
    		}
    	}
    	else if (item.getType() == Material.FLINT_AND_STEEL && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
    		//Flint and Steel + Punch = Cannon
    		if (hasEnch(item, PUNCH, player)) {
    			Location loc = player.getLocation().add(0, 2, 0);
    			if (plugin.canExplode(player, block, Material.FLINT_AND_STEEL.getId(), PUNCH)) {
    				TNTPrimed tnt = (TNTPrimed) world.spawn(loc, TNTPrimed.class);
        			int n = getLevel(item, PUNCH);
        			tnt.setVelocity(player.getLocation().getDirection().normalize().multiply(n));
        			damage(item, player);
    			}
    		}
    		//Flint and Steel + Silk Touch = Remote detonation (Ignite TNT)
    		if (hasEnch(item, SILK_TOUCH, player)) {
    			if (plugin.canExplode(player, block, Material.FLINT_AND_STEEL.getId(), SILK_TOUCH)) {
    				world.createExplosion(player.getTargetBlock(null, 50).getLocation(), 4f);
    				/*int r = getLevel(item, SILK_TOUCH) * 10;
        			int x0 = player.getLocation().getBlockX();
        			int y0 = player.getLocation().getBlockY();
        			int z0 = player.getLocation().getBlockZ();
        			int tntId = Material.TNT.getId();
        			for (int dx = -r; dx < r; dx += 1) {
        				for (int dy = -r; dy < r; dy += 1) {
        					for (int dz = -r; dz < r; dz += 1) {
        						int x = dx + x0;
        						int y = dy + y0;
        						int z = dz + z0;
        						int type = world.getBlockTypeIdAt(x, y, z);
        						if (type == tntId) {
        							Block b = world.getBlockAt(x, y, z);
        							if (plugin.safeSetBlock(player, b, Material.AIR, item.getTypeId(), SILK_TOUCH)) {
        								TNTPrimed tnt = (TNTPrimed) world.spawn(new Location(world, x, y, z), TNTPrimed.class);
        								tnt.setFireTicks(0); //BOOM!
        							}
        						}
        					}
        				}
        			}*/
    			}
    		}
    	}
    	else if (isSword(item.getType())) {
    		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
    			//Sword + Power = Strike lightning far away
    			if (hasEnch(item, POWER, player)) {
    				if (plugin.canStrikeLightning(player, block, item.getTypeId(), POWER)) {
    					int maxDistance = getConfigInt("rangePerLevel", 100, item, POWER);
        				Block target = player.getTargetBlock(null, maxDistance * getLevel(item, FLAME));
        				if (target != null) {
        					world.strikeLightning(target.getLocation());
        				}
    				}
    			}
    		}
    		else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
    			//Sword + Blast Protection = Shoot fireball (right click)
    			if (hasEnch(item, BLAST_PROTECTION, player)) {
    				if (plugin.canExplode(player, block, item.getTypeId(), BLAST_PROTECTION)) {
        				Vector vector = player.getEyeLocation().getDirection();
            			Projectile projectile = player.launchProjectile(Fireball.class);
            			projectile.setVelocity(vector);
            			projectile.setShooter(player);
        			}
    			}
    		}
    	}
    	else if (isShovel(item.getType())) {
    		//Shovel + Silk Touch II = Harvest Fire
    		if (hasEnch(item, SILK_TOUCH, player)) {
    			int minLevel = getConfigInt("minLevel", 2, item, SILK_TOUCH);
    			if (getLevel(item, SILK_TOUCH) == minLevel && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
    				Block target = player.getTargetBlock(null, 3 * getLevel(item, SILK_TOUCH));
    				if (target.getType() == Material.FIRE) {
    					if (plugin.canDropItem(player, block, item.getTypeId(), SILK_TOUCH)) {
    						world.dropItemNaturally(target.getLocation(), new ItemStack(target.getType(), 1));
    					}
    				}
    			}
    			//Shovel + Silk Touch III = Harvest Water, Lava and Fire
    			else if (getLevel(item, SILK_TOUCH) > minLevel && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
    				Block target = player.getTargetBlock(null, 3 * getLevel(item, SILK_TOUCH));
    				if (target.getType() == Material.FIRE || target.getType() == Material.WATER || target.getType() == Material.STATIONARY_WATER || target.getType() == Material.LAVA || target.getType() == Material.STATIONARY_LAVA) {
    					if (plugin.canDropItem(player, block, item.getTypeId(), SILK_TOUCH)) {
    						world.dropItemNaturally(target.getLocation(), new ItemStack(target.getType(), 1));
    					}
    				}
    			}
    		}
    	}
    	else if (isHoe(item.getType())) {
    		//Hoe + Power = Change Time
    		if (hasEnch(item, POWER, player)) {
    			int sign, amount;
    			switch(item.getType()) {
    			case WOOD_HOE: amount = 1; break;
    			case STONE_HOE: amount = 10; break;
    			default:
    			case IRON_HOE: amount = 100; break;
    			case GOLD_HOE: amount = 10000; break;
    			case DIAMOND_HOE: amount = 1000; break;
    			}
    			switch(action) {
    			case LEFT_CLICK_AIR:
    			case LEFT_CLICK_BLOCK:
    				sign = -1;
    				break;
    			case RIGHT_CLICK_AIR:
    			case RIGHT_CLICK_BLOCK:
    			default:
    				sign = 1;
    				break;
    			}
    			int dt = sign * amount;
    			world.setTime(world.getTime() + dt);
    			damage(item, player);
    		}
    		//Hoe + Bane Of Arthropods = Set Weather
    		if (hasEnch(item, BANE_OF_ARTHROPODS, player)) {
    			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
    				world.setStorm(true);
    			}
    			else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
    				world.setStorm(false);
    			}
    			else {
    				world.setStorm(!world.hasStorm());
    			}
    			damage(item, player);
    		}
    		//Hoe + Fire Protection = Block Information
    		if (hasEnch(item, FIRE_PROTECTION, player)) {
    			Block target = player.getTargetBlock(null, 100);
    			boolean showSeed = getLevel(item, FIRE_PROTECTION) >= 2;
    			int x = target.getLocation().getBlockX();
    			int z = target.getLocation().getBlockZ();
    			player.sendMessage(ChatColor.DARK_AQUA + "-------- EnchantMore Block Information --------");
    			player.sendMessage(ChatColor.DARK_AQUA + "Biome: " + ChatColor.AQUA + world.getBiome(x, z));
    			player.sendMessage(ChatColor.DARK_AQUA + "Time: " + ChatColor.AQUA + world.getFullTime());
    			player.sendMessage(ChatColor.DARK_AQUA + "Sea Level: " + ChatColor.AQUA + world.getSeaLevel());
    			player.sendMessage(ChatColor.DARK_AQUA + "Weather: " + ChatColor.AQUA + world.getWeatherDuration());
    			player.sendMessage(ChatColor.DARK_AQUA + "Block: " + ChatColor.AQUA + target.getTypeId() + ";" + target.getData() + " (" + Material.getMaterial(target.getTypeId()) + ")");
    			player.sendMessage(ChatColor.DARK_AQUA + "Light: " + ChatColor.AQUA + target.getLightLevel() + " (" + target.getLightFromSky() + "/" + target.getLightFromBlocks() + ")");
    			player.sendMessage(
    					ChatColor.DARK_AQUA + "Data: " + ChatColor.AQUA +
    					(target.isBlockPowered() ? "Powered" : (target.isBlockIndirectlyPowered() ? "Powered (Indirect)" : "")) +
    					(target.isLiquid() ? "Liquid" : "") +
    					(target.isEmpty() ? "Empty" : ""));
    			if (showSeed) {
    				player.sendMessage(ChatColor.DARK_AQUA + "Seed: " + ChatColor.AQUA + world.getSeed());
    			}
    			player.sendMessage(ChatColor.DARK_AQUA + "-----------------------------------------------");
    		}
    	}
    	if (block == null) {
    		return;
    	}
    	//Everything else requires a block
    	if (item.getType() == Material.SHEARS) {
    		//Shears + Power = Hedge builder; cut grass (Secondary Effect)
    		if (hasEnch(item, POWER, player)) {
    			if (plugin.canBuild(player, block, item.getTypeId(), POWER)) {
    				int n = getLevel(item, POWER);
        			//If grass, cut into dirt
        			if (block.getType() == Material.GRASS) {
        				plugin.safeSetBlock(player, block, Material.DIRT, item.getTypeId(), POWER);
        				damage(item, player);
        			}
        			//If leaves, build hedges
        			else if (block.getType() == Material.LEAVES) {
        				int leavesSlot = player.getInventory().first(Material.LEAVES);
        				if (leavesSlot != -1) {
        					ItemStack leavesStack = player.getInventory().getItem(leavesSlot);
        					int packed = packEnchItem(item.getTypeId(), POWER);
        					String shape = "square";
        					if (plugin.getConfig().getString(packed + ".shape") == "sphere") {
        						shape = "sphere";
        					}
        					if (shape == "square") {
        						for (int dx = -n; dx <= n; dx += 1) {
            						for (int dy = -n; dy <= n; dy += 1) {
            							for (int dz = -n; dz <= n; dz += 1) {
            								Block b = block.getRelative(dx, dy, dz);
            								if (b.getType() == Material.AIR && leavesStack.getAmount() > 0) {
            									plugin.safeSetBlock(player, b, leavesStack.getType(), item.getTypeId(), POWER);
            									byte data = leavesStack.getData().getData();
            									data |= 4; //Permanent player-placed leaves; never decay
            									b.setData(data);
            									leavesStack.setAmount(leavesStack.getAmount() - 1);
            								}
            							}
            						}
            					}
        					}
        					else if (shape == "sphere") {
        						List<Location> blocks = circle(block.getLocation(), 5, 5, true, true);
        			    		for (Location loc : blocks) {
        			    			Block b = loc.getBlock();
        			    			if (b.getType() == Material.AIR && leavesStack.getAmount() > 0) {
    									plugin.safeSetBlock(player, b, leavesStack.getType(), item.getTypeId(), POWER);
    									byte data = leavesStack.getData().getData();
    									data |= 4; //Permanent player-placed leaves; never decay
    									b.setData(data);
    									leavesStack.setAmount(leavesStack.getAmount() - 1);
    								}
        			    		}
        					}
        					if (leavesStack.getAmount() == 0) {
    							player.getInventory().clear(leavesSlot);
    						}
    						else {
    							player.getInventory().setItem(leavesSlot, leavesStack);
    						}
    						updateInventory(player);
        				}
        				damage(item, player);
        			}
    			}
    		}
    	}
    	else if (item.getType() == Material.FLINT_AND_STEEL && action == Action.RIGHT_CLICK_BLOCK) {
    		// Flint & Steel + Smite = Strike lightning
    		if (hasEnch(item, SMITE, player)) {
    			if (plugin.canStrikeLightning(player, block, item.getTypeId(), SMITE)) {
    				world.strikeLightning(block.getLocation());
            		damage(item, 9, player);
    			}
    		}
    		//Flint And Steel + Fire Protection = Fire Resistance
    		if (hasEnch(item, FIRE_PROTECTION, player)) {
    			player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, getLevel(item, FIRE_PROTECTION)*20*5, 1));
    		}
    		//Flint & Steel + Aqua Affinity = Vaporise Water
    		if (hasEnch(item, AQUA_AFFINITY, player)) {
    			//Find water within ignited cube area
    			if (plugin.canBuild(player, block.getLocation(), item.getTypeId(), AQUA_AFFINITY)) {
    				int r = getLevel(item, AQUA_AFFINITY);
        			String shape = "square";
    				if (plugin.getConfig().getString(packEnchItem(item.getTypeId(), AQUA_AFFINITY) + ".shape") == "sphere") {
    					shape = "sphere";
    				}
    				if (shape.equalsIgnoreCase("square")) {
    					Location loc = block.getLocation();
    					int x0 = loc.getBlockX();
    					int y0 = loc.getBlockY();
    					int z0 = loc.getBlockZ();
    					for (int dx = -r; dx <= r; dx += 1) {
    						for (int dy = -r; dy <= r; dy += 1) {
    							for (int dz = -r; dz <= r; dz += 1) {
    								Block b = world.getBlockAt(dx+x0, dy+y0, dz+z0);
    								if (b.getType() == Material.STATIONARY_WATER || b.getType() == Material.WATER) {
    									plugin.safeSetBlock(player, b, Material.AIR, item.getTypeId(), AQUA_AFFINITY);
    									String effect = getConfigString("effect", "smoke", item, AQUA_AFFINITY);
    									if (effect.equalsIgnoreCase("smoke")) {
    										world.playEffect(b.getLocation(), Effect.SMOKE, 0);
    									}
    									else if (effect.equalsIgnoreCase("fire")) {
    										world.playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
    									}
    									else if (effect.equalsIgnoreCase("ender")) {
    										world.playEffect(b.getLocation(), Effect.ENDER_SIGNAL, 0);
    									}
    									else if (effect.equalsIgnoreCase("explosion")) {
    										world.createExplosion(b.getLocation(), 0);
    									}
    								}
    							}
    						}
    					}
    				}
    				else if (shape.equalsIgnoreCase("sphere")) {
    					List<Location> blocks = circle(block.getLocation(), 5, 5, true, true);
    		    		for (Location loc : blocks) {
    		    			Block b = loc.getBlock();
    		    			if (b.getType() == Material.STATIONARY_WATER || b.getType() == Material.WATER) {
    							plugin.safeSetBlock(player, b, Material.AIR, item.getTypeId(), AQUA_AFFINITY);
    							String effect = getConfigString("effect", "smoke", item, AQUA_AFFINITY);
    							if (effect.equalsIgnoreCase("smoke")) {
    								world.playEffect(b.getLocation(), Effect.SMOKE, 0);
    							}
    							else if (effect.equalsIgnoreCase("fire")) {
    								world.playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
    							}
    							else if (effect.equalsIgnoreCase("ender")) {
    								world.playEffect(b.getLocation(), Effect.ENDER_SIGNAL, 0);
    							}
    							else if (effect.equalsIgnoreCase("explosion")) {
    								world.createExplosion(b.getLocation(), 0);
    							}
    						}
    		    		}
    				}
    				event.setCancelled(true);
    			}
    		}
    		//Flint & Steel + Sharpness = Firey explosion
    		if (hasEnch(item, SHARPNESS, player)) {
    			if (plugin.canExplode(player, block, item.getTypeId(), SHARPNESS)) {
    				float power = (getLevel(item, SHARPNESS) - 1) * 1.0f;
        			world.createExplosion(block.getLocation(), power, true);
        			damage(item, player);
        			if (getConfigBoolean("fireworkEffect", false, item, SHARPNESS)) {
        				fireworkEffect(getConfigString("fwCharacteristics", "trail:blue:aqua:small", item, SHARPNESS), world, block.getLocation());
        			}
    			}
    		}
    		//Flint & Steel + Efficiency = Burn Faster (Turn wood to leaves)
    		if (hasEnch(item, EFFICIENCY, player)) {
    			if (isWoodenBlock(block.getType(), block.getData())) {
    				if (plugin.canBuild(player, block, item.getTypeId(), EFFICIENCY)) {
    					plugin.safeSetBlock(player, block, Material.LEAVES, item.getTypeId(), EFFICIENCY);
    				}
    			}
    		}
    	}
    	else if (isHoe(item.getType())) {
    		//Hoe + Aqua Affinity = Auto-Hydrate
    		if (hasEnch(item, AQUA_AFFINITY, player)) {
    			if (plugin.canBuild(player, block, item.getTypeId(), AQUA_AFFINITY)) {
    				//As long as not in hell, hydrate nearby
        			if (world.getEnvironment() != World.Environment.NETHER) {
        				int n = getLevel(item, AQUA_AFFINITY);
        				// Change adjacent air blocks to water
        				for (int dx = -1; dx <= 1; dx += 1) {
        					for (int dz = -1; dz <= 1; dz += 1) {
        						Block near = block.getRelative(dx * n, 0, dz * n);
        						//If either air or flowing water, make stationary water
        						if (near.getType() == Material.AIR || near.getType() == Material.WATER) {
        							plugin.safeSetBlock(player, near, Material.STATIONARY_WATER, item.getTypeId(), AQUA_AFFINITY);
        						}
        					}
        				}
        			}
        			else {
        				world.playEffect(block.getLocation(), Effect.SMOKE, 0);
    					world.playEffect(block.getLocation(), Effect.SMOKE, 1);
    					world.playEffect(block.getLocation(), Effect.SMOKE, 2);
    					world.playEffect(block.getLocation(), Effect.SMOKE, 3);
    					world.playEffect(block.getLocation(), Effect.SMOKE, 4);
    					world.playEffect(block.getLocation(), Effect.SMOKE, 5);
    					world.playEffect(block.getLocation(), Effect.SMOKE, 6);
    					world.playEffect(block.getLocation(), Effect.SMOKE, 7);
    					world.playEffect(block.getLocation(), Effect.SMOKE, 8);
        			}
    			}
    		}
    		//Hoe + Fortune = Chance to drop seeds
    		if (hasEnch(item, FORTUNE, player) && action == Action.RIGHT_CLICK_BLOCK) {
    			if (block.getType() == Material.DIRT || block.getType() == Material.GRASS) {
    				int chance = getConfigInt("chanceDropSeeds", 2, item, FORTUNE); //TODO: Depend on level?
    				if (random.nextInt(chance) == 0) {
    					int rollMax = getConfigInt("dropRollMax", 4, item, FORTUNE);
    					int roll = random.nextInt(rollMax);
    					Material seedType = getConfigMaterial("drops." + roll, Material.SEEDS, item, FORTUNE);
    					ItemStack drop = new ItemStack(seedType, 1);
    					if (plugin.canDropItem(player, block, item.getTypeId(), SILK_TOUCH)) {
    						world.dropItemNaturally(block.getRelative(BlockFace.UP).getLocation(), drop);
    					}
    				}
    			}
    		}
    		//Hoe + Efficiency = Till larger area
    		if (hasEnch(item, EFFICIENCY, player)) {
    			if (plugin.canBuild(player, block, item.getTypeId(), EFFICIENCY)) {
    				int r = getLevel(item, EFFICIENCY);
        			Location loc = block.getLocation();
        			int x0 = loc.getBlockX();
        			int y0 = loc.getBlockY();
        			int z0 = loc.getBlockZ();
        			for (int dx = -r; dx <= r; dx += 1) {
        				for (int dz = -r; dz <= r; dz += 1) {
        					Block b = world.getBlockAt(dx+x0, y0, dz+z0);
        					if (b.getType() == Material.DIRT || b.getType() == Material.GRASS) {
        						plugin.safeSetBlock(player, b, Material.SOIL, item.getTypeId(), EFFICIENCY);
        					}
        				}
        			}
        			damage(item, player);
    			}
    		}
    		/*Hoe + Respiration = Grow
            Note, left-click will also destroy sensitive plants (wheat, saplings, though interestingly not shrooms),
            so it will only work on blocks like grass (which does not break instantly). For 
            this reason, also allow right-click for grow, even though it means you cannot till.*/
    		if (hasEnch(item, RESPIRATION, player)) {
    			if (plugin.canBuild(player, block, item.getTypeId(), RESPIRATION)) {
    				growStructure(block.getLocation(), player);
        			damage(item, player);
    			}
    		}
    	}
    	else if (isPickaxe(item.getType())) {
    		// Pickaxe + Power = instantly break anything (including bedrock - configurable)
    		if (hasEnch(item, POWER, player)) {
    			/* level 1 just breaks one block, but,
                higher powers cut diagonal strip in direction facing
                TODO: cut only in orthogonal directions? or only if in threshold?
                TODO: or like BlastPick? 'clear your path' http://forums.bukkit.org/threads/edit-fun-blastpick-clear-your-path-1-1-rb.7007/ */
    			if (plugin.canBuild(player, block.getLocation(), item.getTypeId(), POWER)) {
    				int level = getLevel(item, POWER);
        			int dx = (int)Math.signum(block.getLocation().getX() - player.getLocation().getX()),
        					dy = (int)Math.signum(block.getLocation().getY() - player.getLocation().getY()),
        					dz = (int)Math.signum(block.getLocation().getZ() - player.getLocation().getZ());
        			for (int i = 0; i < level; i += 1) {
        				if (getConfigBoolean("breakBedrock", false, item, POWER)) {
            				block.getRelative(dx*i, dy*i, dz*i).breakNaturally(item);
            				if (plugin.verboseLogger()) {
            					plugin.getLogger().info(player.getName() + " just broke bedrock using Pickaxe + Power (" + block.getX() + "," + block.getY() + "," + block.getZ() + "," + block.getWorld());
            				}
            			}
            			else {
            				if (!(block.getType() == Material.BEDROCK)) {
            					block.getRelative(dx*i, dy*i, dz*i).breakNaturally(item);
            					plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
            				}
            			}
        			}
    			}
    		}
    	}
    	else if (isAxe(item.getType())) {
    		//Axe + Respiration = Generate Tree
    		if (hasEnch(item, RESPIRATION, player)) {
    			if (plugin.canBuild(player, block.getLocation(), item.getTypeId(), RESPIRATION)) {
    				int n = getLevel(item, RESPIRATION);
        			if (n < 2 || n > 8) {
        				n = random.nextInt(7) + 2;
        			}
        			TreeType type = TreeType.TREE;
        			switch(n) {
        			case 2: type = TreeType.TREE; break;
        			case 3: type = TreeType.BIG_TREE; break;
        			case 4: type = TreeType.REDWOOD; break;
        			case 5: type = TreeType.TALL_REDWOOD; break;
        			case 6: type = TreeType.BIRCH; break;
        			case 7: type = TreeType.RED_MUSHROOM; break;
        			case 8: type = TreeType.BROWN_MUSHROOM; break;
        			}
        			world.generateTree(block.getRelative(BlockFace.UP).getLocation(), type);
        			damage(item, player);
    			}
    		}
    	}
    	else if (item.getType() == Material.FLINT_AND_STEEL) {
    		// Flint & Steel + Fire Protection = player fire resistance (secondary)
			if (hasEnch(item, FIRE_PROTECTION, player)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, getLevel(item, FIRE_PROTECTION)*20*5, 1));
			}
    	}
    	else if (item.getTypeId() == SPAWN_EGG_ID) {
    		if (item.getDurability() == EntityType.ENDER_CRYSTAL.getTypeId()) {
    			if (plugin.canBuild(player, block)) {
    				ItemStack fakeItem = new ItemStack(Material.DIAMOND_PICKAXE, 1); // since configured by item, have to fake it.. (like sublimateIce)
        			boolean shouldSpawnCrystal = getConfigBoolean("spawnEggCrystal", true, fakeItem, SILK_TOUCH);
        			if (shouldSpawnCrystal && plugin.canBuild(player, block)) {
        				block.getWorld().spawn(block.getLocation().add(0, 1, 0), EnderCrystal.class);
        				//Consume egg
        				if (item.getAmount() == 1) {
        					player.setItemInHand(null);
        				}
        				else {
        					player.setItemInHand(new ItemStack(item.getTypeId(), item.getAmount() - 1, (short)EntityType.ENDER_CRYSTAL.getTypeId()));
        				}
        			}
    			}
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    	Entity entity = event.getRightClicked();
    	Player player = event.getPlayer();
    	ItemStack item = player.getItemInHand();
    	if (item == null) {
    		return;
    	}
    	final World world = player.getWorld();
    	
    	// Flint & Steel + Fire Aspect = set mobs on fire
    	if (item.getType() == Material.FLINT_AND_STEEL) {
    		if (hasEnch(item, FIRE_ASPECT, player)) {
    			entity.setFireTicks(getFireTicks(getLevel(item, FIRE_ASPECT)));
    			damage(item, player);
    		}
    		// Flint & Steel + Respiration = smoke inhalation (confusion effect)
    		if (hasEnch(item, RESPIRATION, player)) {
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 0);
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 1);
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 2);
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 3);
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 4);
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 5);
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 6);
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 7);
    			world.playEffect(entity.getLocation(), Effect.SMOKE, 8);
    			world.playEffect(entity.getLocation(), Effect.EXTINGUISH, 0);
    			if (entity instanceof LivingEntity) {
    				((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, getLevel(item, RESPIRATION)*20*5, 1));
    				damage(item, player);
    			}
    		}
    	}
    	else if (item.getType() == Material.SHEARS) {
    		// Shears + Smite = gouge eyes (blindness effect)
    		if (hasEnch(item, SMITE, player)) {
    			if (entity instanceof LivingEntity) {
    				((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, getLevel(item, SMITE)*20*5, 1));
    				damage(item, player);
    			}
    		}
    		// Shears + Bane of Arthropods = collect spider eyes
    		if (hasEnch(item, BANE_OF_ARTHROPODS, player)) {
    			if (entity instanceof CaveSpider || entity instanceof Spider) {
    				Creature bug = (Creature) entity;
    				// If at least 50% health, cut out eyes, then drop health
    				if (bug.getHealth() >= bug.getMaxHealth() / 2) {
    					if (plugin.canDropItem(player, bug.getLocation(), item.getTypeId(), SILK_TOUCH)) {
    						world.dropItemNaturally(bug.getEyeLocation(), new ItemStack(Material.SPIDER_EYE, 1));
    						bug.setHealth(bug.getMaxHealth() / 2 - 1);
    					}
    				}
    				damage(item, player);
    			}
    		}
    		// Shears + Looting = feathers from chicken, leather from cows, saddles from pigs (secondary)
            if (hasEnch(item, LOOTING, player)) {
                if (entity instanceof Chicken) {
                    Creature bird = (Creature)entity;

                    // Pulling feathers damages the creature
                    if (bird.getHealth() >= bird.getMaxHealth() / 2) {
                    	if (plugin.canDropItem(player, bird.getLocation(), item.getTypeId(), SILK_TOUCH)) {
                    		world.dropItemNaturally(entity.getLocation(), new ItemStack(Material.FEATHER, random.nextInt(5) + 1));
                    		// only can drop once (unless healed)
                    		bird.setHealth(bird.getMaxHealth() / 2 - 1);
                    		// There isn't any "featherless chicken" sprite
                    	}

                    }
                    
                    damage(item, player);
                }
                else if (entity instanceof Cow) {
                    Creature bovine = (Creature)entity;
                    if (bovine.getHealth() >= bovine.getMaxHealth() / 2) {
                    	if (plugin.canDropItem(player, bovine.getLocation(), item.getTypeId(), SILK_TOUCH)) {
                    		world.dropItemNaturally(entity.getLocation(), new ItemStack(Material.LEATHER, random.nextInt(5) + 1));
                    		// can drop twice since cows are bigger
                    		bovine.setHealth(bovine.getHealth() - bovine.getMaxHealth() / 3);
                    	}

                    }
                } else if (entity instanceof Pig) {
                    Pig piggy = (Pig)entity;

                    if (piggy.hasSaddle()) {
                    	if (plugin.canDropItem(player, piggy.getLocation(), item.getTypeId(), SILK_TOUCH)) {
                    		world.dropItemNaturally(piggy.getLocation(), new ItemStack(Material.SADDLE, 1));
                    		piggy.setSaddle(false);
                    	}
                    }
                }
            }
        }  else if (isSword(item.getType())) {
            // Sword + Protection = resistance when blocking
            if (hasEnch(item, PROTECTION, player)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getLevel(item, PROTECTION)*20*5, 1));
                damage(item, player);
            }

            // Sword + Silk Touch = capture (right-click to drop entity as item)
            if (hasEnch(item, SILK_TOUCH, player)) {
            	EntityType type = entity.getType();

                if (type == EntityType.UNKNOWN) {
                    // silly Bukkit wrapper, you don't know what this is!
                    // :(
                    // TODO: bypass Bukkit to get modded entity types
                    // would be very useful for mods like Flan's Planes or Hot Air Balloons or AnimalBikes, so can re-acquire glitched items
                    world.playEffect(entity.getLocation(), Effect.EXTINGUISH, 0);
                } else {
                    if (type == EntityType.PLAYER && !getConfigBoolean("capturePlayers", false, item, SILK_TOUCH)) {
                        // Can't ghost out players! 
                        // This has PvP implications, see https://github.com/mushroomhostage/exphc/issues/46
                        // ..but, its an interesting effect, so its configurably allowed - "captured" players can't open chests, etc.
                        world.playEffect(entity.getLocation(), Effect.EXTINGUISH, 0);
                    } else {
                        world.dropItemNaturally(entity.getLocation(), new ItemStack(Material.MONSTER_EGG, 1, type.getTypeId()));
                        entity.remove();
                    }
                }
            }

            // Sword + Punch = knock out of hand (right-click player)
            if (hasEnch(item, PUNCH, player)) {
                if (entity instanceof Player) {
                    Player victim = (Player)entity;
                    int level = getLevel(item, PUNCH);
                    int roll = random.nextInt(getConfigInt("maxLevel", 10, item, PUNCH));
                    int uses = getConfigInt("uses", 30, item, FORTUNE);
                    short durability = item.getDurability();
                    durability /= uses;
                    if (roll <= level) {
                        // Knock item out of hand!
                        ItemStack drop = victim.getItemInHand();
                        if (drop != null && drop.getType() != Material.AIR) {
                        	if (plugin.canDropItem(player, victim.getLocation(), item.getTypeId(), SILK_TOUCH)) {
                        		Location l = new Location(victim.getLocation().getWorld(), victim.getLocation().getX(), victim.getLocation().getY(), victim.getLocation().getZ());
                        		l.add(random.nextInt(getLevel(item, PUNCH) + 1), 0, random.nextInt(getLevel(item, PUNCH) + 1));
                        		world.dropItemNaturally(l, drop); // TODO: bigger variation?
                        		victim.setItemInHand(null);
                        		item.setDurability((short) (item.getDurability() + durability));
                        	}
                        }
                    }
                }
            }

            // Sword + Fortune = pickpocket (right-click player)
            if (hasEnch(item, FORTUNE, player)) {
                if (entity instanceof Player) {
                    Player victim = (Player)entity;
                    int level = getLevel(item, FORTUNE);
                    int roll = random.nextInt(getConfigInt("maxLevel", 10, item, FORTUNE));
                    int uses = getConfigInt("uses", 30, item, FORTUNE);
                    short durability = item.getDurability();
                    durability /= uses;
                    if (roll <= level) {
                        // Pickpocket succeeded!
                        ItemStack[] pockets = victim.getInventory().getContents();
                        if (plugin.canDropItem(player, victim.getLocation(), item.getTypeId(), SILK_TOUCH)) {                        	
                        	for (int i = 0; i < pockets.length; i += 1) {    // TODO: choose random item?
                        		if (pockets[i] != null && pockets[i].getType() != Material.AIR) {
                        			// TODO: only drop one from stack?
                        			Location l = new Location(victim.getLocation().getWorld(), victim.getLocation().getX(), victim.getLocation().getY(), victim.getLocation().getZ());
                            		l.add(random.nextInt(getLevel(item, PUNCH) + 1), 0, random.nextInt(getLevel(item, PUNCH) + 1));
                        			victim.getInventory().setItem(i, null);
                        			ItemStack drop = pockets[i].clone();
                        			world.dropItemNaturally(l, drop);
                        			item.setDurability((short) (item.getDurability() + durability));
                        			break;
                        		}
                        	}
                        }
                    }
                }
            }

            // Sword + Infinity = selective invisibility (right-click player)
            if (hasEnch(item, INFINITY, player)) {
                if (entity instanceof Player) {
                    Player other = (Player)entity;
                    other.hidePlayer(player); // we're invisible to other player
                    player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.DARK_AQUA + "You magically disappear from " + other.getName() + "'s sight!");

                    class ShowPlayerTask implements Runnable {
                        Player player;
                        Player other;

                        public ShowPlayerTask(Player player, Player other) {
                            this.player = player;
                            this.other = other;
                        }

                        public void run() {
                            other.showPlayer(player);
                            player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.DARK_AQUA + "You magically reappear!");
                        }
                    }

                    long lengthTicks = getConfigInt("durationPerLevelTicks", 40, item, INFINITY) * getLevel(item, INFINITY);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new ShowPlayerTask(player, other), lengthTicks);
                    // TODO: cooldown period

                    damage(item, player);
                }
            }

            // Sword + Feather Falling = launch victim (right-click)
            if (hasEnch(item, FEATHER_FALLING, player)) {
                double dy = getConfigDouble("yVelocityPerLevel", 0.5, item, FEATHER_FALLING) * getLevel(item, FEATHER_FALLING);
                entity.setVelocity(new Vector(0, dy, 0));

                damage(item, player);
            }
        }
        else if (isPickaxe(item.getType())) {
            // Pickaxe + Silk Touch III = harvest endercrystal (right-click)
            if (hasEnch(item, SILK_TOUCH, player)) {
                if (getLevel(item, SILK_TOUCH) >= getConfigInt("minLevelCrystal", 3, item, SILK_TOUCH)) {
                    if (entity instanceof EnderCrystal) {
                    	if (plugin.canDropItem(player, entity.getLocation(), item.getTypeId(), SILK_TOUCH)) {                    		
                    		short entityID = (short)entity.getType().getTypeId();
                    		
                    		// drop a very special spawn egg
                    		entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(SPAWN_EGG_ID, 1, entityID));
                    		
                    		entity.remove();
                    		
                    		damage(item, player);
                    		
                    		// cancel explosion
                    		event.setCancelled(true);
                    	}
                    }
                }
            }
        } else if (isHoe(item.getType())) {
            // Hoe + Punch = grow animal
            if (hasEnch(item, PUNCH, player)) {
                if (entity instanceof Animals) {
                    Animals animal = (Animals)entity;
                    if (!animal.isAdult()) {
                        animal.setAdult();
                        
                        try {
                        	Packet63WorldParticles packet = new Packet63WorldParticles();
    						setValue(packet, "a", "heart");
    						setValue(packet, "b", (float) entity.getLocation().getX());
    						setValue(packet, "c", (float) entity.getLocation().getY());
    						setValue(packet, "d", (float) entity.getLocation().getZ());
    						setValue(packet, "e", 0.5f);
    						setValue(packet, "f", 1f);
    						setValue(packet, "g", 0.5f);
    						setValue(packet, "h", 0f);
    						setValue(packet, "i", 20);
    						sendPacketToLocation(animal.getLocation(), packet);
                        } catch (Exception e) {plugin.getLogger().info("Exception creating particle effect. Report to developer (Java:1526)");e.printStackTrace();}
                    }
                }

                damage(item, player);
            }

            // Hoe + Fire Protection = entity sensor (secondary)
            if (hasEnch(item, FIRE_PROTECTION, player)) {
    			player.sendMessage(ChatColor.DARK_AQUA + "-------- EnchantMore Block Information --------");
    			player.sendMessage(ChatColor.DARK_AQUA + "Entity ID: " + ChatColor.AQUA + entity.getEntityId());
    			player.sendMessage(ChatColor.DARK_AQUA + "Type: " + ChatColor.AQUA + capitalise(entity.getType().toString().toLowerCase().replace("_", " ")));
    			player.sendMessage(ChatColor.DARK_AQUA + "Ticks Lived: " + ChatColor.AQUA + entity.getTicksLived());
    			player.sendMessage(ChatColor.DARK_AQUA + "Passenger: " + ChatColor.AQUA + (entity.getPassenger() == null ? "None" : entity.getPassenger()));
    			if (entity instanceof Animals) {
    				Animals animal = (Animals)entity;
    				player.sendMessage(ChatColor.DARK_AQUA + "Age: " + ChatColor.AQUA + (animal.getAgeLock() ? "[Locked] " : "") + animal.getAge());
    				player.sendMessage(ChatColor.DARK_AQUA + "Fertility: " + ChatColor.AQUA + (animal.canBreed() ? "Fertile" : "Infertile"));
    				player.sendMessage(ChatColor.DARK_AQUA + "Maturity: " + ChatColor.AQUA + (animal.isAdult() ? "Adult" : "Baby"));
    			}
    			if (entity instanceof Player) {
    				Player other = (Player)entity;
    				player.sendMessage(ChatColor.DARK_AQUA + "Real Name: " + ChatColor.RESET + other.getDisplayName());
    				player.sendMessage(ChatColor.DARK_AQUA + "Display Name: " + ChatColor.RESET + other.getDisplayName());
    				player.sendMessage(ChatColor.DARK_AQUA + "Experience: " + ChatColor.AQUA + other.getTotalExperience() + "(Level: " + other.getLevel() + ")");
    				player.sendMessage(ChatColor.DARK_AQUA + "Food Level: " + ChatColor.AQUA + other.getFoodLevel());
    				player.sendMessage(ChatColor.DARK_AQUA + "Saturation: " + ChatColor.AQUA + other.getSaturation());
    				player.sendMessage(ChatColor.DARK_AQUA + "Exhaustion: " + ChatColor.AQUA + other.getExhaustion());
    				player.sendMessage(ChatColor.DARK_AQUA + "Bed: " + ChatColor.AQUA + serialiseLocation(other.getBedSpawnLocation()));
    				player.sendMessage(ChatColor.DARK_AQUA + "Compass Target: " + ChatColor.AQUA + serialiseLocation(other.getCompassTarget()));
    			}
                // TODO: more entities

                damage(item, player);
            }

            // Hoe + Knockback = eat dirt (right-click to knock into ground)
            if (hasEnch(item, KNOCKBACK, player)) {
                double dy = getConfigDouble("yPerLevel", 1.0, item, KNOCKBACK) * getLevel(item, KNOCKBACK);
                entity.teleport(entity.getLocation().subtract(0, dy, 0));
                damage(item, player);
            }
    	}
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true) 
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = player.getItemInHand();
        final World world = player.getWorld();
        if (item == null) {
        	return;
        }
        if (isPickaxe(item.getType()) ||
            isShovel(item.getType()) ||
            isAxe(item.getType())) {

            // Pickaxe + Flame = [auto-smelt](http://dev.bukkit.org/server-mods/enchantmore/images/2-pickaxe-shovel-axe-flame-auto-smelt/)
            // Shovel + Flame = [auto-smelt](http://dev.bukkit.org/server-mods/enchantmore/images/2-pickaxe-shovel-axe-flame-auto-smelt/)
            // Axe + Flame = [auto-smelt](http://dev.bukkit.org/server-mods/enchantmore/images/2-pickaxe-shovel-axe-flame-auto-smelt/)
            if (hasEnch(item, FLAME, player)) {
            	if (plugin.canBuild(player, block, item.getTypeId(), FLAME)) {
            		Collection<ItemStack> rawDrops = block.getDrops(item);

                    boolean naturalDrop = true;
                    for (ItemStack rawDrop: rawDrops) {
                        // note: original smelted idea from Firelord tools http://dev.bukkit.org/server-mods/firelord/
                        // also see Superheat plugin? either way, coded this myself..
                        ItemStack smeltedDrop = smelt(rawDrop);

                        if (smeltedDrop != null && smeltedDrop.getType() != Material.AIR) {
                            if (plugin.canDropItem(player, block, item.getTypeId(), FLAME)){
                            	world.dropItemNaturally(block.getLocation(), smeltedDrop);
                            	naturalDrop = false;
                            }
                        } 
                    }
                    if (!naturalDrop) {
                    	event.setCancelled(true);
                        if (plugin.safeSetBlock(player, block, Material.AIR)) {
                        	plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                        }
                    }

                    // no extra damage
            	}
            }
            if (isAxe(item.getType())) {
                // Axe + Power = [fell tree](http://dev.bukkit.org/server-mods/enchantmore/images/3-axe-power-fell-tree/)
                if (hasEnch(item, POWER, player)) {
                	if (plugin.canBuild(player, block, item.getTypeId(), POWER)) {
                		// is it a tree?
                        int id1 = Material.LOG.getId();
                        int id2 = getConfigInt("treeBlockId2", 143, item, POWER);   // RedPower2 Rubberwood
                        int id3 = getConfigInt("treeBlockId3", 243, item, POWER);   // IC2 Rubber Wood

                        if (block.getTypeId() == id1 || block.getTypeId() == id2 || block.getTypeId() == id3) {
                            fellTree(player, block, item, getLevel(item, POWER) * getConfigInt("extraTrunkWidthPerLevel", 1, item, POWER), id1, id2, id3);
                            event.setCancelled(true);
                            // no extra damage
                        }
                	}
                }
            }

            if (isShovel(item.getType())) {
                // Shovel + Power = excavation (dig large area, no drops)
                if (hasEnch(item, POWER, player) && isExcavatable(block.getType())) {
                	if (plugin.canBuild(player, block, item.getTypeId(), POWER)) {
                		// Clear out those annoying veins of gravel (or dirt)

                        // Dig a cube out, but no drops
                        int r = getLevel(item, POWER);

                        Location loc = block.getLocation();
                        int x0 = loc.getBlockX();
                        int y0 = loc.getBlockY();
                        int z0 = loc.getBlockZ();
                      
                        // cube
                        for (int dx = -r; dx <= r; dx += 1) {
                            for (int dy = -r; dy <= r; dy += 1) {
                                for (int dz = -r; dz <= r; dz += 1) {
                                    int x = dx + x0, y = dy + y0, z = dz + z0;

                                    int type = world.getBlockTypeIdAt(x, y, z);
                                    if (isExcavatable(type)) {
                                        Block b = world.getBlockAt(x, y, z);
                                        plugin.safeSetBlock(player, b, Material.AIR);
                                    }
                                }
                            }
                        }

                        // TODO: really would like to clear up all above (contiguous), so nothing falls..

                        event.setCancelled(true);
                        // no extra damage
                	}
                }

                // Shovel + Silk Touch II = harvest fallen snow, fire
                // (fire elsewhere)
                if (hasEnch(item, SILK_TOUCH, player)) {
                	if (plugin.canBuild(player, block, item.getTypeId(), SILK_TOUCH) && plugin.canDropItem(player, block, item.getTypeId(), SILK_TOUCH)) {
                		int minLevel = getConfigInt("minLevel", 2, item, SILK_TOUCH); 
                        if (getLevel(item, SILK_TOUCH) >= minLevel) {
                            if (block.getType() == Material.SNOW) {
                                world.dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), 1));
                                if (plugin.safeSetBlock(player, block, Material.AIR)) {
                                	plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                                }
                                event.setCancelled(true);   // do not drop snowballs
                            }
                        }
                	}
                }
            }
            if (isPickaxe(item.getType())) {
                // Pickaxe + Silk Touch II = harvest ice, double slabs, silverfish blocks
                if (hasEnch(item, SILK_TOUCH, player)) {
                	if (plugin.canBuild(player, block, item.getTypeId(), SILK_TOUCH) && plugin.canDropItem(player, block, item.getTypeId(), SILK_TOUCH)) {
                		int minLevel = getConfigInt("minLevel", 2, item, SILK_TOUCH);
                        if (getLevel(item, SILK_TOUCH) >= minLevel) {
                            if (block.getType() == Material.ICE) {
                                if (getConfigBoolean("harvestIce", true, item, SILK_TOUCH)) {
                                    world.dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), 1));
                                    if (plugin.safeSetBlock(player, block, Material.AIR)) {
                                    	plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                                    }
                                    event.setCancelled(true); 
                                    // no extra damage
                                }
                            } 
                            else if (block.getType() == Material.DOUBLE_STEP) {
                                if (getConfigBoolean("harvestDoubleSlabs", true, item, SILK_TOUCH)) {
                                    ItemStack drop = new ItemStack(block.getType(), 1, (short)block.getData());

                                    // Store data as enchantment level in addition to damage, workaround Bukkit
                                    // We restore this metadata on place
                                    drop.addUnsafeEnchantment(SILK_TOUCH, block.getData());

                                    world.dropItemNaturally(block.getLocation(), drop);
                                    if (plugin.safeSetBlock(player, block, Material.AIR)) {
                                    	plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                                    }
                                    event.setCancelled(true);
                                }
                            } else if (block.getTypeId() == 97) {   // Bukkit Material calls this MONSTER_EGGS, but I'm not going to call it that!
                                if (getConfigBoolean("harvestSilverfishBlocks", true, item, SILK_TOUCH)) {
                                    ItemStack drop = new ItemStack(block.getType(), 1, (short)block.getData());

                                    drop.addUnsafeEnchantment(SILK_TOUCH, block.getData());

                                    world.dropItemNaturally(block.getLocation(), drop);
                                    if (plugin.safeSetBlock(player, block, Material.AIR)) {
                                    	plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                                    }
                                    event.setCancelled(true);
                                }
                            }
                        }
                	}
                    
                    // TODO: how about Silk Touch III = harvest mob spawners? integrate SilkSpawners!
                    // TODO: even better, Silk Touch III = harvest ender crystal :). Spawn egg 200, override placement (optional)..
                    // but it is an entity not a block so handle differently
                }

                // Pickaxe + Looting = deconstruct (reverse crafting)
                if (hasEnch(item, LOOTING, player)) {
                    // partly inspired by Advanced Shears' bookshelves/ladders/jackolatern/stickypiston disassembling
                    // http://forums.bukkit.org/threads/edit-fun-misc-advancedshears-v-1-3-cut-through-more-blocks-and-mobs-953-1060.24746/
                	if (plugin.canDropItem(player, block, item.getTypeId(), LOOTING)) {
                		Collection<ItemStack> finishedDrops = block.getDrops(item);
                		boolean naturalDrop = true;
                		for (ItemStack finishedDrop: finishedDrops) {
                			Collection<ItemStack> componentDrops = uncraft(finishedDrop);
                			if (componentDrops != null) {
                				for (ItemStack drop: componentDrops) {
                					world.dropItemNaturally(block.getLocation(), drop);
                					/*try {
                                    	Packet63WorldParticles packet = new Packet63WorldParticles();
                						setValue(packet, "a", "reddust");
                						setValue(packet, "b", (float) block.getLocation().getX());
                						setValue(packet, "c", (float) block.getLocation().getY());
                						setValue(packet, "d", (float) block.getLocation().getZ());
                						setValue(packet, "e", 0.5f);
                						setValue(packet, "f", 1f);
                						setValue(packet, "g", 0.5f);
                						setValue(packet, "h", 1f);
                						setValue(packet, "i", 100);
                						sendPacketToLocation(block.getLocation(), packet);
                                    } catch (Exception e) {plugin.getLogger().severe("Exception creating particle effect. Report to developer (Java:1751)");e.printStackTrace();}*/
                					naturalDrop = false;
                				}
                			}
                		}
                		
                		if (!naturalDrop) {
                			if (plugin.safeSetBlock(player, block, Material.AIR)) {
                				plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                			}
                			event.setCancelled(true);
                		}
                	}
                }

                // Pickaxe + Sharpness = mine ore vein
                if (hasEnch(item, SHARPNESS, player)) {
                	if (plugin.canBuild(player, block, item.getTypeId(), SHARPNESS)) {
                		int oreId = block.getTypeId();

                        if (oreId == 74) {
                            oreId = 73;
                        }

                        byte oreData = block.getData();

                        boolean defaultValue = false;
                        switch(oreId)
                        {
                        case 14:    // Gold Ore
                        case 15:    // Iron ore
                        case 16:    // Coal Ore
                        case 21:    // Lapis Lazuli Ore
                        case 56:    // Diamond Ore
                        case 73:    // Redstone Ore
                        case 74:    // Glowing Redstone Ore // TODO: fix bug, if only partly glowing won't mine all!
                            defaultValue = true;
                        }

                        if (getConfigBoolean("ores." + oreId + ";" + oreData, defaultValue, item, SHARPNESS)) {
                            int r = getLevel(item, SHARPNESS) * getConfigInt("rangePerLevel", 5, item, SHARPNESS); 
                            int x0 = block.getLocation().getBlockX();
                            int y0 = block.getLocation().getBlockY();
                            int z0 = block.getLocation().getBlockZ();
                          
                            // cube
                            for (int dx = -r; dx <= r; dx += 1) {
                                for (int dy = -r; dy <= r; dy += 1) {
                                    for (int dz = -r; dz <= r; dz += 1) {
                                        int x = dx + x0, y = dy + y0, z = dz + z0;

                                        int type = world.getBlockTypeIdAt(x, y, z);
                                        if (type == oreId) {
                                            Block b = world.getBlockAt(x, y, z);
                                            if (b.getData() == oreData) {
                                                Collection<ItemStack> drops = b.getDrops(item);
                                                if (plugin.safeSetBlock(player, b, Material.AIR)) {
                                                    for (ItemStack drop: drops) {
                                                        // drop all at _central_ location of original block breakage!
                                                        // so this effect can be useful to gather diamonds over dangerous lava
                                                    	if (plugin.canDropItem(player, block, item.getTypeId(), SHARPNESS)) {
                                                    		world.dropItemNaturally(block.getLocation(), drop);
                                                    		plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                                                    	}
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            event.setCancelled(true);
                        }
                	}
                }
            }
        } else if (item.getType() == Material.SHEARS) {
            // Shears + Silk Touch = collect cobweb, dead bush
            if (hasEnch(item, SILK_TOUCH, player)) {
                // Note: you can collect dead bush with shears on 12w05a!
                // http://www.reddit.com/r/Minecraft/comments/pc2rs/just_noticed_dead_bush_can_be_collected_with/
            	if (plugin.canBuild(player, block, item.getTypeId(), SILK_TOUCH)) {
            		if (block.getType() == Material.DEAD_BUSH ||
            				block.getType() == Material.WEB) {
            			
            			world.dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), 1));
            			
            			if (plugin.safeSetBlock(player, block, Material.AIR)) {
            				plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
            			}
            			event.setCancelled(true);
            		} 
            		// no extra damage
            	}
            }

            // Shears + Fortune = apples from leaves
            if (hasEnch(item, FORTUNE, player)) {
                if (block.getType() == Material.LEAVES) {
                	if (plugin.canBuild(player, block, item.getTypeId(), FORTUNE) && plugin.canDropItem(player, block, item.getTypeId(), FORTUNE)) {
                		Material dropType;

                        // TODO: different probabilities, depending on level too (higher, more golden)
                        switch (random.nextInt(10)) {
                        case 0: dropType = Material.GOLDEN_APPLE; break;
                        default: dropType = Material.APPLE;
                        }

                        world.dropItemNaturally(block.getLocation(), new ItemStack(dropType, 1));
                        
                        if (plugin.safeSetBlock(player, block, Material.AIR)) {
                        	plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                        }
                        event.setCancelled(true);
                	}
                }
                // no extra damage
            }

            // Shears + Power = hedge trimmer/builder; cut grass
            // see also secondary effect above
            if (hasEnch(item, POWER, player) && block.getType() == Material.LEAVES) {
            	if (plugin.canBuild(player, block, item.getTypeId(), POWER)) {
            		event.setCancelled(true);
            		hedgeTrimmer(player, block, item, getLevel(item, POWER), item.getTypeId(), POWER);
            		// no extra damage
            	}
            }

        } else if (isHoe(item.getType())) {
            // Hoe + Silk Touch = collect farmland, crop block, pumpkin/melon stem, cake block, sugarcane block, netherwart block (preserving data)
            if (hasEnch(item, SILK_TOUCH, player)) {
            	if (plugin.canBuild(player, block, item.getTypeId(), SILK_TOUCH) && plugin.canDropItem(player, block, item.getTypeId(), SILK_TOUCH)) {
                    // Collect farm-related blocks, preserving the growth/wetness/eaten data
                    if (isFarmBlock(block.getType())) {
                        ItemStack drop = new ItemStack(block.getType(), 1);

                        // Store block data value
                        //drop.setDurability(block.getData());      // bukkit doesn't preserve
                        drop.addUnsafeEnchantment(SILK_TOUCH, block.getData());


                        world.dropItemNaturally(block.getLocation(), drop);
                        
                        if (plugin.safeSetBlock(player, block, Material.AIR)) {
                        	plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                        }
                        event.setCancelled(true);
                    }
                    // no extra damage
            	}
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        World world = block.getWorld();
        Player player = event.getPlayer();
        if (!plugin.canBuild(player, block, "[EnchantMore] Block place event blocked by WorldGuard.")) {
        	return;
        }
        // Item to place as a block
        // NOT event.getItemInHand(), see https://bukkit.atlassian.net/browse/BUKKIT-596 BlockPlaceEvent getItemInHand() loses enchantments
        ItemStack item = player.getItemInHand();

        // Set data of farm-related block
        if (item != null && hasEnch(item, SILK_TOUCH, player)) {
        	if (isFarmBlock(item.getType())) {
                // Make sure we get data from item, not through hasEnch since not player-related
                if (item.containsEnchantment(SILK_TOUCH)) {
                    block.setData((byte)item.getEnchantmentLevel(SILK_TOUCH));
                }
            }
        }

        if (block != null) {
            if (block.getType() == Material.ICE) {
                ItemStack fakeItem = new ItemStack(Material.DIAMOND_PICKAXE, 1); // since configured by item, have to fake it..
                boolean shouldSublimate = getConfigBoolean("sublimateIce", false, fakeItem, SILK_TOUCH);

                if (world.getEnvironment() == World.Environment.NETHER && shouldSublimate) {
                    // sublimate ice to vapor
                    plugin.safeSetBlock(player, block, Material.AIR);

                    // turn into smoke
                    world.playEffect(block.getLocation(), Effect.SMOKE, 0);
                    world.playEffect(block.getLocation(), Effect.SMOKE, 1);
                    world.playEffect(block.getLocation(), Effect.SMOKE, 2);
                    world.playEffect(block.getLocation(), Effect.SMOKE, 3);
                    world.playEffect(block.getLocation(), Effect.SMOKE, 4);
                    world.playEffect(block.getLocation(), Effect.SMOKE, 5);
                    world.playEffect(block.getLocation(), Effect.SMOKE, 6);
                    world.playEffect(block.getLocation(), Effect.SMOKE, 7);
                    world.playEffect(block.getLocation(), Effect.SMOKE, 8);

                    // Workaround type not changing, until fix is in a build:
                    // "Allow plugins to change ID and Data during BlockPlace event." Fixes BUKKIT-674
                    // https://github.com/Bukkit/CraftBukkit/commit/f29b84bf1579cf3af31ea3be6df0bc8917c1de0b

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new EnchantMoreChangeMaterialTask(block, player, Material.AIR, this));
                }
            } else if (block.getType() == Material.DOUBLE_STEP) {
                ItemStack fakeItem = new ItemStack(Material.DIAMOND_PICKAXE, 1); 
                boolean shouldSetData = getConfigBoolean("placeDoubleSlabs", true, fakeItem, SILK_TOUCH);
                if (shouldSetData) {
                    int data = (int)item.getData().getData();
                    // One of the rare cases we get the enchantment level directly.. storing type in ench tag, to workaround Bukkit damage
                    if (item.containsEnchantment(SILK_TOUCH)) {
                        data = item.getEnchantmentLevel(SILK_TOUCH);
                    }

                    block.setData((byte)data);

                    // Oddly, if delay and change, then it will take effect but texture won't be updated. Have to set now ^
                    //Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new EnchantMoreChangeMaterialTask(block, player, Material.DOUBLE_STEP, data, this));
                }
            } else if (block.getTypeId() == 97) {
                // Silverfish blocks, restore data, same as double slabs
                ItemStack fakeItem = new ItemStack(Material.DIAMOND_PICKAXE, 1); 
                boolean shouldSetData = getConfigBoolean("placeSilverfishBlocks", true, fakeItem, SILK_TOUCH);
                if (shouldSetData) {
                    int data = (int)item.getData().getData();
                    if (item.containsEnchantment(SILK_TOUCH)) {
                        data = item.getEnchantmentLevel(SILK_TOUCH);
                    }

                    block.setData((byte)data);
                }

            }
        }
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        ItemStack tool = player.getItemInHand();
        final World world = player.getWorld();

        if (tool == null) {
            return;
        }

        if (!(entity instanceof Sheep)) {
            return;
        }
        // TODO: mooshroom?

        // Shears + Looting = more wool (random colors); feathers from chickens, leather from cows, saddles from saddled pigs
        // see also secondary effect above
        if (tool.getType() == Material.SHEARS && hasEnch(tool, LOOTING, player)) {
            Location loc = entity.getLocation();

            int quantity = random.nextInt(getLevel(tool, LOOTING) * 2);
            for (int i = 0; i < quantity; i += 1) {
                short color = (short)random.nextInt(16);
                if (plugin.canDropItem(player, loc, tool.getTypeId(), LOOTING)) {
                	world.dropItemNaturally(entity.getLocation(), new ItemStack(Material.WOOL, 1, color));
                }
            }
            // no extra damage
        }
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow)entity;
        LivingEntity shooter = arrow.getShooter();
        
        if (shooter == null || !(shooter instanceof Player)) {
            // shot from dispenser, skeleton, etc.
            return;
        }

        Player player = (Player)shooter;
        ItemStack bow = player.getItemInHand();

        if (bow == null || bow.getType() != Material.BOW) {
            return;
        }

        Location dest = arrow.getLocation();
        final World world = dest.getWorld();

        // Arrows carry payloads, did you know that?
        Entity passenger = arrow.getPassenger();
        if (passenger != null) {
            // Bow + Respiration = stapled arrows (attach adjacent item in inventory, e.g. potions, spawn eggs, water, lava, TNT, blocks)
            if (hasEnch(bow, RESPIRATION, player)) {
                if (passenger instanceof Item) {
                    Item item = (Item)passenger;
                    ItemStack itemStack = item.getItemStack();

                    boolean remove = true;


                    for (int i = 0; i < itemStack.getAmount(); i += 1) {
                        if (itemStack.getTypeId() == SPAWN_EGG_ID && getConfigBoolean("allowSpawnEgg", true, bow, RESPIRATION)) {
                            // Spawn Egg = creature
                            int entityId = itemStack.getData().getData();

                            // WARNING: This even spawns enderdragons! Even if Spawn Dragon eggs are blocked 
                            world.spawnEntity(dest, creatureTypeFromId(entityId));
                        } else if (itemStack.getType() == Material.ARROW && getConfigBoolean("allowDoubleArrow", true, bow, RESPIRATION)) {
                            // Arrow

                            // TODO: make the spawned arrow have a useful velocity - none of these attempts
                            // seem to make it do anything but rest and fall to the ground
                            //float n = 10f;     // TODO: get from enchantment level, but would have to enchant arrow on shoot
                            //Vector velocity = new Vector(random.nextFloat() * n, random.nextFloat() * n, random.nextFloat(n));
                            //Vector velocity = arrow.getVelocity().clone();
                            //velocity.multiply(-1);
                            //velocity.setY(-velocity.getY());
                            //velocity.multiply(2);

                            Vector velocity = new Vector(0, 0, 0);
                            float speed = 0.6f;
                            float spread = 12f;
                            world.spawnArrow(dest, velocity, speed, spread);
                        } else if (itemStack.getType() == Material.SNOW_BALL && getConfigBoolean("allowSnowball", true, bow, RESPIRATION)) {
                            world.spawn(dest, Snowball.class);
                        } else if (itemStack.getType() == Material.EGG && getConfigBoolean("allowEgg", true, bow, RESPIRATION)) {
                            world.spawn(dest, Egg.class);
                        } else if (itemStack.getType() == Material.TNT && getConfigBoolean("allowTNT", true, bow, RESPIRATION)) {
                            // TNT, instant ignite from impact
                            TNTPrimed tnt = world.spawn(dest, TNTPrimed.class);
                            tnt.setFuseTicks(0);
                        } else if (itemStack.getType() == Material.WATER_BUCKET && getConfigBoolean("allowWaterBucket", true, bow, RESPIRATION)) {
                            // water bucket, spill and leave empty bucket
                            if (dest.getBlock() == null || dest.getBlock().getType() == Material.AIR) {
                                if (plugin.safeSetBlock(player, dest.getBlock(), Material.WATER)) {
                                	if (plugin.canDropItem(player, dest, bow.getTypeId(), RESPIRATION)) {
                                		world.dropItem(dest, new ItemStack(Material.BUCKET, 1));
                                	}
                                }
                            }
                        } else if (itemStack.getType() == Material.LAVA_BUCKET && getConfigBoolean("allowLavaBucket", true, bow, RESPIRATION)) {
                            // lava bucket, same
                            if (dest.getBlock() == null || dest.getBlock().getType() == Material.AIR) {
                                if (plugin.safeSetBlock(player, dest.getBlock(), Material.LAVA)) {
                                	if (plugin.canDropItem(player, dest, bow.getTypeId(), RESPIRATION)) {
                                		world.dropItem(dest, new ItemStack(Material.BUCKET, 1));    // probably will be destroyed, but whatever
                                	}
                                }
                            }
                        /* this already works - they're blocks!
                        // hacked in water/lava/fire blocks - no drop
                        } else if (itemStack.getType() == Material.WATER) {
                            plugin.safeSetBlock(player, dest.getBlock(), Material.WATER);
                        } else if (itemStack.getType() == Material.LAVA) {
                            plugin.safeSetBlock(player, dest.getBlock(), Material.LAVA);
                        } else if (itemStack.getType() == Material.FIRE) {
                            plugin.safeSetBlock(player, dest.getBlock(), Material.FIRE);
                            */
                        } else if (isSplashPotion(itemStack) && getConfigBoolean("allowSplashPotion", true, bow, RESPIRATION)) {
                            
                            //TODO: replace with potion API in 1.1-R4
                            ThrownPotion pot = (ThrownPotion)world.spawn(dest, ThrownPotion.class);
                            ((ItemStack) pot).setDurability(itemStack.getDurability());
                        } else if (itemStack.getType().isBlock() && getConfigBoolean("allowBlock", true, bow, RESPIRATION)) {
                            // Blocks = build
                            // TODO: better building than straight up vertical columns? build around?
                        	if (plugin.canBuild(player, dest, bow.getTypeId(), RESPIRATION)) {
                        		Block build = dest.getBlock().getRelative(0, i, 0);
                        		
                        		if (build.getType() == Material.AIR) {
                        			build.setType(itemStack.getType());
                        		}
                        	}
                        } else {
                            if (getConfigBoolean("allowItem", true, bow, RESPIRATION)) {
                                passenger.teleport(dest);
                                remove = false; 
                            }
                        }
                    }
                    // Remove item stack entity if it was instantiated into something
                    if (remove) {
                        item.remove();
                    }
                } else {
                    passenger.teleport(dest);
                }
            } 

            // Bow + Silk Touch = magnetic arrows (transport nearby entity) (secondary)
            if (hasEnch(bow, SILK_TOUCH, player)) {
                boolean allow = true;
                
                if (passenger instanceof Projectile) {
                    allow = getConfigBoolean("allowProjectiles", true, bow, SILK_TOUCH);
                } else if (passenger instanceof LivingEntity) {
                    allow = getConfigBoolean("allowLivingEntities", true, bow, SILK_TOUCH);
                }

                if (allow) {
                    passenger.teleport(dest);
                }
            }
        }


        // Bow + Looting = [steal](http://dev.bukkit.org/server-mods/enchantmore/images/6-bow-looting-steal/)
        if (hasEnch(bow, LOOTING, player)) {
            double s = 5.0 * getLevel(bow, LOOTING);

            List<Entity> loots = arrow.getNearbyEntities(s, s, s);
            for (Entity loot: loots) {
                // TODO: different levels, for only items, exp, mobs?
                // This moves everything!
                loot.teleport(player.getLocation());
            }
        }

        // Bow + Smite = strike lightning
        if (hasEnch(bow, SMITE, player)) {
        	if (plugin.canStrikeLightning(player, dest, bow.getTypeId(), SMITE)) {
        		world.strikeLightning(dest);
        	}
        }

        // Bow + Fire Aspect = [firey explosions](http://dev.bukkit.org/server-mods/enchantmore/images/5-bow-fire-aspect-fiery-explosions/)
        if (hasEnch(bow, FIRE_ASPECT, player)) {
            float power = (float)(getConfigDouble("powerPerLevel", 1.0, bow, FIRE_ASPECT)) * getLevel(bow, FIRE_ASPECT);
            if (plugin.canExplode(player, dest, bow.getTypeId(), FIRE_ASPECT)) {
            	world.createExplosion(dest, power, true);
            }
        }

        // Bow + Aqua Affinity = freeze water, stun players
        if (hasEnch(bow, AQUA_AFFINITY, player)) {
        	if (plugin.canBuild(player, dest, bow.getTypeId(), AQUA_AFFINITY)) {
        		//TODO: Doesn't work when it hits the ocean :(
        		int r = getLevel(bow, AQUA_AFFINITY);

                // freeze water 
                int x0 = dest.getBlockX();
                int y0 = dest.getBlockY();
                int z0 = dest.getBlockZ();
               
                int freezeRange = r * getConfigInt("freezeRangePerLevel", 1, bow, AQUA_AFFINITY);
                for (int dx = -freezeRange; dx <= freezeRange; dx += 1) {
                    for (int dy = -freezeRange; dy <= freezeRange; dy += 1) {
                        for (int dz = -freezeRange; dz <= freezeRange; dz += 1) {
                            Block b = world.getBlockAt(dx+x0, dy+y0, dz+z0);
                           
                            if (b.getType() == Material.STATIONARY_WATER || b.getType() == Material.WATER) {
                                b.setType(Material.ICE);
                            }
                        }
                    }
                }
                
                // TODO: only poison hit entity!

                if (plugin.canPVP(player, arrow.getLocation(), bow.getTypeId(), AQUA_AFFINITY)) {
                	double stunRange = r * getConfigDouble("stunRangePerLevel", 1.0, bow, AQUA_AFFINITY);
                	
                	// stun nearby living things
                	List<Entity> victims = arrow.getNearbyEntities(stunRange, stunRange, stunRange);
                	for (Entity victim: victims) {
                		if (victim instanceof LivingEntity) {
                			((LivingEntity)victim).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 
                					r * getConfigInt("stunDurationTicksPerLevel", 20*5, bow, AQUA_AFFINITY),
                					1));
                		}
                	}
                }

                // no extra damage
        	}
        }

        // Bow + Knockback = pierce blocks
        if (hasEnch(bow, KNOCKBACK, player)) {
        	if (plugin.canBuild(player, dest, bow.getTypeId(), KNOCKBACK)) {
        		 class ArrowPierceTask implements Runnable {
                     Arrow arrow;
                     int depth;
                     Player player;

                     public ArrowPierceTask(Player player, Arrow arrow, int depth) {
                         this.arrow = arrow;
                         this.depth = depth;
                         this.player = player;
                     }

                     public void run() {
                         Vector velocity = arrow.getVelocity().clone();  // TODO: unit vector?
                         Block block = arrow.getLocation().getBlock();

                         if (block.getType() == Material.BEDROCK) {
                             return; // bad news
                         }

                         // Pierce block, destroying it
                         block.setType(Material.AIR);
                         plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                         // TODO: should it drop items?
                         
                         // Trace through multiple blocks in same direction, up to enchantment level
                         if (depth > 1) {
                             Vector start = new Vector(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                             BlockIterator it = new BlockIterator(world, start, velocity, 0, depth);
                             while (it.hasNext()) {
                                 Block b = it.next();
                                 if (b.getType() != Material.BEDROCK) {
                                     b.setType(Material.AIR);
                                     plugin.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, player));
                                     // TODO: figure out how to refresh lighting here
                                     //b.setData(b.getDabta(), true);
                                 }
                             }
                         }

                         // if we don't remove, the arrow will fall down, then hit another
                         // block, and another..until it reaches bedrock!
                         arrow.remove();
                     }
                 }

                 Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new ArrowPierceTask(player, arrow, getLevel(bow, KNOCKBACK)));
        	}
        }
        // TODO: fire protection = remove water (like flint & steel aqua affinity)

        // Bow + Bane of Arthropods = poison
        if (hasEnch(bow, BANE_OF_ARTHROPODS, player)) {
        	if (plugin.canPVP(player, arrow.getLocation(), bow.getTypeId(), BANE_OF_ARTHROPODS)) {
        		// TODO: only poison hit entity!
        		
        		// poison nearby living things
        		int r = getLevel(bow, BANE_OF_ARTHROPODS);
        		double poisonRange = r * getConfigDouble("poisonRangePerLevel", 1.0, bow, BANE_OF_ARTHROPODS);
        		List<Entity> victims = arrow.getNearbyEntities(poisonRange, poisonRange, poisonRange);
        		for (Entity victim: victims) {
        			if (victim instanceof LivingEntity) {
        				((LivingEntity)victim).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 
        						r * getConfigInt("poisonDurationTicksPerLevel", 100, bow, BANE_OF_ARTHROPODS),
        						1));
        			}
        		}
        	}

        }

        // Bow + Feather Falling = [teleport](http://dev.bukkit.org/server-mods/enchantmore/images/4-bow-feather-falling-teleport/)
        if (hasEnch(bow, FEATHER_FALLING, player)) {
            arrow.remove();

            // Bow + Feather Falling II = grapple hook (hold Shift to hang on)
            // TODO: should we move the player there slowly, like in in HookShot? (reel in)
            // Grappling hook mod? http://forums.bukkit.org/threads/grappling-hook-mod.8177/
            // [FUN] HookShot v1.3.3 - Scale mountains with a Hookshot [1060] http://forums.bukkit.org/threads/fun-hookshot-v1-3-3-scale-mountains-with-a-hookshot-1060.16494/
            // more complex: "Right-Click arrows to fire a "hook", then right-click whilst holding string to "pull""
            int n = getLevel(bow, FEATHER_FALLING);
            if (n >= getConfigInt("minLevelGrappleHook", 2, bow, FEATHER_FALLING)) {
            	Location l = new Location(dest.getWorld(), dest.getX(), dest.getY(), dest.getZ());
            	l.subtract(0, 2, 0);
                Block below = l.getBlock();
                if (below != null && below.getType() == Material.AIR) {
                    // a ladder to hang on to
                    if (plugin.safeSetBlock(player, below, Material.LADDER)) {
                        // The data isn't set, so the ladder appears invisible - I kinda like that
                        // Player can break it to get a free ladder, but its not a big deal (free sticks, wood, renewable..)

                        //player.setSneaking(true); // only sets appearance, not really if is sneaking - do need to hold shift

                        // Expire the platform after a while, can't hang on forever 
                        long delayTicks = (long)getConfigInt("grappleHangOnTicks", 20 * 10, bow, FEATHER_FALLING);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new EnchantMoreChangeMaterialTask(below, player, Material.AIR, this), delayTicks);
                    }
                }
            }
            /*Location from = player.getEyeLocation();
            double dX = from.getX() - dest.getX(),
            		dY = from.getX() - dest.getX(), 
            		dZ = from.getX() - dest.getX(),
            		yaw = Math.atan2(dZ, dX),
            		pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
            double X = Math.sin(pitch) * Math.cos(yaw),
            		Y = Math.sin(pitch) * Math.sin(yaw),
            		Z = Math.cos(pitch);
            Vector vector = new Vector(X, Y, Z);
            player.setVelocity(vector);
            Snowball sb = player.throwSnowball();
            sb.setPassenger(player);*/
            player.teleport(dest);
        }
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item == null) {
            return;
        }

        PlayerFishEvent.State state = event.getState();
        World world = player.getWorld();
        
        // TODO: Fishing Rod + Feather Falling = reel yourself in
        // see http://dev.bukkit.org/server-mods/dragrod/ - drags caught entities to you
        // and crouch and reel to bring yourself to an entity
        // but can we do it in general? reel into anywhere, not just to an entity, even blocks..
        // see http://forums.bukkit.org/threads/grappling-hook-mod.8177/#post-983920

        if (state == PlayerFishEvent.State.CAUGHT_ENTITY) {
            Entity entity = event.getCaught();

            if (entity == null) {
                return;
            }

            // Fishing Rod + Fire Aspect = set mobs on fire
            if (hasEnch(item, FIRE_ASPECT, player)) {
                entity.setFireTicks(getFireTicks(getLevel(item, FIRE_ASPECT)));
                damage(item, player);
            }
            
            // Fishing Rod + Smite = strike mobs with lightning
            if (hasEnch(item, SMITE, player)) {
            	if (plugin.canStrikeLightning(player, entity.getLocation(), item.getTypeId(), SMITE)) {
            		world.strikeLightning(entity.getLocation());
            		damage(item, player);
            	}
            }

            // Fishing Rod + Sharpness = damage mobs
            if (hasEnch(item, SHARPNESS, player)) {
                if (entity instanceof LivingEntity) {
                    int amount = getLevel(item, SHARPNESS) * getConfigInt("damagePerLevel", 10, item, SHARPNESS);

                    ((LivingEntity)entity).damage(amount, player);
                }
                
                damage(item, player);
            }
        } else if (state == PlayerFishEvent.State.CAUGHT_FISH) {
            // Fishing Rod + Flame = catch cooked fish
            if (hasEnch(item, FLAME, player)) {
            	if (plugin.canDropItem(player, player.getLocation(), item.getTypeId(), FLAME)) {
            		event.setCancelled(true);
            		
            		// replace raw with cooked (TODO: play well with all other enchantments)
            		world.dropItemNaturally(player.getLocation(), new ItemStack(Material.COOKED_FISH, 1));
            	}
            }

            // Fishing Rod + Looting = catch extra fish
            if (hasEnch(item, LOOTING, player)) {
            	if (plugin.canDropItem(player, player.getLocation(), item.getTypeId(), FLAME)) {            		
            		// one extra per level
            		world.dropItemNaturally(player.getLocation(), new ItemStack(Material.RAW_FISH, getLevel(item, FORTUNE)));
            	}
            }

            // Fishing Rod + Fortune = [catch junk](http://dev.bukkit.org/server-mods/enchantmore/images/7-fishing-rod-fortune-catch-sunken-treasure/)
            if (hasEnch(item, FORTUNE, player)) {
            	if (plugin.canDropItem(player, player.getLocation(), item.getTypeId(), FORTUNE)) {
            		int quantity  = getLevel(item, FORTUNE);
            		
            		Material m;
            		
            		// TODO: configurable, like Junkyard Creek http://dev.bukkit.org/server-mods/junkyardcreek/
            		switch(random.nextInt(19)) {
            		case 0: m = Material.MONSTER_EGGS; break;       // hidden silverfish block
            		case 1:
            		default:
            		case 2: m = Material.DIRT; break;
            		case 3: 
            		case 4: m = Material.WOOD; break;
            		case 5: m = Material.SPONGE; break;
            		case 6: m = Material.DEAD_BUSH; break;
            		case 7: m = Material.EYE_OF_ENDER; break;
            		case 8: m = Material.DIAMOND; break;
            		case 9:
            		case 10:
            		case 11: m = Material.IRON_INGOT; break;
            		case 12:
            		case 13: m = Material.GOLD_INGOT; break;
            		case 14: m = Material.CHAINMAIL_CHESTPLATE; break;
            		case 15: 
            		case 16: m = Material.WATER_BUCKET; break;
            		case 17: m = Material.BOAT; break;
            		case 18: m = Material.SLIME_BALL; break;
            		case 19: m = Material.FERMENTED_SPIDER_EYE; break;
            		}
            		
            		world.dropItemNaturally(player.getLocation(), new ItemStack(m, quantity));
            		
            		// TODO: should also cancel fish event as to not drop?
            	}
            }

            // no extra damage 

        } else if (state == PlayerFishEvent.State.FAILED_ATTEMPT) {
            // Fishing Rod + Silk Touch = catch more reliably
            if (hasEnch(item, SILK_TOUCH, player)) {
            	if (plugin.canDropItem(player, player.getLocation(), item.getTypeId(), SILK_TOUCH)) {
            		// probability
            		// TODO: configurable levels, maybe to 100?
            		// 4 = always
            		int n = 4 - getLevel(item, SILK_TOUCH);
            		if (n < 1) {
            			n = 1;
            		}
            		
            		if (random.nextInt(n) == 0) {
            			// TODO: integrate with Flame to catch cooked, too
            			world.dropItemNaturally(player.getLocation(), new ItemStack(Material.RAW_FISH, 1));
            		}
            	}
            }

            // no extra damage
        } else if (state == PlayerFishEvent.State.FISHING) {
            // Fishing Rod + Efficiency = fish faster
            if (hasEnch(item, EFFICIENCY, player)) {
               
                // 13 seconds for level 1, down to 1 for level 7
                int delayTicks = (15 - getLevel(item, EFFICIENCY) * 2 - random.nextInt(4)) * 20;
                if (delayTicks < 0) {
                    delayTicks = 0;
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new EnchantMoreFishTask(player, world), delayTicks);

                // TODO: cancel task if stop fishing (change state)
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true) 
    public void onEntityShootBow(EntityShootBowEvent event) {
        ItemStack bow = event.getBow();
        if (bow == null) {
            // shot by skeleton, they can't have enchanted bows 
            return;
        }

        Entity projectile = event.getProjectile();
        if (!(projectile instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow)projectile;
        LivingEntity shooter = arrow.getShooter();
        if (shooter == null) {
            // can be null if "shot from dispenser"
            return;
        }
        if (!(shooter instanceof Player)) {
            return;
        }
        Player player = (Player)shooter;

        // Bow + Sharpness = increase velocity
        if (hasEnch(bow, SHARPNESS, player)) {
            double factor = getConfigDouble("velocityFactorPerLevel", 2.0, bow, SHARPNESS) * getLevel(bow, SHARPNESS);

            // TODO: instead of scalar multiplication, therefore also multiplying the 'shooting inaccuracy'
            // offset, should we instead try to straighten out the alignment vector?
            projectile.setVelocity(projectile.getVelocity().multiply(factor));

            event.setProjectile(projectile);
        }

        // Bow + Respiration = stapled arrows (secondary) (see above)
        if (hasEnch(bow, RESPIRATION, player)) {
            World world = player.getWorld();
            PlayerInventory inventory = player.getInventory();
            int arrowSlot = inventory.first(Material.ARROW);

            if (arrowSlot != -1) {
                int payloadSlot = arrowSlot + 1;
                ItemStack payloadStack = inventory.getItem(payloadSlot);
                if (payloadStack != null && payloadStack.getType() != Material.AIR) {
                    // Take item(s) TODO: use splitStacks method somewhere
                    int n = getLevel(bow, RESPIRATION);
                    ItemStack part = payloadStack.clone();
                    if (payloadStack.getAmount() <= n) {
                        inventory.clear(payloadSlot);
                    } else {
                        payloadStack.setAmount(payloadStack.getAmount() - n);
                        inventory.setItem(payloadSlot, payloadStack);
                        part.setAmount(n);
                    }

                    // Attach the payload
                    // We can't make an entity without spawning in the world, so start it over the player's head,
                    // also has the pro/con they'll get the item back if it doesn't land in time.. but they may
                    // notice it if they look up!
                    Location start = arrow.getLocation().add(0,10,0);

                    // Starts out life as an item..attached to the arrow! Cool you can do this
                    Item payload = world.dropItem(start, part);
                    arrow.setPassenger(payload);
                }
            }
        }

        // Bow + Silk Touch = magnetic arrows (transport nearby entity)
        if (hasEnch(bow, SILK_TOUCH, player)) {
            double range = 10.0 * getLevel(bow, SILK_TOUCH);
            List<Entity> nearby = player.getNearbyEntities(range, range, range);

            if (nearby.size() != 0) {
                Entity entity = nearby.get(0);   // TODO: random?

                arrow.setPassenger(entity);
            }
        }
    }
    public Block getArrowHit(Arrow arrow) {
        World world = arrow.getWorld();

        net.minecraft.server.v1_6_R3.EntityArrow entityArrow = ((CraftArrow)arrow).getHandle();

        try {
            // saved to NBT tag as xTile,yTile,zTile
            Field fieldX = net.minecraft.server.v1_6_R3.EntityArrow.class.getDeclaredField("d");
            Field fieldY = net.minecraft.server.v1_6_R3.EntityArrow.class.getDeclaredField("e");
            Field fieldZ = net.minecraft.server.v1_6_R3.EntityArrow.class.getDeclaredField("f");

            fieldX.setAccessible(true);
            fieldY.setAccessible(true);
            fieldZ.setAccessible(true);

            int x = fieldX.getInt(entityArrow);
            int y = fieldY.getInt(entityArrow);
            int z = fieldZ.getInt(entityArrow);

            return world.getBlockAt(x, y, z);
        } catch (Exception e) {
            plugin.getLogger().info("getArrowHit("+arrow+" reflection failed: "+e);
            throw new IllegalArgumentException(e);
        }
    }
    public int getArrowFromPlayer(Arrow arrow) {
    	return (((CraftArrow) arrow).getHandle()).fromPlayer;
    }
    public void setArrowFromPlayer(Arrow arrow, int fromPlayer) {
    	(((CraftArrow) arrow).getHandle()).fromPlayer = fromPlayer;
    }
	private void onPlayerDamaged(Player player, EntityDamageEvent event) { //Player taking damage
    	ItemStack chestplate = player.getInventory().getChestplate();
    	if ((chestplate != null) && (chestplate.getType() != Material.AIR)) {
    		//Chestplate + Infinity = godmode
    		if (hasEnch(chestplate, INFINITY, player)) {
    			damage(chestplate, event.getDamage() / 2, player);
				player.getInventory().setChestplate((chestplate.getDurability() > chestplate.getType().getMaxDurability() ? null : chestplate));
				event.setCancelled(true);
    		}
    		//Chestplate + Fish Mode = No damage from drowning
    		if (hasEnch(chestplate, RESPIRATION, player)) {
    			Block blockIn = player.getEyeLocation().getBlock(); //Now checks if player is actually swimming, rather than just standing in the water.
    			if (blockIn.getType() == Material.STATIONARY_WATER || blockIn.getType() == Material.WATER) {
    				if (event.getCause() == DamageCause.DROWNING) { //Check if the damage is actually from drowning
    					damage(chestplate, event.getDamage() / 2, player);
    					player.getInventory().setChestplate((chestplate.getDurability() > chestplate.getType().getMaxDurability() ? null : chestplate));
    					event.setCancelled(true);
    				}
    			}
    		}
    	}
    	
    	EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause == EntityDamageEvent.DamageCause.LAVA ||
            cause == EntityDamageEvent.DamageCause.FIRE ||
            cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            ItemStack helmet = player.getInventory().getHelmet();
            // Helmet + Fire Aspect = swim in lava
            if (helmet != null && helmet.getType() != Material.AIR && hasEnch(helmet, FIRE_ASPECT, player)) {
                event.setCancelled(true);   // stop knockback and damage
                //event.setDamage(0);
                player.setFireTicks(0);     // cool off immediately after exiting lava

                // TODO: can we display air meter under lava? 
                /*
                playerDamaged.setMaximumAir(20*10);
                playerDamaged.setRemainingAir(20*10);
                */

                // similar: http://dev.bukkit.org/server-mods/goldenchant/
                // "golden chestplate = immunity to fire and lava damage" [like my Helmet with Fire Aspect]
                // "golden helmet = breath underwater" [seems to overlap with Respiration, meh]
                // "golden shoes = no fall damage" [ditto for Feather Falling]
            }
        } else if (cause == EntityDamageEvent.DamageCause.CONTACT) {
            // Chestplate + Silk Touch = cactus protection (no contact damage)
            if (chestplate != null && chestplate.getType() != Material.AIR && hasEnch(chestplate, SILK_TOUCH, player)) {
                event.setCancelled(true);
            }
        } else if (cause == EntityDamageEvent.DamageCause.FALL) {
            ItemStack boots = player.getInventory().getBoots();

            if (boots != null && boots.getType() != Material.AIR) {
                // TODO: Boots + Knockback = bounce
                /*if (hasEnch(boots, KNOCKBACK, player)) {
                    event.setCancelled(true);
                    if (!player.isSneaking()) {  // interferes with always-sneak
                        double amount = event.getDamage();   // proportional to height
                        // This needs to be a damped oscillation
                        double n = getLevel(boots, KNOCKBACK) * 2.5f;
                        player.setVelocity(player.getVelocity().setY(n));
                        // see also MorePhysics bouncing blocks
                    }
                }*/
            	
                // Boots + Feather Falling X = zero fall damage
                if (hasEnch(boots, FEATHER_FALLING, player)) {
                    if (getLevel(boots, FEATHER_FALLING) >= getConfigInt("minLevel", 10, boots, FEATHER_FALLING)) {
                        event.setCancelled(true);
                    }
                }
            }
        }

        if (event instanceof EntityDamageByEntityEvent) {    // note: do not register directly
            EntityDamageByEntityEvent e2 = (EntityDamageByEntityEvent)event;
            Entity damager = e2.getDamager();

            if (chestplate != null && chestplate.getType() != Material.AIR) {
                // Chestplate + Sharpness = reflect damage 
                if (hasEnch(chestplate, SHARPNESS, player)) {
                    if (damager instanceof LivingEntity) {
                        double amount = getLevel(chestplate, SHARPNESS) * event.getDamage();
                        ((LivingEntity)damager).damage(amount);
                        event.setCancelled(true);

                        // TODO: damage chestplate still?
                    }
                }

                // Chestplate + Knockback = reflect arrows
                if (hasEnch(chestplate, KNOCKBACK, player)) {
                    if (damager instanceof Arrow) { // TODO: all projectiles?
                        Arrow arrow = (Arrow)damager;


                        event.setCancelled(true);   // stop arrow damage
                        Arrow newArrow = player.launchProjectile(Arrow.class);        // reflect arrow

                        // preserve from-player-ness, to prevent duping from skeleton arrows
                        setArrowFromPlayer(newArrow, getArrowFromPlayer(arrow));

                        damage(chestplate, player);
                    }
                }
                // TODO: Sword + Projectile Protection = reflect arrows while blocking
                // make it as ^^ is, nerf above (sword direction control, chestplate not)
            }
        }
    }
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true) 
    public void onEntityDamage(EntityDamageEvent event) {

        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            onPlayerDamaged((Player)entity, event);
        } 

        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent)event).getDamager();
            if (damager instanceof Player) {
                Player damagerPlayer = (Player)damager;

                ItemStack weapon = damagerPlayer.getInventory().getItemInHand();

                onPlayerAttack(damagerPlayer, weapon, entity, (EntityDamageByEntityEvent)event);
            }
        }
    }

    // Player causing damage, attacking another entity
    private void onPlayerAttack(Player attacker, ItemStack weapon, Entity entity, EntityDamageByEntityEvent event) {
        if (weapon != null) {
            if (isAxe(weapon.getType())) {
                // Axe + Aqua Affinity = slowness effect
                if (hasEnch(weapon, AQUA_AFFINITY, attacker)) {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 
                            getLevel(weapon, AQUA_AFFINITY) * getConfigInt("durationTicksPerLevel", 20*5, weapon, AQUA_AFFINITY),
                            getConfigInt("amplifier", 1, weapon, AQUA_AFFINITY)));
                        // see also: SLOW_DIGGING, WEAKNESS - TODO: can we apply all three?
                    }
                }
            }

            // Sword + Respiration = banhammer (1=kick, 2+=temp ban)
            if (isSword(weapon.getType())) {
                if (hasEnch(weapon, RESPIRATION, attacker)) {
                    if (entity instanceof Player) {
                        int n = getLevel(weapon, RESPIRATION);

                        if (n >= getConfigInt("banLevel", 2, weapon, RESPIRATION)) {
                            // its a real banhammer! like http://forums.bukkit.org/threads/admn-banhammer-v1-2-ban-and-kick-by-hitting-a-player-1060.32360/
                            String banCommand = getConfigString("banCommand", "ban %s", weapon, RESPIRATION).replace("%s", ((Player)entity).getName());
                            Bukkit.getServer().dispatchCommand(attacker, banCommand);

                            // temporary ban (TODO: show how much time left on reconnect?)
                            String pardonCommand = getConfigString("pardonCommand", "pardon %s", weapon, RESPIRATION).replace("%s", ((Player)entity).getName());

                            class BanhammerPardonTask implements Runnable {
                                String command;
                                Player sender;

                                public BanhammerPardonTask(String command, Player sender) {
                                    this.command = command;
                                    this.sender = sender;
                                }

                                public void run() {
                                    Bukkit.getServer().dispatchCommand(sender, command);
                                }
                            }

                            long banTicks = getConfigInt("banTicksPerLevel", 200, weapon, RESPIRATION) * getLevel(weapon, RESPIRATION);

                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new BanhammerPardonTask(pardonCommand, attacker), banTicks);
                        } else if (n >= getConfigInt("kickLevel", 1, weapon, RESPIRATION)) {
                            String message = getConfigString("kickMessage", "Kicked by a Sword with a Respiration Enchantment by %s", weapon, RESPIRATION).replace("%s", attacker.getDisplayName());
                            ((Player)entity).kickPlayer(message);
                        }
                    }
                }
            }

            // TODO: Sword + Efficiency = sudden death
            // disabled for now since doesn't work on enderdragon, where it would be most useful!
            /*
            if (hasEnch(weapon, INFINITE, attacker)) {
                plugin.log.info("infinity sword! on "+entity);
                if (entity instanceof LivingEntity) {
                    plugin.log.info("KILL");
                    ((LivingEntity)entity).setHealth(0);
                    ((LivingEntity)entity).damage(Integer.MAX_VALUE, attacker);


                    // Not even called when damaging enderdragon? says fixed in 1.1-R4..
                    // https://bukkit.atlassian.net/browse/BUKKIT-129
                    
                    if (entity instanceof ComplexLivingEntity) {
                        // just to be sure..
                        Set<ComplexEntityPart> parts = ((ComplexLivingEntity)entity).getParts();
                        for (ComplexEntityPart part: parts) {
                            part.remove();
                        }
                    }

                    entity.remove();
                }
            }
            */
        }

        ItemStack chestplate = attacker.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() != Material.AIR) {
            // 1 = brass knuckles (more damage with fists)
            if (hasEnch(chestplate, PUNCH, attacker)) {
                if (weapon == null || weapon.getType() == Material.AIR) {
                    if (entity instanceof LivingEntity) {
                        int amount = getConfigInt("damagePerLevel", 5, chestplate, PUNCH) * getLevel(chestplate, PUNCH);
                        ((LivingEntity)entity).damage(amount, null /*attacker - not passed so doesn't recurse*/);
                    }
                }
            }
        }

        ItemStack leggings = attacker.getInventory().getLeggings();
        if (leggings != null && leggings.getType() != Material.AIR) {
            // Leggings + Knockback = tackle (more damage when sprinting)
            if (hasEnch(leggings, KNOCKBACK, attacker)) {
                if (attacker.isSprinting()) {
                    // TODO: multiplier, with current weapon?
                    int amount = getConfigInt("damagePerLevel", 5, leggings, KNOCKBACK) * getLevel(leggings, KNOCKBACK);
                    ((LivingEntity)entity).damage(amount, null /*attacker - not passed so doesn't recurse*/);
                }
            }
        }

        // TODO: Leggings + Efficiency = ascend/descend ladders faster..but how? teleport? and where?
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        // TODO: WorldGuard

        if (!event.isSneaking()) {
            return;
        }

        Player player = event.getPlayer();

        // Pressed shift, count number of times pressed
        EnchantMoreTapShiftTask.bumpSneakCount(player);


        ItemStack boots = player.getInventory().getBoots();
        ItemStack leggings = player.getInventory().getLeggings();

        if (leggings != null && leggings.getType() != Material.AIR) {
            // Leggings + Punch = rocket launch pants (double-tap shift)
            if (hasEnch(leggings, PUNCH, player) && EnchantMoreTapShiftTask.isDoubleTapShift(player)) {
                int n = getLevel(leggings, PUNCH);
                if (n > 6) {
            		player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Item use restricted to below Enchantment Level 6.");
            		return;
            	}
                Location loc = player.getLocation();

                Block blockOn = loc.getBlock().getRelative(BlockFace.DOWN);
                
                // Only launch if on solid block
                if (blockOn.getType() != Material.AIR && !blockOn.isLiquid()) {
                    player.setVelocity(loc.getDirection().normalize().multiply(n * 2.5f));   // TODO: configurable factor
                }
            }

            // Leggings + Feather Falling = surface (triple-tap shift)
            if (hasEnch(leggings, FEATHER_FALLING, player) && EnchantMoreTapShiftTask.isTripleTapShift(player)) {
                // TODO: this only gets highest non-transparent :( - can get stuck in glass 
                // but, it does work in water! useful in caves or when swimming
                Block top = player.getWorld().getHighestBlockAt(player.getLocation());

                //player.getLocation().setY(top.getY()); // no change
                // Only go up, not down (may be flying from other enchantment, or above transparent blocks)
                if (top.getLocation().getY() > player.getLocation().getY()) {
                    player.teleport(top.getLocation()); // resets direction facing, which I don't like
                }

                // TODO: nether.. gets stuck on top :(
                // TODO: if already on top, go down! and travel through levels.
            }
        } 

        if (boots != null && boots.getType() != Material.AIR) {
            // Boots + Punch = hover jump (double-tap shift)
            if (hasEnch(boots, PUNCH, player) && EnchantMoreTapShiftTask.isDoubleTapShift(player)) {
            	if (getLevel(boots, PUNCH) > 10) {
            		player.sendMessage(ChatColor.GOLD + "[EnchantMore] " + ChatColor.RED + "Item use restricted to below Enchantment Level 10.");
            		return;
            	}
                double y = getConfigDouble("yVelocityPerLevel", 1.0, boots, PUNCH) * getLevel(boots, PUNCH);
                player.setVelocity(player.getVelocity().setY(y));
            }
        }

        // Reset count so can sneak and sneak again later - must double-tap rapidly to activate
        // TODO: only bump/schedule this if above enchantments are enabled
        EnchantMoreTapShiftTask.scheduleTimeout(player, this);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityExplode(EntityExplodeEvent event) {
        // TODO: WorldGuard

        Entity entity = event.getEntity();

        if (!(entity instanceof Creeper)) {
            return;
        }

        Location blastLocation = entity.getLocation();

        World world = entity.getWorld();
        List<Player> players = world.getPlayers();

        // Check nearby player inventories
        for (Player player: players) {
            if (!player.getWorld().equals(world)) {
                continue;
            }

            PlayerInventory inventory = player.getInventory();
            ItemStack[] contents = inventory.getContents();
            for (int i = 0; i < contents.length; i += 1) {
                ItemStack item = contents[i];
                if (item != null && item.getType() == Material.FLINT_AND_STEEL) {
                    if (hasEnch(item, BLAST_PROTECTION, player)) {
                        double range = getLevel(item, BLAST_PROTECTION) * 10.0;

                        // Flint & Steel + Blast Protection = anti-creeper (cancel nearby explosion)
                        Location loc = player.getLocation();

                        double d2 = loc.distanceSquared(blastLocation);
                        //plugin.log.info("d2="+d2);
                        if (d2 < range) {
                            //plugin.log.info("cancel "+range);
                            event.setCancelled(true);

                            world.playEffect(blastLocation, Effect.SMOKE, 0);
                            world.playEffect(blastLocation, Effect.SMOKE, 1);
                            world.playEffect(blastLocation, Effect.SMOKE, 2);
                            world.playEffect(blastLocation, Effect.SMOKE, 3);
                            world.playEffect(blastLocation, Effect.SMOKE, 4);
                            world.playEffect(blastLocation, Effect.SMOKE, 5);
                            world.playEffect(blastLocation, Effect.SMOKE, 6);
                            world.playEffect(blastLocation, Effect.SMOKE, 7);
                            world.playEffect(blastLocation, Effect.SMOKE, 8);
                            return;
                        }
                    }
                }
            }
        }

        // TODO: also cancel blast if nearby chests/dispensers/furnaces have this item!! like CMA dirty bombs, but the opposite
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityCombust(EntityCombustEvent event) {
        Entity entity = event.getEntity();
        // TODO: WorldGuard

        // TODO: attempt to cancel burning when swimming in lava - no effect
        /*
            if (entity instanceof Player) {
                Player player = (Player)entity;

                ItemStack helmet = player.getInventory().getHelmet();
                if (helmet != null && hasEnch(helmet, FIRE_ASPECT, player)) {
                    event.setCancelled(true);
                }
            }
        }*/

        if (!(entity instanceof Item)) {
            return;
        }

        Item item = (Item)entity;
        ItemStack itemStack = item.getItemStack();

        if (itemStack != null && isSword(itemStack.getType())) {
            // Sword + Fire Protection = return to player when dropped in lava
            if (hasEnch(itemStack, FIRE_PROTECTION, null)) {    // no player.. TODO: find nearest player, check if has permission
                event.setCancelled(true);

                double range = 10.0 * getLevel(itemStack, FIRE_PROTECTION); // TODO: same, find player instead of using null

                List<Entity> dests = item.getNearbyEntities(range, range, range);
                for (Entity dest: dests) {
                    if (!(dest instanceof Player)) { // or LivingEntity? for fun :)
                        continue;
                    }
                    entity.teleport(dest.getLocation());
                    break;
                }
                // TODO: if no one nearby, teleport randomly? in case dies..
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled=true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // TODO: WorldGuard?

        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;

        ItemStack chestplate = player.getInventory().getChestplate();
        // Chestplate + Infinity = no hunger (secondary)
        if (chestplate != null && chestplate.getType() != Material.AIR && hasEnch(chestplate, INFINITY, player)) {
            event.setFoodLevel(20); // max
            // not cancelled, so still can eat
        }
    }
}
