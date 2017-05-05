package com.gmail.berndivader.mmArmorStandAnimator;

import org.bukkit.Location;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedLocationSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

public class mmArmorStandLookAtMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

	public mmArmorStandLookAtMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		this.ASYNC_SAFE=false;
	}

	@Override
	public boolean castAtLocation(SkillMetadata data, AbstractLocation target) {
		ArmorStandAnimator e = ArmorStandUtils.getAnimatorInstance(data.getCaster().getEntity());
		if (e==null) return false;
		Location l = ArmorStandUtils.lookAt(e.getArmorStand().getLocation(), BukkitAdapter.adapt(target));
		main.NMSUtils().setRotation(e.getArmorStand(), l.getYaw(), l.getPitch());
		return true;
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
		return castAtLocation(data, target.getLocation());
	}

}
