package com.gmail.berndivader.mmArmorStandAnimator;

import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimatorClock {
	private static long CleanUpTick=600, currentCleanUpTick=0;
	private static long currentAiTick=0, AiClock=1;
	
	public AnimatorClock() {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				AnimatorClock.currentCleanUpTick++;
				AnimatorClock.currentAiTick++;
				for (Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator(); it.hasNext(); ) {
					ArmorStandAnimator asa = it.next();
					asa.currentAnimTick++;
					if (AnimatorClock.currentAiTick>=AnimatorClock.AiClock) this.aiClock(asa);
					if (asa.currentAnimTick>=asa.AnimClock) {
						this.animClock(asa);
						asa.currentAnimTick = 0;
					}
				}
				if (AnimatorClock.currentCleanUpTick>=AnimatorClock.CleanUpTick) cleanUpClock();
			}
			
			private void cleanUpClock() {
				AnimatorClock.currentCleanUpTick=0;
				new BukkitRunnable() {
					@Override
					public void run() {
		    			for (Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator(); it.hasNext();) {
		    				ArmorStandAnimator asa = it.next();
		    				if (asa!=null) {
		    					if (asa.getArmorStand()==null || asa.getArmorStand().isDead()) it.remove();
		    				} else {
		    					it.remove();
		    				}
		    			}
  		    			for (Iterator<Entry<Integer,UUID>> it = main.getEntityHider().EntityMap.entrySet().iterator(); it.hasNext();) {
		    				Entry<Integer, UUID> u = it.next();
		    				if (Bukkit.getEntity(u.getValue())==null) it.remove();
		    			}
					}
				}.runTaskAsynchronously(main.inst());
				AnimatorClock.currentCleanUpTick=0;
			}

			private void aiClock(ArmorStandAnimator asa) {
				if (!asa.hasAI()) return;
				ArmorStandAnimator.doAI(asa);
			}
			
			private void animClock(ArmorStandAnimator asa) {
				asa.update();
			}

		}.runTaskTimer(main.inst(), 1, 1);
		
	}
}
