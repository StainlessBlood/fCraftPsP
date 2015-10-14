package com.fCraft.PsP;

import java.math.BigDecimal;

public class PlayerAFK {

	/** Time player logs in at **/
	protected long loginTime;
	
	/** Time player logs out at **/
	protected long logoutTime;

	/** Count, in minutes, for AFK time **/
	protected int violations = 0;

	/**
	 * Get the time player played for this session
	 * @param in - Time player logged in at, long
	 * @param out - Time player logged out at, long
	 * @return - The number of minutes spent playing
	 */
	public double calculateTimePlayed(long in, long out) {
		double seconds = (out - in) / 1000d;
		double minutes = seconds / 60d;
		
		return new BigDecimal(minutes).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/** Used to get the time the player logged in at **/
	public long getLoginTime() {
		return loginTime;
	}

	/**
	 * Used to set the time the player logged in at
	 * @param loginTime - The time to set the login at, long
	 */
	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	/** Used to get the time the player logged out at **/
	public long getLogoutTime() {
		return logoutTime;
	}

	/**
	 * Used to set the time the player logged out at
	 * @param logoutTime - The time to set the logout at, long
	 */
	public void setLogoutTime(long logoutTime) {
		this.logoutTime = logoutTime;
	}

	/** Used to get the player's AFK count **/
	public int getViolations() {
		return violations;
	}

	/**
	 * Used to set the player's AFK count 
	 * @param violations - Number of minutes player is/was AFK for (1 violation per minute)
	 */
	public void setViolations(int violations) {
		this.violations = violations;
	}

}
