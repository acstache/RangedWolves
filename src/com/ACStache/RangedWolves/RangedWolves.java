package com.ACStache.RangedWolves;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;

public class RangedWolves extends JavaPlugin
{
    private static MobArenaHandler maHandler;
    private static ArenaMaster am;
    private static MobArena mobArena;
    private static Logger log = Logger.getLogger("Minecraft");
    private static PluginDescriptionFile info;
    private static File dir, file;
    private RWCommandExecutor rwExecutor;

    public void onEnable()
    {
        info = getDescription();
        mobArena = (MobArena)Bukkit.getPluginManager().getPlugin("MobArena");
        if(mobArena != null && mobArena.isEnabled())
            setupMobArena(mobArena);
        
        dir = getDataFolder();
        file = new File(dir, "config.yml");
        if(!dir.exists())
            dir.mkdir();
        
        rwExecutor = new RWCommandExecutor(this);
        getCommand("rw").setExecutor(rwExecutor);
        RWConfig.loadConfig(file);
        this.getServer().getPluginManager().registerEvents(new RWListener(this), this);
        
        printToConsole(info.getVersion() + " Enabled successfully! By: " + info.getAuthors());
        
    }

    public void onDisable()
    {
        RWConfig.clearWorlds();
        RWConfig.clearArenas();
        RWConfig.clearProjectiles();
        RWListener.clearProjectiles();
        printToConsole("Successfully Disabled");
    }
    
    public static void printToConsole(String msg)
    {
        log.info("[" + info.getName() + "] " + msg);
    }
    
    public static void printToPlayer(Player p, String msg)
    {
        p.sendMessage(ChatColor.AQUA + "[RangedWolves] " + ChatColor.WHITE + msg);
    }
    
    /*
     * Mob Arena setup & getters
     */
    public static void setupMobArena(MobArena instance)
    {
        maHandler = new MobArenaHandler();
        am = instance.getArenaMaster();
        new RWArenaChecker();
        printToConsole("Found MobArena!");
    }
    
    public static MobArena getMA()
    {
        return mobArena;
    }
    
    public static MobArenaHandler getMAH()
    {
        return maHandler;
    }
    
    public static ArenaMaster getAM()
    {
        return am;
    }
}