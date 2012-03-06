package com.ACStache.RangedWolves;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RWArenaChecker
{
    /**
     * Checks to see if a player is in a MobArena arena
     * @param player the player being checked
     * @return true or false
     */
    public static boolean isPlayerInArena(Player player)
    {
        return (RangedWolves.maHandler != null && RangedWolves.maHandler.isPlayerInArena(player));
    }
    
    /**
     * Checks to see if a monster is in a MobArena arena
     * @param entity the LivingEntity being checked
     * @return true or false
     */
    public static boolean isMonsterInArena(LivingEntity entity)
    {
        return (RangedWolves.maHandler != null && RangedWolves.maHandler.isMonsterInArena(entity));
    }
}