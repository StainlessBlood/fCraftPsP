package com.fCraft.PsP.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.fCraft.PsP.PsP;
import com.fCraft.PsP.commands.handler.Commands;
import com.fCraft.PsP.commands.util.CommandsDispatcher;
import com.fCraft.PsP.commands.util.CommandsUtility;

public class UtilityCommands extends CommandsUtility {
	
	/** Instance of plugin **/
	PsP plugin = PsP.getPlugin();
	
	@Commands(name = "pinfo", syntax = "help [page]", permission = "pinfo.basic.info", description = "Display plugin help information")
	public void showHelp(CommandSender sender, String[] args) {
		List<CommandsDispatcher> commands = this.manager.getAllCommands();
		
		// How many in a page to display at once and how many pages in total
		int display = 3;
		int totalPages = (int)Math.ceil(commands.size() / (double)display);
		
		// Get the page, and check for any possible errors
		int page;
		if(args.length < 2) {
			page = 1;
		} else {
			try {
				page = Integer.parseInt(args[1]);
				
				// Check the help page upper and lower bounds
				if(page < 1) {
					sender.sendMessage(ChatColor.RED + "Error: Page " + ChatColor.GOLD + page + ChatColor.RED + " is not greater than 0.");
					return;
				} else if(page > totalPages) {
					sender.sendMessage(ChatColor.RED + "Error: There are only " + ChatColor.GOLD + totalPages + ChatColor.RED + " pages of help.");
					return;
				}
			} catch(NumberFormatException exc) {
				page = 1;
			}
		}
		sender.sendMessage("");
		sender.sendMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + plugin.getName() + ChatColor.YELLOW + "] Commands help (" + ChatColor.GOLD + "Page " + page + ChatColor.YELLOW + "/" + ChatColor.GOLD + totalPages + ChatColor.YELLOW + "): ");
		
		// Get where to start and when to stop when displaying the help pages
		int base = display * (page - 1);
		for(int i = base; i < (base + display); i++) {
			if(i >= commands.size())
				break;
			Commands cmd = commands.get(i).getMethod().getAnnotation(Commands.class);
			
			// Color format the syntax
			String rawSyntax = cmd.syntax();
			rawSyntax = rawSyntax.replace("<", ChatColor.RED + "<").replace(">", ">" + ChatColor.RESET + ChatColor.YELLOW);
			rawSyntax = rawSyntax.replace("[", ChatColor.DARK_AQUA + "[").replace("]", "]" + ChatColor.RESET + ChatColor.YELLOW);
			
			// Send the result
			String syntax = String.format("/%s %s", new Object[] {cmd.name(), rawSyntax});
			sender.sendMessage(ChatColor.YELLOW + syntax);
			sender.sendMessage("   " + ChatColor.DARK_GREEN + cmd.description());
		}
	}
	
	@Commands(name = "pinfo", syntax = "reload", permission = "pinfo.admin.manage", description = "Reload the plugins configuration values")
	public void reloadConfig(CommandSender sender, String[] args) {
		plugin.reload();
		
		sender.sendMessage("");
		sender.sendMessage(ChatColor.YELLOW + "[" + 
							ChatColor.GOLD + plugin.getName() + 
							ChatColor.YELLOW + " version " + 
							ChatColor.GREEN + plugin.getDescription().getVersion() + 
							ChatColor.YELLOW + "]");
		sender.sendMessage(ChatColor.YELLOW + "  Reload complete!");
	}
	
}
