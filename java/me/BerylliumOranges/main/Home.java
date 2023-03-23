package me.BerylliumOranges.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class Home implements Serializable {
	public static final String HOMES_PATH = "plugins/Homes";
	public static final int MAX_HOMES = 1;
	private static final long serialVersionUID = 8850007065784471909L;

	String player;
	String uniqueID;
	Vector direction;
	Vector location;
	UUID world;
	String name;

	public static transient HashMap<String, Home> allHomes = new HashMap<>();

	public Home(Player p, String homeName, Location loc) {
		player = p.getName();
		uniqueID = p.getUniqueId().toString();
		direction = loc.getDirection();
		location = loc.toVector();
		world = loc.getWorld().getUID();
		name = homeName;

		allHomes.put(p.getUniqueId().toString() + homeName, this);
	}

	public static boolean saveData() {
		try {
			File tempMain = new File(HOMES_PATH);
			if (!tempMain.exists())
				tempMain.mkdir();

			for (Map.Entry<String, Home> ent : allHomes.entrySet()) {

				File temp = new File(HOMES_PATH + "/" + ent.getValue().player);
				if (!temp.exists())
					temp.mkdir();

				BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(
						HOMES_PATH + "/" + ent.getValue().player + "/" + ent.getKey().substring(ent.getValue().uniqueID.length()) + ".txt")));
				out.writeObject(ent.getValue());
				out.close();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static HashMap<String, Home> loadData() {
		allHomes.clear();
		try {
			File[] listOfFiles = (new File(HOMES_PATH)).listFiles();
			for (File playerFolder : listOfFiles) {
				if (playerFolder.isDirectory()) {
					for (File f : playerFolder.listFiles()) {
						if (f.getName().endsWith(".txt")) {
							BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(f)));
							Home data = (Home) in.readObject();

							allHomes.put(data.uniqueID + data.name, data);
							in.close();
						}
					}
				}
			}
			return allHomes;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			Bukkit.broadcastMessage(ChatColor.RED + "Error: " + e.getLocalizedMessage());
			return null;
		}
	}

	public Location getLocation() {
		Location loc = new Location(Bukkit.getServer().getWorld(world), location.getX(), location.getY(), location.getZ());
		loc.setDirection(direction);
		return loc;
	}

}
