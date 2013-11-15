/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
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
import java.util.Collections;
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
	private HashMap<Integer, FrameIndexEntry> frameIndex;
	private int offsetToFirstFrame;
	
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
	}
	
	private void openStream() {
		startTime = 0;
		frameTime = 0;
		lastFrameTime = 0;

		frameNr = 0;

		running = false;
		fastForward = false;

		try {

			iStream = new RandomAccessFile(videoFile+".cap", "r");
			
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
			int frameIndexLength = iStream.readInt();
			logger.info("frame index size is: " + frameIndexLength);
			byte[] frameIndexBytes = new byte[frameIndexLength];
			iStream.read(frameIndexBytes);
			ByteArrayInputStream bais = new ByteArrayInputStream(frameIndexBytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			frameIndex = (HashMap<Integer, FrameIndexEntry>) ois.readObject();
			offsetToFirstFrame = 4+frameIndexLength+4;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
				e.printStackTrace();
			}
		}
		
	}
	
	public void reset() {
		showFirstFrame();
	}	
	
	private void showFirstFrame() {
		try {
			//go to first frame
			goToFrame(1);
			readFrame();
			listener.newFrame(frameNr, frameTime);
			lastFrameTime = frameTime;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
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
		
		try {
		
			FrameIndexEntry entry = frameIndex.get(toFrame);
			FrameIndexEntry fullEntry = frameIndex.get(entry.getLastFullFrame());
			iStream.seek(offsetToFirstFrame+fullEntry.getStreamPosition());
	
			FramePacket frame = null;
			
			frameTime = 0;
			frameNr = entry.getLastFullFrame()-1;
	
			for (int i = entry.getLastFullFrame(); i < toFrame; i++) {
					frame = decompressor.unpack();
					frameSize = frame.getData().length;
					frameTime = frame.getTimeStamp();
					frameNr++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	private void countTotalFramesAndTime() {
		totalFrames = Collections.max(frameIndex.keySet());
		totalTime = frameIndex.get(totalFrames).getFrameTime();
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
