package com.ACStache.RangedWolves;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;

public class RWConfig
{
    private static Configuration config;
    private static HashMap<World, LinkedList<Boolean>> worldMap = new HashMap<World, LinkedList<Boolean>>();
    private static ArrayList<World> worlds;
    private static HashMap<Arena, LinkedList<Boolean>> arenaMap = new HashMap<Arena, LinkedList<Boolean>>();
    private static LinkedList<Arena> arenas;
    
    /**
     * Load/reload the configuration file
     * @param file the configuration file
     */
    public static void loadConfig(File file)
    {
        config = new Configuration(file);
        config.load();
        
        worlds = (ArrayList<World>)Bukkit.getServer().getWorlds();
        clearWorlds();
        setWorlds();

        if(RangedWolves.maHandler != null)
        {
	        RangedWolves.am.initialize();
	        arenas = (LinkedList<Arena>)RangedWolves.am.getEnabledArenas();
	        clearArenas();
	        setArenas();
        }
        
        config.save();
    }
    
    /**
     * Initialize the configuration file
     * @param file the configuration file
     */
    public static void initConfig(File file)
    {
    	config = new Configuration(file);
        config.load();
        
        config.setHeader(getHeader());
        worlds = (ArrayList<World>)Bukkit.getServer().getWorlds();
        initWorlds();
        setWorlds();
        
        if(RangedWolves.maHandler != null)
        {
	        RangedWolves.am.initialize();
	        arenas = (LinkedList<Arena>)RangedWolves.am.getEnabledArenas();
	        initArenas();
	        setArenas();
        }
        config.save();
    }
    
    /**
     * Returns the Header for the configuration file 
     * @return the Header for the configuration file
     */
    private static String getHeader()
    {
        return "# RangedWolves Config file\n" + 
               "# Please refer to http://dev.bukkit.org/server-mods/ranged-wolves/ for any questions";
    }
    
    /**
     * Initialize the configuration file with any arenas found
     */
    private static void initArenas()
    {
        for(Arena a : arenas)
            config.setProperty("RW-in-MobArena." + a.arenaName(), true);
    }
    
    /**
     * Initialize the arenaMap with the settings from the configuration file
     */
    private static void setArenas()
    {
        for(Arena a : arenas)
        {
            if(arenaMap.get(a) == null)
            {
        	    arenaMap.put(a, new LinkedList<Boolean>());
                arenaMap.get(a).add(config.getBoolean("RW-in-MobArena." + a.arenaName(), true));
            }
            else
                arenaMap.get(a).add(config.getBoolean("RW-in-MobArena." + a.arenaName(), true));
        }
    }
    
    /**
     * Initialize the configuration file with any worlds found
     */
    private static void initWorlds()
    {
        for(World w : worlds)
            config.setProperty("RW-on-Server." + w.getName(), true);
    }
    
    /**
     * Initialize the worldMap with the settings from the configuration file
     */
    private static void setWorlds()
    {
        for(World w : worlds)
        {
            if(worldMap.get(w) == null)
            {
                worldMap.put(w, new LinkedList<Boolean>());
                worldMap.get(w).add(config.getBoolean("RW-on-Server." + w.getName(), true));
            }
            else
                worldMap.get(w).add(config.getBoolean("RW-on-Server." + w.getName(), true));
        }
    }
    
    /**
     * Check if a world is allowed to use RW
     * @param world the world being checked
     * @return true/false
     */
    public static Boolean RWinWorld(World world)
    {
        return worldMap.get(world).getFirst();
    }
    
    /**
     * Check if an arena is allowed to use RW
     * @param arena the arena being checked
     * @return true/false
     */
    public static Boolean RWinArena(Arena arena)
    {
        return arenaMap.get(arena).getFirst();
    }
    
    /**
     * Clear the worldMap for reloads/restarts
     * Mainly to ensure no overlaps happen in settings
     */
    public static void clearWorlds()
    {
        worldMap.clear();
    }
    
    /**
     * Clear the arenaMap for reloads/restarts
     * Mainly to ensure no overlaps happen in settings
     */
    public static void clearArenas()
    {
        arenaMap.clear();
    }
}