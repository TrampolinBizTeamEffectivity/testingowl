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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import ch.sebastianfiechter.testingowl.FrameIndexEntry;

public class FrameCompressor {
	
	public static int FULL_FRAME_INTERVAL = 300;
	
	Logger logger = Logger.getLogger(FrameCompressor.class);

	private FramePacket framePacket;
	
	private int frameNr;
	private int lastFullFrame;
	private long streamWriterPosition;
	
	private HashMap<Integer, FrameIndexEntry> frameIndex;

	public class FramePacket {

		private OutputStream oStream;
		private long frameTime;

		private int[] previousData;
		private int[] newData;

		private FramePacket(OutputStream oStream) {
			this.oStream = oStream;
			//previousData = new int[frameSize];
		}

		private void nextFrame(int[] frameData, long frameTime) {
			this.frameTime = frameTime;
			previousData = newData;
			//newData = null;
			if (previousData == null) {
				previousData = new int[frameData.length];
			}

			this.newData = frameData;// new int[frameData.length];
			
			frameNr++;
		}
	}

	public FrameCompressor(OutputStream oStream, HashMap<Integer, FrameIndexEntry> frameInde) {
		framePacket = new FramePacket(oStream);
		
		frameNr = -1;
		lastFullFrame = 0;
		streamWriterPosition = 0;
		frameIndex = frameInde;
	}

	public void pack(int[] newData, long frameTimeStamp) throws IOException {
		framePacket.nextFrame(newData, frameTimeStamp);

		if (frameNr % FULL_FRAME_INTERVAL == 0) {
			lastFullFrame = frameNr;	
		}
		
		byte[] packed = new byte[newData.length * 4];

		int inCursor = 0;
		int outCursor = 0;

		boolean inBlock = true;
		int blockSize = 0;
		byte blockRed = 0;
		byte blockGreen = 0;
		byte blockBlue = 0;

		int blankBlocks = 0;

		// Sentinel value
		int uncompressedCursor = -1;

		byte red;
		byte green;
		byte blue;

		boolean hasChanges = false;
		boolean lastEntry = false;

		while (inCursor < newData.length) {
			if (inCursor == newData.length - 1) {
				lastEntry = true;
			}

			//if frameNr % FULL_FRAME_INTERVAL == 0 then produce a full frame
			if (newData[inCursor] == framePacket.previousData[inCursor] && frameNr % FULL_FRAME_INTERVAL != 0) {
				red = 0;
				green = 0;
				blue = 0;
			} else {
				red = (byte) ((newData[inCursor] & 0x00FF0000) >>> 16);
				green = (byte) ((newData[inCursor] & 0x0000FF00) >>> 8);
				blue = (byte) ((newData[inCursor] & 0x000000FF));

				if (red == 0 && green == 0 && blue == 0) {
					blue = 1;
				}
			}

			if (blockRed == red && blockGreen == green && blockBlue == blue) {
				if (inBlock == false) {
					if (uncompressedCursor > -1) {
						hasChanges = true;
						packed[uncompressedCursor] = (byte) (blockSize + 0x80);
					}
					inBlock = true;
					blockSize = 0;
					blankBlocks = 0;
				} else if (blockSize == 126 || lastEntry == true) {
					if (blockRed == 0 && blockGreen == 0 && blockBlue == 0) {
						if (blankBlocks > 0) {
							blankBlocks++;
							packed[outCursor - 1] = (byte) blankBlocks;
						} else {
							blankBlocks++;
							packed[outCursor] = (byte) 0xFF;
							outCursor++;
							packed[outCursor] = (byte) blankBlocks;
							outCursor++;
						}
						if (blankBlocks == 255) {
							blankBlocks = 0;
						}
					} else {
						hasChanges = true;
						packed[outCursor] = (byte) blockSize;
						outCursor++;
						packed[outCursor] = blockRed;
						outCursor++;
						packed[outCursor] = blockGreen;
						outCursor++;
						packed[outCursor] = blockBlue;
						outCursor++;

						blankBlocks = 0;
					}
					inBlock = true;
					blockSize = 0;
				}
			} else {
				if (inBlock == true) {
					if (blockSize > 0) {
						hasChanges = true;
						packed[outCursor] = (byte) blockSize;
						outCursor++;
						packed[outCursor] = blockRed;
						outCursor++;
						packed[outCursor] = blockGreen;
						outCursor++;
						packed[outCursor] = blockBlue;
						outCursor++;
					}

					uncompressedCursor = -1;
					inBlock = false;
					blockSize = 0;

					blankBlocks = 0;
				} else if (blockSize == 126 || lastEntry == true) {
					if (uncompressedCursor > -1) {
						hasChanges = true;
						packed[uncompressedCursor] = (byte) (blockSize + 0x80);
					}

					uncompressedCursor = -1;
					inBlock = false;
					blockSize = 0;

					blankBlocks = 0;
				}

				if (uncompressedCursor == -1) {
					uncompressedCursor = outCursor;
					outCursor++;
				}

				packed[outCursor] = red;
				outCursor++;
				packed[outCursor] = green;
				outCursor++;
				packed[outCursor] = blue;
				outCursor++;

				blockRed = red;
				blockGreen = green;
				blockBlue = blue;
			}
			inCursor++;
			blockSize++;
		}

		frameIndex.put(frameNr, new FrameIndexEntry(framePacket.frameTime, 
			streamWriterPosition, lastFullFrame));

//		logger.info(frameNr+"\t"+(framePacket.frameTime/1000.0)+"\t"+streamWriterPosition
//			+"\t"+(packed.length/1000.0)
//			+"\t"+hasChanges+"\t"+lastFullFrame);
		

		framePacket.oStream.write(((int) framePacket.frameTime & 0xFF000000) >>> 24);
		framePacket.oStream.write(((int) framePacket.frameTime & 0x00FF0000) >>> 16);
		framePacket.oStream.write(((int) framePacket.frameTime & 0x0000FF00) >>> 8);
		framePacket.oStream.write(((int) framePacket.frameTime & 0x000000FF));
		streamWriterPosition += 4;
		
		//only create empty image if not a fullFrame to save
		if (hasChanges == false && frameNr % FULL_FRAME_INTERVAL != 0) {
			framePacket.oStream.write(0);
			streamWriterPosition += 1;
			framePacket.oStream.flush();
			framePacket.newData = framePacket.previousData;
		
			//return after frameTime, but before data, because nothing changed
			return;
		} else {
			framePacket.oStream.write(1);
			streamWriterPosition += 1;
			framePacket.oStream.flush();
		}
		
		ByteArrayOutputStream bO = new ByteArrayOutputStream();

		byte[] bA = new byte[0];

		GZIPOutputStream zO = new GZIPOutputStream(bO);

		zO.write(packed, 0, outCursor);
		zO.close();
		bO.close();

		bA = bO.toByteArray();
		
		framePacket.oStream.write(((bA.length & 0xFF000000) >>> 24));
		framePacket.oStream.write(((bA.length & 0x00FF0000) >>> 16));
		framePacket.oStream.write(((bA.length & 0x0000FF00) >>> 8));
		framePacket.oStream.write((bA.length & 0x000000FF));
		streamWriterPosition += 4;

		framePacket.oStream.write(bA);
		streamWriterPosition += bA.length;
		framePacket.oStream.flush();

	}
	

}
