package com.gmail.berndivader.mmArmorStandAnimator;

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

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
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
		if (e.getMechanicName().toUpperCase().equals("ASINIT")) {
			SkillMechanic skill = new mmArmorStandInitMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			return;
		} if (e.getMechanicName().toUpperCase().equals("ASUNLOAD")) {
			SkillMechanic skill = new mmArmorStandUnloadMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			return;
		} if (e.getMechanicName().toUpperCase().equals("ASPAUSE")) {
			SkillMechanic skill = new mmArmorStandPauseMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			return;
		} if (e.getMechanicName().toUpperCase().equals("ASRUN")) {
			SkillMechanic skill = new mmArmorStandRunMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			return;
		} if (e.getMechanicName().toUpperCase().equals("ASCHANGE")) {
			SkillMechanic skill = new mmArmorStandChangeAnimMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
			return;
		} if (e.getMechanicName().toUpperCase().equals("ASLOOKAT")) {
			SkillMechanic skill = new mmArmorStandLookAtMechanic(e.getContainer().getConfigLine(),e.getConfig());
			e.register(skill);
		}
	}
	
	@EventHandler
	public void mmMythicMobsConditionsLoad(MythicConditionLoadEvent e) {
		if (e.getConditionName().toUpperCase().equals("ANIMATESTANDPAUSED")) {
			SkillCondition condition = new mmArmorStandPauseCondition(e.getConditionName(),e.getConfig());
			e.register(condition);
			return;
		} if (e.getConditionName().toUpperCase().equals("ISANIMATESTAND")) {
			SkillCondition condition = new mmArmorStandIsAnimator(e.getConditionName(),e.getConfig());
			e.register(condition);
			return;
		} if (e.getConditionName().toUpperCase().equals("AIMOBTARGETDISTANCE")) {
			SkillCondition condition = new mmArmorStandaiMobTargetDistance(e.getConditionName(),e.getConfig());
			e.register(condition);
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
					new TriggeredSkill(SkillTrigger.ATTACK, am, BukkitAdapter.adapt(e.getEntity()));
				}
				return;
			} else if (e.getEntity().hasMetadata("aiMob")) {
				u = this.getUUIDbyMeta(e.getEntity());
				if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
					ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
					AbstractEntity damager = getAttacker(ed.getDamager());
					am.setLastAggroCause(damager);
					new TriggeredSkill(SkillTrigger.DAMAGED, am, damager);
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
					asa.aiMob.getLivingEntity().damage(e.getDamage(), ee.getDamager());
				}
			}
		}
	}

    @EventHandler
    public void onInteractTrigger(PlayerInteractAtEntityEvent e) {
        if (MythicMobs.inst().getMinecraftVersion() >= 9 && e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Entity l = e.getRightClicked();
        if (l.hasMetadata("aiMob")) {
        	UUID u = this.getUUIDbyMeta(l);
        	if (MythicMobs.inst().getMobManager().isActiveMob(u)) {
        		ActiveMob am = MythicMobs.inst().getMobManager().getActiveMob(u).get();
                TriggeredSkill ts = new TriggeredSkill(SkillTrigger.INTERACT, am, BukkitAdapter.adapt(e.getPlayer()), true);
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
				new TriggeredSkill(SkillTrigger.DEATH, am, killer);
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
				}.runTaskLater(main.inst(), 20L);
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
