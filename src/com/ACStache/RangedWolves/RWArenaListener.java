package com.ACStache.RangedWolves;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArenaListener;

public class RWArenaListener extends MobArenaListener
{
    public void onArenaStart(final Arena arena)
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                for (Wolf w : arena.getPets())
                {
                    RangedWolvesOwner.addWolf((Player)w.getOwner(), w);
                    if(RWDebug.getDebug())
                    {
                        Bukkit.getServer().broadcastMessage("Wolf ID# " + w.getEntityId() + " assigned to: " + ((Player)w.getOwner()).getName());
                    }
                }
            }
        }, 20);
    }
}