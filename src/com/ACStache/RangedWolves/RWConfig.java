package com.ACStache.RangedWolves;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

public class RWConfig
{
    private static YamlConfiguration config = new YamlConfiguration();
    private static File file;
    private static boolean killCreepers;
    private static int maxWolves;
    private static HashMap<World, ArrayList<Boolean>> worldMap = new HashMap<World, ArrayList<Boolean>>();
    private static List<World> worlds;
    private static HashMap<Arena, ArrayList<Boolean>> arenaMap = new HashMap<Arena, ArrayList<Boolean>>();
    private static List<Arena> arenas;
    private static HashMap<String, ArrayList<Boolean>> projMap = new HashMap<String, ArrayList<Boolean>>();
    private static List<String> projs = new ArrayList<String>(6);
    private static MobArenaHandler maHandler;
    private static ArenaMaster am;
    
    /**
     * Load/reload/initialize the configuration file
     * @param file the configuration file
     */
    public static void loadConfig(File f)
    {
        file = f;
        loadConfig();
    }
    public static void loadConfig()
    {
        worlds = new ArrayList<World>(Bukkit.getServer().getWorlds().size());
        worlds = Bukkit.getServer().getWorlds();
        
        am = RangedWolves.getAM();
        maHandler = RangedWolves.getMAH();
        if(maHandler != null)
        {
            arenas = new ArrayList<Arena>(am.getEnabledArenas().size());
            arenas = am.getEnabledArenas();
        }
        addProjectiles();
        
        try
        {
            config.load(file);
        }
        catch (FileNotFoundException e)
        {
            RangedWolves.printToConsole("No config found. Generating a default config");
            
            initCreeper();
            initMaxWolves();
            initWorlds();
            if(maHandler != null)
                initArenas();
            initProjectiles();
        }
        catch (Exception e)
        {
            RangedWolves.printToConsole("An Error has occured. Try deleting your config and reloading RangedWolves");
        }
        finally
        {
            if(!config.contains("RW-Creepers.Wolves-Attack"))
            {
                RangedWolves.printToConsole("Updating your config to include attackable Creepers");
                initCreeper();
            }
            killCreepers = config.getBoolean("RW-Creepers.Wolves-Attack");
            
            if(!config.contains("RW-Max-Wolves.Amount"))
            {
                RangedWolves.printToConsole("Updating your config to include Max Wolves cap");
                initMaxWolves();
            }
            maxWolves = config.getInt("RW-Max-Wolves.Amount");
            
            for(World w : worlds)
            {
                if(!config.contains("RW-on-Server." + w.getName()))
                {
                    RangedWolves.printToConsole("Updating your config to include World: " + w.getName());
                    config.set("RW-on-Server." + w.getName(), true);
                }
            }
            clearWorlds();
            setWorlds();

            if(maHandler != null)
            {
                for(Arena a : arenas)
                {
                    if(!config.contains("RW-in-MobArena." + a.configName()) && config.contains("RW-in-MobArena." + a.arenaName()))
                    {
                        RangedWolves.printToConsole("Editing your config to include Arena: " + a.configName() + " instead of: " + a.arenaName());
                        config.set("RW-in-MobArena." + a.configName(), config.getBoolean("RW-in-MobArena." + a.arenaName(), true));
                        config.set("RW-in-MobArena." + a.arenaName(), null);
                    }
                    else if(!config.contains("RW-in-MobArena." + a.configName()))
                    {
                        RangedWolves.printToConsole("Updating your config to include Arena: " + a.configName());
                        config.set("RW-in-MobArena." + a.configName(), true);
                    }
                }
                clearArenas();
                setArenas();
            }
            
            for(String p : projs)
            {
                if(!config.contains("RW-Projectiles." + p))
                {
                    RangedWolves.printToConsole("Updating your config to include Projectile: " + p);
                    config.set("RW-Projectiles." + p, true);
                }
            }
            clearProjectiles();
            setProjectiles();
            
            try
            {
                config.save(file);
            }
            catch (Exception e)
            {
                RangedWolves.printToConsole("An Error has occured. Try deleting your config and reloading RangedWolves");
            }
        }
    }
    
    
    //Initialize Methods
    /**
     * Initialize the configuration file with wolves default attacking creepers
     */
    private static void initCreeper()
    {
        config.set("RW-Creepers.Wolves-Attack", true);
    }
    
