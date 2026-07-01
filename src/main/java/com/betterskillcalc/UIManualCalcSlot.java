package com.betterskillcalc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;

/**
 * Manual rate calculator shown below the action list. Enter XP/action and/or XP/hr;
 * shows actions-to-target and time-to-target for the current From->To and multiplier.
 */
@Singleton
class UIManualCalcSlot extends JPanel
{
	private static final Pattern NON_NUMERIC = Pattern.compile("[^0-9.]");

	private final FlatTextField xpPerActionField = new FlatTextField();
	private final FlatTextField xpPerHourField = new FlatTextField();
	private final JLabel actionsResult = new JLabel();
	private final JLabel timeResult = new JLabel();

	private int remainingXp;
	private double multiplier = 1.0;

	@Inject
	UIManualCalcSlot()
	{
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 0, 0, 0));

		JPanel body = new JPanel(new DynamicGridLayout(0, 1, 0, 4));
		body.setBorder(new EmptyBorder(10, 10, 10, 10));
		body.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JLabel header = new JLabel("Manual rate");
		header.setForeground(Color.WHITE);
		header.setFont(FontManager.getRunescapeSmallFont());
		body.add(header);

		JPanel fields = new JPanel(new GridLayout(1, 2, 7, 7));
		fields.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		fields.add(buildField("XP per action", xpPerActionField));
		fields.add(buildField("XP per hour", xpPerHourField));
		body.add(fields);

		styleResult(actionsResult);
		styleResult(timeResult);
		body.add(actionsResult);
		body.add(timeResult);

		add(body, BorderLayout.CENTER);
		recompute();
	}

	private static void styleResult(JLabel label)
	{
		label.setForeground(Color.WHITE);
		label.setFont(FontManager.getRunescapeSmallFont());
		label.setBorder(new EmptyBorder(2, 0, 0, 0));
	}

	private JPanel buildField(String label, FlatTextField field)
	{
		JPanel container = new JPanel(new BorderLayout());
		JLabel uiLabel = new JLabel(label);
		uiLabel.setFont(FontManager.getRunescapeSmallFont());
		uiLabel.setForeground(Color.WHITE);
		uiLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

		field.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		field.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		field.setBorder(new EmptyBorder(5, 7, 5, 7));
		field.getTextField().addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				recompute();
			}
		});
		field.getTextField().addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				recompute();
			}
		});

		container.add(uiLabel, BorderLayout.NORTH);
		container.add(field, BorderLayout.CENTER);
		return container;
	}

	/** Push new target context; called whenever From/To/multiplier change. */
	void update(int remainingXp, double multiplier)
	{
		this.remainingXp = remainingXp;
		this.multiplier = multiplier;
		recompute();
	}

	private void recompute()
	{
		double xpPerAction = parse(xpPerActionField);
		double xpPerHour = parse(xpPerHourField);

		if (xpPerAction > 0)
		{
			long actions = Calc.actionsToTarget(remainingXp, xpPerAction, multiplier);
			actionsResult.setText(String.format("%,d actions to target", actions));
		}
		else
		{
			actionsResult.setText("Enter XP/action for action count");
		}

		long secs = Calc.secondsToTarget(remainingXp, xpPerHour, multiplier);
		if (secs >= 0)
		{
			timeResult.setText(Calc.formatDuration(secs) + " to target");
		}
		else
		{
			timeResult.setText("Enter XP/hour for time estimate");
		}
	}

	private static double parse(FlatTextField field)
	{
		try
		{
			String s = NON_NUMERIC.matcher(field.getTextField().getText()).replaceAll("");
			return s.isEmpty() ? 0 : Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
}
