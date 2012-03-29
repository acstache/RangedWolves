package com.ACStache.RangedWolves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.events.ArenaEndEvent;
import com.garbagemule.MobArena.events.ArenaStartEvent;

public class RWListener implements Listener
{
    private static HashMap<Projectile, String> projectiles = new HashMap<Projectile, String>();
    private HashSet<World> worlds = new HashSet<World>();
    private RangedWolves plugin;

    public RWListener(RangedWolves rangedWolves)
    {
        plugin = rangedWolves;
    }

    /*
     * Entity Events
     */
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        Entity damagee = event.getEntity();
        if(!(damagee instanceof LivingEntity)) {return;}
        
        Entity damager = event.getDamager();
        if(!(damager instanceof Projectile)) {return;}

        LivingEntity target = (LivingEntity)damagee;
        
        Projectile proj = (Projectile)damager;
        if(!isProjBeingTracked(proj)) {return;}
        Player player = plugin.getServer().getPlayer(removeProjectile(proj));
        if(player == null) {return;}
        if(RWOwner.getPets(player) == null) {return;}
        HashSet<Wolf> pets = (HashSet<Wolf>)RWOwner.getPets(player);
        
        if(target instanceof Creeper && !RWConfig.RWCreepers()) {
            if(!player.hasPermission("RangedWolves.Creep")) {return;}
        }
        
