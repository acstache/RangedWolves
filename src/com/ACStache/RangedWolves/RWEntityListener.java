package com.ACStache.RangedWolves;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.plugin.Plugin;

import com.garbagemule.MobArena.Arena;

public class RWEntityListener extends EntityListener
{
    final RangedWolves plugin;
    private Plugin mobArena;
    
    public RWEntityListener(RangedWolves instance)
    {
        plugin = instance;
        mobArena = Bukkit.getPluginManager().getPlugin("MobArena");
    }
    
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(event.isCancelled()) {return;}
        
        Entity target = event.getEntity();
        if(!(target instanceof LivingEntity)) {return;}

        LivingEntity newTarget = (LivingEntity)target;
        
        DamageCause damager = target.getLastDamageCause().getCause();
        if(damager != DamageCause.PROJECTILE) {return;}
        
        Entity cause = ((EntityDamageByEntityEvent)event).getDamager();
        if(!(cause instanceof Projectile)) {return;}
        
        Projectile proj = (Projectile)cause;
        if(!(proj.getShooter() instanceof Player)) {return;}
    
        if(proj instanceof Arrow && !RWConfig.RWProj("Arrow")) {return;}
        if(proj instanceof Egg && !RWConfig.RWProj("Egg")) {return;}
        if(proj instanceof Fireball && !RWConfig.RWProj("Fireball")) {return;}
        if(proj instanceof SmallFireball && !RWConfig.RWProj("Small-Fireball")) {return;}
        if(proj instanceof Snowball && !RWConfig.RWProj("Snowball")) {return;}
        if(proj instanceof ThrownPotion && !RWConfig.RWProj("Potions")) {return;}
        
        Player player = (Player)proj.getShooter();
        
        if(RWArenaChecker.isPlayerInArena(player))
        {
            if(!player.hasPermission("RangedWolves.Arenas")) {return;}
            
            Arena arena = RangedWolves.am.getArenaWithPlayer(player);
            boolean arenaPvP = arena.isPvpEnabled();
            HashSet<Wolf> pets = (HashSet<Wolf>)RWOwner.getPets(player);
            
            if(!RWConfig.RWinArena(arena)) {return;}
            
            if(newTarget instanceof Wolf)
            {
                Wolf wolf = (Wolf)newTarget;
                
                if(RWOwner.checkArenaWolf(wolf))
                {
                    if(player == (Player)wolf.getOwner())
                    {
                        event.setCancelled(true);
                    }
                    else
                    {
                        if(arenaPvP)
                        {
                            setArenaTarget(pets, newTarget);
                        }
                        else
                        {
                            event.setCancelled(true);
                        }
                    }
                }
                else
                {
                    setArenaTarget(pets, newTarget);
                }
            }
            else if(newTarget instanceof Player)
            {
                if(!(player == (Player)newTarget))
                {
                    if(arenaPvP)
                    {
                        setArenaTarget(pets, newTarget);
                    }
                    else
                    {
                        event.setCancelled(true);
                    }
                }
            }
            else
            {
                setArenaTarget(pets, newTarget);
            }
        }
        else
        {
            if(!player.hasPermission("RangedWolves.Worlds")) {return;}
            
            World world = player.getWorld();
            HashSet<Wolf> pets = (HashSet<Wolf>)RWOwner.getPets(player);
            boolean worldPvP = world.getPVP();
            
            if(!RWConfig.RWinWorld(world)) {return;}
            
            if(newTarget instanceof Wolf)
            {
                Wolf wolf = (Wolf)newTarget;
                
                if(RWOwner.checkWorldWolf(wolf))
                {
                    if(worldPvP)
                    {
                        if(player.equals(((Player)wolf.getOwner()))) 
                        {
                            event.setCancelled(true);
                        }
                        else
                        {
                            setWorldTarget(pets, newTarget);
                        }
                    }
                    else
                    {
                        event.setCancelled(true);
                    }
                }
                else
                {
                    setWorldTarget(pets, newTarget);
                }
            }
            else if(newTarget instanceof Player)
            {
                if(!(player == (Player)newTarget))
                {
                    if(worldPvP)
                    {
                        setWorldTarget(pets, newTarget);
                    }
                }
            }
            else
            {
                setWorldTarget(pets, newTarget);
            }
        }
    }
    
    public void onEntityTame(EntityTameEvent event)
    {
        Entity pet = event.getEntity();
        if(event.getOwner() instanceof Player)
        {
            Player owner = (Player)event.getOwner();
            if(pet instanceof Wolf)
            {
                Wolf wolf = (Wolf)pet;
                RWOwner.addWolf(owner.getName(), wolf);
                owner.sendMessage(ChatColor.AQUA + "RW: You've tamed a wolf");
            }
        }
    }
    
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity dead = event.getEntity();
        if(dead instanceof Wolf)
        {
            Wolf wolf = (Wolf)dead;
            if(mobArena != null && mobArena.isEnabled())
            {
                if(RangedWolves.am.getArenaWithPet(wolf) != null) {return;}
            }
            if(!RWOwner.checkWorldWolf(wolf)) {return;}
            
            if(wolf.getOwner() instanceof OfflinePlayer)
            {
                OfflinePlayer offPlayer = (OfflinePlayer)wolf.getOwner();
                if(offPlayer != null)
                {
                    RWOwner.removeWolf(offPlayer.getName(), wolf);
                }
            }
            else
            {
                Player owner = (Player)wolf.getOwner();
                if(owner != null)
                {
                    RWOwner.removeWolf(owner.getName(), wolf);
                    owner.sendMessage(ChatColor.AQUA + "RW: You've lost a wolf");
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
            if(mobArena != null && mobArena.isEnabled())
            {
                if(RangedWolves.am.getArenaWithPet(wolf) != null) {return;}
            }
            if(wolf.isTamed())
            {
                Player owner = (Player)wolf.getOwner();
                if(owner != null)
                {
                    if(RWOwner.checkWorldWolf(wolf)) {return;}
                    
                    RWOwner.addWolf(owner.getName(), wolf);
                }
            }
        }
    }
    
    /**
     * Set the Wolves target in an Arena
     * @param pets the pets of the Tamer
     * @param target the Entity being attacked
     */
    private void setArenaTarget(HashSet<Wolf> pets, LivingEntity target)
    {
        if(pets == null) {return;}
        
        for(Wolf w : pets)
        {
            if(w.isSitting())
            {
                w.setSitting(false);
            }
            w.setTarget(target);
        }
    }
    
    /**
     * Set the Wolves target in a World
     * @param pets the pets of the Tamer
     * @param target the Entity being attacked
     */
    private void setWorldTarget(HashSet<Wolf> pets, LivingEntity target)
    {
        if(pets == null) {return;}
    
        for(Wolf w : pets)
        {
            if(!w.isSitting())
            {
                w.setTarget(target);
            }
        }
    }
}