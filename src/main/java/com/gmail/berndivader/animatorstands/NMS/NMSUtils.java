package com.gmail.berndivader.animatorstands.NMS;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Entity;

import io.lumine.xikage.mythicmobs.skills.SkillTargeter;

public class NMSUtils extends NMSUtil {
    
    public static void setRotation(Entity e, float y, float p) {
    	try {
        	Object nmsEntity = getHandle(e);
        	class_Entity_setYawPitchMethod.invoke(nmsEntity, y, p);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    public static void SetNMSLocation(Entity e, double x, double y, double z, float r, float p) {
    	try {
        	Object entityHandle = getHandle(e);
        	class_Entity_setLocationMethod.invoke(entityHandle, x, y, z, r, p);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
	/**
	 * 
	 * @param targeter_string {@link String}
	 * @return skill_targeter {@link SkillTargeter}
	 */
	
	public static SkillTargeter parseSkillTargeter(String targeter_string) {
		SkillTargeter targeter=null;
		try {
			targeter=(SkillTargeter)class_AbstractSkill_parseSkillTargeterMethod.invoke(null,targeter_string);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return targeter;
	}
    
    
}