    /**
     * Initialize the max amount of wolves a player can have
     */
    private static void initMaxWolves()
    {
        config.set("RW-Max-Wolves.Amount", 5);
    }
    
    /**
     * Initialize the configuration file with any worlds found
     */
    private static void initWorlds()
    {
        for(World w : worlds)
            config.set("RW-on-Server." + w.getName(), true);
    }
    
    /**
     * Initialize the configuration file with any arenas found
     */
    private static void initArenas()
    {
        for(Arena a : arenas)
            config.set("RW-in-MobArena." + a.configName(), true);
    }
    
    /**
     * Create a list of currently known projectiles
     */
    private static void addProjectiles()
    {
        projs.add("Arrow");
        projs.add("Egg");
        projs.add("Fireball");
        projs.add("Small-Fireball");
        projs.add("Snowball");
        projs.add("Potions");
    }
    
    /**
     * Initialize the configuration file with all currently known projectiles
     */
    private static void initProjectiles()
    {
        for(String s : projs)
            config.set("RW-Projectiles." + s, true);
    }
    
    
    //Setter Methods
    /**
     * Initialize the worldMap with the settings from the configuration file
     */
    private static void setWorlds()
    {
        for(World w : worlds)
        {
            if(worldMap.get(w) == null)
            {
                worldMap.put(w, new ArrayList<Boolean>());
                worldMap.get(w).add(config.getBoolean("RW-on-Server." + w.getName(), true));
            }
            else
            {
                worldMap.get(w).add(config.getBoolean("RW-on-Server." + w.getName(), true));
            }
        }
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
                arenaMap.put(a, new ArrayList<Boolean>());
                arenaMap.get(a).add(config.getBoolean("RW-in-MobArena." + a.arenaName(), true));
            }
            else
            {
                arenaMap.get(a).add(config.getBoolean("RW-in-MobArena." + a.arenaName(), true));
            }
        }
    }
    
    /**
     * Initialize the projMap with the settins from the configuration file
     */
    private static void setProjectiles()
    {
        for(String s : projs)
        {
            if(projMap.get(s) == null)
            {
                projMap.put(s, new ArrayList<Boolean>());
                projMap.get(s).add(config.getBoolean("RW-Projectiles." + s, true));
            }
            else
            {
                projMap.get(s).add(config.getBoolean("RW-Projectiles." + s, true));
            }
        }
    }
    
    
    //Getter Methods
    /**
     * Check if Creepers are allowed to be attack by wolves.
     * @return If true, wolves attack creepers.
     */
    public static boolean RWCreepers()
    {
        return killCreepers;
    }
    
    /**
     * Check the max number of Wolves any player can have.
     * @return the max number of Wolves a player can have
     */
    public static int RWMaxWolves()
    {
        return maxWolves;
    }
    
    /**
     * Check if an arena is allowed to use RW
     * If the arena has somehow not been added, add it with defaults
     * @param arena the arena being checked
     * @return true/false
     */
    public static boolean RWinArena(Arena arena)
    {
        if(arenaMap.get(arena) == null)
        {
            config.set("RW-in-MobArena." + arena.configName(), true);
            loadConfig();
            RangedWolves.printToConsole("Updating your config to include Arena: " + arena.configName());
            return true;
        }
        else if(arenaMap.get(arena).isEmpty())
        {
            return false;
        }
        else
        {
            return arenaMap.get(arena).get(0);
        }
    }
    
    /**
     * Check if a world is allowed to use RW
     * If the world has somehow not been added, add it with defaults
     * @param world the world being checked
     * @return true/false
     */
    public static boolean RWinWorld(World world)
    {
        if(worldMap.get(world) == null)
        {
            config.set("RW-on-Server." + world.getName(), true);
            loadConfig();
            RangedWolves.printToConsole("Updating your config to include World: " + world.getName());
            return true;
        }
        else if(worldMap.get(world).isEmpty())
        {
            return false;
        }
        else
        {
            return worldMap.get(world).get(0);
        }
    }
    
    /**
     * Check if a projectile is allowed to be used for RW
     * @param projName the projectile in question
     * @return true/false
     */
    public static boolean RWProj(String projName)
    {
        return projMap.get(projName).get(0);
    }
    
    
    //Clearing Methods
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
    
    /**
     * Clear the projMap for reloads/restarts
     * Mainly to ensure no overlaps happen in settings
     */
    public static void clearProjectiles()
    {
        projMap.clear();
    }
}