package com.ACStache.RangedWolves;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.garbagemule.MobArena.Arena;

public class RWOwner
{
    private static HashMap<Arena,HashMap<String,Set<Wolf>>> arenaMap = new HashMap<Arena,HashMap<String,Set<Wolf>>>();
    private static HashMap<String,Set<Wolf>> arenaWolfMap = new HashMap<String,Set<Wolf>>();
    private static HashMap<String,Set<Wolf>> tamedWolfMap = new HashMap<String,Set<Wolf>>();
    
    //adding methods
    /**
     * method to add wolves from onTameEntity and onCreatureSpawn events
     * @param player the player who gets the wolf
     * @param wolf the wolf in question
     */
    public static void addWolf(String playerName, Wolf wolf)
    {
        if(tamedWolfMap.get(playerName) == null)
        {
            tamedWolfMap.put(playerName, new HashSet<Wolf>());
            if(!tamedWolfMap.get(playerName).contains(wolf))
            {
                tamedWolfMap.get(playerName).add(wolf);
            }
        }
        else
        {
            if(!tamedWolfMap.get(playerName).contains(wolf))
            {
                tamedWolfMap.get(playerName).add(wolf);
            }
        }
    }
    
    /**
     * method to add wolves spawned using Mob Arena
     * @param arena the arena the player is in
     * @param player the player who picked a pet class
     * @param wolf the wolf that got spawned
     */
    public static void addWolf(Arena arena, String playerName, Wolf wolf)
    {
        if(arenaMap.get(arena) == null)
        {
            if(arenaWolfMap.get(playerName) == null)
            {
                arenaWolfMap.put(playerName, new HashSet<Wolf>());
                arenaMap.put(arena, arenaWolfMap);
                if(!arenaWolfMap.get(playerName).contains(wolf))
                {
                    arenaWolfMap.get(playerName).add(wolf);
                }
            }
        }
        else
        {
            if(arenaWolfMap.get(playerName) == null)
            {
                arenaWolfMap.put(playerName, new HashSet<Wolf>());
                if(!arenaWolfMap.get(playerName).contains(wolf))
                {
                    arenaWolfMap.get(playerName).add(wolf);
                }
            }
            else
            {
                if(!arenaWolfMap.get(playerName).contains(wolf))
                {
                    arenaWolfMap.get(playerName).add(wolf);
                }
            }
        }
    }
    
    
    //getter methods
    /**
     * checks a wolf versus all wolves that have been attached to a player
     * @param wolf the wolf being checked
     * @return true or false
     */
    public static boolean checkArenaWolf(Wolf wolf)
    {
        for(Set<Wolf> set : arenaWolfMap.values())
            if(set.contains(wolf))
                return true; 
        return false;
    }
    
    /**
     * checks a wolf versus all wolves that have been attached to a player
     * @param wolf the wolf being checked
     * @return true or false
     */
    public static boolean checkWorldWolf(Wolf wolf)
    {
        for(Set<Wolf> set : tamedWolfMap.values())
            if(set.contains(wolf))
                return true; 
        return false;
    }
    
    /**
     * returns the list of pets of the player
     * @param player the owner of the pets
     * @return the list of pets of the player
     */
    public static Set<Wolf> getPets(Player player)
    {
        if(RWArenaChecker.isPlayerInArena(player))
            return arenaWolfMap.get(player.getName());
        else
            return tamedWolfMap.get(player.getName());
    }
    
    /**
     * returns the number of pets a given player has currently
     * @param player
     * @return
     */
    public static int getPetAmount(Player player)
    {
        if(!RWArenaChecker.isPlayerInArena(player))
            return tamedWolfMap.get(player.getName()).size();
        else
            return RWConfig.RWMaxWolves();
    }
    
    
    //clear & remove methods
    /**
     * method to remove a wolf from a player's list of wolves if it dies
     * @param player the player who just lost a pet
     * @param wolf the wolf that just died
     */
    public static void removeWolf(String playerName, Wolf wolf)
    {
        if(tamedWolfMap.get(playerName).contains(wolf))
        {
            tamedWolfMap.get(playerName).remove(wolf);
        }
    }
    
    /**
     * clears all players/wolves in the arena
     * @param arena the arena that just ended
     */
    public static void clearWolves(Arena arena)
    {
        if(arenaMap.get(arena) != null)
            arenaMap.get(arena).clear();
    }
}