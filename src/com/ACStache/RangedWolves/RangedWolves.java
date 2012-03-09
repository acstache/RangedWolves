package com.ACStache.RangedWolves;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;

public class RangedWolves extends JavaPlugin
{
    public static MobArenaHandler maHandler;
    public static ArenaMaster am;
    private static MobArena mobArena;
    private Logger log = Logger.getLogger("Minecraft");
    private PluginDescriptionFile info;
    private static File dir, file;

    public void onEnable()
    {
        mobArena = (MobArena)Bukkit.getPluginManager().getPlugin("MobArena");
        if(mobArena != null && mobArena.isEnabled())
            setupMobArena(mobArena);
        
        dir = getDataFolder();
        file = new File(dir, "config.yml");
        if(!dir.exists())
        {
            dir.mkdir();
        }
        RWConfig.loadConfig(file);
        
        info = getDescription();
        log.info("[" + info.getName() + "] " + info.getVersion() + " Enabled successfully! By: " + info.getAuthors());
        
        this.getServer().getPluginManager().registerEvents(new RWListener(this), this);
    }

    public void onDisable()
    {
        RWConfig.clearWorlds();
        RWConfig.clearArenas();
        RWConfig.clearProjectiles();
        log.info("[" + info.getName() + "] Successfully Disabled");
    }
    
    private void setupMobArena(MobArena instance)
    {
        maHandler = new MobArenaHandler();
        am = instance.getArenaMaster();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("rw"))
        {
            if(args.length >= 1)
            {
                if(args[0].equalsIgnoreCase("reload"))
                {
                    if((sender instanceof Player && ((Player)sender).hasPermission("RangedWolves.Reload")) || !(sender instanceof Player))
                    {
                        RWConfig.loadConfig();
                        if(sender instanceof Player)
                            ((Player)sender).sendMessage(ChatColor.AQUA + "RW: Config reloaded");
                        else
                            log.info("[" + info.getName() + "] Config reloaded");
                    }
                    else
                    {
                        ((Player)sender).sendMessage(ChatColor.AQUA + "RW: You don't have permission to do that");
                    }
                }
                else if(args[0].equalsIgnoreCase("reloadMA"))
                {
                    mobArena = (MobArena)Bukkit.getServer().getPluginManager().getPlugin("MobArena");
                    if(sender instanceof Player && ((Player)sender).hasPermission("RangedWolves.Reload") || !(sender instanceof Player))
                    {
                        if(mobArena != null && mobArena.isEnabled())
                        {
                            setupMobArena(mobArena);
                            RWConfig.loadConfig();
                            if(sender instanceof Player)
                                ((Player)sender).sendMessage(ChatColor.AQUA + "RW: Mob Arena setup code rerun");
                            else
                                log.info("[" + info.getName() + "] Mob Arena setup code rerun");
                        }
                    }
                    else
                    {
                        ((Player)sender).sendMessage(ChatColor.AQUA + "RW: You don't have permission to do that");
                    }
                }
                else if(args[0].equalsIgnoreCase("retro"))
                {
                    if(sender instanceof Player && ((Player)sender).hasPermission("RangedWolves.Retro"))
                    {
                        int wolvesAdded = 0;
                        for(Entity e : ((Player)sender).getNearbyEntities(20, 20, 20))
                        {
                            if(!(e instanceof Wolf)) {continue;}
                            Wolf wolf = (Wolf)e;
                            if(RWOwner.checkWorldWolf(wolf)) {continue;}
                            if(wolf.getOwner() == null) {continue;}
                            if(wolf.getOwner() instanceof Player)
                            {
                                if(RWOwner.getPetAmount((Player)wolf.getOwner()) >= RWConfig.RWMaxWolves())
                                    if(!((Player)wolf.getOwner()).hasPermission("RangedWolves.Unlimited")){continue;}
                                
                                RWOwner.addWolf(((Player)wolf.getOwner()).getName(), wolf);
                            }
                            else
                            {
                                if(RWOwner.getPetAmount((OfflinePlayer)wolf.getOwner()) >= RWConfig.RWMaxWolves()) {continue;}
                                RWOwner.addWolf(((OfflinePlayer)wolf.getOwner()).getName(), wolf);
                            }
                            wolvesAdded++;
                        }
                        
                        if(wolvesAdded != 0)
                            ((Player)sender).sendMessage(ChatColor.AQUA + "RW: " + wolvesAdded + " wolves added to their owners");
                        else
                            ((Player)sender).sendMessage(ChatColor.AQUA + "RW: No new wolves added");
                    }
                    else
                    {
                        log.info("[" + info.getName() + "] You don't have permission to do that from the console");
                    }
                }
                else
                {
                    if(sender instanceof Player)
                        ((Player)sender).sendMessage(ChatColor.AQUA + "Please type '/rw debug', '/rw reload', '/rw reloadMA, or '/rw retro'");
                    else
                        log.info("[" + info.getName() + "] Please type 'rw debug', 'rw reload', or 'rw reloadMA'");
                }
            }
            else
            {
                if(sender instanceof Player)
                    ((Player)sender).sendMessage(ChatColor.AQUA + "RangedWolves version " + info.getVersion());
                else
                    log.info("[" + info.getName() + "] version " + info.getVersion());
            }
        }
        return true;
    }
}