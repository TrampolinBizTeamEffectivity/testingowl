package ch.sebastianfiechter.testingowl

import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException
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
	
	@Autowired
	ExceptionWindow exceptionWindow;

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

		tempFile = File.createTempFile("temp", "wav")
		tempFile.deleteOnExit();

		prepareRecording()


		recordingThread = new Thread() {
					void run() {
						try {
							AudioSystem.write(monitoringAudioInputStream, targetType, tempFile);
						} catch (IOException e) {
							AudioRecorder.this.log.error("cannot write audiostream", e)
							exceptionWindow.show(e, "Cannot write AudioStream.")
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

		try {
			targetDataLine = (TargetDataLine) mixer.getLine(lineInfo);
			targetDataLine.open(audioIO.audioFormat);
		} catch (LineUnavailableException e) {
			log.error("cannot open targetDataLine", e)
			exceptionWindow.show(e, "cannot open targetDataLine")
		}

		targetType = AudioFileFormat.Type.WAVE;

		monitoringAudioInputStream = new MonitoringAudioInputStream(targetDataLine,
				audioIO, soundLevel);
	}

	def stopRecording() {

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
		new File("${filenameWithoutEnding}.owl.wav").delete()

		def out = new File("${filenameWithoutEnding}.owl.wav")

		if (tempFile != null && tempFile.exists()) {
			try {
				FileHelper.copy(tempFile, out);
				tempFile.delete()
			} catch (IOException e) {
				log.error("cannot write audiofile", e)
				exceptionWindow.show(e, "Cannot write .wav file.")
			}
		}

	}

	def cancelSave() {
		if (tempFile != null && tempFile.exists()) {
			tempFile.delete()
		}
	}

}
