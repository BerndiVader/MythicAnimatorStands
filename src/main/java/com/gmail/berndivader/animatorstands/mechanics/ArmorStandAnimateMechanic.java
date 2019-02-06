package com.gmail.berndivader.animatorstands.mechanics;

import com.gmail.berndivader.animatorstands.ArmorStandUtils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

public class ArmorStandAnimateMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	private int repeat, delay;
	
	public ArmorStandAnimateMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		this.ASYNC_SAFE=false;
		this.repeat = mlc.getInteger(new String[]{"repeat","r"},0);
		this.repeat = this.repeat>=0?this.repeat:0;
		this.delay = mlc.getInteger(new String[]{"delay","d"},0);
		this.delay = this.delay>=0?this.delay:0;
	}

	@Override
	public boolean cast(SkillMetadata data) {
		return castAtEntity(data, data.getCaster().getEntity());
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		return ArmorStandUtils.animateArmorStand(target, this.repeat, this.delay);
	}
}
