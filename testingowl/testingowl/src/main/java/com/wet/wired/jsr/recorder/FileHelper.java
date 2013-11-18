/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * The FileHelper, as its name implies gives a number of helper methods that
 * ease the reading and creating of files. It also supplies a file copy
 * function, a read trail function and a get path function.
 * 
 * All the functions are static, so no instance of FileHelper need be created to
 * use the functions.
 * 
 */

public class FileHelper {


   public static long copy(File fileSrc, File fileDest) throws IOException {
      return copy(fileSrc, fileDest, null);
   }

   public static long copy(File fileSrc, File fileDest,
         ProgressListener listener) throws IOException {
      byte[] buffer = new byte[5000];
      long count = 0;
      int sizeRead;

         FileInputStream iStream = new FileInputStream(fileSrc);

         new File(getPath(fileDest.toString())).mkdirs();

         FileOutputStream oStream = new FileOutputStream(fileDest);

         sizeRead = iStream.read(buffer);
         while (sizeRead > 0) {
            oStream.write(buffer, 0, sizeRead);
            oStream.flush();
            count += sizeRead;
            if (listener != null) {
               listener.progress(count, -1);
            }

            sizeRead = iStream.read(buffer);
         }

         iStream.close();
         oStream.flush();
         oStream.close();

         if (listener != null) {
            listener.finished();
         }

      return count;
   }

   public static String getPath(String fileName) {
      File file = new File(fileName);
      return fileName.substring(0, fileName.indexOf(file.getName()));
   }

}
