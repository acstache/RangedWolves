package com.ACStache.RangedWolves;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

public class RangedWolvesOwner
{
    private static Map<Player,List<Wolf>> map = new HashMap<Player,List<Wolf>>();
    
    public static void addWolf(Player player, Wolf wolf)
    {
        if(map.get(player) == null)
        {
            map.put(player, new LinkedList<Wolf>());
            map.get(player).add(wolf);
        }
        else
        {
            map.get(player).add(wolf);
        }
    }
    
    public static List<Wolf> getPets(Player player)
    {
        return map.get(player);
    }
}