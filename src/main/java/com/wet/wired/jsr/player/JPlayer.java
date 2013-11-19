/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.player;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

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
import ch.sebastianfiechter.testingowl.Main;
import ch.sebastianfiechter.testingowl.ProcessRecordingWindow;
import ch.sebastianfiechter.testingowl.OwlIcons;
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

	@Autowired
	OwlIcons owl;

	@Autowired
	ScreenPlayer screenPlayer;

	@Autowired
	ProcessRecordingWindow processRecordingWindow;

	private ImageIcon icon;
	private JScrollPane scrollPane;
	private JButton open;
	private JButton reset;
	private JButton play;
	private JButton fastForward;
	private JButton pause;
	private JButton close;
	private JButton recorder;

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
			filter.addExtension("cap.owl");
			filter.setDescription("TestingOwl File");

			if (target != null) {
				fileChooser.setSelectedFile(new File(target + ".cap.owl"));
			}
			fileChooser.setFileFilter(filter);
			fileChooser.setCurrentDirectory(new File("."));
			fileChooser.showOpenDialog(this);

			if (fileChooser.getSelectedFile() != null) {
				// target = fileChooser.getSelectedFile().getAbsolutePath();
				String targetCapOwl = fileChooser.getSelectedFile()
						.getAbsolutePath();
				target = targetCapOwl.substring(0,
						targetCapOwl.lastIndexOf(".cap.owl"));
				open();
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
		} else if (ev.getActionCommand().equals("recorder")) {
			closePlayer();
			Main.getRecorder().init(new String[0]);
		}
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

		recorder.setEnabled(true);
		recorder.setBackground(null);

		text.setText("Stopped playing " + target);
	}

	public void showNewImage(final Image image) {
		if (icon == null) {
			icon = new ImageIcon(image);
			JLabel label = new JLabel(icon);

			scrollPane = new JScrollPane(label);
			scrollPane.setSize(image.getWidth(this), image.getHeight(this));

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					getContentPane().add(scrollPane, BorderLayout.CENTER);
					pack();
					setSize(getWidth() - 100, 400);
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

		decorator.newFrame(frameNumber, (time / 1000.0));

		slider.setValueProgrammatically((int) frameNumber);

		setFrameLabelText(frameNumber, time / 1000.0);

	}

	public void init(String[] args) {

		showWindow();

		if (args.length == 1) {
			target = new File(args[0]).getAbsolutePath();
			open();
		}
	}

	public void showWindow() {

		setTitle("TestingOwl Player");
		setIconImage(owl.getWelcomeIcon().getImage());
		getContentPane().removeAll();

		JPanel panel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		panel.setLayout(gbl);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closePlayer();
				System.exit(0);
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

		// slider
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

		recorder = new JButton("to Recorder");
		recorder.setActionCommand("recorder");
		recorder.setEnabled(true);
		recorder.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 8;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		panel.add(recorder, gbc);

		panel.doLayout();

		this.getContentPane().add(panel, BorderLayout.NORTH);

		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));

		frameLabel = new JLabel("Frame: 0 Time: 0");
		frameLabel.setForeground(Color.black);
		text = new JLabel("No recording selected");
		// text.setBackground(Color.black);
		text.setForeground(Color.black);

		panel.add(text);
		panel.add(frameLabel);

		this.getContentPane().add(panel, BorderLayout.SOUTH);

		this.setSize(700, this.getHeight());
		this.pack();
		this.setVisible(true);
	}

	private JPanel createSliderLayout() {
		slider.setEnabled(false);

		JPanel sliderPanel = new JPanel();
		sliderPanel.setPreferredSize(close.getSize()); // use default size of
														// button

		GridBagLayout gbl = new GridBagLayout();
		sliderPanel.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbl.setConstraints(slider, gbc);
		sliderPanel.add(slider);

		return sliderPanel;
	}

	public void open() {

		beginWaitForBackgroundProcesses();

		processRecordingWindow.showOpen(0, 4, target + ".cap.owl");

		new Thread() {
			public void run() {
				decorator.setFileNameWithoutEnding(target);
				decorator.unpack();
				processRecordingWindow.setProgressValue(1);

				screenPlayer.init(target, JPlayer.this);

				screenPlayer.open();
				processRecordingWindow.setProgressValue(2);
				decorator.open();

				slider.setMin(1);
				slider.setMax(screenPlayer.getTotalFrames());

				soundLevel.setLevel(0);

				processRecordingWindow.hide();
				endWaitForBackgroundProcesses();

				text.setText("Ready to play " + target);

				reset();
			}
		}.start();
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

		recorder.setEnabled(true);
		recorder.setBackground(null);

		setFrameLabelText(0, screenPlayer.getTotalTime());

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

		recorder.setEnabled(false);
		recorder.setBackground(null);

		screenPlayer.play();

		decorator.play();

		text.setText("Playing " + target);
	}

	public void goToFrame(int frame) {

		beginWaitForBackgroundProcesses();

		pause();

		screenPlayer.goToFrame(frame);

		endWaitForBackgroundProcesses();
	}

	public int getTotalFrames() {
		return screenPlayer.getTotalFrames();
	}

	public long getTotalTime() {
		return screenPlayer.getTotalTime();
	}

	public void fastForward() {

		screenPlayer.fastforward();

		decorator.fastForwart();

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

		recorder.setEnabled(false);
		recorder.setBackground(null);

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

		recorder.setEnabled(true);
		recorder.setBackground(null);

		screenPlayer.pause();

		decorator.pause();

		text.setText("Paused " + target);
	}

	public void close() {

		beginWaitForBackgroundProcesses();

		processRecordingWindow.showSaving(0, 4, target + ".cap.owl");

		new Thread() {
			public void run() {

				screenPlayer.close();
				setFrameLabelText(0, 0);

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

				recorder.setEnabled(true);
				recorder.setBackground(null);

				slider.setEnabled(false);
				
				soundLevel.setLevel(0);
				
				processRecordingWindow.hide();
				endWaitForBackgroundProcesses();
				
				text.setText("No recording selected");
			}
		}.start();
	}

	public void closePlayer() {
		close();
		decorator.dispose();
		dispose();
	}

	public void setFrameLabelText(long frame, double seconds) {
		frameLabel.setText("Frame: " + frame + "/"
				+ screenPlayer.getTotalFrames() + " Time: " + seconds);
	}

	public void beginWaitForBackgroundProcesses() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void endWaitForBackgroundProcesses() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
