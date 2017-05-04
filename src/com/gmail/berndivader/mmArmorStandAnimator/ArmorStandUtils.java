package com.gmail.berndivader.mmArmorStandAnimator;

import java.io.File;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class ArmorStandUtils {

	public static boolean animateArmorStand(AbstractEntity entity, int repeat, int delay) {
		ArmorStandAnimator asa=getAnimatorInstance(entity);
		if (asa!=null) {
			ArmorStand as = asa.getArmorStand();
			final ArmorStandAnimator aa = asa;
			new BukkitRunnable() {
        		int r = repeat;
        		public void run() {
        			if (r!=-1) {
       					aa.setStartLocation(as.getLocation());
        				aa.update();
        				r--;
        			} else {
        				this.cancel();
        			}
                }
            }.runTaskTimer(main.inst(), 0, delay);
			return true;
		}
		return false;
	}
	
	public static boolean changeAnimation(AbstractEntity entity, String animFile) {
		ArmorStandAnimator asa  = getAnimatorInstance(entity);
		if (asa!=null) {
			try {
				File f = new File(MythicMobs.inst().getDataFolder()+"\\Anims", animFile);
				asa.changeAnim(f);
			} catch (Exception e) {
				Bukkit.getLogger().warning("Could not load animation: " + animFile);
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static boolean initArmorStandAnim(ActiveMob am, String file, boolean base, Object oi, Object mobtype) {
		AbstractEntity target = am.getEntity();
		if (!(target.getBukkitEntity() instanceof ArmorStand)) return false;
		ArmorStand as = (ArmorStand)target.getBukkitEntity();
		ArmorStandAnimator asa=getAnimatorInstance(target);
		if (asa != null) {
			asa.stop();
			asa.remove();
		};
		try {
			File f = new File(MythicMobs.inst().getDataFolder()+"\\Anims", file);
			asa = new ArmorStandAnimator(f, as, oi, mobtype);
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
	
	public static ArmorStandAnimator getAnimatorInstance(AbstractEntity entity) {
		if (!entity.getBukkitEntity().getType().equals(EntityType.ARMOR_STAND)) return null;
		ArmorStand as = (ArmorStand)entity.getBukkitEntity();
		ArmorStandAnimator asa=null;
		Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator();
		boolean match=false;
		while (it.hasNext()) {
			asa = it.next();
			if (as.getUniqueId()==asa.getArmorStand().getUniqueId()) {
				match=true;
				break;
			}
		}
		return match?asa:null;
	}
}
