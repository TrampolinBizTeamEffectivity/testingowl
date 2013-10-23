package ch.sebastianfiechter.testpanorama

import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.wet.wired.jsr.recorder.FileHelper;

import groovy.util.logging.*

@Component
@Slf4j
class AudioRecorder {

	@Autowired
	AudioIO audioIO
	
	@Autowired
	SoundLevel soundLevel;

	def mixerName = null
	Thread recordingThread;
	File tempFile;

	TargetDataLine targetDataLine
	AudioFileFormat.Type targetType
	MonitoringAudioInputStream monitoringAudioInputStream
	

	def startRecording() {

		if (mixerName == null) {
			log.info "No mixer defined - won't record audio."
			return
		}

		tempFile = new File("tempwav");
		tempFile.deleteOnExit();

		prepareRecording()


		recordingThread = new Thread() {
					void run() {
						try {
							AudioSystem.write(monitoringAudioInputStream, targetType, tempFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

		targetDataLine.start()
		recordingThread.start()
	}

	def prepareRecording() {
		Mixer mixer = audioIO.getRecordingMixer(mixerName);

		DataLine.Info lineInfo = new DataLine.Info(
				TargetDataLine.class, audioIO.audioFormat);

		targetDataLine = (TargetDataLine) mixer.getLine(lineInfo);
		targetDataLine.open(audioIO.audioFormat);

		targetType = AudioFileFormat.Type.WAVE;

		monitoringAudioInputStream = new MonitoringAudioInputStream(targetDataLine, 
			audioIO, soundLevel);
	}

	def stopRecording() {
		
		monitoringAudioInputStream?.stop()
		
		if (targetDataLine != null && targetDataLine.active) {
			targetDataLine.stop();
			targetDataLine.close();
		}

		if (recordingThread != null && recordingThread.isAlive()) {
			recordingThread.join()
		}
	}

	def writeToWavFile(String filenameWithoutEnding) {

		//delete existing
		new File("${filenameWithoutEnding}.cap.wav").delete()

		def out = new File("${filenameWithoutEnding}.cap.wav")

		if (tempFile != null && tempFile.exists()) {
			FileHelper.copy(tempFile, out);
			tempFile.delete()
		}

	}

	def cancelSave() {
		if (tempFile != null && tempFile.exists()) {
			tempFile.delete()
		}
	}


}
