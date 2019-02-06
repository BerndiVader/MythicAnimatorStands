package com.gmail.berndivader.animatorstands.conditions;

import com.gmail.berndivader.animatorstands.ArmorStandUtils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.conditions.ConditionAction;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;

public class ArmorStandIsAnimator extends SkillCondition implements IEntityCondition {

	public ArmorStandIsAnimator(String line, MythicLineConfig mlc) {
		super(line);
		this.setAction(ConditionAction.TRUE);
	}

	@Override
	public boolean check(AbstractEntity target) {
		return ArmorStandUtils.isArmorStandAnimation(target);
	}
}
