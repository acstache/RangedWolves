package com.ACStache.RangedWolves;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
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

    public void onEnable()
    {
        info = getDescription();
        log.info("[" + info.getName() + "] " + info.getVersion() + " Enabled! By " + info.getAuthors());
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Monitor, this);
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
}