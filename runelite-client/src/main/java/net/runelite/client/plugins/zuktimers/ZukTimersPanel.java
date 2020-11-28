/*
 * Copyright (c) 2018 Abex
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * Copyright (c) 2018, Daniel Teo <https://github.com/takuyakanbr>
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
package net.runelite.client.plugins.zuktimers;

import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ZukTimersPanel extends PluginPanel
{
	private final ItemManager itemManager;

	private JLabel timer;
	private JButton resetBtn;

	private long currentTime = 210;
	private boolean running = false;
	private ZukProgress progress = ZukProgress.PRE_FIRST_SET;

	JPanel buttonPanel;
	private JPanel buttonFirstSet;
	private JPanel buttonAbove600;
	private JPanel buttonPaused;
	private JPanel buttonUnpaused;

	private List<JPanel> buttonsToShow = new ArrayList<>();
	private static final ImageIcon ARROW_RIGHT_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(ZukTimersPlugin.class, "/util/arrow_right.png"));

	ZukTimersPanel(ItemManager itemManager)
	{
		this.itemManager = itemManager;
//		this.config = config;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(3, 0, 0, 0));

		timer = new JLabel();
		timer.setHorizontalAlignment(SwingConstants.CENTER);
		timer.setFont(timer.getFont().deriveFont(64.0f));
		timer.setText("3:30");

		resetBtn = new JButton();
		resetBtn.setText("Reset");
		resetBtn.addActionListener(e -> {
			running = false;
			currentTime = 210;
			progress = ZukProgress.PRE_FIRST_SET;
			progressUpdated();
		});

		buttonFirstSet = getZukButtonPanel(ZukProgress.PRE_FIRST_SET);
		buttonAbove600 = getZukButtonPanel(ZukProgress.ABOVE_SIX_HUNDRED);
		buttonPaused = getZukButtonPanel(ZukProgress.PAUSED);
		buttonUnpaused = getZukButtonPanel(ZukProgress.UNPAUSED);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(buttonFirstSet);
		buttonPanel.add(buttonAbove600);
		buttonPanel.add(buttonPaused);
		buttonPanel.add(buttonUnpaused);

		add(timer);
		add(buttonPanel);
		add(resetBtn);
		setBackground(ColorScheme.DARK_GRAY_COLOR);
	}

	private JPanel getZukButtonPanel(ZukProgress progress)
	{
		boolean enabled = progress == this.progress;
		JPanel panel = new JPanel();
		panel.setBackground(enabled ? ColorScheme.DARKER_GRAY_COLOR : ColorScheme.DARK_GRAY_COLOR);
		panel.setLayout(new BorderLayout());
		panel.setBorder(new EmptyBorder(7, 7, 7, 7));

		JLabel iconLabel = new JLabel();
		iconLabel.setMinimumSize(new Dimension(Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT));
		iconLabel.setSize(new Dimension(100, 100));
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(ZukTimersPlugin.class, progress.getIconImageName());
		iconLabel.setIcon(new ImageIcon(icon));
//		itemManager.getImage(progress.getIconItemID()).addTo(iconLabel);
		panel.add(iconLabel, BorderLayout.EAST);

		JPanel textContainer = new JPanel();
		textContainer.setBackground(enabled ? ColorScheme.DARKER_GRAY_COLOR : ColorScheme.DARK_GRAY_COLOR);
		textContainer.setLayout(new GridLayout(2, 1));
		textContainer.setBorder(new EmptyBorder(5, 7, 5, 7));

		panel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				mainButtonPressed();
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{

			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (ZukProgress.UNPAUSED != progress)
				{
					setCursor(new Cursor(enabled && getMousePosition(true) != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		JLabel titleLabel = new JLabel();
		titleLabel.setForeground(enabled ? Color.WHITE : ColorScheme.LIGHT_GRAY_COLOR);
		titleLabel.setFont(FontManager.getRunescapeSmallFont());
		titleLabel.setText(progress.getTitle());

		JLabel statusLabel = new JLabel();
		statusLabel.setForeground(Color.GRAY);
		statusLabel.setFont(FontManager.getRunescapeSmallFont());
		statusLabel.setText(progress.getMessage());

		textContainer.add(titleLabel);
		textContainer.add(statusLabel);

		panel.add(textContainer, BorderLayout.CENTER);

		if (ZukProgress.UNPAUSED != progress)
		{
			JLabel arrowLabel = new JLabel(ARROW_RIGHT_ICON);
			arrowLabel.setVisible(progress == this.progress);
			panel.add(arrowLabel, BorderLayout.WEST);
		}

		return panel;
	}

	private void mainButtonPressed()
	{
		switch (progress)
		{
			case PRE_FIRST_SET:
				progress = ZukProgress.ABOVE_SIX_HUNDRED;
				running = true;
				break;
			case ABOVE_SIX_HUNDRED:
				progress = ZukProgress.PAUSED;
				running = false;
				currentTime = currentTime + 105; // add 1:45 in seconds
				break;
			case PAUSED:
				progress = ZukProgress.UNPAUSED;
				running = true;
				break;
		}
		progressUpdated();
	}

	private void progressUpdated()
	{
		switch (progress)
		{
			case PRE_FIRST_SET:
				buttonPanel.removeAll();
				buttonFirstSet = getZukButtonPanel(ZukProgress.PRE_FIRST_SET);
				buttonPanel.add(buttonFirstSet);
				buttonAbove600 = getZukButtonPanel(ZukProgress.ABOVE_SIX_HUNDRED);
				buttonPanel.add(buttonAbove600);
				buttonPaused = getZukButtonPanel(ZukProgress.PAUSED);
				buttonPanel.add(buttonPaused);
				buttonUnpaused = getZukButtonPanel(ZukProgress.UNPAUSED);
				buttonPanel.add(buttonUnpaused);
				break;
			case ABOVE_SIX_HUNDRED:
				buttonPanel.remove(buttonFirstSet);
				buttonPanel.remove(buttonAbove600);
				buttonAbove600 = getZukButtonPanel(ZukProgress.ABOVE_SIX_HUNDRED);
				buttonPanel.add(buttonAbove600,0);
				break;
			case PAUSED:
				buttonPanel.remove(buttonAbove600);
				buttonPanel.remove(buttonPaused);
				buttonPaused = getZukButtonPanel(ZukProgress.PAUSED);
				buttonPanel.add(buttonPaused,0);
				break;
			case UNPAUSED:
				buttonPanel.remove(buttonPaused);
				buttonPanel.remove(buttonUnpaused);
				buttonUnpaused = getZukButtonPanel(ZukProgress.UNPAUSED);
				buttonPanel.add(buttonUnpaused,0);
				break;
		}
	}

	static String getFormattedDuration(long duration)
	{
		long mins = (duration / 60) % 60;
		long seconds = duration % 60;

		return String.format("%01d:%02d", mins, seconds);
	}

//	@Override
	public int getUpdateInterval()
	{
		return 5; // 1 second
	}

//	@Override
	public void update()
	{
		if (running)
		{
			if (currentTime <= 0)
			{
				currentTime = 210;
			} else {
				currentTime--;
			}
		}
		timer.setText(getFormattedDuration(currentTime));
		if (currentTime <= 10)
		{
				timer.setForeground(Color.RED);
		}
		else if (timer.getForeground() != Color.WHITE)
		{
			timer.setForeground(Color.WHITE);
		}
	}
}
