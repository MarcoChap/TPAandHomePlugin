package me.BerylliumOranges.main;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class EveryTick {
	public static int shutdownTicks = 0;
	public static int shutdownTicksMax = 10 * 60 * 20;

	public static void tick() {
		if (PluginMain.shutdown && Bukkit.getServer().getOnlinePlayers().isEmpty()) {
			shutdownTicks++;
			if (shutdownTicks > shutdownTicksMax) {
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.RED + "Server has had no players for " + (shutdownTicksMax / 1200) + " minutes. Shutting down.");
				Bukkit.shutdown();
				try {
					Runtime.getRuntime().exec("sudo shutdown now");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			shutdownTicks = 0;
		}

		for (int i = TPARequest.allTPARequests.size() - 1; i >= 0; i--) {
			TPARequest r = TPARequest.allTPARequests.get(i);
			r.setTicks(r.getTicks() - 1);
			if (r.getTicks() == 10 * 20) {
				if (r.isForhere())
					r.getP1().sendMessage(ChatColor.GOLD + "TPAHERE request from " + r.getP2().getName() + " expires in 5 seconds");
				else
					r.getP1().sendMessage(ChatColor.GOLD + "TPA request from " + r.getP2().getName() + " expires in 5 seconds");
			} else if (r.getTicks() <= 0) {
				if (r.isForhere()) {
					r.getP2().sendMessage(ChatColor.RED + "TPAHERE request from " + r.getP1().getName() + " expired");
					r.getP1().sendMessage(ChatColor.RED + "TPAHERE request to " + r.getP2().getName() + " expired");
				} else {
					r.getP2().sendMessage(ChatColor.RED + "TPA request from " + r.getP1().getName() + " expired");
					r.getP1().sendMessage(ChatColor.RED + "TPA request to " + r.getP2().getName() + " expired");
				}
				TPARequest.allTPARequests.remove(i);
			}
		}
	}
}
