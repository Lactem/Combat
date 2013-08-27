package me.lactem.queue;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class QueuePlayer {
	private Player player;
	private String arenaToJoin;
	public static HashMap<String, QueuePlayer> queuePlayers = new HashMap<String, QueuePlayer>();
	public static int size = 0;

	public QueuePlayer(Player player, String arenaToJoin) {
		super();
		this.player = player;
		this.arenaToJoin = arenaToJoin;
		queuePlayers.put(player.getName(), this);
		size++;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getArenaToJoin() {
		return arenaToJoin;
	}

	public void setArenaToJoin(String arenaToJoin) {
		this.arenaToJoin = arenaToJoin;
	}

	public static int getSizeOfArena(String arenaName) {
		return queuePlayers.containsKey(arenaName) ? size : 0;
	}

	public static QueuePlayer getQueuePlayer(String name) {
		return queuePlayers.containsKey(name) ? queuePlayers.get(name) : null;
	}
}