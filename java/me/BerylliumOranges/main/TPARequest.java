package me.BerylliumOranges.main;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class TPARequest {
	public static ArrayList<TPARequest> allTPARequests = new ArrayList<TPARequest>();
	Player p1;
	Player p2;
	int ticks;

	public boolean isForhere() {
		return forhere;
	}

	public void setForhere(boolean forhere) {
		this.forhere = forhere;
	}

	boolean forhere = false;

	public TPARequest(Player p1, Player p2, int ticks) {
		this.p1 = p1;
		this.p2 = p2;
		this.ticks = ticks;
	}

	public Player getP1() {
		return p1;
	}

	public void setP1(Player p1) {
		this.p1 = p1;
	}

	public Player getP2() {
		return p2;
	}

	public void setP2(Player p2) {
		this.p2 = p2;
	}

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

}
