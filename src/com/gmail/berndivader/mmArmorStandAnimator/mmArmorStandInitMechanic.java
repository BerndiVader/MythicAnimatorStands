package com.gmail.berndivader.mmArmorStandAnimator;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

public class mmArmorStandInitMechanic extends SkillMechanic implements INoTargetSkill, ITargetedEntitySkill {

	private String animFile;
	private boolean base;
	private Object oi, mobtype;
	
	public mmArmorStandInitMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		this.animFile = mlc.getString(new String[]{"animation","anim","a"},"");
		this.base = mlc.getBoolean(new String[]{"base","plate"},false);
		this.oi = mlc.getBoolean(new String[]{"autoinit","ai"},false);
		this.mobtype = mlc.getString(new String[]{"mobtype"});
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		return ArmorStandUtils.initArmorStandAnim(target, this.animFile, this.base, this.oi, this.mobtype);
	}

	@Override
	public boolean cast(SkillMetadata data) {
		return ArmorStandUtils.initArmorStandAnim(data.getCaster().getEntity(), this.animFile, this.base, this.oi, this.mobtype);
	}
}
