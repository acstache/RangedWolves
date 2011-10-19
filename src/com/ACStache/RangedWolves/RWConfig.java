package com.ACStache.RangedWolves;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import com.garbagemule.MobArena.Arena;

public class RWConfig
{
    private static YamlConfiguration config;
    private static HashMap<World, LinkedList<Boolean>> worldMap = new HashMap<World, LinkedList<Boolean>>();
    private static ArrayList<World> worlds;
    private static HashMap<Arena, LinkedList<Boolean>> arenaMap = new HashMap<Arena, LinkedList<Boolean>>();
    private static LinkedList<Arena> arenas;
    private static HashMap<String, LinkedList<Boolean>> projMap = new HashMap<String, LinkedList<Boolean>>();
    private static LinkedList<String> projs = new LinkedList<String>();
    private static boolean skeleEnabled, skeleInMobArena;
    private static int skeleChance, skeleMaxPets;
    
    /**
     * Load/reload the configuration file
     * @param file the configuration file
     */
    public static void loadConfig(File file)
    {
        config = new YamlConfiguration();
        try
        {
            config.load(file);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("[RangedWolves] No config found. Generating a default config");
            initConfig(file);
        }
        catch (Exception e)
        {
            System.out.println("[RangedWolves] An Error has occured. Try deleting your config and reloading Ranged Wolves");
        }
        finally
        {
            //set Worlds
            worlds = (ArrayList<World>)Bukkit.getServer().getWorlds();
            for(World w : worlds)
            {
                if(!config.contains("RW-on-Server." + w.getName()))
                {
                    System.out.println("[RangedWolves] Updating your config to include World: " + w.getName());
                    config.set("RW-on-Server." + w.getName(), true);
                }
            }
            clearWorlds();
            setWorlds();

            //set Arenas
            if(RangedWolves.maHandler != null)
            {
                RangedWolves.am.initialize();
                arenas = (LinkedList<Arena>)RangedWolves.am.getEnabledArenas();
                for(Arena a : arenas)
                {
                    if(!config.contains("RW-in-MobArena." + a.arenaName()))
                    {
                        System.out.println("[RangedWolves] Updating your config to include Arena: " + a.arenaName());
                        config.set("RW-in-MobArena." + a.arenaName(), true);
                    }
                }
                clearArenas();
                setArenas();
            }
            
            //set Projectiles
            addProjectiles();
            for(String p : projs)
            {
                if(!config.contains("RW-Projectiles." + p))
                {
                    System.out.println("[RangedWolves] Updating your config to include Projectile: " + p);
                    config.set("RW-Projectiles." + p, true);
                }
            }
            clearProjectiles();
            setProjectiles();
            
            //set Skeleton Tamers
            if(!config.contains("RW-Skeleton-Tamers"))
            {
                System.out.println("[RangedWolves] Adding in Skeleton Tamers configuration options");
                initSkeles();
            }
            setSkeleStuff();
            
            try
            {
                config.save(file);
            }
            catch (Exception e)
            {
                System.out.println("[RangedWolves] An Error has occured. Try deleting your config and reloading Ranged Wolves");
            }
        }
    }
    
