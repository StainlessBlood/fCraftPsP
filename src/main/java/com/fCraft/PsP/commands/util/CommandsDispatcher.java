package com.fCraft.PsP.commands.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandsDispatcher {

	/** The class instance for a command **/
	private Object object;
	
	/** The method reference for a command **/
	private Method method;

	/**
	 * Constructor to set the command class and method
	 * @param object - Instance of the class holding a command
	 * @param method - Reference of the method that is to be invoked on command
	 */
	public CommandsDispatcher(Object object, Method method) {
		this.object = object;
		this.method = method;
	}

	/**
	 * Call the command method inside the 'object' class passing 'args' for parameters
	 * @param args - Arbitrary number of arguments to pass to the method we're invoking
	 */
	public void call(Object... args) {
		try {
			this.method.invoke(this.object, args);
		} catch (IllegalAccessException exc) {
			exc.printStackTrace();
		} catch (IllegalArgumentException exc) {
			exc.printStackTrace();
		} catch (InvocationTargetException exc) {
			exc.printStackTrace();
		}
	}

	/** Used to get the method correlating to a command **/
	public Method getMethod() {
		return method;
	}

}
