package com.ACStache.RangedWolves;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;

public class RangedWolves extends JavaPlugin
{
    public static MobArenaHandler maHandler;
    private Logger log = Logger.getLogger("Minecraft");
    private PluginDescriptionFile info;
    private final RWEntityListener entityListener = new RWEntityListener(this);
    @SuppressWarnings("unused")
    private RWArenaListener arenaListener;

    public void onEnable()
    {
        RWDebug.setDebug(false);
        
        setupMobArenaHandler();
        
        info = getDescription();
        log.info("[" + info.getName() + "] " + info.getVersion() + " Enabled successfully! By " + info.getAuthors());
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.ENTITY_TAME, entityListener, Priority.Monitor, this);
    }

    public void onDisable()
    {
        log.info("[" + info.getName() + "] " + info.getVersion() + " Disabled!");
    }
    
    public void setupMobArenaHandler()
    {
        Plugin maPlugin = (MobArena)Bukkit.getServer().getPluginManager().getPlugin("MobArena");
        
        if(maPlugin == null)
            return;
        
        maHandler = new MobArenaHandler();
        arenaListener = new RWArenaListener();
    }
    
    //used solely for the debug command
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("rw"))
        {
            if((sender instanceof Player && ((Player)sender).isOp()) || !(sender instanceof Player))
            {
                if(args[0].equalsIgnoreCase("debug"))
                {
                    RWDebug.setDebug(!(RWDebug.getDebug()));
                    if(RWDebug.getDebug())
                        ((Player)sender).sendMessage(ChatColor.AQUA + "Debug Mode Activated");
                    else
                        ((Player)sender).sendMessage(ChatColor.AQUA + "Debug Mode Deactivated");
                }
                else
                {
                    ((Player)sender).sendMessage("Please type '/rw debug'");
                }
            }
            else
            {
                ((Player)sender).sendMessage("You don't have permission to do that");
            }
        }
        return true;
    }
}