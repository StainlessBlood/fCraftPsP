package com.fCraft.PsP;

import org.bukkit.Location;
import org.bukkit.World;

public class PlayerLocation extends Location {

	private World world;
	private double x;
	private double y;
	private double z;
	
	private boolean active;

	public PlayerLocation(World world, double x, double y, double z) {
		super(world, x, y, z);
		
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.active = false;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
