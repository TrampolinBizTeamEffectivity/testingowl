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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.sebastianfiechter.testingowl.FrameIndexEntry;

import com.wet.wired.jsr.player.FrameDecompressor.FramePacket;
import com.wet.wired.jsr.recorder.JRecorder;

@Component
public class ScreenPlayer implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(ScreenPlayer.class);

	private Thread thread;

	private ScreenPlayerListener listener;

	private MemoryImageSource mis = null;
	private Rectangle area;

	private FrameDecompressor decompressor;
	private int frameSize;

	private long startTime;
	private long frameTime;
	private long lastFrameTime;

	private int frameNr;

	private int totalFrames;
	private long totalTime;

	private boolean running;
	private boolean fastForward;

	private RandomAccessFile iStream;
	private int frameIndexLength;
	private HashMap<Integer, FrameIndexEntry> frameIndex;
	
	private String videoFile;
	private int width;
	private int height;
	
	public void init(String video, ScreenPlayerListener list) {
		listener = list;
		videoFile = video;
	
		startTime = 0;
		frameTime = 0;
		lastFrameTime = 0;

		frameNr = 0;
		totalFrames = 0;
		totalTime = 0;

		running = false;
		fastForward = false;
	}

	
	public void open() {
		openStream();
		
		countTotalFramesAndTime();

		reset();		
	}
	
	private void openStream() {
		startTime = 0;
		frameTime = 0;
		lastFrameTime = 0;

		frameNr = 0;

		running = false;
		fastForward = false;

		try {

			iStream = new RandomAccessFile(videoFile, "r");
			
			readFrameIndex();

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
	
	private void readFrameIndex() {
		try {
			frameIndexLength = iStream.readInt();
			logger.info("frame index size is: " + frameIndexLength);
			byte[] frameIndexBytes = new byte[frameIndexLength];
			iStream.read(frameIndexBytes);
			ByteArrayInputStream bais = new ByteArrayInputStream(frameIndexBytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			frameIndex = (HashMap<Integer, FrameIndexEntry>) ois.readObject();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void closeStream() {
		
		//ensure, thread is closed
		stopThread();
		
		if (iStream != null) {
			try {
				iStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void reset() {
		closeStream();
		openStream();
		showFirstFrame();
	}	
	
	public void play() {
		fastForward = false;
		startThread();
	}

	public void fastforward() {
		fastForward = true;
		startThread();
	}


	public void pause() {
		stopThread();
	}
	
	public void close() {
		closeStream();
		
		clearImage();
		
		startTime = 0;
		frameTime = 0;
		lastFrameTime = 0;

		frameNr = 0;
		totalFrames = 0;
		totalTime = 0;

		running = false;
		fastForward = false;
	}

	public void goToFrame(int toFrame) {
		
		stopThread();

		FramePacket frame = null;
		
		frameTime = 0;

		for (int i = frameNr+1; i < toFrame; i++) {
			try {
				frame = decompressor.unpack();
				frameSize = frame.getData().length;
				frameTime = frame.getTimeStamp();
				frameNr++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	

	private void countTotalFramesAndTime() {
		// we have to iterate, because, the file is Zipped
		totalFrames = 0;
		totalTime = 0;

		FrameDecompressor.FramePacket frame;
		try {
			frame = decompressor.unpack();

			while (frame.getResult() != -1) {
				totalTime = frame.getTimeStamp();
				totalFrames++;

				frame = decompressor.unpack();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showFirstFrame() {
		try {
			readFrame();
			listener.newFrame(frameNr, frameTime);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		lastFrameTime = frameTime;
	}
	
	private void clearImage() {
		if (area != null) {
			mis = new MemoryImageSource(area.width, area.height,
					new int[frameSize], 0, area.width);
			mis.setAnimated(true);
			listener.showNewImage(Toolkit.getDefaultToolkit().createImage(mis));
		}
	}	
	
	private void startThread() {
		if (running == false) {
			thread = new Thread(this, "Screen Player");
			thread.start();
		}
	}

	private void stopThread() {
		running = false;
		if (thread != null && thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void run() {

		running = true;
		startTime = System.currentTimeMillis() - frameTime;

		while (running) {

			try {
				readFrame();
				listener.newFrame(frameNr, frameTime);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				listener.showNewImage(null);
				break;
			}

			if (fastForward == true) {
				startTime -= (frameTime - lastFrameTime);
			} else {
				while (System.currentTimeMillis() - startTime < frameTime) {

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
		
		if (frameNr == totalFrames) {
			listener.playerStopped();
		}
	}

	private void readFrame() throws IOException {

		FrameDecompressor.FramePacket frame = decompressor.unpack();

		int result = frame.getResult();
		if (result == 0) {
			//empty image, because no change
			frameNr++;
			frameSize = frame.getData().length;
			frameTime = frame.getTimeStamp();
			return;
		} else if (result == -1) {
			//end of file, stop
			running = false;
			return;
		}

		frameNr++;
		frameSize = frame.getData().length;
		frameTime = frame.getTimeStamp();

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
	
	public int getTotalFrames() {
		return totalFrames;
	}

	public long getTotalTime() {
		return totalTime;
	}
	
	public int getFrameNr() {
		return frameNr;
	}

	public long getFrameTime() {
		return frameTime;
	}
	
}
