package com.ACStache.RangedWolves;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
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
        //if the event is cancelled, exit
        if(event.isCancelled()) {return;}
        
        //if target entity isn't a living entity, exit
        Entity target = event.getEntity();
        if(!(target instanceof LivingEntity)) {return;}

        //make target a LivingEntity for later on
        LivingEntity newTarget = (LivingEntity)target;
        
        //if damage cause isn't a projectile, exit
        DamageCause damager = target.getLastDamageCause().getCause();
        if(damager != DamageCause.PROJECTILE) {return;}
        
        //if damager wasn't a projectile, exit
        Entity cause = ((EntityDamageByEntityEvent)event).getDamager();
        if(!(cause instanceof Projectile)) {return;}
        
        //if projectile shooter isn't a player, exit
        Projectile proj = (Projectile)cause;
        if(!(proj.getShooter() instanceof Player)) {return;}
        
        //3 checks for projectiles from config
        if(proj instanceof Arrow && !RWConfig.RWProj("Arrow")) {return;}
        if(proj instanceof Egg && !RWConfig.RWProj("Egg")) {return;}
        if(proj instanceof Snowball && !RWConfig.RWProj("Snowball")) {return;}
        
        //get the shooter of the projectile
        Player player = (Player)proj.getShooter();
        
        //if shooter is in an arena match
        if(RWArenaChecker.isPlayerInArena(player))
        {
            //get the arena the player is in & the player's pets
            Arena arena = RangedWolves.am.getArenaWithPlayer(player);
            boolean arenaPvP = arena.isPvpEnabled();
            HashSet<Wolf> pets = (HashSet<Wolf>)RWOwner.getPets(player);
            
            //if RW is not allowed in the arena, exit
            if(!RWConfig.RWinArena(arena)) {return;}
            
            //if the target is a wolf
            if(newTarget instanceof Wolf)
            {
                //set it as a wolf
                Wolf wolf = (Wolf)newTarget;
                
                //if it has an owner
                if(RWOwner.checkArenaWolf(wolf))
                {
                    //if the player is the wolf's owner
                    if(player == (Player)wolf.getOwner())
                    {
                        event.setCancelled(true);
                    }
                    //if the wolf has an owner other than the player
                    else
                    {
                        //if arena pvp is enabled
                        if(arenaPvP)
                        {
                            //set wolf as your wolves' target
                            setArenaTarget(pets, newTarget);
                        }
                        //else if arena pvp is disabled
                        else
                        {
                            event.setCancelled(true);
                        }
                    }
                }
                //wolf is not a pet
                else
                {
                    setArenaTarget(pets, newTarget);
                }
            }
            //else if the target is a player
            else if(newTarget instanceof Player)
            {
                //if you manage to shoot yourself
                if(player == (Player)newTarget)
                {
                    event.setCancelled(true);
                }
                //if you shoot another player
                else
                {
                    //if arena pvp is enabled
                    if(arenaPvP)
                    {
                        //set player as your wolves' target
                        setArenaTarget(pets, newTarget);
                    }
                    //else if arena pvp is disabled
                    else
                    {
                        event.setCancelled(true);
                    }
                }
            }
            //else the target is not a wolf or player
            else
            {
                setArenaTarget(pets, newTarget);
            }
        }
        //if shooter is not in an arena match
        else
        {
            //get the player's pets, world, and world's pvp 
            World world = player.getWorld();
            HashSet<Wolf> pets = (HashSet<Wolf>)RWOwner.getPets(player);
            boolean worldPvP = world.getPVP();
            
            //if RW not allowed in world, exit
            if(!RWConfig.RWinWorld(world)) {return;}
            
            //if the target is a wolf
            if(newTarget instanceof Wolf)
            {
                //set it as a wolf
                Wolf wolf = (Wolf)newTarget;
                
                //if it has an owner
                if(RWOwner.checkWorldWolf(wolf))
                {
                    //if world pvp is enabled
                    if(worldPvP)
                    {
                        //wolf is the shooter's pet
                        if(player.equals(((Player)wolf.getOwner()))) 
                        {
                            event.setCancelled(true);
                        }
                        //wolf is not the shooter's pet
                        else
                        {
                            setWorldTarget(pets, newTarget);
                        }
                    }
                    //else world pvp is disabled
                    else
                    {
                        event.setCancelled(true);
                    }
                }
                //wolf is not a pet
                else
                {
                    setWorldTarget(pets, newTarget);
                }
            }
            //if the target is a player
            else if(newTarget instanceof Player)
            {
                //if world pvp is enabled
                if(worldPvP)
                {
                    setWorldTarget(pets, newTarget);
                }
            }
            //if the target is not a wolf or player
            else
            {
                setWorldTarget(pets, newTarget);
            }
        }
    }
    
    public void setArenaTarget(HashSet<Wolf> pets, LivingEntity target)
    {
        //if player doesn't have pets, exit
        if(pets == null) {return;}
        
        //loop through the player's pets
        for(Wolf w : pets)
        {
            //if the wolf is sitting
            if(w.isSitting())
            {
                //get it up & set it's target
                w.setSitting(false);
                w.setTarget(target);
            }
            //else the wolf is not sitting
            else
            {
                //set it's target
                w.setTarget(target);
            }
        }
    }
    public void setWorldTarget(HashSet<Wolf> pets, LivingEntity target)
    {
        //if player doesn't have pets, exit
        if(pets == null) {return;}
    
        //loop through the player's pets
        for(Wolf w : pets)
        {
            //if the wolf is not sitting
            if(!w.isSitting())
            {
                //set it's target
                w.setTarget(target);
            }
        }
    }
    
    public void onEntityTame(EntityTameEvent event)
    {
        Entity pet = event.getEntity();
        if(pet instanceof Wolf)
        {
            Wolf wolf = (Wolf)pet;
            RWOwner.addWolf((Player)(wolf.getOwner()), wolf);
            ((Player)(wolf.getOwner())).sendMessage(ChatColor.AQUA + "RW: You've tamed a wolf");
        }
    }
    
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity dead = event.getEntity();
        if(dead instanceof Wolf)
        {
            Wolf wolf = (Wolf)dead;
            Player owner = (Player)wolf.getOwner();
            if(owner != null)
            {
                if(RangedWolves.am.getArenaWithPet(wolf) == null) //if the wolf that died is not in an arena
                {
                    if(RWOwner.checkWorldWolf(wolf))
                    {
                        RWOwner.removeWolf(owner, wolf);
                        owner.sendMessage(ChatColor.AQUA + "RW: You've lost a wolf");
                    }
                }
            }
        }
    }
    
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        Entity spawn = event.getEntity();
        if(spawn instanceof Wolf)
        {
            Wolf wolf = (Wolf)spawn;
            Player owner = (Player)wolf.getOwner();
            if(owner != null) //if there is an owner
            {
                if(RangedWolves.am.getArenaWithPet(wolf) == null) //if the wolf isn't spawned in an arena
                {
                    if(!RWOwner.checkWorldWolf(wolf)) //if it's not already associated
                    {
                        RWOwner.addWolf(owner, wolf); //add it
                    }
                }
            }
        }
    }
}