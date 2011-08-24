package com.ACStache.RangedWolves;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;

public class RWEntityListener extends EntityListener
{
    final RangedWolves plugin;
    
    public RWEntityListener(RangedWolves instance)
    {
        plugin = instance;
    }
    
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(!event.isCancelled()) //damage event is not cancelled
        {
            Entity target = event.getEntity(); //get entity of the damage event
            if(!(target instanceof Player)) //if the entity is not a player
            {
                DamageCause damager = target.getLastDamageCause().getCause(); //get cause of the last damage caused
                if(damager == DamageCause.PROJECTILE) //if the cause was a projectile
                {
                    if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Entity damaged due to projectile");}
                    EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent)event; //get the damaged by entity event
                    Entity cause = event2.getDamager(); //get the damaging entity of the event
                    if(cause instanceof Projectile) //if the entity is a projectile
                    {
                        if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Damaging entity is a projectile");}
                        Projectile proj = (Projectile)cause; //cast it to a projectile
                        if(proj.getShooter() instanceof Player) //if the shooter is a player
                        {
                            if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Shooter of the projectile is a Player");}
                            Player player = (Player)proj.getShooter(); //get the shooter of the projectile
                            if(isPlayerInArena(player)) //shooter is in an arena match
                            {
                                if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Shooter is in an arena");}
                                if(!(RangedWolvesOwner.getPets(player) == null)) //if the shooter has pets
                                {
                                    if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Entity hit is a Living Entity");}
                                    if(target instanceof LivingEntity) //if the target is a living entity
                                    {
                                        LivingEntity newTarget = (LivingEntity)target; //make it a living entity
                                        for(Wolf w : RangedWolvesOwner.getPets(player)) //for each wolf pet the player has
                                        {
                                            if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Wolf ID# " + w.getEntityId());}
                                            w.setTarget(newTarget); //set the target of the wolf to the damaged target
                                            if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Target set as: " + newTarget.getEntityId());}
                                        }
                                    }
                                    else //target is a regular entity (sign/painting/etc)
                                    {
                                        return; //ignore
                                    }
                                }
                                else //if the shooter has no pets
                                {
                                    if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Shooter has no pets");}
                                    return; //ignore
                                }
                            }
                            else //shooter isn't in an arena
                            {
                                if(RWDebug.getDebug()) {Bukkit.getServer().broadcastMessage("Shooter is NOT in an arena");}
                                return; //ignore
                            }
                        }
                        else //if the shooter is a monster (skeleton/boss)
                        {
                            return; //ignore
                        }
                    }
                    else //entity is not a projectile
                    {
                        return; //ignore
                    }
                }
                else //if it's anything other than a projectile
                {
                    return; //ignore
                }
            }
            else //entity hit is a player
            {
                return; //ignore
            }
        }
        else //damage event is cancelled
        {
            return; //ignore
        }
    }
    
    public boolean isPlayerInArena(Player player)
    {
        if(RangedWolves.maHandler != null && RangedWolves.maHandler.isPlayerInArena(player))
        {
            return true; //Mob Arena found and player is in an arena
        }
        else
        {
            return false; //Mob Arena not found, player not in an arena, or both
        }
    }
}