package com.fCraft.PsP.commands.util;

import java.util.Map.Entry;

public class CommandsSyntax {

	/** The command syntax **/
	private String syntax;
	
	/** The permission 'node' for the command **/
	private String permission;
	
	/** The arguments that follow the command **/
	private String[] arguments;
	
	/**
	 * Constructor setting the syntax and arguments for a command
	 * @param syntax - The syntax for a command as found in the annotation
	 * @param permission - The permission 'node' for the command 
	 */
	public CommandsSyntax(String syntax, String permission) {
		this.syntax = syntax;
		this.permission = permission;
		
		this.arguments = splitArgs(syntax);
	}

	/**
	 * Separates the arguments of a command by splitting it at the
	 * points where any whitespace occurs in the command syntax.
	 * @param syntax - The syntax string to split as found in the annotation
	 * @return A string array of the command arguments as given by the syntax string
	 */
	private String[] splitArgs(String syntax) {
		return syntax.split("\\s+");
	}

	/**
	 * When a player uses a command check the arguments the player gave upon
	 * issuing the command against the arguments of the commands we have defined.
	 * @param entry 
	 * @param args - The arguments the player provided upon issuing the command
	 * @param length 
	 * @return True if we found a command whose arguments match those provided by
	 * the Player issuing the command
	 */
	public CommandsCheck getMatchingArgs(Entry<CommandsSyntax, CommandsDispatcher> entry, String[] args, int length) {
		int downCheck;
		
		// Check if the command has optional argument
		if(args.length < arguments.length)
			downCheck = checkOptioanlArgs(arguments);
		else
			downCheck = arguments.length;
		
		// If the lengths aren't matching up don't waste time
		if(downCheck != args.length)
			return null;

		CommandsCheck match = null;
		for(int i = 0; i < args.length; i++) {
			int cnt;
			
			// If match no longer null get the argument tracking count
			if(match != null)
				cnt = match.getMatchingArgs();
			else
				cnt = 0;
			
			// If the syntax argument contains '<' or '>' new object halfPositive should be true, otherwise false
			if(arguments[i].contains("<") || arguments[i].contains(">") || arguments[i].contains("[") || arguments[i].contains("]"))
				match = new CommandsCheck(entry, cnt + 1, true);
			else if(args[i].equalsIgnoreCase(arguments[i]))
				match = new CommandsCheck(entry, cnt + 1, false);
			else
				continue;
		}
		
		// If match isn't null, argument count and length match, and half positive not true; set full positive true
		if(match != null)
			if(match.getMatchingArgs() == length)
				if(!match.isHalfPositive())
					match.setFullPositive(true);
		return match;
	}
	
	/**
	 * Check if any arguments are optional. If there are optional
	 * arguments don't include in the overall count of arguments.
	 * @param args - The arguments to check
	 * @return Count of arguments excluding optional ones
	 */
	private int checkOptioanlArgs(String[] args) {
		int cnt = 0;
		
		// Check if any arguments contain brackets
		for(String arg : args)
			if(arg.contains("[") || arg.contains("]"))
				continue;
			else
				cnt++;
		return cnt;
	}

	/** Used to get the syntax for a command **/
	public String getSyntax() {
		return syntax;
	}
	
	/**
	 * Used to set the syntax for a command
	 * @param syntax - The command syntax you want to set
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	/** Used to get the permission for a command **/
	public String getPermission() {
		return permission;
	}

	/**
	 * Used to set the permission for a command
	 * @param permission - The permission 'node' to set for a command
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	/** Used to get the arguments for a command **/
	public String[] getArguments() {
		return arguments;
	}

	/**
	 * used to set the arguments for a command
	 * @param arguments - The command arguments you want to set
	 */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

}
