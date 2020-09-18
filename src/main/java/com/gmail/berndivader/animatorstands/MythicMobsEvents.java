package com.gmail.berndivader.animatorstands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.animatorstands.conditions.ArmorStandIsAnimator;
import com.gmail.berndivader.animatorstands.conditions.ArmorStandPauseCondition;
import com.gmail.berndivader.animatorstands.conditions.ArmorStandaiMobTargetDistance;
import com.gmail.berndivader.animatorstands.mechanics.ArmorStandChangeAnimMechanic;
import com.gmail.berndivader.animatorstands.mechanics.ArmorStandInitMechanic;
import com.gmail.berndivader.animatorstands.mechanics.ArmorStandLookAtMechanic;
import com.gmail.berndivader.animatorstands.mechanics.ArmorStandPauseMechanic;
import com.gmail.berndivader.animatorstands.mechanics.ArmorStandRunMechanic;
import com.gmail.berndivader.animatorstands.mechanics.ArmorStandUnloadMechanic;
import com.gmail.berndivader.animatorstands.targeters.AiMobTargeter;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicConditionLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillTrigger;

public class MythicMobsEvents implements Listener {
	public MythicMobsEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(this, AnimatorStands.inst());
	}
	
	@EventHandler
	public void mmMythicMobsMechanicsLoad(MythicMechanicLoadEvent e) {
		String m = e.getMechanicName().toLowerCase();
		switch (m) {
		case "asinit": {
			SkillMechanic skill = new ArmorStandInitMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			break;
		}
		case "asunload": {
			SkillMechanic skill = new ArmorStandUnloadMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			break;
		}
		case "aspause": {
			SkillMechanic skill = new ArmorStandPauseMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			break;
		}
		case "asrun": {
			SkillMechanic skill = new ArmorStandRunMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			break;
		}
		case "aschange": {
			SkillMechanic skill = new ArmorStandChangeAnimMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			break;
		}
		case "aslookat": {
			SkillMechanic skill = new ArmorStandLookAtMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			break;
		}
		}
	}
	
	@EventHandler
	public void mmMythicMobsConditionsLoad(MythicConditionLoadEvent e) {
		String c = e.getConditionName().toLowerCase();
		switch (c) {
		case "animatestandpaused": {
			SkillCondition condition = new ArmorStandPauseCondition(c,e.getConfig());
			e.register(condition);
			break;
		}
		case "isanimatestand": {
			SkillCondition condition = new ArmorStandIsAnimator(c,e.getConfig());
			e.register(condition);
			break;
		}
		case "aimobtargetdistance": {
			SkillCondition condition = new ArmorStandaiMobTargetDistance(c,e.getConfig());
			e.register(condition);
			break;
		}
		}
	}
	
	@EventHandler
	public void MythicMobTargeterLoad(MythicTargeterLoadEvent e) {
		if(e.getTargeterName().toLowerCase().equals("aitarget")) {
			e.register(new AiMobTargeter(e.getConfig()));
		}
	}

	@EventHandler
	public void mmAiMobDeath(MythicMobDeathEvent e) {
		if (!e.getEntity().hasMetadata("aiMob")) return;
		UUID u = this.getUUIDbyMeta(e.getEntity());
        ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
        am.setDead();
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
					new AbstractTriggeredSkill(SkillTrigger.ATTACK, am, BukkitAdapter.adapt(e.getEntity()));
				}
				return;
			} else if (e.getEntity().hasMetadata("aiMob")) {
				u = this.getUUIDbyMeta(e.getEntity());
				if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
					ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
					AbstractEntity damager = getAttacker(ed.getDamager());
					am.setLastAggroCause(damager);
					new AbstractTriggeredSkill(SkillTrigger.DAMAGED, am, damager);
				}
				return;
			}
		} else {
			if (e.getEntity().hasMetadata("aiMob")) {
				UUID u = this.getUUIDbyMeta(e.getEntity());
				if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
					ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
					new AbstractTriggeredSkill(SkillTrigger.DAMAGED, am, null);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void AnimatorStandHandleDamage(EntityDamageEvent e) {
		if (e.isCancelled()) return;
		if (e.getEntity().getType().equals(EntityType.ARMOR_STAND) && e.getEntity().hasMetadata("asa")) {
			ArmorStandAnimator asa = ArmorStandUtils.getAnimatorInstance(BukkitAdapter.adapt(e.getEntity()));
			if (!asa.hasAI()) return;
			e.setCancelled(true);
			if (e instanceof EntityDamageByEntityEvent && !asa.isDying) {
				EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
				AbstractEntity attacker = this.getAttacker(ee.getDamager());
				if (!attacker.isPlayer()) {
					LivingEntity entity=(LivingEntity)asa.aiMob.getEntity().getBukkitEntity();
					entity.damage(e.getDamage(), ee.getDamager());
				}
			}
		}
	}

    @EventHandler
    public void onInteractTrigger(PlayerInteractAtEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Entity l = e.getRightClicked();
        if (l.hasMetadata("aiMob")) {
        	UUID u = this.getUUIDbyMeta(l);
        	if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
        		ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
        		AbstractTriggeredSkill ts = new AbstractTriggeredSkill(SkillTrigger.INTERACT, am, BukkitAdapter.adapt(e.getPlayer()), null, true);
                if (ts.getCancelled()) {
                    e.setCancelled(true);
                }
        	}
        }
    }
    
	@EventHandler
	public void mmaiMobDeathEvent(MythicMobDeathEvent e) {
		if (e.getEntity().hasMetadata("aiMob")) {
			UUID u = this.getUUIDbyMeta(e.getEntity());
			if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
				ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
				AbstractEntity killer = BukkitAdapter.adapt(e.getKiller());
				am.setLastAggroCause(killer);
				new AbstractTriggeredSkill(SkillTrigger.DEATH, am, killer);
				final ArmorStandAnimator asa = ArmorStandUtils.getAnimatorInstance(am.getEntity());
				asa.isDying = true;
				new BukkitRunnable() {
					@Override
					public void run() {
	    				if (asa!=null) {
	    					asa.getArmorStand().remove();
	    					asa.stop();
	    					asa.remove();
	    				}
					}
				}.runTaskLater(AnimatorStands.inst(), 20L);
			}
		}
	}
	
	private AbstractEntity getAttacker(Entity damager) {
        if (damager instanceof Projectile) {
            if (((Projectile)damager).getShooter() instanceof LivingEntity) {
                LivingEntity shooter = (LivingEntity)((Projectile)damager).getShooter();
                if (shooter != null && shooter instanceof LivingEntity) {
                    return BukkitAdapter.adapt(shooter);
                }
            } else {
                return null;
            }
        }
        if (damager instanceof LivingEntity) {
            return BukkitAdapter.adapt(damager);
        }
        return null;
	}

	private UUID getUUIDbyMeta(Entity e) {
		String u1 = e.getMetadata("aiMob").get(0).asString();
		String u2 = e.getMetadata("aiMob1").get(0).asString();
		return UUID.fromString(u1+u2);
	}
}
