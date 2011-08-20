package com.ACStache.RangedWolves;

import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArenaListener;

public class RWArenaListener extends MobArenaListener
{
    public void onArenaStart(Arena arena)
    {
        for (Wolf w : arena.getPets())
            RangedWolvesOwner.addWolf((Player)w.getOwner(), w);
    }
}