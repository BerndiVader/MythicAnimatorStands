package com.gmail.berndivader.animatorstands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.SkillTargeter;

public class ArmorStandUtils {

	public static boolean animateArmorStand(AbstractEntity entity, int repeat, int delay) {
		ArmorStandAnimator asa=getAnimatorInstance(entity);
		if (asa!=null) {
			if (asa.aiMobName!=null && asa.aiMob.getEntity().isDead()) {
				if (checkForNearByPlayers(entity.getBukkitEntity(), 16000.0d)) {
					asa.reAttachAIMob();
				}
			}
			return true;
		}
		return false;
	}
	
	private static boolean checkForNearByPlayers(Entity entity, double r) {
        for (Player p : entity.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(entity.getLocation()) >= r) continue;
            return true;
        }
		return false;
	}
	
	public static boolean changeAnimation(AbstractEntity entity, String animFile, int animSpeed) {
		ArmorStandAnimator asa  = getAnimatorInstance(entity);
		if (asa!=null) {
			try {
				File f = new File(MythicMobs.inst().getDataFolder()+"/Anims", animFile);
				asa.changeAnim(f, animSpeed);
			} catch (Exception e) {
				Bukkit.getLogger().warning("Could not load animation: " + animFile);
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static boolean initArmorStandAnim(ActiveMob am, String file, boolean base, int animSpeed, Object oi, Object mobtype) {
		AbstractEntity target = am.getEntity();
		if (!(target.getBukkitEntity() instanceof ArmorStand)) return false;
		ArmorStand as = (ArmorStand)target.getBukkitEntity();
		ArmorStandAnimator asa=getAnimatorInstance(target);
		if (asa != null) {
			asa.stop();
			asa.remove();
		};
		try {
			File f = new File(MythicMobs.inst().getDataFolder()+"/Anims", file);
			asa = new ArmorStandAnimator(f, as, animSpeed, oi, mobtype);
			asa.setStartLocation(as.getLocation());
			as.setBasePlate(base);
		} catch (Exception e) {
			Bukkit.getLogger().warning("Could not load animation: " + file);
			return false;
		}
		return true;
	}
	
	public static boolean startArmorStandAnimation(AbstractEntity entity) {
		ArmorStandAnimator asa = getAnimatorInstance(entity);
		if (asa!=null) {
			asa.play();
			return true;
		}
		return false;
	}
	
	public static boolean pauseArmorStandAnimation(AbstractEntity entity) {
		ArmorStandAnimator asa = getAnimatorInstance(entity);
		if (asa!=null) {
			asa.pause();
			return true;
		}
		return false;
	}
	
	public static boolean isArmorStandAnimation(AbstractEntity entity) {
		ArmorStandAnimator asa = getAnimatorInstance(entity);
		return asa!=null;
	}

	public static boolean unloadArmorStandAnimation(AbstractEntity entity) {
		ArmorStandAnimator asa = getAnimatorInstance(entity);
		if (asa!=null) {
			asa.stop();
			asa.remove();
			return true;
		}
		return false;
	}
	
	public static boolean isAnimationPaused(AbstractEntity entity) {
		ArmorStandAnimator asa = getAnimatorInstance(entity);
		if (asa!=null) {
			return asa.isPaused();
		}
		return false;
	}
	
	public static Location getTargetBlock(LivingEntity entity, int range) {
		BlockIterator bit=new BlockIterator(entity,range);
		while(bit.hasNext()) {
			Block next = bit.next();
			if(next != null && next.getType() != Material.AIR) {
				return next.getLocation().clone();
			}
		}
		return null;
	}	
	
	public static Location lookAt(Location loc, Location lookat) {
        loc=loc.clone();
        lookat=lookat.clone();
        double dx=lookat.getX()-loc.getX();
        double dy=lookat.getY()-loc.getY();
        double dz=lookat.getZ()-loc.getZ();
 
        if (dx!=0) {
            if (dx<0) {
                loc.setYaw((float)(1.5*Math.PI));
            } else {
                loc.setYaw((float)(0.5*Math.PI));
            }
            loc.setYaw(loc.getYaw()-(float)Math.atan(dz/dx));
        } else if (dz<0) {
            loc.setYaw((float)Math.PI);
        }
        double dxz=Math.sqrt(Math.pow(dx,2)+Math.pow(dz,2));
        loc.setPitch((float)-Math.atan(dy/dxz));
        loc.setYaw(-loc.getYaw()*180f/(float)Math.PI);
        loc.setPitch(loc.getPitch()*180f/(float)Math.PI);
        return loc;
    }	
	
	public static ArmorStandAnimator getAnimatorInstance(AbstractEntity entity) {
		return ArmorStandAnimator.getAnimatorByUUID(entity.getUniqueId());
	}
	
	public static void removeEntitySync(Entity entity) {
		new BukkitRunnable() {
			@Override
			public void run() {
				entity.remove();
			}
		}.runTask(AnimatorStands.inst());
	}

	public static void applyInvisible(ActiveMob am, long runlater) {
		PotionEffect pe = new PotionEffect(PotionEffectType.INVISIBILITY, 2073600, 4, false, false);
		LivingEntity le = (LivingEntity)am.getEntity().getBukkitEntity();
		pe.apply(le);
		new BukkitRunnable() {
			@Override
			public void run() {
				le.getEquipment().clear();
			}
		}.runTaskLater(AnimatorStands.inst(), runlater);
	}
	
	/**
	 * 
	 * @param targeter_string {@link String}
	 * @return skill_targeter {@link SkillTargeter}
	 */
	
	public static SkillTargeter parseSkillTargeter(String targeter_string) {
        String search = targeter_string.substring(1);
        MythicLineConfig mlc = new MythicLineConfig(search);
        String name = search.contains("{") ? search.substring(0, search.indexOf("{")) : search;
        return SkillTargeter.getMythicTargeter(name, mlc);
	}
	
	
}
