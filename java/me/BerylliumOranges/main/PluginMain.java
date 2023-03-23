package me.BerylliumOranges.main;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.BerylliumOranges.listeners.purityItems.ItemBuilder;
import me.BerylliumOranges.listeners.purityItems.traits.PurityItemAbstract;
import net.md_5.bungee.api.ChatColor;

public class PluginMain extends JavaPlugin implements Listener {
	private static PluginMain instance;
	public static boolean shutdown = true;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;

		new PlayerListener();
		Home.loadData();

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				EveryTick.tick();
			}
		}, 0, 0);
	}

	@Override
	public void onDisable() {
		Home.saveData();
	}

	public static PluginMain getInstance() {
		return instance;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && cmd.getName().equalsIgnoreCase("tpahere")) {
			if (args.length == 1) {
				for (Player p : getServer().getOnlinePlayers()) {
					if (args[0].equalsIgnoreCase(p.getName())) {
						TPARequest r = new TPARequest((Player) sender, p, 20 * 20);
						r.setForhere(true);
						TPARequest.allTPARequests.add(r);
						sender.sendMessage(ChatColor.GOLD + "TPAHERE request made to " + p.getName());
						p.sendMessage(ChatColor.GOLD + sender.getName() + " would like you to teleport " + ChatColor.BOLD + "to them");
						p.sendMessage(ChatColor.GOLD + "use /tpaccept " + sender.getName() + " to accept");
						return true;
					}
				}
				sender.sendMessage(ChatColor.RED + "Player " + args[0] + " was not found");
			} else
				return false;
		}
		if (sender instanceof Player && cmd.getName().equalsIgnoreCase("tpa")) {
			Player player = (Player) sender;
			if (args.length == 1) {

				for (Player p : getServer().getOnlinePlayers()) {
					if (args[0].equalsIgnoreCase(p.getName())) {
						if (PurityItemAbstract.hasPotionTrait(player, "[Home]")) {
							player.teleport(p);
						} else {
							TPARequest.allTPARequests.add(new TPARequest((Player) sender, p, 20 * 20));
							sender.sendMessage(ChatColor.GOLD + "Teleport request made to " + p.getName());
							p.sendMessage(ChatColor.GOLD + sender.getName() + " would like to teleport to you");
							p.sendMessage(ChatColor.GOLD + "use /tpaccept " + sender.getName() + " to accept");
						}
						return true;
					}
				}
				sender.sendMessage(ChatColor.RED + "Player " + args[0] + " was not found");
			} else
				return false;
		} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("tpaccept")) {
			if (args.length == 1) {
				TPARequest found = null;
				for (TPARequest r : TPARequest.allTPARequests) {
					if (r.getP2().getName().equals(sender.getName()) && r.getP1().getName().equalsIgnoreCase(args[0])) {
						found = r;
					}
				}
				if (found != null) {
					TPARequest.allTPARequests.remove(found);
					startTeleport(found.getP1(), found.getP2(), found.isForhere());
				} else {
					sender.sendMessage(ChatColor.RED + "No TPA request exists from " + args[0]);
				}
				return true;
			} else if (args.length == 0) {
				int c = 0;
				TPARequest found = null;
				for (TPARequest r : TPARequest.allTPARequests) {
					if (r.getP2().equals(sender)) {
						c++;
						found = r;
					}
				}
				if (found == null) {
					sender.sendMessage(ChatColor.RED + "No TPA request exist");
				} else if (c > 1) {
					found.getP1().sendMessage(ChatColor.RED + "Multiple teleport requests exist, use /tpaccept <player>");
				} else {
					TPARequest.allTPARequests.remove(found);
					startTeleport(found.getP1(), found.getP2(), found.isForhere());
				}
				return true;
			} else if (args.length > 1) {
				return false;
			}
		} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("home")) {
			Player p = (Player) sender;
			String name = "home";
			if (args.length == 1) {
				name = args[0];
			}

			int numHomes = getMaxHomes(p);
			int count = 0;
			boolean found = false;
			for (Map.Entry<String, Home> h : Home.allHomes.entrySet()) {
				if (h.getValue().uniqueID.equals(p.getUniqueId().toString())) {
					count++;
				}
				if (count > numHomes) {
					p.sendMessage(ChatColor.RED + "You don't have access to this home, you have too many homes set!");
					found = true;
					break;
				}
				if (h.getValue().uniqueID.equals(p.getUniqueId().toString()) && h.getValue().name.equals(name)) {
					p.teleport(h.getValue().getLocation());
					found = true;
					break;
				}
			}
			if (!found) {
				p.sendMessage(ChatColor.RED + "No home named \'" + ChatColor.WHITE + name + ChatColor.RED + "\' found!");
			}
			return true;
		} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("sethome")) {
			Player p = (Player) sender;
			int numHomes = getMaxHomes(p);
			String name = "home";
			if (args.length == 1) {
				name = args[0];
			}

			int count = 0;
			ArrayList<String> homes = new ArrayList<>();
			for (Map.Entry<String, Home> h : Home.allHomes.entrySet()) {
				if (h.getValue().uniqueID.equals(p.getUniqueId().toString())) {
					homes.add(h.getValue().name);
					count++;
				}
			}

			boolean found = false;
			if (Home.allHomes.containsKey(p.getUniqueId().toString() + name)) {
				found = true;
			}

			if (found || count < numHomes) {
				new Home(p, name, p.getLocation());
			} else {

				if (numHomes == 1) {
					p.sendMessage(ChatColor.RED + "You already have a home!");
				} else
					p.sendMessage(ChatColor.RED + "You already have the maximum number(" + ChatColor.WHITE + (numHomes - Home.MAX_HOMES) + " + "
							+ Home.MAX_HOMES + " = " + numHomes + ChatColor.RED + ") of homes!");
				for (String h : homes) {
					p.sendMessage(" - " + h);
				}
				return true;
			}

			if (found) {
				p.sendMessage(ChatColor.GREEN + "Successfully replaced \'" + ChatColor.WHITE + name + ChatColor.GREEN + "\'.");
			} else {
				p.sendMessage(ChatColor.GREEN + "Successfully added \'" + ChatColor.WHITE + name + ChatColor.GREEN + "\'.");
			}
			return true;
		} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("listhome")) {
			Player p = (Player) sender;
			boolean found = false;
			boolean noAccess = false;
			int numHomes = getMaxHomes(p);
			int count = 0;
			for (Map.Entry<String, Home> h : Home.allHomes.entrySet()) {
				if (h.getValue().uniqueID.equals(p.getUniqueId().toString())) {
					count++;
					if (!found) {
						p.sendMessage("");
						p.sendMessage(ChatColor.GREEN + "Homes:");
					}
					if (count > numHomes) {
						if (!noAccess) {
							p.sendMessage(ChatColor.RED + "Homes you don't have access to: ");
						}
						noAccess = true;
					}
					p.sendMessage(" - " + h.getValue().name);
					found = true;
				}
			}
			if (!found) {
				p.sendMessage(ChatColor.GREEN + "You have no homes");
			}
			return true;
		} else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("delhome")) {
			Player p = (Player) sender;
			if (args.length == 1) {
				if (Home.allHomes.remove(p.getUniqueId().toString() + args[0]) != null) {
					p.sendMessage(ChatColor.GREEN + "Successfully deleted \'" + ChatColor.WHITE + args[0] + ChatColor.GREEN + "\'.");
				} else {
					p.sendMessage(ChatColor.RED + "\'" + ChatColor.WHITE + args[0] + ChatColor.RED + "\' does not exist!");
				}
				return true;
			}
			return false;
		}

		if (sender instanceof Player && sender.isOp() && cmd.getName().equalsIgnoreCase("stopshutdown")) {
			try {
				if (args.length == 1) {
					int time = Math.max(Integer.parseInt(args[0]), 5);
					EveryTick.shutdownTicksMax = time * 20 * 60;
					sender.sendMessage("The EC2 instance " + ChatColor.GREEN + "will " + ChatColor.WHITE + "shutdown after " + ChatColor.GREEN + time
							+ ChatColor.WHITE + " minutes.");
					shutdown = true;
					return true;
				}
			} catch (Exception er) {
				return false;
			}
			shutdown = !shutdown;
			if (!shutdown)
				sender.sendMessage("The EC2 instance " + ChatColor.RED + "will not " + ChatColor.WHITE + "shutdown.");
			else
				sender.sendMessage("The EC2 instance " + ChatColor.GREEN + "will " + ChatColor.WHITE + "shutdown after "
						+ EveryTick.shutdownTicksMax / (20 * 60) + " minutes.");
			return true;
		}
		return false;
	}

	public static int getMaxHomes(Player p) {
		return ItemBuilder.sumOfTraitInEquipment(p, "[Home]", true) + Home.MAX_HOMES;
	}

	public static void startTeleport(Player p1, Player p2, boolean forHere) {
		p1.sendMessage(ChatColor.GOLD + "Teleport request accepted");
		p2.sendMessage(ChatColor.GOLD + "Teleport request accepted! Teleporting...");
		Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			@Override
			public void run() {
				if (forHere) {
					p2.teleport(p1);
				} else
					p1.teleport(p2);
			}
		}, 20);
	}
}
