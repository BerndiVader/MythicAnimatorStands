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
	private int minecraftVersion;
	private String bukkitVersion;
	public static NMSUtils NMSUtils() {return nmsutils;}
	public static AnimatorClock getClock() {return clock;}
	
	@Override
	public void onEnable() {
		setPlugin(this);
	    this.bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
	    try
	    {
	      String[] split = bukkitVersion.split("_");
	      this.minecraftVersion = Integer.parseInt(split[1]);
	    } catch (Exception ex) {
	      this.minecraftVersion = 11;
	      ex.printStackTrace();
	    }
		
	    if (this.minecraftVersion<9) {
	    	getLogger().warning("Bukkit 1.9 or higher is required!");
	    	getPluginLoader().disablePlugin(main.plugin);
	    	return;
	    }
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
		nmsutils=new NMSUtils();
		return nmsutils!=null;
	}
}
