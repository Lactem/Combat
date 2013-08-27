package me.lactem.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.lactem.arenaplayer.ArenaPlayer;
import me.lactem.coords.Coords;
import me.lactem.main.Main;
import me.lactem.utils.SettingsManager;

public class Arena {
	private String arenaName;
	private Coords coords;
	private Plugin plugin;
	private ArrayList<String> playing = new ArrayList<String>();
	private ArrayList<String> spectating = new ArrayList<String>();
	private ArrayList<String> spectatingNormally = new ArrayList<String>();
	private HashMap<String, Integer> roundsWon = new HashMap<String, Integer>();
	public static HashMap<String, Arena> arenaPlayers = new HashMap<String, Arena>();
	public static ArrayList<String> invinciblePlayers = new ArrayList<String>();
	private int round = 0;
	static SettingsManager settings = SettingsManager.getInstance();

	public Arena(String arenaName) {
		this.arenaName = arenaName;
	}

	public String getArenaName() {
		return arenaName;
	}

	public Coords getCoords() {
		return this.coords;
	}

	public String getBlock1() {
		return settings.getArenas().getString(arenaName + ".block1");
	}

	public String getBlock2() {
		return settings.getArenas().getString(arenaName + ".block2");
	}

	public String getWorld() {
		return settings.getArenas().getString(arenaName + ".world");
	}

	public int getBlock1X() {
		return settings.getArenas().getInt(arenaName + ".block1X");
	}

	public int getBlock1Y() {
		return settings.getArenas().getInt(arenaName + ".block1Y");
	}

	public int getBlock1Z() {
		return settings.getArenas().getInt(arenaName + ".block1Z");
	}

	public int getBlock2X() {
		return settings.getArenas().getInt(arenaName + ".block2X");
	}

	public int getBlock2Y() {
		return settings.getArenas().getInt(arenaName + ".block2Y");
	}

	public int getBlock2Z() {
		return settings.getArenas().getInt(arenaName + ".block2Z");
	}

	public int getRound() {
		return round;
	}

	public ArrayList<String> getPlaying() {
		return playing;
	}

	public ArrayList<String> getSpectating() {
		return spectating;
	}

	public ArrayList<String> getSpectatingNormally() {
		return spectatingNormally;
	}

	public Arena getArena() {
		return this;
	}

	public void setBlock1(String block1) {
		settings.getArenas().set(arenaName + ".block1", block1);
		settings.saveArenas();
	}

	public void setBlock2(String block2) {
		settings.getArenas().set(arenaName + ".block2", block2);
		settings.saveArenas();
	}

	public void setArenaName(String arenaName) {
		this.arenaName = arenaName;
	}

	public void setCoords(Coords coords) {
		this.coords = coords;
	}

	public void setRound(int i) {
		round = i;
	}

	public void setPlaying(ArrayList<String> playing) {
		this.playing = playing;
		for (int i = 0; i < playing.size(); i++) {
			if (!arenaPlayers.containsKey(playing.get(i))) {
				arenaPlayers.put(playing.get(i), this);
			}
		}
	}

	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public void addSpectatorFromAlivePlayer(Player player) {
		System.out.println("moved " + player.getName()
				+ " to spectator from alive player");
		ArrayList<String> newPlayers = new ArrayList<String>();
		newPlayers.addAll(playing);
		newPlayers.remove(player.getName());
		setPlaying(newPlayers);
		getSpectating().add(player.getName());
	}

