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

	File soundFile
	Clip clip

	MonitoringAudioInputStream sound

	def readFromWav(String filenameWithoutFileEnding) {
				
		soundFile = new File("${filenameWithoutFileEnding}.cap.wav");
		
		sound = new MonitoringAudioInputStream(
			AudioSystem.getAudioInputStream(soundFile), audioIO, soundLevel);
		clip = AudioSystem.getClip();
		clip.open(sound)
	}

	def close() {
		if (clip != null) {
			stopPlaying();
			clip.close();
		}
	}
	
	def playFromTime(double seconds) {
		log.info "play from time: ${seconds}"
		clip.setFramePosition((sound.getFormat().getFrameRate()*seconds) as int)
		clip.start()
	}


	def stopPlaying() {
		clip.stop();
	}
	

}
