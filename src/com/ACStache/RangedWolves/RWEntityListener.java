package com.ACStache.RangedWolves;

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
        Entity target = event.getEntity(); //get entity of the damage event
        if(!(target instanceof Player)) //if the entity is not a player
        {
            DamageCause damager = target.getLastDamageCause().getCause(); //get cause of the last damage caused
            if(damager == DamageCause.PROJECTILE) //if the cause was a projectile
            {
                EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent)event; //get the damaged by entity event
                Entity cause = event2.getDamager(); //get the damaging entity of the event
                if(cause instanceof Projectile) //if the entity is a projectile
                {
                    Projectile proj = (Projectile)cause; //cast it to a projectile
                    if(proj.getShooter() instanceof Player) //if the shooter is a player
                    {
                        Player player = (Player)proj.getShooter(); //get the shooter of the projectile
                        if(isPlayerInArena(player)) //shooter is in an arena match
                        {
                            if(target instanceof LivingEntity) //if the target is a living entity
                            {
                                LivingEntity newTarget = (LivingEntity)target; //make it a living entity
                                for(Wolf w : RangedWolvesOwner.getPets(player)) //for each wolf pet the player has
                                    w.setTarget(newTarget); //set the target of the wolf to the damaged target
                            }
                            else //target is a regular entity (sign/painting/etc)
                            {
                                return; //ignore
                            }
                        }
                        else //shooter isn't in an arena
                        {
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