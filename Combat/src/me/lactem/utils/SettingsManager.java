package me.lactem.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {

	private SettingsManager() {
	}

	static SettingsManager instance = new SettingsManager();

	public static SettingsManager getInstance() {
		return instance;
	}

	Plugin p;

	FileConfiguration stats;
	File statsFile;

	FileConfiguration arenas;
	File arenasFile;

	public void setup(Plugin p) {

		arenasFile = new File(p.getDataFolder(), "arenas.yml");

		if (!arenasFile.exists()) {
			try {
				arenasFile.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger()
						.severe(ChatColor.RED + "Could not create arenas.yml!");
			}
		}
		arenas = YamlConfiguration.loadConfiguration(arenasFile);
		statsFile = new File(p.getDataFolder(), "stats.yml");
		if (!statsFile.exists()) {
			try {
				statsFile.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger()
						.severe(ChatColor.RED + "Could not create stats.yml!");
			}
		}

		stats = YamlConfiguration.loadConfiguration(statsFile);
	}

	public FileConfiguration getArenas() {
		return arenas;
	}

	public void saveArenas() {
		try {
			arenas.save(arenasFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save arenas.yml!");
		}
	}

	public void reloadArenas() {
		arenas = YamlConfiguration.loadConfiguration(arenasFile);
	}

	public FileConfiguration getStats() {
		return stats;
	}

	public void saveStats() {
		try {
			stats.save(statsFile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save stats.yml!");
		}
	}

	public void reloadStats() {
		stats = YamlConfiguration.loadConfiguration(statsFile);
	}

	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}
}