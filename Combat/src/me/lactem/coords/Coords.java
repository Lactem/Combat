package me.lactem.coords;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Coords {
	private Block block1;
	private Block block2;
	private Player player;
	public static String permission = "";
	public static int id = 0;
	public static HashMap<String, Coords> coords = new HashMap<String, Coords>();

	public Coords(Player player) {
		this.player = player;
	}

	public Block getBlock1() {
		return block1;
	}

	public Block getBlock2() {
		return block2;
	}

	public Player getPlayer() {
		return player;
	}

	public void setBlock1(Block block1) {
		this.block1 = block1;
	}

	public void setBlock2(Block block2) {
		this.block2 = block2;
	}

	public boolean areBothPointsSet() {
		return getBlock1() == null || getBlock2() == null ? false : true;
	}

	public boolean areBlocksInDifferentWorlds() {
		return getBlock1().getWorld().getName()
				.equals(getBlock2().getWorld().getName()) ? false : true;
	}

	public static String getPermission() {
		return Coords.permission;
	}

	public static void setPermission(String permission) {
		Coords.permission = permission;
	}

	public static int getUniversalWandId() {
		return id;
	}

	public static void setUniversalWandId(int id) {
		Coords.id = id;
	}

	public static boolean isUniversalWandSet() {
		return Coords.id == 0 ? false : true;
	}

	public static Coords getPlayerCoords(Player player) {
		if (!coords.containsKey(player.getName())) {
			coords.put(player.getName(), new Coords(player));
		}
		return coords.get(player.getName());
	}
}