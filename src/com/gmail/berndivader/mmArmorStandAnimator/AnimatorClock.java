package com.gmail.berndivader.mmArmorStandAnimator;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

public class AnimatorClock {
	
	public AnimatorClock() {
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Iterator<ArmorStandAnimator> it = ArmorStandAnimator.getAnimators().iterator(); it.hasNext(); ) {
					try {
						ArmorStandAnimator asa = it.next();
						asa.currentTick++;
						if (asa.currentTick>=asa.ClockTick) {
							animClock(asa);
							asa.currentTick = 0;
						}
						aiClock(asa);
					} catch (Exception ex) {
						break;
					}
				}
			}
		}.runTaskTimer(main.inst(), 1, 1);
		
	}
	
	private void aiClock(ArmorStandAnimator asa) {
		if (!asa.hasAI()) return;
		ArmorStandAnimator.doAI(asa);
	}
	
	private void animClock(ArmorStandAnimator asa) {
		asa.update();
	}
	
}
