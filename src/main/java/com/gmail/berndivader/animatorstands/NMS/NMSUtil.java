package com.gmail.berndivader.animatorstands.NMS;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import java.lang.reflect.Method;

public class NMSUtil {
    protected static String versionPrefix = "";

    protected static Class<?> class_Entity;
    protected static Class<?> class_CraftEntity;
	protected static Class<?> class_AbstractSkill;
    
    protected static Method class_CraftEntity_getHandleMethod;
    protected static Method class_Entity_setYawPitchMethod;
    protected static Method class_Entity_setLocationMethod;
    protected static Method class_AbstractSkill_parseSkillTargeterMethod;

    static
    {
        String className = Bukkit.getServer().getClass().getName();
        String[] packages = StringUtils.split(className, '.');
        
        
        if (packages.length == 5) {
            versionPrefix = packages[3] + ".";
        }

        try {
            class_Entity = fixBukkitClass("net.minecraft.server.Entity");
            class_CraftEntity = fixBukkitClass("org.bukkit.craftbukkit.entity.CraftEntity");
            
            class_CraftEntity_getHandleMethod = class_CraftEntity.getMethod("getHandle");
            
            class_Entity_setYawPitchMethod = class_Entity.getDeclaredMethod("setYawPitch", Float.TYPE, Float.TYPE);
            class_Entity_setYawPitchMethod.setAccessible(true);
            
            class_Entity_setLocationMethod = class_Entity.getMethod("setLocation", Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE);
            
			class_AbstractSkill=fixBukkitClass("io.lumine.xikage.mythicmobs.skills.AbstractSkill");
	        class_AbstractSkill_parseSkillTargeterMethod=class_AbstractSkill.getDeclaredMethod("parseSkillTargeter",String.class);
            
        } catch(Exception ex) {
        	//Empty
        }
    }
    
    public static Class<?> fixBukkitClass(String className) throws ClassNotFoundException {
        if (!versionPrefix.isEmpty()) {
            className = className.replace("org.bukkit.craftbukkit.", "org.bukkit.craftbukkit." + versionPrefix);
            className = className.replace("net.minecraft.server.", "net.minecraft.server." + versionPrefix);
        }

        return NMSUtil.class.getClassLoader().loadClass(className);
    }
    

    public static Class<?> getClass(String className) {
        Class<?> result = null;
        try {
            result = NMSUtil.class.getClassLoader().loadClass(className);
        } catch (Exception ex) {
            result = null;
        }

        return result;
    }
    
    public static Object getHandle(org.bukkit.entity.Entity entity) {
        if (entity == null) return null;
        Object handle = null;
        try {
            handle = class_CraftEntity_getHandleMethod.invoke(entity);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return handle;
    }
    
}
