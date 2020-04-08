package com.gmail.berndivader.animatorstands.conditions;

import com.gmail.berndivader.animatorstands.ArmorStandAnimator;
import com.gmail.berndivader.animatorstands.ArmorStandUtils;
import com.gmail.berndivader.animatorstands.RangedDouble;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.ConditionAction;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;

public class ArmorStandaiMobTargetDistance extends SkillCondition 
implements
IEntityCondition {
	protected RangedDouble distance;
	
	public ArmorStandaiMobTargetDistance(String line, MythicLineConfig mlc) {
		super(line);
		try {
			this.ACTION = ConditionAction.valueOf(mlc.getString(new String[]{"action","a"}, "TRUE").toUpperCase());
		} catch (Exception ex) {
			this.ACTION = ConditionAction.TRUE;
		}
        String d = mlc.getString(new String[]{"distance", "d"},"5");
        this.distance = new RangedDouble(d);
	}

	@Override
	public boolean check(AbstractEntity entity) {
		ArmorStandAnimator asa = ArmorStandUtils.getAnimatorInstance(entity);
		if (asa!=null && asa.hasAI()) {
			ActiveMob aiMob = asa.aiMob;
			if (aiMob.getEntity().getTarget()!=null) {
				AbstractEntity target = aiMob.getEntity().getTarget();
		        double diffSq = (float)entity.getLocation().distanceSquared(target.getLocation());
		        return this.distance.equals(diffSq);
			}
		}
		return false;
	}
}
