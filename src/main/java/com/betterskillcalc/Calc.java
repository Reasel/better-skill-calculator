package com.betterskillcalc;

/**
 * Pure XP/action/time math. No client dependencies so it is unit-testable.
 */
public final class Calc
{
	private Calc()
	{
	}

	/** Remaining XP from {@code fromXp} to {@code toXp}, clamped to >= 0. */
	public static int remainingXp(int fromXp, int toXp)
	{
		return Math.max(0, toXp - fromXp);
	}

	/** Multipliers <= 0 fall back to 1.0. */
	private static double effMult(double mult)
	{
		return mult <= 0 ? 1.0 : mult;
	}

	/**
	 * Actions to gain {@code remainingXp} at {@code xpPerAction} base xp times {@code mult}.
	 * Returns 0 when remaining <= 0 or xpPerAction <= 0.
	 */
	public static long actionsToTarget(int remainingXp, double xpPerAction, double mult)
	{
		if (remainingXp <= 0 || xpPerAction <= 0)
		{
			return 0;
		}
		return (long) Math.ceil(remainingXp / (xpPerAction * effMult(mult)));
	}

	/**
	 * Seconds to gain {@code remainingXp} at {@code xpPerHour} times {@code mult}.
	 * Returns 0 when remaining <= 0; returns -1 when xpPerHour <= 0 (caller hides the line).
	 */
	public static long secondsToTarget(int remainingXp, double xpPerHour, double mult)
	{
		if (xpPerHour <= 0)
		{
			return -1;
		}
		if (remainingXp <= 0)
		{
			return 0;
		}
		double perSec = xpPerHour * effMult(mult) / 3600.0;
		return (long) Math.ceil(remainingXp / perSec);
	}

	/** "3d 4h", "2h 15m", "45m", "<1m", or "0m". Input is seconds. */
	public static String formatDuration(long seconds)
	{
		if (seconds <= 0)
		{
			return "0m";
		}
		long totalMinutes = seconds / 60;
		if (totalMinutes == 0)
		{
			return "<1m";
		}
		long days = totalMinutes / 1440;
		long hours = (totalMinutes % 1440) / 60;
		long minutes = totalMinutes % 60;
		if (days > 0)
		{
			return days + "d " + hours + "h";
		}
		if (hours > 0)
		{
			return hours + "h " + minutes + "m";
		}
		return minutes + "m";
	}
}
