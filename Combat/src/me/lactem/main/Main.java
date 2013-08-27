package me.lactem.main;

import java.util.ArrayList;
import java.util.Random;

import me.lactem.arena.Arena;
import me.lactem.arena.CreateArena;
import me.lactem.coords.Coords;
import me.lactem.events.Events;
import me.lactem.queue.ArenaQueue;
import me.lactem.queue.QueuePlayer;
import me.lactem.utils.SettingsManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	SettingsManager settings = SettingsManager.getInstance();
	public static String COMBAT = ChatColor.WHITE + "[" + ChatColor.GOLD
			+ "Combat" + ChatColor.WHITE + "] ";

	@Override
	public void onEnable() {
		Coords.setUniversalWandId(2262);
		Coords.setPermission("combat.wand");
		settings.setup(this);
		getServer().getPluginManager().registerEvents(new Events(this), this);
		settings.getArenas().options().copyDefaults(false);
		settings.saveArenas();
		settings.getStats().options().copyDefaults(false);
		settings.saveStats();
		getConfig().options().copyDefaults(false);
		saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("reload")) {
			getServer()
					.broadcastMessage(
							COMBAT
									+ ChatColor.RED
									+ "All arenas had to be cancelled because of a reload.");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("Combat")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Combat commands can only be used in-game.");
				return true;
			}
			Player player = (Player) sender;
			if (args.length == 0) {
				player.sendMessage(COMBAT + "Available commands:");
				if (player.hasPermission("combat.create")) {
					player.sendMessage(COMBAT + "/combat create <arena>");
				}
				if (player.hasPermission("combat.join")) {
					player.sendMessage(COMBAT + "/combat join <arena>");
				}
				if (player.hasPermission("combat.leave")) {
					player.sendMessage(COMBAT + "/combat leave");
				}
				if (player.hasPermission("combat.spectate")) {
					player.sendMessage(COMBAT + "/combat spectate <player>");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("create")) {
				if (!player.hasPermission("combat.create")) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "No permission!");
					return true;
				}
				if (args.length < 2) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "Incorrect usage. " + ChatColor.GREEN
							+ "Use /combat create <arena>.");
					return true;
				}
				Coords coords = Coords.getPlayerCoords(player);
				if (!coords.areBothPointsSet()) {
					player.sendMessage(COMBAT
							+ ChatColor.RED
							+ "You must select both points of the arena. Right click on point and left click the other.");
					return true;
				}
				if (coords.areBlocksInDifferentWorlds()) {
					player.sendMessage(COMBAT
							+ ChatColor.RED
							+ "The two blocks you have selected are in different worlds!");
					return true;
				}
				CreateArena createArena = new CreateArena(args[1], player);
				createArena.addDetailsToConfig();
				createArena.createArena();
				player.sendMessage(COMBAT + ChatColor.GREEN + "Arena "
						+ ChatColor.WHITE + args[1] + ChatColor.GREEN
						+ " was successfully created!");
				return true;
			} else if (args[0].equalsIgnoreCase("join")) {
				if (!player.hasPermission("combat.join")) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "No permission!");
					return true;
				}
				if (args.length < 2) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "Incorrect usage. " + ChatColor.GREEN
							+ "Use /combat join <arena>.");
					return true;
				}
				if (!Arena.isArena(args[1])) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "The specified arena does not exist.");
					return true;
				}
				if (ArenaQueue.allArenaQueues.containsKey(player.getName())) {
					player.sendMessage(COMBAT
							+ ChatColor.RED
							+ "You can't join an arena if you're already in one!");
					return true;
				}
				if (!ArenaQueue.doesArenaQueueExist(args[1])) {
					ArrayList<QueuePlayer> queuePlayers = new ArrayList<QueuePlayer>();
					QueuePlayer queuePlayer = new QueuePlayer(player, args[1]);
					queuePlayers.add(queuePlayer);
					@SuppressWarnings("unused")
					ArenaQueue arenaQueue = new ArenaQueue(queuePlayers,
							args[1]);
					player.sendMessage(COMBAT
							+ ChatColor.GREEN
							+ "You've been added to the queue. Waiting for more players to join...");
				} else {
					ArenaQueue arenaQueue = ArenaQueue.getArenaQueue(args[1]);
					if (arenaQueue.getQueuePlayers().contains(
							QueuePlayer.getQueuePlayer(player.getName()))) {
						player.sendMessage(COMBAT + ChatColor.RED
								+ "You're already in this arena!");
						return true;
					}
					arenaQueue.addPlayerToQueue(player);
					player.sendMessage(COMBAT + ChatColor.GREEN
							+ "You've been added to the queue.");
					if (arenaQueue.getPlayersInQueue() >= 2) {
						if (!arenaQueue.startedCountdown()) {
							arenaQueue.setPlugin(this);
							arenaQueue.startCountdown();
						}
					}
				}
				return true;
			} else if (args[0].equalsIgnoreCase("leave")) {
				if (!player.hasPermission("combat.leave")) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "No permission!");
					return true;
				}
				ArenaQueue arenaQueue = ArenaQueue
						.getArenaQueueFromPlayerName(player.getName());
				if (arenaQueue == null) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "You're not in a queue for any arena!");
					return true;
				}
				arenaQueue.removePlayerFromQueue(player);
				player.teleport(player.getWorld().getSpawnLocation());
			} else if (args[0].equalsIgnoreCase("spectate")) {
				if (!player.hasPermission("combat.spectate")) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "No permission!");
					return true;
				}
				if (Arena.getArena(player.getName()) != null) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "You can't spectate while you're in an arena!");
					return true;
				}
				if (args.length < 2) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "Incorrect usage. " + ChatColor.GREEN
							+ "Use /combat spectate <player>.");
					return true;
				}
				try {
					getServer().getPlayer(args[1]).getFoodLevel();
				} catch (NullPointerException e) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "The specified player could not be found.");
					return true;
				}
				if (Arena.getArena(args[1]) == null) {
					player.sendMessage(COMBAT + ChatColor.RED
							+ "That player is not in any arena.");
					return true;
				}
				Arena arena = Arena.getArena(args[1]);
				arena.addNormalSpectator(player, getServer().getPlayer(args[1])
						.getLocation());
			}
			return true;
		}
		return true;
	}

	public static boolean locationIsInCuboid(Location location, Arena arena) {
		boolean trueOrNot = false;
		int block1X = arena.getBlock1X();
		int block1Y = arena.getBlock1Y();
		int block1Z = arena.getBlock1Z();
		int block2X = arena.getBlock2X();
		int block2Y = arena.getBlock2Y();
		int block2Z = arena.getBlock2Z();
		if (location.getWorld() == Bukkit.getWorld(arena.getWorld())) {
			if (location.getX() >= block1X && location.getX() <= block2X) {
				if (location.getY() >= block1Y && location.getY() <= block2Y) {
					if (location.getZ() >= block1Z
							&& location.getZ() <= block2Z) {
						trueOrNot = true;
					}
				}
			}
			if (location.getX() <= block1X && location.getX() >= block2X) {
				if (location.getY() <= block1Y && location.getY() >= block2Y) {
					if (location.getZ() <= block1Z
							&& location.getZ() >= block2Z) {
						trueOrNot = true;
					}
				}
			}
		}
		return trueOrNot;
	}

	public static Location generateRandomSpawn(Arena arena, Player player) {
		Location location = player.getLocation();
		location = keepChecking(location, arena);
		return location;
	}

	public static Location randomLocation(Arena arena) {
		Random random = new Random();
		int range = 100 - -100 + 1;
		int next = random.nextInt(range) + -100;
		Location location = new Location(Bukkit.getWorld(arena.getWorld()),
				arena.getBlock1X(), arena.getBlock1Y(), arena.getBlock1Z())
				.add(next, 0, 0);
		range = 25 - -25 + 1;
		next = random.nextInt(range) + -25;
		location.add(0, next, 0);
		range = 100 - -100 + 1;
		next = random.nextInt(range) + -100;
		location.add(0, 0, next);
		return location;
	}

	public static void testLocation(Arena arena) {
		Location location = randomLocation(arena);
		locationIsInCuboid(location, arena);
	}

	public static boolean isLocationInBlock(Location location) {
		if (location.getBlock() == null)
			return false;
		if (location.getBlock().getType() == Material.AIR)
			return false;
		return true;
	}

	public static Location keepChecking(Location location, Arena arena) {
		while (!locationIsInCuboid(location, arena)) {
			location = randomLocation(arena);
		}
		location = location.getWorld().getHighestBlockAt(location)
				.getLocation();
		return location;
	}
}