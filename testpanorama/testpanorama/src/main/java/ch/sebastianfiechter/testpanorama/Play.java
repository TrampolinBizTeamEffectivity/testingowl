package ch.sebastianfiechter.testpanorama;

import java.io.*;
import javax.sound.sampled.*;

public class Play {
     public static void main(String[] args) {
         class MyLineListener implements LineListener {
             public void update(LineEvent le) {
                 LineEvent.Type type = le.getType();
                 System.out.println(type);
             }
         };

         try {
             AudioInputStream fis =
              AudioSystem.getAudioInputStream(new File(args[0]));
             System.out.println("File AudioFormat: " + fis.getFormat());
             AudioInputStream ais = AudioSystem.getAudioInputStream(
              AudioFormat.Encoding.PCM_SIGNED,fis);
             AudioFormat af = ais.getFormat();
             System.out.println("AudioFormat: " + af.toString());

             int frameRate = (int)af.getFrameRate();
             System.out.println("Frame Rate: " + frameRate);
             int frameSize = af.getFrameSize();
             System.out.println("Frame Size: " + frameSize);

             SourceDataLine line = AudioSystem.getSourceDataLine(af);
             line.addLineListener(new MyLineListener());

             line.open(af);
             int bufSize = line.getBufferSize();
             System.out.println("Buffer Size: " + bufSize);

             line.start();

             byte[] data = new byte[bufSize];
             int bytesRead;

             while ((bytesRead = ais.read(data,0,data.length)) != -1)
                 line.write(data,0,bytesRead);

             line.drain();
             line.stop();
             line.close();
         } catch (Exception e) {
             System.out.println(e);
         }
     }
}

