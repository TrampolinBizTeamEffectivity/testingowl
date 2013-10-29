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

package com.wet.wired.jsr.recorder;

import groovy.util.logging.Slf4j;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.sebastianfiechter.testingowl.InProgressWindow;
import ch.sebastianfiechter.testingowl.JRecorderDecorator;
import ch.sebastianfiechter.testingowl.Owl;
import ch.sebastianfiechter.testingowl.SoundLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLoggerFactory;

@SuppressWarnings("serial")
@Component
public class JRecorder extends JFrame implements ScreenRecorderListener,
		ActionListener {

	Logger logger = LoggerFactory.getLogger(JRecorder.class);

	@Autowired
	JRecorderDecorator decorator;

	@Autowired
	Owl owl;
	
	@Autowired
	InProgressWindow inProgressWindow;

	private ScreenRecorder recorder;
	private File temp;

	private JButton control;
	private JLabel text;

	private boolean shuttingDown = false;
	private int frameCount = 0;

	public boolean startRecording(String fileName) {

		// setState(Frame.ICONIFIED);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
		}

		if (recorder != null) {
			return true;
		}

		if (!decorator.fetchTopicAndMixer(this)) {
			return false;
		}

		try {
			FileOutputStream oStream = new FileOutputStream(fileName);
			temp = new File(fileName);
			temp.deleteOnExit();
			recorder = new DesktopScreenRecorder(oStream, this);
			recorder.startRecording();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev.getActionCommand().equals("start") && recorder == null) {
			try {
				temp = File.createTempFile("temp", "rec");

				if (startRecording(temp.getAbsolutePath())) {
					control.setActionCommand("stop");
					control.setText("Stop Recording");
					decorator.recordStarted();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (ev.getActionCommand().equals("stop") && recorder != null) {
			text.setText("Stopping");
			recorder.stopRecording();
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

		if (!shuttingDown) {

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

			FileHelper.delete(temp);
			recorder = null;
			frameCount = 0;

			control.setActionCommand("start");
			control.setText("Start Recording");

			text.setText("Ready to record");
		} else
			FileHelper.delete(temp);
	}

	public void save() {

		this.beginWaitForBackgroundProcesses();

		File target = decorator.prepareSuggestedFile();
		
		this.setEnabled(false);
		inProgressWindow.show(0, 4, "Saving recording to: ", target.getAbsolutePath());

		saveVideo(target);

		decorator.saveFile(target);

		decorator.pack(target);
		
		this.endWaitForBackgroundProcesses();
		
		inProgressWindow.waitForConfirm();
		this.setEnabled(true);

	}
	
	public void saveVideo(File target) {
		File capFile = new File(target.getAbsolutePath().substring(0,
				target.getAbsolutePath().lastIndexOf(".")));
		logger.info("start save cap");
		FileHelper.copy(temp, capFile);
		inProgressWindow.setProgressValue(1);
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

		showFrame();

	}

	private void showFrame() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});

		setTitle("TestingOwl Recorder");
		setIconImage(owl.getWelcome().getImage());

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

		text = new JLabel("Ready to record");
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 5;
		this.getContentPane().add(text, gbc);

		getContentPane().doLayout();

		this.setAlwaysOnTop(true);
		this.pack();
		this.setVisible(true);

	}

	public void shutdown() {

		shuttingDown = true;

		if (recorder != null) {
			recorder.stopRecording();
		}

		decorator.dispose();
		dispose();

		System.exit(0);
	}

	public void beginWaitForBackgroundProcesses() {
		control.setEnabled(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	}

	public void endWaitForBackgroundProcesses() {
		control.setEnabled(true);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
