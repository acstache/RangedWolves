package com.ACStache.RangedWolves;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArenaListener;

public class RWArenaListener extends MobArenaListener
{
    /**
     * Hook into the onArenaStart event to add wolves to their owners
     * This relationship is currently lacking from Bukkit
     */
    public void onArenaStart(final Arena arena)
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                if(!(arena.getPets() == null)) //if there are pets in the arena
                    for (Wolf w : arena.getPets()) //for all pet wolves in the arena
                        RWOwner.addWolf(arena, (Player)w.getOwner(), w); //attach the wolf to the owner
                else //if there are no pets in the arena
                    return; //ignore
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