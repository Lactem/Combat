package me.lactem.arenaplayer;

import org.bukkit.entity.Player;

import me.lactem.arena.Arena;
import me.lactem.main.Main;

public class ArenaPlayer {
	private Arena arena;
	private Player player;

	public ArenaPlayer(Player player, Arena arena) {
		this.player = player;
		this.arena = arena;
	}

	public Arena getArena() {
		return arena;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setIsPlaying() {
		if (!arena.getPlaying().contains(player.getName())) {
			arena.getPlaying().add(player.getName());
			if (arena.getSpectating().contains(player.getName())) {
				arena.getSpectating().remove(player.getName());
			}
		}
	}

	public void setIsSpectating() {
		if (!arena.getSpectating().contains(player.getName())) {
			arena.getSpectating().add(player.getName());
			if (arena.getPlaying().contains(player.getName())) {
				arena.getPlaying().remove(player.getName());
			}
		}
	}

	public void spawnPlayerInArena() {
		player.teleport(Main.generateRandomSpawn(arena, player));
	}

	public boolean isPlaying() {
		return arena.getPlaying().contains(player.getName()) ? true : false;
	}

	public boolean isSpectating() {
		return arena.getSpectating().contains(player.getName()) ? true : false;
	}
}