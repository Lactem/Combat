package me.lactem.events;

import me.lactem.arena.Arena;
import me.lactem.coords.Coords;
import me.lactem.main.Main;
import me.lactem.queue.ArenaQueue;
import net.minecraft.server.v1_6_R2.Packet205ClientCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Events implements Listener {
	private Plugin plugin;

	public Events(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Arena arena = Arena.getArena(event.getPlayer().getName());
		if (arena != null) {
			if (arena.getSpectating().contains(event.getPlayer().getName())
					|| arena.getSpectatingNormally().contains(
							event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
		if (event.getPlayer().getItemInHand() == null)
			return;
		if (!Coords.isUniversalWandSet())
			return;
		if (!(event.getPlayer().getItemInHand().getTypeId() == Coords
				.getUniversalWandId()))
			return;
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			event.getPlayer().sendMessage(
					Main.COMBAT + ChatColor.GREEN
							+ "Block one has been successfully set.");
			Coords.getPlayerCoords(event.getPlayer()).setBlock1(
					event.getClickedBlock());
			event.setCancelled(true);
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			event.getPlayer().sendMessage(
					Main.COMBAT + ChatColor.GREEN
							+ "Block two has been successfully set.");
			Coords.getPlayerCoords(event.getPlayer()).setBlock2(
					event.getClickedBlock());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Arena arena = Arena.getArena(event.getPlayer().getName());
		if (arena != null) {
			if (arena.getPlaying().contains(event.getPlayer().getName())
					|| arena.getSpectating().contains(
							event.getPlayer().getName())
					|| arena.getSpectatingNormally().contains(
							event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Arena arena = Arena.getArena(event.getPlayer().getName());
		if (arena != null) {
			if (arena.getPlaying().contains(event.getPlayer().getName())
					|| arena.getSpectating().contains(
							event.getPlayer().getName())
					|| arena.getSpectatingNormally().contains(
							event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		Arena arena = Arena.getArena(event.getWhoClicked().getName());
		if (arena != null) {
			if (arena.getSpectating().contains(event.getWhoClicked().getName())
					|| arena.getSpectatingNormally().contains(
							event.getWhoClicked().getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		ArenaQueue arenaQueue = ArenaQueue.getArenaQueueFromPlayerName(event
				.getPlayer().getName());
		if (arenaQueue != null) {
			arenaQueue.removePlayerFromQueue(event.getPlayer());
			event.getPlayer().teleport(
					event.getPlayer().getWorld().getSpawnLocation());
		}
		Arena arena = Arena.getArena(event.getPlayer().getName());
		if (arena != null) {
			if (arena.getPlaying().contains(event.getPlayer().getName())) {
				arena.removePlayer(event.getPlayer());
				arena.possiblyCloseArena();
			}
			if (arena.getSpectating().contains(event.getPlayer().getName())) {
				arena.removeSpectator(event.getPlayer());
			}
			if (arena.getSpectatingNormally().contains(
					event.getPlayer().getName())) {
				arena.removeNormalSpectator(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		ArenaQueue arenaQueue = ArenaQueue.getArenaQueueFromPlayerName(event
				.getPlayer().getName());
		if (arenaQueue != null) {
			arenaQueue.removePlayerFromQueue(event.getPlayer());
			event.getPlayer().teleport(
					event.getPlayer().getWorld().getSpawnLocation());
		}
		Arena arena = Arena.getArena(event.getPlayer().getName());
		if (arena != null) {
			if (arena.getPlaying().contains(event.getPlayer().getName())) {
				arena.removePlayer(event.getPlayer());
				arena.possiblyCloseArena();
			}
			if (arena.getSpectating().contains(event.getPlayer().getName())) {
				arena.removeSpectator(event.getPlayer());
			}
			if (arena.getSpectatingNormally().contains(
					event.getPlayer().getName())) {
				arena.removeNormalSpectator(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Arena arena = Arena.getArena(event.getPlayer().getName());
		if (arena != null) {
			if (arena.getSpectating().contains(event.getPlayer().getName())
					|| arena.getPlaying().contains(event.getPlayer().getName())
					|| arena.getSpectatingNormally().contains(
							event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Arena arena = Arena.getArena(event.getPlayer().getName());
		if (arena != null) {
			if (arena.getSpectating().contains(event.getPlayer().getName())
					|| arena.getPlaying().contains(event.getPlayer().getName())
					|| arena.getSpectatingNormally().contains(
							event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (Arena.invinciblePlayers.contains(player.getName())) {
				event.setCancelled(true);
			}
		}
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (Arena.invinciblePlayers.contains(player.getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (Arena.invinciblePlayers.contains(player.getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(final PlayerDeathEvent event) {
		Arena arena = Arena.getArena(event.getEntity().getName());
		if (arena == null)
			return;
		if (arena.getPlaying().contains(event.getEntity().getName())) {
			event.setDroppedExp(0);
			event.getDrops().clear();
			Bukkit.getServer().getScheduler()
					.runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							Packet205ClientCommand packet = new Packet205ClientCommand();
							packet.a = 1;
							((CraftPlayer) event.getEntity()).getHandle().playerConnection
									.a(packet);
						}
					}, 100l);
			if (event.getEntity().getKiller() != null) {
				arena.announceWinner(event.getEntity().getKiller());
				if (arena.getPlaying().contains(
						event.getEntity().getKiller().getName())) {
					event.getEntity().getKiller().getInventory()
							.addItem(new ItemStack(Material.ARROW, 2));
				}
			} else {
				for (String s : arena.getPlaying()) {
					Player player = Bukkit.getServer().getPlayerExact(s);
					if (player != null) {
						if (player != event.getEntity()) {
							if (arena.getPlaying().contains(player)) {
								if (arena.getPlaying().size() <= 1) {
									arena.announceWinner(player);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		Arena arena = Arena.getArena(event.getPlayer().getName());
		if (arena == null)
			return;
		if (arena.getPlaying().contains(event.getPlayer().getName())) {
			if (arena.getPlaying().size() <= 1) {
				arena.startNextRound();
			} else {
				arena.addSpectatorFromDeadPlayer(event.getPlayer());
			}
		}
	}
}