    /**
     * Initialize the configuration file
     * @param file the configuration file
     */
    public static void initConfig(File file)
    {
        config = new YamlConfiguration();
        try
        {
            config.load(file);
        }
        catch (Exception e)
        {
            System.out.println("[RangedWolves] An Error has occured. Try deleting your config and reloading Ranged Wolves");
        }
        finally
        {
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
            
            addProjectiles();
            initProjectiles();
            setProjectiles();
            
            initSkeles();
            setSkeleStuff();
            
            try
            {
                config.save(file);
            }
            catch (IOException e)
            {
                System.out.println("[RangedWolves] An Error has occured. Try deleting your config and reloading Ranged Wolves");
            }
        }
    }
    
    
    //Initialize Methods
    /**
     * Initialize the configuration file with any arenas found
     */
    private static void initArenas()
    {
        for(Arena a : arenas)
            config.set("RW-in-MobArena." + a.arenaName(), true);
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
     * Initialize the Skeleton Tamer settings.
     * Default set to enabled, 10% chance of occurence, and 1 Max Pet
     */
    private static void initSkeles()
    {
        config.set("RW-Skeleton-Tamers.Enabled", true);
        config.set("RW-Skeleton-Tamers.MA-Enabled", true);
        config.set("RW-Skeleton-Tamers.Chance", 10);
        config.set("RW-Skeleton-Tamers.Max-Pets", 1);
    }
    
    private static void addProjectiles()
    {
        projs.add("Arrow");
        projs.add("Egg");
        projs.add("Fireball");
        projs.add("Snowball");
    }
    
    private static void initProjectiles()
    {
        for(String s : projs)
            config.set("RW-Projectiles." + s, true);
    }
    
    
    //Setter Methods
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
            {
                arenaMap.get(a).add(config.getBoolean("RW-in-MobArena." + a.arenaName(), true));
            }
        }
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
            {
                worldMap.get(w).add(config.getBoolean("RW-on-Server." + w.getName(), true));
            }
        }
    }
    
    private static void setSkeleStuff()
    {
        skeleEnabled = config.getBoolean("RW-Skeleton-Tamers.Enabled");
        skeleInMobArena = config.getBoolean("RW-Skeleton-Tamers.MA-Enabled");
        skeleChance = config.getInt("RW-Skeleton-Tamers.Chance");
        //if someone puts over 100 for the % chance to spawn
        if(skeleChance > 100)
            skeleChance = 100;
        
        //if someone puts a negative number for the % chance to spawn
        if(skeleChance < 0)
            skeleChance = 0;
        
        skeleMaxPets = config.getInt("RW-Skeleton-Tamers.Max-Pets");
        //if someone puts over 5 for the # of maximum pets
        if(skeleMaxPets > 5)
            skeleMaxPets = 5;
        //if someone puts a negative number for the # of maximum pets
        if(skeleMaxPets < 0)
            skeleMaxPets = 0;
    }
    
    private static void setProjectiles()
    {
        for(String s : projs)
        {
            if(projMap.get(s) == null)
            {
                projMap.put(s, new LinkedList<Boolean>());
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
     * Check if an arena is allowed to use RW
     * @param arena the arena being checked
     * @return true/false
     */
    public static boolean RWinArena(Arena arena)
    {
        return arenaMap.get(arena).getFirst();
    }
    
    /**
     * Check if a world is allowed to use RW
     * @param world the world being checked
     * @return true/false
     */
    public static boolean RWinWorld(World world)
    {
        return worldMap.get(world).getFirst();
    }
    
    /**
     * Check if a projectile is allowed to be used for RW
     * @param projName the projectile in question
     * @return true/false
     */
    public static boolean RWProj(String projName)
    {
        return projMap.get(projName).getFirst();
    }
    
    /**
     * Check if Skeletons are allowed to have pet Wolves
     * @return true/false
     */
    public static boolean getSkeleEnabled()
    {
        return skeleEnabled;
    }
    
    /**
     * Check if Skeletons are allowed to have pet Wolves in an Arena
     * @return
     */
    public static boolean getSkeleInMobArena()
    {
        return skeleInMobArena;
    }
    
    /**
     * Check the chance of a Skeleton Tamer spawning
     * @return Integer value (0 through 100)
     */
    public static int getSkeleChance()
    {
        return skeleChance;
    }
    
    /**
     * Returns the maximum number of allowed Skeleton pets
     * @return Integer value (0 through 5)
     */
    public static int getSkeleMaxPets()
    {
        return skeleMaxPets;
    }
    
    /**
     * Returns the Header for the configuration file 
     * @return the Header for the configuration file
     */
    @SuppressWarnings("unused")
    private static String getHeader()
    {
        return "# RangedWolves Config file\n" + 
               "# Please refer to http://dev.bukkit.org/server-mods/ranged-wolves/ for any questions";
    }
    
    
    //Clearing Methods
    /**
     * Clear the arenaMap for reloads/restarts
     * Mainly to ensure no overlaps happen in settings
     */
    public static void clearArenas()
    {
        arenaMap.clear();
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
     * Clear the projMap for reloads/restarts
     * Mainly to ensure no overlaps happen in settings
     */
    public static void clearProjectiles()
    {
        projMap.clear();
    }
}