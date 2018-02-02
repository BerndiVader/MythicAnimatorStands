package com.gmail.berndivader.animatorstands;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

public class AnimatorClock {
	
	public AnimatorClock() {
		
		new BukkitRunnable() {
			private long CleanUpTick=600, currentCleanUpTick=0;
			private long currentAiTick=0, AiClock=0;
			@Override
			public void run() {
				this.currentCleanUpTick++;
				this.currentAiTick++;
				for (Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator(); it.hasNext(); ) {
					ArmorStandAnimator asa = it.next();
					asa.currentAnimTick++;
					if (asa.currentAnimTick>=asa.AnimClock) {
						this.animClock(asa);
						asa.currentAnimTick = 0;
					}
					if (this.currentAiTick>=this.AiClock) this.aiClock(asa);
				}
				if (this.currentCleanUpTick>=this.CleanUpTick) cleanUpClock();
				if (this.currentAiTick>=this.AiClock) this.currentAiTick = 0;
			}
			
			private void cleanUpClock() {
				this.currentCleanUpTick=0;
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
					}
				}.runTaskAsynchronously(AnimatorStands.inst());
				this.currentCleanUpTick=0;
			}

			private void aiClock(ArmorStandAnimator asa) {
				if (!asa.hasAI()) return;
				ArmorStandAnimator.doAI(asa);
			}
			
			private void animClock(ArmorStandAnimator asa) {
				asa.update();
			}

		}.runTaskTimer(AnimatorStands.inst(), 1, 1);
		
	}
}
