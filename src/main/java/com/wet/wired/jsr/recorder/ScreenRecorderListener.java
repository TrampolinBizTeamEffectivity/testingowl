/*
 * Original code: Copyright 2000-2001 by Wet-Wired.com Ltd., Portsmouth England
 * This class is distributed under the MIT License (MIT)
 * Download original code from: http://code.google.com/p/java-screen-recorder/
 * 
 * The current version of this class is heavily refactored by Sebastian Fiechter.
 * 
 */

package com.wet.wired.jsr.recorder;

import java.io.IOException;

public interface ScreenRecorderListener {

   public void frameRecorded(boolean fullFrame, long frameTime) throws IOException;

   public void recordingStopped();
}
