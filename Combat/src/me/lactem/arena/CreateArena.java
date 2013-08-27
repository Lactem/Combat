package me.lactem.arena;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.lactem.coords.Coords;
import me.lactem.utils.SettingsManager;

public class CreateArena {
	private String arenaName;
	private Player player;
	SettingsManager settings = SettingsManager.getInstance();

	public CreateArena(String arenaName, Player player) {
		this.arenaName = arenaName;
		this.player = player;
	}

	public void addDetailsToConfig() {
		Coords coords = Coords.getPlayerCoords(player);
		if (settings.getArenas().get(arenaName.toLowerCase()) == null) {
			settings.getArenas().createSection(arenaName.toLowerCase());
			ConfigurationSection cs = settings.getArenas()
					.getConfigurationSection(arenaName.toLowerCase());
			cs.set("name", arenaName);
			cs.set("world", coords.getBlock1().getWorld().getName());
			cs.set("block1", coords.getBlock1().toString());
			cs.set("block2", coords.getBlock2().toString());
			cs.set("block1X", coords.getBlock1().getX());
			cs.set("block1Y", coords.getBlock1().getX());
			cs.set("block1Z", coords.getBlock1().getX());
			cs.set("block2X", coords.getBlock2().getX());
			cs.set("block2Y", coords.getBlock2().getX());
			cs.set("block2Z", coords.getBlock2().getX());
			settings.saveArenas();
		}
	}

	public void createArena() {
		Arena arena = new Arena(arenaName.toLowerCase());
		arena.setCoords(Coords.getPlayerCoords(player));
	}
}