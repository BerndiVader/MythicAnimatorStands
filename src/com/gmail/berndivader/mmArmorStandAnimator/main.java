package com.gmail.berndivader.mmArmorStandAnimator;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	
	private static Plugin plugin;
	private static int mmVer;
	private static String strMMVer;
	
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
		
		new mmMythicMobsEvents();
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
}
