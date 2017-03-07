package com.gmail.berndivader.mmArmorStandAnimator;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;

public class mmMythicMobsEvents implements Listener {

	public mmMythicMobsEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(this, main.inst());
	}
	
	@EventHandler
	public void mmMythicMobsMechanicsLoad(MythicMechanicLoadEvent e) {
		if (e.getMechanicName().equals("ASANIMATE")) {
			SkillMechanic skill = new mmArmorStandAnimateMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().equals("ASINIT")) {
			SkillMechanic skill = new mmArmorStandInitMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().equals("ASUNLOAD")) {
			SkillMechanic skill = new mmArmorStandUnloadMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().equals("ASPAUSE")) {
			SkillMechanic skill = new mmArmorStandPauseMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().equals("ASRUN")) {
			SkillMechanic skill = new mmArmorStandRunMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		}
	}
	
	@EventHandler
	public void mmMythicMobsConditionsLoad(MythicConditionLoadEvent e) {
		if (e.getConditionName().equals("ANIMATESTANDPAUSED")) {
			SkillCondition condition = new mmArmorStandPauseCondition(e.getConditionName(),e.getConfig());
			e.register(condition);
		} else if (e.getConditionName().equals("ISANIMATESTAND")) {
			SkillCondition condition = new mmArmorStandIsAnimator(e.getConditionName(),e.getConfig());
			e.register(condition);
		}
	}

	@EventHandler
	public void mmArmorStandAnimatorDestroy(EntityDamageByEntityEvent e) {
		if (e.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
			ArmorStand as=(ArmorStand)e.getEntity();
			Entity entity = e.getEntity();
			Bukkit.getScheduler().scheduleSyncDelayedTask(main.inst(), new Runnable() {
	            @Override
	            public void run() {
	    			if (entity.isDead()) {
	    				Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator();
	    				ArmorStandAnimator asa = null;
	    				while (it.hasNext()) {
	    					asa = it.next();
	    					ArmorStand a = asa.getArmorStand();
	    					if (as.getUniqueId()==a.getUniqueId()) {
	    						asa.stop();
	    						asa.remove();
	    						break;
	    					}
	    				}
	    			}
	            }
	        });			
		}
	}
}
