package com.ACStache.RangedWolves;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class RWCommandExecutor implements CommandExecutor
{
    private final String CONSOLE_CMDS = "Please type 'rw reload', 'rw reloadMA', or 'rw retro playername'";
    private final String PLAYER_CMDS = "Please type '/rw reload', '/rw reloadMA, or '/rw retro'";
    private final String MA_RELOAD = "Mob Arena setup code rerun";
    private final String NO_PERM = "You don't have permission to do that";
    private final String RELOAD = "Config reloaded";
    private RangedWolves plugin;
    private String version;
    
    public RWCommandExecutor(RangedWolves instance)
    {
        plugin = instance;
        version = plugin.getDescription().getVersion();
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player p = null;
        if(sender instanceof Player)
            p = (Player)sender;
        
        if(command.getName().equalsIgnoreCase("rw")) {
            if(args.length >= 1) {
                if(args[0].equalsIgnoreCase("reload")) {
                    if((sender instanceof Player && p.hasPermission("RangedWolves.Reload")) || !(sender instanceof Player)) {
                        RWConfig.loadConfig();
                        if(sender instanceof Player)
                            RangedWolves.printToPlayer(p, RELOAD);
                        else
                            RangedWolves.printToConsole(RELOAD);
                    }
                    else {
                        RangedWolves.printToPlayer(p, NO_PERM);
                    }
                }
                else if(args[0].equalsIgnoreCase("reloadMA")) {
                    if(sender instanceof Player && p.hasPermission("RangedWolves.Reload") || !(sender instanceof Player)) {
                        if(RangedWolves.getMA() != null && RangedWolves.getMA().isEnabled()) {
                            RangedWolves.setupMobArena(RangedWolves.getMA());
                            RWConfig.loadConfig();
                            if(sender instanceof Player)
                                RangedWolves.printToPlayer(p, MA_RELOAD);
                            else
                                RangedWolves.printToConsole(MA_RELOAD);
                        }
                    }
                    else {
                        RangedWolves.printToPlayer(p, NO_PERM);
                    }
                }
                else if(args[0].equalsIgnoreCase("retro")) {
                    if(sender instanceof Player && p.hasPermission("RangedWolves.Retro")) {
                        int wolvesAdded = retroWolves((Player)sender);
                        
                        if(wolvesAdded >= 0)
                            RangedWolves.printToPlayer(p, wolvesAdded + " wolves added to their owners");
                        else
                            RangedWolves.printToPlayer(p, "No new wolves added");
                    }
                    else if(!(sender instanceof Player)) {
                        p = plugin.getServer().getPlayer(args[1]);
                        if(p != null) {
                            int wolvesAdded = retroWolves(p);
                        
                            if(wolvesAdded >= 0)
                                RangedWolves.printToConsole(wolvesAdded + " wolves added to their owners");
                            else
                                RangedWolves.printToConsole("No new wolves added");
                        }
                        else {
                            RangedWolves.printToConsole(args[1] + " is not a valid player");
                        }
                    }
                    else {
                        RangedWolves.printToConsole(NO_PERM);
                    }
                }
                else {
                    if(sender instanceof Player)
                        RangedWolves.printToPlayer(p, PLAYER_CMDS);
                    else
                        RangedWolves.printToConsole(CONSOLE_CMDS);
                }
            }
            else {
                if(sender instanceof Player)
                    RangedWolves.printToPlayer(p, "version " + version);
                else
                    RangedWolves.printToConsole("version " + version);
            }
        }
        return true;
    }
    
    private int retroWolves(Player p)
    {
        int wolvesAdded = 0;
        for(Entity e : p.getNearbyEntities(20, 20, 20)) {
            if(!(e instanceof Wolf)) {continue;}
            Wolf wolf = (Wolf)e;
            if(RWOwner.checkWorldWolf(wolf) || wolf.getOwner() == null) {continue;}
            if(wolf.getOwner() instanceof Player) {
                if(RWOwner.getPetAmount((Player)wolf.getOwner()) >= RWConfig.RWMaxWolves())
                    if(!((Player)wolf.getOwner()).hasPermission("RangedWolves.Unlimited")){continue;}
                
                RWOwner.addWolf(((Player)wolf.getOwner()).getName(), wolf);
            }
            else {
                if(RWOwner.getPetAmount((OfflinePlayer)wolf.getOwner()) >= RWConfig.RWMaxWolves()) {continue;}
                RWOwner.addWolf(((OfflinePlayer)wolf.getOwner()).getName(), wolf);
            }
            wolvesAdded++;
        }
        return wolvesAdded;
    }
}