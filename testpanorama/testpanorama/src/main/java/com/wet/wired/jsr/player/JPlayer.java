/*
 * This software is OSI Certified Open Source Software
 * 
 * The MIT License (MIT)
 * Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 */

package com.wet.wired.jsr.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.sebastianfiechter.testingowl.FramesSlider;
import ch.sebastianfiechter.testingowl.JPlayerDecorator;
import ch.sebastianfiechter.testingowl.SoundLevel;

@SuppressWarnings("serial")
@Component
public class JPlayer extends JFrame implements ScreenPlayerListener,
		ActionListener {

	@Autowired
	JPlayerDecorator decorator;
	
	@Autowired
	FramesSlider slider;
	
	@Autowired
	SoundLevel soundLevel;

	private ScreenPlayer screenPlayer;

	private ImageIcon icon;
	private JScrollPane scrollPane;
	private JButton open;
	private JButton reset;
	private JButton play;
	private JButton fastForward;
	private JButton pause;
	private JButton close;

	private JLabel text;
	private JLabel frameLabel;

	private String target;
	private long startTime;

	private Color activeButtonColor = new Color(248, 229, 179);

	public void actionPerformed(ActionEvent ev) {

		if (ev.getActionCommand().equals("open")) {
			UIManager.put("FileChooser.readOnly", true);
			JFileChooser fileChooser = new JFileChooser();
			FileExtensionFilter filter = new FileExtensionFilter();

			filter = new FileExtensionFilter();
			filter.addExtension("cap.zip");
			filter.setDescription("TestingOwl File");

			if (target != null) {
				fileChooser.setSelectedFile(new File(target));
			}
			fileChooser.setFileFilter(filter);
			fileChooser.showOpenDialog(this);

			if (fileChooser.getSelectedFile() != null) {
				//target = fileChooser.getSelectedFile().getAbsolutePath();
				String targetAsZip = fileChooser.getSelectedFile().getAbsolutePath();
				decorator.unzip(targetAsZip);
				target = targetAsZip.substring(0, targetAsZip.lastIndexOf("."));
				open();
				decorator.open(target);
			}
		} else if (ev.getActionCommand().equals("play")) {
			play();
		} else if (ev.getActionCommand().equals("reset")) {
			reset();
		} else if (ev.getActionCommand().equals("fastForward")) {
			fastForward();
		} else if (ev.getActionCommand().equals("pause")) {
			pause();
		} else if (ev.getActionCommand().equals("close")) {
			close();
		}
	}

	public void playerPaused() {

		open.setEnabled(false);
		open.setBackground(null);

		reset.setEnabled(true);
		reset.setBackground(null);

		play.setEnabled(false);
		play.setBackground(null);

		fastForward.setEnabled(false);
		fastForward.setBackground(null);

		pause.setEnabled(false);
		pause.setBackground(null);

		close.setEnabled(true);
		close.setBackground(null);

		text.setText("Paused playing " + target);
	}

	public void playerStopped() {

		open.setEnabled(false);
		open.setBackground(null);

		reset.setEnabled(true);
		reset.setBackground(null);

		play.setEnabled(false);
		play.setBackground(null);

		fastForward.setEnabled(false);
		fastForward.setBackground(null);

		pause.setEnabled(false);
		pause.setBackground(activeButtonColor);

		close.setEnabled(true);
		close.setBackground(null);

		text.setText("Stopped playing " + target);
	}

	public void showNewImage(final Image image) {
		if (icon == null) {
			icon = new ImageIcon(image);
			JLabel label = new JLabel(icon);

			scrollPane = new JScrollPane(label);
			scrollPane
					.setSize(image.getWidth(this), image.getHeight(this));

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					getContentPane().add(scrollPane, BorderLayout.CENTER);
					pack();
					setSize(getWidth()-100, 400);
					setVisible(true);
					repaint(0);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					icon.setImage(image);
					repaint(0);
				}
			});
		}

	}

	public void newFrame(long frameNumber, long frameTime) {

		// long time = System.currentTimeMillis() - startTime;
		long time = frameTime - startTime;
		
		decorator.newFrame(frameNumber, (time/1000.0));
		
		setFrameLabelText(frameNumber, time/1000.0);

		slider.setValueProgrammatically((int) frameNumber);
	}

	public void init(String[] args) {

		showFrame();

		if (args.length == 1) {
			target = new File(args[0]).getAbsolutePath();
			decorator.open(target);
			open();
		}
	}

	public void showFrame() {

		setTitle("TestingOwl Player");
		
		JPanel panel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(gbl);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
				decorator.disposing();
				dispose();
			}
		});

		open = new JButton("Open Recording");
		open.setActionCommand("open");
		open.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;	
		panel.add(open, gbc);

		reset = new JButton("Reset");
		reset.setActionCommand("reset");
		reset.setEnabled(false);
		reset.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;	
		panel.add(reset, gbc);

		play = new JButton("Play");
		play.setActionCommand("play");
		play.setEnabled(false);
		play.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;		
		panel.add(play, gbc);

		fastForward = new JButton("Fast Forward");
		fastForward.setActionCommand("fastForward");
		fastForward.setEnabled(false);
		fastForward.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;	
		panel.add(fastForward, gbc);

		pause = new JButton("Pause");
		pause.setActionCommand("pause");
		pause.setEnabled(false);
		pause.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;		
		panel.add(pause, gbc);

		close = new JButton("Close File");
		close.setActionCommand("close");
		close.setEnabled(false);
		close.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;	
		panel.add(close, gbc);
		

		//slieder
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 6;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;			
		panel.add(createSliderLayout(), gbc);
		
		soundLevel.setSize(30, (int) close.getSize().getHeight());
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 7;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		panel.add(soundLevel, gbc);
		
		panel.doLayout();

		this.getContentPane().add(panel, BorderLayout.NORTH);

		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		//panel.setBackground(Color.black);

		frameLabel = new JLabel("Frame: 0 Time: 0");
		//frameLabel.setBackground(Color.black);
		frameLabel.setForeground(Color.black);
		text = new JLabel("No recording selected");
		//text.setBackground(Color.black);
		text.setForeground(Color.black);

		panel.add(text);
		panel.add(frameLabel);

		this.getContentPane().add(panel, BorderLayout.SOUTH);

		this.pack();
		this.setSize(700, this.getHeight());
		this.setVisible(true);
	}
	
	private JPanel createSliderLayout() {
		slider.setEnabled(false);
		

		JPanel sliderPanel = new JPanel();
		sliderPanel.setPreferredSize(close.getSize()); //use default size of button
		
		GridBagLayout gbl = new GridBagLayout();
		sliderPanel.setLayout(gbl);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbl.setConstraints(slider, gbc);
		sliderPanel.add(slider);
		
		
		return sliderPanel;
	}

	public boolean open() {
		
		beginShowWaitingCursor();
			
		if (target != null) {
			try {
				screenPlayer = new ScreenPlayer(target, this);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		open.setEnabled(false);
		open.setBackground(null);

		reset.setEnabled(true);
		reset.setBackground(null);

		play.setEnabled(true);
		play.setBackground(null);

		fastForward.setEnabled(true);
		fastForward.setBackground(null);

		pause.setEnabled(false);
		pause.setBackground(null);

		close.setEnabled(true);
		close.setBackground(null);

		slider.setMin(1);
		slider.setMax(screenPlayer.getTotalFrames());
		slider.setEnabled(true);

		screenPlayer.showFirstFrame();
		slider.setValueProgrammatically(1);

		endShowWaitingCursor();
		
		text.setText("Ready to play " + target);

		return true;
	}

	public void reset() {

		open.setEnabled(false);
		open.setBackground(null);

		reset.setEnabled(true);
		reset.setBackground(null);

		play.setEnabled(true);
		play.setBackground(null);

		fastForward.setEnabled(true);
		fastForward.setBackground(null);

		pause.setEnabled(false);
		pause.setBackground(null);

		close.setEnabled(true);
		close.setBackground(null);

		slider.setEnabled(true);
		
		setFrameLabelText(0);
		
		screenPlayer.reset();
		
		decorator.reset();
		
		text.setText("Ready to play " + target);

	}

	public void play() {

		open.setEnabled(false);
		open.setBackground(null);

		reset.setEnabled(true);
		reset.setBackground(null);

		play.setEnabled(false);
		play.setBackground(activeButtonColor);

		fastForward.setEnabled(true);
		fastForward.setBackground(null);

		pause.setEnabled(true);
		pause.setBackground(null);

		close.setEnabled(true);
		close.setBackground(null);

		slider.setEnabled(true);

		screenPlayer.play();
		
		decorator.play();

		text.setText("Playing " + target);
	}

	public void goToFrame(int frame) {
		
		beginShowWaitingCursor();
		
		reset();
		screenPlayer.goToFrame(frame);
		
		endShowWaitingCursor();
		
		play();
	}

	public int getTotalFrames() {
		return screenPlayer.getTotalFrames();
	}

	public void fastForward() {

		open.setEnabled(false);
		open.setBackground(null);

		reset.setEnabled(false);
		reset.setBackground(null);

		play.setEnabled(true);
		play.setBackground(null);

		fastForward.setEnabled(false);
		fastForward.setBackground(activeButtonColor);

		pause.setEnabled(true);
		pause.setBackground(null);

		close.setEnabled(true);
		close.setBackground(null);

		slider.setEnabled(true);

		screenPlayer.fastforward();
		
		decorator.fastForwart();

		text.setText("Fast Forward " + target);
	}

	public void pause() {

		open.setEnabled(false);
		open.setBackground(null);

		reset.setEnabled(true);
		reset.setBackground(null);

		play.setEnabled(true);
		play.setBackground(null);

		fastForward.setEnabled(true);
		fastForward.setBackground(null);

		pause.setEnabled(false);
		pause.setBackground(activeButtonColor);

		close.setEnabled(true);
		close.setBackground(null);

		slider.setEnabled(true);

		screenPlayer.pause();
		
		decorator.pause();
		
		text.setText("Paused " + target);
	}

	public void close() {

		if (screenPlayer != null) {
			screenPlayer.stop();
		}
		
		

		decorator.close();

		open.setEnabled(true);
		open.setBackground(null);

		reset.setEnabled(false);
		reset.setBackground(null);

		play.setEnabled(false);
		play.setBackground(null);

		fastForward.setEnabled(false);
		fastForward.setBackground(null);

		pause.setEnabled(false);
		pause.setBackground(null);

		close.setEnabled(false);
		close.setBackground(null);

		slider.setEnabled(false);

		text.setText("No recording selected");
	}
	
	public void setFrameLabelText(long frame, double seconds) {
		frameLabel.setText("Frame: " + frame + "/"
				+ screenPlayer.getTotalFrames() + " Time: " + seconds);
	}
	
	public void setFrameLabelText(long frame) {
		
		double seconds = (0.0+frame)/screenPlayer.getTotalFrames()
				*screenPlayer.getTotalTime()/1000.0;
		
		frameLabel.setText("Frame: " + frame + "/"
				+ screenPlayer.getTotalFrames() + " Time: " + seconds);
	}	
	
	public void beginShowWaitingCursor() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	public void endShowWaitingCursor() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
