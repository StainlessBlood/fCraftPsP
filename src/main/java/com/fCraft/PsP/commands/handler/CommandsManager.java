package com.fCraft.PsP.commands.handler;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.fCraft.PsP.PsP;
import com.fCraft.PsP.commands.util.CommandsCheck;
import com.fCraft.PsP.commands.util.CommandsDispatcher;
import com.fCraft.PsP.commands.util.CommandsSyntax;

public class CommandsManager {

	/** Map of available commands **/
	private LinkedHashMap<String, Map<CommandsSyntax, CommandsDispatcher>> dispatchMap;

	/** Plugin instance **/
	private PsP plugin;

	/**
	 * Constructor to do some initial settings
	 * @param plugin
	 */
	public CommandsManager(PsP plugin) {
		this.plugin = plugin;
		this.dispatchMap = new LinkedHashMap<String, Map<CommandsSyntax, CommandsDispatcher>>();
	}

	/**
	 * Get the commands from the passed class object
	 * @param classObj - The class that we're checking commands for
	 */
	public void registerCmds(CommandsListener listener) {
		Class<?> cmdClass = listener.getClass();

		// Loop through all the methods of the class
		for(Method method : cmdClass.getDeclaredMethods()) {
			// If the method has a matching annotation, defined by the Commands class
			if(method.isAnnotationPresent(Commands.class)) {
				Commands cmdAnt = (Commands)method.getAnnotation(Commands.class);
	
				// Get the current method annotation and see if the map of commands has this command yet
				Map<CommandsSyntax, CommandsDispatcher> cmdMap = dispatchMap.get(cmdAnt.name());
				if(cmdMap == null) {
					cmdMap = new LinkedHashMap<CommandsSyntax, CommandsDispatcher>();
					
					// Map didn't contain an entry matching the command annotation's name so add it to the map
					dispatchMap.put(cmdAnt.name(), cmdMap);
				}
				cmdMap.put(new CommandsSyntax(cmdAnt.syntax(), cmdAnt.permission()), new CommandsDispatcher(listener, method));
			}
		}
		listener.onRegister(this);
	}

	/**
	 * Attempt to execute a PsP command. Checks if what the sender typed is a valid
	 * command; if it is call invoke the method that correlates to the syntax of
	 * the arguments given by what the sender typed in after the PsP command.
	 * @param sender - The player/console that issued the command
	 * @param cmd - The command issued
	 * @param args - The arguments added after the command
	 * @return
	 */
	public boolean executeCmds(CommandSender sender, Command cmd, String[] args) {
		Map<CommandsSyntax, CommandsDispatcher> callMap = dispatchMap.get(cmd.getName());

		// If the command wasn't found in the call map return immediately
		if(callMap == null)
			return false;
		
		// Find the method that has the command syntax that matches the arguments provided by the sender
		CommandsCheck cmdMatch = null;
		for(Map.Entry<CommandsSyntax, CommandsDispatcher> entry : callMap.entrySet()) {
			CommandsSyntax syntax = entry.getKey();
			
			// Run a check against each match in the callMap for arguments that match the ones the sender provided
			CommandsCheck check = syntax.getMatchingArgs(entry, args, args.length);
			if(check == null) {
				continue;
			} else if(check.isFullPositive()) {
				cmdMatch = check;
				break;
			} else if(check.isHalfPositive() && (check.getMatchingArgs() == args.length)) {
				cmdMatch = check;
			}
		}
		
		// If we found a match check if sender has permissions to use
		if(cmdMatch != null) {
			String perms = cmdMatch.getCommandEntry().getKey().getPermission();
			
			// If the sender does have permission, invoke the method whose syntax matches the arguments provided by the sender
			if(sender.hasPermission(perms))
				cmdMatch.getCommandEntry().getValue().call(new Object[] {sender, args});
			else
				sender.sendMessage(ChatColor.RED + "Error: You do not have the permission - " + 
									ChatColor.GOLD + perms + ChatColor.RED + " to use this command.");
		} else {
			sender.sendMessage(ChatColor.RED + "Error: Invalid command. Refer to plugin help for list of available commands");
		}
		return true;
	}

	/**
	 * Get all the available commands and sort them
	 * @return Sorted list of all the available commands
	 */
	public List<CommandsDispatcher> getAllCommands() {
		List<CommandsDispatcher> commands = new LinkedList<CommandsDispatcher>();
		
		// Add them to the list then sort the list
		for(Map<CommandsSyntax, CommandsDispatcher> entry : dispatchMap.values())
			commands.addAll(entry.values());
		Collections.sort(commands, new CommandsComparator());
		
		return commands;
	}
	
	/** Get plugin instance **/
	public PsP getPlugin() {
		return plugin;
	}
	
	public class CommandsComparator implements Comparator<CommandsDispatcher> {
		
		@Override
		public int compare(CommandsDispatcher cd1, CommandsDispatcher cd2) {
			String syntax1 = cd1.getMethod().getAnnotation(Commands.class).syntax();
			String syntax2 = cd2.getMethod().getAnnotation(Commands.class).syntax();
			
			return syntax1.compareTo(syntax2);
		}
		
	}
}
