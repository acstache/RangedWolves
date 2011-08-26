package com.ACStache.RangedWolves;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTameEvent;

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
                    EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent)event; //get the damaged by entity event
                    Entity cause = event2.getDamager(); //get the damaging entity of the event
                    if(cause instanceof Projectile) //if the entity is a projectile
                    {
                        Projectile proj = (Projectile)cause; //cast it to a projectile
                        if(proj.getShooter() instanceof Player) //if the shooter is a player
                        {
                            Player player = (Player)proj.getShooter(); //get the shooter of the projectile
                            if(RWArenaChecker.isPlayerInArena(player)) //shooter is in an arena match
                            {
                                if(!(RangedWolvesOwner.getPets(player) == null)) //if the shooter has pets
                                {
                                    if(target instanceof LivingEntity) //if the target is a living entity
                                    {
                                        LivingEntity newTarget = (LivingEntity)target; //make it a living entity
                                        for(Wolf w : RangedWolvesOwner.getPets(player)) //for each wolf pet the player has
                                        {
                                            if(!w.isSitting()) //if the wolf is  sitting
                                            {
                                                w.setSitting(false); //stand the wolf up
                                                w.setTarget(newTarget); //set the target of the wolf to the damaged target
                                            }
                                            else //if the wolf is not sitting
                                                w.setTarget(newTarget); //set the target of the wolf to the damaged target
                                        }
                                    }
                                    else //target is a regular entity (sign/painting/etc)
                                        return; //ignore
                                }
                                else //if the shooter has no pets
                                    return; //ignore
                            }
                            else //shooter isn't in an arena
                            {
                                if(!(RangedWolvesOwner.getPets(player) == null)) //if the shooter has pets
                                {
                                    if(target instanceof LivingEntity) //if the target is a living entity
                                    {
                                        LivingEntity newTarget = (LivingEntity)target; //make it a living entity
                                        for(Wolf w : RangedWolvesOwner.getPets(player)) //for each wolf pet the player has
                                            if(!w.isSitting()) //if the wolf is not sitting
                                                w.setTarget(newTarget); //set the target of the wolf to the damaged target
                                    }
                                    else //target is a regular entity (sign/painting/etc)
                                        return; //ignore
                                }
                                else //if the shooter has no pets
                                    return; //ignore
                            }
                        }
                        else //if the shooter is a monster (skeleton/boss)
                            return; //ignore
                    }
                    else //entity is not a projectile
                        return; //ignore
                }
                else //if it's anything other than a projectile
                    return; //ignore
            }
            else //entity hit is a player
                return; //ignore
        }
        else //damage event is cancelled
            return; //ignore
    }
    
    public void onEntityTame(EntityTameEvent event)
    {
        Entity pet = event.getEntity(); //get tamed entity
        if(pet instanceof Wolf) //if it's a wolf
        {
            Wolf wolf = (Wolf)pet; //make it a wolf
            RangedWolvesOwner.addWolf((Player)(wolf.getOwner()), wolf); //add wolf to player who tamed it
        }
    }
    
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity dead = event.getEntity(); //get entity that just died
        if(dead instanceof Wolf) //if it's a wolf
        {
            Wolf wolf = (Wolf)dead; //make it a wolf
            Player tamer = (Player)wolf.getOwner(); //get it's owner
            if(tamer != null) //if there was a tamer (not null)
                if(!(RWArenaChecker.isPlayerInArena(tamer))) //if the tamer is not in an arena
                    RangedWolvesOwner.removeWolf(tamer, wolf); //remove wolf from player's list
        }
    }
    
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        Entity spawn = event.getEntity(); //get entity that just spawned
        if(spawn instanceof Wolf) //if it's a wolf
        {
            Wolf wolf = (Wolf)spawn; //make it a wolf
            Player tamer = (Player)wolf.getOwner(); //get it's owner
            if(tamer != null) //if there was an owner
                if(!(RWArenaChecker.isPlayerInArena(tamer))) //if the tamer is not in an arena
                    RangedWolvesOwner.addWolf(tamer, wolf); //add wolf to the player who spawned it
        }
    }
}