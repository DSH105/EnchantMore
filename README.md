EnchantMore - enchanted hoes, enchanted fishing rods, enchanted shears and more!

EnchantMore adds dozens of new item enchantment effects,
on flint & steel, hoes, shears, fishing rods, and other items --
ranging from entertainingly destructive to legitimately useful.

***[Download EnchantMore 2.0](http://dev.bukkit.org/server-mods/enchantmore/files/12-enchant-more-2-0/)*** - released 2013/04/19 for 1.5.1-R0.2

## Features
* More than 80 new enchantment/item combinations
* Items in inventory appear as expected (glowing + descriptive tooltip) 
* No client mods required
* Only adds functionality, does not change any -- all your existing enchanted tools work as expected
* You can disable any effects in config.yml
* Supports enchantments on modded items

**Important**: to enchant items, you currently need to use an enchanting plugin or inventory editor. EnchantMore does not yet enchant items itself or alter enchantment tables. Known plugins compatible with EnchantMore:

* [EasyEnchant](http://dev.bukkit.org/server-mods/easyenchant/) - see the Exception section in the config to enable/disable
* [Tim The Enchanter](http://dev.bukkit.org/server-mods/enchanter/) - /enchant command for ops
* [AutoEnchanter](http://dev.bukkit.org/server-mods/autoenchanter/) - set "unsafe" to true in config 
* [General](http://dev.bukkit.org/server-mods/general/) - supported in v4.3+, enable in config

If you know of any other compatible plugins, let me know and I'll add them here. (If you're writing an enchanting plugin, use addUnsafeEnchantment() and 
ignore canEnchantItem(), then it should be compatible with EnchantMore.)

## Effects List
*[Vote for your favorites!](http://dev.bukkit.org/server-mods/enchantmore/polls/what-are-your-favorite-enchantments-in-enchant-more/)*

* **Axe + Aqua Affinity**: Activates a slowness potion on the player that is attacked
* **Axe + Flame**: Auto-Smelt - Automatically smelts broken blocks without the need of a furnace. For example, it will smelt a wood log into charcoal.
* **Axe + Power**: Fell tree - Based on the enchantment level, all wood logs within the radius of a broken log will be removed and items dropped. Can fell a tree in one hit!
* **Axe + Respiration**: Grows a plant on right-click, if the interacted block is dirt (or mycelium for mushrooms). The higher the level, the greater chance of being able to grow a mushroom.
* **Boots + Feather Falling X**: Zero Fall Damage - All fall damage is cancelled while wearing these boots
* **Boots + Flame**: Firewalker - Ignites the ground behind the wearer, without igniting them. This means that the fire is not in a straight line and is more destructive!
* **Boots + Power**: Witch's broom - When sprint is enabled, the wearer is able to fly up into the sky and soar above the ground. Be careful of fall damage!
* **Boots + Punch**: Hover Jump - Double tapping shift will launch the player upwards into the sky at a velocity that depends on the enchantment level
* **Bow + Aqua Affinity**: Freeze water - If the arrow lands near water, the water will freeze and stuns (adds a slowness potion) to any entity in its vicinity.
* **Bow + Bane of Arthropods**: Poison Bow - Arrows activate a poison potion effect on any entity that it hits.
* **Bow + Efficiency**: Instant shoot - Right-clicking on a block shoots an arrow at full velocity without the need for drawing back. However, it does not work when interacting with air (this cannot be fixed).
* **Bow + Feather Falling**: EnderBow - teleports the user to its arrows destination. Feather Falling II unlocks the grapple hook, which places an invisible ladder at the teleport destination (use Shift to hold on).
* **Bow + Fire Aspect**: Arrows explode on impact.
* **Bow + Knockback**: Pierce blocks - Removes a set of blocks at the arrow's destination without drops.
* **Bow + Looting**: Steal - Moves all entities near the arrow impact location to the player, even players!
* **Bow + Respiration**: Stapled arrows - When shot, the arrow will attach an item that is adjacent in the player's inventory to itself.
* **Bow + Sharpness**: Arrows have increased velocity
* **Bow + Silk Touch**: Magnetic arrowsAttaches an entity near the player onto the arrow, similar to Bow + Respiration.
* **Bow + Smite**: Strikes lightning where its arrows land
* **Chestplate + Infinity**: God mode (Wearer takes no damage)
* **Chestplate + Knockback**: Reflect arrows
* **Chestplate + Punch**: Brass knuckles (more damage with fists)
* **Chestplate + Respiration**: Fish mode (no damage in water)
* **Chestplate + Sharpness**: Reflect all damage to the player that dealt it.
* **Chestplate + Silk Touch**: Cactus protection (no contact damage)
* **Fishing Rod + Efficiency**: Fish faster
* **Fishing Rod + Fire Aspect**: Set mobs on fire when they are hooked
* **Fishing Rod + Flame**: Catch cooked fish
* **Fishing Rod + Fortune**: Catch junk. 15 different items can be caught using this fishing rod.
* **Fishing Rod + Looting**: Catch extra fish
* **Fishing Rod + Sharpness**: Damage hooked mobs
* **Fishing Rod + Silk Touch**: Catch more reliably
* **Fishing Rod + Smite**: Strike hooked mobs with lightning
* **Flint & Steel + Aqua Affinity**: Vaporise water in the vicinity
* **Flint & Steel + Blast Protection**: Anti-creeper (cancel nearby explosion)
* **Flint & Steel + Efficiency**: Burn faster (turn wood to leaves)
* **Flint & Steel + Fire Aspect**: Set mobs on fire
* **Flint & Steel + Fire Protection**: Activates a fire resistance potion effect
* **Flint & Steel + Punch**: Cannon (launches a Primed TNT into the air)
* **Flint & Steel + Respiration**: Smoke inhalation (applies a confusion effect to the entity right-clicked)
* **Flint & Steel + Sharpness**: Creates an explosion on right-click
* **Flint & Steel + Silk Touch**: Remote detonation (Ignites a TNT)
* **Flint & Steel + Smite**: Strike lightning
* **Helmet + Fire Aspect**: Swim in lava without taking damage
* **Hoe + Aqua Affinity**: Auto-hydrate (Adds water to crops)
* **Hoe + Bane of Arthropods**: Weather alteration (left-click storm, right-click sun)
* **Hoe + Efficiency**: Till larger area
* **Hoe + Fire Protection**: Block and Entity information sensor
* **Hoe + Fortune**: When tilled, dirt has a chance to drop seeds
* **Hoe + Knockback**: Pushes an entity into the ground when they are right-clicked (Eat dirt!)
* **Hoe + Power**: Time control (Depending on the efficiency of the hoe, time is moved forward a certain amount)
* **Hoe + Punch**: Transform a baby into an adult
* **Hoe + Respiration**: Grow any plant as bone meal allows
* **Hoe + Silk Touch**: Harvest farmland, crop block, pumpkin/melon stem, cake block, sugarcane block, netherwart block (preserving data)
* **Leggings + Feather Falling**: Surface (triple-tap shift to teleport to the highest block at current location)
* **Leggings + Knockback**: Tackle (more damage when sprinting)
* **Leggings + Punch**: Rocket launch pants (double-tap shift to launch in the direction of view)
* **Pickaxe + Flame**: Auto-Smelt - Automatically smelts broken blocks without the need of a furnace. For example, it will smelt a wood log into charcoal.
* **Pickaxe + Looting**: Deconstruct broken blocks (reverse crafting)
* **Pickaxe + Power**: Instantly break anything (except bedrock - can be configured)
* **Pickaxe + Sharpness**: When one ore is mined, the rest nearby will be broken automatically
* **Pickaxe + Silk Touch II**: Harvest ice, double slabs, silverfish blocks
* **Pickaxe + Silk Touch III**: Harvest endercrystal (right-click)
* **Shears + Bane of Arthropods**: Extract spider eyes
* **Shears + Fortune**: Apples are dropped when leaves are broken
* **Shears + Looting**: Harvest more wool (random colors), feathers from chickens, leather from cows, saddles from saddled pigs
* **Shears + Power**: Cut grass and trim hedges quickly
* **Shears + Silk Touch**: Harvest cobweb, dead bush
* **Shears + Smite**: Gouge eyes (Adds a blindness effect to the victim)
* **Shovel + Flame**: Auto-Smelt - Automatically smelts broken blocks without the need of a furnace. For example, it will smelt a wood log into charcoal.
* **Shovel + Power**: Excavation (dig large area without drops)
* **Shovel + Silk Touch II**: Harvest fallen snow, fire
* **Sword + Blast Protection**: Shoot fireball (right-click)
* **Sword + Feather Falling**: Launch victim (right-click)
* **Sword + Fire Protection**: When dropped in lava, the sword will automatically return to the owner's feet
* **Sword + Fortune**: Pickpocket (right-click player to drop an item out of their inventory)
* **Sword + Infinity**: Selective invisibility (right-click player to vanish from their view. Note that you are only invisible from that player)
* **Sword + Power**: Strike lightning far away
* **Sword + Protection**: Resistance potion effect when blocking
* **Sword + Punch**: Knock item out of hand (right-click player)
* **Sword + Respiration**: Banhammer (1=kick, 2+=temp ban)
* **Sword + Silk Touch**: Capture (right-click to drop entity as spawn egg item)

## Limitations
* Not all enchantments reasonably combine with one another

* No permission support, incomplete world protection support

* Some effects are overpowered or unstable; be careful!

For all known issues or to file a new bug see [Tickets](http://dev.bukkit.org/server-mods/enchantmore/tickets/).

## Notes
[Slot data](http://wiki.vg/Slot\_Data) protocol reference

Other relevant plugins of interest pertaining to enchantments:

* [Sublimation](http://dev.bukkit.org/server-mods/sublimation/) - silk touch ice, no longer overpowered
* [SilkSpawners](http://dev.bukkit.org/server-mods/silkspawners/) - pickup and move mob spawners with silk touch

Got a cool idea for a new effect? I can't promise I'll implement everything, but all suggestions are welcome!
Feel free to discuss your ideas below, or open a [ticket](http://dev.bukkit.org/server-mods/enchantmore/tickets/)
for more specific requests. Including the specific item + enchantment name is appreciated, preferably
from the available [wanted effects](http://dev.bukkit.org/server-mods/enchantmore/pages/wanted-effects/) list.

Also interested in new potions? Try [PotionsPlus](http://dev.bukkit.org/server-mods/potionsplus/).

## GitHub

[Fork me on GitHub!](https://github.com/DSH105/EnchantMore/fork)
