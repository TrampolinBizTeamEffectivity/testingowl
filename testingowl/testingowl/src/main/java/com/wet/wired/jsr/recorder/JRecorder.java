/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.recorder;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.sebastianfiechter.testingowl.CommitIssuesWindow;
import ch.sebastianfiechter.testingowl.SaveRecordingWindow;
import ch.sebastianfiechter.testingowl.JRecorderDecorator;
import ch.sebastianfiechter.testingowl.Main;
import ch.sebastianfiechter.testingowl.OwlIcons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@Component
public class JRecorder extends JFrame implements ScreenRecorderListener,
		ActionListener {

	Logger logger = LoggerFactory.getLogger(JRecorder.class);

	@Autowired
	JRecorderDecorator decorator;

	@Autowired
	OwlIcons owl;
	
	@Autowired
	CommitIssuesWindow commitIssuesWindow;

	@Autowired
	SaveRecordingWindow saveRecordingWindow;

	@Autowired
	DesktopScreenRecorder recorder;

	private JButton control;
	private JLabel text;
	private JButton player;

	private int frameCount = 0;

	public boolean startRecording() {

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// do nothing
		}

		if (!decorator.fetchTopicAndMixer(this)) {
			return false;
		}

		try {
			File temp = File.createTempFile("temp", "rec");
			temp.deleteOnExit();
			recorder.init(temp, this);
			recorder.startRecording();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("start")) {
			try {
				if (startRecording()) {
					control.setActionCommand("stop");
					control.setText("Stop Recording");
					player.setEnabled(false);
					decorator.recordStarted();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (ev.getActionCommand().equals("stop")) {
			text.setText("Stopping");
			recorder.stopRecording();
		} else if (ev.getActionCommand().equals("player")) {
			closeRecorder();
			Main.getPlayer().init(new String[0]);
		}
	}

	public void frameRecorded(boolean fullFrame, long frameTime) {
		frameCount++;
		if (text != null) {
			String seconds = "" + frameTime / 1000;
			String milliseconds = String.format("%04d", frameTime % 1000);
			text.setText("Frame: " + frameCount + " Time: " + seconds + "."
					+ milliseconds);
		}
	}

	public void recordingStopped() {

		decorator.recordStopped();

		// UIManager.put("FileChooser.readOnly", true);
		// JFileChooser fileChooser = new JFileChooser();
		// FileExtensionFilter filter = new FileExtensionFilter();
		//
		// filter = new FileExtensionFilter();
		// filter.addExtension("cap.owl");
		// filter.setDescription("TestingOwl File");
		//
		// fileChooser.setFileFilter(filter);
		// fileChooser.setSelectedFile(decorator.prepareSuggestedFileName());
		// fileChooser.showSaveDialog(this);
		//
		// File target = fileChooser.getSelectedFile();
		//
		// save(target);
		save();

		frameCount = 0;

		control.setActionCommand("start");
		control.setText("Start Recording");

		player.setEnabled(true);

		text.setText("Ready to record");

	}

	public void save() {

		this.beginWaitForBackgroundProcesses();

		File targetWithCapOwl = decorator.prepareSuggestedFile();

		this.setEnabled(false);
		
		commitIssuesWindow.showAndWaitForConfirm();
		
		
		saveRecordingWindow.show(0, 4, targetWithCapOwl.getAbsolutePath());

		saveVideo(targetWithCapOwl);

		decorator.saveFile(targetWithCapOwl);

		decorator.pack(targetWithCapOwl);

		this.endWaitForBackgroundProcesses();

		saveRecordingWindow.waitForConfirm();
		this.setEnabled(true);

	}

	private void saveVideo(File targetWithCapOwl) {
		File capFile = new File(targetWithCapOwl.getAbsolutePath().substring(0,
				targetWithCapOwl.getAbsolutePath().lastIndexOf(".")));

		logger.info("start save cap");
		try {
			FileOutputStream fos = new FileOutputStream(capFile);
			recorder.writeFrameIndex(fos.getChannel());
			recorder.writeVideo(fos.getChannel());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		saveRecordingWindow.setProgressValue(1);
		logger.info("stop save cap");
	}


	public void init(String[] args) {

		if (args.length >= 1) {
			if (args[0].equals("-white_cursor"))
				DesktopScreenRecorder.useWhiteCursor = true;
			else {
				System.out
						.println("Usage: java -jar screen_recorder.jar [OPTION]...");
				System.out.println("Start the screen recorder.");
				System.out.println("Options:   ");
				System.out
						.println("   -white_cursor   record with white cursor");
				System.exit(0);
			}
		}

		showWindow();

	}

	private void showWindow() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeRecorder();
				System.exit(0);
			}
		});

		setTitle("TestingOwl Recorder");
		setIconImage(owl.getWelcomeIcon().getImage());
		getContentPane().removeAll();

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		this.getContentPane().setLayout(gbl);

		control = new JButton("Start Recording");
		control.setActionCommand("start");
		control.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		this.getContentPane().add(control, gbc);

		decorator.getButtonsAndSoundLevel(this.getContentPane(), gbc);

		player = new JButton("to Player");
		player.setActionCommand("player");
		player.addActionListener(this);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		this.getContentPane().add(player, gbc);

		text = new JLabel("Ready to record");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 5;
		this.getContentPane().add(text, gbc);

		getContentPane().doLayout();

		this.pack();
		this.setAlwaysOnTop(true);
		this.setVisible(true);

	}

	public void closeRecorder() {
		recorder.stopRecording();

		decorator.dispose();
		dispose();
	}

	public void beginWaitForBackgroundProcesses() {
		control.setEnabled(false);
		player.setEnabled(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	}

	public void endWaitForBackgroundProcesses() {
		control.setEnabled(true);
		player.setEnabled(true);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
