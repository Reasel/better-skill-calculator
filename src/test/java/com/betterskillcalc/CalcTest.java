package com.betterskillcalc;

import net.runelite.api.Experience;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CalcTest
{
	@Test
	public void level50IsKnownXp()
	{
		// Anchors the test data: level 50 == 101,333 xp, and level<->xp round-trips.
		assertEquals(101_333, Experience.getXpForLevel(50));
		assertEquals(50, Experience.getLevelForXp(Experience.getXpForLevel(50)));
	}

	@Test
	public void remainingXpClampsToZero()
	{
		assertEquals(90_000, Calc.remainingXp(11_333, 101_333));
		assertEquals(0, Calc.remainingXp(101_333, 11_333));
	}

	@Test
	public void actionsToTargetCeils()
	{
		assertEquals(845L, Calc.actionsToTarget(101_333, 120, 1.0)); // ceil(844.44)
		assertEquals(563L, Calc.actionsToTarget(101_333, 120, 1.5)); // ceil(562.96)
		assertEquals(0L, Calc.actionsToTarget(0, 120, 1.0));
		assertEquals(0L, Calc.actionsToTarget(101_333, 0, 1.0));
	}

	@Test
	public void multiplierGuardTreatsNonPositiveAsOne()
	{
		assertEquals(Calc.actionsToTarget(101_333, 120, 1.0),
			Calc.actionsToTarget(101_333, 120, 0.0));
		assertEquals(Calc.actionsToTarget(101_333, 120, 1.0),
			Calc.actionsToTarget(101_333, 120, -5.0));
	}

	@Test
	public void secondsToTargetAndFormatting()
	{
		// 101,333 xp at 45,000 xp/hr = 8107s = 135 min = 2h 15m
		assertEquals(8107L, Calc.secondsToTarget(101_333, 45_000, 1.0));
		assertEquals("2h 15m", Calc.formatDuration(8107L));
		assertEquals(-1L, Calc.secondsToTarget(101_333, 0, 1.0)); // no rate -> hidden
		assertEquals(0L, Calc.secondsToTarget(0, 45_000, 1.0));
	}

	@Test
	public void formatDurationBuckets()
	{
		assertEquals("0m", Calc.formatDuration(0));
		assertEquals("<1m", Calc.formatDuration(30));
		assertEquals("45m", Calc.formatDuration(45 * 60));
		assertEquals("1d 1h", Calc.formatDuration(90_000)); // 25h
	}
}
