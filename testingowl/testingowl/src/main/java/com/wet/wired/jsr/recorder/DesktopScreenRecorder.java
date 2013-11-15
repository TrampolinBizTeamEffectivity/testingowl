/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.recorder;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

@Component
public class DesktopScreenRecorder extends ScreenRecorder {

   public static boolean useWhiteCursor;
   private Robot robot;
   private BufferedImage mouseCursor;
   
   public void init(File tempFile, 
         ScreenRecorderListener listener) {
      super.init(tempFile, listener);
      
      try {

         String mouseCursorFile;

         if (useWhiteCursor)
            mouseCursorFile = "white_cursor.png";
         else
            mouseCursorFile = "black_cursor.png";

         URL cursorURL = getClass().getClassLoader().getResource(
               "mouse_cursors/" + mouseCursorFile);

         mouseCursor = ImageIO.read(cursorURL);

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public Rectangle initialiseScreenCapture() {
      try {
         robot = new Robot();
      } catch (AWTException awe) {
         awe.printStackTrace();
         return null;
      }
      return new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
   }

   public Robot getRobot() {
      return robot;
   }

   public BufferedImage captureScreen(Rectangle recordArea) {
      Point mousePosition = MouseInfo.getPointerInfo().getLocation();
      BufferedImage image = robot.createScreenCapture(recordArea);

      Graphics2D grfx = image.createGraphics();

      grfx.drawImage(mouseCursor, mousePosition.x - 8, mousePosition.y - 5,
            null);

      grfx.dispose();

      return image;
   }


}
