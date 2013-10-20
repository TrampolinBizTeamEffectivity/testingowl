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
import java.awt.GridLayout;
import java.awt.Image;
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
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.sebastianfiechter.testpanorama.JPlayerDecorator;

@SuppressWarnings("serial")
@Component
public class JPlayer extends JFrame implements ScreenPlayerListener,
		ActionListener {

	@Autowired
	JPlayerDecorator decorator;

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
			filter.addExtension("cap");
			filter.setDescription("Screen Capture File");

			if (target != null) {
				fileChooser.setSelectedFile(new File(target));
			}
			fileChooser.setFileFilter(filter);
			fileChooser.showOpenDialog(this);

			if (fileChooser.getSelectedFile() != null) {
				target = fileChooser.getSelectedFile().getAbsolutePath();
				open();
				decorator.openIssues(target);
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

	public void showNewImage(Image image) {
		if (icon == null) {
			icon = new ImageIcon(image);
			JLabel label = new JLabel(icon);

			scrollPane = new JScrollPane(label);
			scrollPane.setSize(image.getWidth(this)-50, image.getHeight(this));


			this.getContentPane().add(scrollPane, BorderLayout.CENTER);

			pack();
			this.setSize(this.getWidth(), 400);
			setVisible(true);
		} else {
			icon.setImage(image);
		}

		repaint(0);
	}

	public void newFrame(long frameNumber, long frameTime) {
		
		//long time = System.currentTimeMillis() - startTime;
		long time = frameTime - startTime;
		String seconds = "" + time / 1000;
		String milliseconds = String.format("%04d", time % 1000);
		frameLabel.setText("Frame: " + frameNumber + "/" + screenPlayer.getTotalFrames() + " Time: " + seconds + "."
				+ milliseconds);
		
		decorator.getSlider().setValueProgrammatically((int)frameNumber);
	}

	public void init(String[] args) {

		showFrame();

		if (args.length == 1) {
			target = new File(args[0]).getAbsolutePath();
			decorator.openIssues(target);
			open();
		}
	}

	public void showFrame() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 7));
		setTitle("Screen Player");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				close();
				
				decorator.dispose();
				dispose();
			}
		});

		open = new JButton("Open Recording");
		open.setActionCommand("open");
		open.addActionListener(this);

		reset = new JButton("Reset");
		reset.setActionCommand("reset");
		reset.setEnabled(false);
		reset.addActionListener(this);

		play = new JButton("Play");
		play.setActionCommand("play");
		play.setEnabled(false);
		play.addActionListener(this);

		fastForward = new JButton("Fast Forward");
		fastForward.setActionCommand("fastForward");
		fastForward.setEnabled(false);
		fastForward.addActionListener(this);

		pause = new JButton("Pause");
		pause.setActionCommand("pause");
		pause.setEnabled(false);
		pause.addActionListener(this);

		close = new JButton("Close File");
		close.setActionCommand("close");
		close.setEnabled(false);
		close.addActionListener(this);

		decorator.getSlider().setEnabled(false);
		
		panel.add(open);
		panel.add(reset);
		panel.add(play);
		panel.add(fastForward);
		panel.add(pause);
		panel.add(close);
		panel.add(decorator.getSlider());
		panel.doLayout();

		this.getContentPane().add(panel, BorderLayout.NORTH);

		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.setBackground(Color.black);

		frameLabel = new JLabel("Frame: 0 Time: 0");
		frameLabel.setBackground(Color.black);
		frameLabel.setForeground(Color.red);
		text = new JLabel("No recording selected");
		text.setBackground(Color.black);
		text.setForeground(Color.red);

		panel.add(text);
		panel.add(frameLabel);

		this.getContentPane().add(panel, BorderLayout.SOUTH);

		this.pack();
		this.setSize(700, this.getHeight());
		this.setVisible(true);
	}

	public boolean open() {

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
		
		decorator.getSlider().setMin(1);
		decorator.getSlider().setMax(screenPlayer.getTotalFrames());
		decorator.getSlider().setEnabled(true);
		
		screenPlayer.showFirstFrame();
		decorator.getSlider().setValueProgrammatically(1);
		
		text.setText("Ready to play " + target);

		return true;
	}

	public void reset() {

		screenPlayer.reset();
		
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

		decorator.getSlider().setEnabled(true);
			
		frameLabel.setText("Frame: 0/"+screenPlayer.getTotalFrames()+" Time: 0.0");
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
		
		decorator.getSlider().setEnabled(true);

		screenPlayer.play();

		text.setText("Playing " + target);
	}
	
	public void goToFrame(int frame) {
		reset();
		screenPlayer.goToFrame(frame);
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
		
		decorator.getSlider().setEnabled(true);

		screenPlayer.fastforward();

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
		
		decorator.getSlider().setEnabled(true);

		screenPlayer.pause();
		text.setText("Paused " + target);
	}

	public void close() {

		decorator.closeFile();
		
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
		
		decorator.getSlider().setEnabled(false);

		if (screenPlayer != null) {
			screenPlayer.stop();
		}
		text.setText("No recording selected");
	}
}
