package com.gmail.berndivader.animatorstands.NMS;

import org.bukkit.entity.Entity;

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
    
}