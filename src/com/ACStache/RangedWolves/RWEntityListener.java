package com.ACStache.RangedWolves;

import org.bukkit.ChatColor;
import org.bukkit.World;
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

import com.garbagemule.MobArena.Arena;

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
                if(target instanceof LivingEntity) //if the entity is a living entity
                {
                    LivingEntity newTarget = (LivingEntity)target; //make a new living entity (for later)
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
                                    Arena arena = RangedWolves.am.getArenaWithPlayer(player); //get the arena the player is in
                                    if(RWConfig.RWinArena(arena)) //if RW is allowed in the arena
                                    {
                                        if(newTarget instanceof Wolf) //if the target is a wolf
                                        {
                                            Wolf wolf = (Wolf)newTarget; //set it as a wolf
                                            if(RangedWolvesOwner.checkWolf(wolf)) //if it has an owner
                                                event.setCancelled(true); //cancel event
                                            else //wolf is not a pet
                                                if(RangedWolvesOwner.getPets(player) != null) //if player has pets
                                                    for(Wolf w : RangedWolvesOwner.getPets(player)) //loop through the player's pets
                                                    {
                                                        if(w.isSitting()) //if the wolf is sitting
                                                        {
                                                            w.setSitting(false); //get it up
                                                            w.setTarget(newTarget); //set it's target
                                                        }
                                                        else //else the wolf is not sitting
                                                            w.setTarget(newTarget); //set it's target
                                                    }
                                        }
                                        else //else the target is not a wolf (or player)
                                            if(RangedWolvesOwner.getPets(player) != null) //if player has pets
                                                for(Wolf w : RangedWolvesOwner.getPets(player)) //loop through the player's pets
                                                {
                                                    if(w.isSitting()) //if the wolf is sitting
                                                    {
                                                        w.setSitting(false); //get it up
                                                        w.setTarget(newTarget); //set it's target
                                                    }
                                                    else //else the wolf is not sitting
                                                        w.setTarget(newTarget); //set it's target
                                                }
                                    }
                                }
                                else //shooter is not in an arena match
                                {
                                    World world = player.getWorld(); //get the player's world
                                    if(RWConfig.RWinWorld(world)) //if RW allowed in world
                                    {
                                        if(newTarget instanceof Wolf) //if the target is a wolf
                                        {
                                            Wolf wolf = (Wolf)newTarget; //set it as a wolf
                                            if(RangedWolvesOwner.checkWolf(wolf)) //if it has an owner
                                                event.setCancelled(true); //cancel event
                                            else //wolf is not a pet
                                                if(RangedWolvesOwner.getPets(player) != null) //if player has pets
                                                    for(Wolf w : RangedWolvesOwner.getPets(player)) //loop through the player's pets
                                                        if(!w.isSitting()) //if the wolf is not sitting
                                                            w.setTarget(newTarget); //set it's target
                                        }
                                        else if(newTarget instanceof Player)
                                        {
                                            //TODO check server for PVP, possibly have wolves attack players
                                            //TODO rearrange some logic/checks to incorporate server PvP
                                            //TODO remove initial entity check
                                            //TODO check after determining possible attackable target
                                        }
                                        else //if the target is not a wolf (or player)
                                            if(RangedWolvesOwner.getPets(player) != null) //if player has pets
                                                for(Wolf w : RangedWolvesOwner.getPets(player)) //loop through the player's pets
                                                    if(!w.isSitting()) //if the wolf is not sitting
                                                        w.setTarget(newTarget); //set it's target
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void onEntityTame(EntityTameEvent event)
    {
        Entity pet = event.getEntity(); //get tamed entity
        if(pet instanceof Wolf) //if it's a wolf
        {
            Wolf wolf = (Wolf)pet; //make it a wolf
            RangedWolvesOwner.addWolf((Player)(wolf.getOwner()), wolf); //add wolf to player who tamed it
            ((Player)(wolf.getOwner())).sendMessage(ChatColor.AQUA + "RW: You've tamed a wolf");
        }
    }
    
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity dead = event.getEntity(); //get entity that just died
        if(dead instanceof Wolf) //if it's a wolf
        {
            Wolf wolf = (Wolf)dead; //make it a wolf
            Player owner = (Player)wolf.getOwner(); //get it's owner
            if(owner != null) //if there was a owner
                if(!(RWArenaChecker.isPlayerInArena(owner))) //if the owner is not in an arena
                    if(RangedWolvesOwner.checkWolf(wolf)) //if the wolf is associated with it's owner
                    {
                        RangedWolvesOwner.removeWolf(owner, wolf); //remove wolf from owner's list
                        owner.sendMessage(ChatColor.AQUA + "RW: You've lost a wolf");
                    }
        }
    }
    
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        Entity spawn = event.getEntity(); //get entity that just spawned
        if(spawn instanceof Wolf) //if it's a wolf
        {
            Wolf wolf = (Wolf)spawn; //make it a wolf
            Player owner = (Player)wolf.getOwner(); //get it's owner
            if(owner != null) //if there was an owner
                if(!(RWArenaChecker.isPlayerInArena(owner))) //if the owner is not in an arena
                {
                    RangedWolvesOwner.addWolf(owner, wolf); //add wolf to the owner
                    owner.sendMessage(ChatColor.AQUA + "RW: You've been given a wolf");
                }
        }
    }
}