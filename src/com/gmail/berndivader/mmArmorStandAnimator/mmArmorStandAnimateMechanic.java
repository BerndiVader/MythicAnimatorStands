package com.gmail.berndivader.mmArmorStandAnimator;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

public class mmArmorStandAnimateMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {

	private String animFile;
	private int repeat, delay;
	private boolean base;
	
	public mmArmorStandAnimateMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		
		this.animFile = mlc.getString(new String[]{"animation","anim","a"},"");
		this.repeat = mlc.getInteger(new String[]{"repeat","r"},0);
		this.delay = mlc.getInteger(new String[]{"delay","d"},0);
		this.base = mlc.getBoolean(new String[]{"base","plate"},false);
		this.delay = this.delay>=0?this.delay:0;
		this.repeat = this.repeat>=0?this.repeat:0;
	}

	@Override
	public boolean cast(SkillMetadata data) {
		return ArmorStandUtils.animateArmorStand(data.getCaster().getEntity(),this.animFile, this.repeat, this.delay, this.base);
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		return ArmorStandUtils.animateArmorStand(target, this.animFile, this.repeat, this.delay, this.base);
	}
}
