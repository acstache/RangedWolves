package com.ACStache.RangedWolves;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArenaListener;

public class RWArenaListener extends MobArenaListener
{
    /**
     * Hook into the onArenaStart event to add wolves to their owners
     */
    public void onArenaStart(final Arena arena)
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                HashSet<Wolf> pets = (HashSet<Wolf>)arena.getPets();
                if(!(pets == null))
                    for (Wolf w : pets)
                        RWOwner.addWolf(arena, (Player)w.getOwner(), w);
            }
        }, 20);
    }
    
    /**
     * clear any wolves associated with players in this arena onArenaEnd
     */
    public void onArenaEnd(Arena arena)
    {
        RWOwner.clearWolves(arena);
    }
}