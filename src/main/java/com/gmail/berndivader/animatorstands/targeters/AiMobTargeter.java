package com.gmail.berndivader.animatorstands.targeters;

import java.util.HashSet;

import org.bukkit.entity.EntityType;

import com.gmail.berndivader.animatorstands.ArmorStandAnimator;
import com.gmail.berndivader.animatorstands.ArmorStandUtils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.targeters.IEntitySelector;

public 
class 
AiMobTargeter 
extends 
IEntitySelector {

	public AiMobTargeter(MythicLineConfig mlc) {
		super(mlc);
	}

	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		HashSet<AbstractEntity>targets=new HashSet<>();
		if(data.getCaster().getEntity().getBukkitEntity().getType()==EntityType.ARMOR_STAND) {
			AbstractEntity e=data.getCaster().getEntity();
			if (ArmorStandUtils.isArmorStandAnimation(e)) {
				ArmorStandAnimator asa=ArmorStandUtils.getAnimatorInstance(e);
				if(asa.hasAI()) {
					ActiveMob am=asa.aiMob;
					targets.add(am.hasThreatTable()&&am.getThreatTable().size()>0?am.getThreatTable().getTopThreatHolder():am.hasTarget()?am.getEntity().getTarget():null);
				}
			}
		}
		return targets;
	}
}
