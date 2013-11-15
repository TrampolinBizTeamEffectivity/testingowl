/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.converter;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class RecordingStream {

   private Rectangle area;
   private Rectangle outputArea;
   private FrameDecompressor decompressor;
   private long frameTime;
   private boolean finished = false;

   public RecordingStream(InputStream iStream, int width, int height) {
      this(iStream);
      outputArea = new Rectangle(width, height);
   }

   public RecordingStream(InputStream iStream) {

      try {
         int width = iStream.read();
         width = width << 8;
         width += iStream.read();
         int height = iStream.read();
         height = height << 8;
         height += iStream.read();
         area = new Rectangle(width, height);
         outputArea = area;
         decompressor = new FrameDecompressor(iStream, width * height);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public BufferedImage readFrame() throws IOException {

      FrameDecompressor.FramePacket frame = decompressor.unpack();
      frameTime = frame.getTimeStamp();
      int result = frame.getResult();
      if (result == 0) {
         return null;
      } else if (result == -1) {
         finished = true;
         return null;
      }

      BufferedImage bufferedImage = new BufferedImage(area.width, area.height,
            BufferedImage.TYPE_INT_RGB);
      bufferedImage.setRGB(0, 0, area.width, area.height, frame.getData(), 0,
            area.width);

      return bufferedImage;
   }

   public Rectangle getArea() {
      return outputArea;
   }

   public long getFrameTime() {
      return frameTime;
   }

   public boolean isFinished() {
      return finished;
   }
}
