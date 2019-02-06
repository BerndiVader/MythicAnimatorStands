package com.gmail.berndivader.animatorstands.mechanics;

import com.gmail.berndivader.animatorstands.ArmorStandUtils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

public class ArmorStandChangeAnimMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	private String animFile;
	private int animSpeed;

	public ArmorStandChangeAnimMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		this.ASYNC_SAFE=false;
		this.animFile = mlc.getString(new String[]{"animation","anim","a"},"");
		this.animSpeed = mlc.getInteger(new String[]{"animspeed","speed","s"},0);
		
	}

	@Override
	public boolean cast(SkillMetadata data) {
		return castAtEntity(data, data.getCaster().getEntity());
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		return ArmorStandUtils.changeAnimation(target, this.animFile, this.animSpeed);
	}

}
