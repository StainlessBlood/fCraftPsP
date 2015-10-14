package com.fCraft.PsP.util;

import org.bukkit.ChatColor;

public enum Colors {
	AQUA(ChatColor.AQUA),
	BLACK(ChatColor.BLACK),
	BLUE(ChatColor.BLUE),
	BOLD(ChatColor.BOLD),
	DARK_AQUA(ChatColor.DARK_AQUA),
	DARK_BLUE(ChatColor.DARK_BLUE),
	DARK_GRAY(ChatColor.DARK_GRAY),
	DARK_GREEN(ChatColor.DARK_GREEN),
	DARK_PURPLE(ChatColor.DARK_PURPLE),
	DARK_RED(ChatColor.DARK_RED),
	GOLD(ChatColor.GOLD),
	GRAY(ChatColor.GRAY),
	GREEN(ChatColor.GREEN),
	ITALIC(ChatColor.ITALIC),
	LIGHT_PURPLE(ChatColor.LIGHT_PURPLE),
	MAGIC(ChatColor.MAGIC),
	RED(ChatColor.RED),
	RESET(ChatColor.RESET),
	STRIKETHROUGH(ChatColor.STRIKETHROUGH),
	UNDERLINE(ChatColor.UNDERLINE),
	WHITE(ChatColor.WHITE),
	YELLOW(ChatColor.YELLOW);
	
	private ChatColor color;
	
	private Colors(ChatColor color) {
		this.color = color;
	}
	
	public ChatColor getValue() {
		return color;
	}
}
