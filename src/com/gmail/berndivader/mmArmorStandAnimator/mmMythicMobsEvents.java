package com.gmail.berndivader.mmArmorStandAnimator;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillTrigger;
import io.lumine.xikage.mythicmobs.skills.TriggeredSkill;

public class mmMythicMobsEvents implements Listener {

	public mmMythicMobsEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(this, main.inst());
	}
	
	@EventHandler
	public void mmMythicMobsMechanicsLoad(MythicMechanicLoadEvent e) {
		if (e.getMechanicName().toUpperCase().equals("ASANIMATE")) {
			SkillMechanic skill = new mmArmorStandAnimateMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().toUpperCase().equals("ASINIT")) {
			SkillMechanic skill = new mmArmorStandInitMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().toUpperCase().equals("ASUNLOAD")) {
			SkillMechanic skill = new mmArmorStandUnloadMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().toUpperCase().equals("ASPAUSE")) {
			SkillMechanic skill = new mmArmorStandPauseMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().toUpperCase().equals("ASRUN")) {
			SkillMechanic skill = new mmArmorStandRunMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		} else if (e.getMechanicName().toUpperCase().equals("ASCHANGE")) {
			SkillMechanic skill = new mmArmorStandChangeAnimMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		}
	}
	
	@EventHandler
	public void mmMythicMobsConditionsLoad(MythicConditionLoadEvent e) {
		if (e.getConditionName().toUpperCase().equals("ANIMATESTANDPAUSED")) {
			SkillCondition condition = new mmArmorStandPauseCondition(e.getConditionName(),e.getConfig());
			e.register(condition);
		} else if (e.getConditionName().toUpperCase().equals("ISANIMATESTAND")) {
			SkillCondition condition = new mmArmorStandIsAnimator(e.getConditionName(),e.getConfig());
			e.register(condition);
		}
	}

	@EventHandler
	public void mmAiMobDeath(MythicMobDeathEvent e) {
		if (!e.getEntity().hasMetadata("aiMob")) return;
		UUID u = this.getUUIDbyMeta(e.getEntity());
        ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
        am.signalMob(BukkitAdapter.adapt(e.getKiller()), "DEATH");
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void mmAiMobDamage(EntityDamageEvent e) {
		if (e.isCancelled()) return;
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ed = (EntityDamageByEntityEvent)e;
			UUID u = null;
			if (ed.getDamager().hasMetadata("aiMob")) {
				ed.setCancelled(true);
				u = this.getUUIDbyMeta(ed.getDamager());
				if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
					ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
					am.setTarget(BukkitAdapter.adapt(e.getEntity()));
					new TriggeredSkill(SkillTrigger.ATTACK, am, BukkitAdapter.adapt(e.getEntity()));
				}
				return;
			} else if (e.getEntity().hasMetadata("aiMob")) {
				u = this.getUUIDbyMeta(e.getEntity());
				if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
					ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
					am.setLastAggroCause(BukkitAdapter.adapt(ed.getDamager()));
					new TriggeredSkill(SkillTrigger.DAMAGED, am, BukkitAdapter.adapt(ed.getDamager()));
				}
				return;
			}
		} else {
			if (e.getEntity().hasMetadata("aiMob")) {
				UUID u = this.getUUIDbyMeta(e.getEntity());
				if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
					ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
					new TriggeredSkill(SkillTrigger.DAMAGED, am, null);
				}
			}
		}
	}
	
	@EventHandler
	public void mmArmorStandAnimatorDestroy(EntityDamageByEntityEvent e) {
		if (e.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
			Entity entity = e.getEntity();
			Bukkit.getScheduler().scheduleSyncDelayedTask(main.inst(), new Runnable() {
	            @Override
	            public void run() {
	    			if (entity.isDead()) {
	    				ArmorStandAnimator asa = ArmorStandUtils.getAnimatorInstance(BukkitAdapter.adapt(entity));
	    				if (asa!=null) {
	   						asa.stop();
	   						asa.remove();
	    				}
	    			}
	            }
	        });			
		}
	}

	private UUID getUUIDbyMeta(Entity e) {
		String u1 = e.getMetadata("aiMob").get(0).asString();
		String u2 = e.getMetadata("aiMob1").get(0).asString();
		return UUID.fromString(u1+u2);
	}
}
