/*
 * Copyright (c) 2018, Kruithne <kruithne@gmail.com>
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.betterskillcalc;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import lombok.Getter;
import static com.betterskillcalc.BetterSkillCalculator.MAX_XP_MULTIPLIER;
import static com.betterskillcalc.BetterSkillCalculator.MIN_XP_MULTIPLIER;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;

@Getter
@Singleton
class UICalculatorInputArea extends JPanel
{
	private static final Pattern NON_NUMERIC = Pattern.compile("\\D");
	private final JTextField uiFieldCurrentLevel;
	private final JTextField uiFieldCurrentXP;
	private final JTextField uiFieldTargetLevel;
	private final JTextField uiFieldTargetXP;
	private final JSpinner uiFieldXPMultiplier;
	// Read-only text field (not a JLabel) so the "XP required" value can be selected and copied.
	private final JTextField neededXpLabel;

	@Inject
	UICalculatorInputArea()
	{
		setLayout(new BorderLayout(0, 7));

		final JPanel grid = new JPanel(new GridLayout(3, 2, 7, 7));
		uiFieldCurrentLevel = addComponent(grid, "Current Level");
		uiFieldCurrentXP = addComponent(grid, "Current Experience");
		uiFieldTargetLevel = addComponent(grid, "Target Level");
		uiFieldTargetXP = addComponent(grid, "Target Experience");
		uiFieldXPMultiplier = addMultiplicationSpinnerComponent(grid, "XP Multiplier", MAX_XP_MULTIPLIER);

		neededXpLabel = new JTextField("");
		neededXpLabel.setEditable(false);
		neededXpLabel.setBorder(null);
		neededXpLabel.setOpaque(false);
		neededXpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		neededXpLabel.setFont(FontManager.getRunescapeSmallFont());
		neededXpLabel.setForeground(Color.WHITE);

		add(grid, BorderLayout.CENTER);
		add(neededXpLabel, BorderLayout.SOUTH);
	}

	int getCurrentLevelInput()
	{
		return getInput(uiFieldCurrentLevel);
	}

	void setCurrentLevelInput(int value)
	{
		setInput(uiFieldCurrentLevel, value);
	}

	int getCurrentXPInput()
	{
		return getInput(uiFieldCurrentXP);
	}

	void setCurrentXPInput(Object value)
	{
		setInput(uiFieldCurrentXP, value);
	}

	int getTargetLevelInput()
	{
		return getInput(uiFieldTargetLevel);
	}

	void setTargetLevelInput(Object value)
	{
		setInput(uiFieldTargetLevel, value);
	}

	int getTargetXPInput()
	{
		return getInput(uiFieldTargetXP);
	}

	void setTargetXPInput(Object value)
	{
		setInput(uiFieldTargetXP, value);
	}

	double getXPMultiplierDoubleInput()
	{
		Object value = uiFieldXPMultiplier.getValue();
		double raw = value instanceof Number ? ((Number) value).doubleValue() : 1.0;
		// Round to 1 decimal so accumulated 0.1-step float error (e.g. 1.3000000000000003) doesn't leak into calc.
		return Math.round(raw * 10.0) / 10.0;
	}

	void setXPMultiplier(Object value)
	{
		setInput(uiFieldXPMultiplier, value);
	}

	void setNeededXP(Object value)
	{
		uiFieldTargetXP.setToolTipText((String) value);
		// setText unconditionally resets caret/selection even for identical text,
		// which would wipe an in-progress copy selection on every focus change.
		if (!neededXpLabel.getText().equals(value))
		{
			neededXpLabel.setText((String) value);
		}
	}

	private static int getInput(JTextField field)
	{
		try
		{
			return Integer.parseInt(NON_NUMERIC.matcher(field.getText()).replaceAll(""));
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	private static void setInput(JTextField field, Object value)
	{
		field.setText(String.valueOf(value));
	}

	private static void setInput(JSpinner field, Object value)
	{
		((JSpinner.DefaultEditor) field.getEditor()).getTextField().setValue(value);
	}

	/** Shared builder for a labeled FlatTextField; also used by UICustomCalcSlot so styling stays in one place. */
	static JPanel labeledField(String label, FlatTextField field)
	{
		final JPanel container = new JPanel();
		container.setLayout(new BorderLayout());

		final JLabel uiLabel = new JLabel(label);

		field.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		field.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		field.setBorder(new EmptyBorder(5, 7, 5, 7));

		uiLabel.setFont(FontManager.getRunescapeSmallFont());
		uiLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
		uiLabel.setForeground(Color.WHITE);

		container.add(uiLabel, BorderLayout.NORTH);
		container.add(field, BorderLayout.CENTER);

		return container;
	}

	private JTextField addComponent(JPanel parent, String label)
	{
		final FlatTextField uiInput = new FlatTextField();
		parent.add(labeledField(label, uiInput));
		return uiInput.getTextField();
	}

	private JSpinner addMultiplicationSpinnerComponent(JPanel parent, String label, int max)
	{
		final JPanel container = new JPanel();
		container.setLayout(new BorderLayout());

		final JLabel uiLabel = new JLabel(label);

		// Step from the rounded current value so 0.1 float drift can't leave the
		// exact min/max unreachable via the arrows (e.g. stuck at 31.9x).
		SpinnerModel model = new SpinnerNumberModel(MIN_XP_MULTIPLIER, MIN_XP_MULTIPLIER, (double) max, 0.1)
		{
			@Override
			public Object getNextValue()
			{
				return step(1);
			}

			@Override
			public Object getPreviousValue()
			{
				return step(-1);
			}

			private Object step(int dir)
			{
				double next = Math.round((((Number) getValue()).doubleValue() + dir * 0.1) * 10.0) / 10.0;
				return next < MIN_XP_MULTIPLIER || next > (double) max ? null : next;
			}
		};
		final JSpinner uiInput = new JSpinner(model);

		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) uiInput.getEditor();
		JFormattedTextField spinnerTextField = editor.getTextField();
		spinnerTextField.setHorizontalAlignment(JTextField.LEFT);
		// Show/parse one decimal with an "x" suffix. valueClass=Double keeps the model a
		// Double so 0.1 steps stay decimal, and the "0.0" format hides float noise like 1.3000000000000003.
		DecimalFormat multiplierFormat = new DecimalFormat("0.0'x'");
		NumberFormatter multiplierFormatter = new NumberFormatter(multiplierFormat);
		multiplierFormatter.setValueClass(Double.class);
		multiplierFormatter.setMinimum(MIN_XP_MULTIPLIER);
		multiplierFormatter.setMaximum((double) max);
		spinnerTextField.setFormatterFactory(new DefaultFormatterFactory(multiplierFormatter));
		uiInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		uiInput.setBorder(new EmptyBorder(5, 7, 5, 7));

		uiLabel.setFont(FontManager.getRunescapeSmallFont());
		uiLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
		uiLabel.setForeground(Color.WHITE);

		container.add(uiLabel, BorderLayout.NORTH);
		container.add(uiInput, BorderLayout.CENTER);

		parent.add(container);

		return uiInput;
	}
}
