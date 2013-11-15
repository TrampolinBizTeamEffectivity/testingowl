/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.player;

import java.awt.Image;

public interface ScreenPlayerListener {

   public void showNewImage(Image image);
   
   public void playerStopped();

   public void newFrame(long frameNumber, long frameTime);

}
