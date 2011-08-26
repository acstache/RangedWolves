package com.ACStache.RangedWolves;

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
        if(RangedWolves.maHandler != null && RangedWolves.maHandler.isPlayerInArena(player))
            return true; //Mob Arena found and player is in an arena
        else
            return false; //Mob Arena not found, player not in an arena, or both
    }
}