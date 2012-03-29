package com.ACStache.RangedWolves;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

public class RWArenaChecker
{
    private static ArenaMaster am;
    private static MobArenaHandler maHandler;
    
    public RWArenaChecker() {
        am = RangedWolves.getAM();
        maHandler = RangedWolves.getMAH();
    }

    /**
     * Checks to see if a player is in a MobArena arena
     * @param player the player being checked
     * @return true or false
     */
    public static boolean isPlayerInArena(Player player)
    {
        return (maHandler != null && maHandler.isPlayerInArena(player));
    }
    
    /**
     * Checks to see if a monster is in a MobArena arena
     * @param entity the LivingEntity being checked
     * @return true or false
     */
    public static boolean isMonsterInArena(LivingEntity entity)
    {
        return (maHandler != null && maHandler.isMonsterInArena(entity));
    }
    
    /**
     * Checks to see if a location is in a MobArena arena
     * @param l the location to be checked
     * @return the Arena object, or null if not
     */
    public static Arena getArenaAtLocation(Location l)
    {
        return am.getArenaAtLocation(l);
    }
}