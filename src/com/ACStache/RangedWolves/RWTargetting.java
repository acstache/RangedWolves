package com.ACStache.RangedWolves;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

/**
 * Currently unused, and mainly for trying to get a better targetting system in place for wolves to determine what creature to attack next
 * 
 * @author BSlater
 *
 */
public class RWTargetting
{
    private static HashMap<String, LinkedList<LivingEntity>> playerWorldTargets = new HashMap<String, LinkedList<LivingEntity>>();
    private static HashMap<String, LinkedList<LivingEntity>> playerArenaTargets = new HashMap<String, LinkedList<LivingEntity>>();
    
    /**
     * add a target to the Owner's pets Arena target queue
     * @param pets the pets of the Owner
     * @param target the Entity being attacked
     * @param owner the Owner of the pets
     */
    public static void addArenaTarget(HashSet<Wolf> pets, LivingEntity target, Player owner)
    {
        if(pets == null) {return;}
        if(owner.getLocation().distance(target.getLocation()) >= 16) {return;}
        
        if(playerArenaTargets.get(owner.getName()) == null)
        {
            playerArenaTargets.put(owner.getName(), new LinkedList<LivingEntity>());
            playerArenaTargets.get(owner.getName()).add(target);
        }
        else
        {
            if(!playerArenaTargets.get(owner.getName()).contains(target))
            {
                playerArenaTargets.get(owner.getName()).add(target);
            }
        }
        Target(pets, owner);
    }
    
    /**
     * add a target to the Owner's pets World target queue
     * @param pets the pets of the Owner
     * @param target the Entity being attacked
     * @param owner the Owner of the pets
     */
    public static void addWorldTarget(HashSet<Wolf> pets, LivingEntity target, Player owner)
    {
        if(pets == null) {return;}
        if(owner.getLocation().distance(target.getLocation()) >= 16) {return;}
    
        if(playerWorldTargets.get(owner.getName()) == null)
        {
            playerWorldTargets.put(owner.getName(), new LinkedList<LivingEntity>());
            playerWorldTargets.get(owner.getName()).add(target);
        }
        else
        {
            if(!playerWorldTargets.get(owner.getName()).contains(target))
            {
                playerWorldTargets.get(owner.getName()).add(target);
            }
        }
        Target(pets, owner);
    }

    /**
     * set the target of the Owner's pets (World or Arena)
     * @param pets the pets of the Owner
     * @param owner the Owner of the pets
     */
    public static void Target(HashSet<Wolf> pets, Player owner)
    {
        if(RWArenaChecker.isPlayerInArena(owner))
        {
            LinkedList<LivingEntity> pAT = playerArenaTargets.get(owner.getName());
            LivingEntity target = null;
            for(Iterator<LivingEntity> iter = pAT.iterator(); iter.hasNext(); target = iter.next())
            {
                if(target != null && !target.isDead())
                {
                    if(owner.getLocation().distance(target.getLocation()) >= 16)
                    {
                        iter.remove();
                    }
                    else
                    {
                        break;
                    }
                }
            }
            if(target == null) {return;}
            for(Wolf w : pets)
            {
                if(w.isSitting())
                {
                    w.setSitting(false);
                }
                if(w.getLocation().distance(target.getLocation()) < 16)
                {
                    w.setTarget(target);
                }
            }
        }
        else
        {
            LinkedList<LivingEntity> pWT = playerWorldTargets.get(owner.getName());
            LivingEntity target = null;
            for(Iterator<LivingEntity> iter = pWT.iterator(); iter.hasNext(); target = iter.next())
            {
                if(target != null && !target.isDead())
                {
                    if(owner.getLocation().distance(target.getLocation()) >= 16)
                    {
                        iter.remove();
                    }
                    else
                    {
                        break;
                    }
                }
            }
            if(target == null) {return;}
            for(Wolf w : pets)
            {
                if(!w.isSitting() && w.getLocation().distance(target.getLocation()) < 16)
                {
                    w.setTarget(target);
                }
            }
        }
    }
    
    /**
     * remove the target from the list of targets if it dies
     * @param target the LivingEntity that died
     */
    public static void removeTarget(LivingEntity target)
    {
        if(RWArenaChecker.isMonsterInArena(target))
            for(LinkedList<LivingEntity> l1 : playerArenaTargets.values())
                if(l1.contains(target))
                    l1.remove(target);
        else
            for(LinkedList<LivingEntity> l2 : playerWorldTargets.values())
                if(l2.contains(target))
                    l2.remove(target);
    }
    
    /**
     * clear a player's targetted entities if he/she dies
     * @param player the player dying
     */
    public static void clearTargets(Player player)
    {
        if(playerWorldTargets.containsKey(player.getName()))
            playerWorldTargets.get(player.getName()).clear();
    }

    /**
     * clear a player's targetted entities if he/she dies or leaves the arena
     * @param player the player dying/leaving
     */
    public static void clearArenaTargets(Player player)
    {
        if(playerArenaTargets.containsKey(player.getName()))
            playerArenaTargets.get(player.getName()).clear();
    }
}