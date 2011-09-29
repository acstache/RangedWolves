package com.ACStache.RangedWolves;

import java.util.HashSet;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RWPlayerListener extends PlayerListener
{
    final RangedWolves plugin;
    private HashSet<World> worlds = new HashSet<World>();
    
    public RWPlayerListener(RangedWolves instance)
    {
        plugin = instance;
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        World world = event.getPlayer().getWorld();
        if(!worlds.contains(world))
        {
            for(LivingEntity e : world.getLivingEntities())
            {
                if(e instanceof Wolf)
                {
                    Wolf wolf = (Wolf)e;
                    if(wolf.getOwner() instanceof OfflinePlayer)
                    {
                        OfflinePlayer offPlayer = (OfflinePlayer)wolf.getOwner();
                        if(offPlayer != null)
                        {
                            String name = offPlayer.getName();
                            System.out.println("Wolf " + wolf.getEntityId() + " added to Player " + name);
                            RWOwner.addWolf(name, wolf);
                        }
                    }
                    else
                    {
                        Player owner = (Player)wolf.getOwner();
                        if(owner != null)
                        {
                            String name = owner.getName();
                            System.out.println("Wolf " + wolf.getEntityId() + " added to Player " + name);
                            RWOwner.addWolf(name, wolf);
                        }
                    }
                }
            }
        }
    }
}