	public void addSpectatorFromDeadPlayer(Player player) {
		ArrayList<String> newPlayers = new ArrayList<String>();
		newPlayers.addAll(playing);
		newPlayers.remove(player.getName());
		setPlaying(newPlayers);
		getSpectating().add(player.getName());
		player.getInventory().clear();
		player.setGameMode(GameMode.CREATIVE);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.hidePlayer(player);
		}
		player.sendMessage(Main.COMBAT + ChatColor.GRAY
				+ "You are now spectating.");
	}

	public void addNormalSpectator(Player player, Location teleportTo) {
		player.setGameMode(GameMode.CREATIVE);
		player.teleport(teleportTo);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.hidePlayer(player);
		}
		spectatingNormally.add(player.getName());
	}

	public void removeNormalSpectator(Player player) {
		if (player.getGameMode() != GameMode.SURVIVAL) {
			player.setGameMode(GameMode.SURVIVAL);
		}
		player.teleport(player.getWorld().getSpawnLocation());
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.showPlayer(player);
		}
		spectatingNormally.remove(player.getName());
	}

	public void clearNormalSpectators() {
		for (String s : spectatingNormally) {
			Player player = Bukkit.getServer().getPlayerExact(s);
			player.setGameMode(GameMode.SURVIVAL);
			player.teleport(player.getWorld().getSpawnLocation());
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				p.showPlayer(player);
			}
		}
		spectatingNormally.clear();
	}

	public void removeSpectator(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		player.teleport(player.getWorld().getSpawnLocation());
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.showPlayer(player);
		}
		spectating.remove(player.getName());
	}

	public void removePlayer(Player player) {
		player.teleport(player.getWorld().getSpawnLocation());
		playing.remove(player.getName());
	}

	public void startNextRound() {
		setRound(getRound() + 1);
		if (getRound() >= 5) {
			gameOver();
			return;
		}
		Bukkit.getServer().broadcastMessage(
				Main.COMBAT + ChatColor.GREEN + "Round " + getRound() + " in "
						+ getArenaName() + " is starting soon.");
		Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for (String s : getPlaying()) {
					ArenaPlayer arenaPlayer = new ArenaPlayer(Bukkit
							.getPlayer(s), getArena());
					addSpectatorFromAlivePlayer(arenaPlayer.getPlayer());
				}
				getPlaying().clear();
				ArrayList<String> nowPlaying = new ArrayList<String>();
				for (String s : getSpectating()) {
					final ArenaPlayer arenaPlayer = new ArenaPlayer(Bukkit
							.getPlayerExact(s), getArena());
					nowPlaying.add(s);
					arenaPlayer.getPlayer().getInventory().clear();
					arenaPlayer.spawnPlayerInArena();
					restorePlayer(arenaPlayer.getPlayer());
					invinciblePlayers.add(arenaPlayer.getPlayer().getName());
					for (Player player : Bukkit.getServer().getOnlinePlayers()) {
						player.showPlayer(arenaPlayer.getPlayer());
					}
					arenaPlayer
							.getPlayer()
							.sendMessage(
									Main.COMBAT
											+ ChatColor.RED
											+ "Your invincibility wears off in 20 seconds.");
					Bukkit.getServer().getScheduler()
							.runTaskLater(plugin, new Runnable() {
								@Override
								public void run() {
									updateStuffForNextRound(arenaPlayer);
									restorePlayer(arenaPlayer.getPlayer());
									invinciblePlayers.remove(arenaPlayer
											.getPlayer().getName());
								}
							}, 400l);
				}
				getSpectating().clear();
				setPlaying(nowPlaying);
			}
		}, 10l);
	}

	public void announceWinner(Player player) {
		Bukkit.getServer().broadcastMessage(
				player.getName() + " has won round " + getRound() + " in "
						+ getArenaName() + "!");
		if (roundsWon.containsKey(player.getName())) {
			int won = roundsWon.get(player.getName()) + 1;
			roundsWon.remove(player.getName());
			roundsWon.put(player.getName(), won);
		} else {
			roundsWon.put(player.getName(), 1);
		}
	}

	public void announceFinalWinner() {
		List<Map.Entry<String, Integer>> sortedRoundsWon = new ArrayList<Map.Entry<String, Integer>>(
				roundsWon.entrySet());
		Collections.sort(sortedRoundsWon,
				new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> entry1,
							Map.Entry<String, Integer> entry2) {
						return entry1.getValue().compareTo(entry2.getValue());
					}
				});
		String winnerName = null;
		Integer wonRounds = null;
		for (Map.Entry<String, Integer> winners : sortedRoundsWon) {
			winnerName = winners.getKey();
			wonRounds = winners.getValue();
		}
		if (wonRounds == 1) {
			Bukkit.getServer().broadcastMessage(
					Main.COMBAT + ChatColor.GREEN + "By winning " + wonRounds
							+ " round, " + winnerName
							+ " has arisen victorious in " + getArenaName());
		} else {
			Bukkit.getServer().broadcastMessage(
					Main.COMBAT + ChatColor.GREEN + "By winning " + wonRounds
							+ " rounds, " + winnerName
							+ " has arisen victorious in " + getArenaName());
		}
	}

	public void setSpectating(ArrayList<String> spectating) {
		this.spectating = spectating;
	}

	public static boolean isArena(String arena) {
		return settings.getArenas().contains(arena.toLowerCase()) ? true
				: false;
	}

	public static Arena getArena(String playerName) {
		return arenaPlayers.containsKey(playerName) ? arenaPlayers
				.get(playerName) : null;
	}

	public void updateStuffForGameOver(Player player) {
		if (player.getGameMode() != GameMode.SURVIVAL) {
			player.setGameMode(GameMode.SURVIVAL);
		}
		if (player.getAllowFlight() == true) {
			player.setAllowFlight(false);
		}
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.showPlayer(player);
		}
		player.getInventory().clear();
		player.setHealth(20.0);
		player.setExhaustion(0);
		player.setFoodLevel(20);
		player.setSaturation(20);
	}

	public void updateStuffForNextRound(ArenaPlayer arenaPlayer) {
		Player player = arenaPlayer.getPlayer();
		if (!(player.getInventory().containsAtLeast(
				new ItemStack(Material.BOW), 1) && player.getInventory()
				.containsAtLeast(new ItemStack(Material.ARROW), 3))) {
			player.getInventory().addItem(new ItemStack(Material.BOW));
			player.getInventory().addItem(new ItemStack(Material.ARROW, 3));
		}
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			p.hidePlayer(player);
		}
		player.sendMessage(Main.COMBAT + ChatColor.GREEN + "Round "
				+ getRound() + " has started!");
	}

	public void restorePlayer(Player player) {
		if (player.getGameMode() != GameMode.SURVIVAL) {
			player.setGameMode(GameMode.SURVIVAL);
		}
		if (player.getAllowFlight() == true) {
			player.setAllowFlight(false);
		}
		player.setHealth(20.0);
		player.setExhaustion(0);
		player.setFoodLevel(20);
		player.setSaturation(20);
	}

	public void possiblyCloseArena() {
		if (getPlaying().size() + getSpectating().size() <= 1) {
			Bukkit.getServer().broadcastMessage(
					Main.COMBAT + ChatColor.RED
							+ "There aren't enough players to continue in "
							+ getArenaName());
			for (String s : getPlaying()) {
				Player player = Bukkit.getServer().getPlayerExact(s);
				player.teleport(player.getWorld().getSpawnLocation());
				player.getInventory().clear();
				getPlaying().remove(player.getName());
			}
			for (String s : getSpectating()) {
				Player player = Bukkit.getServer().getPlayerExact(s);
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				player.teleport(player.getWorld().getSpawnLocation());
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.showPlayer(player);
				}
			}
			for (String s : getSpectatingNormally()) {
				Player player = Bukkit.getServer().getPlayerExact(s);
				player.setGameMode(GameMode.SURVIVAL);
				player.teleport(player.getWorld().getSpawnLocation());
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.showPlayer(player);
				}
			}
		}
	}

	public void gameOver() {
		Bukkit.getServer().broadcastMessage(
				Main.COMBAT + ChatColor.GREEN + "All rounds in "
						+ getArenaName() + " are over. Come again soon!");
		for (String s : getSpectating()) {
			Player player = Bukkit.getPlayerExact(s);
			player.teleport(player.getWorld().getSpawnLocation());
			updateStuffForGameOver(player);
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				p.showPlayer(player);
			}
			arenaPlayers.remove(player.getName());
			getSpectating().remove(player.getName());
		}
		for (String s : getPlaying()) {
			Player player = Bukkit.getPlayerExact(s);
			if (player != null) {
				player.teleport(player.getWorld().getSpawnLocation());
				updateStuffForGameOver(player);
			}
			arenaPlayers.remove(player.getName());
			getSpectating().remove(player.getName());
		}
		clearNormalSpectators();
		announceFinalWinner();
	}
}