package com.fCraft.PsP.commands.util;

import java.util.Map.Entry;

public class CommandsCheck {
	
	/** The number of matching arguments **/
	private int matchingArgs = 0;
	
	/** Partial command match **/
	private boolean halfPositive;
	
	/** Full command match **/
	private boolean fullPositive;

	/** The dispatcher relating to the command **/
	private Entry<CommandsSyntax, CommandsDispatcher> entry;

	/**
	 * Constructor to set values. The number of arguments as well as
	 * the partial command match are checked is a full positive match
	 * was not found. If half positive is true and number of arguments
	 * match then we found a matching command.
	 * @param entry - The entry to check if user has permission, and the dispatcher to later invoke
	 * @param args - A count of the number of arguments
	 * @param half - Whether or not to set the half positive match
	 */
	public CommandsCheck(Entry<CommandsSyntax, CommandsDispatcher> entry, int args, boolean half) {
		this.entry = entry;
		this.matchingArgs = args;
		this.halfPositive = half;
	}

	/** Used to get the number of matching command arguments **/
	public int getMatchingArgs() {
		return matchingArgs;
	}

	/**
	 * Used to set the number of matching command arguments
	 * @param matchingArgs - The number to set in terms of how many arguments match
	 */
	public void setMatchingArgs(int matchingArgs) {
		this.matchingArgs = matchingArgs;
	}

	/** Used to check if partial command match **/
	public boolean isHalfPositive() {
		return halfPositive;
	}

	/**
	 * Used to set that part of the command matches
	 * @param halfPositive - True if partial command match
	 */
	public void setHalfPositive(boolean halfPositive) {
		this.halfPositive = halfPositive;
	}

	/** Used to check if full command match **/
	public boolean isFullPositive() {
		return fullPositive;
	}

	/**
	 * Used to set that all of the command matches
	 * @param fullPositive - True if all of the command matches
	 */
	public void setFullPositive(boolean fullPositive) {
		this.fullPositive = fullPositive;
	}

	/** Used to get the command entry containing permissions and the method to later invoke **/
	public Entry<CommandsSyntax, CommandsDispatcher> getCommandEntry() {
		return entry;
	}
}
