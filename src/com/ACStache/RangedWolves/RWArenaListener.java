package com.ACStache.RangedWolves;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Listener;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaListener;

public class RWArenaListener extends MobArenaListener implements Listener
{
    public RWArenaListener(MobArena instance)
    {
        plugin = instance;
    }
    
    public void onArenaStart(final Arena arena)
    {
        if(!(arena.getPets().equals(null))) //if there are pets in the arena
        {
            for (Wolf w : arena.getPets()) //for all pet wolves in the arena
            {
                RangedWolvesOwner.addWolf((Player)w.getOwner(), w); //attach the wolf to the owner
                if(RWDebug.getDebug())
                    Bukkit.getServer().broadcastMessage("Wolf ID# " + w.getEntityId() + " assigned to: " + ((Player)w.getOwner()).getName());
            }
        }
        else //if there are no pets in the arena
        {
            if(RWDebug.getDebug())
            {
                Bukkit.getServer().broadcastMessage("No pets found in the arena");
            }
            return; //ignore
        }
    }
}