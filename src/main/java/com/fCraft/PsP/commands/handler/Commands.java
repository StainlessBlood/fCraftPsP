package com.fCraft.PsP.commands.handler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Commands {
	/** The name of the command, e.g. the first part after the '/' **/
	String name();
	
	/** The syntax for the command the player is attempting to use **/
	String syntax();
	
	/** The permission for the command the player is attempting to use **/
	String permission();

	/** The description for a command **/
	String description();
}
