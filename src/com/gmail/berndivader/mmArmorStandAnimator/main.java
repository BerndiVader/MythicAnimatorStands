package com.gmail.berndivader.mmArmorStandAnimator;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.berndivader.mmArmorStandAnimator.NMS.*;

public class main extends JavaPlugin {
	
	private static Plugin plugin;
	private static int mmVer;
	private static String strMMVer;
	private static NMSUtils nmsutils;
	private static AnimatorClock clock;
	public static NMSUtils NMSUtils() {return nmsutils;}
	public static AnimatorClock getClock() {return clock;}
	
	@Override
	public void onEnable() {
		setPlugin(this);
		if (getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
	    	strMMVer = Bukkit.getServer().getPluginManager().getPlugin("MythicMobs").getDescription().getVersion().replaceAll("[\\D]", "");
			mmVer = Integer.valueOf(strMMVer);
			if (mmVer < 400) {
				getLogger().warning("MythicMobs Version not supported.");
				getPluginLoader().disablePlugin(main.plugin);
				return;
			}
		} else {
			getLogger().warning("MythicMobs is not avaible.");
			getPluginLoader().disablePlugin(main.plugin);
			return;
		}
		getNMSUtil();
		new mmMythicMobsEvents();
		clock = new AnimatorClock();
	}

	@Override
	public void onDisable() {
	}

	public static Plugin inst() {
		return plugin;
	}

	public static void setPlugin(Plugin plugin) {
		main.plugin = plugin;
	}
	private boolean getNMSUtil() {
		String v;
		try {v = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException e) {return false;}
		if (v.equals("v1_8_R3") || v.equals("v1_8_R2")) {nmsutils=new NMSUtil18();}
		else if (v.equals("v1_9_R1") || v.equals("v1_9_R2") || v.equals("v1_10_R1") || v.equals("v1_11_R1")) {nmsutils=new NMSUtil19();}
		return nmsutils!=null;
	}
}
