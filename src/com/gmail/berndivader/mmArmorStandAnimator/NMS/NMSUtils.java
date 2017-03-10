package com.gmail.berndivader.mmArmorStandAnimator.NMS;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Entity;

public interface NMSUtils {

	public Entity getEntity(World world, UUID uuid);
	public void setRotation(Entity e, float y, float p);
	public void SetNMSLocation(Entity e, double x, double y, double z, float r, float p);
}
