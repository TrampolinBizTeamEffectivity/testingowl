package ch.sebastianfiechter.testingowl

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip
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

	@Autowired
	SoundLevel soundLevel
	
	@Autowired
	ExceptionWindow exceptionWindow

	File soundFile
	MonitoringAudioInputStream sound
	SourceDataLine soundLine

	Thread playingThread
	def play

	def readFromWav(String filenameWithoutFileEnding) {

		soundFile = new File("${filenameWithoutFileEnding}.cap.wav");
		
		sound = new MonitoringAudioInputStream(
				AudioSystem.getAudioInputStream(soundFile), audioIO, soundLevel);
		
		AudioFormat audioFormat = sound.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		soundLine = (SourceDataLine) AudioSystem.getLine(info);
		try {
			soundLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			log.error("cannot open audio line to play", e);
			exceptionWindow.show(e, "cannot open audio line to play")	
		}
	}
	
	def playFromTime(double seconds) {
		stopPlaying();

		reset();
		
		long toSkip = seconds * sound.format.sampleRate * sound.format.frameSize
		def skipped = sound.skip(toSkip)
		log.info "play from time: ${seconds}, skip ${toSkip}, skipped ${skipped}"
		
		soundLine.start();
		int nBytesRead = 0;
		byte[] sampledData = new byte[4096];
		play = true
		playingThread = Thread.start {
			while (play == true && nBytesRead != -1) {
				nBytesRead = sound.read(sampledData, 0, sampledData.length);
				if (nBytesRead >= 0) {
					// Writes audio data to the mixer via this source data line.
					soundLine.write(sampledData, 0, nBytesRead);
				}
			}
			soundLine.drain();
		}
	}

	def reset() {
		if (sound != null) {
			sound.close()
		}
		sound = new MonitoringAudioInputStream(
			AudioSystem.getAudioInputStream(soundFile), audioIO, soundLevel);
	}
	
	def close() {
		stopPlaying()
		if (sound != null) {
			sound.close();
		}
		if (soundLine != null) {
			soundLine.close();
		}
	}
	
	def synchronized stopPlaying() {
		if (playingThread != null) {
			play = false
			playingThread.join();
		}
	}
}
