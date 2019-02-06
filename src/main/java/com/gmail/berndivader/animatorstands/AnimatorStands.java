package com.gmail.berndivader.animatorstands;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class AnimatorStands extends JavaPlugin {
	
	private static Plugin plugin;
	private static AnimatorClock clock;
	
	public static AnimatorClock getClock() {
		return clock;
	}
	
	@Override
	public void onEnable() {
		setPlugin(this);
		new MythicMobsEvents();
		clock = new AnimatorClock();
	}

	@Override
	public void onDisable() {
	}

	public static Plugin inst() {
		return plugin;
	}
	
	public static void setPlugin(Plugin plugin) {
		AnimatorStands.plugin = plugin;
	}
}
