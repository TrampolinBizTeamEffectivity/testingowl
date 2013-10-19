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

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.io.FileInputStream;
import java.io.IOException;

public class ScreenPlayer implements Runnable {

	private Thread thread;

	private ScreenPlayerListener listener;

	private MemoryImageSource mis = null;
	private Rectangle area;

	private FrameDecompressor decompressor;
	private int frameSize;

	private long startTime;
	private long frameTime;
	private long lastFrameTime;

	private boolean running;
	private boolean paused;
	private boolean fastForward;

	private boolean resetReq;

	private FileInputStream iStream;
	private String videoFile;
	private int width;
	private int height;

	public ScreenPlayer(String videoFile, ScreenPlayerListener listener) {

		this.listener = listener;
		this.videoFile = videoFile;

		initialize();
	}

	private void initialize() {

		startTime = System.currentTimeMillis();
		frameTime = startTime;
		lastFrameTime = startTime;
		paused = true;

		try {

			iStream = new FileInputStream(videoFile);

			width = iStream.read();
			width = width << 8;
			width += iStream.read();

			height = iStream.read();
			height = height << 8;
			height += iStream.read();

			area = new Rectangle(width, height);
			decompressor = new FrameDecompressor(iStream, width * height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reset() {

		paused = false;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clearImage();

		resetReq = false;
		initialize();

	}

	public void play() {

		fastForward = false;
		paused = false;

		if (running == false) {
			thread = new Thread(this, "Screen Player");
			thread.start();
		}
	}

	public void fastforward() {
		fastForward = true;
		paused = false;
		
		if (running == false) {
			thread = new Thread(this, "Screen Player");
			thread.start();
		}		
	}

	public void pause() {
		paused = true;
	}

	public void stop() {
		
		paused = false;
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clearImage();
		
		listener.playerStopped();
	}
	
	private void clearImage() {
		 mis = new MemoryImageSource(area.width, area.height, new int[frameSize],
		 0, area.width);
		 mis.setAnimated(true);
		 listener.showNewImage(Toolkit.getDefaultToolkit().createImage(mis));
	}

	public synchronized void run() {

		running = true;

		while (running) {

			while (paused && !resetReq) {

				try {
					Thread.sleep(50);
				} catch (Exception e) {
				}
				//???
				//startTime += 50;
			}

			try {
				readFrame();
				listener.newFrame(frameTime);
			} catch (IOException ioe) {
				listener.showNewImage(null);
				break;
			}

			if (fastForward == true) {
				startTime -= (frameTime - lastFrameTime);
			} else {
				while ((System.currentTimeMillis() - startTime < frameTime)
						&& !paused) {
					try {
						Thread.sleep(100);
					} catch (Exception e) {
					}
				}

				// System.out.println(
				// "FrameTime:"+frameTime+">"+(System.currentTimeMillis()-startTime));
			}

			lastFrameTime = frameTime;
		}

		// listener.playerStopped();
	}

	private void readFrame() throws IOException {

		if (resetReq) {
			reset();
			return;
		}

		FrameDecompressor.FramePacket frame = decompressor.unpack();
		frameSize = frame.getData().length;
		frameTime = frame.getTimeStamp();

		int result = frame.getResult();
		if (result == 0) {
			return;
		} else if (result == -1) {
			paused = true;
			listener.playerPaused();
			return;
		}

		if (mis == null) {
			mis = new MemoryImageSource(area.width, area.height,
					frame.getData(), 0, area.width);
			mis.setAnimated(true);
			listener.showNewImage(Toolkit.getDefaultToolkit().createImage(mis));
			return;
		} else {
			mis.newPixels(frame.getData(), ColorModel.getRGBdefault(), 0,
					area.width);
			return;
		}
	}
}
