package com.ACStache.RangedWolves;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
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
    private Random r = new Random();
    private int skeleChance, skeleMaxPets;
    
    public RWEntityListener(RangedWolves instance)
    {
        plugin = instance;
        mobArena = Bukkit.getPluginManager().getPlugin("MobArena");
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
        
        //if projectile shooter isn't a player
        Projectile proj = (Projectile)cause;
        if(!(proj.getShooter() instanceof Player))
        {
            //if it's not a Skeleton, ignore
            if(!(proj.getShooter() instanceof Skeleton)) {return;}
            Skeleton skele = (Skeleton)proj.getShooter();
            
            //if Skeleton Tamers aren't enabled, ignore
            if(!RWConfig.getSkeleEnabled()) {return;}
            
            //get the pets of the Skeleton
            HashSet<Wolf> pets = (HashSet<Wolf>)RWOwner.getPets(skele);
            
            //if the Skeleton is in an Arena
            if(RWArenaChecker.isMonsterInArena(skele))
            {
                //get the arena the skele is in & the skele's pets
                Arena arena = RangedWolves.am.getArenaWithMonster(skele);
                
                //if Ranged Wolves isn't allowed in the arena, ignore
                if(!RWConfig.RWinArena(arena)) {return;}
                
                //if Skeleton Tamers aren't allowed in Mob Arena, ignore
                if(!RWConfig.getSkeleInMobArena()) {return;}
                
                if(newTarget instanceof Player)
                {
                    setArenaTarget(pets, newTarget);
                }
                else if(newTarget instanceof Wolf)
                {
                    if(RWOwner.checkArenaWolf((Wolf)newTarget))
                    {
                        setArenaTarget(pets, newTarget);
                    }
                }
            }
            //if the Skeleton is NOT in an Arena
            else
            {
                if(newTarget instanceof Player)
                {
                    setWorldTarget(pets, newTarget);
                }
                else if(newTarget instanceof Wolf)
                {
                    if(RWOwner.checkWorldWolf((Wolf)newTarget))
                    {
                        setWorldTarget(pets, newTarget);
                    }
                }
            }
        }
        //if the shooter is a player
        else
        {
            //4 checks for projectiles from config
            if(proj instanceof Arrow && !RWConfig.RWProj("Arrow")) {return;}
            if(proj instanceof Egg && !RWConfig.RWProj("Egg")) {return;}
            if(proj instanceof Fireball && !RWConfig.RWProj("Fireball")) {return;}
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
            //get the dead wolf
            Wolf wolf = (Wolf)dead;
            //if the wolf that died is in an arena, ignore it
            if(mobArena != null && mobArena.isEnabled())
            {
                if(RangedWolves.am.getArenaWithPet(wolf) != null) {return;}
            }
            //if the wolf isn't attached to a player, check it versus Skeleton pets
            if(!RWOwner.checkWorldWolf(wolf))
            {
                //if it's not attached to a Skeleton, ignore it
                if(!RWOwner.checkSkeleWolf(wolf)) {return;}
                
                RWOwner.removeWolf(wolf);
            }
            
            if(wolf.getOwner() instanceof OfflinePlayer) //if owner is offline
            {
                OfflinePlayer offPlayer = (OfflinePlayer)wolf.getOwner();
                if(offPlayer != null)
                {
                    RWOwner.removeWolf(offPlayer.getName(), wolf);
                }
            }
            else //if owner is online
            {
                Player owner = (Player)wolf.getOwner();
                if(owner != null)
                {
                    RWOwner.removeWolf(owner.getName(), wolf);
                    owner.sendMessage(ChatColor.AQUA + "RW: You've lost a wolf");
                }
            }
        }
        else if(dead instanceof Skeleton)
        {
            final Skeleton skele = (Skeleton)dead;
            final HashSet<Wolf> pets = (HashSet<Wolf>)RWOwner.getPets(skele);
            //if this particular Skeleton has no pets, ignore
            if(pets == null) {return;}
            
            //upon death, set his pets to Angry/Hostile
            //TODO - make this possibly configurable, get consensus from users
            for(Wolf w : pets)
            {
                w.setAngry(true);
            }
            
            //After 10 seconds (200 server ticks), angry wolves die.
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
            {
                public void run()
                {
                    for(Wolf w : pets)
                    {
                        if(!w.isDead())
                        {
                            w.setHealth(0);
                            RWOwner.removeWolf(w);
                        }
                    }
                    RWOwner.removeSkele(skele);
                }
            }, 200);
        }
    }
    
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        Entity spawn = event.getEntity();
        if(spawn instanceof Wolf)
        {
            //get the spawned wolf
            Wolf wolf = (Wolf)spawn;
            //if it was spawned in an arena, ignore it
            if(mobArena != null && mobArena.isEnabled())
            {
                if(RangedWolves.am.getArenaWithPet(wolf) != null) {return;}
            }
            //if the wolf isn't attached to a player, ignore it
            if(!RWOwner.checkWorldWolf(wolf)) {return;}
            
            Player owner = (Player)wolf.getOwner();
            //if there is an owner
            if(owner != null)
            {
                //add it
                RWOwner.addWolf(owner.getName(), wolf);
            }
        }
        else if(spawn instanceof Skeleton)
        {
            Skeleton skele = (Skeleton)spawn;
            
            //if RW isn't allowed in the world, ignore
            if(!RWConfig.RWinWorld(skele.getWorld())) {return;}
            
            //if Skeleton Tamers aren't enabled, ignore
            if(!RWConfig.getSkeleEnabled()) {return;}
            
            //if the Skeleton is in an Arena
            if(RWArenaChecker.isMonsterInArena(skele))
            {
                //if Skeleton Tamers aren't enabled in Mob Arena, ignore
                if(!RWConfig.getSkeleInMobArena()) {return;}
            }
            
            skeleChance = RWConfig.getSkeleChance();
            skeleMaxPets = RWConfig.getSkeleMaxPets();
            
            if(r.nextInt(100) + 1 <= skeleChance)
            {
                World world = skele.getWorld();
                int petNum = r.nextInt(skeleMaxPets) + 1;
                for(int i = 0; i < petNum; i++)
                {
                    Wolf wolf = (Wolf)world.spawnCreature(skele.getLocation(), CreatureType.WOLF);
                    wolf.setTamed(true);
                    wolf.setOwner((AnimalTamer)skele);
                    RWOwner.addWolf(skele, wolf);
                }
            }
        }
    }
    
    /**
     * Set the Wolves target in an Arena
     * @param pets the pets of the Tamer
     * @param target the Entity being attacked
     */
    public void setArenaTarget(HashSet<Wolf> pets, LivingEntity target)
    {
        //if player doesn't have pets, exit
        if(pets == null) {return;}
        
        //loop through the player's pets
        for(Wolf w : pets)
        {
            if(w.isSitting()) //if the wolf is sitting
            {
                w.setSitting(false); //get it up
            }
            //set it's target
            w.setTarget(target);
        }
    }
    
    /**
     * Set the Wolves target in a World
     * @param pets the pets of the Tamer
     * @param target the Entity being attacked
     */
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
}