package com.betterskillcalc;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Better Skill Calculator",
	description = "Skill calculator for all skills with XP multiplier and manual rates",
	tags = {"panel", "skilling", "calculator", "xp"}
)
public class BetterSkillCalculatorPlugin extends Plugin
{
	@Inject
	private BetterSkillCalculatorConfig config;

	@Provides
	BetterSkillCalculatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BetterSkillCalculatorConfig.class);
	}
}
