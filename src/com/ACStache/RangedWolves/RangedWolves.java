package com.ACStache.RangedWolves;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
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
    private MobArena MobArena;
    private final RWArenaListener arenaListener = new RWArenaListener(MobArena);

    public void onEnable()
    {
        RWDebug.setDebug(false);
        
        setupMobArenaHandler();
        
        info = getDescription();
        log.info("[" + info.getName() + "] " + info.getVersion() + " Enabled successfully! By " + info.getAuthors());
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Monitor, this);
        //pm.registerEvent(Event.Type.CUSTOM_EVENT, arenaListener, Priority.Normal, this);
        //this event breaks the plugin, although it is broken without it as well, just without errors
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
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("rw"))
        {
            if(sender instanceof Player && ((Player)sender).isOp())
            {
                if(args[0].equalsIgnoreCase("debug"))
                {
                    RWDebug.setDebug(!(RWDebug.getDebug()));
                    if(RWDebug.getDebug())
                        ((Player)sender).sendMessage("Debug Mode Activated");
                    else
                        ((Player)sender).sendMessage("Debug Mode Deactivated");
                }
                else if(args[0].equalsIgnoreCase("get"))
                {
                    if(RWDebug.getDebug())
                        ((Player)sender).sendMessage("DEBUG MODE IS ACTIVE");
                    else
                        ((Player)sender).sendMessage("DEBUG MODE IS NOT ACTIVE");
                }
                else
                {
                    ((Player)sender).sendMessage("Please type '/rw debug'");
                }
            }
            else
            {
                log.info("[RangedWolves] You can't use Ranged Wolves commands from the console");
            }
        }
        return true;
    }
}