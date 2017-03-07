package com.gmail.berndivader.mmArmorStandAnimator;

import java.io.File;
import java.util.Iterator;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;

public class ArmorStandUtils {

	public static boolean animateArmorStand(AbstractEntity entity, String animFile, int repeat, int delay, boolean base) {
		
		if (entity.getBukkitEntity().getType().equals(EntityType.ARMOR_STAND)) {
			ArmorStand as = (ArmorStand)entity.getBukkitEntity();
			ArmorStand a=null;
			ArmorStandAnimator asa=null;
			boolean match=false;
			Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator();
			while (it.hasNext()) {
				asa = it.next();
				a = asa.getArmorStand();
				if (as.getUniqueId()==a.getUniqueId()) {
					match=true;
					break;
				}
			}
			if (match) {
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
			} else {
				if (initArmorStandAnim(entity, animFile, base)) return true;
			}
		}
		return false;
	}

	public static boolean initArmorStandAnim(AbstractEntity target, String file, boolean base) {
		ArmorStand as = (ArmorStand)target.getBukkitEntity();
		ArmorStandAnimator asa=null;
		ArmorStand a=null;
		boolean match=false;
		Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator();
		while (it.hasNext()) {
			asa = it.next();
			a = asa.getArmorStand();
			if (as.getUniqueId()==a.getUniqueId()) {
				match=true;
				break;
			}
		}
		if (match) {
			asa.stop();
			asa.remove();
		}
		try {
			File f = new File(MythicMobs.inst().getDataFolder()+"\\Anims", file);
			asa = new ArmorStandAnimator(f, as);
			asa.setStartLocation(as.getLocation());
			as.setBasePlate(false);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean unloadArmorStandAnimation(AbstractEntity entity) {
		ArmorStand as = (ArmorStand)entity.getBukkitEntity();
		ArmorStandAnimator asa=null;
		Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator();
		boolean match=false;
		while (it.hasNext()) {
			asa = it.next();
			ArmorStand a = asa.getArmorStand();
			if (as.getUniqueId()==a.getUniqueId()) {
				match=true;
				break;
			}
		}
		if (match) {
			asa.stop();
			asa.remove();
			return true;
		}
		return false;
	}
	
}
