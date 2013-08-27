package me.lactem.queue;

import java.util.ArrayList;
import java.util.HashMap;

import me.lactem.arena.Arena;
import me.lactem.main.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ArenaQueue {
	private Plugin plugin;
	private boolean didArenaStart = false;
	private int playersInQueue;
	private String arenaName;
	private boolean startedCountdown;
	private ArrayList<QueuePlayer> queuePlayers = new ArrayList<QueuePlayer>();
	private ArrayList<String> players = new ArrayList<String>();
	public static HashMap<String, ArenaQueue> arenaQueues = new HashMap<String, ArenaQueue>();
	public static HashMap<String, ArenaQueue> allArenaQueues = new HashMap<String, ArenaQueue>();

	public ArenaQueue(ArrayList<QueuePlayer> queuePlayers, String arenaName) {
		this.queuePlayers = queuePlayers;
		this.arenaName = arenaName;
		for (int i = 0; i < queuePlayers.size(); i++) {
			players.add(queuePlayers.get(i).getPlayer().getName());
			allArenaQueues.put(queuePlayers.get(i).getPlayer().getName(), this);
		}
		playersInQueue = queuePlayers.size();
		arenaQueues.put(arenaName, this);
	}

	public int getPlayersInQueue() {
		return playersInQueue;
	}

	public ArrayList<QueuePlayer> getQueuePlayers() {
		return queuePlayers;
	}

	public ArenaQueue getArenaQueue() {
		return this;
	}

	public void setPlayersInQueue(int playersInQueue) {
		this.playersInQueue = playersInQueue;
	}

	public void addPlayerToQueue(Player player) {
		QueuePlayer queuePlayer = QueuePlayer.getQueuePlayer(player.getName());
		if (queuePlayer == null) {
			queuePlayer = new QueuePlayer(player, arenaName);
		}
		players.add(player.getName());
		queuePlayers.add(queuePlayer);
		allArenaQueues.put(player.getName(), this);
		playersInQueue++;
	}

	public void removePlayerFromQueue(Player player) {
		QueuePlayer queuePlayer = QueuePlayer.getQueuePlayer(player.getName());
		queuePlayers.remove(queuePlayer);
		players.remove(player);
		playersInQueue--;
		allArenaQueues.remove(player.getName());
	}

	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public void startArena() {
		Arena arena = new Arena(arenaName);
		arena.setPlugin(plugin);
		ArrayList<String> nowPlaying = new ArrayList<String>();
		for (int i = 0; i < players.size(); i++) {
			nowPlaying.add(players.get(i));
		}
		arena.setPlaying(nowPlaying);
		didArenaStart = true;
		arena.startNextRound();
	}

	public void startCountdown() {
		Bukkit.getServer().broadcastMessage(
				Main.COMBAT + ChatColor.GREEN + arenaName
						+ " will start in two minutes.");
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				startArena();
			}
		}, 1200l);
		startedCountdown = true;
	}

	public boolean startedCountdown() {
		return startedCountdown;
	}

	public static boolean didArenaStart(ArenaQueue arenaQueue) {
		return arenaQueue.didArenaStart ? true : false;
	}

	public static boolean doesArenaQueueExist(String arenaQueue) {
		return arenaQueues.containsKey(arenaQueue) ? true : false;
	}

	public static ArenaQueue getArenaQueue(String arenaQueue) {
		return arenaQueues.get(arenaQueue);
	}

	public static ArenaQueue getArenaQueueFromPlayerName(String name) {
		return allArenaQueues.containsKey(name) ? allArenaQueues.get(name)
				: null;
	}
}