        if(RWArenaChecker.isPlayerInArena(player))
            onEDBEinArena(event, target, player, pets);
        else
            onEDBEinWorld(event, target, player, pets);
    }
    
    private void onEDBEinArena(EntityDamageByEntityEvent event, LivingEntity target, Player player, HashSet<Wolf> pets)
    {
        if(!player.hasPermission("RangedWolves.Arenas")) {return;}
        
        Arena arena = RangedWolves.getAM().getArenaWithPlayer(player);
        boolean arenaPvP = arena.getSettings().getBoolean("pvp-enabled");
        
        if(!RWConfig.RWinArena(arena)) {return;}
        
        if(target instanceof Wolf) {
            Wolf wolf = (Wolf)target;
            
            if(RWOwner.checkArenaWolf(wolf)) {
                if(!arenaPvP) {return;}
                if(wolf.getOwner() instanceof Player) {
                    if(player.equals((Player)wolf.getOwner())) {
                        event.setCancelled(true);
                    }
                    else {
                        setTarget(pets, target, true);
                    }
                }
                else {
                    setTarget(pets, target, true);
                }
            }
            else {
                setTarget(pets, target, true);
            }
        }
        else if(target instanceof Player) {
            if(player.equals((Player)target)) {return;}
            if(!arenaPvP) {return;}
            setTarget(pets, target, true);
        }
        else {
            setTarget(pets, target, true);
        }
    }
    
    private void onEDBEinWorld(EntityDamageByEntityEvent event, LivingEntity target, Player player, HashSet<Wolf> pets)
    {
        if(!player.hasPermission("RangedWolves.Worlds")) {return;}
            
        World world = player.getWorld();
        boolean worldPvP = world.getPVP();
        
        if(!RWConfig.RWinWorld(world)) {return;}
        
        if(target instanceof Wolf) {
            Wolf wolf = (Wolf)target;
            
            if(RWOwner.checkWorldWolf(wolf)) {
                if(!worldPvP) {return;}
                if(wolf.getOwner() instanceof Player) {
                    if(player.equals((Player)wolf.getOwner())) {
                        event.setCancelled(true);
                    }
                    else {
                        setTarget(pets, target, false);
                    }
                }
                else {
                    setTarget(pets, target, false);
                }
            }
            else {
                setTarget(pets, target, false);
            }
        }
        else if(target instanceof Player) {
            if(player.equals((Player)target)) {return;}
            if(!worldPvP) {return;}
            setTarget(pets, target, false);
        }
        else {
            setTarget(pets, target, false);
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event)
    {
        if(!(event.getEntity() instanceof LivingEntity)) {return;}
        LivingEntity dead = (LivingEntity)event.getEntity();
        if(!(dead instanceof Wolf)) {return;}
        Wolf wolf = (Wolf)dead;
        
        if(RangedWolves.getMA() != null && RangedWolves.getMA().isEnabled()) {
            if(RangedWolves.getAM().getArenaWithPet(wolf) != null) {return;}
        }
        if(!RWOwner.checkWorldWolf(wolf)) {return;}
        if(wolf.getOwner() == null) {return;}
        
        if(wolf.getOwner() instanceof OfflinePlayer) {
            OfflinePlayer offPlayer = (OfflinePlayer)wolf.getOwner();
            RWOwner.removeWolf(offPlayer.getName(), wolf);
        }
        else {
            Player owner = (Player)wolf.getOwner();
            RWOwner.removeWolf(owner.getName(), wolf);
            RangedWolves.printToPlayer(owner, "You've lost a wolf");
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event)
    {
        Entity pet = event.getEntity();
        if(event.getOwner() instanceof Player) {
            Player owner = (Player)event.getOwner();
            if(pet instanceof Wolf) {
                if(RWOwner.getPetAmount(owner) >= RWConfig.RWMaxWolves()) {
                    if(!owner.hasPermission("RangedWolves.Unlimited")) {
                        RangedWolves.printToPlayer(owner, "You don't have permission to have more than " + RWConfig.RWMaxWolves() + " wolves");
                        event.setCancelled(true);
                        return;
                    }
                }
                Wolf wolf = (Wolf)pet;
                RWOwner.addWolf(owner.getName(), wolf);
                RangedWolves.printToPlayer(owner, "You've tamed a wolf");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        Entity spawn = event.getEntity();
        if(!(spawn instanceof Wolf)) {return;}
        Wolf wolf = (Wolf)spawn;
        if(!wolf.isTamed()) {return;}
        
        if(RangedWolves.getMA() != null && RangedWolves.getMA().isEnabled()) {
            if(RangedWolves.getAM().getArenaWithPet(wolf) != null) {return;}
        }
        
        Player owner = (Player)wolf.getOwner();
        if(owner != null) {
            if(RWOwner.checkWorldWolf(wolf)) {return;}
            
            if(RWOwner.getPetAmount(owner) >= RWConfig.RWMaxWolves()) {
                if(!owner.hasPermission("RangedWolves.Unlimited")) {
                    wolf.setTamed(false);
                    return;
                }
            }
            RWOwner.addWolf(owner.getName(), wolf);
        }
    }
    
    
    /*
     * Projectile stuff
     */
    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event)
    {
        Projectile proj = event.getEntity();
        
        if(proj instanceof Arrow && !RWConfig.RWProj("Arrow")) {return;}
        if(proj instanceof Egg && !RWConfig.RWProj("Egg")) {return;}
        if(proj instanceof Fireball && !RWConfig.RWProj("Fireball")) {return;}
        if(proj instanceof SmallFireball && !RWConfig.RWProj("Small-Fireball")) {return;}
        if(proj instanceof Snowball && !RWConfig.RWProj("Snowball")) {return;}
        if(proj instanceof ThrownPotion && !RWConfig.RWProj("Potions")) {return;}
        
        if(proj.getShooter() instanceof Player)
            addProjectile(proj, ((Player)proj.getShooter()).getName());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event)
    {
        ThrownPotion potion = event.getPotion();
        if(!isProjBeingTracked((Projectile)potion)) {return;}
        Player player = plugin.getServer().getPlayer(removeProjectile((Projectile)potion));
        if(RWOwner.getPets(player) == null) {return;}
        HashSet<Wolf> pets = (HashSet<Wolf>)RWOwner.getPets(player);
        
        Arena arena = RWArenaChecker.getArenaAtLocation(potion.getLocation());
        boolean inArena = (arena == null) ? false : true;
        
        HashSet<LivingEntity> affected = (HashSet<LivingEntity>)event.getAffectedEntities();
        ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>(affected.size());
        LivingEntity target = null;
        
        for(LivingEntity le : affected) {
            if(!inArena) {
                if(le instanceof Player && player.getWorld().getPVP() && !le.equals(player))
                    targets.add(le);
                else if(!(le instanceof Player))
                    targets.add(le);
            }
            else {
                if(le instanceof Player && arena.getSettings().getBoolean("pvp-enabled") && !le.equals(player))
                    targets.add(le);
                else if(!(le instanceof Player))
                    targets.add(le);
            }
        }
        if(targets.isEmpty()) {return;}

        Random rand = new Random();
        target = targets.get(rand.nextInt(targets.size()));
        setTarget(pets, target, inArena);
    }
    
    
    /*
     * Player Events
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        World world = event.getPlayer().getWorld();
        if(worlds.contains(world)) {return;}

        for(LivingEntity e : world.getLivingEntities()) {
            if(!(e instanceof Wolf)) {continue;}
            Wolf wolf = (Wolf)e;
            if(wolf.getOwner() == null) {continue;}
            
            if(wolf.getOwner() instanceof OfflinePlayer) {
                OfflinePlayer offPlayer = (OfflinePlayer)wolf.getOwner();
                if(RWOwner.getPetAmount(offPlayer) >= RWConfig.RWMaxWolves()) {continue;}
                RWOwner.addWolf(offPlayer.getName(), wolf);
            }
            else {
                Player owner = (Player)wolf.getOwner();
                if(RWOwner.getPetAmount(owner) >= RWConfig.RWMaxWolves())
                    if(!owner.hasPermission("RangedWolves.Unlimited")) {continue;}
                
                RWOwner.addWolf(owner.getName(), wolf);
            }
        }
        worlds.add(world);
    }
    
    
    /*
     * MobArena Events
     */
    @EventHandler
    public void onArenaStart(ArenaStartEvent event)
    {
        final Arena arena = event.getArena();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                HashSet<Wolf> pets = (HashSet<Wolf>)arena.getMonsterManager().getPets();
                if(!(pets == null))
                    for (Wolf w : pets)
                        RWOwner.addWolf(arena, ((Player)w.getOwner()).getName(), w);
            }
        }, 20);
    }
    
    @EventHandler
    public void onArenaEnd(ArenaEndEvent event)
    {
        RWOwner.clearWolves(event.getArena());
    }
    
    
    /*
     * Projectile Tracking
     */
    private void addProjectile(Projectile proj, String pName)
    {
        if(!isProjBeingTracked(proj))
            projectiles.put(proj, pName);
    }
    
    private String removeProjectile(Projectile proj)
    {
        return projectiles.remove(proj);
    }
    
    private boolean isProjBeingTracked(Projectile proj)
    {
        return projectiles.containsKey(proj);
    }
    
    public static void clearProjectiles()
    {
        projectiles.clear();
    }
    
    /*
     * Setting Targets
     */
    private void setTarget(HashSet<Wolf> pets, LivingEntity target, boolean inArena)
    {
        for(Wolf w : pets) {
            if(w.isSitting()) {
                if(inArena)
                    w.setSitting(false);
                else
                    continue;
            }
            
            EntityTargetEvent ete = new EntityTargetEvent(w, target, TargetReason.OWNER_ATTACKED_TARGET);
            plugin.getServer().getPluginManager().callEvent(ete);
            if(!ete.isCancelled())
                w.setTarget(target);
        }
    }
}