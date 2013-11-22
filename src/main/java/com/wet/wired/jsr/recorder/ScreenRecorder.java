/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.recorder;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ch.sebastianfiechter.testingowl.ExceptionWindow;
import ch.sebastianfiechter.testingowl.FrameIndexEntry;

public abstract class ScreenRecorder implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(ScreenRecorder.class);

	@Autowired
	ExceptionWindow exceptionWindow;
	
	private Rectangle recordArea;

	private int frameSize;
	private int[] rawData;

	private File temp;
	private OutputStream oStream;

	private HashMap<Integer, FrameIndexEntry> frameIndex;
	
	private boolean recording = false;
	private boolean running = false;

	private long startTime;
	private long frameTime;

	private ScreenRecorderListener listener;
	

	private class DataPack {
		public DataPack(int[] newData, long frameTime) {
			this.newData = newData;
			this.frameTime = frameTime;
		}

		public long frameTime;
		public int[] newData;
	}

	private class StreamPacker implements Runnable {
		Queue<DataPack> queue = new LinkedList<DataPack>();
		private FrameCompressor compressor;

		public StreamPacker(OutputStream oStream, 
				HashMap<Integer, FrameIndexEntry> frameIndex) {
			compressor = new FrameCompressor(oStream, frameIndex);

			new Thread(this, "Stream Packer").start();
		}

		public void packToStream(DataPack pack) {
			while (queue.size() > 2) {
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					//do nothing
				}
			}
			queue.add(pack);
		}

		public void run() {
			while (recording) {
				while (queue.isEmpty() == false) {
					DataPack pack = queue.poll();

					try {
						// long t1 = System.currentTimeMillis();
						compressor.pack(pack.newData, pack.frameTime);
						// long t2 = System.currentTimeMillis();
						// System.out.println("  pack time:"+(t2-t1));

					} catch (IOException e) {
						try {
							oStream.close();
						} catch (IOException oe) {
							logger.error("cannot close oStream", oe);
						}
						logger.error("cannot pack new data", e);
						exceptionWindow.show(e,"cannot pack new data");
					
						return;
					} 
				}
				while (queue.isEmpty() == true) {
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						//do nothing
					}
				}
			}
		}
	}

	private StreamPacker streamPacker;

	public void init(File tempFile, ScreenRecorderListener listener) {

		this.listener = listener;

	    temp = tempFile;
		
		try {
			this.oStream = new FileOutputStream(tempFile);
		} catch (FileNotFoundException e) {
			logger.error("temp file not found", e);
			exceptionWindow.show(e, "temp file not found");
		}
	}

	public void triggerRecordingStop() {
		recording = false;
	}

	public synchronized void run() {
		startTime = System.currentTimeMillis();

		recording = true;
		running = true;
		long lastFrameTime = 0;
		long time = 0;

		frameSize = recordArea.width * recordArea.height;
		frameIndex = new HashMap<Integer, FrameIndexEntry>();
		streamPacker = new StreamPacker(oStream, frameIndex);

		while (recording) {
			time = System.currentTimeMillis();
			while (time - lastFrameTime < 190) {
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					//do nothing
				}
				time = System.currentTimeMillis();
			}
			lastFrameTime = time;

			try {
				recordFrame();
			} catch (Exception e) {
				try {
					oStream.close();
				} catch (Exception oe) {
					logger.error("cannot close stream", oe);
				}
				logger.error("cannot record frame", e);
				exceptionWindow.show(e,"cannot record frame");
				break;
			}
		}

		running = false;
		recording = false;

		listener.recordingStopped();
	}

	public abstract Rectangle initialiseScreenCapture();

	public abstract BufferedImage captureScreen(Rectangle recordArea);

	public void recordFrame() throws IOException {
		// long t1 = System.currentTimeMillis();
		BufferedImage bImage = captureScreen(recordArea);
		frameTime = System.currentTimeMillis() - startTime;
		// long t2 = System.currentTimeMillis();

		rawData = new int[frameSize];

		bImage.getRGB(0, 0, recordArea.width, recordArea.height, rawData, 0,
				recordArea.width);
		// long t3 = System.currentTimeMillis();

		streamPacker.packToStream(new DataPack(rawData, frameTime));

		// System.out.println("Times");
		// System.out.println("  capture time:"+(t2-t1));
		// System.out.println("  data grab time:"+(t3-t2));

		listener.frameRecorded(false, frameTime);
	}

	public void startRecording() {
		recordArea = initialiseScreenCapture();

		if (recordArea == null) {
			return;
		}
		try {
			oStream.write((recordArea.width & 0x0000FF00) >>> 8);
			oStream.write((recordArea.width & 0x000000FF));

			oStream.write((recordArea.height & 0x0000FF00) >>> 8);
			oStream.write((recordArea.height & 0x000000FF));
		} catch (IOException e) {
			logger.error("cannot write stream record area", e);
			exceptionWindow.show(e,"cannot write stream record area");
		}

		new Thread(this, "Screen Recorder").start();
	}

	public void stopRecording() {
				
		triggerRecordingStop();

		int count = 0;
		while (running == true && count < 10) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				//do nothing
			}
			count++;
		}

		if (oStream != null) {
			try {
				oStream.flush();
				oStream.close();
			} catch (IOException e) {
				logger.error("cannot flush and close stream", e);
				exceptionWindow.show(e,"cannot flush and close stream");
			}
		}
	}
	
	public void writeFrameIndex(FileChannel targetChannel) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(getFrameIndex());
			oos.close();
			baos.close();

			ByteBuffer frameIndexLength = ByteBuffer.allocate(4);
			logger.info("frame index size is: " + baos.toByteArray().length);
			frameIndexLength.putInt(baos.toByteArray().length);
			frameIndexLength.flip();
			targetChannel.write(frameIndexLength);

			ByteBuffer frameIndex = ByteBuffer.wrap(baos.toByteArray());
			targetChannel.write(frameIndex);

		} catch (IOException e) {
			logger.error("cannot write frame index", e);
			exceptionWindow.show(e, "cannot write frame index");
		}

	}

	public void writeVideo(FileChannel targetChannel) {

		try {
			RandomAccessFile tempVideo = new RandomAccessFile(temp, "r");

			logger.info("will write video length of: " + tempVideo.length()
					+ " at position: " + targetChannel.position());

			targetChannel.transferFrom(tempVideo.getChannel(),
					targetChannel.position(), tempVideo.length());

			tempVideo.getChannel().close();
			tempVideo.close();
		} catch (FileNotFoundException e) {
			logger.error("temp file not found", e);
			exceptionWindow.show(e, "temp file not found");
		} catch (IOException e) {
			logger.error("cannot transfer from channels", e);
			exceptionWindow.show(e, "cannot transfer from channels");
		}

	}	
	
	public boolean isRecording() {
		return recording;
	}

	public int getFrameSize() {
		return frameSize;
	}
	
	public HashMap<Integer, FrameIndexEntry> getFrameIndex() {
		return frameIndex;
	}
}
