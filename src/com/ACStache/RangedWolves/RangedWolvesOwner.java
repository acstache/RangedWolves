package com.ACStache.RangedWolves;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.garbagemule.MobArena.Arena;

public class RangedWolvesOwner
{
    private static HashMap<Arena, HashMap<Player, List<Wolf>>> arenaMap = new HashMap<Arena,HashMap<Player,List<Wolf>>>();
    private static HashMap<Player, List<Wolf>> arenaWolfMap = new HashMap<Player,List<Wolf>>();
    private static HashMap<Player,List<Wolf>> tamedWolfMap = new HashMap<Player,List<Wolf>>();
    
    /**
     * method to add wolves from onTameEntity event
     * @param player the player who did the taming
     * @param wolf the wolf that got tamed
     */
    public static void addWolf(Player player, Wolf wolf)
    {
        if(tamedWolfMap.get(player) == null)
        {
            tamedWolfMap.put(player, new LinkedList<Wolf>());
            tamedWolfMap.get(player).add(wolf);
        }
        else
        {
            tamedWolfMap.get(player).add(wolf);
        }
    }
    
    /**
     * method to add wolves spawned using Mob Arena
     * @param arena the arena the player is in
     * @param player the player who picked a pet class
     * @param wolf the wolf that got spawned
     */
    public static void addWolf(Arena arena, Player player, Wolf wolf)
    {
        if(arenaMap.get(arena) == null) //arena not found
        {
            arenaMap.put(arena, new HashMap<Player, List<Wolf>>());
            if(arenaWolfMap.get(player) == null) //player not found in the arena (always true on just making a new arena key)
            {
                arenaWolfMap.put(player, new LinkedList<Wolf>());
                arenaMap.get(arena).get(player).add(wolf);
            }
        }
        else
        {
            if(arenaWolfMap.get(player) == null) //player not found in the arena
            {
                arenaWolfMap.put(player, new LinkedList<Wolf>());
                arenaMap.get(arena).get(player).add(wolf);
            }
            else
                arenaMap.get(arena).get(player).add(wolf);
        }
    }
    
    /**
     * method to remove a wolf from a player's list of pets if it dies
     * @param player the player who just lost a pet
     * @param wolf the wolf that just died
     */
    public static void removeWolf(Player player, Wolf wolf)
    {
        tamedWolfMap.get(player).remove(wolf);
    }
    
    /**
     * returns the list of pets of the player
     * @param player the owner of the pets
     * @return the list of pets of the player
     */
    public static List<Wolf> getPets(Player player)
    {
        if(RWArenaChecker.isPlayerInArena(player)) //if player is in an arena match
            return arenaWolfMap.get(player);
        else //if player is NOT in an arena match
            return tamedWolfMap.get(player);
    }
    
    /**
     * clears all players/wolves in the arena
     * @param arena the arena that just ended
     */
    public static void clearWolves(Arena arena)
    {
        arenaMap.get(arena).clear();
    }
}