package me.steve8playz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class EasyHeads extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("EasyHeads v1.0 has been Enabled");
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("EasyHeads v1.0 has been Disabled");
    }

    // Gets the name of the player with the same head of the entity clicked.
    public static String EntityToName(Entity entity) {
        if (entity instanceof Blaze) return "Blaze";
        else if (entity instanceof CaveSpider) return "CaveSpider";
        else if (entity instanceof Chicken) return "Chicken";
        else if (entity instanceof Cow) return "Cow";
        else if (entity instanceof Creeper) return "Creeper";
        else if (entity instanceof Enderman) return "Enderman";
        else if (entity instanceof Ghast) return "Ghast";
        else if (entity instanceof Golem) return "Golem";
        else if (entity instanceof MagmaCube) return "LavaSlime";
        else if (entity instanceof MushroomCow) return "MushroomCow";
        else if (entity instanceof Ocelot) return "Ocelot";
        else if (entity instanceof Pig) return "Pig";
        else if (entity instanceof PigZombie) return "PigZombie";
        else if (entity instanceof Sheep) return "Sheep";
        else if (entity instanceof Skeleton) return "Skeleton";
        else if (entity instanceof Slime) return "Slime";
        else if (entity instanceof Spider) return "Spider";
        else if (entity instanceof Squid) return "Squid";
        else if (entity instanceof Villager) return "Villager";
        else if (entity instanceof WitherSkeleton) return "WSkeleton";
        else if (entity instanceof Zombie) return "Zombie";
        else return "Steve";
    }

    // Makes the Head
    // TODO: Make the lores and item name customisable from the config
    private static ItemStack getHead(String player, String itemName, int itemAmount) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, itemAmount);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(itemName);
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("EasyHeads Head");
        skull.setLore(lore);
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(player)); // TODO: Make getOfflinePlayer() not deprecated.
        item.setItemMeta(skull);
        return item;
    }

    // Keeps track of the cooldowns for multiple players
    private HashMap<String, Long> cooldowns = new HashMap<String, Long>();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        // Cooldown to stop this from executing twice
        // TODO: Find a better way of doing this
        // TODO: Put cooldownTime into config
        int cooldownTime = 5;
        if(cooldowns.containsKey(player.getName())) {
            long secondsLeft = ((cooldowns.get(player.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
            if(secondsLeft>0) {
                // player.sendMessage("You cant use that commands for another "+ secondsLeft +" seconds!");
                return;
            }
        }
        cooldowns.put(player.getName(), System.currentTimeMillis());
        // END Cooldown Code
        if (player.hasPermission("EasyHeads.clickhead")) {
            if (getConfig().getBoolean("player-heads-only")) {
                if (player.getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD) {
                    setHead(player, entity);
                }
            } else {
                if (player.getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD || player.getInventory().getItemInMainHand().getType() == Material.CREEPER_HEAD || player.getInventory().getItemInMainHand().getType() == Material.ZOMBIE_HEAD) {
                    setHead(player, entity);
                } else {
                    if (player.getInventory().getItemInMainHand().getType() == Material.DRAGON_HEAD)
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.RED + "You need to be holding a cube head in your main hand to set heads to this entity");
                    //else
                    //    player.sendMessage(messagePrefix + ChatColor.RED + "You need to be holding a head in your main hand to do this");
                }
            }
        }
    }

    private void setHead(Player player, Entity entity) {
        int itemAmount = player.getInventory().getItemInMainHand().getAmount();
        if (entity instanceof Player) {
            Player other = (Player) entity;
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                player.getInventory().setItemInMainHand(getHead(other.getName(), other.getName() + "'s Head", itemAmount));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.AQUA + "Set head in hand to " + other + "'s head");
            });
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                player.getInventory().setItemInMainHand(getHead("MHF_" + EntityToName(entity), EntityToName(entity) + " Head", itemAmount));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.AQUA + "Set head in hand to a " + EntityToName(entity) + " head");
            });
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInventory inventory = player.getInventory();
            // BEGIN SetHead Command
            if ((cmd.getName().equalsIgnoreCase("SetHead")) && (sender.hasPermission("EasyHeads.sethead"))) {
                if (args.length == 1) {
                    if (getConfig().getBoolean("player-heads-only")) {
                        if (player.getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD) {
                            int itemAmount = player.getInventory().getItemInMainHand().getAmount();
                            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                                inventory.setItemInMainHand(getHead(args[0], args[0] + "'s Head", itemAmount));
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.AQUA + "Set head in hand to " + args[0] + "'s head");
                            });
                        } else {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.RED + "You need to be holding a player head in your main hand to do this");
                        }
                    } else {
                        if (player.getInventory().getItemInMainHand().getType() == Material.PLAYER_HEAD || player.getInventory().getItemInMainHand().getType() == Material.CREEPER_HEAD || player.getInventory().getItemInMainHand().getType() == Material.ZOMBIE_HEAD) {
                            int itemAmount = player.getInventory().getItemInMainHand().getAmount();
                            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                                inventory.setItemInMainHand(getHead(args[0], args[0] + "'s Head", itemAmount));
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.AQUA + "Set head in hand to " + args[0] + "'s head");
                            });
                        } else {
                            if (player.getInventory().getItemInMainHand().getType() == Material.DRAGON_HEAD)
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.RED + "You need to be holding a cube head in your main hand to do this");
                            else
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.RED + "You need to be holding a head in your main hand to do this");
                        }
                    }

                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.RED + "Usage: /SetHead <player>");
                }
            }
            // BEGIN GetHead Command
            if ((cmd.getName().equalsIgnoreCase("GetHead")) && (sender.hasPermission("EasyHeads.gethead"))) {
                if (args.length == 1 || args.length == 2) {
                    if (args.length == 1)
                        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                            player.getInventory().addItem(getHead(args[0], args[0] + "'s Head", 1));
                        });
                    else if (args.length == 2)
                        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                            player.getInventory().addItem(getHead(args[0], args[0] + "'s Head", Integer.parseInt(args[1])));
                        });
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.AQUA + "Got head of " + args[0]);
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-prefix")) + ChatColor.RED + "Usage: /GetHead <player> [amount]");
                }
            }
        } else getLogger().warning("You need to be a player to run this command.");

        // BEGIN Admin Commands
        if ((cmd.getName().equalsIgnoreCase("EasyHeads")) && (sender.hasPermission("EasyHeads.admin"))) {
            if (args.length == 1) {
                if ((args[0].equalsIgnoreCase("reload")) && (sender.hasPermission("EasyHeads.admin.reload"))) {
                    Bukkit.getPluginManager().getPlugin("EasyHeads").reloadConfig();
                    sender.sendMessage(ChatColor.GOLD + "[EasyHeads] Reloaded the config!");
                }
                if ((!(args[0].equalsIgnoreCase("reload") || (args[0].equalsIgnoreCase("debug")))) && (sender.hasPermission("ServerWebsite.admin"))) {
                    sender.sendMessage("Usage: /ServerWebsite <reload/variable> [value]");
                }
            } else if (args.length == 2) {
                if ((args[0].equalsIgnoreCase("player-heads-only")) && (sender.hasPermission("EasyHeads.admin.settings"))) {
                    if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                        getConfig().set("player-heads-only", args[1].toLowerCase());
                        sender.sendMessage(ChatColor.GOLD + "[EasyHeads] Successfully changed " + args[0]);
                    }
                }
                if ((!(args[0].equalsIgnoreCase("reload") || (args[0].equalsIgnoreCase("player-heads-only")))) && (sender.hasPermission("ServerWebsite.admin"))) {
                    sender.sendMessage("Usage: /ServerWebsite <reload/variable> [value]");
                }
            } else {
                if (sender.hasPermission("ServerWebsite.admin")) {
                    sender.sendMessage("Usage: /ServerWebsite <reload/variable> [value]");

                }
            }
        }
        return true;
    }
}