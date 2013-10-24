package ch.sebastianfiechter.testpanorama

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.wet.wired.jsr.recorder.FileHelper;

import groovy.util.logging.*

@Component
@Slf4j
class AudioPlayer {

	@Autowired
	AudioIO audioIO

	def playing = false
	File soundFile
	SourceDataLine line
	
	Thread playingThread
	AudioInputStream audioInputStream
	AudioFormat audioFormat

	
	def readFromWav(String filenameWithoutFileEnding) {
		soundFile = new File("${filenameWithoutFileEnding}.cap.wav");
	}

	def playFromTime(double seconds) {

		log.info "play from time: " + seconds
		
		assert soundFile != null

		stopPlaying()
	
		playingThread = new Thread() {
			public void run() {
				
				openLine()
				
				line.start();
				
				goToStartPosition(seconds)
				
				int bytesPerSecond = audioFormat.getSampleRate() * audioFormat.getSampleSizeInBits() / 8
				def abData = new byte[bytesPerSecond];
				
				int nBytesRead = 0;
				while (playing == true && nBytesRead != -1) {
					try {
						nBytesRead = audioInputStream.read(abData, 0, abData.length);
						//AudioPlayer.this.log.info ("level: "+audioIO.calculateRMSLevel(abData[0..200] as byte[]))
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (nBytesRead >= 0) {
						int nBytesWritten = line.write(abData, 0, nBytesRead);
					}
				}
				
				line.drain();
				line.close();
				audioInputStream.close();
			}
			
			def openLine() {
				try {
					audioInputStream = AudioSystem.getAudioInputStream(soundFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
				audioFormat = audioInputStream.getFormat();
		
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,
						audioFormat);
		
				line = null;
				try {
					line = (SourceDataLine) AudioSystem.getLine(info);
					line.open(audioFormat);
				} catch (LineUnavailableException e) {
					e.printStackTrace()
				} catch (Exception e) {
					e.printStackTrace()
				}
			}
		
			def goToStartPosition(double secondsAfterStart) {
				int nBytesRead = 0;
				double bytesPerSecond = audioFormat.getSampleRate() * audioFormat.getSampleSizeInBits() / 8;
				
				int bytesToEat = (int) (secondsAfterStart*bytesPerSecond-1.0)
				AudioPlayer.this.log.info ("bytesToEat ${bytesToEat}")
				if (bytesToEat <= 0) {
					return
				}
				byte[] abData = new byte[bytesToEat]
				try {
					nBytesRead = audioInputStream.read(abData, 0, abData.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
			
		
		}
		
		playing = true

		playingThread.start()
	}
	

	def stopPlaying() {
		playing = false
		if (playingThread != null && playingThread.isAlive()) {
			playingThread.join();
		}
	}
}
